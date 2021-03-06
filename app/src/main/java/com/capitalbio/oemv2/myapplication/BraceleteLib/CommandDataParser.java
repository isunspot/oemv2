package com.capitalbio.oemv2.myapplication.BraceleteLib;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.Bean.SportDataDetailBean;
import com.capitalbio.oemv2.myapplication.Bean.SportDataTotalBean;
import com.capitalbio.oemv2.myapplication.Const.Base_Url;
import com.capitalbio.oemv2.myapplication.Const.Const;
import com.capitalbio.oemv2.myapplication.Devices.Bracelete.ISportDataCallback;
import com.capitalbio.oemv2.myapplication.NetUtils.HttpTools;
import com.capitalbio.oemv2.myapplication.Utils.OemLog;
import com.capitalbio.oemv2.myapplication.Utils.PreferencesUtils;
import com.capitalbio.oemv2.myapplication.Utils.TimeStampUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.db.DbManagerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chengkun on 15-12-20.
 */
public class CommandDataParser {

    public static final String TAG = "CommandDataParser";
    public static final int BRACELETE_COMMAND_DELAY_TIME = 800;
    private Context mContext = MyApplication.getInstance();

    private int height = 175;/*PreferencesUtils.getInt(mContext, "user_height", 0);*/
    private int sex = 0;/*PreferencesUtils.getInt(mContext, "user_sex", -1);*/

    private JSONObject sportDataTotalJson = new JSONObject();

    private JSONObject sportDataTotalUploadJson = new JSONObject();



    private ISportDataCallback sportDataCallback;
    private List<SleepDetailData> sleepRecordList;
    private List<SleepDataTotalBean> sleepDataTotalBeanList;
    private List<SportDataDetailBean> sportsDetailDataList;
    private SportDataTotalBean sportDataTotalBean;
    private Handler callbackHandler;
    private String latestYMD;
    private long latestSleepLongtime;
    DbManager dbManager = MyApplication.getDB();

    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("biodb")
            .setDbDir(new File("/sdcard"))
            .setDbVersion(1)
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                }
            });


    public CommandDataParser() {
        sportDataTotalBean = new SportDataTotalBean();
        sportsDetailDataList = new ArrayList<SportDataDetailBean>();
        sleepRecordList = new ArrayList<SleepDetailData>();
        sleepDataTotalBeanList = new ArrayList<SleepDataTotalBean>();

    }



    public void pasrseData(byte[] bytes) {

        if (null == bytes)
            return;

        callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);

        if (bytes.length == 5 && bytes[0] == 0x6e && bytes[1] == 0x01
                && bytes[2] == 0x00 && bytes[4] == (byte) 0x8f) {
            // 电量返回的结果

        } else if ((bytes.length == 6) && bytes[0] == 0x6e && bytes[1] == 0x01
                && bytes[2] == 0x01 && bytes[5] == (byte) 0x8f) {
            // 响应消息应答,具体看通讯协议

            OemLog.log(TAG, "响应消息解析 ,命令码:  0x" + String.format("%02x", bytes[3])
                    + "     结果： " + showResult(bytes[4]) + "\n");
            if (bytes[3] == 0x31 && bytes[4] == 0x03) {
                OemLog.log(TAG, "睡眠没有数据\n");
//                callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            } else if (bytes[3] == 0x06 && bytes[4] == 0x04) {
                OemLog.log(TAG, "运动没有数据\n");
//                callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            } else if (bytes[4] != 0x00) {
                OemLog.log(TAG, "接收出错XXXXXXXXXXXXXXXXXXX\n");
                return;
            }
            switch (bytes[3]) {
                case 0x15: // 设置设备时间返回
                    OemLog.log(TAG, "同步当前时间完成End---\n");
                    break;
                case 0x34: // 时间格式和距离单位设置返回
                    OemLog.log(TAG, "设置24小时制，Km显示完成End---\n");
                    break;
                case 0x32:
                    break;
                case 0x0c:
                    OemLog.log(TAG, "设置个人信息不清除当日运动数据End---\n");
                    break;
                case 0x1b:
                    break;
                case 0x06: // 运动详细数据传输完成返回
                    OemLog.log(TAG, "获取运动明细数据完成End---\n");
                    break;
                case 0x31:
                    OemLog.log(TAG, "获取睡眠数据完成End---\n");
                    break;
                case 0x21:
                    OemLog.log(TAG, "清除设备全部提醒设置完成End---\n");
                    break;
                case 0x12:
                    OemLog.log(TAG, "初始化個人信息，並且清除当日总运动数据完成End---\n");
                    break;
                case 0x0d:
                    OemLog.log(TAG, "设置目标步数完成End---\n");
                case (byte) 0xA2:
                    break;
                default:
                    break;
            }
        } else if (bytes.length == 20 && bytes[0] == 0x6e && bytes[1] == 0x01
                && bytes[2] == 0x0F && bytes[19] == (byte) 0x8f) {
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            //运动汇总数据返回
            sportDataTotalBean = ProtocolParser.parseSportTotalDataBytes(bytes);
            if (null == sportDataTotalBean) {
                OemLog.log(TAG, "获取运动汇总数据为空\n");
            } else {
                sportDataTotalBean.setDistance((int) ((height / 100.00f) * (sex == 0 ? 0.415f : 0.413f) * sportDataTotalBean.getSteps()));

                if (sportDataCallback != null) {
                    sportDataCallback.onLoadData(sportDataTotalBean);
                }
                PreferencesUtils.putString(mContext,  MyApplication.getInstance().getCurrentUserName() + "bracelete_latest_sport_time", TimeStampUtil.latestTestTime(0));
                OemLog.log(TAG, "获取运动汇总数据:  步数:" + sportDataTotalBean.getSteps() + " ,卡路里: " + sportDataTotalBean.getCal() + " 距离: " + sportDataTotalBean.getDistance());
            }
        } else if ((bytes.length == 21 && bytes[0] == 0x6e && bytes[2] == 0x05 && bytes[20] == (byte)0x8f ) //L28S/C返回
                || (bytes.length == 19 && bytes[0] == 0x6e && bytes[2] == 0x05                           //L11/L28T/W/H 返回
                && bytes[18] == (byte) 0x8f)) {
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            // 运动详细数据返回
            SportDataDetailBean sDataDetail = null;
            try {
                sDataDetail = DataAnalytical.Sports.getSportsDataBean(bytes);
                sDataDetail.setDistance((int) ((height / 100.00f) * (sex == 0 ? 0.415f : 0.413f) * sDataDetail.getSteps()));
          //add username
                sDataDetail.setUsername(MyApplication.getInstance().getCurrentUserName());
            } catch (DataErrorException e) {
                e.printStackTrace();
            }
            OemLog.log(TAG, "获取运动详细数据:  步数:" + sDataDetail.getSteps() + " ,卡路里: "
                    + sDataDetail.getCal() + ", 运动时间:"+ sDataDetail.getSamplePointTime() + " 数据索引为： " + sDataDetail.getIndex());
            synchronized (sportsDetailDataList) {
                if (sportsDetailDataList != null) {
                    sportsDetailDataList.add(sDataDetail);
                }
            }


            if ((0x80 & bytes[3]) == 0) {
                OemLog.log(TAG, "最后一条运动数据，进行数据库插入操作");
                insertDataToDb();
            }

        } else if (bytes.length == 9 && bytes[2] == 0x13 && bytes[0] == 0x6e
                && bytes[8] == (byte) 0x8f) {
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            //睡眠分条记录数据
            try {
                SleepDetailData data = DataAnalytical.getSleepDetailData(bytes);
                latestYMD = data.getYmd();
                latestSleepLongtime = data.getLongSleepTime();
                synchronized (sleepRecordList) {
                    sleepRecordList.add(data);
                }

                OemLog.log(TAG, "获取睡眠记录数据:  睡眠类型:" + data.getSleepType() + " ,时间: "
                        + data.getFomatTime() + " \n");

            } catch (DataErrorException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bytes[3] == 0x11) {
                insertDataToDb();
            }
        } else if (bytes.length == 17 && bytes[2] == 0x14 && bytes[0] == 0x6e
                && bytes[16] == (byte) 0x8f) {
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            //睡眠总数据
            try {
                SleepDataTotalBean data = DataAnalytical.Sleep.getSleepSumData(bytes);
                //昨天晚上8：00到今天晚上8：00的记录 都算今天的记录
                String ymd = TimeStampUtil.getSleepYmd(latestSleepLongtime);
                Log.i("睡眠规则下的day","----"+ymd);
                data.setYmd(ymd);
                data.setLongTime(latestSleepLongtime);
                data.setUsername(MyApplication.getInstance().getCurrentUserName());
                synchronized (sleepDataTotalBeanList) {
                    if(data.getSeepSleepTime()+data.getSomnolenceTime() != 0){
                        sleepDataTotalBeanList.add(data);
                    }
                }
                OemLog.log(TAG,
                        "获取睡眠总数据:  入睡时间:" + data.getGotoSleepTime()
                                + " ,清醒时间: " + data.getSoberTime() + " ,浅睡时间: "
                                + data.getSomnolenceTime() + ",深睡时间:"
                                + data.getSeepSleepTime() + "唤醒次数:"
                                + data.getRouseNumber() + ",总时间:"
                                + data.getSumNumber() + " \n");
                insertDataToDb();
            } catch (DataErrorException e) {
                e.printStackTrace();
            }
        } else if (bytes.length == 20 && bytes[2] == 0x04 && bytes[0] == 0x6e) {
            //watchID返回
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            OemLog.log(TAG, "获取WatchID完成 End---\n");
            byte[] newByte = new byte[20];
            for (int i = 3; i < bytes.length - 1; i++) {
                newByte[i - 3] = bytes[i];
            }
            String watchIdStr = new String(newByte);

        } else if (bytes.length == 8 && bytes[2] == 0x12 && bytes[0] == 0x6e
                && bytes[7] == (byte) 0x8f) {
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            //回传运动记录总数
            byte[] newByte = new byte[4];
            for (int i = 3; i < bytes.length - 1; i++) {
                newByte[i - 3] = bytes[i];
            }

            // 解析运动总数据
            int byteReverseToInt = ProtocolParser.byteReverseToInt(newByte);
            OemLog.log(TAG, "运动记录总数 = " + byteReverseToInt + " End---\n");
        }else{
//            callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
            OemLog.log(TAG, "未识别的数据包！");
        }

    }

    private String showResult(byte res) {
        String s = "";
        switch (res) {
            case 0:
                s = "成功";
                callbackHandler.sendEmptyMessageDelayed(Const.BRACELETE_COMMAND_EXECUTE_STATUS_COMPLETE, BRACELETE_COMMAND_DELAY_TIME);
                break;

            case 1:
                s = "失败";
                break;

            case 2:
                s = "非法命令";
                break;

            default:
                s = String.format("%x", res);
                break;
        }
        return s;
    }

    public void setCallbackHandler(Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public static DbManager getDb(DbManager.DaoConfig daoConfig) {
        return DbManagerImpl.getInstance(daoConfig);
    }

    private void insertDataToDb () {
        //进行数据库插入操作
        Thread insertThread = new Thread() {
            @Override
            public void run() {
                try {
                    //运动明细
                    if (sportsDetailDataList.size() != 0) {
                        OemLog.log(TAG, " sportdetail is not null and insert to db");
                        synchronized (sportsDetailDataList) {
                            for (SportDataDetailBean sp : sportsDetailDataList) {
                                dbManager.saveOrUpdate(sp);
                            }
                            sportsDetailDataList.clear();
                        }

                    }

                    if (sleepRecordList.size() != 0) {
                        OemLog.log(TAG, "sleepdata is not null and insert to db...");
                        synchronized (sleepRecordList) {
                            for (SleepDetailData sd : sleepRecordList) {
                                dbManager.saveOrUpdate(sd);
                            }
                            sleepRecordList.clear();
                        }
                    }

                    if (sleepDataTotalBeanList.size() != 0) {
                        OemLog.log(TAG, "sleepTataldata is not null and insert to db");
                        synchronized (sleepDataTotalBeanList) {
                            for (SleepDataTotalBean sleepDataTotalBean : sleepDataTotalBeanList) {
                                dbManager.saveOrUpdate(sleepDataTotalBean);
                            }
                            sleepDataTotalBeanList.clear();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        insertThread.start();

    }

    private void sportDataToBean (SportsDetailData sp) {
        SportDataDetailBean tmpBean = new SportDataDetailBean();

        if (sportsDetailDataList == null) {
            sportsDetailDataList = new ArrayList<SportDataDetailBean>();
        }

        if (null == sp) {
            OemLog.log(TAG, "获取运动详细数据为空");
        } else {
            tmpBean.setSteps(sp.getStepNumber());
            tmpBean.setCal(sp.getCalorie());
            tmpBean.setDistance(sp.getDistance());
            tmpBean.setSamplePointTime(DateUtil.getDateToString(sp.getSprtsDate(), DateUtil.YMDHM));
            sportsDetailDataList.add(tmpBean);

        }
    }

    public void registerSportDataCallback(ISportDataCallback sportCallback) {
        sportDataCallback = sportCallback;
    }

    private void uploadSportDataTotal() {

        try {
            SportDataDetailBean sp;
            sportDataTotalJson.put("steps", sportDataTotalBean.getSteps());
            sportDataTotalJson.put("distance", sportDataTotalBean.getDistance());
            sportDataTotalJson.put("cal", sportDataTotalBean.getCal());
            sportDataTotalJson.put("testTime", System.currentTimeMillis());
            sportDataTotalJson.put("modelType", "braceletSportsTotal");
            sportDataTotalUploadJson.put("data", sportDataTotalJson.toString());
            OemLog.log(TAG, "upload json is " + sportDataTotalUploadJson.toString());
            HttpTools.post(mContext, Base_Url.Url_Base + Base_Url.uploadData_Url, sportDataTotalUploadJson, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        OemLog.log(TAG, "uploadSportDataTotal success result is " + new String(responseBody));
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
