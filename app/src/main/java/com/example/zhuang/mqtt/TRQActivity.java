package com.example.zhuang.mqtt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuang.mqtt.service.IGetMessageCallBack;
import com.example.zhuang.mqtt.service.MQTTService;
import com.example.zhuang.mqtt.service.MyServiceConnection;
import com.example.zhuang.mqtt.utils.ToastUtils;
import com.yanzhenjie.permission.Permission;

import java.math.BigDecimal;

public class TRQActivity extends AppCompatActivity implements IGetMessageCallBack {

    private RelativeLayout rl_search;
    private EditText et_imei;
    private Context context;
    private RelativeLayout rl_rq_change;
    private ImageView check;
    private boolean isOpen = true;
    private String message = "12312312";
    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;
    private Button btn_connect_on;
    private Button btn_connect_off;
    private String myTopic2;
    private String myTopic;
    private String clientId;
    private String messageStatus;
    private Button btn_search;
    private TextView tv_bit0;
    private TextView tv_bit1;
    private TextView tv_bit2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trq);

        initView();
        initListener();

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
            } else {
                btn_connect_on.setEnabled(true);
            }

        }
    };


    private void initMQTT() {
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(TRQActivity.this);
        Intent intent = new Intent(this, MQTTService.class);
        intent.putExtra("myTopic2", myTopic2);
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

        et_imei.addTextChangedListener(mTextWatcher);


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


    }

    private void initData() {
        message = et_imei.getText().toString().trim();
        myTopic2 = "/LIGHT/" + message + "/RSP";
        myTopic = "/LIGHT/" + message + "/REQ";
        clientId = String.valueOf((Math.random() * 9 + 1) * 100000);
        Log.i("TRQActivity", clientId);
    }

    private void scan() {
        QrUtils.getInstance(context).startScan((Activity) context, result -> {
            Loggers.e("P2 Scan succeeded：" + result);
            et_imei.setText(result);
        });
    }

    @Override
    public void setMessage(String message) {
        if (message.equals("OK")&&isOpen){
            isOpen = false;
            tv_bit1.setTextColor(Color.GREEN);
            tv_bit1.setText("关闭");
            check.setImageResource(R.drawable.icon_choose_close);
        } else if (message.equals("OK")&&!isOpen) {
            isOpen = true;
            tv_bit1.setTextColor(Color.RED);
            tv_bit1.setText("开启");
            check.setImageResource(R.drawable.icon_choose_open);
        }

        if (message.contains("STATE")) {
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

    @CallSuper
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() ==  MotionEvent.ACTION_DOWN ) {
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
