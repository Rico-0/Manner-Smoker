package com.kapstone.mannersmoker.ui.news

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentNewsBinding
import com.kapstone.mannersmoker.model.data.News

class NewsFragment : BaseFragment<FragmentNewsBinding>() {

    private val tmpNewsList : MutableList<News> by lazy { mutableListOf() }
    private val newsAdapter : NewsAdapter by lazy { NewsAdapter() }

    override val layoutResourceId: Int
        get() = R.layout.fragment_news

    override fun initStartView() {
        tmpPushNewsData()
        newsAdapter.setPostsList(tmpNewsList)
        binding.newsListView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }
        newsAdapter.setOnItemClickListener(object : NewsAdapter.OnItemClickListener {
            override fun onItemClick(v: View, news: News, pos: Int) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.originalContentUrl))
                startActivity(intent)
            }
        })
    }

    private fun tmpPushNewsData() {
        tmpNewsList.add(News("담배꽁초, 아무 데나 버리는 순간 생태계가 멈춘다", "세계질병부담(The Global Burden of Diseases)에 의하면 2019년 기준으로 11억 3,000만 명 흡연자가 7조 4,100억 개비의 담배를 소비했다. 그중 4분의 3분량의 담배꽁초가 버려졌다. 버려진 담배꽁초는..", "https://www.sciencetimes.co.kr/wp-content/uploads/2021/11/%EB%8B%B4%EB%B0%B0%EA%BD%81%EC%B4%88-%EC%9C%84%ED%97%98-480x321.jpg", "https://www.sciencetimes.co.kr/news/%EB%8B%B4%EB%B0%B0%EA%BD%81%EC%B4%88-%EC%95%84%EB%AC%B4-%EB%8D%B0%EB%82%98-%EB%B2%84%EB%A6%AC%EB%8A%94-%EC%88%9C%EA%B0%84-%EC%83%9D%ED%83%9C%EA%B3%84%EA%B0%80-%EB%A9%88%EC%B6%98%EB%8B%A4/"))
        tmpNewsList.add(News("새해 목표는 금연? 준비과정 '6단계'", "우리나라 성인 남성 흡연율은 34%. 하지만 이중 70%는 금연을 원한다는 통계가 있다. 금연을 해야 하는 이유는 명확하다. 흡연이 식도암, 후두암, 폐암, 췌장암, 방광암, 신장암, 자궁암, 뇌졸중, 심장질환, 만성폐쇄성폐질환 등..", "https://health.chosun.com/site/data/img_dir/2022/01/25/2022012501471_0.jpg", "https://m.health.chosun.com/svc/news_view.html?contid=2022012501477"))
        tmpNewsList.add(News("새해에 반드시 '금연'해야 하는 또 하나의 이유", "새해에는 금연을 결심하는 사람들이 많다. 금연은 자신은 물론 주변 사람의 건강을 위해서 필수이다. 그런데 금연을 하고 있더라도 스트레스를 받으면 다시 흡연을 하는 경우가 많다. 왠지 흡연을 하면 스트레스가..", "https://src.hidoc.co.kr/image/lib/2022/1/10/1641794890218_0.jpg", "https://www.hidoc.co.kr/healthstory/news/C0000661732"))
        tmpNewsList.add(News("담배로 스트레스 해소? 오히려 '우울·불안 악화'", "흡연이 해롭다는 것은 누구나 아는 사실이지만, 그럼에도 금연에 성공하지 못하는 데에는 여러 원인이 작용한다고 볼 수 있습니다. 흡연자 중에는 일정 기간 담배를 끊었다가 스트레스를 받으면 다시 담배를 찾는..", "https://src.hidoc.co.kr/image/lib/2022/4/19/1650332123682_0.jpg", "https://www.hidoc.co.kr/healthstory/news/C0000685229"))
        tmpNewsList.add(News("담배, 줄이기만 해도 암 위험 뚝↓", "금연이 도저히 힘들다면, 흡연량을 줄이는 것부터 해보자. 아예 끊지 않고 피우는 담배 개수를 줄이는 것만으로도 암 발병 위험을 낮출 수 있다는 연구 결과가 나왔다. 삼성서울병원 가정의학과 신동욱 교수, 서울대병원 강남센터..", "https://health.chosun.com/site/data/img_dir/2022/04/08/2022040801726_0.jpg", "https://m.health.chosun.com/svc/news_view.html?contid=2022040801728"))
    }
}