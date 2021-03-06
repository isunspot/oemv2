package com.capitalbio.oemv2.myapplication.NetUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.capitalbio.oemv2.myapplication.Activity.MainActivity;
import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.Bean.AirCatInfo;
import com.capitalbio.oemv2.myapplication.Const.Base_Url;
import com.capitalbio.oemv2.myapplication.Utils.Common_dialog;
import com.capitalbio.oemv2.myapplication.Utils.OemLog;
import com.capitalbio.oemv2.myapplication.Utils.PreferencesUtils;
import com.capitalbio.oemv2.myapplication.Utils.TimeStampUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sina.weibo.sdk.net.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wxy on 16-3-5.
 */
public class HistoryDownd {

    public static  String TAG ="downloadhistory";
    static private String resultData;
    static RegistOnGetResult callback;


    public  static  void  getHistory(final Context context,HashMap<String,Object> params, final RegistOnGetResult callback) {

        try {


            //构造请求json对象
            JSONObject jsonObject = new JSONObject();
            JSONObject dataObj = new JSONObject();

            jsonObject.put("apikey", MyApplication.getInstance().apikey);
            //Log.i("info", "==========apikey========" + MyApplication.getInstance().apikey);
            jsonObject.put("username", MyApplication.getInstance().getCurrentUserName());
            //Log.i("info", "==========username========" + MyApplication.getInstance().getCurrentUserName());
            jsonObject.put("token", MyApplication.getInstance().getCurentToken());
            //Log.i("info", "==========token========" +PreferencesUtils.getString(context, "token"));

            dataObj.put("modelType", params.get("modelType"));
            dataObj.put("mac", params.get("mac"));
            dataObj.put("beginDate", params.get("beginDate"));
            dataObj.put("endDate", params.get("endDate"));
            if(params.containsKey("startNum")){
                dataObj.put("startNum", params.get("startNum")+"");
            }
            if(params.containsKey("showCount")){
                dataObj.put("showCount", params.get("showCount")+"");
            }

            jsonObject.put("data", dataObj.toString());
            //Log.i("info", "==========data========" + dataObj.toString());


            OemLog.log(TAG, "params is ....." + params.get("url") + "--" + jsonObject.toString());
            //
            HttpTools.post(context, Base_Url.Url_Base + params.get("url"), jsonObject, new AsyncHttpResponseHandler() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String result1 = new String(responseBody);
                 //   Log.i(TAG, "======下载数据的结果是" + result1);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result1);
                        int code = jsonObject.optInt("code");
                        String msg = jsonObject.optString("message");
                        String data = jsonObject.optString("data");

                        callback.OnGetResult(code,msg,data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "服务器异常", Toast.LENGTH_LONG).show();
                    resultData = "服务器异常";
                    Log.i(TAG, "下载数据:服务器异常");
                }


            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public interface  RegistOnGetResult{
        public void OnGetResult(int code,String msg,String data);
    }
}
