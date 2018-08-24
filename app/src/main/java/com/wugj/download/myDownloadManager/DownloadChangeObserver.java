package com.wugj.download.myDownloadManager;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

/**
 * description:观察者观察更新下载进度
 * </br>
 * author: wugj
 * </br>
 * date: 2018/8/25
 * </br>
 * version:
 */
public class DownloadChangeObserver  extends ContentObserver {

    //每次下载对应渠道id
    private long downloadManagerEnqueueId;
    //系统现在管理类
    DownloadManager mDownloadManager;
    //下载进度监听
    AppDownloadManager.OnUpdateListener mUpdateListener;

    String TAG = DownloadChangeObserver.class.getSimpleName();

    public DownloadChangeObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        updateView();
    }
    //更新下载进度
    private void updateView() {
        int[] bytesAndStatus = new int[]{0, 0, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadManagerEnqueueId);
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                //已经下载的字节数
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //状态所在的列索引
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        //下载进度回调
        if (mUpdateListener != null) {
            mUpdateListener.update(bytesAndStatus[0], bytesAndStatus[1]);
        }

        Log.i(TAG, "下载进度：" + bytesAndStatus[0] + "/" + bytesAndStatus[1] + "");
    }



    protected void setDownloadManagerEnqueueId(long mReqId){
        this.downloadManagerEnqueueId = mReqId;
    }

    protected void setDownloadManager(DownloadManager mDownloadManager){
        this.mDownloadManager = mDownloadManager;
    }


    protected void setUpdateListener(AppDownloadManager.OnUpdateListener mUpdateListener){
        this.mUpdateListener = mUpdateListener;
    }
}