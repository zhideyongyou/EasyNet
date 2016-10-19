package xyz.yhsj.easynetdemo;

import android.app.Application;

import java.io.IOException;

import xyz.yhsj.easynet.EasyNet;

/**
 * Created by LOVE on 2016/9/7 007.
 */

public class MyAPP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        try {
            EasyNet.init(new EasyNet.Config(this)
                    .debug(true)
                    .cookie(true)
                    .setCertificates()      //方法二：也可以自己设置https证书（选一种即可）
                    .setBaseUrl("http://gank.io/")
                    .addInterceptor(new TestInterceptor())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
