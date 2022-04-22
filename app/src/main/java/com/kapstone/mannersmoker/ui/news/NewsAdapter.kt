package com.kapstone.mannersmoker.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.databinding.ItemNewsBinding
import com.kapstone.mannersmoker.model.data.News
import com.kapstone.mannersmoker.model.data.Post

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private lateinit var newsList : MutableList<News>
    private var listener : OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v : View, news : News, pos : Int)
    }

    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding : ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(news: News) {
            binding.newsTitle.text = news.title
            binding.newsContent.text = news.content
            Glide.with(binding.root.context)
                .load(news.thumbnailUrl)
                .into(binding.newsThumbnail)

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, news, pos)
                }
            }
        }

    }
    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size

    fun setPostsList(news : MutableList<News>) {
        newsList = news
    }
}

