package xyz.yhsj.easynet.request;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.yhsj.easynet.listener.ProgressListener;

/**
 * 上传文件的接口，包括上传图片，多图上传，图文混合上传
 * Created by LOVE on 2016/8/24 024.
 */

public class UploadFileMap implements Serializable {
    private HashMap<String, RequestBody> baseParam;
    private ProgressListener listener;

    public UploadFileMap(@NonNull ProgressListener listener) {
        baseParam = new HashMap<>();
        this.listener = listener;
    }

    public UploadFileMap() {
        baseParam = new HashMap<>();
    }


    public UploadFileMap addParams(String key, String value) {
        baseParam.put(key, UploadFileRequestBody.create(MediaType.parse("Accept: text/html, application/xhtml+xml, image/jxr, */*"), value));
        return this;
    }

    public UploadFileMap addFile(String key, File file) {
        baseParam.put(key + "\"; filename=\"" + file.getName(), new UploadFileRequestBody(file, listener));
        return this;
    }

    public UploadFileMap addFile(File file) {
        baseParam.put("file[]\"; filename=\"" + file.getName(), new UploadFileRequestBody(file, listener));
        return this;
    }

    public HashMap<String, RequestBody> build() {
        return baseParam;
    }
}
