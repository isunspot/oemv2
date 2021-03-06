package com.capitalbio.oemv2.myapplication.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.capitalbio.oemv2.myapplication.Activity.LoginActivity;
import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.Bean.BodyJson;
import com.capitalbio.oemv2.myapplication.Bean.DeviceGrade;
import com.capitalbio.oemv2.myapplication.Bean.LineChartBg;
import com.capitalbio.oemv2.myapplication.Const.Base_Url;
import com.capitalbio.oemv2.myapplication.Const.BodyFatConst;
import com.capitalbio.oemv2.myapplication.Devices.BodyFatDevices.src.com.bayh.sdk.ble.bean.BodyFatSaid;
import com.capitalbio.oemv2.myapplication.NetUtils.HistoryDownd;
import com.capitalbio.oemv2.myapplication.NetUtils.NetTool;
import com.capitalbio.oemv2.myapplication.R;
import com.capitalbio.oemv2.myapplication.Utils.Common_dialog;
import com.capitalbio.oemv2.myapplication.Utils.FatRule;
import com.capitalbio.oemv2.myapplication.Utils.OemLog;
import com.capitalbio.oemv2.myapplication.Utils.PreferencesUtils;
import com.capitalbio.oemv2.myapplication.Utils.TimeStampUtil;
import com.capitalbio.oemv2.myapplication.View.MyMarkerView;
import com.capitalbio.oemv2.myapplication.View.MyYAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;

import org.xutils.db.DbModelSelector;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 历史记录月
 * Created by wxy on 15-12-4.
 */
public class BodyFatFragmentMonth extends BaseFragment {

    private LineChart mChart;
    /*PullToRefreshScrollView mPullRefreshScrollView;
    ScrollView mScrollView;*/
   // private ChartBackgroundView chartbg;
   List<LineChartBg> bginfos  = new ArrayList<>();
    List<BodyFatSaid> dataMonth = new ArrayList<>();//查询的月记录
    HashMap<String,String> hashMap = new HashMap<>();

    private  String TAG = "BodyFatFragmentMonth";
    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fg_bodyfat_month, container, false);
    }

    @Override
    protected void findViewById(View view) {
        mChart = (LineChart) view.findViewById(R.id.linechart);
        view.findViewById(R.id.ll_weight).setOnClickListener(this);
        view.findViewById(R.id.ll_bmi).setOnClickListener(this);
        view.findViewById(R.id.ll_bmr).setOnClickListener(this);
        view.findViewById(R.id.ll_viscus).setOnClickListener(this);
        view.findViewById(R.id.ll_bone).setOnClickListener(this);
        view.findViewById(R.id.ll_fat).setOnClickListener(this);
        view.findViewById(R.id.ll_muscle).setOnClickListener(this);
        view.findViewById(R.id.ll_water).setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ll_weight:
                setData(BodyFatConst.TYPE_WEIGHT);
                break;
            case R.id.ll_bmi:
                setData(BodyFatConst.TYPE_BMI);
                break;
            case R.id.ll_bmr:
                setData(BodyFatConst.TYPE_BMR);
                break;
            case R.id.ll_viscus:
                setData(BodyFatConst.TYPE_VISCERAL_LEVAL);
                break;
            case R.id.ll_bone:
                setData(BodyFatConst.TYPE_BONECONTENT);
                break;
            case R.id.ll_fat:
                setData(BodyFatConst.TYPE_FAT_RATION);
                break;
            case R.id.ll_muscle:
                setData(BodyFatConst.TYPE_MUSLE_CONTENT);
                break;
            case R.id.ll_water:
                setData(BodyFatConst.TYPE_WATER_CONTENT);
                break;
        }
    }

    @Override
    protected void processLogic() {

        getMonthHistory();
        initChart();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("time", BodyFatConst.month);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            update();
        }
    }

    private void getMonthHistory(){

        getMonthHistoryData(BodyFatConst.month);
        if(dataMonth.size()<=0){
            getDataByNet(BodyFatConst.month);
        }
    }
    public void initChart() {

        //设置网格
        mChart.setDrawBorders(false);

        mChart.setDrawGridBackground(false);
        mChart.setGridBackgroundColor(Color.rgb(255, 235, 236));
        //在chart上的右下角加描述
        mChart.setDescription(" ");
        mChart.setNoDataText("无数据");

        //设置是否可以触摸，如为false，则不能拖动，缩放等
        mChart.setTouchEnabled(true);
        //设置是否可以拖拽，缩放
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        //设置是否能扩大扩小
        mChart.setPinchZoom(false);

        // 设置背景颜色
        //   mChart.setBackgroundColor(getResources().getColor(high));
        //设置点击chart图对应的数据弹出标注
        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view,0);
        // define an offset to change the original position of the marker
        // (optional)
        // set the marker to the chart
        mChart.setMarkerView(mv);
        // enable/disable highlight indicators (the lines that indicate the
        // highlighted Entry)
        mChart.getHighlightByTouchPoint(mv.getX(),mv.getY());
        //设置字体格式，如正楷
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "OpenSans-Regular.ttf");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(0);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMaxValue(150f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setLabelCount(6, true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setGridLineWidth(1);
        DecimalFormat format= new DecimalFormat("###,###,###,##0.0");
        leftAxis.setValueFormatter(new MyYAxisValueFormatter(format));
        mChart.getAxisRight().setEnabled(false);
        List<Integer> colors = new ArrayList<>();
        List<String> labels = new ArrayList<>();

       // mChart.getLegend().setEnabled(false);
        // leftAxis.setLabelCount(6,false);
        LimitLine limitLine= new LimitLine(35);
        limitLine.setLabel("middle_");

        setData(BodyFatConst.TYPE_WEIGHT);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setTypeface(tf);

       /* String min = leftAxis.getFormattedLabel(0);
        String max = leftAxis.getFormattedLabel(1);
        int count = leftAxis.getLabelCount();

        float d = (Float.valueOf(max) - Float.valueOf(min)) / 10.0f;
        Log.i("scaleinfo3", "---" + d);
        mChart.getViewPortHandler().setMaximumScaleY(d);*/
        mChart.invalidate();
  }
    private int lastXindex;
    private LineData data;
    public void setData(int flag) {
        ArrayList<Entry> yVals_MIN = new ArrayList<Entry>();
        ArrayList<Entry> yVals_middle = new ArrayList<Entry>();
        ArrayList<Entry> yVals_max = new ArrayList<Entry>();
        DeviceGrade grade = null;
        int days = TimeStampUtil.daysInMonth(BodyFatConst.month);
        List<String> xLables =new ArrayList<>();
        String weight = PreferencesUtils.getString(mContext, "weight", "60");
        float weightF= Float.parseFloat(weight);
        String sex = PreferencesUtils.getString(mContext, "sex", "男");
        String age = PreferencesUtils.getString(mContext, "age", "20");
        int ageInt = Integer.valueOf(age);
        String height = PreferencesUtils.getString(mContext, "height", "175");
        float heightF = Float.parseFloat(height);
            for(int i=0;i<days;i++){
                xLables.add(i+1+"");
                switch (flag) {

                    case BodyFatConst.TYPE_WEIGHT:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_WEIGHT);

                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;
                    case BodyFatConst.TYPE_BMI:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_BMI);

                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(40.0f);

                        break;
                    case BodyFatConst.TYPE_BMR:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_BMR);
                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;
                    case BodyFatConst.TYPE_BONECONTENT:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_BONECONTENT);
                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;
                    case BodyFatConst.TYPE_VISCERAL_LEVAL:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_VISCERAL_LEVAL);

                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;
                    case BodyFatConst.TYPE_FAT_RATION:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_FAT_RATION);
                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;
                    case BodyFatConst.TYPE_WATER_CONTENT:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_WATER_CONTENT);
                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;
                    case BodyFatConst.TYPE_MUSLE_CONTENT:
                        grade = FatRule.getDeviceGrade(ageInt,sex,weightF,heightF,BodyFatConst.TYPE_MUSLE_CONTENT);
                        yVals_MIN.add(new Entry(grade.getValue_low(), i));
                        yVals_middle.add(new Entry(grade.getValue_mid(), i));
                        yVals_max.add(new Entry(grade.getValue_high(), i));
                        mChart.getAxisLeft().setAxisMaxValue(grade.getValue_high());

                        break;

                }
            }



        ArrayList<Entry> yVals = new ArrayList<Entry>();
        if(yVals.size()>0){
            yVals.clear();
        }
        if(dataMonth!=null&&dataMonth.size()>0){

            for (int i = 0; i < dataMonth.size(); i++) {

                int day = TimeStampUtil.getDay(Long.valueOf(dataMonth.get(i).getLongTime()));
                if(i ==  dataMonth.size()-1){
                    lastXindex = day-1;
                }
                switch (flag) {
                    case BodyFatConst.TYPE_WEIGHT:
                        yVals.add(new Entry(dataMonth.get(i).getWeight(), day-1));

                        break;
                    case BodyFatConst.TYPE_BMI:
                        if(dataMonth.get(i).getBmi()!=null){
                            float bmi = Float.parseFloat(dataMonth.get(i).getBmi());
                            yVals.add(new Entry(bmi, day-1));
                        }

                        break;
                    case BodyFatConst.TYPE_BMR:
                        yVals.add(new Entry(dataMonth.get(i).getKcal(), day-1));
                        break;
                    case BodyFatConst.TYPE_BONECONTENT:
                        yVals.add(new Entry(dataMonth.get(i).getBone(), day-1));
                        break;
                    case BodyFatConst.TYPE_VISCERAL_LEVAL:
                        yVals.add(new Entry(dataMonth.get(i).getVisceraFat(), day-1));
                        break;
                    case BodyFatConst.TYPE_FAT_RATION:
                        yVals.add(new Entry(dataMonth.get(i).getFat(), day-1));
                        break;
                    case BodyFatConst.TYPE_WATER_CONTENT:
                        yVals.add(new Entry(dataMonth.get(i).getWater(), day-1));
                        break;
                    case BodyFatConst.TYPE_MUSLE_CONTENT:
                        yVals.add(new Entry(dataMonth.get(i).getMuscle(), day-1));
                        break;
                }
            }


        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "体重");

        set1.setDrawCubic(true);  //设置曲线为圆滑的线
        set1.setCubicIntensity(0.05f);
        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set1.setDrawCircles(true);  //设置有圆点
        set1.setLineWidth(4f);    //设置线的宽度
        set1.setCircleSize(6f);   //设置小圆的大小

        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));    //设置曲线的颜色
        set1.setDrawValues(false);

        // create a data object with the datasets
        // LineData data = new LineData(xVals, set1);


        //  Log.i("ymax",mChart.getYMax()+"");
        LineDataSet set2 = new LineDataSet(yVals_MIN, "偏低");

       // set2.setDrawCubic(true);  //设置曲线为圆滑的线

        set2.setDrawFilled(true);  //设置包括的范围区域填充颜色
        set2.setDrawCircles(false);  //设置有圆点
        set2.setLineWidth(1f);    //设置线的宽度
        //set1.setHighLightColor(Color.rgb(244, 117, 117));
        set2.setColor(Color.parseColor("#FFFAE0"));    //设置曲线的颜色
        set2.setFillColor(Color.parseColor("#FFFAE0"));
        set2.setFillAlpha(225);
        set2.setDrawValues(false);
        set2.setHighlightEnabled(false);

        LineDataSet set3 = new LineDataSet(yVals_middle, "标准");

        set3.setDrawCubic(false);  //设置曲线为圆滑的线

        set3.setDrawFilled(true);  //设置包括的范围区域填充颜色
        set3.setDrawCircles(false);  //设置有圆点
        set3.setLineWidth(1f);    //设置线的宽度
        set3.setColor(Color.rgb(214, 246, 222));    //设置曲线的颜色
        set3.setFillColor(Color.rgb(214, 246, 222));
        set3.setFillAlpha(225);
        set3.setFillFormatter(new DefaultFillFormatter(grade.getValue_low()));
        set3.setDrawValues(false);
        set3.setHighlightEnabled(false);

        LineDataSet set4 = new LineDataSet(yVals_max, "偏高");

        set4.setDrawCubic(false);  //设置曲线为圆滑的线

        set4.setDrawFilled(true);  //设置包括的范围区域填充颜色
        set4.setDrawCircles(false);  //设置有圆点
        set4.setColor(Color.parseColor("#FFEBEC"));    //设置曲线的颜色
        set4.setFillColor(Color.parseColor("#FFEBEC"));
        set4.setFillAlpha(225);
        set4.setFillFormatter(new DefaultFillFormatter(grade.getValue_mid()));
        set4.setDrawValues(false);
        set4.setHighlightEnabled(false);

        if(flag==BodyFatConst.TYPE_VISCERAL_LEVAL){
            set2.setLabel("标准");
            set3.setLabel("稍微偏高");
            set4.setLabel("偏高");

        }else{
            set2.setLabel("偏低");
            set3.setLabel("标准");
            set4.setLabel("偏高");
        }
        switch (flag){
            case BodyFatConst.TYPE_WEIGHT:
                set1.setHighLightColor(Color.parseColor("#00CCFE"));
                set1.setColor(Color.parseColor("#00CCFE"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#00CCFE"));
                set1.setLabel("体重");
                break;
            case BodyFatConst.TYPE_BMI:
                set1.setHighLightColor(Color.parseColor("#9BCD5E"));
                set1.setColor(Color.parseColor("#9BCD5E"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#9BCD5E"));

                set1.setLabel("BMI");
                break;
            case BodyFatConst.TYPE_BMR:
                set1.setHighLightColor(Color.parseColor("#FA602E"));
                set1.setColor(Color.parseColor("#FA602E"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#FA602E"));
                set1.setLabel("BMR");
                break;
            case BodyFatConst.TYPE_BONECONTENT:
                set1.setHighLightColor(Color.parseColor("#EE9900"));
                set1.setColor(Color.parseColor("#EE9900"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#EE9900"));
                set1.setLabel("骨含量");
                break;
            case BodyFatConst.TYPE_VISCERAL_LEVAL:
                set1.setHighLightColor(Color.parseColor("#30D2C7"));
                set1.setColor(Color.parseColor("#30D2C7"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#30D2C7"));
                set1.setLabel("内脏等级");

                break;
            case BodyFatConst.TYPE_FAT_RATION:
                set1.setHighLightColor(Color.parseColor("#FF6263"));
                set1.setColor(Color.parseColor("#FF6263"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#FF6263"));
                set1.setLabel("脂肪率");
                break;
            case BodyFatConst.TYPE_WATER_CONTENT:

                set1.setLabel("水分含量");
                set1.setHighLightColor(Color.parseColor("#297FBA"));
                set1.setColor(Color.parseColor("#297FBA"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#297FBA"));
                break;
            case BodyFatConst.TYPE_MUSLE_CONTENT:
                set1.setHighLightColor(Color.parseColor("#FFBD0C"));
                set1.setColor(Color.parseColor("#FFBD0C"));    //设置曲线的颜色
                set1.setCircleColorHole(Color.parseColor("#FFFFFF"));
                set1.setCircleColor(Color.parseColor("#FFBD0C"));
                set1.setLabel("肌肉含量");
                break;
        }
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        dataSets.add(set3);
        dataSets.add(set2);
        dataSets.add(set4);
        dataSets.add(set1);

        data = new LineData(xLables, dataSets);

        mChart.setData(data);
        mChart.setNoDataTextDescription("");
        mChart.setVisibleXRangeMaximum(7);
        mChart.moveViewToX(lastXindex);

        mChart.getViewPortHandler().setMaximumScaleX(4f);
        mChart.setScaleMinima(0.7f, 1f);
        mChart.invalidate();
    }

    public void update() {
        //TODO findDateBYMonth
       /* int days = TimeStampUtil.daysInMonth(BodyFatConst.month);

        if(xLables.size()>0){xLables.clear();}

        for (int i = 0; i < days; i++) {
            xLables.add(i + 1 + "");
        }*/
        getMonthHistoryData(BodyFatConst.month);
        if(dataMonth.size()<=0){
            getDataByNet(BodyFatConst.month);
        }else{
            setData(BodyFatConst.TYPE_WEIGHT);
            mChart.notifyDataSetChanged();
        }

    }

    /**
     * 获取月历史记录
     * month yyyy-mm
     */
    public void getMonthHistoryData(String month) {
        dataMonth.clear();
        //TODO


        try {

            String[] columExps = new String[]{"ymd","weight","bmi", "kcal", "visceraFat","bone","fat","muscle","water", "MAX(longTime)"};
            DbModelSelector dbModelSelector = MyApplication.getDB().selector(BodyFatSaid.class).
                    where("ymd", "like", "%" + month + "%").and("username", "=", MyApplication.getInstance().getCurrentUserName()).groupBy("ymd").select(columExps);

            List<DbModel> all = dbModelSelector.findAll();

            if(all!=null && all.size()>0) {
                for (int i = 0;i<all.size();i++){
                    BodyFatSaid bodyFatSaid = new BodyFatSaid();
                    bodyFatSaid.setYmd(all.get(i).getString("ymd"));
                    bodyFatSaid.setWeight(all.get(i).getFloat("weight"));
                    bodyFatSaid.setBmi(all.get(i).getString("bmi"));
                    bodyFatSaid.setKcal(all.get(i).getInt("kcal"));
                    bodyFatSaid.setVisceraFat(all.get(i).getFloat("visceraFat"));
                    bodyFatSaid.setBone(all.get(i).getFloat("bone"));
                    bodyFatSaid.setMuscle(all.get(i).getFloat("muscle"));
                    bodyFatSaid.setWater(all.get(i).getFloat("water"));
                    bodyFatSaid.setFat(all.get(i).getFloat("fat"));
                    bodyFatSaid.setLongTime(all.get(i).getLong("MAX(longTime)"));
                    dataMonth.add(bodyFatSaid);
                }
               Log.i("body", "weight is " + all.get(0).getString("weight"));
                Log.i("body", "bmi is " + all.get(0).getString("bmi"));
                Log.i("body", "kcal is " + all.get(0).getString("kcal"));
                Log.i("body", "visceraFat is " + all.get(0).getString("visceraFat"));
                Log.i("body", "bone is " + all.get(0).getString("bone"));
                Log.i("body", "fat is " + all.get(0).getString("fat"));
                Log.i("body", "muscale is " + all.get(0).getString("muscle"));
                Log.i("body", "water is " + all.get(0).getString("water"));
                Log.i("body", "max is " + all.get(0).getString("MAX(longTime)"));

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }





    //从网上分页获取数据
    private void  getDataByNet(String month){

        if(!NetTool.isNetwork(mContext,true)){
            return;
        }
        Common_dialog.startWaitingDialog(getActivity(), "正在加载数据...");

        String url = Base_Url.downloaddayUrl;
        HashMap<String, Object> param = new HashMap<>();
        param.put("modelType", "bodyfat");
        param.put("url", url);
        param.put("beginDate", month+"-01 00:00:00");
        param.put("endDate", TimeStampUtil.getLastDayOfMonth_(month)+" 23:59:59");

        HistoryDownd.getHistory(mContext, param, new HistoryDownd.RegistOnGetResult() {
            @Override
            public void OnGetResult(int code, String msg, String data) {
                if (code == 11) {
                    MyApplication.getInstance().exit();
                    Toast.makeText(mContext, "用户未登陆,请重新登陆", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else if (code == 0) {
                    dataMonth.clear();
                    OemLog.log(TAG,"月下载记录是" + data);
                    List<BodyJson> list = JSON.parseArray(data, BodyJson.class);
                    if(list == null || list.size() == 0){
                        Toast.makeText(mContext,"无数据",Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        BodyFatSaid bean = new BodyFatSaid();
                        bean.setUserName(list.get(i).getUsername());
                        bean.setIsUpload(true);
                        bean.setLongTime(list.get(i).getTestTime());
                        float bone =list.get(i).getBone();
                        bean.setBone(bone);
                        bean.setWeight(Float.parseFloat(list.get(i).getWeight()));

                        float fat= list.get(i).getFat();
                        bean.setFat(fat);

                        float water = list.get(i).getWater();
                        bean.setWater(water);

                        float muscle = list.get(i).getMuscle();
                        bean.setMuscle(muscle);

                        float visceraFat = list.get(i).getVisceralLevel();
                        bean.setVisceraFat(list.get(i).getVisceralLevel());

                        String bmi = list.get(i).getBmi();
                        bean.setBmi(bmi);
                        float weight = Float.parseFloat(list.get(i).getWeight());
                        String sex = PreferencesUtils.getString(mContext,"sex","男");
                        String age = PreferencesUtils.getString(mContext, "age", "20");
                        String height = PreferencesUtils.getString(mContext,"height","175");

                        int kcal = list.get(i).getBmr();
                        bean.setKcal(kcal);
                        String weightGrade = FatRule.getRate(weight,BodyFatConst.TYPE_WEIGHT,sex,Integer.parseInt(age),Integer.valueOf(height),weight);
                        String bmiGrade = weightGrade;
                        String bmrGrade = FatRule.getRate(weight,BodyFatConst.TYPE_BMR,sex,Integer.parseInt(age),Integer.valueOf(height),kcal);
                        String viscerGrade = FatRule.getRate(weight,BodyFatConst.TYPE_VISCERAL_LEVAL,sex,Integer.parseInt(age),Integer.valueOf(height),visceraFat);
                        String boneGrade = FatRule.getRate(weight,BodyFatConst.TYPE_BONECONTENT,sex,Integer.parseInt(age),Integer.valueOf(height),bone);
                        String fatGrade = FatRule.getRate(weight,BodyFatConst.TYPE_FAT_RATION,sex,Integer.parseInt(age),Integer.valueOf(height),fat);
                        String muscleGrade = FatRule.getRate(weight,BodyFatConst.TYPE_MUSLE_CONTENT,sex,Integer.parseInt(age),Integer.valueOf(height),muscle/100);
                        String waterGrade = FatRule.getRate(weight,BodyFatConst.TYPE_WATER_CONTENT,sex,Integer.parseInt(age),Integer.valueOf(height),water/100);

                        bean.setWeightGrade(weightGrade);
                        bean.setBmrGrade(bmrGrade);
                        bean.setVisceraGrade(viscerGrade);
                        bean.setBoneGrade(boneGrade);
                        bean.setFatGrade(fatGrade);
                        bean.setMuscleGrade(muscleGrade);
                        bean.setWaterGrade(waterGrade);

                        long time = list.get(i).getTestTime();
                        bean.setYmd(TimeStampUtil.currTime3(time));
                        bean.setTestHour(TimeStampUtil.getHour(time) + "");
                        bean.setTestMinute(TimeStampUtil.getDoubleDay(time) + "");
                        bean.setDataSource("网上");
                        dataMonth.add(bean);
                        try {
                            MyApplication.getDB().saveOrUpdate(bean);

                            Log.i(TAG, " after net insert db");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                    }
                    setData(BodyFatConst.TYPE_WEIGHT);
                    mChart.notifyDataSetChanged();
                  /*  if(dataMonth.size()>0){
                        setData(BodyFatConst.TYPE_WEIGHT);
                        mChart.notifyDataSetChanged();
                    }else{
                        if (mChart.getData() != null) {
                            mChart.clear();
                            mChart.setNoDataText("无数据");
                        }
                    }*/
                   /* setData(BodyFatConst.TYPE_WEIGHT);
                    mChart.notifyDataSetChanged();*/
                } else {
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
                Common_dialog.stop_WaitingDialog();
            }
        });

    }
}
