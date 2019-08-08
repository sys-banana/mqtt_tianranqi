package com.example.zhuang.mqtt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.zhuang.mqtt.service.IGetMessageCallBack;
import com.example.zhuang.mqtt.service.MQTTService;
import com.example.zhuang.mqtt.service.MyServiceConnection;
import com.yanzhenjie.permission.Permission;

public class TRQActivity extends AppCompatActivity implements IGetMessageCallBack {

    private RelativeLayout rl_search;
    private EditText et_imei;
    private Context context;
    private RelativeLayout rl_rq_change;
    private ImageView check;
    private boolean isOpen = true;
    private String message="12312312";
    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trq);
//
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(TRQActivity.this);
        Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        MyMqttService.startService(this);


        initView();
        initListener();
        initData();







    }

    private void initView(){
        rl_search=findViewById(R.id.rl_search);
        et_imei=findViewById(R.id.et_imei);
        rl_rq_change=findViewById(R.id.rl_rq_change);
        check=findViewById(R.id.check);

        context = TRQActivity.this;
    }

    private void initListener(){
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PermissionUtils().getInstance(context)
                        .permissions(Permission.CAMERA)
                        .errHint(getString(R.string.permission_name_camera))
                        .permission(permissions -> {
                            scan();
                        }).start();
            }
        });
        rl_rq_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MQTTService.publish(message);
                if (isOpen) {
                    isOpen = false;
                    check.setImageResource(R.drawable.icon_choose_open);
                }else {
                    isOpen = true;
                    check.setImageResource(R.drawable.icon_choose_close);
                }
            }
        });


    }

    private void initData(){
        message = et_imei.getText().toString().trim();
    }

    private void scan() {
        QrUtils.getInstance(context).startScan((Activity) context, result -> {
            Loggers.e("P2 Scan succeeded：" + result);
            et_imei.setText(result);
        });
    }

    @Override
    public void setMessage(String message) {
        et_imei.setText(message);
        mqttService = serviceConnection.getMqttService();
        mqttService.toCreateNotification(message);
    }


    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
