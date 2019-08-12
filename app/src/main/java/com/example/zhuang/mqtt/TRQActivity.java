package com.example.zhuang.mqtt;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuang.mqtt.service.IGetMessageCallBack;
import com.example.zhuang.mqtt.service.MQTTService;
import com.example.zhuang.mqtt.service.MyServiceConnection;
import com.example.zhuang.mqtt.utils.Contants;
import com.example.zhuang.mqtt.utils.NotificationUtilsMe;
import com.example.zhuang.mqtt.utils.SharedPref;
import com.example.zhuang.mqtt.utils.ToastUtils;
import com.yanzhenjie.permission.Permission;


public class TRQActivity extends AppCompatActivity implements IGetMessageCallBack {

    private RelativeLayout rl_search;
    private EditText et_imei;
    private Context context;
    private RelativeLayout rl_rq_change;
    private ImageView check;
    private ImageView iv_clear;
    private boolean isOpen = true;
    private String message = "12312312";
    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;
    private Button btn_connect_on;
    private Button btn_connect_off;
    private String myTopic2;
    private String myTopic3;
    private String myTopic;
    private String clientId;
    private Button btn_search;
    private TextView tv_bit0;
    private TextView tv_bit1;
    private TextView tv_bit2;
    private Notification notify1;
    private Context mContext;
    private NotificationManager mNManager;
    private static final int NOTIFYID_1 = 1232414;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trq);

        initView();
        initListener();

        mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }

    }

    public void sendChatMsg(String message) {
        String exMesage1 = "";
        String exMesage2 = "";
        String exMesage3 = "";
        char bit0 = message.charAt(6);
        char bit1 = message.charAt(7);
        char bit2 = message.charAt(8);
        if (bit0 == '0') {
            exMesage1="燃气正常";
        } else {
            exMesage1="燃气泄露";
        }
        if (bit1 == '0') {
            exMesage2="阀门关闭";
        } else {
            exMesage2="阀门开启";
        }
        if (bit2 == '0') {
            exMesage3="设备正常";
        } else {
            exMesage3="设备故障";
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("chat");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
            }
        }
        Notification notification = new NotificationCompat.Builder(this, "chat")
                .setContentTitle("警告！控制器异常！")
                .setContentText(exMesage1+" | "+exMesage2+" | "+exMesage3)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String etimei = et_imei.getText().toString().trim();
            if (TextUtils.isEmpty(etimei)) {
                btn_connect_on.setEnabled(false);
                iv_clear.setVisibility(View.GONE);
            } else {
                btn_connect_on.setEnabled(true);
                iv_clear.setVisibility(View.VISIBLE);
            }

        }
    };


    private void initMQTT() {
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(TRQActivity.this);
        Intent intent = new Intent(this, MQTTService.class);
        intent.putExtra("myTopic2", myTopic2);
        intent.putExtra("myTopic3", myTopic3);
        intent.putExtra("myTopic", myTopic);
        intent.putExtra("clientId", clientId);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        rl_search = findViewById(R.id.rl_search);
        et_imei = findViewById(R.id.et_imei);
        rl_rq_change = findViewById(R.id.rl_rq_change);
        check = findViewById(R.id.check);
        btn_connect_on = findViewById(R.id.btn_connect_on);
        btn_connect_off = findViewById(R.id.btn_connect_off);
        btn_search = findViewById(R.id.btn_search);
        tv_bit0 = findViewById(R.id.tv_bit0);
        tv_bit1 = findViewById(R.id.tv_bit1);
        tv_bit2 = findViewById(R.id.tv_bit2);
        iv_clear = findViewById(R.id.iv_clear);

        et_imei.addTextChangedListener(mTextWatcher);

        if (SharedPref.getInstance().getData(Contants.IMEI) != null) {
            et_imei.setText(String.valueOf(SharedPref.getInstance().getData(Contants.IMEI)));
        } else {
            et_imei.setText("");
        }

        int color = Color.parseColor("#ff00ff");
        ColorDrawable drawable = new ColorDrawable(color);


        context = TRQActivity.this;
    }

    private void initListener() {
        btn_connect_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                initMQTT();

            }
        });
        btn_connect_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceConnection != null) {
                    Intent intent = new Intent(TRQActivity.this, MQTTService.class);
                    unbindService(serviceConnection);
                    serviceConnection = null;
                } else {
                    ToastUtils.showToast(TRQActivity.this, "请建立连接");
                }
            }
        });
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
                if (isOpen) {
                    MQTTService.publish("LIGHT:" + 0);
                } else {
                    MQTTService.publish("LIGHT:" + 1);
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MQTTService.publish("CHECK");
            }
        });

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_imei.setText("");
            }
        });


    }

    private void initData() {
        message = et_imei.getText().toString().trim();
        myTopic2 = "/LIGHT/" + message + "/RSP";
        myTopic3 = "/LIGHT/" + message + "/DATA";
        myTopic = "/LIGHT/" + message + "/REQ";
        clientId = String.valueOf((Math.random() * 9 + 1) * 100000);
        Log.i("TRQActivity", clientId);

        SharedPref.getInstance().saveData(Contants.IMEI, message);
    }

    private void scan() {
        QrUtils.getInstance(context).startScan((Activity) context, result -> {
            Loggers.e("P2 Scan succeeded：" + result);
            et_imei.setText(result);
        });
    }

    @Override
    public void setMessage(String message,String topic) {

        if (topic.equals("data")) {
//            mqttService = serviceConnection.getMqttService();
//            mqttService.toCreateNotification(message);
//            showOpenHistoryNotice(this,"警告","控制器发生泄露或检测到异常");
//            NotificationUtilsMe notificationUtilsMe = new NotificationUtilsMe(this);
//            notificationUtilsMe.sendNotification("zzz","zzzzzxczx");

            sendChatMsg(message);

        }

        if (message.equals("OK") && isOpen) {
            isOpen = false;
            tv_bit1.setTextColor(Color.GREEN);
            tv_bit1.setText("关闭");
            check.setImageResource(R.drawable.icon_choose_close);
        } else if (message.equals("OK") && !isOpen) {
            isOpen = true;
            tv_bit1.setTextColor(Color.RED);
            tv_bit1.setText("开启");
            check.setImageResource(R.drawable.icon_choose_open);
        }

        if (message.contains("STATE")) {
            ToastUtils.showToast(this, "数据更新成功");
            char bit0 = message.charAt(6);
            char bit1 = message.charAt(7);
            char bit2 = message.charAt(8);
            if (bit0 == '0') {
                tv_bit0.setText("正常");
                tv_bit0.setTextColor(Color.GREEN);
//                tv_bit0.setBackgroundColor(Color.GREEN);
            } else {
                tv_bit0.setText("泄露");
//                tv_bit0.setBackgroundColor(Color.RED);
                tv_bit0.setTextColor(Color.RED);
            }
            if (bit1 == '0') {
                tv_bit1.setText("关闭");
                isOpen = false;
                check.setImageResource(R.drawable.icon_choose_close);
//                tv_bit0.setBackgroundColor(Color.GREEN);
                tv_bit1.setTextColor(Color.GREEN);
            } else {
                tv_bit1.setText("开启");
                isOpen = true;
                check.setImageResource(R.drawable.icon_choose_open);
//                tv_bit0.setBackgroundColor(Color.RED);
                tv_bit1.setTextColor(Color.RED);
            }
            if (bit2 == '0') {
                tv_bit2.setText("正常");
//                tv_bit0.setBackgroundColor(Color.GREEN);
                tv_bit2.setTextColor(Color.GREEN);
            } else {
                tv_bit2.setText("故障");
//                tv_bit0.setBackgroundColor(Color.RED);
                tv_bit2.setTextColor(Color.RED);
            }
        }
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideKeyBord(view, ev)) {
                hideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定当前是否需要隐藏
     */
    protected boolean isShouldHideKeyBord(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
            //return !(ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
