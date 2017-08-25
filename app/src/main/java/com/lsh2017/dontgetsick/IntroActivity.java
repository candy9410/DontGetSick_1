package com.lsh2017.dontgetsick;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(this.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                //사용자가 임의로 권한을 취소 시킨경우
                //권한 재요청
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);

            }else {
                //최초 권한 요청
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
            }
        }else {
            //사용 권한이 있음을 확인한경우
            new Thread(){
                @Override
                public void run() {

                    try {
                        sleep(3000);
                        Intent intent=new Intent(IntroActivity.this,MainActivity.class);
                        startActivity(intent);

                        finish();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==10){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                new Thread(){
                    @Override
                    public void run() {

                        try {
                            sleep(3000);
                            Intent intent=new Intent(IntroActivity.this,MainActivity.class);
                            startActivity(intent);

                            finish();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        }
    }
}
