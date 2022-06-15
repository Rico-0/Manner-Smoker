package com.kapstone.mannersmoker.ui.community

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityPostDetailBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.post.PostGetModel
import com.kapstone.mannersmoker.model.data.post.PostModifyModel
import com.kapstone.mannersmoker.model.data.reply.ReplyDataClass
import com.kapstone.mannersmoker.model.data.reply.ReplyGetModel
import com.kapstone.mannersmoker.model.data.reply.ReplySendModel
import com.kapstone.mannersmoker.model.data.reply.ReplyViewModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.PreferencesManager
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class PostDetailActivity : BaseActivity2<ActivityPostDetailBinding>() {

    private lateinit var smokeDao: SmokeDao

    private lateinit var replyAdapter: ReplyAdapter

    private var postId: Int = -1
    private var userId: Int = -1
    private lateinit var content: String
    private lateinit var nickname : String
    private lateinit var thumbnailURL : String
    private lateinit var createdDate: String
    private var modifiedDate: String = ""

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override val layoutResourceId: Int
        get() = R.layout.activity_post_detail

    override fun initStartView() {
        smokeDao = RetrofitInstance.smokeDao
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    // modifyActivity에서 갖고 온 intent (it)
                    val modifiedData: Intent? = it.data
                    Glide.with(this)
                        .load(user_profile_image)
                        .error(R.drawable.my)
                        .into(binding.postUserProfileImage)
                    binding.postUserName.text = user_id
                    binding.postUserContent.text = modifiedData?.getStringExtra("modifiedContent") ?: ""
                    binding.postUserDate.text = modifiedData?.getStringExtra("modifiedDate") ?: ""
                    userId = modifiedData?.getIntExtra("userId", -1) ?: -1
                    postId = it.data?.getIntExtra("postId", -1) ?: -1
                }
            }
        getPostData()
        bindingContents()
        getAllReply()
    }

    private fun bindingContents() {
        Glide.with(this)
            .load(thumbnailURL)
            .error(R.drawable.my)
            .into(binding.postUserProfileImage)
        binding.postUserName.text = nickname
        binding.postUserContent.text = content
        // CommunityFragment에서 넘어옴
        if (intent.getStringExtra("modifiedDate") != "")
            binding.postUserDate.text = modifiedDate
        else
            binding.postUserDate.text = createdDate

        binding.writeCommentButton.setOnClickListener {
            if (binding.comment.text.toString() == "") {
                Toast.makeText(this, "입력된 댓글 내용이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                sendReply(postId, binding.comment.text.toString(), user_id_from_server)
            }
        }

        binding.postModify.visibility =
            if (userId == user_id_from_server) View.VISIBLE else View.GONE
        binding.postDelete.visibility =
            if (userId == user_id_from_server) View.VISIBLE else View.GONE

        binding.postModify.setOnClickListener {
            val intent = Intent(applicationContext, PostModifyActivity::class.java).apply {
                putExtra("postId", postId)
                putExtra("content", content)
                putExtra("title", title)
                putExtra("userId", userId)
            }
            activityResultLauncher.launch(intent)
        }

        binding.postDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("게시글 삭제 확인")
                .setMessage("이 게시글을 삭제할까요?")
                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        smokeDao.deletePost(postId).enqueue(object : Callback<PostGetModel> {
                            override fun onResponse(
                                call: Call<PostGetModel>,
                                response: Response<PostGetModel>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@PostDetailActivity,
                                        "게시글 삭제가 완료되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    Log.d("adapterr", "게시글 삭제 코드 : ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<PostGetModel>, t: Throwable) {
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

    private fun getPostData() {
        if (intent.getBooleanExtra("fromCommunity", false)) {
            content = intent.getStringExtra("content") ?: ""
            postId = intent.getIntExtra("postId", -1)
            userId = intent.getIntExtra("userId", -1)
            createdDate = intent.getStringExtra("createdDate") ?: ""
            modifiedDate = intent.getStringExtra("modifiedDate") ?: ""
            nickname = intent.getStringExtra("nickname") ?: ""
            thumbnailURL = intent.getStringExtra("thumbnail") ?: ""
        }
    }

    private fun sendReply(postId: Int, replyContent: String, userId: Int) {
        smokeDao.sendReply(ReplySendModel(postId, replyContent, userId))
            .enqueue(object : Callback<ReplyGetModel> {
                override fun onResponse(
                    call: Call<ReplyGetModel>,
                    response: Response<ReplyGetModel>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.replyData?.let {
                            Toast.makeText(
                                this@PostDetailActivity,
                                "댓글이 등록되었습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.comment.text.clear()
                            replyAdapter.notifyDataSetChanged()
                            getAllReply()
                        }
                    } else {
                        Log.d(TAG, "댓글 등록 코드 : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ReplyGetModel>, t: Throwable) {
                    Log.d(TAG, "댓글 등록 실패 : $t")
                }
            })
    }

    private fun deleteReply(replyId : Int) {
        val dialog = AlertDialog.Builder(this@PostDetailActivity)
            .setTitle("댓글 삭제 확인")
            .setMessage("이 댓글을 정말 삭제할까요?")
            .setPositiveButton("예", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    smokeDao.deleteReply(replyId).enqueue(object : Callback<ReplyGetModel> {
                        override fun onResponse(
                            call: Call<ReplyGetModel>,
                            response: Response<ReplyGetModel>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@PostDetailActivity, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                replyAdapter.notifyDataSetChanged()
                                getAllReply()
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

    private fun getAllReply() {
        smokeDao.getReplys(postId).enqueue(object : Callback<ReplyDataClass> {
            override fun onResponse(
                call: Call<ReplyDataClass>,
                response: Response<ReplyDataClass>
            ) {
                if (response.isSuccessful) {
                    val replys = response.body()
                    replys?.replyData?.let {
                        Log.d(TAG, "댓글 개수 : ${replys.replyData.size}")
                        binding.postCommentCount.text = replys.replyData.size.toString()
                        replyAdapter = ReplyAdapter(this@PostDetailActivity, it)
                    }

                    replyAdapter.setDeleteReplyClickListener { replyId ->
                        deleteReply(replyId)
                    }

                    val linearLayoutManager = LinearLayoutManager(
                        this@PostDetailActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )

                    this@PostDetailActivity.runOnUiThread {
                        binding.replyList.apply {
                            layoutManager = linearLayoutManager
                            setHasFixedSize(false)
                            setItemViewCacheSize(10)
                            adapter = replyAdapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ReplyDataClass>, t: Throwable) {
                Toast.makeText(this@PostDetailActivity, "댓글 데이터 받아오기 실패 : $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}