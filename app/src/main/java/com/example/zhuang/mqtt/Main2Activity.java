package com.example.zhuang.mqtt;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(Main2Activity.this, TRQActivity.class); //前者为跳转前页面，后者为跳转后页面
                Main2Activity.this.startActivity(mainIntent);
                Main2Activity.this.finish();
            }
        }, 2500); //设置时间，2.5秒后自动跳转 

    }
}
