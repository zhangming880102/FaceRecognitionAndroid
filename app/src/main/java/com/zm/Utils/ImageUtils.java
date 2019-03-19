package com.zm.Utils;

import android.os.Environment;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {
    public static Mat toMat(byte[] data,int frameHeight,int frameWidth,int degree){
        if(data==null ||data.length==0 ||frameHeight==0 ||frameWidth==0){
            return new Mat();
        }
        Mat mat = new Mat((int)(1.5*frameHeight),frameWidth,CvType.CV_8UC1);
        mat.put(0,0,data);
        Mat bgr_i420 = new Mat(frameHeight,frameWidth,CvType.CV_8UC3);
        Imgproc.cvtColor(mat , bgr_i420, Imgproc.COLOR_YUV2RGBA_NV21);
        Imgproc.cvtColor(bgr_i420,bgr_i420,Imgproc.COLOR_RGBA2BGRA);
        Log.i("DATALENGTH",data.length+"\t"+mat.height()+"\t"+bgr_i420.width()+"\t"+bgr_i420.height());
        //旋转，如果预览图被顺时针旋转了90度，那么这里就需要逆时针旋转90度来得到正确方向的人脸
        if(degree==0){
            mat=bgr_i420;
        }else {
            switch (degree) {
                case 90:
                    Core.rotate(bgr_i420, mat, Core.ROTATE_90_COUNTERCLOCKWISE);
                    break;
                case 270:
                    Core.rotate(bgr_i420, mat, Core.ROTATE_90_CLOCKWISE);
                    break;
                case 180:
                    Core.rotate(bgr_i420, mat, Core.ROTATE_180);
                    break;
                default:
                    mat = bgr_i420;
            }
        }
        //左右互换，否则不是镜子效果。
        Core.flip(mat,mat,1);
        return mat;
    }

    public static byte[] toBytes(Mat mat){
        byte[] bytes=new byte[mat.height()*mat.width()];
        mat.get(0,0,bytes);
        return bytes;
    }

    public static byte[] toColorBytes(Mat mat){
        byte[] bytes=new byte[mat.height()*mat.width()*4];
        mat.get(0,0,bytes);
        return bytes;
    }
    public static Rect extendRect(Rect r,double ratio1,double ratio2,int width,int height){
        if(r.width==0 ||r.height==0){
            return r;
        }
        double x=r.tl().x+3;
        double y=r.tl().y+3;

        double rx_left=(r.tl().x-3)/r.width;
        double ry_up=(r.tl().y-3)/r.height;
        double rx_right=(width-x)/r.width;
        double ry_down=(height-y)/r.height;

        double rrx=(rx_left+rx_right)/ratio1;
        double rry=(ry_up+ry_down)/ratio2;

        double rrmin=rrx<rry? rrx:rry;
        if(rrmin<0){
            return r;
        }
        if(rrmin<1){
            ratio1*=rrmin;
            ratio2*=rrmin;
        }
        double tlx=r.tl().x;
        double tly=r.tl().y;
        double btx=r.br().x;
        double bty=r.br().y;

        if(rx_left>0 && rx_right>0){
            if(rx_left>ratio1/2 && rx_right>ratio1/2){
                tlx-=ratio1/2*r.width;
                btx+=ratio1/2*r.width;
            }else if(rx_left<ratio1/2){
                tlx-=rx_left*r.width;
                btx+=(ratio1-rx_left)*r.width;
            }else{
                tlx-=(ratio1-rx_right)*r.width;
                btx+=rx_right*r.width;
            }
        }
        if(ry_up>0 && ry_down>0){
            if(ry_up>ratio2/2 && ry_down>ratio2/2){
                tly-=ratio2/2*r.height;
                bty+=ratio2/2*r.height;
            }else if(ry_up<ratio2/2){
                tly-=ry_up*r.height;
                bty+=(ratio2-ry_up)*r.height;
            }else{
                tly-=(ratio2-ry_down)*r.height;
                bty+=ry_down*r.height;
            }
        }
        double ftlx=tlx;
        double ftly=tly;
        double fbtx=btx;
        double fbty=bty;
        if(ftlx< fbtx && ftly<fbty && ftlx>0 && fbtx<width && ftly>0 && fbty<height){
            return new Rect(new Point(ftlx,ftly),new Point(fbtx,fbty));
        }
        return r;
       // return new Rect(r.tl(),new Size(r.width*ratio1,r.height*ry));
    }
}