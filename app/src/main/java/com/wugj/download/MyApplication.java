package com.wugj.download;

import android.app.Application;
import android.content.Context;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/8/27
 * </br>
 * version:
 */
public class MyApplication extends Application {
        public static Context sContext;//全局的Context对象

        @Override
        public void onCreate() {
            super.onCreate();
            sContext = this;
        }

}
