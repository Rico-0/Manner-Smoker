package com.kapstone.mannersmoker.ui.community

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.databinding.ItemCommunityPostBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.post.Post
import com.kapstone.mannersmoker.model.data.reply.ReplyDataClass
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.DateUtil
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapter(
    private val context: Context,
    private var postList: List<Post>,
    private val userName: String,
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    companion object {
        private val smokeDao : SmokeDao = RetrofitInstance.smokeDao
    }

    private var postArrayList = ArrayList<Post>(postList)

    private lateinit var modifyPostClickListener : ((Int, String, String) -> Unit)
    private lateinit var deletePostClickListener : ((Int) -> Unit)

    fun setDeletePostClickListener(listener : ((Int) -> Unit)) {
        this.deletePostClickListener = listener
    }

    fun setModifyPostClickListener(listener : ((Int, String, String) -> Unit)) {
        this.modifyPostClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemCommunityPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(post: Post) {
            Glide.with(context)
                .load(user_profile_image)
                .error(R.drawable.my)
                .into(binding.postUserProfileImage)
            binding.postUserName.text = userName
            binding.postUserTitle.text = post.title
            binding.postUserContent.text = if (post.content.length >= 100) post.content + "..." else post.content
            binding.postUserDate.text = if (post.modifiedDate != null) DateUtil.LocalDateTimeToString(post.modifiedDate) else DateUtil.LocalDateTimeToString(post.createdDate)
            smokeDao.getReplys(post.postId).enqueue(object : Callback<ReplyDataClass> {
                override fun onResponse(
                    call: Call<ReplyDataClass>,
                    response: Response<ReplyDataClass>
                ) {
                    if (response.isSuccessful){
                        val replys = response.body()
                        replys?.replyData?.let{
                            binding.postCommentCount.text = replys.replyData.size.toString()
                        }
                    }
                }
                override fun onFailure(call: Call<ReplyDataClass>, t: Throwable) {
                   Log.d("adapterr", "postAdapter : 댓글 개수 받아오기 실패")
                }
            })

            // 게시글 클릭 시 이동
            binding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) { // 어댑터에서 삭제된 데이터가 아닌 경우 실행
                    val post = postList.get(pos)
                    val intent = Intent(context, PostDetailActivity::class.java).apply {
                        putExtra("fromCommunity", true)
                        putExtra("content", post.content)
                        putExtra("title", post.title)
                        putExtra("userId", post.userId)
                        putExtra("createdDate", DateUtil.LocalDateTimeToString(post.createdDate))
                        putExtra("modifiedDate", DateUtil.LocalDateTimeToString(post.modifiedDate))
                        putExtra("postId", post.postId)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(postArrayList[position])
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }

    fun update(newList : List<Post>) {
        val diffResult = DiffUtil.calculateDiff(ContentDiffUtil(postList, newList), false)
        diffResult.dispatchUpdatesTo(this) //Dispatches the update events to the given adapter. 주어진 어댑터에 변경사항을 전달한다.
        postArrayList.clear()
        postArrayList.addAll(newList)
    }

    inner class ContentDiffUtil(private val oldList: List<Post>, private val currentList: List<Post>) : DiffUtil.Callback() {

        //1. 아이템의 고유 id 값이 같은지
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].postId == currentList[newItemPosition].postId
        }

        //2. id 가 같다면, 내용물도 같은지 확인 equals()
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == currentList[newItemPosition]
        }

        //변화하기 전 데이터셋 사이즈
        override fun getOldListSize(): Int = oldList.size
        //변화한 후 데이터셋 사이즈
        override fun getNewListSize(): Int = currentList.size
    }

}