package com.capitalbio.oemv2.myapplication.Devices.AirCatDevices.esptouch.command;

import android.content.Context;
import android.util.Log;

import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.Devices.AirCatDevices.esptouch.IEsptouchListener;
import com.capitalbio.oemv2.myapplication.Devices.AirCatDevices.esptouch.ISearchMacCallBack;
import com.capitalbio.oemv2.myapplication.Devices.AirCatDevices.esptouch.ISetTargetApCallBack;
import com.capitalbio.oemv2.myapplication.Utils.OemLog;
import com.capitalbio.oemv2.myapplication.Utils.PreferencesUtils;

public class AirCatDevices {

    public static final String TAG = "AirCatDevices";
    
    
    private Context mContext;
    
    private EsptouchAsyncTask3 mEsptouchAsyncTask3 = null;

    private LocalWiFiInfo mLocalWiFiInfo = null;

    private IEsptouchListener mEsptouchListener = null;
    
    
    
    public AirCatDevices(Context context, IEsptouchListener listener,
            String password, ISetTargetApCallBack setTargetApCallBack, ISearchMacCallBack searchMacCallBack) {
        mContext = context;
        mEsptouchListener = listener;
        mLocalWiFiInfo = new LocalWiFiInfo(context, password);
        mEsptouchAsyncTask3 = new EsptouchAsyncTask3(mContext, mEsptouchListener, setTargetApCallBack, searchMacCallBack);
    }

    public void setTargetAp() {
        Log.d(TAG, "setTargetAp Command ssid is " + mLocalWiFiInfo.getmApSsid()
                + " bssid is " + mLocalWiFiInfo.getmApBssid() + " password is "
                + mLocalWiFiInfo.getmApPassword());
        
        mEsptouchAsyncTask3.execute(mLocalWiFiInfo.getmApSsid(), mLocalWiFiInfo.getmApBssid(), mLocalWiFiInfo.getmApPassword(),
                "NO", "5");
    }
    
    public void searchMacAddress() {
        PreferencesUtils.putBoolean(MyApplication.getInstance(), "isAircatSearchMac", true);
        mEsptouchAsyncTask3.execute(Constant.AIRCAT_SEARCH_MAC_COMMAND);
    }

    public void stopPreviousTask() {
        if (mEsptouchAsyncTask3 != null) {
            OemLog.log(TAG, "stopPreviousTask...");
            mEsptouchAsyncTask3.stopPreviousTask();
        }
    }

}





