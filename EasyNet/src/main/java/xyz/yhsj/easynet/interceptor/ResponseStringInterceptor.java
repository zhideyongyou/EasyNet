package xyz.yhsj.easynet.interceptor;

import android.text.TextUtils;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import xyz.yhsj.easynet.EasyNet;


/**
 * String类型返回值拦截器，仅实现String类型的拦截
 * 目标实现统一的数据预处理，如，解密数据
 * Created by LOVE on 2016/9/7 007.
 */

public abstract class ResponseStringInterceptor implements Interceptor {

    private final String TAG = this.getClass().getName();

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Response response = null;

        response = chain.proceed(request);


        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        //注意 >>>>>>>>> okhttp3.4.1这里变成了
        // !HttpHeader.hasBody(response)
        // !HttpEngine.hasBody(response)
        if (!HttpHeaders.hasBody(response)) {
            // END HTTP
        } else if (bodyEncoded(response.headers())) {
            //HTTP (encoded body omitted)
        } else {
            BufferedSource source = responseBody.source();

            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();

            // Log.e(">>>", "contentType>>>" + contentType);

            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    //Couldn't decode the response body; charset is likely malformed.
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                Log.e(">>>", "<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                return response;
            }

            if (contentLength != 0) {

                //获取到response的body的string字符串
                //do something .... 这段代码可以用来改变返回值

                String result = buffer.clone().readString(charset);

                String newResult = parseData(contentType, result);

                if (!TextUtils.isEmpty(newResult)) {

                    if (EasyNet.getInstance().isDebug()) {
                        Log.e(TAG, "拦截器处理数据");
                    }

                    ResponseBody resultResponseBody = ResponseBody.create(contentType, newResult);

                    return response.newBuilder().body(resultResponseBody).build();
                }
            }

            if (EasyNet.getInstance().isDebug()) {
                Log.e(TAG, "拦截器未处理数据");
            }
        }
        return response;
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }


    /**
     * 数据解析
     *
     * @param contentType 数据类型
     * @param result      数据
     * @return 解析后的数据
     */
    protected abstract String parseData(MediaType contentType, String result);
}


