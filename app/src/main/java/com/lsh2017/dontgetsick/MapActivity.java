package com.lsh2017.dontgetsick;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import android.widget.Toast;
import android.widget.Toolbar;


import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.util.ArrayList;

public class MapActivity extends NMapActivity {

    ListView listView;
    String clientID = "DUFMkSZTjABqyr1WHp2C";

    SearchView searchView;
    boolean isGPSEnabled = false;
    // String clientPass="JahUtF04Ca";
    NMapView nMapView;
    Location location = null;

    double latitude;
    double longitude;
    // ArrayList<HospitalItem> items=new ArrayList<>();
    NGeoPoint nGeoPoint;
    LocationManager locationManager;

    NMapViewerResourceProvider nMapViewerResourceProvider;

    NMapOverlayManager nOverlayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        searchView = (SearchView) findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MapActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //뒤로 가기 버튼
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listview_map);


//        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            Log.d("로캐이션매니저","로캐이션");
//            return;
//        }
//        Location location = null;
//
//        if (locationManager.isProviderEnabled("gps")) {//gps사용가능한가?
//            location = locationManager.getLastKnownLocation("gps");
//            Log.d("로캐이션매니저","가능");//안떠
//        }else if(locationManager.isProviderEnabled("network")){
//            location=locationManager.getLastKnownLocation("network");
//        }else{
//            location=locationManager.getLastKnownLocation("passive");
//        }
//        if(location==null){
//            Toast.makeText(this, "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
//        }else{
//            //위치정보 얻어오기..(위도경도)
//            double latitude=location.getLatitude(); //위도
//            double longitude=location.getLongitude(); //경도
//
//            Toast.makeText(this, latitude+" , "+longitude, Toast.LENGTH_SHORT).show();
//        }
        nMapView = (NMapView) findViewById(R.id.nmapview);
        nMapView.setClientId(clientID);//아이디값 설정.
        nMapView.setClickable(true);
        nMapView.setEnabled(true);
        nMapView.setFocusable(true);
        nMapView.setFocusableInTouchMode(true);
        nMapView.requestFocus();
        nMapView.setScalingFactor(4.0f, true);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},10);
        }

        if (locationManager.isProviderEnabled("gps")) {  //gps사용가능한가?
            Log.i("gps사용가능한가","ㅇㅇ");
            location = locationManager.getLastKnownLocation("gps");
        } else if (locationManager.isProviderEnabled("network")) {
            location = locationManager.getLastKnownLocation("network");
        } else {
            location = locationManager.getLastKnownLocation("passive");
        }

        locationManager.requestLocationUpdates("network", 3000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();


                Log.i("location",latitude+",  "+longitude+"");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


        if(!isGPSEnabled) {//위치 설정 다이얼로그 창
            showSettingsAlert();

        }
        //오버레이 표시=========================================================================
        nMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        nOverlayManager = new NMapOverlayManager(this, nMapView, nMapViewerResourceProvider);

        int markerId = NMapPOIflagType.PIN;

// set POI data
        NMapPOIdata poiData = new NMapPOIdata(2, nMapViewerResourceProvider);
        poiData.beginPOIdata(2);
        poiData.addPOIitem(latitude, longitude, "현재위치", markerId, 0);
        poiData.addPOIitem(latitude, longitude, "현위", markerId, 0);
        poiData.endPOIdata();

        Log.i("위도",latitude+"");
        Log.i("경도",longitude+"");
//// create POI data overlay
       NMapPOIdataOverlay poiDataOverlay = nOverlayManager.createPOIdataOverlay(poiData, R.drawable.ic_pin_01);
//        // show all POI data
       poiDataOverlay.showAllPOIdata(0);
//        // set event listener to the overlay

        //해당 이벤트 발생시 호출
        poiDataOverlay.setOnStateChangeListener(new NMapPOIdataOverlay.OnStateChangeListener() {
            @Override
            public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            }
            @Override
            public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            }
        });

        //NMapCalloutOverlay 클래스는 오버레이 아이템을 선택했을 때 표시되는 말풍선 오버레이의 기반 클래스입니다.
        // 지도 라이브러리에서 제공하는 말풍선 오버레이 대신에 사용자 정의 말풍선 오버레이로 변경하려면 상기 클래스를 상속받은 객체를 생성해야 합니다.
        // 그리고, 사용자 정의 말풍선 오버레이를 NMapOverlayManager에 전달하기 위하여 아래와 같이 이벤트 리스너를 등록합니다

        nOverlayManager.setOnCalloutOverlayListener(new NMapOverlayManager.OnCalloutOverlayListener() {
            @Override
            public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect rect) {
                return new NMapCalloutBasicOverlay(nMapOverlay, nMapOverlayItem, rect);
            }
        });

////오버레이 끝==============================================================================================


        NMapLocationManager mapLocationManager=new NMapLocationManager(this,true);
        mapLocationManager.setOnLocationChangeListener(new NMapLocationManager.OnLocationChangeListener() {

            @Override
            public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
                //현재위치 변경시 호출.mylocation에 변경된 좌표가 전달.현재위치탐색시 true.

                double lon=nGeoPoint.getLongitude();
                double lat=nGeoPoint.getLatitude();
                Log.i("lon",lon+"");
                Log.i("lat",lat+"");
                nMapLocationManager.enableMyLocation(true);

                return true;
            }

            @Override
            public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {
                //정해진 시간내에 현재 위치 탐색 실패 시 호출된다
                Toast.makeText(MapActivity.this, "현재 위치 탐색을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
                //현재 위치가 지도 상에 표시할 수 있는 범위를 벗어나는 경우에 호출된다.
                Toast.makeText(MapActivity.this, "지도 상에 표시할 수 없는 지역입니다.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS 사용 유무");
        alertDialog.setMessage("GPS가 설정되지 않았습니다.\n 설정창으로 가시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // Cancel 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }





}
