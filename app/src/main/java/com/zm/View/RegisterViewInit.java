package com.zm.View;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zm.Bean.ImgBean;
import com.zm.Bean.UserBean;
import com.zm.Utils.CameraPreview;
import com.zm.Utils.ImageUtils;
import com.zm.Utils.MyProperty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.zm.R;
import com.zm.Utils.MyUtils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class RegisterViewInit extends ViewInit {
    final static int STAGE_POST=1;
    final static int STAGE_CAMERA=0;
    Uri imageUri;
    String postJson;
    Handler handler;
    boolean outtime;
    Dialog dialog;
    String response;
    String serv="RegisterService";
    UserBean userBean;
    boolean info_checked=false;

    Runnable post_runnable;
    Runnable camera_runnable;

    ImgBean imgBean;
    ImgBean reviewImg;
    CameraPreview cp;
    int count=0;
    boolean img_success=false;
    String err_str="";
    boolean camera_done=false;
    boolean has_face;
    Rect face_rect;
    int stage;

    @Override
    public void init_buttons() {
        init_camera_runnable();
        init_post_runnable();
        init_handler();
        init_caiji_button();
        cameraView=v.findViewById(R.id.register_camera_view);
        cp=new CameraPreview(cameraView,ac);
    }
    void init_camera_runnable(){
        camera_runnable=new Runnable() {
            @Override
            public void run() {
                while(!camera_done) {
                    if (count > 30) {
                        camera_done=true;
                        handler.sendMessage(handler.obtainMessage());
                    } else {
                        if(cp.getData()==null ||cp.getData().length==0){
                            try {
                                Thread.sleep(100);
                                count++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        count++;
                        Mat mat=ImageUtils.toMat(cp.getData(),cp.getHeight(),cp.getWidth(),cp.getDisplayDegree());
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
                            handler.sendMessage(handler.obtainMessage());
                        }
                        if (has_face) {
                            Log.i("FaceDetectNum", face_rect.width + " " + face_rect.height);
                            if (reviewImg.getBytes().size() == 0) {
                                Mat smat=new Mat();
                                Imgproc.resize(mat,smat,new Size(MyProperty.reViewImgWidth,MyProperty.reViewImgHeight));
                                reviewImg.setHeight(MyProperty.reViewImgHeight);
                                reviewImg.setWidth(MyProperty.reViewImgWidth);
                                reviewImg.getBytes().add(ImageUtils.toColorBytes(smat));
                            }
                            //face_rect = ImageUtils.extendRect(face_rect, 1.5d, 2.5d, mat.width(), mat.height());
                            Mat submat = mat.submat(face_rect);
                            if(submat!=null && !submat.empty()) {
                                Imgproc.cvtColor(submat, submat, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.resize(submat, submat, new Size(MyProperty.imgWidth, MyProperty.imgHeight));
                                imgBean.getBytes().add(ImageUtils.toBytes(submat));
                                if (imgBean.getBytes().size() >= MyProperty.REGISTER_NEED_IMG) {
                                    camera_done = true;
                                    img_success = true;
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
    void init_handler(){

        handler=new Handler(){
            @Override
            public void handleMessage(Message message){
                super.handleMessage(message);
                if(stage==STAGE_CAMERA){
                    if(!camera_done){
                        msg_string("图像采集中..."+imgBean.getBytes().size()+"/"+MyProperty.REGISTER_NEED_IMG);
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
                            userBean.setReviewImg(reviewImg);
                            stage = STAGE_POST;
                            post_user();
                        }
                    }
                }else if(stage==STAGE_POST){
                    stage=-1;
                    dialog.dismiss();
                    if (outtime) {
                        alert_string("服务器连接超时！请稍后重试");
                        return;
                    }
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    UserBean lb = gson.fromJson(response, UserBean.class);
                    if (lb == null || !(lb.isRegistered())) {
                        String str = "注册失败！";
                        if (lb != null) {
                            str += lb.getErr_str();
                        }
                        alert_string(str);
                    } else {
                        ac.setUser(lb);
                        msg_string("注册成功!");
                        TextView signinfo=ac.getsView().findViewById(R.id.sign_info_text);
                        signinfo.setText(lb.getCompany_name()+lb.getDepartment_name());
                        String key[]=new String[]{MyProperty.USER_SHARE_KEY,MyProperty.PASSWORD_SHARE_KEY};
                        String value[]=new String[]{lb.getUser(),lb.getPassword()};
                        MyUtils.saveShareInfo(ac,key,value,MyProperty.SHARE_NAME);

                        ((EditText)ac.getLoginView().findViewById(R.id.login_input_user)).setText(lb.getUser());
                        ((EditText)ac.getLoginView().findViewById(R.id.login_input_password)).setText(lb.getPassword());
                        ac.setContentView(ac.getLoginView());
                    }
                }
            }
        };
    }
    void init_post_runnable(){

       post_runnable=new Runnable() {
            @Override
            public void run() {
                Log.i("postip","http://"+MyProperty.ip + ":" + MyProperty.port + "/" + MyProperty.service+"/" +serv);
                try {
                    response=doPost(postJson, "http://"+MyProperty.ip + ":" + MyProperty.port + "/" +MyProperty.service+"/" +serv);
                    Message msg=handler.obtainMessage();
                    handler.sendMessage(msg);
                }catch (Exception e){
                    Log.i("err",e.toString());
                }
            }
        };
    }

    void post_user(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        postJson=gson.toJson(userBean,UserBean.class);
        outtime=false;
        dialog=new ProgressDialog(ac);
        dialog.show();
        new Thread(post_runnable).start();
    }
    void check_img(){
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setEnabled(true);
        imgBean=new ImgBean();
        imgBean.setBytes(new ArrayList<byte[]>());
        imgBean.setWidth(MyProperty.imgWidth);
        imgBean.setHeight(MyProperty.imgHeight);

        reviewImg=new ImgBean();
        reviewImg.setBytes(new ArrayList<byte[]>());
        //cp.openCamera();
        has_face=false;
        count=0;
        img_success=false;
        err_str="";
        camera_done=false;

        new Thread(camera_runnable).start();
    }
    void checkUserInfo(){
        info_checked=false;

        String user=((EditText) (v.findViewById(R.id.register_input_zhanghao))).getText().toString();
        String password=((EditText) (v.findViewById(R.id.register_input_mima))).getText().toString();
        String xuexiao=((EditText) (v.findViewById(R.id.register_input_xuexiao))).getText().toString();
        String banji=((EditText) (v.findViewById(R.id.register_input_banji))).getText().toString();
        String name=((EditText) (v.findViewById(R.id.register_input_name))).getText().toString();

        if(user==null || user.trim().length()==0){
            alert_string("账号不能为空！");
            return;
        }
        if(password==null ||password.trim().length()==0){
            alert_string("密码不能为空！");
            return;
        }

        if(xuexiao==null ||xuexiao.trim().length()==0){
            alert_string("学校不能为空！");
            return;
        }

        if(banji==null ||banji.trim().length()==0){
            alert_string("班级不能为空！");
            return;
        }

        if(name==null ||name.trim().length()==0){
            alert_string("姓名不能为空！");
            return;
        }
        user=user.trim();
        password=password.trim();
        xuexiao=xuexiao.trim();
        banji=banji.trim();
        name=name.trim();

        userBean=new UserBean();
        userBean.setUser(user);
        userBean.setPassword(password);
        userBean.setDepartment_name(banji);
        userBean.setCompany_name(xuexiao);
        userBean.setName(name);
        info_checked=true;
    }
    void init_caiji_button(){
        (v.findViewById(R.id.caiji_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stage=STAGE_CAMERA;
                checkUserInfo();
                if(!info_checked){
                    stage=-1;
                    return;
                }
                check_img();
            }
        });
    }


}
