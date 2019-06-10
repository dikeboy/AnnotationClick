package com.lin.annotationclick;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Constructor;

/**
 * author : leo
 * date   : 2018/12/2620:14
 */
public class InjectHelper {

    public static IViewBuilder initViewBuilder() {
        // 1、
        String classFullName = "com.lin.annotationclick.VovaViewBuilder";
        try {
            // 2、
            Class proxy = Class.forName(classFullName);

            IViewBuilder builder = (IViewBuilder)proxy.newInstance();
            // 3、
            return builder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}

