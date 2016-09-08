package xyz.yhsj.easynet.request;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import xyz.yhsj.easynet.listener.ProgressListener;


/**
 * 文件上传实体，回调
 * Created by LOVE on 2016/8/18 018.
 */

public class UploadFileRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private ProgressListener mProgressListener;

    private BufferedSink bufferedSink;
    private String file_name = "";


    public UploadFileRequestBody(File file, ProgressListener progressListener) {

        if (file.exists()) {
            this.mRequestBody = RequestBody.create(MediaType.parse("Accept: text/html, application/xhtml+xml, image/jxr, */*"), file);
            this.mProgressListener = progressListener;
            this.file_name = file.getName();
        } else {
            throw new NullPointerException("File not found");
        }
    }

    public UploadFileRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.mRequestBody = requestBody;
        this.mProgressListener = progressListener;
    }

    //返回了requestBody的类型，想什么form-data/MP3/MP4/png等等等格式
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    //返回了本RequestBody的长度，也就是上传的totalLength
    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        mRequestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调上传接口
                if (mProgressListener != null) {
                    mProgressListener.onProgress(file_name, bytesWritten, contentLength, bytesWritten == contentLength);
                }
                super.write(source, byteCount);
            }
        };
    }
}