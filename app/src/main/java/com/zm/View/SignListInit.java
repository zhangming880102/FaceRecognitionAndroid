package com.zm.View;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zm.Bean.SignItemBean;
import com.zm.R;
import com.zm.Utils.MyProperty;

import java.util.List;

public class SignListInit extends ViewInit {
    @Override
    public void init_buttons(){
        v.findViewById(R.id.sign_list_button_fanhui).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ac.setContentView(ac.getsView());
            }
        }
        );
        init_list();
    }

    public void init_list(){
        LinearLayout signListView=(LinearLayout)((v.findViewById(R.id.sign_list)));
        signListView.removeAllViews();

        if(ac.getUser()==null|| ac.getUser().getSignList()==null || ac.getUser().getSignList().size()==0){
            return;
        }
        List<SignItemBean> signList=ac.getUser().getSignList();
        int num=signList.size();
        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100*num);
        signListView.setLayoutParams(lp_list);
        if(num>=10){
            LinearLayout scrolllinear=(LinearLayout)((v.findViewById(R.id.sign_list_scrolllinear)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100*(num+1));
            scrolllinear.setLayoutParams(lp);
            ((v.findViewById(R.id.sign_list_tianchong))).setVisibility(View.GONE);
        }else{
            LinearLayout tianchong=(v.findViewById(R.id.sign_list_tianchong));
            tianchong.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp_tianchong = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100*(10-num));
            tianchong.setLayoutParams(lp_tianchong);
        }
        for(int i=0;i<num;i++){
            SignItemBean item=signList.get(i);
            View oneSign=View.inflate(ac,R.layout.one_sign_item,null);
            LinearLayout.LayoutParams lp_item = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100);
            oneSign.setLayoutParams(lp_item);
            int newid=MyProperty.generateViewId();
            oneSign.setId(newid);
            TextView timev=oneSign.findViewById(R.id.sign_list_time);
            TextView addressv=oneSign.findViewById(R.id.sign_list_dizhi);
            TextView statusv=oneSign.findViewById(R.id.sign_list_status);
            timev.setText(item.getCreate_time());
            addressv.setText(item.getAddress());
            if(item.getSign_status()==1) {
                statusv.setText("签到成功");
            }else{
                statusv.setText("签到失败");
            }
            signListView.addView(oneSign);
        }
        signListView.setVisibility(View.VISIBLE);
    }
}
