package com.lin.annotationclick;

import android.content.Context;
import android.util.AttributeSet;

public interface IViewBuilder {
    public  Object getView(String name, Context context, AttributeSet attr);
}
