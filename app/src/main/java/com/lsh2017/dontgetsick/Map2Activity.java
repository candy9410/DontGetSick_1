package com.lsh2017.dontgetsick;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Map2Activity extends AppCompatActivity {

    SearchView searchView;

    LatLng mylocation;
    boolean isGPSEnabled = false;
    double latitude;
    double longitude;
    GoogleMap map;
    Fragment googlemap;
    Location location;
    LocationManager locationManager;
    LoadHospitalTask task;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    HospitalAdapter adapter;
    ArrayList<HospitalInfo> HospitalInfos =new ArrayList<>();
    String nearby="";
    String Key="AIzaSyDZPxw5xmTMzmqKHZrQS9L36de9nUyvPLA";//구글 장소 키

    //장소 세부요청
    String detailnear="https://maps.googleapis.com/maps/api/place/details/json?parameters"; /*output에 json이나 xml*/
    //주변 병원

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        db=openOrCreateDatabase("hos.db",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+"hospital"+"("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"name TEXT, "
                +"addr TEXT,"
                +"icon TEXT,"
                +"rating DOUBLE,"
                +"open INTEGER"
                +")");

        searchView = (SearchView) findViewById(R.id.searchview);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipefresh);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = null;

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!isGPSEnabled) {
            showSettingsAlert();
        }

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);

        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter=new HospitalAdapter(this,HospitalInfos,db);
        recyclerView.setAdapter(adapter);


        //구글 맵 내 위치 보여주기
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                LatLng mylocation = new LatLng(latitude, longitude);

                if (ActivityCompat.checkSelfPermission(Map2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    return;
                }

                map.setMyLocationEnabled(true);

                Log.i("latitudede",latitude+"");
                Log.i("longitudede",longitude+"");
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,18));
                MarkerOptions marker=new MarkerOptions();
                marker.title("내 위치");
                marker.position(mylocation);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_01));

                map.addMarker(marker);

                UiSettings settings=map.getUiSettings();
                settings.setZoomControlsEnabled(true);


            }
        });



    }

    //위치 정보 듣는 리스너
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("위치", latitude + ",,," + longitude);

            mylocation = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,13));


            MarkerOptions marker=new MarkerOptions();

//            marker.title("내 위치");
//            marker.position(mylocation);
//            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_01));
//
//            map.addMarker(marker);
            // Log.i("위치", latitude + ",,," + longitude);

            nearby="https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location="+latitude+","+longitude+"&radius=1000&type=hospital&key="+Key+"&language=ko";

//            if(thread==null){
//                thread=new hosThread();
//                thread.start();
//            }
            if(task==null){
                task=new LoadHospitalTask();
                task.execute();
            }


            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    task=new LoadHospitalTask();
                    task.execute();

                }
            });//swipe

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
    };

    class LoadHospitalTask extends AsyncTask<Void,HospitalInfo,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
            HospitalInfos.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                URL url=new URL(nearby);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setUseCaches(false);

                InputStream is=conn.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader reader=new BufferedReader(isr);

                StringBuffer buffer=new StringBuffer();
                String line=reader.readLine();
                while (line!=null){
                    buffer.append(line);
                    line=reader.readLine();
                }

                Log.i("버퍼",buffer.toString());
                JSONObject jsonObject=new JSONObject(buffer.toString());

                JSONArray resultArray=jsonObject.getJSONArray("results");
                for(int i=0;i<resultArray.length();i++){
                    JSONObject object= resultArray.getJSONObject(i);
                    double lat=object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double lng=object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    Log.i("json",lat+"    "+lng);
                    String iconimgUrl=object.getString("icon");
                    Log.i("json",iconimgUrl);
                    String name=object.getString("name");
                    Log.i("json",name);
                    boolean open;
                    if(object.has("opening_hours")) {
                        open = object.getJSONObject("opening_hours").getBoolean("open_now");
                    }else {
                        open=false;
                    }
                    double rating;
                    if(object.has("rating")){
                        rating=object.getDouble("rating");
                    }else{
                        rating=0.0;
                    }

                    Log.i("json",open+"  "+rating);
                    String addr=object.getString("vicinity");
                    Log.i("json",addr);

                    HospitalInfo info=new HospitalInfo(name,addr,lat,lng,iconimgUrl,open,rating);
                    SharedPreferences sharedPreferences=getSharedPreferences("hosfavorite",MODE_PRIVATE);

                    boolean star=sharedPreferences.getBoolean(addr,false);
                    info.star=star;
                    HospitalInfos.add(info);

                   publishProgress(info);
                }

                Log.i("json",HospitalInfos.size()+"");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(HospitalInfo... infos) {
            super.onProgressUpdate(infos);

            MarkerOptions marker=new MarkerOptions();
            marker.title(infos[0].name);

            LatLng location=new LatLng(infos[0].lat,infos[0].lng);
            marker.position(location);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_01));

            map.addMarker(marker);

            adapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
        }
    }



    //위치 설정 다이얼 로그==============================================================
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


    //뒤로가기 버튼
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(Map2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager.isProviderEnabled("network")) {
            locationManager.requestLocationUpdates("network",10000,1,locationListener);
            Toast.makeText(this, "network", Toast.LENGTH_SHORT).show();
        } else if (locationManager.isProviderEnabled("gps")) {
            locationManager.requestLocationUpdates("gps",10000,1,locationListener);
            Toast.makeText(this, "gps", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "위치정보제공자가 없습니다.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(locationListener);
    }
}
