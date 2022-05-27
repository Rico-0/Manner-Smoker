package com.kapstone.mannersmoker.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.kapstone.mannersmoker.util.PermissionUtil
import com.kapstone.mannersmoker.util.PreferencesManager
import com.kapstone.mannersmoker.util.PreferencesManager.isForegroundPermissionChecked
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BaseActivity2<T : ViewDataBinding> : AppCompatActivity() {
    val TAG: String = this.javaClass.simpleName // 해당 클래스의 이름으로 로그 출력 시 태그 설정

    // 데이터 바인딩 객체 선언 방법
    lateinit var binding: T

    // 레이아웃 이름(아이디) 는 상속받는 액티비티마다 다르므로 추상 필드로 선언
    abstract val layoutResourceId: Int

//    abstract val mainViewModel: R

    // 메모리 누수 방지를 위해 RxJava에서 CompositeDisposable() 을 사용
    // 생성된 모든 Observable 을 안드로이드 생명주기에 맞춰 한 번에 모두 해제 가능
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 데이터 바인딩 사용 : Activity에서 사용하는 경우 DataBindingUtil.setContentView를 사용한다.
        Log.d(TAG, "isForegroundPermissionChecked : $isForegroundPermissionChecked")

        binding = DataBindingUtil.setContentView(this, layoutResourceId)
        initStartView()
    }

    abstract fun initStartView()


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "++onStart()")
    }

    override fun onResume() {
        Log.d(TAG, "++onResume()")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "++onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "++onStop()")
        super.onStop()
    }

    /*
      서버 통신을 한 화면에서 여러 번 수행하는 경우, 사용자가 화면 이탈 시 한꺼번에 구독 해제시키기 위해 CompositeDisposable에 넣고 onDestroy() 쯤에서 dispose()를 호출
      dispose()의 경우 clear() 와는 다르게 이 메서드가 수행되면 새로운 Disposable 객체를 받을 수 없음
    */
    override fun onDestroy() {
        Log.d(TAG, "++onDestroy()")
        compositeDisposable.dispose()
        super.onDestroy()
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}
