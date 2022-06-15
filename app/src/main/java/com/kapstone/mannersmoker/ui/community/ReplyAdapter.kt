package com.kapstone.mannersmoker.ui.community

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.databinding.ItemReplyBinding
import com.kapstone.mannersmoker.model.data.reply.Reply
import com.kapstone.mannersmoker.util.DateUtil
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image

class ReplyAdapter(
    private val context: Context,
    private var replyList: List<Reply>
) : RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    private lateinit var deleteReplyClickListener : ((Int) -> Unit)

    fun setDeleteReplyClickListener(listener : ((Int) -> Unit)) {
        this.deleteReplyClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(reply: Reply) {
            Glide.with(context)
                .load(reply.thumbnailURL)
                .error(R.drawable.my)
                .into(binding.replyUserProfileImage)
            binding.replyUserName.text = reply.nickname
            binding.replyUserContent.text = reply.replyContent
            binding.replyDate.text = DateUtil.LocalDateTimeToString(reply.createdDate)
            binding.replyDelete.visibility =
                if (reply.userId == user_id_from_server) View.VISIBLE else View.GONE

            // 댓글 삭제
            binding.replyDelete.setOnClickListener {
                deleteReplyClickListener.invoke(reply.replyId)
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
}