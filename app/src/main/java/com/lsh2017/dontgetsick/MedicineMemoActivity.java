package com.lsh2017.dontgetsick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MedicineMemoActivity extends AppCompatActivity {


    EditText editMemo;
    Button btnPhoto, btnRephoto;
    TextView medicineDate;


    MedicineItem items;
    boolean isTakenPic=false;
    CameraPreview cameraPreview;
    byte[] imgByte;
    String time="";

    String serverUrl="http://sohee4112.dothome.co.kr/DontgetSick/insertInfo.php";
    String imgUrl="http://sohee4112.dothome.co.kr/DontgetSick/images/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_memo);

        editMemo=(EditText)findViewById(R.id.edit_memo);
        cameraPreview=(CameraPreview)findViewById(R.id.camerapreview);
        btnPhoto=(Button)findViewById(R.id.btn_photo);
        btnRephoto=(Button)findViewById(R.id.btn_rephoto);
        medicineDate=(TextView)findViewById(R.id.medicine_date);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.capture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if(!isTakenPic){
                            isTakenPic=true;
                            cameraPreview.camera.stopPreview();
                            imgByte=data;

                        }

                    }
                });
            }
        });
        btnRephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTakenPic){
                    cameraPreview.camera.startPreview();
                    isTakenPic=false;
                }

            }
        });



    }//---------------------------------------------------------카메라 찍는것

    //버튼 누르면 카드뷰에있는 애들한테 값들을 전달.
    public void clickBtn(View v){


        Log.e("imgByte",imgByte+"");

        if (imgByte!=null){
            Intent intent=getIntent();
            String path= Environment.getExternalStorageDirectory().getAbsolutePath();

            Date date= new Date();
            SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd_HHmmss");
            time=sdf.format(date);

            //앨범에 sick 라는 파일만듬.
            File dir=new File(path+"/sick/");
            if(!dir.exists()) dir.mkdirs();

            //파일 만들고 파일경로정해줌.
            path = path +"/sick/"+time+".png";
            File file=new File(path);

            Log.e("경로경로경로",path);
             try {

                 FileOutputStream fos=new FileOutputStream(file);
                 fos.write(imgByte);
                 fos.flush();
                 fos.close();

                 new MediaScanning(this,file);


                 Log.d("FILE", "SUCCESS");
            } catch (FileNotFoundException e) {
                 Log.d("FILE", "FAIL");
            } catch (IOException e) {
                 Log.d("FILE", "FAIL");
            }

            intent.putExtra("imgMedicine",file.getPath());
            intent.putExtra("memo",editMemo.getText().toString());

            Log.e("imgMedicine", file.getPath());
            Log.e("memo", editMemo.getText().toString());

            setResult(RESULT_OK,intent);
            finish();

        }
        MyThread myThread=new MyThread();
        myThread.start();



        // 쓰여진 값들을 db에 저장


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;

        }

        return true;
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            try {

                URL url =new URL(serverUrl);
                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                OutputStream os= conn.getOutputStream();
                PrintWriter writer= new PrintWriter(os);
                String memo=editMemo.getText().toString();
                String imgName=time+".png";

//                writer.write(imgUrl+imgName);
//                writer.write(memo);
//                writer.write(time);
//
//                writer.flush();
//                writer.close();


                String data="img="+imgUrl+imgName+"&memo="+memo+"&date="+time;


                os.write(data.getBytes());
                os.flush();
                os.close();

                InputStream is=conn.getInputStream();
                InputStreamReader isr= new InputStreamReader(is);
                BufferedReader buffer=new BufferedReader(isr);
                final StringBuffer sb=new StringBuffer();
                String line=buffer.readLine();
                while(line!=null){
                    sb.append(line);
                    line=buffer.readLine();
                }

                final String finalLine = line;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MedicineMemoActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("소희못생김0",sb.toString());
                    }
                });



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
