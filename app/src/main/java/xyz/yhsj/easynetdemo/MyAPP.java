package xyz.yhsj.easynetdemo;

import android.app.Application;

import xyz.yhsj.easynet.EasyNet;

/**
 * Created by LOVE on 2016/9/7 007.
 */

public class MyAPP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        EasyNet.init(new EasyNet.Builder(this)
                .debug(true)
                .setBaseUrl("http://gank.io/")
                .addInterceptor(new TestInterceptor())
        );


    }
}