package com.capitalbio.oemv2.myapplication.Const;

/**
 * Created by chengkun on 15-11-5.
 */
public class Const {

    public static boolean DEBUG = true;


    //主界面滑动菜单用到的常量
    public static final int LEFT_OUT_OF_SCREEN = 1000;
    public static final int RIGHT_OUT_OF_SCREEN = 1001;
    public static final int OUT_OF_SCREEN_NOT_DISPLAY = 1003;

    //主界面滑动方向
    public static final int MAIN_VIEW_SLIP_DIRECTION_LEFT = 20;
    public static final int MAIN_VIEW_SLIP_DIRECTION_RIGHT = 10;

    //注册界面边界框的填写状态
    public static final int REGISTER_EDIT_STATE_EMPTY = 0;
    public static final int REGISTER_EDIT_STATE_FULL = 1;

    //注册消息
    public static final int REGISTER_MESSAGE_GET_VERYFICATION_CODE_SUCCESSFULL = 0;
    public static final int REGISTER_MESSAGE_REGISTER_FINISH = 1;

    //后台服务消息
    public static final int BACK_SERVICES_MESSAGE_DATA_SYNCHRONIZE_COMMPLETE = 2001;
    public static final int BACK_SERVICES_MESSAGE_DATA_UPLOAD_COMMPLETE = 2002;


    //手环连接消息
    public static final int BRACELETE_CONNECT_MESSAGE_CONNECT_SUCCESSFULL = 30001;


    //services从UI层获取的当前显示界面信息
    public static final int CURRENT_DISPLAY_UI_APP_NOT_DISPLAY = 4000;
    public static final int CURRENT_DISPLAY_UI_BRACELETE_SPORT_DAY = 4001;
    public static final int CURRENT_DISPLAY_UI_BRACELETE_SPORT_MONTH = 4002;
    public static final int CURRENT_DISPLAY_UI_BRACELETE_SLEEP_DAY = 4003;
    public static final int CURRENT_DISPLAY_UI_BRACELETE_SLEEP_MONTH = 4004;


    //手环命令执行状态信息
    public static final int BRACELETE_COMMAND_EXECUTE_STATUS_INITIAL = 5000;
    public static final int BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE = 5001;
    public static final int BRACELETE_COMMAND_EXECUTE_STATUS_RUNNING = 5002;
    public static final int BRACELETE_COMMAND_EXECUTE_STATUS_COMMAND_ADD = 5004;

    public static final int BRACELETE_COMMAND_EXECUTE_STATUS_CONNECTED = 5005;
    public static final int BRACELETE_COMMAND_EXECUTE_STATUS_DISCONNECTED = 5006;


    //蓝牙开启延迟连接消息
    public static final int OEM_SERVECES_BLUETOOTH_ON_CONNECT_DELAY = 6001;
    public static final int DISCOVER_SERVICES_DELAY_TIME = 6002;


    //gatt连接状态消息
    public static final int GATT_SERVICES_CONNECTED = 2;
    public static final int GATT_SERVICES_DISCONNECTED = 0;

    //oem设备类型
    public static final int OEM_DEVICES_TYPE_BRACELETE = 1;
    public static final int OEM_DEVICES_TYPE_BLOOD_PRESS = 2;
    public static final int OEM_DEVICES_TYPE_BLOOD_BODY_FAT = 3;


    //命令超时时间
    public static final int OEM_COMMAND_TIMEOUT = 5000;


    //血压计测量完成断开连接消息
    public static final int BLOOD_PRESS_MEASURE_COMMPLETE_DISSCONNECT = 7001;

    public static final  float MIN_PERkM = 12f;//1km需要12min
}







