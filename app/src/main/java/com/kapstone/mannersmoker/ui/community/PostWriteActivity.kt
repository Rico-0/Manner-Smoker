package com.kapstone.mannersmoker.ui.community

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityWritePostBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.post.PostGetModel
import com.kapstone.mannersmoker.model.data.post.PostSendModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostWriteActivity : BaseActivity2<ActivityWritePostBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_write_post

    private lateinit var smokeDao : SmokeDao

    override fun initStartView() {
        smokeDao = RetrofitInstance.smokeDao
        Glide.with(this)
            .load(user_profile_image)
            .error(R.drawable.my)
            .into(binding.proflieImage)
        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        binding.writePostButton.setOnClickListener {
            if (binding.writePostContent.text.toString() == "") {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("게시글 등록 오류")
                    .setMessage("내용이 입력되지 않았습니다. 내용을 입력해 주세요.")
                    .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                           return
                        }
                    })
                dialog.show()
            } else {
                smokeDao.sendPost(PostSendModel(binding.writePostContent.text.toString(), user_id_from_server))
                    .enqueue(object : Callback<PostGetModel> {
                        override fun onResponse(
                            call: Call<PostGetModel>,
                            response: Response<PostGetModel>
                        ) {
                            if (response.isSuccessful) {
                                Log.d(TAG, "게시글 등록 응답 코드 : ${response.code()}")
                                Toast.makeText(this@PostWriteActivity, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                this@PostWriteActivity.finish()
                            } else {
                                Log.d(TAG, "게시글 등록 응답 코드 : ${response.code()}")
                            }
                        }
                        override fun onFailure(call: Call<PostGetModel>, t: Throwable) {
                            Toast.makeText(this@PostWriteActivity, "게시글 등록 실패 : $t", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
        binding.writePostCancelButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("게시글 취소 확인")
                .setMessage("이 페이지를 벗어나면 작성한 내용은 저장되지 않습니다. 정말 게시글 작성을 취소할까요?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        this@PostWriteActivity.finish()
                    }
                }).setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        return
                    }
                })
            dialog.show()
        }
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 막기
    }
}