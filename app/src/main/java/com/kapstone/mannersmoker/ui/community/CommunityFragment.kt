package com.kapstone.mannersmoker.ui.community

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentCommunityBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.post.PostDataClass
import com.kapstone.mannersmoker.model.data.post.PostGetModel
import com.kapstone.mannersmoker.model.data.post.PostModifyModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityFragment : BaseFragment<FragmentCommunityBinding>() {

    private lateinit var smokeDao: SmokeDao

    private lateinit var postAdapter: PostAdapter

    override val layoutResourceId: Int
        get() = R.layout.fragment_community

    override fun initStartView() {
        smokeDao = RetrofitInstance.smokeDao
        getAllPost()
        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        binding.writePostFloatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), PostWriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllPost() {
        smokeDao.getAllPost().enqueue(object : Callback<PostDataClass> {
            override fun onResponse(call: Call<PostDataClass>, response: Response<PostDataClass>) {
                if (response.isSuccessful) {
                        val posts = response.body()
                        Log.d(TAG, "리스트 개수 : ${posts?.postData?.size}")
                        val linearLayoutManager = LinearLayoutManager(
                            requireActivity(),
                            LinearLayoutManager.VERTICAL,
                            true
                        )
                        postAdapter = posts?.postData?.let { PostAdapter(requireContext(), it) }!!
                        postAdapter.setDeletePostClickListener { postId ->
                            val dialog = AlertDialog.Builder(requireContext())
                                .setTitle("게시글 삭제 확인")
                                .setMessage("이 게시글을 정말 삭제할까요?")
                                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        smokeDao.deletePost(postId).enqueue(object : Callback<PostGetModel> {
                                            override fun onResponse(
                                                call: Call<PostGetModel>,
                                                response: Response<PostGetModel>
                                            ) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(requireContext(), "게시글 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
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
                                .setNegativeButton("아니오", object : DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                       return
                                    }
                                })
                            dialog.show()
                        }
                    postAdapter.setModifyPostClickListener { postId, content ->
                        val intent = Intent(requireContext(), PostModifyActivity::class.java).apply {
                            putExtra("postId", postId)
                            putExtra("content", content)
                        }
                        startActivity(intent)
                    }
                        requireActivity().runOnUiThread {
                            binding.communityPostListView.apply {
                                layoutManager = linearLayoutManager
                                setHasFixedSize(false)
                                setItemViewCacheSize(10)
                                adapter = postAdapter
                            }
                        }
                     }
                 }
            override fun onFailure(call: Call<PostDataClass>, t: Throwable) {
                Toast.makeText(requireContext(), "커뮤니티 데이터 받아오기 실패 : $t", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "커뮤니티 데이터 받아오기 실패 : $t")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getAllPost()
    }

    /* private fun observeData() {
         mainActivity?.mainViewModel?.postList?.observe(viewLifecycleOwner, {
             it.let { posts ->
                 postAdapter.update(comments)
                 Handler(Looper.getMainLooper()).post {
                     binding.postList.scrollToPosition(posts.size - 1)
                 }
             }
         })
     } */
}