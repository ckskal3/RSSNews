package com.ckh.org.rssnews.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ckh.org.rssnews.listener.NewsItemClickListener
import com.ckh.org.rssnews.vo.Data
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.news_recycler_list_item.view.*

class NewsRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), LayoutContainer{
    override val containerView: View?
        get() = itemView

    fun setItem(item : Data, itemClickListener: NewsItemClickListener){
        with(itemView){
            NewsTitleTV.text = item.title
            NewsDesTV.text = item.des
            NewsTag1TV.text = item.tag1
            NewsTag2TV.text = item.tag2
            NewsTag3TV.text = item.tag3

            Glide.with(this).load(item.img).override(100,100).into(NewsThumbNailIV)

            setOnClickListener{
                itemClickListener.onClick(item.link)
            }
        }
    }
}