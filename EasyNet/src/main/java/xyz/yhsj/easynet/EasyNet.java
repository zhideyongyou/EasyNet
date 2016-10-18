package xyz.yhsj.easynet;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import xyz.yhsj.easynet.cookie.CookieJarImpl;
import xyz.yhsj.easynet.cookie.store.PersistentCookieStore;
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

    public static void init(Config config) {
        instance = new EasyNet(config);
    }

    private EasyNet(Config config) {
        this.mContext = config.appContext;
        this.debug = config.debug;

        if (debug) {
            config.okhttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        config.retrofitBuilder.client(config.okhttpBuilder.build());

        retrofit = config.retrofitBuilder.build();

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

        if (debug) {
            Log.w("create", "create: " + retrofit);
        }

        return (T) retrofit.create(clazz);
    }


    public static class Config {
        private String bserUrl;
        private Context appContext;
        private OkHttpClient.Builder okhttpBuilder;

        private Retrofit.Builder retrofitBuilder;

        private boolean debug = false;
        private boolean isCookie = false;

        // 构建的步骤
        public Config(Context appContext) {
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
        public Config debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * cookie保持
         *
         * @param isCookie
         * @return
         */
        public Config cookie(boolean isCookie) {
            this.isCookie = isCookie;

            if (isCookie) {
                okhttpBuilder.cookieJar(new CookieJarImpl(new PersistentCookieStore(appContext)));
            }
            return this;
        }

        /**
         * https
         *
         * @param isSsl
         * @return
         */
        public Config ssl(boolean isSsl) {
            if (isSsl) {
                okhttpBuilder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            return this;
        }

        /**
         * 连接超时
         *
         * @param time 秒
         * @return
         */
        public Config connectTimeout(long time) {
            okhttpBuilder.connectTimeout(time, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 读超时
         *
         * @param time 秒
         * @return
         */
        public Config readTimeout(long time) {
            okhttpBuilder.readTimeout(time, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 写超时
         *
         * @param time 秒
         * @return
         */
        public Config writeTimeout(long time) {
            okhttpBuilder.writeTimeout(time, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 基础 url
         *
         * @param bserUrl
         * @return
         */
        public Config setBaseUrl(String bserUrl) {
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
        public Config addInterceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(interceptor);
            return this;
        }

    }

}
