package com.capitalbio.oemv2.myapplication.Activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capitalbio.oemv2.myapplication.Fragment.BraceleteFragmentDay;
import com.capitalbio.oemv2.myapplication.Fragment.BraceleteFragmentDaySleep;
import com.capitalbio.oemv2.myapplication.Fragment.BraceleteFragmentMonth;
import com.capitalbio.oemv2.myapplication.R;
import com.capitalbio.oemv2.myapplication.dialog.ChangeBirthDialog;

import java.util.Date;

/**
 * Created by chengkun on 15-11-4.
 */
public class Bracelete2Activity extends Activity implements View.OnClickListener {

    RelativeLayout rl_fgcontainer;
    FragmentManager fragmentManager;
    BraceleteFragmentDay fgBraceleteDay;
    BraceleteFragmentMonth fgBraceleteMonth;
    BraceleteFragmentDaySleep fgBraceleteDaySleep;
    FragmentTransaction trx ;
    private ImageView iv_bracelete_set;//set
    private TextView tv_year,tv_month,tv_day;//datewheelview
    private TextView tv_switch_day,tv_switch_month;//switchdateview
    private LinearLayout ll_date,ll_switch_date,ll_switch_sportorsleep;
    private TextView tv_switch_sports,tv_switch_sleep;
    private boolean isSports= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracelete);
        initView();
    }


    @Override
    public void onClick(View v) {
        Bundle bundle =new Bundle();
        switch (v.getId()){

            case R.id.tv_switch_day:

                ll_switch_date.setBackground(getResources().getDrawable(R.drawable.switch_bracelete_day));
                tv_switch_day.setTextColor(Color.parseColor("#0EA8C3"));
                tv_switch_month.setTextColor(Color.WHITE);
            /*    bundle.putBoolean("isSports", isSports);
                fgBraceleteDay.setArguments(bundle);*/
                FragmentTransaction trans = getFragmentManager()
                        .beginTransaction();
                if(isSports){
                    rl_fgcontainer.setBackground(getResources().getDrawable(R.drawable.bg_tang_zhi_san_xiang));
                    trans.replace(R.id.ll_fg_body,fgBraceleteDay).commit();
                }else{
                    rl_fgcontainer.setBackground(getResources().getDrawable(R.drawable.bg_shou_huan));
                    trans.replace(R.id.ll_fg_body,fgBraceleteDaySleep).commit();
                }


                break;
            case R.id.tv_switch_month:
                if(isSports){
                    rl_fgcontainer.setBackground(getResources().getDrawable(R.drawable.bg_tang_zhi_san_xiang));
                }else{
                    rl_fgcontainer.setBackground(getResources().getDrawable(R.drawable.bg_shou_huan));

                }

                bundle.putBoolean("isSports",isSports);
                fgBraceleteMonth.setArguments(bundle);
                ll_switch_date.setBackground(getResources().getDrawable(R.drawable.switch_bracelete_month));
                tv_switch_month.setTextColor(Color.parseColor("#0EA8C3"));
                tv_switch_day.setTextColor(Color.WHITE);
                iv_bracelete_set.setVisibility(View.GONE);

                FragmentTransaction tran = getFragmentManager()
                        .beginTransaction();
                tran.replace(R.id.ll_fg_body,fgBraceleteMonth).commit();
            /*    trx.hide(fgBraceleteDay);
                trx.show(fgBraceleteMonth).commit();*/
                break;
            case R.id.tv_switch_sports:
                isSports = true;
                ll_switch_date.setBackground(getResources().getDrawable(R.drawable.switch_bracelete_day));
                rl_fgcontainer.setBackground(getResources().getDrawable(R.drawable.bg_tang_zhi_san_xiang));
                ll_switch_sportorsleep.setBackground(getResources().getDrawable(R.drawable.switch_sportorsleep));
                tv_switch_sports.setTextColor(Color.WHITE);
                tv_switch_sleep.setTextColor(Color.parseColor("#00AEB5"));
                tv_switch_day.setTextColor(Color.parseColor("#00AEB5"));
                tv_switch_month.setTextColor(Color.WHITE);

                FragmentTransaction transports = getFragmentManager()
                        .beginTransaction();
                transports.replace(R.id.ll_fg_body,fgBraceleteDay).commit();
                break;
            case R.id.tv_switch_sleep:
                isSports = false;
                ll_switch_date.setBackground(getResources().getDrawable(R.drawable.switch_bracelete_day));
                rl_fgcontainer.setBackground(getResources().getDrawable(R.drawable.bg_shou_huan));
                ll_switch_sportorsleep.setBackground(getResources().getDrawable(R.drawable.switch_sleeporsports_));
                tv_switch_sleep.setTextColor(Color.WHITE);
                tv_switch_sports.setTextColor(Color.parseColor("#001749"));
                tv_switch_day.setTextColor(Color.parseColor("#001749"));
                tv_switch_month.setTextColor(Color.WHITE);
                FragmentTransaction tranSleep = getFragmentManager()
                        .beginTransaction();
                tranSleep.replace(R.id.ll_fg_body,fgBraceleteDaySleep).commit();
                break;

        }
    }

    public void initView(){

        rl_fgcontainer = (RelativeLayout) findViewById(R.id.rl_fgcontainer);
        fgBraceleteMonth =new BraceleteFragmentMonth();
        fgBraceleteDay = new BraceleteFragmentDay();
        fgBraceleteDaySleep= new BraceleteFragmentDaySleep();
        trx = getFragmentManager().beginTransaction();
      //  trx.add(R.id.ll_fg_body, fgBraceleteMonth);
        trx.add(R.id.ll_fg_body, fgBraceleteDay).show(fgBraceleteDay).commit();
        iv_bracelete_set = (ImageView) findViewById(R.id.iv_bracelete_set);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_day = (TextView) findViewById(R.id.tv_day);

        Date date =new Date();
        curyearString = date.getYear()+1900+"年";
        curmonthString = date.getMonth()+1+"月";
        curdayString = date.getDate()+"日";

        tv_year.setText(curyearString);
        tv_month.setText(curmonthString);
        tv_day.setText(curdayString);
        tv_switch_day = (TextView) findViewById(R.id.tv_switch_day);
        tv_switch_month = (TextView) findViewById(R.id.tv_switch_month);
        ll_switch_date = (LinearLayout) findViewById(R.id.ll_switch_date);
        ll_switch_sportorsleep = (LinearLayout) findViewById(R.id.ll_switch_sportorsleep);
        tv_switch_sports = (TextView) findViewById(R.id.tv_switch_sports);
        tv_switch_sleep = (TextView) findViewById(R.id.tv_switch_sleep);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);

        ll_date.setOnClickListener(this);
        tv_switch_day.setOnClickListener(this);
        tv_switch_month.setOnClickListener(this);
        tv_switch_sleep.setOnClickListener(this);
        tv_switch_sports.setOnClickListener(this);
        findViewById(R.id.iv_top_left_return).setOnClickListener(this);
    }
}