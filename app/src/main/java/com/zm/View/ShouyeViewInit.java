package com.zm.View;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zm.Activity.ShouyeActivity;
import com.zm.Bean.ImgBean;
import com.zm.Bean.PositionBean;
import com.zm.Bean.UserBean;
import com.zm.R;
import com.zm.Utils.BDLocation;
import com.zm.Utils.CameraPreview;
import com.zm.Utils.ImageUtils;
import com.zm.Utils.MyProperty;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dalvik.system.BaseDexClassLoader;

import static java.lang.Thread.sleep;

public class ShouyeViewInit extends ViewInit{
    public static final int STAGE_LOCATION=0;
    public static final int STAGE_CAMERA=1;
    public static final int STAGE_POST=2;

    Dialog dialog;
    BDLocation bdl;
    Handler handler;
    Runnable thread;

    Runnable thread_post;

    Runnable thread_camera;

    boolean camera_done=false;
    boolean img_success=false;
    boolean has_face=false;
    Rect face_rect;
    String err_str="";
    int count=0;
    int camera_scene;
    CameraPreview cp;
    String response;
    String postJson;
    String serv="RecieveMatService";
    String serv_sign_list="SignListService";
    ImgBean imgBean;
    UserBean userBean;
    int stage=-1;
    //Bitmap bitmap;

    Runnable post_sign_list;
    Handler handler_sign_list;

    @Override
    public void init_buttons(){
        init_runnable_for_location();
        init_runnable_for_camera();
        init_runnable_for_post();

        init_runnable_for_post_sign_list();
        init_handler_for_post_sign_list();
        init_handler();
        v.findViewById(R.id.shouye_button_yuyueyisheng).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                bdl=new BDLocation(ac);
                bdl.checkPos(ac);
                userBean=new UserBean();
                userBean.setUser(ac.getUser().getUser());
                userBean.setPassword(ac.getUser().getPassword());

                count=0;
                dialog = new ProgressDialog(ac);
                dialog.show();
                stage=STAGE_LOCATION;
                handler.post(thread);

            }
        });
        cameraView=v.findViewById(R.id.sign_camera_view);
        cp=new CameraPreview(cameraView,ac);
        v.findViewById(R.id.shouye_button_sign_list).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new Thread(post_sign_list).start();
                dialog=new ProgressDialog(ac);
                dialog.show();
            }
        });

        v.findViewById(R.id.shouye_button_fanhuidenglu).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    ac.setContentView(ac.getLoginView());
                }
            }
        );
        v.findViewById(R.id.shouye_button_xiugaibumen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAddressViewInit eav=new EditAddressViewInit();
                eav.init(ac,ac.getEditView());
                ac.setContentView(ac.getEditView());
            }
        });
    }
    void init_runnable_for_post_sign_list(){
        post_sign_list=new Runnable() {
            @Override
            public void run() {
                Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                postJson=gson.toJson(ac.getUser(),UserBean.class);
                try {
                    outTime=false;
                    response = doPost(postJson, "http://" + MyProperty.ip + ":" + MyProperty.port + "/" + MyProperty.service + "/" + serv_sign_list);
                }catch (Exception e){
                    e.printStackTrace();
                }
                handler_sign_list.sendMessage(handler_sign_list.obtainMessage());
            }
        };
    }
    void init_handler_for_post_sign_list(){
        handler_sign_list=new Handler(){
            @Override
            public void handleMessage(Message msg){
                dialog.dismiss();
                Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                UserBean responseUser=gson.fromJson(response,UserBean.class);
                if(outTime) {
                    alert_string("服务器连接失败，请稍后重试！");
                }else if(responseUser==null || !responseUser.isChecked()){
                    alert_string("获取签到列表失败");
                }else{
                    ac.setUser(responseUser);
                    new SignListInit().init(ac,ac.getSignListView());
                    ac.setContentView(ac.getSignListView());
                }
            }
        };
    }

    void init_handler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(stage==STAGE_LOCATION) {
                    if (!bdl.getPos().isReceived()) {
                        alert_string("定位失败，请检查定位是否打开");
                        stage=-1;
                    } else {
                        msg_string("定位成功");
                        userBean.setSign_addrees(bdl.getPos().getAddress());
                        userBean.setSign_latitude(bdl.getPos().getLatitude());
                        userBean.setSign_longtitude(bdl.getPos().getLongtitude());
                        stage=STAGE_CAMERA;
                        check_img();
                    }
                    dialog.dismiss();
                }else if(stage==STAGE_CAMERA){
                    if(!camera_done){
                        msg_string("图像采集中..."+imgBean.getBytes().size()+"/"+MyProperty.imgMax);
                    }else {
                        cp.releaseCamera();
                        cameraView.setEnabled(false);
                        cameraView.setVisibility(SurfaceView.GONE);
                        if (!img_success) {
                            if (err_str != null && err_str.length() != 0) {
                                alert_string(err_str);
                            } else {
                                alert_string("未检测到人脸，请重试！");
                            }
                            stage = -1;
                        } else {
                            msg_string("采集图片成功");
                            userBean.setImg(imgBean);
                            stage = STAGE_POST;
                            post_images();
                        }
                    }
                }else if(stage==STAGE_POST){
                    Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    UserBean responseUser=gson.fromJson(response,UserBean.class);
                    if(outTime) {
                        alert_string("服务器连接失败，请稍后重试！");
                    }else if(!responseUser.isFinal_check()){
                        alert_string(responseUser.getErr_str());
                    }else if(!responseUser.isImg_signed()){
                        alert_string("人脸识别未成功，请重新拍照");
                    }else if(!responseUser.isAddress_signed()){
                        alert_string("地址匹配未成功");
                    }else{
                        msg_string("签到成功！");
                    }
                    stage=-1;
                }
            }
        };
    }

    void init_runnable_for_location(){
        thread=new Runnable() {
            @Override
            public void run() {

                if(!bdl.getPos().isReceived()) {
                    count++;
                    if (count > 10) {
                        handler.sendMessage(handler.obtainMessage());
                    }else{
                        handler.postDelayed(thread,300);
                    }
                }else{
                    handler.sendMessage(handler.obtainMessage());
                }
            }
        };

    }

    void init_runnable_for_post(){
        thread_post=new Runnable() {
            @Override
            public void run() {
                Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                postJson=gson.toJson(userBean,UserBean.class);
                try {
                    outTime=false;
                    response = doPost(postJson, "http://" + MyProperty.ip + ":" + MyProperty.port + "/" + MyProperty.service + "/" + serv);
                }catch (Exception e){
                    e.printStackTrace();
                }
                handler.sendMessage(handler.obtainMessage());
            }
        };
    }
    void init_runnable_for_camera(){
        thread_camera=new Runnable() {
            @Override
            public void run() {
                while(!camera_done) {

                    if (count > 10) {
                        camera_done=true;
                        handler.sendMessage(handler.obtainMessage());
                    } else {
                       // Log.i("FRAME_INFO",+cp.getHeight()+"\t"+cp.getWidth()+"\t"+cp.getDisplayDegree()+"\t"+(cp.getMat()==null));
                        if(cp.getData()==null ||cp.getData().length==0){
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        count++;
                        //Mat mat = cp.getMat().clone();
                        Mat mat=ImageUtils.toMat(cp.getData(),cp.getHeight(),cp.getWidth(),cp.getDisplayDegree());
                        //bitmap=Bitmap.createBitmap(mat.width(),mat.height(),Bitmap.Config.ARGB_8888);
                        //Utils.matToBitmap(mat,bitmap);
                        CascadeClassifier classifier = ac.getClassifier();
                        CascadeClassifier eyeclassifier = ac.getEyeClassifier();
                        if (classifier != null) {
                            MatOfRect mof = new MatOfRect();
                            classifier.detectMultiScale(mat, mof, 1.05, 3, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(40, 40));
                            Rect[] rs = mof.toArray();
                            has_face = false;
                            Log.i("FaceDetectNum", rs.length + "");
                            for (int i = 0; i < rs.length; i++) {
                                if(rs[i].width<=15 ||rs[i].width<15){
                                    continue;
                                }
                                if (eyeclassifier != null) {
                                    Mat smat = mat.submat(rs[i]);
                                    MatOfRect eyemfo = new MatOfRect();
                                    eyeclassifier.detectMultiScale(smat, eyemfo, 1.05, 3, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                                            new Size(15, 15));
                                    if (eyemfo.toArray().length > 1) {
                                        has_face = true;
                                        face_rect = rs[i];
                                        break;
                                    }
                                } else {
                                    err_str = "未能加载人脸识别眼睛分类器";
                                    camera_done=true;
                                    handler.sendMessage(handler.obtainMessage());
                                }
                            }
                        /*
                        if(rs.length>0 && !has_face){
                            has_face=true;
                            face_rect=rs[0];
                        }
                        */
                        } else {
                            err_str = "未能加载人脸识别分类器";
                            camera_done=true;
                            handler.sendMessage(handler.obtainMessage());
                        }
                        if (has_face) {
                            Log.i("FaceDetectNum", face_rect.width + " " + face_rect.height);
                            //face_rect = ImageUtils.extendRect(face_rect, 0.2d, 0.8d, mat.width(), mat.height());
                            Mat submat = mat.submat(face_rect);
                            if(submat!=null && !submat.empty()) {
                                Imgproc.cvtColor(submat, submat, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.resize(submat, submat, new Size(MyProperty.imgWidth, MyProperty.imgHeight));
                                img_success = true;
                                imgBean.getBytes().add(ImageUtils.toBytes(submat));
                                if (imgBean.getBytes().size() >= MyProperty.imgMax) {
                                    camera_done = true;
                                }
                                handler.sendMessage(handler.obtainMessage());
                            }
                        }
                        if (!camera_done) {
                            try {
                                Thread.sleep(40);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
    }


    public void post_images(){
        new Thread(thread_post).start();
    }

    public void check_img(){
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setEnabled(true);
        imgBean=new ImgBean();
        imgBean.setBytes(new ArrayList<byte[]>());
        imgBean.setWidth(MyProperty.imgWidth);
        imgBean.setHeight(MyProperty.imgHeight);
        //cp.openCamera();

        count=0;
        img_success=false;
        err_str="";
        camera_done=false;

        new Thread(thread_camera).start();
    }


}
