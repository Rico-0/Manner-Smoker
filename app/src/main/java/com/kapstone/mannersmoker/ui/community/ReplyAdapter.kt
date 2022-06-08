package com.kapstone.mannersmoker.ui.community

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.databinding.ItemReplyBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.post.Post
import com.kapstone.mannersmoker.model.data.reply.Reply
import com.kapstone.mannersmoker.model.data.reply.ReplyGetModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.DateUtil
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReplyAdapter(
    private val context: Context,
    private var replyList: List<Reply>,
    private val userName: String,
) : RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    private var replyArrayList = ArrayList<Reply>(replyList)

    companion object {
        private val smokeDao: SmokeDao = RetrofitInstance.smokeDao
    }

    inner class ViewHolder(private val binding: ItemReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(reply: Reply) {
            Glide.with(context)
                .load(user_profile_image)
                .error(R.drawable.my)
                .into(binding.replyUserProfileImage)
            binding.replyUserName.text = userName
            binding.replyUserContent.text = reply.replyContent
            binding.replyDate.text = DateUtil.LocalDateTimeToString(reply.createdDate)
            binding.replyDelete.visibility =
                if (reply.userId == user_id_from_server) View.VISIBLE else View.GONE

            // 댓글 삭제
            binding.replyDelete.setOnClickListener {
                val dialog = AlertDialog.Builder(context)
                    .setTitle("댓글 삭제 확인")
                    .setMessage("이 댓글을 정말 삭제할까요?")
                    .setPositiveButton("예", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            smokeDao.deleteReply(reply.replyId).enqueue(object : Callback<ReplyGetModel> {
                                override fun onResponse(
                                    call: Call<ReplyGetModel>,
                                    response: Response<ReplyGetModel>
                                ) {
                                    if (response.isSuccessful) {
                                        val pos = adapterPosition
                                        Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                        notifyItemRemoved(pos)
                                    } else {
                                        Log.d("adapterr", "댓글 삭제 응답 코드 : ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<ReplyGetModel>, t: Throwable) {
                                    Log.d("adapterr", "게시글 삭제 실패 : $t")
                                }
                            })
                        }
                    })
                    .setNegativeButton("아니오", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            return
                        }
                    })
                dialog.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(replyList[position])
    }

    override fun getItemCount(): Int {
        return replyList.size
    }

    fun update(newList : List<Reply>) {
        val diffResult = DiffUtil.calculateDiff(ContentDiffUtil(replyList, newList), false)
        diffResult.dispatchUpdatesTo(this) //Dispatches the update events to the given adapter. 주어진 어댑터에 변경사항을 전달한다.
        replyArrayList.clear()
        replyArrayList.addAll(newList)
    }

    inner class ContentDiffUtil(private val oldList: List<Reply>, private val currentList: List<Reply>) : DiffUtil.Callback() {

        //1. 아이템의 고유 id 값이 같은지
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].replyId == currentList[newItemPosition].replyId
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