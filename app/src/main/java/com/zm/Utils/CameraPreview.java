package com.zm.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


import com.zm.Activity.ShouyeActivity;
import com.zm.R;

import org.opencv.core.Mat;

import java.io.IOException;

public class CameraPreview implements SurfaceHolder.Callback,
        Camera.PreviewCallback, View.OnClickListener {
    private static final int SRC_FRAME_WIDTH = 1280;
    private static final int SRC_FRAME_HEIGHT = 720;
    private static final int IMAGE_FORMAT = ImageFormat.YV12;

    private Camera mCamera;
    private Camera.Parameters mParams;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    ShouyeActivity ac;
    Paint p;
    boolean draw_circle=false;
    int height;
    int width;
    Handler handler;
    Runnable runnable;
    int displayDegree;
    Mat mat;
    byte[] data;

    public CameraPreview(SurfaceView sv,ShouyeActivity activity){
        mSurfaceView=sv;
        ac=activity;
        initView();
        setListener();
    }


    private void initView() {
        mSurfaceHolder = mSurfaceView.getHolder();
        //mSurfaceHolder.setFixedSize(SRC_FRAME_WIDTH, SRC_FRAME_HEIGHT);
        mSurfaceHolder.addCallback(this);
        //mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void setListener() {
        // set Listener if you want, eg: onClickListener
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //ImageUtils.saveImageData(data);
        this.data=data;
        //Log.i("FRAME_INFO",data.length+"\t"+getHeight()+"\t"+getWidth()+"\t"+getDisplayDegree()+"\t"+(getMat()==null));
        //mat=ImageUtils.toMat(data,height,width,this.displayDegree);
        camera.addCallbackBuffer(data);
    }

    public void openCamera(SurfaceHolder holder) {
        releaseCamera(); // release Camera, if not release camera before call camera, it will be locked
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

       // mParams = mCamera.getParameters();
        setCameraDisplayOrientation(ac, Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);
        Camera.Size s=mCamera.getParameters().getPreviewSize();
        height=s.height;
        width=s.width;
        Log.i("CAMERASIZE",s.height+"\t"+s.width+"\t"+displayDegree);
        //mParams.setPreviewSize(SRC_FRAME_WIDTH, SRC_FRAME_HEIGHT);
       // mParams.setPreviewFormat(IMAGE_FORMAT); // setting preview format：YV12
        //mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        //mCamera.setParameters(mParams); // setting camera parameters
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
    }

    public synchronized void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera = null;
        }
    }

    /**
     * Android API: Display Orientation Setting
     * Just change screen display orientation,
     * the rawFrame data never be changed.
     */
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayDegree = (info.orientation + degrees) % 360;
            displayDegree = (360 - displayDegree) % 360;  // compensate the mirror
        } else {
            displayDegree = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(displayDegree);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("SURFACE_CREATE:","n");
        openCamera(holder); // open camera
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("SURFACE_DESTROYED:","n");
        releaseCamera();
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isDraw_circle() {
        return draw_circle;
    }

    public void setDraw_circle(boolean draw_circle) {
        this.draw_circle = draw_circle;
    }

    public int getDisplayDegree() {
        return displayDegree;
    }

    public void setDisplayDegree(int displayDegree) {
        this.displayDegree = displayDegree;
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}