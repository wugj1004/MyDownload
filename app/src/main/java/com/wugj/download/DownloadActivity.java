package com.wugj.download;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wugj.download.myOkhttp.DownLoadObserver;
import com.wugj.download.myOkhttp.DownloadInfo;
import com.wugj.download.myOkhttp.OkHttpDownloadManager;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/8/27
 * </br>
 * version:
 */
public class DownloadActivity extends Activity  implements View.OnClickListener{

    ProgressBar main_progress1,main_progress2,main_progress3;
    Button main_btn_down1,main_btn_down2,main_btn_down3;
    Button main_btn_cancel1,main_btn_cancel2,main_btn_cancel3;
    TextView main_text1,main_text2,main_text3;

    private String url1 = "https://hz-app.oss-cn-beijing.aliyuncs.com/update/zhushou/non_custom/officeb-release-4.3.1.apk";
    private String url2 = "https://app-cdn.haozu.com/update/space_one/install/android/SpaceOne-release-2.0.apk";
    private String url3 = "https://hz-app.oss-cn-beijing.aliyuncs.com/update/zhushou/non_custom/officeb-release-4.3.1.apk";

    DownloadActivity instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_file);
        instance = this;
        initView();
    }

    private void initView() {
        main_progress1 = findViewById(R.id.main_progress1);
        main_progress2 = findViewById(R.id.main_progress2);
        main_progress3 = findViewById(R.id.main_progress3);

        main_btn_down1 = findViewById(R.id.main_btn_down1);
        main_btn_down2 = findViewById(R.id.main_btn_down2);
        main_btn_down3 = findViewById(R.id.main_btn_down3);

        main_btn_cancel1 = findViewById(R.id.main_btn_cancel1);
        main_btn_cancel2 = findViewById(R.id.main_btn_cancel2);
        main_btn_cancel3 = findViewById(R.id.main_btn_cancel3);

        main_text1 = findViewById(R.id.main_text1);
        main_text2 = findViewById(R.id.main_text2);
        main_text3 = findViewById(R.id.main_text3);

        main_btn_down1.setOnClickListener(this);
        main_btn_down2.setOnClickListener(this);
        main_btn_down3.setOnClickListener(this);
        main_btn_cancel1.setOnClickListener(this);
        main_btn_cancel2.setOnClickListener(this);
        main_btn_cancel3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_btn_down1:
                OkHttpDownloadManager.getInstance(instance).download(url1, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        main_progress1.setMax((int) value.getTotal());
                        main_text1.setText((value.getProgress()*100)/(value.getTotal()) + "%");
                        main_progress1.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(instance,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_down2:
                OkHttpDownloadManager.getInstance(instance).download(url2, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        main_progress2.setMax((int) value.getTotal());

                        main_text2.setText((value.getProgress()*100)/(value.getTotal()) + "%");
                        main_progress2.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(instance,
                                    downloadInfo.getFileName() + "下载完成",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                break;
            case R.id.main_btn_down3:
                OkHttpDownloadManager.getInstance(instance).download(url3, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        main_progress3.setMax((int) value.getTotal());
                        main_text3.setText((value.getProgress()*100)/(value.getTotal()) + "%");
                        main_progress3.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(instance,
                                    downloadInfo.getFileName() + "下载完成",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                break;
            case R.id.main_btn_cancel1:
                OkHttpDownloadManager.getInstance(instance).cancel(url1);
                break;
            case R.id.main_btn_cancel2:
                OkHttpDownloadManager.getInstance(instance).cancel(url2);
                break;
            case R.id.main_btn_cancel3:
                OkHttpDownloadManager.getInstance(instance).cancel(url3);
                break;

        }
    }
}
