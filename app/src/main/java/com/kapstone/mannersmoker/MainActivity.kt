package com.kapstone.mannersmoker

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.kapstone.mannersmoker.util.NetworkConnection
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.ballon_layout.view.*
import net.daum.mf.map.api.*

// TODO : 네트워크 상태 체크해서 연결 끊겼을 경우 기본 위치 지정?
// TODO : 다시 네트워크에 연결되었을 때 현재 위치 받아오기 (안되는중...)
// TODO : 지도 뷰 위에 검색 창 안 띄워지는 문제 해결
class MainActivity : AppCompatActivity(), MapView.CurrentLocationEventListener,
    MapView.MapViewEventListener {

    // 카카오맵 지도 뷰
    private val mapView: MapView by lazy {
        MapView(this)
    }

    // 현재 위치 마커
    private val marker: MapPOIItem by lazy {
        MapPOIItem()
    }

    // 현재 위치
    private var currentMapPoint: MapPoint? = null
    private var isTrackingMode: Boolean = true
    private var zoomLevel: Int = 3
    private var currentLocationAddress : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        clKakaoMapView.addView(mapView)

        mapView.apply {
            setMapViewEventListener(this@MainActivity)
            setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            setCalloutBalloonAdapter(CustomBallonAdapter(layoutInflater))
        }

        checkNetworkConnection()
        startTracking()
        setOnZoomButtonListener()

    }

    private fun checkNetworkConnection() {
        var connected = false
        // 네트워크 연결 여부 체크
        val connection = NetworkConnection(applicationContext)
        connection.observe(this, Observer { isConnected ->
            if (!isConnected && currentMapPoint == null) {
                Toast.makeText(this@MainActivity, "인터넷에 연결되어 있지 않아 현재 위치를 받아올 수 없습니다.\n 와이파이나 LTE에 연결해 주세요.", Toast.LENGTH_LONG).show()
                return@Observer
            } else if (connected == false && isConnected){
                Log.d("MainActivityN", "이부분 실행됨")
                mapView.refreshMapTiles() // 지도 화면에 나타나는 지도 타일들을 서버에서 불러와서 갱신
                startTracking()
            } else if (isConnected) {
                connected = true
            }
        })
    }

    private fun setOnZoomButtonListener() {
        map_bigger_button.setOnClickListener {
            zoomLevel -= 1 // 줌 레벨이 낮을수록 확대인 듯
            mapView.setZoomLevel(zoomLevel, true)
        }
        map_smaller_button.setOnClickListener {
            zoomLevel += 1 // 줌 레벨이 높을수록 축소?
            mapView.setZoomLevel(zoomLevel, true)
        }
        get_current_location.setOnClickListener {
            val connection = NetworkConnection(applicationContext)
            connection.observe(this, Observer { isConnected ->
                if (!isConnected) {
                    startTracking()
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
        clKakaoMapView.removeAllViews()
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun getCurrentMapPoint() : MapPoint {
        val lm: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        // 권한 체크해야 위 함수 작동함
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        // 위도, 경도
        val uLatitude = userNowLocation?.latitude
        val uLongtitude = userNowLocation?.longitude
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongtitude!!)
        return uNowPosition
    }

    // 사용자 현재 위치를 주소로 변환
    private fun findAddressByMapPoint() {
        val reverseGeoCoder = MapReverseGeoCoder(OPEN_API_KEY, getCurrentMapPoint(), ReverseGeoListener(), this@MainActivity)
        reverseGeoCoder.startFindingAddress()
    }

    inner class ReverseGeoListener : MapReverseGeoCoder.ReverseGeoCodingResultListener {
        // 호출에 성공한 경우
        override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
            p0.toString()
            currentLocationAddress = p1
        }

        override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
            // 호출에 실패한 경우
            currentLocationAddress = "검색된 주소 없음"
            Toast.makeText(this@MainActivity, "현재 위치를 주소로 변환하는 데 실패하였습니다. 인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 현재 사용자 위치 추척 후 마커 찍기
    private fun startTracking() {
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        mapView.setZoomLevel(zoomLevel, true)
        val lm: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        // 위도, 경도
        val uLatitude = userNowLocation?.latitude
        val uLongtitude = userNowLocation?.longitude
        // mapPoint 객체로 다시 객체화해서 마커 위치 설정 시 사용
        currentMapPoint = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongtitude!!)

        // 현재 위치로 지도 중심 이동
        mapView.setMapCenterPoint(currentMapPoint, true)

        // 현재 위치 주소로 변환 (currentLocationAddress 값 변함)
        findAddressByMapPoint()

        // 현재 위치에 마커 찍기
        marker.apply {
            itemName = currentLocationAddress
            mapPoint = currentMapPoint
            markerType = MapPOIItem.MarkerType.RedPin
        }
        mapView.addPOIItem(marker)
    }

    // 커스텀 말풍선
    inner class CustomBallonAdapter(inflater: LayoutInflater) : CalloutBalloonAdapter {

        val mCalloutBalloon : View = inflater.inflate(R.layout.ballon_layout, null)

        override fun getCalloutBalloon(p0: MapPOIItem?): View {
          mCalloutBalloon.ball_tv_address.text = currentLocationAddress
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(p0: MapPOIItem?): View {
            return mCalloutBalloon
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

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    // TODO : 권한 관련 애들 클래스 밖으로 빼기
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 권한 요청을 했고 요청한 퍼미션 개수만큼 수신되었다면
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PREMISSIONS.size) {
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
                        this,
                        REQUIRED_PREMISSIONS[0]
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PREMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "권한이 거부되었습니다. 설정(앱 정보) 에서 권한을 허용해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkRunTimePermission() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 위치 값 가져오기 가능, 권한 허용됨
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    REQUIRED_PREMISSIONS[0]
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    REQUIRED_PREMISSIONS[1]
                )
            ) {
                Toast.makeText(this@MainActivity, "앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    REQUIRED_PREMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 사용자가 퍼미션 거부를 한 적이 없는 경우 퍼미션 요청 바로 하기
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    REQUIRED_PREMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    // GPS 활성화 다이얼로그 띄우기
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화됨")
        builder.setMessage("앱을 사용하려면 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val callGPSSettingIntent =
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
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
            GPS_ENABLE_REQUEST_CODE -> {
                if (checkLocationServicesStatus()) {
                    Log.d("@@@", "onActivityResult : GPS 활성화됨")
                    checkRunTimePermission()
                    return
                }
            }
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    companion object {
        private const val GPS_ENABLE_REQUEST_CODE = 2001
        private const val PERMISSIONS_REQUEST_CODE = 100
        private val REQUIRED_PREMISSIONS = arrayOf<String>(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val OPEN_API_KEY = "d1134cd947745b49f0d8c93f0dd4fe81"
    }

    // 앱 해시 키 얻는 코드
// keytool -exportcert -alias androiddebugkey -keystore C:\Users\LG06\.android\debug.keystore -storepass android -keypass android | D:\openssl-1.0.2j-fips-x86_64\OpenSSL\bin\openssl sha1 -binary | D:\openssl-1.0.2j-fips-x86_64\OpenSSL\bin\openssl base64

}


