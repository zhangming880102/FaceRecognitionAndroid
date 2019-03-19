package com.zm.View;

import android.view.View;
import android.widget.LinearLayout;

import com.zm.R;
import com.zm.Utils.MyProperty;

public class ToastViewSuccessInit extends ViewInit {
    @Override
    public void init_sizes(){
        View  n=v.findViewById(R.id.mytoast_suc_text_label);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MyProperty.width,(int)(MyProperty.height*0.075f));
        n.setLayoutParams(lp);
    }
}
