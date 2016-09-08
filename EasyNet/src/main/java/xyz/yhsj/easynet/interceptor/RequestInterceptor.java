package xyz.yhsj.easynet.interceptor;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 请求拦截器，实现请求内容的拦截
 * 目标实现统一的数据预处理，如，加密请求，统一参数
 * Created by LOVE on 2016/9/7 007.
 */

public abstract class RequestInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Headers headers = chain.request().headers();

        Request request = parseData(chain.request());

        if (request == null) {
            return chain.proceed(chain.request());
        }

//        System.out.println(headers.toString());
//
//        Request request = chain.request()
//                .newBuilder()
//                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
//                .addHeader("Cookie", "add cookies here")
//                .build();
        return chain.proceed(request);
    }

    /**
     * 数据解析
     *
     * @param request 原始的请求
     * @return 修改后的请求
     */
    protected abstract Request parseData(Request request);

}




