package com.example.zhuang.mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private NotificationManager manager;
    private Notification notification;
    Bitmap LargeBitmap = null;
    private static final int NOTIFYID = 1;


    private Button btn_show_normal;
    private Button btn_close_normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        LargeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        bindView();
    }

    private void bindView(){
        btn_show_normal = (Button) findViewById(R.id.btn_show_normal);
        btn_close_normal = (Button) findViewById(R.id.btn_close_normal);
        btn_show_normal.setOnClickListener(this);
        btn_close_normal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show_normal:
                //定义一个PendingIntent点击Notification后启动一个Activity
                Intent it = new Intent(context,Main2Activity.class);
                PendingIntent pit = PendingIntent.getActivity(context,0,it,0);

                //设置图片,通知标题,发送时间,提示方式等属性
                Notification.Builder builder = new Notification.Builder(this);
                builder.setContentTitle("zhuang")
                        .setContentText("wo shinei rong")
                        .setSubText("——记住我叫叶良辰")
                        .setTicker("收到叶良辰发送过来的信息~")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(LargeBitmap)
                        .setAutoCancel(true)
                        .setContentIntent(pit);

                notification = builder.build();
                manager.notify(NOTIFYID,notification);
                break;

            case R.id.btn_close_normal:
                //除了可以根据ID来取消Notification外,还可以调用cancelAll();关闭该应用产生的所有通知
                manager.cancel(NOTIFYID);
                break;

        }
    }
}
