package com.capitalbio.oemv2.myapplication.Devices.BodyFatDevices.src.com.bayh.bluetooth.util;

public class Constant {
	public static final String FATSCALE_DEVICE_NAME ="eBody-Fat-Scale";
    
    public static final int FATSCALE_PRESS_CONNECT_MESSAGE_DELAY_TIME = 3000;
    
    public static final int FATSCALE_PRESS_DISCONNECT_STATE = 0;
    public static final int FATSCALE_PRESS_CONNECT_STATE = 1;
    public static final int FATSCALE_PRESS_MEASUREING_STATE = 2;
    
    
    //命令类型
    public static final int FATSCALE_PRESS_COMMAND_CONNECT = 70001;
    public static final int FATSCALE_PRESS_COMMAND_SET = 1;
    public static final int FATSCALE_PRESS_COMMAND_DISCONNECT = 2;
    
    //命令执行结果
    public static final int FATSCALE_PRESS_CONNECT_COMMAND_SUCCESS = 2001;//���ӳɹ�
    public static final int FATSCALE_PRESS_CONNECT_COMMAND_FAILTRUE = 2002;//����ʧ��
    public static final int WRITE_SUCESS = 2003; //д�ɹ�
    public static final int WRITE_FAILURE = 2004; //дʧ��
    public static final int READ_SUCESS = 2005;   //���ɹ�
    public static final int READ_FAILURE = 2006;  //��ʧ�ܣ�û����Ӧ
    public static final int FATSCALE_PRESS_MEASURE_COMMAND_SUCCESS = 2007; //������ȷ
    public static final int FATSCALE_PRESS_MEASURE_COMMAND_FAILTURE = 2008;
    
  /*  public static final int FATSCALE_PRESS_CONNECT_COMMAND_SUCCESS = 2001;
    public static final int FATSCALE_PRESS_CONNECT_COMMAND_FAILTRUE = 2002;
    
    public static final int FATSCALE_PRESS_MEASURE_COMMAND_SUCCESS = 2003;
    public static final int FATSCALE_PRESS_MEASURE_COMMAND_FAILTURE = 2004;
    
    public static final int FATSCALE_PRESS_DISCONNECT_COMMAND_SUCCESS = 2005;
    public static final int FATSCALE_PRESS_DISCONNECT_COMMAND_FAIL = 2006;*/
    public static final int FATSCALE_PRESS_MEASURE_COMPLETED = 2007;
    public static String FATSCALE_DEVICE_ADDRESS = null;


    public static final int BODYFAT_SHUTDOWN_COMMAND_MESSAGE = 6500;


    //体脂称连接超时时间
    public static final int BODYFAT_CONNECT_TIMEOUT_MESSAGE = 6501;
    public static final int BODYFAT_CONNECT_TIMEOUT_MESSAGE_DELAY_TIME = 4000;



}
