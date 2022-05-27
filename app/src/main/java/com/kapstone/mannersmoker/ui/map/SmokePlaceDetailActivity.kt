package com.kapstone.mannersmoker.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivitySmokePlaceDetailBinding
import kotlinx.android.parcel.Parcelize

class SmokePlaceDetailActivity : BaseActivity2<ActivitySmokePlaceDetailBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_smoke_place_detail

    private lateinit var smokePlaceData: SmokePlaceData

    override fun initStartView() {
       /* Glide.with(binding.root)
            .load(argument.smokePlaceImage)
            .error(R.drawable.smoke)
            .into(binding.smokePlaceImage) */
        smokePlaceData = intent.getParcelableExtra("smokePlaceData")!!
        binding.smokePlaceName.text = smokePlaceData.smokePlaceName
        binding.smokePlaceAddress.text = smokePlaceData.smokePlaceAddress
        binding.distanceFromCurrentPlace.text = smokePlaceData.distance

        binding.goToFindDestination.setOnClickListener {
            showDialog(smokePlaceData.smokePlaceLatitude, smokePlaceData.smokePlaceLongtitude)
        }
        binding.goToFindDestinationCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDialog(lat : Double, lon : Double) {
        val dialog = MapDialog(this)
        dialog.setAcceptBtnClickListener {
            showVehicleChoice(lat, lon)
        }
        dialog.setDialog()
    }

    private fun showVehicleChoice(lat : Double, lon : Double) {
        val builder = AlertDialog.Builder(this)
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
                            "kakaomap://route?sp=${smokePlaceData.currentLatitude},${smokePlaceData.currentLongtitude}&ep=${destinationLat},${destinationLon}&by=CAR"
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
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
                            "kakaomap://route?sp=${smokePlaceData.currentLatitude},${smokePlaceData.currentLongtitude}&ep=${destinationLat},${destinationLon}&by=PUBLICTRANSIT"
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
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
                            "kakaomap://route?sp=${smokePlaceData.currentLatitude},${smokePlaceData.currentLongtitude}&ep=${destinationLat},${destinationLon}&by=FOOT"
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

@Parcelize
data class SmokePlaceData (
    val currentLatitude : Double,
    val currentLongtitude : Double,
    val smokePlaceLatitude : Double,
    val smokePlaceLongtitude : Double,
    val smokePlaceImage : String,
    val smokePlaceName : String,
    val smokePlaceAddress : String,
    val distance : String
) : Parcelable