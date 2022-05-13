package com.kapstone.mannersmoker.ui.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.BallonLayoutBinding
import com.kapstone.mannersmoker.databinding.FragmentSmokePlaceDetailBinding
import com.kapstone.mannersmoker.ui.main.findNavControllerSafely
import kotlinx.android.parcel.Parcelize
import net.daum.mf.map.api.MapPoint
import kotlin.math.roundToInt

class SmokePlaceDetailFragment : BaseFragment<FragmentSmokePlaceDetailBinding>() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로가기 버튼 막아서 카카오맵 검은색으로 되는 문제 해결
        }
    }

    companion object {
        private const val NAV_ID = R.id.action_go_to_smoke_place_detail

        fun start(
            fragment: Fragment,
            argument: Argument,
            navOptions: NavOptions? = null
        ) {
            try { // 다른 프래그먼트에서 start 함수를 호출하면, 해당 프래그먼트에 정의된 이동 경로를 찾아 이동
                fragment.findNavController().navigate(
                    // bundleOf로 넘어온 값을 저장할 수 있다. Argument 타입은 밑에 있음
                    NAV_ID, bundleOf("argument" to argument), navOptions
                )
            } catch (e: Exception) {
                Log.d("SmokePlaceDetail" , "error : $e")
            }
        }
    }

    @Parcelize
    data class Argument ( // Fragment가 전달받을 데이터
        val currentLatitude : Double,
        val currentLongtitude : Double,
        val smokePlaceLatitude : Double,
        val smokePlaceLongtitude : Double,
        val smokePlaceImage : String,
        val smokePlaceName : String,
        val smokePlaceAddress : String,
        val distance : String
    ) : Parcelable

    private val argument: Argument by lazy {
        arguments?.getParcelable<Argument>("argument") // ScreenSlidePagerAdapter에서 bundleOf에 argument라는 키로 값 저장한 것 불러오기
            ?: throw IllegalArgumentException("Argument must exist")
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_smoke_place_detail

    override fun initStartView() {
       /* Glide.with(binding.root)
            .load(argument.smokePlaceImage)
            .error(R.drawable.smoke)
            .into(binding.smokePlaceImage) */
        binding.smokePlaceName.text = argument.smokePlaceName
        binding.smokePlaceAddress.text = argument.smokePlaceAddress
        binding.distanceFromCurrentPlace.text = argument.distance

        binding.goToFindDestination.setOnClickListener {
            showDialog(argument.smokePlaceLatitude, argument.smokePlaceLongtitude)
        }
        binding.goToFindDestinationCancel.setOnClickListener {
            findNavControllerSafely()?.navigate(R.id.action_go_to_main)
        }
    }

    private fun showDialog(lat : Double, lon : Double) {
        val dialog = MapDialog(requireContext())
        dialog.setAcceptBtnClickListener {
            showVehicleChoice(lat, lon)
        }
        dialog.setDialog()
    }

    private fun showVehicleChoice(lat : Double, lon : Double) {
        val builder = AlertDialog.Builder(requireContext())
        val itemList = arrayOf("자동차", "대중교통", "도보")
        builder.setTitle("이동 수단을 선택하세요.")
        builder.setItems(itemList) { dialog, which ->
            when (which) {
                0 -> searchDestination("자동차", lat, lon)
                1 -> searchDestination("대중교통", lat, lon)
                2 -> searchDestination("도보", lat, lon)
            }
        }
        builder.show()
    }

    private fun searchDestination(vehicle: String, destinationLat : Double, destinationLon : Double) {
        when (vehicle) {
            "자동차" -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "kakaomap://route?sp=${argument.currentLatitude},${argument.currentLongtitude}&ep=${destinationLat},${destinationLon}&by=CAR"
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "카카오맵이 설치되어 있지 않습니다. 설치 화면으로 이동합니다.",
                        Toast.LENGTH_SHORT
                    ).show();
                    val intent =
                        Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            "대중교통" -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "kakaomap://route?sp=${argument.currentLatitude},${argument.currentLongtitude}&ep=${destinationLat},${destinationLon}&by=PUBLICTRANSIT"
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "카카오맵이 설치되어 있지 않습니다. 설치 화면으로 이동합니다.",
                        Toast.LENGTH_SHORT
                    ).show();
                    val intent =
                        Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            "도보" -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "kakaomap://route?sp=${argument.currentLatitude},${argument.currentLongtitude}&ep=${destinationLat},${destinationLon}&by=FOOT"
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "카카오맵이 설치되어 있지 않습니다. 설치 화면으로 이동합니다.",
                        Toast.LENGTH_SHORT
                    ).show();
                    val intent =
                        Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback);
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}