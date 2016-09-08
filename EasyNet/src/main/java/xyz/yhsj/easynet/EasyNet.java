package xyz.yhsj.easynet;

import android.content.Context;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import xyz.yhsj.easynet.cookie.CookieJarImpl;
import xyz.yhsj.easynet.cookie.store.MemoryCookieStore;
import xyz.yhsj.easynet.factory.ConverterFactoryPro;

/**
 * Created by LOVE on 2016/9/7 007.
 */

public class EasyNet {
    private Context mContext;
    private static EasyNet instance;
    private Retrofit retrofit;

    private boolean debug = false;

    public static EasyNet getInstance() {
        if (instance == null) {
            throw new RuntimeException("EasyNet not initialized!,should call EasyNet.init first");
        } else {
            return instance;
        }
    }

    public static void init(Builder builder) {
        instance = new EasyNet(builder);
    }

    private EasyNet(Builder builder) {
        this.mContext = builder.appContext;
        this.debug = builder.debug;

        if (debug) {
            builder.okhttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        builder.retrofitBuilder.client(builder.okhttpBuilder.build());

        retrofit = builder.retrofitBuilder.build();

    }

    public Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("EasyNet not initialized!should call EasyNet.init first");
        }
        return mContext;
    }

    public boolean isDebug() {
        return debug;
    }

    public <T> T create(Class<?> clazz) {

        Log.w("create", "create: " + retrofit);

        return (T) retrofit.create(clazz);
    }


    public static class Builder {
        private String bserUrl;
        private Context appContext;
        private OkHttpClient.Builder okhttpBuilder;

        private Retrofit.Builder retrofitBuilder;

        private boolean debug = false;
        private boolean isCookie = false;

        // 构建的步骤
        public Builder(Context appContext) {
            this.appContext = appContext;
            okhttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.addConverterFactory(ConverterFactoryPro.create());
            retrofitBuilder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }

        /**
         * debug
         * 用看来显示日志
         *
         * @param debug
         * @return
         */
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * cookie保持
         *
         * @param isCookie
         * @return
         */
        public Builder cookie(boolean isCookie) {
            this.isCookie = isCookie;

            okhttpBuilder.cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
            return this;
        }

        /**
         * 基础 url
         *
         * @param bserUrl
         * @return
         */
        public Builder setBaseUrl(String bserUrl) {
            this.bserUrl = bserUrl;
            retrofitBuilder.baseUrl(bserUrl);
            return this;
        }

        /**
         * 拦截器
         *
         * @param interceptor
         * @return
         */
        public Builder addInterceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(interceptor);
            return this;
        }

    }

}
