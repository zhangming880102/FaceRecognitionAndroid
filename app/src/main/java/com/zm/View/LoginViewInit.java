package com.zm.View;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zm.Bean.UserBean;
import com.zm.R;
import com.zm.Utils.MyProperty;
import com.zm.Utils.MyUtils;

public class LoginViewInit extends ViewInit {
    String postJson;
    Handler handler;
    Runnable runnable;
    boolean outtime;
    Dialog dialog;
    String response;
    String serv="LoginService";
    @Override
    public void init_buttons(){
        init_login_button();
        init_register_button();
    }
    void init_login_button(){
        (v.findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String user=((EditText) (v.findViewById(R.id.login_input_user))).getText().toString();
                String password=((EditText) (v.findViewById(R.id.login_input_password))).getText().toString();
                if(user==null || user.trim().length()==0){
                    alert_string("用户名不能为空！");
                    return;
                }
                if(password==null ||password.trim().length()==0){
                    alert_string("密码不能为空！");
                    return;
                }
                user=user.trim();
                password=password.trim();
                UserBean userBean=new UserBean();
                userBean.setUser(user);
                userBean.setPassword(password);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                postJson=gson.toJson(userBean,UserBean.class);
                outtime=false;
                handler=new Handler(){
                    @Override
                    public void handleMessage(Message message){
                        super.handleMessage(message);
                        if(outtime){
                            Log.i("chaoshi","chaoshi");
                            alert_string("服务器连接超时！请稍后重试");
                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create();
                        UserBean lb=gson.fromJson(response,UserBean.class);
                        if(lb==null||!(lb.isChecked())) {
                            String str="登录失败！";
                            if(lb!=null){
                                str+=lb.getErr_str();
                            }
                            alert_string(str);
                        }else {
                            ac.setUser(lb);
                            ac.setContentView(ac.getsView());
                            TextView signinfo=ac.getsView().findViewById(R.id.sign_info_text);
                            signinfo.setText(lb.getCompany_name() + lb.getDepartment_name()+"\n"+lb.getDepartment_address());
                            msg_string(lb.getName()+"登录成功");
                            String key[]=new String[]{MyProperty.USER_SHARE_KEY,MyProperty.PASSWORD_SHARE_KEY};
                            String value[]=new String[]{lb.getUser(),lb.getPassword()};
                            MyUtils.saveShareInfo(ac,key,value,MyProperty.SHARE_NAME);
                        }
                        dialog.dismiss();
                    }
                };
                Runnable runnable=new Runnable() {
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
                dialog=new ProgressDialog(ac);
                dialog.show();
                new Thread(runnable).start();
            }
        });
    }



    void init_register_button(){
        (v.findViewById(R.id.register_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.setContentView(ac.getRegisterView());
            }
        });
    }

}
