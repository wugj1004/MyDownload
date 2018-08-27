package com.wugj.download.myOkhttp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wugj.download.MainActivity;
import com.wugj.download.R;

/**
 * Created by Administrator on 2016-6-19.
 * notification builder android
 */
public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;
    Notification.Builder builder;
    public NotificationUtil(Context context) {
        this.context = context;
        builder = new Notification.Builder(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    /**
     * 使用下载的Notification,在4.0以后才能使用<p></p>
     * Notification.Builder类中提供一个setProgress(int max,int progress,boolean indeterminate)方法用于设置进度条，
     * max用于设定进度的最大数，progress用于设定当前的进度，indeterminate用于设定是否是一个确定进度的进度条。
     * 通过indeterminate的设置，可以实现两种不同样式的进度条，一种是有进度刻度的（true）,一种是循环流动的（false）。
     */
    public void postDownloadNotification() {
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("showProgressBar").setContentInfo("contentInfo")
                .setOngoing(true).setContentTitle("标题")
                .setWhen(System.currentTimeMillis())
//                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText("通知内容");
    }

    public void setDownloadProcess(int progress){
        builder.setProgress(100, progress, false)
                .setContentText("下载进度"+progress+"%");
        notificationManager.notify(0, builder.build());

        if (progress == 100){
            builder.setContentTitle("下载完成")
                    .setProgress(0, 0, false).setOngoing(false);
        }
    }

    public void cancelById() {
        notificationManager.cancel(0);  //对应NotificationManager.notify(id,notification);第一个参数
    }

    public void cancelAllNotification() {
        notificationManager.cancelAll();
    }
}