package com.wugj.download.myOkhttp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/** 
 * description: Okhttp3简单实现下载更新
 * </br>
 * autour: wugj
 * </br>
 * date: 2018/8/25 上午8:16
 * </br>
 * version: 
 */
public class OkHttpDownloadInstallManager {

    private WeakReference<Activity> weakReference;
    private final OkHttpClient okHttpClient;
    Call callRequest;
    static String FILE_NAME = "app_name.apk";

    private OkHttpDownloadInstallManager(Activity activity) {
        weakReference = new WeakReference<Activity>(activity);
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url 下载连接
     * @param listener 下载监听
     */
    private void download(final String url, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        callRequest = okHttpClient.newCall(request);
        callRequest.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                    installAPk();
                } catch (Exception e) {
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private void installAPk(){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri contentUri;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){// 6.0以下
            contentUri =Uri.fromFile(new File(Environment.getExternalStorageDirectory(), FILE_NAME));
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){//7.0以上
                contentUri = FileProvider.getUriForFile(weakReference.get(),
                        "com.wugj.download.fileProvider",
                        new File(Environment.DIRECTORY_DOWNLOADS, FILE_NAME));//注意修改
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }else{//6.0-7.0
                contentUri =Uri.fromFile(new File(Environment.DIRECTORY_DOWNLOADS, FILE_NAME));
            }
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        //调用安装界面
        weakReference.get().startActivity(intent);


    }
    /**
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir() throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.DIRECTORY_DOWNLOADS, FILE_NAME);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 取消下载
     */
    public void cancel() {
        if (callRequest != null) {
            callRequest.cancel();
        }
    }
    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}
