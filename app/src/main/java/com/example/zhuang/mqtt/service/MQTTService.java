package com.example.zhuang.mqtt.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.zhuang.mqtt.R;
import com.example.zhuang.mqtt.TRQActivity;
import com.example.zhuang.mqtt.utils.ToastUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTService extends Service {

    public static final String TAG = MQTTService.class.getSimpleName();

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private Context mContext;

    private String host = "tcp://140.207.48.130:2601";
    private String userName = "admin";
    private String passWord = "123456";
    private static String myTopic = "/LIGHT/xxxxxxxxxxxxxxx/REQ";      //要发布的主题
    private static String myTopic2 = "/LIGHT/xxxxxxxxxxxxxxx/RSP";      //要订阅的主题
    private static String myTopic3 = "/LIGHT/xxxxxxxxxxxxxxx/DATA";      //要订阅的主题
    private String clientId = "MQTT_FX_Client1";//客户端标识
    private IGetMessageCallBack IGetMessageCallBack;
    private NotificationManager notificationManager;
    private Notification notification;
    private Intent updateIntent;
    private PendingIntent pendingIntent;
    private int notification_id = 0;


    public static String getMyTopic() {
        return myTopic;
    }

    public static void setMyTopic(String myTopic) {
        MQTTService.myTopic = myTopic;
    }

    public static String getMyTopic2() {
        return myTopic2;
    }

    public static void setMyTopic2(String myTopic2) {
        MQTTService.myTopic2 = myTopic2;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getName(), "onCreate");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public static void publish(String msg){
        String topic = myTopic;
        Integer qos = 2;
        Boolean retained = false;
        try {
            if (client != null){
                client.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




    private void init() {
        // 服务器地址（协议+地址+端口号）
        String uri = host;
        client = new MqttAndroidClient(this, uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());     //将字符串转换为字符串数组

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        Log.e(getClass().getName(), "message是:" + message);
        String topic = myTopic;
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            // MQTT本身就是为信号不稳定的网络设计的，所以难免一些客户端会无故的和Broker断开连接。
            //当客户端连接到Broker时，可以指定LWT，Broker会定期检测客户端是否有异常。
            //当客户端异常掉线时，Broker就往连接时指定的topic里推送当时指定的LWT消息。

            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection();
        }

    }


    @Override
    public void onDestroy() {
        stopSelf();
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /** 连接MQTT服务器 */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNormal()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            Handler handlerThree=new Handler(Looper.getMainLooper());
            handlerThree.post(new Runnable(){
                public void run(){
                    ToastUtils.showToast(getApplicationContext(),"连接成功");
                }
            });
            try {
                // 订阅myTopic话题
                client.subscribe(myTopic2,2);
                client.subscribe(myTopic3,2);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连

            Handler handlerThree=new Handler(Looper.getMainLooper());
            handlerThree.post(new Runnable(){
                public void run(){
                    ToastUtils.showToast(getApplicationContext(),"连接失败");
                }
            });

        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            if (topic.contains("DATA")){
                if (IGetMessageCallBack != null){
                    IGetMessageCallBack.setMessage(str1,"data");
                }
            }else {
                if (IGetMessageCallBack != null) {
                    IGetMessageCallBack.setMessage(str1, "req");
                }
            }
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Log.i(TAG, "messageArrived:" + str1);
            Log.i(TAG, str2);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
        }
    };

    /** 判断网络是否连接 */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getName(), "onBind");
        myTopic = intent.getStringExtra("myTopic");
        myTopic2 = intent.getStringExtra("myTopic2");
        myTopic3 = intent.getStringExtra("myTopic3");
        clientId = intent.getStringExtra("clientId");
        return new CustomBinder();
    }

    public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack){
        this.IGetMessageCallBack = IGetMessageCallBack;
    }

    public class CustomBinder extends Binder {
        public MQTTService getService(){
            return MQTTService.this;
        }
    }

    RemoteViews contentView;

    public  void toCreateNotification(String message){
        String id = "channel_001";
        String name = "update";

        contentView = new RemoteViews(getPackageName(), R.layout.item_notification);
        contentView.setImageViewResource(R.id.notificationImage, R.mipmap.icon);
        contentView.setTextViewText(R.id.notificationTitle, getString(R.string.app_name));
        contentView.setTextViewText(R.id.notificationPercent, "0%");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setSmallIcon(R.mipmap.icon)
                    .setTicker("")
                    .setCustomContentView(contentView)
                    .build();
        } else {
            notification = new Notification();
            notification.icon = R.mipmap.icon;
            notification.tickerText = "";
            notification.contentView = contentView;
        }

        updateIntent = new Intent(this, TRQActivity.class);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        notification.contentIntent = pendingIntent;

        notificationManager.notify(notification_id, notification);

    }


}
