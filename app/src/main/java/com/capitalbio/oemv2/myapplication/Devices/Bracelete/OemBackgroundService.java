package com.capitalbio.oemv2.myapplication.Devices.Bracelete;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.capitalbio.oemv2.myapplication.Activity.AirCatActivity;
import com.capitalbio.oemv2.myapplication.Activity.LoginActivity;
import com.capitalbio.oemv2.myapplication.Base.Const;
import com.capitalbio.oemv2.myapplication.Base.IBloodPressDevicesCallBack;
import com.capitalbio.oemv2.myapplication.Base.IBodyFatDevicesCallBack;
import com.capitalbio.oemv2.myapplication.Base.IConnectionStateChange;
import com.capitalbio.oemv2.myapplication.Base.IDeviceSearchCallBack;
import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.Base.OemBleConnectThread;
import com.capitalbio.oemv2.myapplication.Bean.AircatDevice;
import com.capitalbio.oemv2.myapplication.BraceleteLib.BraceleteDevices;
import com.capitalbio.oemv2.myapplication.Const.AircatConst;
import com.capitalbio.oemv2.myapplication.Const.Base_Url;
import com.capitalbio.oemv2.myapplication.Devices.BloodPressDevice.BloodPressDevices;
import com.capitalbio.oemv2.myapplication.Devices.BodyFatDevices.src.com.xtremeprog.sdk.ble.BodyFatDevices;
import com.capitalbio.oemv2.myapplication.Devices.Bracelete.BraceleteCommand.GetSleepDetailBraceleteCommand;
import com.capitalbio.oemv2.myapplication.Devices.Bracelete.BraceleteCommand.SetCallNameBraceleteCommand;
import com.capitalbio.oemv2.myapplication.Devices.Bracelete.BraceleteCommand.SetSMSNumBraceleteCommand;
import com.capitalbio.oemv2.myapplication.NetUtils.HttpTools;
import com.capitalbio.oemv2.myapplication.R;
import com.capitalbio.oemv2.myapplication.Utils.OemLog;
import com.capitalbio.oemv2.myapplication.Utils.PreferencesUtils;
import com.capitalbio.oemv2.myapplication.Utils.TimeStampUtil;
import com.capitalbio.oemv2.myapplication.Utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class OemBackgroundService extends Service {

    public static final String TAG = "OemBackgroundService";

    public static final int HOUR_24 = 1000 * 60  * 60 * 24;

    public static final int FOUND_OTHER_DEVICES_NOTIFY = 50001;
    public static final int FOUND_OTHER_DEVICES_NOTIFY_END = 50003;

    public static final int BODYFAT_CONNECT_TIMEOUT_MESSAGE = 9700;
    public static final int BLE_AUTO_SEARCH_COMEPLETE = 9500;

    public static final int CONNECT_BLOODPRESS_DEVICES_SUCCESSFULL = 7200;
    public static final int CONNECT_BODYFAT_DEVICES_SUCCESSFULL = 7201;

    public static final int BLOOD_PRESS_SHUTDOWN_DELAY_MESSAGE = 7202;
    public static final int BLOOD_PRESS_SHUTDOWN_DELAY_TIME = 2000;

    public static final int NOTIFY_DEVICES_TYPE_BLOODPRESS = 0;
    public static final int NOTIFY_DEVICES_TYPE_BODYFAT = 1;
    private AutoConnectThread autoConnectThread;
    private CommandExecuteThread commandExecuteThread;
    private SyncThread syncThread;
    private UploadOperation uploadOperation;
    private GetRealTimeSportDataThread getRealTimeSportDataThread;
    private OemBleConnectThread oemBleConnectThread;

    private BraceleteDevices braceleteDevices;
    private BloodPressDevices bloodPressDevices;
    private BodyFatDevices bodyFatDevices;

    private OemBackgroundServicesBindrer mBinder;

    private BluetoothAdapter bluetoothAdapter;
    private final IntentFilter mAdapterIntentFilter = new IntentFilter();

    private NotificationManager nm;

    //取睡眠数据等待时间
    private long syncSleepWaitTime;

    //当前时间距离次日八点的时间
    private long nextDayTimstamp;

    BroadcastReceiver rcvIncoming = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("telephony", "SMS received");
            Bundle data = intent.getExtras();
            if (data != null) {
                // SMS uses a data format known as a PDU
                Object pdus[] = (Object[]) data.get("pdus");
                String message = "New message:\n";
                String sender = null;
                for (Object pdu : pdus) {
                    SmsMessage part = SmsMessage.createFromPdu((byte[])pdu);
                    message += part.getDisplayMessageBody();
                    if (sender == null) {
                        sender = part.getDisplayOriginatingAddress();
                    }
                }
                //根据用户设置确定是不是提醒短信
                boolean isSendSmsNotify = PreferencesUtils.getBoolean(MyApplication.getInstance(), "msgWarn", false);
                if (braceleteDevices != null && isSendSmsNotify) {
                    braceleteDevices.addCommandToQueue(new SetSMSNumBraceleteCommand(1));
                }
                OemLog.log(TAG, "receive message message is " + message);
            }
        }
    };




    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            OemLog.log(TAG, "devices call back message " + msg.what);
            switch (msg.what) {
                case Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE:
                    OemLog.log(TAG, "before enter lock11111");
                    if (commandExecuteThread != null) {
                        synchronized (commandExecuteThread) {
                            commandExecuteThread.setThreadStaus(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE);
                            commandExecuteThread.notify();
                        }
                    }
                    OemLog.log(TAG, "out of lock11111");
                    break;

                case Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMMAND_ADD:
                    OemLog.log(TAG, "before enter lock2222");
                    if (commandExecuteThread != null) {
                        synchronized (commandExecuteThread) {
                            commandExecuteThread.notify();
                        }
                    }
                    OemLog.log(TAG, "out of lock2222");
                    break;

                case Const.BRACELETE_COMMAND_EXECUTE_STATUS_DISCONNECTED:
                    OemLog.log(TAG, "before enter lock1111");
                        if (autoConnectThread.isAlive()) {
                            return;
                        } else {
                            OemLog.log(TAG, "start new thread to connect bracelete...");
                            if (hasMessages(Const.BRACELETE_COMMAND_EXECUTE_STATUS_DISCONNECTED)) {
                                removeMessages(Const.BRACELETE_COMMAND_EXECUTE_STATUS_DISCONNECTED);
                            }
                            /*autoConnectThread = new AutoConnectThread();
                            autoConnectThread.start();*/
                        }
                    OemLog.log(TAG, "out of lock2222");
                    break;
                case Const.NETWORK_MESSAGE_LOGOUT:
                    if (PreferencesUtils.getBoolean(MyApplication.getInstance(), "isLogin")) {
                      //  Toast.makeText(OemBackgroundService.this, "您的帐号在另一台手机登录，请重新登录！", Toast.LENGTH_SHORT).show();

                        MyApplication.getInstance().exit();
                        Intent loginIntent = new Intent(OemBackgroundService.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                        braceleteDevices.disconnect();
                        PreferencesUtils.putString(MyApplication.getInstance(), "default_bracelete_address", "");
                        PreferencesUtils.putBoolean(OemBackgroundService.this, "connect_state", false);
                        PreferencesUtils.putBoolean(OemBackgroundService.this, "pre_connect_state", false);
                        PreferencesUtils.putBoolean(OemBackgroundService.this, "bracelete_init_complete", false);
                        PreferencesUtils.putBoolean(MyApplication.getInstance(), "isLogin", false);
                    }
                    break;
                case Const.OEM_SERVECES_BLUETOOTH_ON_CONNECT_DELAY:
                    break;
                case com.capitalbio.oemv2.myapplication.Const.Const.BLOOD_PRESS_MEASURE_COMMPLETE_DISSCONNECT:
                    OemLog.log(TAG, "receive bloodpress delay message and set measure state...");
                    //断开连接
                    bloodPressDevices.disconnectGatt();
                    braceleteDevices.setIsBloodPressMeasuring(false);
                    MyApplication.getInstance().setIsDevicesMeasuring(false);
                    break;
                case Const.BRACELETE_AUTO_SYNC_SLEEP_BROADCASE:
                    braceleteDevices.addCommandToQueue(new GetSleepDetailBraceleteCommand());
                    break;
                case Const.BODYFAT_GATT_DISCONNECT_MESSAGE:
                    MyApplication.getInstance().setIsDevicesMeasuring(false);
                    braceleteDevices.setIsBodyFatMeasuring(false);
                    break;
                case Const.UPLOAD_DATA_DELAY_MESAGE:
                    uploadOperation.uploadData();
                    break;

                case Const.BRACELETE_AUTO_CONNECT_DELAY_MESSAGE:
                    break;
                case FOUND_OTHER_DEVICES_NOTIFY:
                    //发现其他设备, 停止手环数据获取
                    getRealTimeSportDataThread.nofifyTmpPause();
                    Toast.makeText(MyApplication.getInstance(), "发现设备, 正在连接中...", Toast.LENGTH_SHORT).show();
                    break;

                case FOUND_OTHER_DEVICES_NOTIFY_END:
                    //发现其他设备, 停止手环数据获取
                    getRealTimeSportDataThread.notifyOtherDevicesMeasureComeplete();
                    break;


                case BLE_AUTO_SEARCH_COMEPLETE:
                    BraceleteDevices.getInstance().stopBleScan();
                    oemBleConnectThread.notifyAutoSearchComeplete();

                    break;

                case BODYFAT_CONNECT_TIMEOUT_MESSAGE:
                    braceleteDevices.setIsBodyFatMeasuring(false);
                    break;
                case CONNECT_BLOODPRESS_DEVICES_SUCCESSFULL:
                    Toast.makeText(MyApplication.getInstance(), "连接成功,开始测量...", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_BODYFAT_DEVICES_SUCCESSFULL:
                    Toast.makeText(MyApplication.getInstance(), "连接成功,开始测量...", Toast.LENGTH_SHORT).show();
                    break;

                default:break;


            }
        }
    };


    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            OemLog.log("TimeTask", "TimeTask message...");
            handler.sendEmptyMessage(Const.BRACELETE_AUTO_SYNC_SLEEP_BROADCASE);
        }
    };


    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OemLog.log(TAG, "receiver intent action is " + intent.getAction());
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    //收到蓝牙打开的广播，开启自动连接线程
                    /*if (autoConnectThread.isAlive()) {
                        return;
                    } else {
                        autoConnectThread = new AutoConnectThread();
                        autoConnectThread.start();
                    }*/
                } else if (state == BluetoothAdapter.STATE_OFF){
                    PreferencesUtils.putBoolean(context, "connect_state", false);
                    PreferencesUtils.putBoolean(context, "pre_connect_state", false);
                }
            } else if (Intent.ACTION_TIME_TICK.equals(action) && PreferencesUtils.getBoolean(MyApplication.getInstance(), "isLogin") && MyApplication.getInstance().getCurrentUser() != null) {

                //检查后台服务是否在运行，如停止运行则开启服务
                if (!Utils.isOemServicesRunning()) {
                    OemLog.log(TAG, "services is not running and start the oemservices...");
                    Intent startServicesIntent = new Intent(MyApplication.getInstance(), OemBackgroundService.class);
                    MyApplication.getInstance().startService(startServicesIntent);
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("apikey", MyApplication.getInstance().apikey);
                    jsonObject.put("username", MyApplication.getInstance().getCurrentUserName());
                    jsonObject.put("token", MyApplication.getInstance().getCurentToken());
                    OemLog.log(TAG, "JSON is " + jsonObject.toString());
                    HttpTools.post(MyApplication.getInstance(), Base_Url.Url_Base + Base_Url.monitorShareList_Url, jsonObject, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String result = new String(responseBody);
                            OemLog.log(TAG, "result is " + result);
                            //json解析
                            try {
                                JSONObject allMessage = new JSONObject(result);
                                AircatDevice currentDevice = new AircatDevice();

                                if ("success".equals(allMessage.getString("message"))) {
                                    OemLog.log(TAG,"data is "+allMessage.get("data").toString());
                                    if(allMessage.getString("data")==null||"".equals(allMessage.getString("data").trim())){
                                        return;
                                    }
                                    JSONArray messageArrayJson = new JSONArray(allMessage.getString("data"));
                                    for (int i = 0; i < messageArrayJson.length(); i++) {
                                        JSONObject tmp = (JSONObject) messageArrayJson.get(i);
                                        if (tmp.has("mac") && tmp.has("sender") && tmp.has("ctime")) {
                                            String macAddress = (String) tmp.get("mac");
                                            String sender = (String) tmp.get("sender");

                                            currentDevice.setMac(macAddress);
                                            currentDevice.setCtime((Long) tmp.get("ctime"));
                                            currentDevice.setWay(AircatConst.Way_share);
                                            currentDevice.setBindName(sender + "分享的空气猫");

                                            Intent intent = new Intent(OemBackgroundService.this, AirCatActivity.class);
                                            intent.setAction(Long.toString(System.currentTimeMillis()));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.putExtra("aircat", currentDevice);
                                            intent.putExtra("fromShare",true);
                                            PendingIntent pi = PendingIntent.getActivity(OemBackgroundService.this, 0, intent, 0);
                                            Notification notify = new Notification.Builder(OemBackgroundService.this)
                                                    // 设置打开该通知，该通知自动消失
                                                    .setAutoCancel(true)
                                                            // 设置显示在状态栏的通知提示信息
                                                    .setTicker("您有新消息")
                                                            // 设置通知的图标
                                                    .setSmallIcon(R.drawable.ic_launcher)
                                                            // 设置通知内容的标题
                                                    .setContentTitle("设备分享通知")
                                                            // 设置通知内容
                                                    .setContentText(sender + "向您分享了他的空气猫")
                                                            // 设置通知的自定义声音
                                                    .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.msg))
                                                    .setWhen(System.currentTimeMillis())
                                                            // 设改通知将要启动程序的Intent
                                                    .setContentIntent(pi).build();
                                            //发送通知
                                            nm.notify(i, notify);
                                        }
                                    }
                                } else if ("用户未登录".equals(allMessage.getString("message")) || "缺少Token或者用户名".equals(allMessage.getString("message"))) {
                                    handler.sendEmptyMessage(Const.NETWORK_MESSAGE_LOGOUT);
                                } else {
                                    OemLog.log(TAG, "user message apply failture...");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (responseBody != null) {
                                String result = new String(responseBody);
                                OemLog.log(TAG, "faiture result is " + result);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };



    public OemBackgroundService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        OemLog.log(TAG, "onCreate...");

        mBinder = new OemBackgroundServicesBindrer(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bloodPressDevices = BloodPressDevices.getInstance();
        braceleteDevices = BraceleteDevices.getInstance();
        bodyFatDevices = BodyFatDevices.getInstance();
        if (braceleteDevices.getCommandList() != null) {
            braceleteDevices.getCommandList().clear();
        }
        //设置services回调handler
        braceleteDevices.setServicesCallBackHandler(handler);
        bloodPressDevices.setServicesCallbackHandler(handler);
        bodyFatDevices.setServecieCallBackHandler(handler);


        PreferencesUtils.putBoolean(this, "connect_state", false);
        PreferencesUtils.putBoolean(this, "pre_connect_state", false);

        //开启自动连接线程
        autoConnectThread = new AutoConnectThread();
        autoConnectThread.setServicesCallBackHandler(handler);
        autoConnectThread.start();

        //开启命令执行线程
        commandExecuteThread = new CommandExecuteThread();
        commandExecuteThread.setThreadStaus(Const.BRACELETE_COMMAND_EXECUTE_STATUS_INITIAL);
        commandExecuteThread.start();

        //开启自动同步数据线程,获取明细数据
        syncThread = new SyncThread();
        syncThread.start();

        //开启自动同步数据线程,获取手环显示的实时数据
        getRealTimeSportDataThread = new GetRealTimeSportDataThread(handler);
        getRealTimeSportDataThread.start();

        //开启自动扫描连接线程,如果扫描到有自己app对应的设备则进行自动连接
        oemBleConnectThread = new OemBleConnectThread();
        oemBleConnectThread.setServicesCallBackHandler(handler);
        oemBleConnectThread.setPriority(10);
        oemBleConnectThread.start();

        //开启自动上传数据县城
        uploadOperation = new UploadOperation();
        uploadOperation.setCallbackHandler(handler);
        uploadOperation.uploadData();

        mAdapterIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mAdapterIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mAdapterIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mAdapterIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mBroadcastReceiver, mAdapterIntentFilter);

        //为手环自动唤醒设置闹钟
        syncSleepWaitTime = getNextDayAwakTime() - System.currentTimeMillis();
        OemLog.log("TimeStamp", "synctime is " + syncSleepWaitTime);
        timer.schedule(task, syncSleepWaitTime, HOUR_24);
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //监听电话来电

        //获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

        registerReceiver(rcvIncoming, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));



    }
    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state) {
                case TelephonyManager.CALL_STATE_IDLE: //空闲
                    Log.i("phone","idle...........");

                    break;
                case TelephonyManager.CALL_STATE_RINGING: //来电
                    Log.i("phone","ring...........");
                    boolean phoneWarn = PreferencesUtils.getBoolean(getApplicationContext(),"phoneWarn",false);
                    if(phoneWarn){
                        for (int i = 0; i < 3; i++) {
                            braceleteDevices.addCommandToQueue(new SetCallNameBraceleteCommand(MyApplication.getInstance(),incomingNumber));
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                    Log.i("phone","ing...........");

                    break;
            }
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    public void registerSportDataCallBack(ISportDataCallback sportDataCallback) {
        braceleteDevices.registerSportDataCallBack(sportDataCallback);
    }

    public void unregisterSportDataCallback() {

    }

    public void registerConnectSateChange(IConnectionStateChange connectionStateChange) {
        braceleteDevices.registerConnectStateChangeCallBack(connectionStateChange);
    }

    public void registerDeviceSearchCallBack(IDeviceSearchCallBack deviceSearchCallBack) {
        OemLog.log(TAG, "registerDeviceSearchCallBack...");
        braceleteDevices.registerDeviceSearchCallBack(deviceSearchCallBack);
    }

    public void registerBloodPressCallBack(IBloodPressDevicesCallBack bloodPressDevicesCallBack) {
        OemLog.log(TAG, "registerBloodPressCallBack...");
        bloodPressDevices.registerBloodPressCallBack(bloodPressDevicesCallBack);
    }

    public void registerBodyFatDevicesCallBack(IBodyFatDevicesCallBack bodyFatDevicesCallBack) {
        bodyFatDevices.registerBodyFatDevicesCallBack(bodyFatDevicesCallBack);
    }

    @Override
    public void onDestroy() {
        OemLog.log(TAG, "onDestroy...");
        commandExecuteThread.setThreadStaus(Const.BRACELETE_COMMAND_EXECUTE_STATUS_INITIAL);
    }

    /**
     * 向ui层爆露的bind接口，这些接口在主动没有生效的情况下使用，
     * 正常数据的上报流程为
     */

    public static class OemBackgroundServicesBindrer extends Binder {

        private OemBackgroundService mService;


        public OemBackgroundServicesBindrer(OemBackgroundService svr) {
            mService = svr;
        }

        public boolean cleanup() {
            mService = null;
            return true;
        }

        public OemBackgroundService getmService() {
            if (mService != null) {
                return mService;
            }
            return null;
        }

        public void setBindState(boolean isBind) {

        }

        public void syncTime() {

        }

        public int getRealTimeSportState() {
            return 0;
        }


        public void syncSportData() {

        }

        public void syncSleepData() {

        }

        public void setClearMode() {

        }

        public void uiNotify(int showType) {

        }

        public void registerSportDataCallBack(ISportDataCallback sportDataCallback) {
            mService.registerSportDataCallBack(sportDataCallback);
        }

        public void unregisterSportDataCallback() {
            mService.unregisterSportDataCallback();
        }

        public void registerConnectStateChangeCallBack(IConnectionStateChange connectionStateChange) {
            mService.registerConnectSateChange(connectionStateChange);
        }

        public void registerDeviceSearchCallBack(IDeviceSearchCallBack deviceSearchCallBack) {
            mService.registerDeviceSearchCallBack(deviceSearchCallBack);
        }

        public void registerBloodPressCallBack(IBloodPressDevicesCallBack bloodPressDevicesCallBack) {
            mService.registerBloodPressCallBack(bloodPressDevicesCallBack);
        }

        public void registerBodyFatDevicesCallBack(IBodyFatDevicesCallBack bodyFatDevicesCallBack) {
            mService.registerBodyFatDevicesCallBack(bodyFatDevicesCallBack);
        }
    }

    //取次日用户的清醒的时间
    private long getNextDayAwakTime() {
        DateFormat df;
        Date date;
        long timestamp;

        try {
            df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date = df.parse(TimeStampUtil.currTime2(1) + " 08:00:00");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            timestamp = cal.getTimeInMillis() + HOUR_24;
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

}











