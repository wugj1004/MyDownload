package com.wugj.download.myOkhttp.apkInstall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.wugj.download.R;

/**
 * Created by Administrator on 2016-6-19.
 * notification builder android
 */
public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;
    Notification.Builder builder;

    String channelId = "0";
    int notificationId = 0;
    public NotificationUtil(Context context) {
        this.context = context;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(context,channelId);
            notificationId = Integer.valueOf(channelId);
        }else{
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("showProgressBar").setContentInfo("contentInfo")
                .setOngoing(true).setContentTitle("标题")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText("通知内容");



    }
    /**
     * 使用下载的Notification,在4.0以后才能使用<p></p>
     * Notification.Builder类中提供一个setProgress(int max,int progress,boolean indeterminate)方法用于设置进度条，
     * max用于设定进度的最大数，progress用于设定当前的进度，indeterminate用于设定是否是一个确定进度的进度条。
     * 通过indeterminate的设置，可以实现两种不同样式的进度条，一种是有进度刻度的（true）,一种是循环流动的（false）。
     */

    public void setDownloadProcess(int progress){
        builder.setProgress(100, progress, false)
                .setContentText("下载进度"+progress+"%");

        if (progress == 100){
            builder.setContentTitle("下载完成")
                    .setProgress(0, 0, false).setOngoing(false);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    public void cancelById() {
        notificationManager.cancel(notificationId);  //对应NotificationManager.notify(id,notification);第一个参数
    }

    public void cancelAllNotification() {
        notificationManager.cancelAll();
    }
}