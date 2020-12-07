package com.example.relearnandroid.coor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.relearnandroid.R
import kotlinx.android.synthetic.main.test_coordinator_layout.*

/**
 *
 * @ClassName: TestCoor
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/12/7 22:17
 * @Version 1.0
 *
 **/
class TestCoor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_coordinator_layout)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TestCoor)
            adapter = MyAdapter(arrayOfNulls(100))
        }
    }

    companion object {
        fun startTestCoorActivity(context: Context) {
            context.startActivity(Intent(context, TestCoor::class.java))
        }
    }

}

class MyAdapter(private val list: Array<Int?>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.test_coor_item, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.v.findViewById<TextView>(R.id.coor_text).text = "测试测试sagasg发给啊啊大给again啊 $position"
    }

}