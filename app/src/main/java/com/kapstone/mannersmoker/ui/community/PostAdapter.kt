package com.kapstone.mannersmoker.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kapstone.mannersmoker.databinding.ItemCommunityPostBinding
import com.kapstone.mannersmoker.model.data.Post

class PostAdapter : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

   private lateinit var postList : MutableList<Post>

    inner class ViewHolder(private val binding : ItemCommunityPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(post : Post) {
            // binding.userprofileimage ...
            binding.postUserName.text = post.user_name
            binding.postUserTitle.text = post.title
            binding.postUserContent.text = post.content
        }
    }
    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(postList[position])
    }

    override fun getItemCount(): Int = postList.size

    fun setPostsList(posts : MutableList<Post>) {
        postList = posts
    }
}