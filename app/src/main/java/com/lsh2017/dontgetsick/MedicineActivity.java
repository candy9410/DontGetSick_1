package com.lsh2017.dontgetsick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MedicineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MedicineAdapter adapter;
    ArrayList<MedicineItem> items=new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;
    TextView medicineDate;

    String serverUrl="http://sohee4112.dothome.co.kr/DontgetSick/loadMedicine.php";

    String imgName;
    String imgPath;
    String memo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);




        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        medicineDate=(TextView)findViewById(R.id.medicine_date);
        adapter=new MedicineAdapter(this,items);

                //데이터 가져와서 보이기
        Thread();

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                items.clear();
                Thread();

                swipeRefreshLayout.setRefreshing(false);

            }
        });
        Log.d("스레드 끝","스레드 끝");
        recyclerView.setLayoutManager(new LinearLayoutManager(MedicineActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);



        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


//        try {
//            URL url=new URL(serverUrl);
//            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
    void Thread(){
        new Thread(){
            @Override
            public void run() {
                try {
                    Log.i("스레드시작","thread");
                    URL url= new URL(serverUrl);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setUseCaches(false);

                    InputStream is=conn.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is);
                    BufferedReader reader= new BufferedReader(isr);
                    StringBuffer buffer=new StringBuffer();
                    String line=reader.readLine();
                    items.clear();
                    while(line!=null){
                        String[] row = line.split("&");
                        buffer.append(line);
                        Log.e("스트링라인",line);
                        Log.e("스트링라인",row.length+"");


                        if ( row.length == 3 ){
                            String img = row[0];
                            String memo = row[1];
                            String date = row[2];
                            Log.e("img",img);
                            Log.e("memo",memo);
                            Log.e("date",date);

                            items.add(new MedicineItem(img,memo,date));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                                }
                            });
                        }

                        line=reader.readLine();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        switch (id){
            case R.id.medicine_add:
                Intent intent=new Intent(this,MedicineMemoActivity.class);
                startActivityForResult(intent,10);
                break;
            case android.R.id.home:
                finish();
                break;

        }


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==10){

            if(resultCode == RESULT_OK) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());

                Log.i("kkkkkk",data.getStringExtra("imgMedicine"));
                imgPath=data.getStringExtra("imgMedicine");
                memo=data.getStringExtra("memo");

                //서버전송
                SimpleMultiPartRequest smpr=new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response",response);
                     //   Toast.makeText(MedicineActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MedicineActivity.this, "서버접속실패", Toast.LENGTH_SHORT).show();
                    }
                });

                smpr.addFile("image",imgPath);
                smpr.addStringParam("memo",memo);
                smpr.addStringParam("date",time);


                RequestQueue queue= Volley.newRequestQueue(this);
                queue.add(smpr);

                //getImageNameToUri(data.getData());
                //Log.e("imgPath", abs);

                MedicineItem item = new MedicineItem(data.getStringExtra("imgMedicine"), data.getStringExtra("memo"), time);


                items.add(item);



                Log.e("사진경로",item.imgMedicine.toString());

                Log.d("LIST", items.size() + "");
            }

        }


    }

//    public String getImageNameToUri(Uri data)
//    {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(data, proj, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//        cursor.moveToFirst();
//
//        imgPath = cursor.getString(column_index);
//        Log.d("imgpath", imgPath);
//        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
//
//        return imgName;
//    }

    void databaseSave(MedicineItem item){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//
//                    Log.e("hello","hello");
//                    URL url=new URL(serverUrl);//해임달
//                    HttpURLConnection connection=(HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("POST");
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setUseCaches(false);
//                    FileInputStream fis= new FileInputStream(item.abs);
//                    OutputStream os=connection.getOutputStream();
//                    int available=fis.available();
//
//                    connection.setRequestProperty("Connection","Keep-Alive");
//                    connection.setRequestProperty("Content-Type","multipart/form-data; boundary=*****");
//
//                    while(true){
//                        int bytesSize=(available<1024)?available:1024;
//                        byte[] bytes=new byte[bytesSize];
//                        int readByte=fis.read(bytes,0,bytesSize);
//                        if(readByte<=0){
//                            break;//다 읽음
//                        }
//                        os.write(bytes,0,bytes.length);
//                        os.flush();
//
//                    }
//
//                    os.write("--*****/r/n".getBytes());
//                    os.write(("Content-Disposition: form-data: name=upload; filename="+imgPath+"/r/n").getBytes());
//                    os.write("/r/n".getBytes());
//
////                    os.write("/r/n".getBytes());
////                    os.write("--*****--/r/n".getBytes());
//
//                    os.close();
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_medicine_item, menu);

        return true;
    }

}
