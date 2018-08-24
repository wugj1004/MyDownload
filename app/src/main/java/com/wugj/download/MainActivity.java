package com.wugj.download;
/**
 * 8.0系统在没有打开开发者模式的前提下，安装app必须要有签名文件
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wugj.download.myDownloadManager.AppDownloadManager;

public class MainActivity extends AppCompatActivity {

    String title = "app name";
    String desc = "版本更新";
    String download_url = "https://hz-app.oss-cn-beijing.aliyuncs.com/update/zhushou/non_custom/officeb-release-4.2.1.apk";

    String Tag = MainActivity.class.getSimpleName();
    MainActivity instance;

    AppDownloadManager mDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        mDownloadManager = new AppDownloadManager(instance);

        //downloadManager-更新
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadManagerDialog();
            }
        });

        //okhttp3下载-更新
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOkHttpDownloadDialog();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDownloadManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDownloadManager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //每次退出页面就会取消下载操作
        mDownloadManager.cancel();
    }


    private void showDownloadManagerDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("提示");
        builder.setMessage("版本更新提示");
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDownloadManager.setUpdateListener(new AppDownloadManager.OnUpdateListener() {
                    @Override
                    public void update(int currentByte, int totalByte) {
                        Log.e(Tag,"currentByte:"+currentByte+";totalByte:"+totalByte);
                        if ((currentByte == totalByte) && totalByte != 0) {
                            //加载完毕
                            showToast("下载完成");
                        }
                    }
                });
                mDownloadManager.downloadApk(download_url, title, desc);


            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDownloadManager.cancel();
                showToast("取消");
            }
        });
        builder.show();
    }






    private void showOkHttpDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("提示");
        builder.setMessage("版本更新提示");
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("取消");
            }
        });
        builder.show();
    }







    private void showToast(String msg){
        Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
    }
}
