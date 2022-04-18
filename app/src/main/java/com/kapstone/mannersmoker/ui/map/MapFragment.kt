package com.kapstone.mannersmoker.ui.map

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import com.kapstone.mannersmoker.model.data.Places.places
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.BallonLayoutBinding
import com.kapstone.mannersmoker.databinding.FragmentMapBinding
import com.kapstone.mannersmoker.model.data.Place
import com.kapstone.mannersmoker.util.NetworkConnection
import com.kapstone.mannersmoker.util.PermissionUtil
import kotlinx.android.synthetic.main.ballon_layout.view.*
import net.daum.mf.map.api.*
import android.os.Handler
import android.view.Window
import android.widget.TextView
import java.lang.Exception

// TODO : 네트워크 상태 체크해서 연결 끊겼을 경우 기본 위치 지정?
// TODO : 다시 네트워크에 연결되었을 때 현재 위치 받아오기 (안되는중...)
// TODO : 지도 뷰 위에 검색 창 안 띄워지는 문제 해결
class MapFragment : BaseFragment<FragmentMapBinding>(), MapView.CurrentLocationEventListener,
    MapView.MapViewEventListener {

    override val layoutResourceId: Int
        get() = R.layout.fragment_map

    // 카카오맵 지도 뷰
    private val mapView: MapView by lazy {
        MapView(requireActivity())
    }

    private lateinit var ballonBinding: BallonLayoutBinding

    // 위치 마커 배열
    private var markers: ArrayList<MapPOIItem> = ArrayList()

    // 현재 위치
    private var currentMapPoint: MapPoint? = null

    // 현재 위치 주소
    private var currentLocationAddress: String? = ""

    private var zoomLevel: Int = 3

    private var checkCurrentMapPoint = false

    private val markerListener = object : MapView.POIItemEventListener {
        override fun onPOIItemSelected(map_View: MapView?, poiItem: MapPOIItem?) {
            Log.d(TAG, "onPOIItemSelected 호출됨")
            if (poiItem?.markerType == MapPOIItem.MarkerType.BluePin) {
                val mapPoint = poiItem?.mapPoint
                showDialog(mapPoint)
            }
        }
        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) { }
        override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            buttonType: MapPOIItem.CalloutBalloonButtonType?
        ) { }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) { }
    }

    override fun initStartView() {
        binding.clKakaoMapView.addView(mapView)
        Log.d(TAG, "initStartView")
        mapView.setMapViewEventListener(this@MapFragment)
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff
        mapView.setPOIItemEventListener(markerListener)
        ballonBinding = DataBindingUtil.inflate(layoutInflater, R.layout.ballon_layout, null, false)

        checkNetworkConnection()
        startTracking()
        setOnZoomButtonListener()
        setPlaceData()


    }

    private fun checkNetworkConnection() {
        // 네트워크 연결 여부 체크
        val connection = NetworkConnection(requireActivity().applicationContext)
        connection.observe(this, Observer { isConnected ->
            if (!isConnected) {
                Toast.makeText(
                    requireActivity(),
                    "인터넷에 연결되어 있지 않아 현재 위치를 받아올 수 없습니다.\n 와이파이나 LTE에 연결해 주세요.",
                    Toast.LENGTH_LONG
                ).show()
                return@Observer
            }
        })
    }

    private fun setOnZoomButtonListener() {
        binding.mapBiggerButton.setOnClickListener {
            zoomLevel -= 1 // 줌 레벨이 낮을수록 확대인 듯
            mapView.setZoomLevel(zoomLevel, true)
        }
        binding.mapSmallerButton.setOnClickListener {
            zoomLevel += 1 // 줌 레벨이 높을수록 축소?
            mapView.setZoomLevel(zoomLevel, true)
        }
        binding.getCurrentLocation.setOnClickListener {
            val connection = NetworkConnection(requireActivity())
            connection.observe(this, Observer { isConnected ->
                if (!isConnected) {

                } else {
                    // 현재 위치로 지도 중심 이동
                    mapView.setMapCenterPoint(currentMapPoint, true)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        checkNetworkConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.clKakaoMapView.removeAllViews()
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        checkCurrentMapPoint = false
    }

    // 현재 위치 구하기
    private fun getCurrentMapPoint(): MapPoint {
        val lm: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // 권한 체크해야 위 함수 작동함
        if (!PermissionUtil.checkLocationServicesStatus(requireActivity())) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

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
    private fun findAddressByMapPoint(place: Place) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(place.latitude, place.longitude)
        val reverseGeoCoder = MapReverseGeoCoder(
            resources.getString(R.string.kakao_app_key),
            mapPoint,
            ReverseGeoListener(),
            requireActivity()
        )
        reverseGeoCoder.startFindingAddress()
    }

    // 해당 위치에 마커 찍기
    private fun setMarkerToLocation(place: Place, address: String?) {
        val marker = MapPOIItem()
        marker.apply {
            itemName = "Marker"
            mapPoint = convertPlaceToMapPoint(place.latitude, place.longitude)
            markerType = setMarkerColor(place)
        }
        markers.add(marker)
        mapView.addPOIItem(marker)
    }

    // 현재 위치는 빨간색 마커, 흡연 구역은 파란색 마커
    private fun setMarkerColor(place: Place): MapPOIItem.MarkerType {
        val currentPlace = Place(
            getCurrentMapPoint().mapPointGeoCoord.latitude,
            getCurrentMapPoint().mapPointGeoCoord.longitude,
            currentLocationAddress
        )
        if (place.latitude == currentPlace.latitude && place.longitude == currentPlace.longitude) {
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
            Place(
                currentMapPoint!!.mapPointGeoCoord.latitude,
                currentMapPoint!!.mapPointGeoCoord.longitude,
                ""
            )
        )

        // 현재 위치에 마커 찍기
        setMarkerToLocation(
            Place(
                currentMapPoint!!.mapPointGeoCoord.latitude,
                currentMapPoint!!.mapPointGeoCoord.longitude,
                currentLocationAddress
            ),
            currentLocationAddress
        )
    }

    private fun setPlaceData() {
        // 저장된 흡연 구역들 데이터 설정 후 마커 찍기
        // TODO : 서버에서 데이터 받아오는 경우 MainActivity의 MainViewModel로 받아오기
        places.forEach {
            // findAddressByMapPoint(it)
            setMarkerToLocation(it, it.address)
        }
    }


    private fun showDialog(mapPoint: MapPoint?) {
        val dialog = MapDialog(requireContext())
        dialog.setAcceptBtnClickListener {
            showVehicleChoice(mapPoint)
        }
        dialog.setDialog()
    }

    private fun showVehicleChoice(mapPoint: MapPoint?) {
        val builder = AlertDialog.Builder(requireContext())
        val itemList = arrayOf("자동차", "대중교통", "도보")
        builder.setTitle("이동 수단을 선택하세요.")
        builder.setItems(itemList) { dialog, which ->
            when (which) {
                0 -> searchDestination("자동차", mapPoint)
                1 -> searchDestination("대중교통", mapPoint)
                2 -> searchDestination("도보", mapPoint)
            }
        }
        builder.show()
    }

    private fun searchDestination(vehicle: String, destinationMapPoint: MapPoint?) {
        when (vehicle) {
            "자동차" -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "kakaomap://route?sp=${currentMapPoint?.mapPointGeoCoord?.latitude},${currentMapPoint?.mapPointGeoCoord?.longitude}&ep=${destinationMapPoint?.mapPointGeoCoord?.latitude},${destinationMapPoint?.mapPointGeoCoord?.longitude}&by=CAR"
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
                            "kakaomap://route?sp=${currentMapPoint?.mapPointGeoCoord?.latitude},${currentMapPoint?.mapPointGeoCoord?.longitude}&ep=${destinationMapPoint?.mapPointGeoCoord?.latitude},${destinationMapPoint?.mapPointGeoCoord?.longitude}&by=PUBLICTRANSIT"
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
                            "kakaomap://route?sp=${currentMapPoint?.mapPointGeoCoord?.latitude},${currentMapPoint?.mapPointGeoCoord?.longitude}&ep=${destinationMapPoint?.mapPointGeoCoord?.latitude},${destinationMapPoint?.mapPointGeoCoord?.longitude}&by=FOOT"
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
            checkNetworkConnection()
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

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            // 권한 요청을 했고 요청한 퍼미션 개수만큼 수신되었다면
            if (requestCode == PermissionUtil.PERMISSIONS_REQUEST_CODE && grantResults.size == PermissionUtil.REQUIRED_PREMISSIONS.size) {
                var check_result: Boolean = true

                // 모든 퍼미션 허용했는지 체크
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        check_result = false
                        return@forEach
                    }
                }
                if (check_result) {
                    Log.d("@@@", "start")
                    // 위치 값 가져오기 가능
                } else {
                    // 거부한 퍼미션이 있는 경우 앱 사용이 불가능
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            PermissionUtil.REQUIRED_PREMISSIONS[0]
                        ) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            PermissionUtil.REQUIRED_PREMISSIONS[1]
                        )
                    ) {
                        Toast.makeText(
                            requireActivity(),
                            "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "권한이 거부되었습니다. 설정(앱 정보) 에서 권한을 허용해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        private fun checkRunTimePermission() {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                // 위치 값 가져오기 가능, 권한 허용됨
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        PermissionUtil.REQUIRED_PREMISSIONS[0]
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        PermissionUtil.REQUIRED_PREMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        requireActivity(),
                        "앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        PermissionUtil.REQUIRED_PREMISSIONS,
                        PermissionUtil.PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    // 사용자가 퍼미션 거부를 한 적이 없는 경우 퍼미션 요청 바로 하기
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        PermissionUtil.REQUIRED_PREMISSIONS,
                        PermissionUtil.PERMISSIONS_REQUEST_CODE
                    )
                }
            }
        }

        // GPS 활성화 다이얼로그 띄우기
        private fun showDialogForLocationServiceSetting() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("위치 서비스 비활성화됨")
            builder.setMessage("앱을 사용하려면 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?")
            builder.setCancelable(true)
            builder.setPositiveButton("설정", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val callGPSSettingIntent =
                        Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(
                        callGPSSettingIntent,
                        PermissionUtil.GPS_ENABLE_REQUEST_CODE
                    )
                }
            })
            builder.setNegativeButton("취소", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.cancel()
                }
            })
            builder.create().show()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            when (requestCode) {
                PermissionUtil.GPS_ENABLE_REQUEST_CODE -> {
                    if (PermissionUtil.checkLocationServicesStatus(requireActivity())) {
                        Log.d("@@@", "onActivityResult : GPS 활성화됨")
                        checkRunTimePermission()
                        return
                    }
                }
            }
        }
    }


