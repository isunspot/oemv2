package com.capitalbio.oemv2.myapplication.Utils;

import android.content.Context;
import android.util.Log;

import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.View.views.SphygmomanometerTurntable;

/**
 * @author lzq
 * @Time 2016/2/29 15:48
 */
public class BloodPressDataTransfer {

    public static final int BLOODPRESS_LOW = 0x01;//低压
    public static final int BLOODPRESS_MIDDLE = 0x02;//正常
    public static final int BLOODPRESS_HIGH = 0x03;//正常高值
    public static final int BLOODPRESS_HIGH1 = 0x04;//一级
    public static final int BLOODPRESS_HIGH2 = 0x05;//二级
    public static final int BLOODPRESS_HIGH3 = 0x06;//三级

    public static final int HEARTRATE_SLOW = 0x10;//过缓
    public static final int HEARTRATE_MIDDLE = 0x11;//正常
    public static final int HEARTRATE_FAST = 0x12;//过速

    public static final int HEARTRATE_EXCEPTION = 0x13;//年龄数据异常


    //单独判断收缩压
    public static int parseSYS(int value) {
        if (value < 90) {
            return BLOODPRESS_LOW;
        }
        if (value <= 119) {
            return BLOODPRESS_MIDDLE;
        }
        if (value <= 139) {
            return BLOODPRESS_HIGH;
        }
        if (value <= 159) {
            return BLOODPRESS_HIGH1;
        }
        if (value <= 179) {
            return BLOODPRESS_HIGH2;
        }
        return BLOODPRESS_HIGH3;
    }

    //单独判断舒张压
    public static int parseDIA(int value) {
        if (value < 60) {
            return BLOODPRESS_LOW;
        }
        if (value <= 79) {
            return BLOODPRESS_MIDDLE;
        }
        if (value <= 89) {
            return BLOODPRESS_HIGH;
        }
        if (value <= 99) {
            return BLOODPRESS_HIGH1;
        }
        if (value <= 109) {
            return BLOODPRESS_HIGH2;
        }
        return BLOODPRESS_HIGH3;
    }


    //耽误判断心率
    public static int parseHeart(int value, Context context) {
        //当清后台程序时容易空
        //String age = PreferencesUtils.getString(MyApplication.getInstance(), "age");
        String birth = MyApplication.getInstance().getCurrentUser().getAge();
        int age_i = 30 ;
        if(birth != null){
            age_i = TimeStampUtil.getAgeByBirth(birth);
        }
        OemLog.log("datatransfer","age is " + age_i);

       // if (age != null && !age.trim().equals("")) {
         //   int age_i = Integer.parseInt(age);
            if (age_i < 0) {
                return HEARTRATE_EXCEPTION;
            } else {
                if (age_i <= 1) {
                    if (value <= 80) {
                        return HEARTRATE_SLOW;
                    }
                    if (value <= 140) {
                        return HEARTRATE_MIDDLE;
                    }
                    return HEARTRATE_FAST;
                }
                if (age_i <= 6) {
                    if (value <= 100) {
                        return HEARTRATE_SLOW;
                    }
                    if (value <= 120) {
                        return HEARTRATE_MIDDLE;
                    }
                    return HEARTRATE_FAST;
                }
                if (age_i <= 60) {
                    if (value < 60) {
                        return HEARTRATE_SLOW;
                    }
                    if (value <= 100) {
                        return HEARTRATE_MIDDLE;
                    }
                    return HEARTRATE_FAST;
                }
                if (value <= 50) {
                    return HEARTRATE_SLOW;
                }
                if (value <= 80) {
                    return HEARTRATE_MIDDLE;
                }
                return HEARTRATE_FAST;

            }
       /* } else {
            return HEARTRATE_EXCEPTION;
        }*/
    }


    public static int discDegree(int high, int low) {

        if (high < 90) {
            //高压低
            return SphygmomanometerTurntable.VALUE_LOW;
        } else if (high > 140) {
            //高压高
            if (high < 159) {
                //一级高血压
                return SphygmomanometerTurntable.VALUE_HIGH1;
            } else if (high < 179) {
                //二级高血压
                return SphygmomanometerTurntable.VALUE_HIGH2;
            } else {
                //三级高血压
                return SphygmomanometerTurntable.VALUE_HIGH3;
            }
        } else {
            //高压正常
            if (low < 60) {
                //低血压
                return SphygmomanometerTurntable.VALUE_LOW;
            } else if (low > 90) {
                if (low < 99) {
                    //低血压的一级高血压
                    return SphygmomanometerTurntable.VALUE_HIGH1;
                } else if (low < 109) {
                    //低血压的二级高血压
                    return SphygmomanometerTurntable.VALUE_HIGH2;
                } else {
                    //低血压的三级高血压
                    return SphygmomanometerTurntable.VALUE_HIGH3;
                }
            } else {
                //低压正常
                if (high < 120) {
                    //高血压 正常血压
                    if (low < 80) {
                        //低血压 正常血压
                        return SphygmomanometerTurntable.VALUE_MIDDLE;
                    } else {
                        //低血压 正常高值
                        return SphygmomanometerTurntable.VALUE_HIGH;
                    }
                } else {
                    //高血压 正常高值
                    if (low < 80) {
                        //低血压 正常血压
                        return SphygmomanometerTurntable.VALUE_HIGH;
                    } else {
                        //低血压 正常高值
                        return SphygmomanometerTurntable.VALUE_HIGH;
                    }
                }
            }

        }

    }

    public static String parseThreeLevel(int high, int low) {

        int degree = discDegree(high, low);
        if(degree==SphygmomanometerTurntable.VALUE_EXCEPTION){
            //Log.i("info","=======");
            return "异常";
        }
        if(degree==SphygmomanometerTurntable.VALUE_HIGH){
            //Log.i("info","=======");
            return "正常";
        }
        if(degree==SphygmomanometerTurntable.VALUE_HIGH1){
            //Log.i("info","=======");
            return "高血压";
        }
        if(degree==SphygmomanometerTurntable.VALUE_HIGH2){
            //Log.i("info","=======");
            return "高血压";
        }
        if(degree==SphygmomanometerTurntable.VALUE_HIGH3){
            //Log.i("info","=======");
            return "高血压";
        }
        if(degree==SphygmomanometerTurntable.VALUE_LOW){
            //Log.i("info","=======");
            return "低血压";
        }
        if(degree==SphygmomanometerTurntable.VALUE_MIDDLE){
            //Log.i("info","=======");
            return "正常";
        }

        /*if (degree == 0 || degree == 45 || degree == 315) {
            return "正常";
        }
        if (degree == 90) {
            return "低血压";
        }
        if (degree == 135) {
            return "异常";
        }
        if (degree == 180 || degree == 225 || degree == 270) {
            return "高血压";
        }*/

        return "";
    }
}
