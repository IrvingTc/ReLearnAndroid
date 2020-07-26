#### 一、**可控的作用域**是啥？实际上是**自动地注册或解绑对事件源的监听**。无需开发者手动编写，从而避免的因疏忽而造成的不必要的错误。那具体是怎么做到自动的呢？

##### 1. 在Activity中，通过以下代码可以给`ViewModel`中的`LiveData`添加一个观察者。

```kotlin
  vm.testData.observe(this, Observer { 
      it?.let {     
      	//TODO    
      }
  })
```
##### 2.再看`LiveData`中是如何添加这个观察者的：

```
	@MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    			//1. 确保该方法只能在主线程中调用
        assertMainThread("observe");
        
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            // ignore
            	//2. 如果当前LifeCycleOwner实例已被销毁，则直接返回
            return;
        }
        
        		//3. 将传入的LifeCycleOwner和Observer对象包装成LifecycleBoundObserver对象
        LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
        
        		//4. 将我们传入的Observer和包装后的LifecycleBoundObserver对象
        		//   添加到LiveData管理所有观察者的map中
        ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
        
        if (existing != null && !existing.isAttachedTo(owner)) {
        		//5. 如果该Observer对象已被添加到管理所有Oberver的map中，
        		//   并且它们的LifeCycleOwner也相同，则抛出异常
            throw new IllegalArgumentException("Cannot add the same observer"
                    + " with different lifecycles");
        }
        
        if (existing != null) {
        		//6. 由5判断可得知，该Observer已经在map中，并且两个的LifeCycleOwner是同一个，
        		//	 所以不需要再执行步骤7(上一次已经执行过)
            return;
        }
        
        		//7. 将包装好的LifecycleBoundObserver添加到LifeCycleOwner用于
        		//   管理所有观察它生命周期的Observer的map中。
        		//   这一步的作用就是用于解决 生命周期安全问题
        owner.getLifecycle().addObserver(wrapper);
    }

```

- 疑问来了，为什么要将我们传入的`Observer`再次包装成`LifecycleBoundObserver`并添加到`owner.getLifecycle().addObserver`呢，从这个包装类的名称我们可以知道，这个包装类也是一个观察者。猜想一下，将这个观察者对象传递给`LifeCycleOwner`用于 管理所有观察它生命周期的`Observer`的`map`中，是不是为了， 当`LifeCycleOwner`的生命周期发生改变的时候，通知我们传入的这个`LifecycleBoundObserver`对象(通知实际上就是回调)，最后在这个包装类中对我们在`Activity`中传入的`Observer`进行操作，会不会是这样的呢。

##### 3. 我们接下来看一下包装类 `LifecycleBoundObserver`,  这是`LiveData`的一个内部类。重点关注它的`onStateChanged`方法，由方法名可以知道该方法是要被回调的方法(可以查看调用它的地方进一步确认)。

```java
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
   
   	@NonNull
   	final LifecycleOwner mOwner;

	LifecycleBoundObserver(@NonNull LifecycleOwner owner, Observer<? super T> observer) {
		super(observer);
		mOwner = owner;
	}
	。。。省略部分代码

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source,@NonNull Lifecycle.Event event) {
            // 1. 获取当前LifeCycleOwner所处的状态
        Lifecycle.State currentState = mOwner.getLifecycle().getCurrentState();
        
        if (currentState == DESTROYED) {
        	// 2. 如果当前LifeCycleOwner已经销毁，则调用LiveData的removeObserver方法，
        	//    将我们从Activity传入的Observer对象从 管理所有Observer对象的Map中移除，
        	//    这也是为什么我们不需要手动移除Observer的原因。
            removeObserver(mObserver);
            return;
        }
        Lifecycle.State prevState = null;
        while (prevState != currentState) {
            prevState = currentState;
            // 3. 通过shouldBeActive()方法可得知当前owner是否处于Active状态，
            //    并将结果传递给activeStateChanged方法进行下一步操作
            activeStateChanged(shouldBeActive());
            currentState = mOwner.getLifecycle().getCurrentState();
        }
    }

    @Override
   	boolean shouldBeActive() {
   		// 由LifeCycle中 State和LifeCycleOwner的生命周期节点回调关系图 可知，
   		// 只有处于 onResume和onPause 状态时此方法才返回true。
   		// 又即，只有 onResume和onPause 这两个状态 owner才是Active的
    	return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
    }

	。。。省略部分代码

}
```

##### 4. 继续看`activeStateChanged(boolean)`方法

```java
void activeStateChanged(boolean newActive) {
    if (newActive == mActive) {
        // 1. 状态(这个状态指LifeCycleOwner是否处于Active)没有改变，直接返回
        return;
    }
    // immediately set active state, so we'd never dispatch anything to inactive
    // owner
    mActive = newActive;
    changeActiveCounter(mActive ? 1 : -1);
    if (mActive) {
        // 2. 如果为当前LifeCycleOwner为活跃状态，则执行LiveData中的dispatchingValue方法，
        //    该方法会处理我们从Activity传入的Observer观察者。后面会具体分析该方法
        dispatchingValue(this);
    }
}
```

看完了`LifecycleBoundObserver`，我们知道这个包装类的回调方法`onStateChanged`，可以根据当前`LifeCycleOwner`的状态，对我们传入的`Observer`进行操作。那么是哪里、什么时机会调用此方法呢。在`LiveData`的`observer`方法中我们可以知道`LifecycleBoundObserver`包装对象，通过`owner.getLifecycle().addObserver(wrapper)`方法被添加进了`LifecycleRegistry`(`LifeCycle`唯一的实现类)中维护所有`Observer`的`map`中，那么我们的包装对象的`onStateChanged`应该也是在这个类中被回调的。下面是整理后的完整的调用路径：

![image-20200726145103158](C:\Users\tucheng\AppData\Roaming\Typora\typora-user-images\image-20200726145103158.png)

![LifeCycleBoundObserver回调](C:\Users\tucheng\Desktop\Relearn Android\图片\LifeCycleBoundObserver回调.png)

#### 小结

至此，我们终于知道将`LifeCycleBoundObserver`这个观察者的作用了，总结一下就是：

在`LifeCycleOwner`的所处的生命周期节点**发生变化**的时候，通过调用`LifeCycleBoundObserver`的`onStateChanged`方法"告知"`LiveData`，该`LifeCycleOwner`的生命周期节点发生了改变，然后再根据此时具体的生命周期状态进行操作，如移除我们传入的`Observer`等等，所以这就是为什么`LiveData`的作用域是可控的了。

#### 二、我们在Activity中给LiveData添加的Observer，什么时候会被回调呢

##### 1. 用过`LiveData`的人都知道，在调用`LiveData.setValue(xxx)\postValue(xxx)`方法的时候会，哈哈哈。我们先看一下`LiveData.setValue(xxx)`方法。

```java
public abstract class LiveData<T> {
	private int mVersion;
    private volatile Object mData;
    
    @MainThread
    protected void setValue(T value) {
           // 1. 确保在主线程调用
        assertMainThread("setValue");
           // 2. 将Version加一
        mVersion++;
           // 3. 刷新数据
        mData = value;
           // 4. 处理数据
        dispatchingValue(null);
    }
}
```

##### 2. 继续看`LiveData.dispatchingValue(@Nullable ObserverWrapper initiator)`方法:

```java
void dispatchingValue(@Nullable ObserverWrapper initiator) {
    if (mDispatchingValue) {
        // 1. 正在处理数据，修改完mDispatchInvalidated变量后返回
        mDispatchInvalidated = true;
        return;
    }
    mDispatchingValue = true;
    do {
        // 2. do..While循环, 每当1多走一次，循环将多执行一次
        mDispatchInvalidated = false;
        if (initiator != null) {
            //3. 传入的ObserverWrapper不为空进入此分支。由第一部分可知，此时为某个LifeCycleOwner生命周期改变后，
            //   会调用LifeCycleBoundObserver的onStateChanged方法，在此方法中判断，如果当前LifeCycleOwner
            //   为Active状态，则会调用此方法(dispatchingValue方法)，然后进入到此分支中。
            //   具体执行Activity中传入的Observer的回调方法的地方。
            considerNotify(initiator);
            initiator = null;
        } else {
            // 4. 如果传入的ObserverWrapper为空，则是我们调用setValue中的情况，此时会遍历map中所有的Observer，
            //    逐一进行通知。
            for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
                 mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
                //具体执行Activity中传入的Observer的回调方法的地方。
                considerNotify(iterator.next().getValue());
                if (mDispatchInvalidated) {
                    break;
                }
            }
        }
    } while (mDispatchInvalidated);
    // 5. 事件通知完毕，修改状态
    mDispatchingValue = false;
}
```

##### 3. 最后看具体执行回调的方法`LiveData.considerNotify(ObserverWrapper observer)`:

```kotlin
private void considerNotify(ObserverWrapper observer) {
        if (!observer.mActive) {
            // 1. 如果此Observer对象对应的LifeCycleOwner实例不处于Active状态，
            //    则直接返回。
            return;
        }
        // Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
        //
        // we still first check observer.active to keep it as the entrance for events. So even if
        // the observer moved to an active state, if we've not received that event, we better not
        // notify for a more predictable notification order.
        if (!observer.shouldBeActive()) {
            // 2. 再次判断此Observer对象对应的LifeCycleOwner实例是否处于Active状态，
            //    因为我们的LifeCycleBoundObserver.onStateChanged方法此时可能还没被回调。
            observer.activeStateChanged(false);
            return;
        }
        if (observer.mLastVersion >= mVersion) {
            // 3. 判断当前的数据是否是新数据
            return;
        }
    		// 4. 更新版本号
        observer.mLastVersion = mVersion;
    		// 5. 执行我们传入的Observer的回调。
        observer.mObserver.onChanged((T) mData);
    }
```

##### 4. 然后再看看`LiveData.postValue(xxx)`方法

```kotlin
public abstract class LiveData<T> {
	protected void postValue(T value) {
        boolean postTask;
        synchronized (mDataLock) {
            postTask = mPendingData == NOT_SET;
            // 1. 不管此时有没有在处理数据，均更新mPendingData
            mPendingData = value;
        }
        if (!postTask) {
            // 2. 如果mPendingData还没有被复原为NOT_SET，说明post到主线程的任务还没有被执行，
            //    此时直接返回。通过1和2我们可以知道，到下一次主线程执行mPostValueRunnable任务前，
            //    我们多次调用postValue方法，也只会执行一次步骤3，也就是最后，最后只会把最新的数据推送给观察者，
            //    中间的数据不会被推送。  
            return;
        }
        // 3. 到主线程执行mPostValueRunnable任务
        ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
    }
    
    private final Runnable mPostValueRunnable = new Runnable() {
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            Object newValue;
            synchronized (mDataLock) { 
                newValue = mPendingData;
                //1. 将mPendingData的值复原
                mPendingData = NOT_SET;
            }
            //2. 调用setValue方法向观察者推送数据。
            setValue((T) newValue);
        }
    };
}
```

小结：有三种情况**可能**会执行我们传递给`LiveData`的`Observer`的`onChange()`方法。

1.  `LifeCycleOwner`的所处的生命周期节点发生改变。
2. 调用`LiveData.setValue(xx)`方法。
3. 调用`LiveData.postValue(xx)`方法。

下图很清楚的表明了会调用`Observer`的`onChange()`方法的三种情况:

![LiveData调用Observer回调的几种情况](C:\Users\tucheng\Desktop\Relearn Android\图片\LiveData调用Observer回调的几种情况.png)

#### 三、可以一直观察的ObserverForever(@NonNull Observer<? super T> observer)

`LiveData.ObserverForever`方法不需要我们传入`LifeCycleOwner`对象，所以自然无法与我们当前页面的`LifeCycleOwner`发生联系,

可以在没有`LifeCycleOwner`的地方调用此方法去监听`livedata`的变化，或者当我们需要长期观察`livedata`数据的时候使用。如果在界面中使用，不要忘记在界面`destroy`的时候手动`remove`掉我们传入的`Observer`对象(其他可能出现异常的情况下也需要主动`remove`)。

##### 1. 先看observerForever方法：

```java
@MainThread
    public void observeForever(@NonNull Observer<? super T> observer) {
        // 1. 确保在主线程调用
        assertMainThread("observeForever");
        // 2. 使用AlwaysActiveObserver将传入的Observer对象包装起来
        AlwaysActiveObserver wrapper = new AlwaysActiveObserver(observer);
        ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
        if (existing instanceof LiveData.LifecycleBoundObserver) {
            // 3. 如果该Observer对象之前被LiveData.observe方法添加到map中，则抛出异常
            throw new IllegalArgumentException("Cannot add the same observer"
                    + " with different lifecycles");
        }
        if (existing != null) {
            // 4. 如果该Observer对象之前已经被LifeData.observerForever方法添加到了map中，则直接返回
            return;
        }
        // 5. 将wrapper中的active变量置为true，之后也一直为true，不会在改变了。
        wrapper.activeStateChanged(true);
    }
```

##### 2. 再看 AlwaysActiveObserver包装类：

```java
private class AlwaysActiveObserver extends ObserverWrapper {
        AlwaysActiveObserver(Observer<? super T> observer) {
            super(observer);
        }

        @Override
        boolean shouldBeActive() {
            return true;
        }
    }
```

这个类非常简单，只重写了`shouldBeActive`方法，并始终返回`true`, 这样在`considerNotify(ObserverWrapper observer)`方法中，只要数据更新了(`version`比对)，就会直接推送数据。

#### 总结

1. 当我们给`LiveData`添加一个`Observer`的时候，会用` LifecycleBoundObserver` 包装 `Observer`，而 `LifecycleBoundObserver `可以感应生命周期(实际上就是我们当传入的`LifecycleOwner` 所处的 **生命周期节点发生改变** 的时候，会回调我们传给`LifecycleOwner`的`LifecycleBoundObserver `包装类的`onStateChanged(xxx)`方法)。从而当我们的`LifeCycleOwner`(如`Activity`或`Fragment`实例)生命周期发生变化的时候，如果当前`LifeCycleOwner`已被销毁，可以帮助我们移除当前的Observer，

   从而避免**页面已被销毁**的情况下`Observer`的`onChange`方法被调用而发生错误。

2. 当`LifeCycleOwner`由非激活状态变为激活状态(`onResume`和`onPause`)的时候, 会比较`LiveData`的`version`和包装类`LifeCycleBoundObserver`中的`version`,如果`LiveData`中的`version`比`LifeCycleBoundObserver`大，说明数据需要更新，则调用`Observer`的`onChange`方法推送数据，所以`LiveData`的推送事件为**粘性事件**(粘性事件是指：发布者发送事件的动作发生在 **订阅者订阅该事件的动作** 之前，订阅者在订阅之后，**仍然可以处理该事件**)。所以在多个页面共享`ViewModel`的时候，订阅同一个`LiveData`会出现 "**数据倒灌**" 的现象(这个词来源于小专栏 **《重学安卓》** 的作者)。

3. 当调用`setValue`或`postValue`（实际上最终也是调用`setValue`）的时候，首先将`LiveData`的`version`加一，然后遍历所有的`ObserverWrapper`,  逐一取出`LifeCycleOwner`，判断是否该`owner`是否处于激活状态，是的话则推送数据，否则不推送。

4. 使用`LiveData.observerForever`方法添加订阅者时，可以一直观察`LiveData`数据的变化，但是注意要在合适的时候进行`removeObserver`操作。

