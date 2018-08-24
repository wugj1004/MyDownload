package com.wugj.download.myDownloadManager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

public class AppDownloadManager {
    public static final String TAG = "AppDownloadManager";
    private WeakReference<Activity> weakReference;
    private DownloadManager mDownloadManager;
    //下载进度观察
    private DownloadChangeObserver mDownLoadChangeObserver;
    //
    private DownloadReceiver mDownloadReceiver;
    private long mReqId;
    private OnUpdateListener mUpdateListener;

    static String FILE_NAME = "app_name.apk";
    public AppDownloadManager(Activity activity) {
        weakReference = new WeakReference<Activity>(activity);
        mDownloadManager = (DownloadManager) weakReference.get().getSystemService(Context.DOWNLOAD_SERVICE);
        mDownLoadChangeObserver = new DownloadChangeObserver(new Handler());
        mDownloadReceiver = new DownloadReceiver();
    }

    /**
     * 下载进度监听
     * @param mUpdateListener
     */
    public void setUpdateListener(OnUpdateListener mUpdateListener) {
        this.mUpdateListener = mUpdateListener;
        if (mDownLoadChangeObserver != null){
            mDownLoadChangeObserver.setUpdateListener(mUpdateListener);
        }else{
            Log.e(TAG,"先初始化下载");
        }
    }

    //apk文件下载
    public void downloadApk(String apkUrl, String title, String desc) {
        // fix bug : 装不了新版本，在下载之前应该删除已有文件
        File apkFile = new File(weakReference.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FILE_NAME);
        if (apkFile.exists()) {
            if (apkFile.delete()) {
                System.out.println("删除单个文件成功！");
            } else {
                System.out.println("删除单个文件失败！");
            }
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle(title);
        request.setDescription(desc);
        // 完成后显示通知栏
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(weakReference.get(), Environment.DIRECTORY_DOWNLOADS, FILE_NAME);
        //在手机SD卡上创建一个download文件夹
        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;
        //指定下载到SD卡的/download/my/目录下
        // request.setDestinationInExternalPublicDir("/codoon/","codoon_health.apk");

        request.setMimeType("application/vnd.android.package-archive");
        // 获取下载对应ID，mReqId由系统通过数据库持久化保存，开发过程中可通过sharedpreference保存mReqId，痛殴sharedPreference获取mReqId；
        // 以此解决重复下载，或者其他操作。
        mReqId = mDownloadManager.enqueue(request);

        mDownLoadChangeObserver.setDownloadManager(mDownloadManager);
        mDownLoadChangeObserver.setDownloadManagerEnqueueId(mReqId);
    }

    /**
     * 取消下载
     */
    public void cancel() {
        mDownloadManager.remove(mReqId);
    }

    /**
     * 对应 {@link Activity }
     */
    public void resume() {
        //设置监听Uri.parse("content://downloads/my_downloads")
        weakReference.get().getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true,
                mDownLoadChangeObserver);
        // 注册广播，监听APK是否下载完成
        weakReference.get().registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 对应{@link Activity#onPause()} ()}
     */
    public void onPause() {
        weakReference.get().getContentResolver().unregisterContentObserver(mDownLoadChangeObserver);
        weakReference.get().unregisterReceiver(mDownloadReceiver);
    }

    /**
     * 注册广播通知下载完成
     */
    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            boolean haveInstallPermission;
            // 兼容Android 8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //先获取是否有安装未知来源应用的权限
                haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {//没有权限
                    // 弹窗，并去设置页面授权
                    AndroidOInstallPermissionListener listener = new AndroidOInstallPermissionListener() {
                        @Override
                        public void permissionSuccess() {
                            installApk(context, intent);
                        }

                        @Override
                        public void permissionFail() {
                            Toast.makeText(context,"授权失败，无法安装应用",Toast.LENGTH_SHORT).show();
                        }
                    };

                    AndroidOPermissionActivity.sListener = listener;
                    Intent intent1 = new Intent(context, AndroidOPermissionActivity.class);
                    context.startActivity(intent1);

                } else {
                    installApk(context, intent);
                }
            } else {
                installApk(context, intent);
            }

        }
    }

    /**
     * 应用安装
     * @param context
     * @param intent
     */
    private void installApk(Context context, Intent intent) {
        long completeDownLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        Log.e(TAG, "收到广播");
        Uri uri;
        Intent intentInstall = new Intent();
        intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentInstall.setAction(Intent.ACTION_VIEW);

        if (completeDownLoadId == mReqId) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // 6.0以下
                uri = mDownloadManager.getUriForDownloadedFile(completeDownLoadId);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { // 6.0 - 7.0
                File apkFile = queryDownloadedApk(context, completeDownLoadId);
                uri = Uri.fromFile(apkFile);
            } else { // Android 7.0 以上
                uri = FileProvider.getUriForFile(context,
                        "com.wugj.download.fileProvider",
                        new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FILE_NAME));
                intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            // 安装应用
            Log.e("AppDownloadManager", "下载完成了");

            intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
            //调用安装界面
            context.startActivity(intentInstall);
        }
    }

    //通过downLoadId查询下载的apk，解决6.0以后安装的问题
    public static File queryDownloadedApk(Context context, long downloadId) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }



    public interface OnUpdateListener {
        void update(int currentByte, int totalByte);
    }

    public interface AndroidOInstallPermissionListener {
        void permissionSuccess();

        void permissionFail();
    }
}