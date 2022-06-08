package com.kapstone.mannersmoker.ui.community

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityModifyPostBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.post.PostGetModel
import com.kapstone.mannersmoker.model.data.post.PostModifyModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.DateUtil
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostModifyActivity : BaseActivity2<ActivityModifyPostBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_modify_post

    private lateinit var smokeDao: SmokeDao

    private var postId: Int = -1
    private lateinit var content: String
    private lateinit var title: String
    private var userId : Int = -1

    override fun initStartView() {
        setPostData()
        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        binding.modifyPostButton.setOnClickListener {
            modifyPost()
        }
        binding.modifyPostCancelButton.setOnClickListener {
            checkCancel()
        }
    }

    private fun checkCancel() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("게시글 취소 확인")
            .setMessage("이 페이지를 벗어나면 작성한 내용은 저장되지 않습니다. 정말 게시글 작성을 취소할까요?")
            .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    this@PostModifyActivity.finish()
                }
            }).setNegativeButton("취소", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    return
                }
            })
        dialog.show()
    }

    private fun modifyPost() {
        smokeDao = RetrofitInstance.smokeDao
        if (binding.postUserTitle.text.toString() == "" || binding.postUserContent.text.toString() == "") {
            val dialog = AlertDialog.Builder(this)
                .setTitle("게시글 수정 오류")
                .setMessage("제목 혹은 내용이 입력되지 않았습니다.")
                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        return
                    }
                })
            dialog.show()
        } else {
            val dialog = AlertDialog.Builder(this)
                .setTitle("게시글 수정 확인")
                .setMessage("게시글을 수정할까요?")
                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        smokeDao.modifyPost(postId, PostModifyModel(binding.postUserTitle.text.toString(), binding.postUserContent.text.toString()))
                            .enqueue(object :
                                Callback<PostGetModel> {
                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun onResponse(
                                    call: Call<PostGetModel>,
                                    response: Response<PostGetModel>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            this@PostModifyActivity,
                                            "게시글 수정이 완료되었습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(applicationContext, PostDetailActivity::class.java).apply {
                                            putExtra("modifiedTitle", binding.postUserTitle.text.toString())
                                            putExtra("modifiedContent", binding.postUserContent.text.toString())
                                            putExtra("postId", postId)
                                            putExtra("userId", userId)
                                            putExtra("modifiedDate", DateUtil.LocalDateTimeToString(response.body()?.postData?.modifiedDate!!))
                                        }
                                        setResult(RESULT_OK, intent)
                                        if (!isFinishing) finish()
                                    } else {
                                        Log.d("adapterr", "게시글 수정 코드 : ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<PostGetModel>, t: Throwable) {
                                    Log.d("adapterr", "게시글 수정 실패 : $t")
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

    private fun setPostData() {
        Glide.with(this)
            .load(user_profile_image)
            .error(R.drawable.my)
            .into(binding.postUserProfileImage)
        binding.postUserName.text = user_id

        postId = intent.getIntExtra("postId", -1)
        content = intent.getStringExtra("content") ?: ""
        title = intent.getStringExtra("title") ?: ""
        userId = intent.getIntExtra("userId", -1)

        binding.postUserTitle.setText(title)
        binding.postUserContent.setText(content)
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 막기
    }
}