package com.ckh.org.rssnews.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ckh.org.rssnews.R
import com.ckh.org.rssnews.listener.NewsItemClickListener
import com.ckh.org.rssnews.viewholder.NewsRecyclerViewHolder
import com.ckh.org.rssnews.vo.Data

class NewsRecyclerAdapter: ListAdapter<Data, NewsRecyclerViewHolder>(ListItemCallback()){
    private var items: ArrayList<Data> = ArrayList()
    private lateinit var itemClickListener: NewsItemClickListener;
    class ListItemCallback: DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRecyclerViewHolder {
        return NewsRecyclerViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.news_recycler_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: NewsRecyclerViewHolder, position: Int) {
        holder.setItem(items[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Data {
        return items[position]
    }

    fun addItem(data: Data){
        items.add(data)
    }

    fun setItemClickListener(itemClickListener: NewsItemClickListener){
        this.itemClickListener = itemClickListener
    }

    fun addAllItem(datas: ArrayList<Data>){
        this.items = datas
        notifyDataSetChanged()
    }
}