package xyz.yhsj.easynet;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
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
import xyz.yhsj.easynet.https.HttpsUtils;

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

            //默认开启htps
            okhttpBuilder.hostnameVerifier(new DefaultHostnameVerifier());
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

            if (debug) {
                okhttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

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
         * https的全局访问规则
         */
        public Config setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            okhttpBuilder.hostnameVerifier(hostnameVerifier);
            return this;
        }

        /**
         * https的全局自签名证书
         */
        public Config setCertificates(InputStream... certificates) {
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
            okhttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            return this;
        }

        /**
         * https双向认证证书
         */
        public Config setCertificates(InputStream bksFile, String password, InputStream... certificates) {
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
            okhttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
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

        /**
         * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
         * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
         * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
         */
        private class DefaultHostnameVerifier implements HostnameVerifier {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }
    }
}
