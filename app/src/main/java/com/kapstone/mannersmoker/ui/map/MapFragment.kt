package com.kapstone.mannersmoker.ui.map

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.BallonLayoutBinding
import com.kapstone.mannersmoker.databinding.FragmentMapBinding
import com.kapstone.mannersmoker.model.data.Place
import com.kapstone.mannersmoker.model.data.Places.places
import com.kapstone.mannersmoker.ui.map.SmokePlaceDetailFragment
import com.kapstone.mannersmoker.util.LocationDistance
import com.kapstone.mannersmoker.util.NetworkConnection
import com.kapstone.mannersmoker.util.PermissionUtil
import kotlinx.android.synthetic.main.ballon_layout.view.*
import net.daum.mf.map.api.*

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
                val mapPoint = poiItem?.mapPoint!!
                val placeData = poiItem.itemName // 여기에 정보 들어감 (순서대로 이미지 주소, 장소 이름, 장소 주소)
                val placeDataResult = placeData.split(",") // 따옴표로 결과 분리
                click.run {
                    SmokePlaceDetailFragment.start(
                        fragment = this@MapFragment,
                        argument = SmokePlaceDetailFragment.Argument(
                            currentLatitude = currentMapPoint?.mapPointGeoCoord?.latitude!!,
                            currentLongtitude = currentMapPoint?.mapPointGeoCoord?.longitude!!,
                            smokePlaceLatitude = mapPoint.mapPointGeoCoord.latitude,
                            smokePlaceLongtitude = mapPoint.mapPointGeoCoord.longitude,
                            smokePlaceImage = placeDataResult[0],
                            smokePlaceName = placeDataResult[1],
                            smokePlaceAddress = placeDataResult[2],
                            distance = getSmokePlaceDistanceFromCurrent(mapPoint)
                        )
                    )
                }
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

        checkNetworkConnection()
        startTracking()
        setButtonUiListener()
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
            val connection = NetworkConnection(requireActivity())
            connection.observe(this, Observer { isConnected ->
                if (!isConnected) {

                } else {
                    // 현재 위치로 지도 중심 이동
                    mapView.setMapCenterPoint(currentMapPoint, true)
                }
            })
        }
        binding.addNewPlace.setOnClickListener {
            this@MapFragment.findNavController().navigate(R.id.action_go_to_add_new_smoke_place)
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
    // TODO : 처음 앱 실행 시 권한 문제 Error 해결
    private fun getCurrentMapPoint(): MapPoint {
        PermissionUtil.checkForgroundLocationPermission(requireActivity())
        val lm: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
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
    private fun setMarkerToLocation(place: Place) {
        val marker = MapPOIItem()
        // val placeData : String = place.address + " " + place.name + ... (데이터 전부 공백 단위로 묶어서 문자열로 붙인 다음 Name으로 설정)
        marker.apply {
            // itemName : 흡연 구역 이미지 파일명, 흡연 구역 이름, 흡연 구역 주소
            itemName = ",경기대학교 복지관 앞,경기도 수원시 권선구 13-5" // placeData
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
            )
        )
    }

    private fun setPlaceData() {
        // 저장된 흡연 구역들 데이터 설정 후 마커 찍기
        // TODO : 서버에서 데이터 받아오는 경우 MainActivity의 MainViewModel로 받아오기
        places.forEach {
            // findAddressByMapPoint(it)
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
}