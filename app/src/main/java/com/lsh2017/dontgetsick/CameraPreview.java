package com.lsh2017.dontgetsick;

import android.content.Context;
import android.graphics.Camera;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by 이소희 on 2017-08-07.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    Context context;
    android.hardware.Camera camera;
    SurfaceHolder holder;

   public void init(Context context){

       this.context=context;

       holder=getHolder();
       holder.addCallback(this);
       holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
   }

    public CameraPreview(Context context) {
        super(context);
        init(context);

    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        camera= android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);

        camera.setDisplayOrientation(90);


        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            camera.release();
            camera=null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        WindowManager manager=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

//        parameters.setPreviewSize(manager.getDefaultDisplay().getWidth(),manager.getDefaultDisplay().getHeight());
        camera.setParameters(parameters);
        camera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.stopPreview();
        camera=null;


    }
    public boolean capture(android.hardware.Camera.PictureCallback jpegHandler){
        if(camera!=null){
            camera.takePicture(null,null,jpegHandler);
            return true;
        }else{
            return false;
        }

    }
}
