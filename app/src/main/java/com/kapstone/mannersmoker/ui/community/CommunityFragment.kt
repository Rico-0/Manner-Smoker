package com.kapstone.mannersmoker.ui.community

import androidx.recyclerview.widget.LinearLayoutManager
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentCommunityBinding
import com.kapstone.mannersmoker.model.data.Post
import java.util.*

class CommunityFragment : BaseFragment<FragmentCommunityBinding>() {

    private val tmpPostList : MutableList<Post> by lazy { mutableListOf() }
    private val postAdapter : PostAdapter by lazy { PostAdapter() }

    override val layoutResourceId: Int
        get() = R.layout.fragment_community

    override fun initStartView() {
        tmpPushPostData()
        postAdapter.setPostsList(tmpPostList)
        binding.communityPostListView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = postAdapter
        }
    }

    private fun tmpPushPostData() {
        tmpPostList.add(Post("홍길동", Date(), "금연 5일차인데 죽을 것 같다.. 어차피 언젠간 다시 필 것 같은데 그냥 관둘까?"))
        tmpPostList.add(Post("박길동", getCalendar(-1).time, "금연 4일차인데 죽을 것 같다.. 어차피 언젠간 다시 필 것 같은데 그냥 관둘까?"))
        tmpPostList.add(Post("정길동", getCalendar(-2).time, "금연 3일차인데 죽을 것 같다.. 어차피 언젠간 다시 필 것 같은데 그냥 관둘까?"))
        tmpPostList.add(Post("조길동", getCalendar(-3).time, "금연 2일차인데 죽을 것 같다.. 어차피 언젠간 다시 필 것 같은데 그냥 관둘까?"))
        tmpPostList.add(Post("이길동", getCalendar(-4).time, "금연 1일차인데 죽을 것 같다.. 어차피 언젠간 다시 필 것 같은데 그냥 관둘까?"))

    }

    private fun getCalendar(amount : Int) : Calendar {
        return Calendar.getInstance().apply {
            add(Calendar.DATE, amount)
        }
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