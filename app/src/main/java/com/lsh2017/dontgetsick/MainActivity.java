package com.lsh2017.dontgetsick;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    NavigationView navigationView;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    EditText editName, editAge, editWeight, editHeight;
    TextView headerName, headerAge, headerWeight, headerHeight, headerGender;

    RadioGroup radioGroup;
    RadioButton rbMale, rbFemale;

    //병원 키
    String APIKey="AAABXXIyJDUjHnGySgsCbhqnYpeHMTRagVeG7Q%3D%3D";
    //아픈 곳
    String sick="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView=(NavigationView) findViewById(R.id.navi_view);
        View headerlayout=navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_medicine:
                Log.d("MENU","약");
                Intent intent=new Intent(MainActivity.this,MedicineActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_myhospital:
                //내가 즐겨찾기한 병원 목록
               Intent intent2=new Intent(MainActivity.this,MyHospitalActivity.class);
                startActivity(intent2);
                Log.d("MENU","병원");
                break;
            case R.id.menu_item_search_hospital:
                //네이버 지도 검색
                Intent intent1=new Intent(MainActivity.this,Map2Activity.class);
                startActivity(intent1);
                Log.d("MENU","검색 병원");
                break;

            }

            return true;
        }
    });


        //헤더뷰에 나타날 이름 나이 키 몸무게 성별
        headerName=(TextView)headerlayout.findViewById(R.id.header_name);
        headerAge=(TextView)headerlayout.findViewById(R.id.header_age);
        headerHeight=(TextView)headerlayout.findViewById(R.id.header_height);
        headerWeight=(TextView)headerlayout.findViewById(R.id.header_weight);
        headerGender=(TextView)headerlayout.findViewById(R.id.header_gender);



        //내비 뷰

        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.app_name,R.string.app_name);


        //삼선 모양 보이게 하기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();

        //삼선 화살표로 바꾸기
        drawerLayout.addDrawerListener(drawerToggle);




            try {
                    FileInputStream dis=openFileInput("info.json");

                    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
                    String line = br.readLine();
                    StringBuffer buffer = new StringBuffer();
                    while (line != null) {
                        buffer.append(line);
                        line = br.readLine();
                    }
                    br.close();

                    JSONObject object = new JSONObject(buffer.toString());
                    String name = object.getString("이름");
                    String age = object.getString("나이");
                    String weight = object.getString("몸무게");
                    String height = object.getString("키");


                    headerName.setText(name);
                    headerAge.setText(age);
                    headerWeight.setText(weight);
                    headerHeight.setText(height);




            } catch (IOException e) {

                    //내정보 다이얼로그 보여주기
                    LayoutInflater inflater=getLayoutInflater();
                    View dialogView=inflater.inflate(R.layout.dialog_my_info,null);

                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    //입력받은 이름 나이 키 몸무게 성별
                    editName=(EditText)dialogView.findViewById(R.id.edit_name);
                    editAge=(EditText)dialogView.findViewById(R.id.edit_age);
                    editWeight=(EditText)dialogView.findViewById(R.id.edit_weight);
                    editHeight=(EditText)dialogView.findViewById(R.id.edit_height);


                    builder.setView(dialogView);

                    builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            headerName.setText(editName.getText().toString()+"");
                            headerAge.setText(editAge.getText().toString()+"세");
                            headerHeight.setText(editHeight.getText().toString()+"cm");
                            headerWeight.setText(editWeight.getText().toString()+"kg");

                            try {

                                File path= new File("info.json");
                                if(!path.exists()){
                                    path.mkdirs();
                                }
                                FileOutputStream fos=openFileOutput("info.json",MODE_PRIVATE);

                                PrintWriter writer=new PrintWriter(fos);
                                JSONObject object=new JSONObject();
                                object.put("이름",editName.getText().toString());
                                object.put("나이",editAge.getText().toString());
                                object.put("몸무게",editWeight.getText().toString());
                                object.put("키",editHeight.getText().toString());


                                Log.e("e",object.toString());

                                writer.print(object.toString());
                                writer.flush();
                                writer.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("e","e");
                            }
                            Toast.makeText(MainActivity.this, "저장합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "취소합니다.", Toast.LENGTH_SHORT).show();

                        }
                    });

                    builder.show();
                    e.printStackTrace();

                    Log.e("c","c");
            } catch (JSONException e) {
                e.printStackTrace();
            }


    }


//    public void clickHead(View v){
//        //머리버튼 눌렀을때
//
//        sick="머리";
//        Intent intent=new Intent();
//        urlThread urlThread=new urlThread();
//        urlThread.start();
//    }

    //질병정보 xml얻어오기
    class urlThread extends Thread{
        @Override
        public void run() {

            String address="http://openapi.samsunghospital.com/service/searchapi/search?query="
                    +sick+"&pageNo=1&numOfRows=10&accessKey="+APIKey;
            try {
                //해임달
                URL url=new URL(address);
                //주소와연결하는 무지개로드
                InputStream is=url.openStream();
                InputStreamReader isr=new InputStreamReader(is);

                //xmlpullparser객체 생성
                XmlPullParserFactory pullParserFactory= XmlPullParserFactory.newInstance();
                XmlPullParser xpp=pullParserFactory.newPullParser();
                xpp.setInput(isr);

                xpp.next();
                int eventType=xpp.getEventType();

                String diseaseName="";//병명이름:contTitle
                String diseaseImg="";//사진이미지:MTumImg
                String diseaseContent="";//질병 내용:Summary
                String detailUrl="";//상세 url:mDetailUrl

                while(eventType!=XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;

                    }
                    eventType=xpp.next();
                }






            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }


        }
    }


    @Override
    public void onBackPressed() {

        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else

        super.onBackPressed();
    }

    @Override//삼선 눌렀을 때 내비게이션바 나오게 하는거.
    public boolean onOptionsItemSelected(MenuItem item) {
        drawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }


}
