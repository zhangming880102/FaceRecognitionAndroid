package com.zm.Utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/9/28.
 */
public class MyProperty {
    public static int width=360;
    public static int height=640;
    public static int version=15;
    public static float scale=1f;
    public static int statbarheight=18;
    public static float scaleY=1f;
    public static float scaleX=1f;
    public static String ip="192.168.18.10";
    //public static String ip="182.61.57.223";
    //public static String ip="172.25.11.247";
    public static String port="8080";
    //public static String service="MarvelDoctorService";
    public static int imgWidth=128;
    public static int imgHeight=128;
    public static int reViewImgWidth=180;
    public static int reViewImgHeight=320;
    public static int imgMax=2;
    public static int REGISTER_NEED_IMG=5;
    public static String USER_SHARE_KEY="user";
    public static String PASSWORD_SHARE_KEY="password";
    public static String SHARE_NAME="UserInfo";

    public static String service="FaceRecognitionService";
    public static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        for (;;) {
            final int result = MyProperty.sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (MyProperty.sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

}
