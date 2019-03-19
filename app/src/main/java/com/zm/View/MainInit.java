package com.zm.View;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zm.Bean.UserBean;
import com.zm.R;
import com.zm.Utils.MyProperty;
import com.zm.Utils.MyUtils;

public class MainInit extends ViewInit {
    String postJson;
    Handler handler;
    Runnable runnable_post;
    Runnable runnable_delay;
    boolean outtime;
    Dialog dialog;
    String response;
    String serv="LoginService";

    @Override
    public void  init_buttons(){
        init_handler();
        try_default_login();
    }
    void init_handler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message message){
                super.handleMessage(message);
                if(outtime){
                    Log.i("chaoshi","chaoshi");
                    alert_string("服务器连接超时！请稍后重试");
                    dialog.dismiss();
                    ac.setContentView(ac.getLoginView());
                    return;
                }
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                UserBean lb=gson.fromJson(response,UserBean.class);
                if(lb==null||!(lb.isChecked())) {
                    String str="自动登录失败！";
                    if(lb!=null){
                        str+=lb.getErr_str();
                    }
                    alert_string(str);
                    ac.setContentView(ac.getLoginView());
                }else {
                    ac.setUser(lb);
                    TextView signinfo=ac.getsView().findViewById(R.id.sign_info_text);
                    signinfo.setText(lb.getCompany_name() + lb.getDepartment_name()+"\n"+lb.getDepartment_address());
                    ac.setContentView(ac.getsView());
                    msg_string(lb.getName()+"登录成功");
                }
                dialog.dismiss();
            }
        };
        runnable_post=new Runnable() {
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
        runnable_delay=new Runnable() {
            @Override
            public void run() {
                ac.setContentView(ac.getLoginView());
            }
        };
    }

    public void try_default_login() {


        String values[] = new String[2];
        MyUtils.getShareInfo(ac, new String[]{MyProperty.USER_SHARE_KEY, MyProperty.PASSWORD_SHARE_KEY}, values, MyProperty.SHARE_NAME);
        boolean check = true;
        for (int i = 0; i < 2; i++) {
            if (values[i] == null || values[i].length() == 0) {
                check = false;
            }
        }
        if (!check) {
            handler.postDelayed(runnable_delay,1000);
        } else {

            String user_name = values[0];
            String password = values[1];
            UserBean userBean = new UserBean();
            userBean.setUser(user_name);
            userBean.setPassword(password);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            postJson = gson.toJson(userBean, UserBean.class);
            outtime = false;

            dialog=new ProgressDialog(ac);
            dialog.show();
            new Thread(runnable_post).start();
        }
    }
}
