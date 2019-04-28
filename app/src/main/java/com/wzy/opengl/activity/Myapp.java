package com.wzy.opengl.activity;

import android.app.Application;
import android.content.Context;

/**
 * Time:2019/4/28
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class Myapp extends Application {
   public static Context app ;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }


}
