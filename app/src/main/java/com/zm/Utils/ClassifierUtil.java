package com.zm.Utils;

import android.app.Activity;
import android.content.Context;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ClassifierUtil {
    public static CascadeClassifier initClassifier(int res, String outxml, Activity ac) {
        try {
            InputStream is = ac.getResources()
                    .openRawResource(res);
            File cascadeDir = ac.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, outxml);
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            return new CascadeClassifier(cascadeFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
