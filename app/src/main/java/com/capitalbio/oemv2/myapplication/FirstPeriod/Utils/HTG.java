package com.capitalbio.oemv2.myapplication.FirstPeriod.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.oemv2.myapplication.Bean.AirIndexBean;
import com.capitalbio.oemv2.myapplication.FirstPeriod.Bean.CatBean;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class HTG {
	
	/**
	 * 跳转
	 * @param context
	 * @param clazz
	 */
	public static void to(Context context,Object clazz){
		Intent to = new Intent();
		to.setClass(context, (Class<?>) clazz);
		context.startActivity(to);
	}
	
	/**
	 * 获取当前年
	 * @return
	 */
	public static int currYear(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * 获取当前月
	 * @return
	 */
	public static int currMonth(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH)+1;
	}
	
	/**
	 * 获取当前日
	 * @return
	 */
	public static int currDay(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 获取当前小时
	 * @return
	 */
	public static int currHour(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 获取当前分钟
	 * @return
	 */
	public static int currMinute(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MINUTE);
	}
	
	/**
	 * 判断一个整数是否是一位整数
	 * @param i
	 * @return
	 */
	public static boolean isSingleBitInt(int i){
		String j = i+"";
		if(j.length()==1){
			return true;
		}
		return false;
	}
	
	 /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    /**
	 * 
	 * @param
	 * @return
	 */
	public static String getMac(Context context) {
		SharedPreferences sp = context.getSharedPreferences("mac", Context.MODE_PRIVATE);
		return sp.getString("mac", "");
	}
	
	/**
	 * 获取本地保存的用户名
	 * 
	 * @return
	 */
	public static String getUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences("login_panther", Context.MODE_PRIVATE);
		String user = sp.getString("user", "");
		String username = "";
		if (!user.equals("")) {
			JSONObject jsonObject = JSONObject.parseObject(user);
			username = jsonObject.getString("username");
		}
		return username;
	}
	
	
	//将服务器返回的catbean数据转成airindexbean数据
	public static List<AirIndexBean> transfer(List<CatBean> catBeans){
		if(catBeans==null){
			return null;
		}
		int size = catBeans.size();
		if(size<=0){
			return null;
		}
		
		List<AirIndexBean> airIndexBeans = new ArrayList<AirIndexBean>();
		
		for(int i=0;i<size;i++){
			CatBean catBean = catBeans.get(i);
			String monitortime = catBean.getTestTime();
			
			long mt = Long.parseLong(monitortime);
			
			int year = TimeStampUtil.getYear(mt);
			int month = TimeStampUtil.getMonth(mt);
			int day = TimeStampUtil.getDay(mt);
			int hour = TimeStampUtil.getHour(mt);
			int minute = TimeStampUtil.getMinute(mt);
			
			//System.out.println("监测时间=="+monitortime);
			System.out.println("监测时间=="+year+"-"+month+"-"+day+"="+hour+":"+minute);
			
			AirIndexBean airIndexBean = new AirIndexBean();
			airIndexBean.setAqi(catBean.getTvoc());
			airIndexBean.setFormaldehyde(catBean.getMethanal());
			airIndexBean.setHumidity(catBean.getHumidity());
			airIndexBean.setMAC(catBean.getDeviceId());
			airIndexBean.setTemperature(catBean.getTemperature());
			airIndexBean.setPm(catBean.getPm2Point5());
			airIndexBean.setPollutionlevel(catBean.getPollutionlevel());
			airIndexBean.setTime(catBean.getTestTime());
			airIndexBean.setUser(catBean.getUsername());
			
			airIndexBean.setYear(year);
			airIndexBean.setMonth(month);
			airIndexBean.setDay(day);
			airIndexBean.setHour(hour);
			airIndexBean.setMinute(minute);
			
			airIndexBeans.add(airIndexBean);
		}
		
		return airIndexBeans;
	}




	/**
	 * 判断当前季节：1春2夏3秋4冬
	 * @return
	 */
	public static int currSeason(){
		System.out.println("==========================================================================当前季节");
		int month = currMonth();
		if(month>=5&&month<=10){
			return 2;
		}
		if(month>=11&&month<=12){
			return 4;
		}
		if(month>=1&&month<=4){
			return 4;
		}
		return 1;
	}

}
