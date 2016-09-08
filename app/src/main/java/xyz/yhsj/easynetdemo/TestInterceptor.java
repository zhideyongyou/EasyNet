package xyz.yhsj.easynetdemo;

import okhttp3.MediaType;
import xyz.yhsj.easynet.interceptor.ResponseStringInterceptor;


/**
 * 自定义返回值拦截器
 * Created by LOVE on 2016/9/7 007.
 */

public class TestInterceptor extends ResponseStringInterceptor {


    @Override
    protected String parseData(MediaType contentType, String result) {

        return "";
    }
}


