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


public class EditAddressViewInit extends ViewInit {
    public static final int STAGE_LOCATION=0;
    public static final int STAGE_POST=2;

    Dialog dialog;
    Handler handler;
    Runnable thread;

    Runnable thread_post;

    UserBean userBean;
    int count=0;
    String response;
    String postJson;
    String serv="UserEditCompanyService";
    int stage=-1;
    String raw_department="";
    String raw_company="";

    boolean info_checked=false;
    int using=0;

    @Override
    public void init_buttons(){

        init_edit_text();
        init_runnable_for_post();
        init_handler();

        v.findViewById(R.id.edit_button_fanhui).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ac.setContentView(ac.getsView());
            }
        });
        v.findViewById(R.id.edit_xiugai_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                stage=STAGE_LOCATION;
                using=1;
                checkUserInfo();
                if(!info_checked){
                    stage=-1;
                    return;
                }
                post_user();
            }
        });

    }
    void init_edit_text(){
        if(ac.getUser()==null ){
            return;
        }
        ((EditText) (v.findViewById(R.id.edit_input_xuexiao))).setText(MyUtils.nullStr(ac.getUser().getCompany_name()));
        ((EditText) (v.findViewById(R.id.edit_input_banji))).setText(MyUtils.nullStr(ac.getUser().getDepartment_name()));

    }
    void post_user(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        postJson=gson.toJson(userBean,UserBean.class);
        outTime=false;
        dialog=new ProgressDialog(ac);
        dialog.show();
        new Thread(thread_post).start();
    }
    void init_handler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Log.i("STAGE_STATUS",stage+"");
                    dialog.dismiss();
                    if (outTime) {
                        alert_string("服务器连接超时！请稍后重试");
                        userBean.setCompany_name(raw_company);
                        userBean.setDepartment_name(raw_department);
                        return;
                    }
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    UserBean lb = gson.fromJson(response, UserBean.class);
                    if (lb == null || !(lb.isChecked())) {
                        String str = "修改失败！";
                        if (lb != null) {
                            str += lb.getErr_str();
                        }
                        alert_string(str);
                        userBean.setCompany_name(raw_company);
                        userBean.setDepartment_name(raw_department);
                    } else {
                        ac.setUser(lb);
                        msg_string("修改成功!");
                        TextView signinfo = ac.getsView().findViewById(R.id.sign_info_text);
                        signinfo.setText(lb.getCompany_name() + lb.getDepartment_name()+"\n"+lb.getDepartment_address());
                        ac.setContentView(ac.getLoginView());
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

    void checkUserInfo(){
        info_checked=false;

        String xuexiao=((EditText) (v.findViewById(R.id.edit_input_xuexiao))).getText().toString().trim();
        String banji=((EditText) (v.findViewById(R.id.edit_input_banji))).getText().toString().trim();

        if(xuexiao==null ||xuexiao.trim().length()==0){
            alert_string("公司不能为空！");
            return;
        }

        if(banji==null ||banji.trim().length()==0){
            alert_string("部门不能为空！");
            return;
        }


        userBean=ac.getUser();
        raw_company=userBean.getCompany_name();
        raw_department=userBean.getDepartment_name();
        userBean.setDepartment_name(banji);
        userBean.setCompany_name(xuexiao);

        info_checked=true;
    }
}
