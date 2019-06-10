package com.lin.annotationclick;

import android.util.Log;
import android.view.View;

public class MyClickListener implements View.OnClickListener {
    private View.OnClickListener listener;
    public MyClickListener(View.OnClickListener listener){
        this.listener= listener;
    }
    @Override
    public void onClick(View v) {
        Log.e("lin","MyClickListener click");
        listener.onClick(v);
    }
}
