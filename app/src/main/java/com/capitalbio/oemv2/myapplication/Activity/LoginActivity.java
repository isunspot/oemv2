package com.capitalbio.oemv2.myapplication.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.capitalbio.oemv2.myapplication.Base.MyApplication;
import com.capitalbio.oemv2.myapplication.Bean.LoginUser;
import com.capitalbio.oemv2.myapplication.Const.Base_Url;
import com.capitalbio.oemv2.myapplication.Devices.BodyFatDevices.src.com.bayh.sdk.ble.bean.BodyFatSaid;
import com.capitalbio.oemv2.myapplication.NetUtils.HttpTools;
import com.capitalbio.oemv2.myapplication.NetUtils.NetTool;
import com.capitalbio.oemv2.myapplication.R;
import com.capitalbio.oemv2.myapplication.Tools.MDTools;
import com.capitalbio.oemv2.myapplication.Utils.OemLog;
import com.capitalbio.oemv2.myapplication.Utils.PreferencesUtils;
import com.capitalbio.oemv2.myapplication.Utils.TimeStampUtil;
import com.capitalbio.oemv2.myapplication.Utils.Utility;
import com.capitalbio.oemv2.myapplication.Utils.Utils;
import com.capitalbio.oemv2.myapplication.View.ClearEditText;
import com.capitalbio.oemv2.myapplication.View.GlideCircleTransform;
import com.capitalbio.oemv2.myapplication.View.RoundImageView;
import com.capitalbio.oemv2.myapplication.adapter.RegistLastDeletListener;
import com.capitalbio.oemv2.myapplication.adapter.SelectUserAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.table.ColumnEntity;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 登陆界面
 */
public class LoginActivity extends Activity {

    private Button btLogin;
    private ClearEditText username, password;
    private Context context;
    String token;
    String usernameString, passwordString;
    private ProgressDialog mProgressDialog = null;
    private ImageView iv_user_photo;
    private RelativeLayout rl_loginUser;
    private CheckBox cb_remeber_pw;//记住密码
    private String TAG = "LoginActivity";
    private ListView listView;
    private List<LoginUser> userList = new ArrayList<>();
    private LoginUser userbylike = new LoginUser();

    private SelectUserAdapter mSelectAdapter;
    private boolean isfirst;
    public static final int REQUEST_PERMISSON_SUCCESSFULL = 200;

    //Activity最外层的Layout视图
    private View activityRootView;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bt_login:
                    OemLog.log(TAG,"datelogin...");
                    if(Utils.isFastClick()){
                        OemLog.log(TAG,"datelogin step0...");

                    }else{
                        OemLog.log(TAG,"datelogin step1...");

                        if (!(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                            //your code that requires permission
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                    REQUEST_PERMISSON_SUCCESSFULL);
                        }else{
                            checkUserInfoAndLogin();
                        }
                    }
                    break;
                case R.id.tv_register:
                    Utility.startActivity(context, RegisterActivity.class);
                    break;
                case R.id.tv_forgetPwd:
                    Utility.startActivity(context, ForgetPwdActivity.class);
                    break;
                case R.id.bt_selectUser:
                    if(bt_selectUser.isChecked()){
                        try {
                            userList=MyApplication.getInstance().getDB().findAll((LoginUser.class));
                            if(userList != null&&userList.size()>0){
                                bt_selectUser.setClickable(true);
                                showPopupWindow();

                            }else{
                                bt_selectUser.setClickable(false);
                            }

                        } catch (DbException e) {
                            OemLog.log(TAG,e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        dismissPopWindow();
                    }
                    break;
            }

        }
    };

    private PopupWindow pop;

   private ToggleButton bt_selectUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title

        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_login);

        context = this;
        if (!(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            //your code that requires permission
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSON_SUCCESSFULL);
        }
       /* if (PreferencesUtils.getBoolean(context, "isChecked")&&MyApplication.getInstance().getCurrentUserName()!=null) {
            Utility.startActivity(context, MainActivity.class);
            finish();
        }*/


        init();
        initData();
    }



    /**
     *查询用户
     */
    private void initData() {

        showTriangle();
       /* String loginName = PreferencesUtils.getString(context, "loginNum");
        if(loginName==null||"".equals(loginName)){
            return;
        }
        username.setText(loginName);*/
        Intent intent = getIntent();
        String usernameString = intent.getStringExtra("loginname");
        String pwdString = intent.getStringExtra("password");
        if(!TextUtils.isEmpty(usernameString)){
            username.setText(usernameString);
            password.setText(pwdString);
            return;
        }

        try {
           LoginUser  loginUser = MyApplication.getDB().selector(LoginUser.class).orderBy("loginTime_",true).findFirst();
          if(loginUser!=null){
              username.setText(loginUser.getUsername());
              byte[] bytePic = loginUser.getHeadPic();

              if (bytePic != null && bytePic.length > 0) {
                  Glide.with(context)
                          .load(bytePic)
                          .transform(new GlideCircleTransform(this)).into(iv_user_photo);
               /*   Bitmap bitmap = com.capitalbio.oemv2.myapplication.Devices.BloodPressDevice.ByteUtil.byteToBitmap(bytePic);
                  iv_user_photo.setImageBitmap(bitmap);*/
              }
              if(loginUser.isRememberName()){
                  cb_remeber_pw.setChecked(true);
                  //password.setText(PreferencesUtils.getString(context,"loginPwd"));
                  password.setText(loginUser.getPassword());
              }else{
                  cb_remeber_pw.setChecked(false);
                  password.setText("");
              }
          }

        } catch (DbException e) {
            e.printStackTrace();
        }



    }

   /* @Override
    protected void onResume() {
        super.onResume();

        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(this);
    }*/
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private void init() {

        //申请权限
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight/3;
        btLogin = (Button) findViewById(R.id.bt_login);

        //设置点击监听
        btLogin.setOnClickListener(onClickListener);
        username = (ClearEditText) findViewById(R.id.et_phone_username);
        password = (ClearEditText) findViewById(R.id.edit_pwd);
        cb_remeber_pw = (CheckBox) findViewById(R.id.cb_remeber_pw);
        findViewById(R.id.tv_register).setOnClickListener(onClickListener);
        findViewById(R.id.tv_forgetPwd).setOnClickListener(onClickListener);
        activityRootView = findViewById(R.id.activityRootView);

        bt_selectUser = (ToggleButton) findViewById(R.id.bt_selectUser);
        bt_selectUser.setOnClickListener(onClickListener);
        rl_loginUser = (RelativeLayout) findViewById(R.id.rl_loginUser);

        iv_user_photo = (ImageView) findViewById(R.id.iv_user_photo);
        Glide.with(context)
                .load(R.drawable.ic_scene)
                .transform(new GlideCircleTransform(context)).into(iv_user_photo);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               usernameString =s.toString();
                try {
                    userbylike = MyApplication.getDB().selector(LoginUser.class).where("username", "=", usernameString).findFirst();

                  if(userbylike!=null){
                      password.setText(userbylike.getPassword());
                      byte[] bytePic = userbylike.getHeadPic();
                      if (bytePic != null && bytePic.length > 0) {
                          Glide.with(context)
                                  .load(bytePic)
                                  .transform(new GlideCircleTransform(context)).into(iv_user_photo);
                        /*  Bitmap bitmap = com.capitalbio.oemv2.myapplication.Devices.BloodPressDevice.ByteUtil.byteToBitmap(bytePic);
                          iv_user_photo.setImageBitmap(bitmap);*/
                      }else{

                          Glide.with(context)
                                  .load(R.drawable.ic_scene)
                                  .transform(new GlideCircleTransform(context)).into(iv_user_photo);
                         // iv_user_photo.setImageResource(R.drawable.ic_scene);
                      }
                  } else{
                      password.setText("");

                      Glide.with(context)
                              .load(R.drawable.ic_scene)
                              .transform(new GlideCircleTransform(context)).into(iv_user_photo);
                      //iv_user_photo.setImageResource(R.drawable.ic_scene);
                      cb_remeber_pw.setChecked(false);
                  }
                } catch (DbException e) {
                    e.printStackTrace();
                }


            }
        });

    }



    private void checkUserInfoAndLogin() {
        //判断用户的输入信息
        OemLog.log(TAG,"datelogin check...");

        check();
        if (check() && NetTool.isNetwork(context, true)) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在登陆,请稍后...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);

            if(NetTool.isNetwork(context,true)){
                login();
            }
        }


    }

    /**
     * 登陆校验
     */
    private boolean check() {
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();
        if (usernameString == null || usernameString.trim().equals("")) {
            Toast.makeText(context, "请输入登陆帐号！", Toast.LENGTH_LONG).show();
            return false;
        }
        if (passwordString == null || passwordString.trim().equals("")) {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;


    }

    public void login() {

        try {
            OemLog.log(TAG,"datelogin... login");

            //构造请求json对象
            JSONObject jsonObject = new JSONObject();
            JSONObject dataObj = new JSONObject();
            jsonObject.put("apikey", MyApplication.getInstance().apikey);
            jsonObject.put("username", username.getText().toString());
            String password_ = MDTools.MD5(password.getText().toString());
            String mobileId = MyApplication.getInstance().getIMEI();
            OemLog.log(TAG,"imei is" + mobileId);
            token = MDTools.MD5(MyApplication.getInstance().apikey + password_+mobileId);
            jsonObject.put("token", token);
            dataObj.put("mobileId",MyApplication.getInstance().getIMEI());
            jsonObject.put("data",dataObj.toString());
            OemLog.log(TAG,token);

            //
            HttpTools.post(this, Base_Url.Url_Base + Base_Url.LoginKey_Url, jsonObject, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    mProgressDialog.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    String s = new String(responseBody);
                    Log.i(TAG, "result if sucess is" + s);
                    try {
                        JSONObject jo = new JSONObject(s);
                        int code = jo.optInt("code");

                        if (code == 0) {
                            //登陆成功
                            Toast.makeText(context, "登陆成功！", Toast.LENGTH_SHORT).show();
                            PreferencesUtils.putBoolean(MyApplication.getInstance(), "isLogin", true);
                            String data = jo.getString("data");
                            Log.i("info",data);
                            if (data.trim() != null) {
                                JSONObject dataObj = new JSONObject(data);
                                LoginUser user = new LoginUser();
                                user.setToken(token);
                                user.setLoginTime_(System.currentTimeMillis());
                                user.setLoginTime(TimeStampUtil.currTime2(0));
                                user.setIsLogin("true");
                                user.setId(dataObj.optString("id"));
                                user.setPassword(passwordString);

                                user.setIdcard(dataObj.optString("idcard"));
                                user.setAge(dataObj.optString("age"));
                                user.setCareer(dataObj.optString("career"));
                                user.setCtime(dataObj.optString("ctime"));
                                user.setHeight(dataObj.optString("height"));
                                user.setWeight(dataObj.optString("weight"));
                                user.setNickname(dataObj.optString("nickname"));
                                user.setSex(dataObj.optString("sex"));
                                user.setMobile(dataObj.optString("mobile"));

                                String headData = dataObj.optString("headImage");
                                if(headData!=null){
                                    byte[] headBytes = Base64.decode(headData, Base64.DEFAULT);
                                    user.setHeadPic(headBytes);
                                }else{
                                    user.setHeadPic(null);

                                }
                                if (cb_remeber_pw.isChecked()) {

                                    user.setIsRememberName(true);
                                    PreferencesUtils.putBoolean(context, "isChecked", true);
                                    PreferencesUtils.putBoolean(context, user.getUsername(), true);

                                } else {
                                    user.setIsRememberName(false);

                                    PreferencesUtils.putBoolean(context, "isChecked", false);
                                    PreferencesUtils.putBoolean(context, user.getUsername(), false);

                                }
                                user.setUsername(dataObj.optString("username"));
                                user.setLoginName(usernameString);
                                PreferencesUtils.putBoolean(context, "isLogin", true);
                              //  MyApplication.getInstance().getDB().dropTable(LoginUser.class);
                                //MyApplication.getInstance().getDB().addColumn(LoginUser.class,"loginTime");

                         /*       TableEntity<?> table = TableEntity.get(MyApplication.getDB(), LoginUser.class);
                                if( table.tableIsExist() ){
                                    OemLog.log(TAG,"存在用户表");

                                    LinkedHashMap<String, ColumnEntity> columnMap = table.getColumnMap();
                                    if(!columnMap.containsKey("isLogin")){
                                        OemLog.log(TAG,"不包含新增字段");

                                        //这几个字段是后加的
                                        MyApplication.getInstance().getDB().addColumn(LoginUser.class,"token");
                                        MyApplication.getInstance().getDB().addColumn(LoginUser.class,"isLogin");
                                        MyApplication.getInstance().getDB().addColumn(LoginUser.class,"loginTime");
                                        MyApplication.getInstance().getDB().addColumn(LoginUser.class,"loginTime_");
                                        MyApplication.getInstance().getDB().addColumn(LoginUser.class,"exitlogintime");
                                    }
                                  OemLog.log(TAG,"COLOUM IS " +  columnMap.keySet().toString());
                                }*/

                                MyApplication.getInstance().getDB().saveOrUpdate(user);
                                MyApplication.getInstance().setCurrentUser(user);
                             //  Log.i(TAG, "插入头像后" + MyApplication.getInstance().getDB().findFirst(LoginUser.class).getHeadPic());
                                PreferencesUtils.putString(context, "username", user.getUsername());
                                String birth = dataObj.optString("age");
                                String height = dataObj.optString("height");
                                String weight = dataObj.optString("weight");
                                int age = TimeStampUtil.getAgeByBirth(birth);
                                OemLog.log("loginBirth", "birth" + birth + "  age" + age);

                                PreferencesUtils.putString(context, "mobile", user.getMobile());
                                PreferencesUtils.putString(context, "token", token);

                                PreferencesUtils.putString(context,"loginNum",usernameString);
                                PreferencesUtils.putString(context, "loginPwd", passwordString);

                                OemLog.log(TAG, token);

                             //   Log.d("TAG", "loginuser is " + user.toString());
                                if(birth!=""&&birth!=null){
                                    PreferencesUtils.putBoolean(context,"personInfo",true);
                                    PreferencesUtils.putString(context, "sex", dataObj.optString("sex"));
                                    PreferencesUtils.putString(context, "birth", birth);
                                    PreferencesUtils.putString(context, "age", age+"");
                                    PreferencesUtils.putString(context, "height", height);
                                    PreferencesUtils.putString(context,"weight",weight);

                                    Utility.startActivity(context, MainActivity.class);
                                    finish();
                                }else{
                                    Utility.startActivity(context, PersonalDetailsAcitivity.class);
                                }

                            }


                        } else if (code == 13) {
                            Toast.makeText(context, "密码不正确！", Toast.LENGTH_SHORT).show();
                            password.setText("");

                        } else if (code == 10) {
                            Toast.makeText(context, getResources().getString(R.string.no_user), Toast.LENGTH_SHORT).show();
                            username.setFocusable(true);
                            username.requestFocus();
                            username.setFocusableInTouchMode(true);
                            username.setText("");
                            password.setText("");

                        }

                    } catch (JSONException e) {
                        //Toast.makeText(context,"json  解析异常",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (DbException e) {
                      //Toast.makeText(context,"db操作异常",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        OemLog.log(TAG, e.getMessage());
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i(TAG, "result if faliure is" + " 服务器异常");
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(context, "服务器异常！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);

    }

    RegistLastDeletListener callback = new RegistLastDeletListener() {
        @Override
        public void onLastDeleteListener() {

            if(userList.size()==0){
                bt_selectUser.setVisibility(View.GONE);
                username.setText("");
                password.setText("");
                cb_remeber_pw.setChecked(false);

            }else{
                HashMap<String,String> map = new HashMap<>();
                for(int i = 0;i<userList.size();i++){
                    map.put("username",userList.get(i).getUsername());
                }
                if(!map.containsValue(usernameString)){
                    username.setText("");
                    password.setText("");
                    cb_remeber_pw.setChecked(false);
                }
                bt_selectUser.setClickable(true);
            }

            dismissPopWindow();
        }
    };

    private void showPopupWindow() {
        if(pop == null){
            listView = new ListView(this);
            listView.setDividerHeight(1);
            // listView.setBackgroundResource(R.drawable.kge_feek_bg);
            listView.setCacheColorHint(0x00000000);
            pop = new PopupWindow(listView, rl_loginUser.getWidth(), LayoutParams.WRAP_CONTENT, true);
        }
        mSelectAdapter = new SelectUserAdapter(context,userList,callback);

        listView.setAdapter(mSelectAdapter);
        pop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));//
        pop.showAsDropDown(rl_loginUser, 0, 0);
        pop.setOutsideTouchable(true);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bt_selectUser.setChecked(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //tv_text_age.setText(datas.get(position));
                dismissPopWindow();
                LoginUser user = userList.get(position);
                String name = user.getUsername();
                username.setText(name);
                byte[] bytePic = user.getHeadPic();
                if (bytePic != null && bytePic.length > 0) {
                    Glide.with(context)
                            .load(bytePic)
                            .transform(new GlideCircleTransform(context)).into(iv_user_photo);
                  /*  Bitmap bitmap = com.capitalbio.oemv2.myapplication.Devices.BloodPressDevice.ByteUtil.byteToBitmap(bytePic);
                    iv_user_photo.setImageBitmap(bitmap);*/
                }else{

                    Glide.with(context)
                            .load(R.drawable.ic_scene)
                            .transform(new GlideCircleTransform(context)).into(iv_user_photo);
                   // iv_user_photo.setImageResource(R.drawable.ic_scene);
                }
                if (user.isRememberName()) {
                    password.setText(userList.get(position).getPassword());
                    cb_remeber_pw.setChecked(true);
                } else {
                    password.setText(null);
                    cb_remeber_pw.setChecked(false);
                }

            }
        });
    }
    public void dismissPopWindow(){
        if(pop!=null&&pop.isShowing()){
            pop.dismiss();
            bt_selectUser.setChecked(false);
        }
    }


  /*  @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){

            //Toast.makeText(this, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();

        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){

            //Toast.makeText(this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
            try {
                if(username.isFocused() && !TextUtils.isEmpty(usernameString)){
                    userList = MyApplication.getDB().selector(LoginUser.class).where("username", "like", "%"+usernameString+"%").findAll();

                    if(userList.size()>0){
                        showPopupWindow();
                    }

                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }*/

    public void  showTriangle(){
        try {
            List<LoginUser> list = MyApplication.getDB().findAll(LoginUser.class);
            if(list==null||list.size()==0){
                bt_selectUser.setVisibility(View.GONE);
            }else{
                bt_selectUser.setVisibility(View.VISIBLE);

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Utils.lastClickTime = 0;
        super.onPause();
    }

}
