package com.example.relearnandroid.recyclerview.snapler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.relearnandroid.R
import kotlinx.android.synthetic.main.test_snap_helper_rv_item.view.*

/**
 *
 * @ClassName: SnapHelperRvAdapter
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/5 21:56
 * @Version 1.0
 *
 **/
class SnapHelperRvAdapter(private val data: Array<Int?>) :
    RecyclerView.Adapter<SnapHelperRvAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.test_snap_helper_rv_item, parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.snap_helper_item_text_view.text = "第 $position 个"
    }
}