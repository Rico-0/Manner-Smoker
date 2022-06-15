package com.kapstone.mannersmoker.ui.map

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.BallonLayoutBinding
import com.kapstone.mannersmoker.databinding.FragmentMapBinding
import com.kapstone.mannersmoker.model.data.CurrentPlace.currentMapPoint
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.smoke.*
import com.kapstone.mannersmoker.model.data.smoke.SmokeAreaModels.oneSmokeAreaList
import com.kapstone.mannersmoker.model.data.user.UserId
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.*
import com.kapstone.mannersmoker.util.PreferencesManager.today_smoke_amount
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import kotlinx.android.synthetic.main.ballon_layout.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import net.daum.mf.map.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MapFragment : BaseFragment<FragmentMapBinding>(), MapView.CurrentLocationEventListener,
    MapView.MapViewEventListener {

    override val layoutResourceId: Int
        get() = R.layout.fragment_map

    // 카카오맵 지도 뷰
    private val mapView: MapView by lazy {
        MapView(requireActivity())
    }

    private val markers = arrayListOf<MapPOIItem>()

    private lateinit var ballonBinding: BallonLayoutBinding

    private lateinit var smokeDao: SmokeDao

    // 현재 위치 주소
    private var currentLocationAddress: String? = ""

    private var zoomLevel: Int = 3

    private var checkCurrentMapPoint = false

    private val markerListener = object : MapView.POIItemEventListener {
        override fun onPOIItemSelected(map_View: MapView?, poiItem: MapPOIItem?) {
            Log.d(TAG, "onPOIItemSelected 호출됨")
            if (poiItem?.markerType == MapPOIItem.MarkerType.BluePin) {
                val mapPoint = poiItem?.mapPoint!!
                poiItem.itemName = "흡연 구역"
                val distance = getSmokePlaceDistanceFromCurrent(mapPoint)
                val builder = AlertDialog.Builder(requireActivity())
                    .setTitle("흡연 구역 안내")
                    .setMessage("현재 위치에서 이 흡연 구역까지의 거리는 $distance 입니다. 이 구역으로 길을 안내할까요?")
                    .setPositiveButton("예", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            showVehicleChoice(mapPoint.mapPointGeoCoord.latitude, mapPoint.mapPointGeoCoord.longitude)
                        }
                    })
                    .setNegativeButton("아니오", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            Toast.makeText(requireContext(), "흡연 구역 안내를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                    })
                builder.show()
            } else if (poiItem?.markerType == MapPOIItem.MarkerType.RedPin) {
                poiItem.itemName = currentLocationAddress ?: "현재 위치 받아오기에 실패하였습니다."
            }
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {}
        override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            buttonType: MapPOIItem.CalloutBalloonButtonType?
        ) {
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
    }

    override fun initStartView() {
        binding.clKakaoMapView.addView(mapView)
        Log.d(TAG, "initStartView")
        mapView.setMapViewEventListener(this@MapFragment)
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff
        mapView.setPOIItemEventListener(markerListener)
        ballonBinding = DataBindingUtil.inflate(layoutInflater, R.layout.ballon_layout, null, false)

        startTracking()
        setButtonUiListener()
        setPlaceData()
    }

    /* areaCode
    용산구 - 1
    영등포구 - 2
    광진구 - 3
    중구 - 4
    중랑구 - 5
    동작구 - 6
    송파구 - 7
    서대문구 - 8
    동대문구 - 9
     */
    private fun searchSmokePlace(areaCode : Int) {
        // 흡연 구역 검색할 때마다 마커 초기화
        if (markers.size > 0) {
            mapView.removePOIItems(markers.toTypedArray())
            markers.clear()
        }
        smokeDao = RetrofitInstance.smokeDao
        smokeDao.getSmokeArea(areaCode).enqueue(object : Callback<SmokeAreaDataClass> {
            override fun onResponse(
                call: Call<SmokeAreaDataClass>,
                response: Response<SmokeAreaDataClass>
            ) {
                val result: SmokeAreaDataClass? = response.body()
                val places = result?.placeData

                if (places != null) {
                    for (i in places.indices) {
                        val areaCode : Int = places?.get(i)?.areaCode!!
                        val latitude: Double = places?.get(i)?.latitude!!
                        val longtitude: Double = places?.get(i)?.longtitude!!
                        // 흡연 구역 데이터 추가
                        oneSmokeAreaList.add(SmokeAreaModel(areaCode, latitude, longtitude))
                        // 흡연 구역 데이터를 가지고 마커를 추가하고, 추가한 마커를 리턴받음
                        val marker = setMarkerToLocation(oneSmokeAreaList.get(i))
                        // 위에서 받은 마커를 마커 배열에 추가 (초기화시키기 위함)
                        markers.add(marker)
                    }
                    // 추가한 데이터 중 첫 번째 데이터 (아무 순서나 상관 없음) 로 맵 중앙 위치 설정
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(oneSmokeAreaList.get(0)?.latitude!!, oneSmokeAreaList?.get(0)?.longtitude!!), true)
                    mapView.setZoomLevel(4, true)
                    // 흡연 구역 데이터 초기화
                    oneSmokeAreaList.clear()
                }
            }
            override fun onFailure(call: Call<SmokeAreaDataClass>, t: Throwable) {
                Log.d("MainViewModel", "흡연 구역 데이터 받아오기 실패 : $t")
            }
        })
    }

    private fun searchSmokePlaceByArea(keyWord : String) {
        when (keyWord) {
            "용산구" -> {
                searchSmokePlace(1)
            }
            "영등포구" -> {
                searchSmokePlace(2)
            }
            "광진구" -> {
                searchSmokePlace(3)
            }
            "중구" -> {
                searchSmokePlace(4)
            }
            "중랑구" -> {
                searchSmokePlace(5)
            }
            "동작구" -> {
                searchSmokePlace(6)
            }
            "송파구" -> {
                searchSmokePlace(7)
            }
            "서대문구" -> {
                searchSmokePlace(8)
            }
            "동대문구" -> {
                searchSmokePlace(9)
            }
            else -> {
                Toast.makeText(requireContext(), "검색된 장소가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showVehicleChoice(lat : Double, lon : Double) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
                            "kakaomap://route?sp=${currentMapPoint?.mapPointGeoCoord?.latitude},${currentMapPoint?.mapPointGeoCoord?.longitude}&ep=${destinationLat},${destinationLon}&by=CAR"
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
                            "kakaomap://route?sp=${currentMapPoint?.mapPointGeoCoord?.latitude},${currentMapPoint?.mapPointGeoCoord?.longitude}&ep=${destinationLat},${destinationLon}&by=PUBLICTRANSIT"
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
                            "kakaomap://route?sp=${currentMapPoint?.mapPointGeoCoord?.latitude},${currentMapPoint?.mapPointGeoCoord?.longitude}&ep=${destinationLat},${destinationLon}&by=FOOT"
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

    private fun setButtonUiListener() {
        binding.mapBiggerButton.setOnClickListener {
            zoomLevel -= 1 // 줌 레벨이 낮을수록 확대인 듯
            mapView.setZoomLevel(zoomLevel, true)
        }
        binding.mapSmallerButton.setOnClickListener {
            zoomLevel += 1 // 줌 레벨이 높을수록 축소?
            mapView.setZoomLevel(zoomLevel, true)
        }
        binding.getCurrentLocation.setOnClickListener {
            currentMapPoint?.let {
                mapView.setMapCenterPoint(currentMapPoint, true)
            }
        }
        binding.addSmokeCount.setOnClickListener {
            val dialog1 = AlertDialog.Builder(requireActivity())
                .setTitle("흡연량 추가 안내")
                .setMessage("현재 흡연 중이신가요?")
                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        smokeDao = RetrofitInstance.smokeDao
                        smokeDao.sendSmokeData(UserId(user_id_from_server)).enqueue(object : Callback<SendSmokeDataClass> {
                            override fun onResponse(call: Call<SendSmokeDataClass>, response: Response<SendSmokeDataClass>) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    result?.SmokeData?.let {
                                    today_smoke_amount += 1
                                    PreferencesManager.used_money += 225 // 1개비 당 약 225원
                                    val date = Date()
                                    PreferencesManager.time_last_smoke = DateUtil.dateToString(date)
                                    Toast.makeText(requireContext(), "서버로 데이터 전송됨", Toast.LENGTH_SHORT).show()
                                    var dialog2 = AlertDialog.Builder(requireActivity())
                                        .setTitle("오늘의 흡연량이 한 개비 증가되었습니다.")
                                        .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                                            override fun onClick(dialog : DialogInterface?, p1: Int) {
                                                dialog?.dismiss()
                                            }
                                        })
                                    dialog2.show()
                                }
                              } else {
                                    Log.d(TAG, "흡연량 추가 상태 코드 : ${response.code()}")
                                }
                            }
                            override fun onFailure(call: Call<SendSmokeDataClass>, t: Throwable) {
                                Toast.makeText(requireContext(), "서버로 데이터 전송 실패 : $t", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                })
                .setNegativeButton("아니오", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, p1: Int) {
                       dialog?.dismiss()
                    }
                })
            dialog1.show()
        }
        binding.startSearch.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val keyWord = binding.locationSearchEditText.text.toString()
            searchSmokePlaceByArea(keyWord)
            imm.hideSoftInputFromWindow((binding.locationSearchEditText).windowToken, 0) // 흡연 구역 검색 후 키보드 숨기기
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.clKakaoMapView.removeAllViews()
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        checkCurrentMapPoint = false
    }

    // 현재 위치 구하기
    private fun getCurrentMapPoint(): MapPoint {
        PermissionUtil.checkForgroundLocationPermission(requireActivity())
        val lm: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // 위도, 경도
        val uLatitude = userNowLocation?.latitude
        val uLongtitude = userNowLocation?.longitude

        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongtitude!!)
        Log.d(
            TAG,
            "getCurrentLocation - 위도 : ${uNowPosition.mapPointGeoCoord.latitude} 경도 : ${uNowPosition.mapPointGeoCoord.longitude}"
        )
        return uNowPosition
    }

    private fun convertPlaceToMapPoint(latitude: Double, longtitude: Double): MapPoint {
        val uLatitude = latitude
        val uLongtitude = longtitude
        val uMapPoint = MapPoint.mapPointWithGeoCoord(uLatitude, uLongtitude)
        return uMapPoint
    }

    // 위치를 주소로 변환
    private fun findAddressByMapPoint(place: SmokeAreaModel) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(place.latitude, place.longtitude)
        val reverseGeoCoder = MapReverseGeoCoder(
            resources.getString(R.string.kakao_app_key),
            mapPoint,
            ReverseGeoListener(),
            requireActivity()
        )
        reverseGeoCoder.startFindingAddress()
    }

    // 해당 위치에 마커 찍기
    private fun setMarkerToLocation(place: SmokeAreaModel) : MapPOIItem {
        val marker = MapPOIItem()
        marker.apply { // 주의 : itemName 마커 생성 시 존재하지 않을 경우 지도에 마커 표시 안 됨
            itemName = ""
            mapPoint = convertPlaceToMapPoint(place.latitude, place.longtitude)
            markerType = setMarkerColor(place)
        }
        mapView.addPOIItem(marker)
        return marker
    }

    // 현재 위치는 빨간색 마커, 흡연 구역은 파란색 마커
    private fun setMarkerColor(place: SmokeAreaModel): MapPOIItem.MarkerType {
        val currentPlace = SmokeAreaModel(
            place.areaCode,
            currentMapPoint?.mapPointGeoCoord?.latitude!!,
            currentMapPoint?.mapPointGeoCoord?.longitude!!,
        )
        if (place.latitude == currentPlace.latitude && place.longtitude == currentPlace.longtitude) {
            return MapPOIItem.MarkerType.RedPin
        } else
            return MapPOIItem.MarkerType.BluePin
    }

    inner class ReverseGeoListener : MapReverseGeoCoder.ReverseGeoCodingResultListener {
        // 호출에 성공한 경우
        override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, address: String?) {
            Log.d(TAG, "주소 : $address")
            currentLocationAddress = address
        }

        override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
            // 호출에 실패한 경우 (현재 위치)
            currentLocationAddress = "검색된 주소 없음"
            Toast.makeText(
                requireActivity(),
                "현재 위치를 주소로 변환하는 데 실패하였습니다. 인터넷 연결을 확인해 주세요.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // 현재 사용자 위치 추척 후 마커 찍기
    private fun startTracking() {

        mapView.setZoomLevel(zoomLevel, true)

        currentMapPoint = getCurrentMapPoint()

        // 현재 위치로 지도 중심 이동
        mapView.setMapCenterPoint(currentMapPoint, true)

        // 현재 위치 주소로 변환 (currentLocationAddress 값 변함)

        findAddressByMapPoint(
            SmokeAreaModel(
                -1,
                currentMapPoint!!.mapPointGeoCoord.latitude,
                currentMapPoint!!.mapPointGeoCoord.longitude,
            )
        )

        // 현재 위치에 마커 찍기
        setMarkerToLocation(
            SmokeAreaModel(
                -1,
                currentMapPoint!!.mapPointGeoCoord.latitude,
                currentMapPoint!!.mapPointGeoCoord.longitude,
            )
        )
    }

    private fun setPlaceData() {
        // 저장된 흡연 구역들 데이터 설정 후 마커 찍기
        SmokeAreaModels.allSmokeAreaList.forEach {
          setMarkerToLocation(it)
          }
    }

    private fun getSmokePlaceDistanceFromCurrent(mapPoint: MapPoint?) : String {
        var distance : Double = LocationDistance.distance(currentMapPoint!!.mapPointGeoCoord.latitude, currentMapPoint!!.mapPointGeoCoord.longitude, mapPoint?.mapPointGeoCoord?.latitude!!, mapPoint?.mapPointGeoCoord?.longitude!!, "meter")
        if (distance >= 1000.0) {
            distance = LocationDistance.distance(currentMapPoint!!.mapPointGeoCoord.latitude, currentMapPoint!!.mapPointGeoCoord.longitude, mapPoint?.mapPointGeoCoord?.latitude!!, mapPoint?.mapPointGeoCoord?.longitude!!, "kilometer")
            return "약 " + distance.toInt().toString() + "km"
        } else return "약 " + distance.toInt().toString() + "m"
    }

    // 현재 위치 업데이트
    override fun onCurrentLocationUpdate(
        mmapView: MapView?,
        mapPoint: MapPoint?,
        accuracyInMeters: Float
    ) {
        /*  var mapPointGeo : MapPoint.GeoCoordinate? = mapPoint?.mapPointGeoCoord
          Log.d("@@@", String.format("MapView onCurrentLocationUpdate (%f, %f) accuracy (%f)", mapPointGeo?.latitude, mapPointGeo?.longitude, accuracyInMeters))
          mapPointGeo?.let {
              currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude)
          }
          // 이 좌표로 지도 중심 이동
          mapView.setMapCenterPoint(currentMapPoint, true)
          currentLat = mapPointGeo?.latitude
          currentLng = mapPointGeo?.longitude
          Log.d("@@@", "현재 위치 : $currentLat . $currentLng")
          // 트래킹 모드가 아닌 단순 현재위치 업데이트일 경우 한번만 위치 업데이트하고 트래킹 중단
          if (!isTrackingMode) {
              mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
          } */
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
       // checkNetworkConnection()
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {

    }

    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        // TODO : 장소 등록 기능
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }
}