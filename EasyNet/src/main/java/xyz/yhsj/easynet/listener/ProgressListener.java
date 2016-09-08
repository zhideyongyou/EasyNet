package xyz.yhsj.easynet.listener;

/**
 * 文件上传回调
 * Created by LOVE on 2016/8/18 018.
 */

public interface ProgressListener {

    void onProgress(String fileName, long bytesWritten, long contentLength, boolean success);
}
