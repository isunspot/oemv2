package com.capitalbio.oemv2.myapplication.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.capitalbio.oemv2.myapplication.R;


public class Utility {

	// 为每一个groupactivity中的子页面设置index,每一个页面对应一个index;
	public static int currentTabIndex = 0;
	public final static int TAB_INDEX_SEARCH = 0;
	public final static int TAB_INDEX_ORDER = 1;
	public final static int TAB_INDEX_HOTAL = 2;
	public final static int TAB_INDEX_ACCOUNT = 3;
	public final static int TAB_INDEX_MORE = 4;

	public static ProgressDialog progressDialog = null;
	
	/***
	 * bitmap等比缩小
	 * 
	 * **/
	 public static Bitmap bitmapSmall(final Bitmap bitmap) {
		  Matrix matrix = new Matrix(); 
		  matrix.postScale(0.5f,0.5f); //长和宽放大缩小的比例
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		  return resizeBmp;
		 }
	 
	/**
	 * 判断数据是否为0
	 * 
	 * 
	 * */
	public static boolean isZeroValue(String value) {
		if ("".equals(value)||"null".equals(value)||"0".equals(value) || "0.0".equals(value) || "0.00".equals(value)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取数据是否为空
	 * 
	 * @param value 
	 * @return  
	 * String 如果数据为空，返回 "0"
	 */
	public static String isNullValue(String value) {
		if (null == value || "".equals(value)) {
			return "0";
		} else {
			return value;
		}
	}
	
	/**
     * 获取数据是否为空
     * 
     * @param value 
     * @return  
     * String 如果数据为空，返回 ""
     */
    public static String isNullValue2(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

	/***
	 * 
	 * isEntity
	 * 
	 * **/
	public static boolean isEntity(String value1) {
		if (null == value1 || "".equals(value1) || "0".equals(value1)) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 判断网络
	 * 
	 * @param mActivity
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity mActivity) {
		// return true;
		Context context = mActivity.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static long lastClickTime;

	/**
	 * 是否是快速两次点击
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	//字符串转化时间类型
public static Date getDate(String mytime){
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
	String dstr="2008-4-24";  
	Date date = null;
	try {
		date = sdf.parse(mytime);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return date;  
}
	// 将字符串转为时间戳

	@SuppressLint("SimpleDateFormat")
	public static String getTime(String user_time) {
	String re_time = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date d;
	try {

	d = sdf.parse(user_time);
	long l = d.getTime();
	String str = String.valueOf(l);
	re_time = str;

	} catch (ParseException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	return re_time;
	}
	
	
	/**
	 * 获得屏幕高宽
	 * 
	 * @param activity
	 * @return
	 */
	public static Point getScreenPoint(Activity activity) {
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		Point point = new Point(display.getWidth(), display.getHeight());
		return point;
	}

	/**
	 * 得到高
	 * 
	 * @param a
	 * @return
	 */
	public static int getsW(Activity a) {
		return getScreenPoint(a).x;
	}

	/**
	 * 得到宽
	 * 
	 * @param a
	 * @return
	 */
	public static int getsH(Activity a) {
		return getScreenPoint(a).y;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Activity context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Activity context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 隐藏软键盘
	public static void hiddenKeyboard(Activity activity) {
		((InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 分享 *
	 */
	public static void share(Activity mActivity, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity.startActivity(Intent.createChooser(intent,
				mActivity.getTitle()));
	}

	/**
	 * 拨打电话
	 * 
	 * @param phonenumber
	 */
	public static void tel(Activity a, String phonenumber) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		if (phonenumber != null) {
			phonenumber = phonenumber.replace(",", "");
			phonenumber = phonenumber.replace("，", "");
		}
		intent.setData(Uri.parse("tel:" + phonenumber));
		a.startActivity(intent);
	}

	/* 验证手机号是否合法 */
	public static boolean checkPhone(String phone) {
		// ^(13|15|18)\\d{9}$
		Pattern pattern = Pattern.compile("^(13|14|15|18)\\d{9}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}

	/**
	 * 是否是正确的邮件地址
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher mc = pattern.matcher(strEmail);
		return mc.matches();
	}

	/**
	 * 验证固定电话号码
	 * 
	 * @param strPhone
	 * @return
	 */
	public static boolean isPhone(String strPhone) {
		boolean tag = true;
		String reg = "^((0\\d{2,3})-)(\\d{7,8})(-(\\d{3,}))?$";
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(strPhone);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param strMobile
	 * @return
	 */
	public static boolean isMobile(String strMobile) {
		boolean tag = true;
		String reg = "^(13[0-9]|15[012356789]|18[02367895]|14[57])[0-9]{8}$"; // 验证手机号码
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(strMobile);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 检查邮编格式
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isPostcode(String code) {
		boolean tag = true;
		String reg = "^[1-9][0-9]{5}$";
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(code);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 身份证
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isIDCard(String code) {
		boolean tag = true;
		String reg = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(code);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 是否是6到16位字母和数字的结合
	 * 
	 * @param pass
	 * @return
	 */
	public static boolean isPassWord(String pass) {

		boolean tag = true;
		String reg = "(?![^a-zA-Z0-9]+$)(?![^a-zA-Z/D]+$)(?![^0-9/D]+$).{6,16}$";
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(pass);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 是否是ip
	 * 
	 * @param code
	 * @return
	 */

	public static boolean isIP(String code) {
		if (code.indexOf(":", 5) > 0) {
			code = code.substring(0, code.lastIndexOf(":"));
		}
		boolean tag = true;
		String reg = "^http://(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(code);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	public static boolean isIP2(String code) {
		if (code.indexOf(":", 5) > 0) {
			code = code.substring(0, code.lastIndexOf(":"));
		}
		boolean tag = true;
		String reg = "^https://(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
		final Pattern pattern = Pattern.compile(reg);
		final Matcher mat = pattern.matcher(code);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 是否是数子
	 * 
	 * @param str
	 * @return
	 */

	public static boolean isNum(String str) {
		boolean tag = true;
		tag = str.matches("[0-9]+");
		return tag;
	}

	public static void share(Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setItems(new String[] { "新浪微博", "腾讯微博", "微信", "QQ" },
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.show();
	}

	public static ArrayList<Activity> activities = new ArrayList<Activity>();

	public static ArrayList<Activity> getActivityList() {
		return activities;
	}

	private static int CircleType = 0;

	public static void SetCircleType(int type) {
		CircleType = type;
	}

	public static int GetCircleType() {
		return CircleType;
	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param imageView
	 * @param bitmap
	 * @param roundPx
	 * @return
	 *//*
	public static Bitmap getRoundedCornerBitmap(ImageView imageView,
			Bitmap bitmap, float roundPx) {
		if (roundPx == 0f) {
			return bitmap;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		imageView.setImageBitmap(output);
		ImageLoader.getInstance().getMemoryCache()
				.put(imageView.toString(), bitmap);
		return output;
	}*/

	/**
	 * 
	 * 
	 * @param textView
	 */
	public static void setTextGradient(TextView textView) {
		textView.measure(0, 0);
		Shader shader = new LinearGradient(0, 0, 0,
				textView.getMeasuredHeight(), new int[] {
						Color.argb(255, 149, 101, 44),
						Color.argb(255, 210, 185, 105),
						Color.argb(255, 149, 101, 44) }, new float[] { 0, 0.3f,
						1f }, Shader.TileMode.CLAMP);
		textView.getPaint().setShader(shader);
	}

	/**
	 * Returns application cache directory. Cache directory will be created on
	 * SD card <i>("/Android/data/[app_package_name]/cache")</i> if card is
	 * mounted. Else - Android defines cache directory on device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @return Cache {@link File directory}
	 */
	public static File getCacheDirectory(Context context) {
		File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	/**
	 * 得到缓存路径
	 * 
	 * @param context
	 * @return
	 */
	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(
				Environment.getExternalStorageDirectory(), context
						.getResources().getString(R.string.app_name)), "data");
		File appCacheDir = new File(
				new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				Log.w("Utility", "Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.e("Utility",
						"Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	/**
	 * 用于计算ListView的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 根据url生成缓存文件完整路径名
	 */
	public static String urlToFilePath(String cacheDir, String url) {

		// 扩展名位置
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}

		StringBuilder filePath = new StringBuilder();

		// 图片存取路径
		filePath.append(cacheDir).append('/');

		// 图片文件名
		filePath.append(Md5(url)).append(url.substring(index));

		return filePath.toString();
	}

	// MD5変換
	public static String Md5(String str) {
		if (str != null && !str.equals("")) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
						'9', 'a', 'b', 'c', 'd', 'e', 'f' };
				byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < md5Byte.length; i++) {
					sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
					sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
				}
				str = sb.toString();
			} catch (NoSuchAlgorithmException e) {
			} catch (Exception e) {
			}
		}
		return str;
	}

	/**
	 * 从sd卡读取图片
	 */
	public static Bitmap readBitMap(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		Bitmap bm = BitmapFactory.decodeFile(path, options);
		// Log.i("get success " + path);
		return bm;
	}

	/**
	 * 保存Bitmap到本地
	 */
	public static String SaveBitmap(Bitmap bmp, String name) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(name);

			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			// Log.i("save success " + name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return name;
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
/*

	*/
/***
	 * 从身份证中获取性别年龄
	 * 
	 * 
	 * **//*

	public static String[] userDetail (Context mContext,String idCard){
		String[] idcard=  new String [2];
		if(idCard.length()!=18){
			Utility.showToast(mContext, "身份证信息不正确");
		}else {
			String yy=idCard.substring(6,10);//出生的年份
			String mm=idCard.substring(10,12);//出生的月份
			String dd =idCard.substring(12,14);//出生的日期
			String birsday=yy+"-"+mm+"-"+dd;
			try {
				Date date= new Date();
				SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
				String s1= sdf.format(date);
				Date today=sdf.parse(s1);
				//生日
				Date mybirsday =sdf.parse(birsday);
				int age=today.getYear()-mybirsday.getYear();
				idcard[0]=age+"";
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String sex = idCard.substring(16, 17);
			if(Integer.parseInt(sex)%2==0){
				sex = "女";
			}else{
				sex ="男";
				
			}
			idcard[1]=sex;
		}
		return idcard;
	}
*/

	/**
	 * 验证身份证
	 */

	public static boolean chechCertificateNum(String s_aStr) {// 验正身份证
		// 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10
		// 5 8 4 2
		// 2.将这17位数字和系数相乘的结果相加。
		// 3.用加出来和除以11，看余数是多少？
		if (TextUtils.isEmpty(s_aStr) || s_aStr.length() < 18) {
			return false;
		}
		// 身份证计算结果对应的校验码
		String[] iii = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
		// 身份证17位系数
		int[] iiii = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		// 身份证号
		String ss = s_aStr;
		// 系数和
		int ii = 0;
		// 17位乘以相应系数，再相加
		for (int i = 0; i < 17; i++) {
			ii = ii + Integer.parseInt(ss.charAt(i) + "") * iiii[i];
		}
		// 生日验证510823198807225011
		// 当前年
		int year = Calendar.getInstance().get(Calendar.YEAR);
		// 生日_年
		int y = Integer.parseInt("" + ss.charAt(6) + ss.charAt(7)
				+ ss.charAt(8) + ss.charAt(9));
		// 生日_月
		int m = Integer.parseInt("" + ss.charAt(10) + ss.charAt(11));
		// 生日_日
		int d = Integer.parseInt("" + ss.charAt(12) + ss.charAt(13));
		// 系数和对11求余
		if (String.valueOf(ss.charAt(17)).equals(iii[ii % 11]) && y <= year
				&& y >= (year - 200) && m > 0 && m < 13 && d > 0 && d <= 31) {
			return true;
		} else {
			return false;
		}

	}

	/*
	 * public static boolean checkIdcard(String value) { int length = 0; if
	 * (value == null) { return false; } else { length = value.length();
	 * 
	 * if (length != 15) { } else if (length != 18) { } else { return false; } }
	 * String[] areasArray = { "11", "12", "13", "14", "15", "21", "22", "23",
	 * "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45",
	 * "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
	 * "81", "82", "91" };
	 * 
	 * HashSet<String> areasSet = new HashSet<String>(
	 * Arrays.asList(areasArray)); String valueStart2 = value.substring(0, 2);
	 * 
	 * if (areasSet.contains(valueStart2)) { } else { return false; }
	 * 
	 * Pattern pattern = null; Matcher matcher = null;
	 * 
	 * int year = 0; switch (length) { case 15: year =
	 * Integer.parseInt(value.substring(6, 8)) + 1900;
	 * 
	 * if (year % 4 == 0 || (year % 100 == 0 && year % 4 == 0)) { pattern =
	 * Pattern .compile(
	 * "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$"
	 * ); // 测试出生日期的合法性 } else { pattern = Pattern .compile(
	 * "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$"
	 * ); // 测试出生日期的合法性 } matcher = pattern.matcher(value); if (matcher.find())
	 * { return true; } else { return false; } case 18: year =
	 * Integer.parseInt(value.substring(6, 10));
	 * 
	 * if (year % 4 == 0 || (year % 100 == 0 && year % 4 == 0)) { pattern =
	 * Pattern .compile(
	 * "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$"
	 * ); // 测试出生日期的合法性 } else { pattern = Pattern .compile(
	 * "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$"
	 * ); // 测试出生日期的合法性 }
	 * 
	 * matcher = pattern.matcher(value); if (matcher.find()) { int S =
	 * (Integer.parseInt(value.substring(0, 1)) + Integer
	 * .parseInt(value.substring(10, 11))) 7 +
	 * (Integer.parseInt(value.substring(1, 2)) + Integer
	 * .parseInt(value.substring(11, 12))) 9 +
	 * (Integer.parseInt(value.substring(2, 3)) + Integer
	 * .parseInt(value.substring(12, 13))) 10 +
	 * (Integer.parseInt(value.substring(3, 4)) + Integer
	 * .parseInt(value.substring(13, 14))) 5 +
	 * (Integer.parseInt(value.substring(4, 5)) + Integer
	 * .parseInt(value.substring(14, 15))) 8 +
	 * (Integer.parseInt(value.substring(5, 6)) + Integer
	 * .parseInt(value.substring(15, 16))) 4 +
	 * (Integer.parseInt(value.substring(6, 7)) + Integer
	 * .parseInt(value.substring(16, 17))) 2 +
	 * Integer.parseInt(value.substring(7, 8)) 1 +
	 * Integer.parseInt(value.substring(8, 9)) 6 +
	 * Integer.parseInt(value.substring(9, 10)) * 3; int Y = S % 11; String M =
	 * "F"; String JYM = "10X98765432"; M = JYM.substring(Y, Y + 1); // 判断校验位 if
	 * (M.equals(value.substring(17, 18))) { return true; // 检测ID的校验位 } else {
	 * return false; } } else { return false; } default: return false; } }
	 */
	public static boolean is(String s) {
		if (TextUtils.isEmpty(s) || "null".equals(s)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 心电测量结果
	 */
	public static String getResult(int number) {
		String result = null;
		switch (number) {
		case 0:
			result = "波形未见异常";
			break;
		case 1:
			result = "波形疑似心跳稍快，请注意休息";
			break;
		case 2:
			result = "波形疑似心跳稍快，请注意休息";
			break;
		case 3:
			result = "波形疑似阵发性心跳过快，请咨询医生";
			break;
		case 4:
			result = "波形疑似心跳稍缓，请注意休息";
			break;
		case 5:
			result = "波形疑似心跳过缓，请注意休息";
			break;
		case 6:
			result = "波形疑似偶发心跳间期缩短，请咨询医生";
			break;
		case 7:
			result = "波形疑似心跳间期不规则，请咨询医生";
			break;
		case 8:
			result = "波形疑似心跳稍快伴有偶发心跳间期缩短，请咨询医生";
			break;
		case 9:
			result = "波形疑似心跳稍缓伴有偶发心跳间期缩短，请咨询医生";
			break;
		case 10:
			result = "波形疑似心跳稍缓伴有心跳间期不规则，请咨询医生";
			break;
		case 11:
			result = "波形有漂移，请重新测量";
			break;
		case 12:
			result = "波形疑似心跳过快伴有波形漂移，请咨询医生";
			break;
		case 13:
			result = "波形疑似心跳过缓伴有波形漂移，请咨询医生";
			break;
		case 14:
			result = "波形疑似偶发心跳间期缩短伴有波形漂移，请咨询医生";
			break;
		case 15:
			result = "波形疑似心跳间期不规则伴有波形漂移，请咨询医生";
			break;
		case 16:
			result = "信号较差，请重新测量";
			break;
		case 17:
			result = "与设备断开连接";
			break;
		case 18:
			result="传输中断，请继续连接测量！";
			break;
		case 19:
			result="传输过程出错，可以尝试重传！";
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
     * 获取日期 yyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMD(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
    
    /**
     * 获取日期 yyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMDHMS(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        return formatter.format(date);
    }
    
    /**
     * 获取日期 yyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMDHMS1(long time) {
    	Date date=new Date(time);
        return getYMDHMS(date);
    }
    
    /**
     * 获取时间 HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHMS(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }
    
    /**
     * 获取时间 HH:mm
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }
    
    /**
     * 获取 yyyy-MM
     * @param date
     * @return
     * String
     */
    public static String getYM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(date);
    }
    
    /**
     * 将字符串时间戳转换成 yyyy-MM 格式日期
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYM(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat mmFormat = new SimpleDateFormat("yyyy-MM");

        try {
            Date date = format.parse(dateStr);
            String mmStr = mmFormat.format(date);
            
            return mmStr;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * 判断下载数据是否为空
     * 
     * */
    public static boolean downLoadisNull(String value){
    	if("".equals(value)||"(null)".equals(value)||null==value||"null".equals(value)){
    		return false;
    	}else{
    		return true;
    	}
    	
    }
 /*   *//**
     * 显示Toast
     * @param text
     *//*
    public static void showToast(Context mContext, String text) {
        try {
            ToastMaster.showToast(mContext, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    /**
     * 
     * 判断当前是否为wifi网络模式
     * 
     * **/
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.getType() == connectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    /**
     * 获取当前月的第一天或最后一天
     * 
     * **/
    @SuppressLint("SimpleDateFormat")
	public static String getDayNumber(String day){
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
    	if("first".equals(day)){
    	 Calendar c = Calendar.getInstance();    
         c.add(Calendar.MONTH, 0);
         c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
         String firstday = format.format(c.getTime());
        // System.out.println("===============first:"+first);
          return firstday;
    	}else {
    		//获取当前月最后一天
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));  
            String lastday = format.format(ca.getTime());
            return lastday;
    	}
         
        
    }
    
    /**
     * 获取当前的年月日 yyyy-MM-dd HH:mm:ss
     * void
     * @return yyyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }
    
    /**
     * 获取当前的年月日 yyyy-MM-dd
     * void
     * @return yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentYMD() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }
    
    /**
     * 获取当前的年月日 dd
     * void
     * @return dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }
    
    /**
     * 获取当前的年月日 yyyy-MM
     * void
     * @return yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentYM() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }
 
    /**
     * 获取当前的年月日 yyyy
     * void
     * @return yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentYear() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

	@SuppressLint("SimpleDateFormat")
	public static String getCurrentMonth() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

	/**
     * 获取当前的年月日 yyyy/MM
     * void
     * @return yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentNewYM() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }
 
	/**
	 * 
	 * 获取时分秒
	 * */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentHMS() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}
    /**
     * 将字符串时间戳转换成 dd 格式日期
     */
    @SuppressLint("SimpleDateFormat")
    public static int getDay(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = format.parse(dateStr);
            
            int day = getDay(date);

            return day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 通过 Date 获取 dd 格式日期
     */
    public static int getDay(Date date) {
        try {
            SimpleDateFormat mmFormat = new SimpleDateFormat("dd");
            String mmStr = mmFormat.format(date);
            
            int day = Integer.parseInt(mmStr);
            
            return day;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 通过 Date 获取 MM 格式日期
     */
    public static int getMonth(Date date) {
        try {
            SimpleDateFormat mmFormat = new SimpleDateFormat("MM");
            String mmStr = mmFormat.format(date);
            
            int month = Integer.parseInt(mmStr);
            
            return month;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 通过身高步数性别计算距离
     * @return
     * float
     */
    public static float calDistance(float height, int sex, int steps) {
        return ((height / 100.00f) * (sex == 0 ? 0.415f : 0.413f) * steps);
    }
    
    /**
     * 获取 HH:mm:ss 中的 HH
     */
    @SuppressLint("SimpleDateFormat")
    public static int getHour(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat mmFormat = new SimpleDateFormat("HH");

        try {
            Date date = format.parse(dateStr);
            String mmStr = mmFormat.format(date);
            
            int hour = Integer.parseInt(mmStr);
            
            return hour;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    /**
     * 获取 yyyy-MM-dd HH:mm:ss 中的 yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMD(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat mmFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = format.parse(dateStr);
            String ymd = mmFormat.format(date);
            
            return ymd;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * 获取 yyyy-MM-dd HH:mm:ss 中的 yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMDHMS(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date(time);
        String ymdhms = format.format(date);

        return ymdhms;

    }
    
    /**
     * 获取 yyyy-MM-dd HH:mm:ss 中的 yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHMS(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat mmFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            Date date = format.parse(dateStr);
            String hms = mmFormat.format(date);
            
            return hms;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * 获取时间 HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHMS(long milliseconds) {
        Date date = new Date(milliseconds);
        return getHMS(date);
    }
    
    public static String getHM(long milliseconds) {
        Date date = new Date(milliseconds);
        return getHM(date);
    }
    
    /**
     * 获取 HH:mm:ss 中的 mm
     */
    @SuppressLint("SimpleDateFormat")
    public static int getMinute(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat mmFormat = new SimpleDateFormat("mm");

        try {
            Date date = format.parse(dateStr);
            String mmStr = mmFormat.format(date);
            
            int minute = Integer.parseInt(mmStr);
            
            return minute;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    /**
     * 进行除法运算
     * 
     * @param d1
     *            int 除数
     * @param f
     *            int 被除数
     * @param len
     *            小数位数
     * @return float
     */
    public static float div(int d1, float f, int len) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(f);
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    
    /**
     * 进行除法运算(不进行四舍五入，超出位数的直接舍去)
     * 
     * @param d1
     *            int 除数
     * @param f
     *            int 被除数
     * @param len
     *            小数位数
     * @return float
     */
    public static float divDown(float d1, float f, int len) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(f);
        return b1.divide(b2, len, BigDecimal.ROUND_DOWN).floatValue();
    }
    
    /**
     * 进行除法运算
     * 
     * @param d1
     *            int 除数
     * @param f
     *            int 被除数
     * @param len
     *            小数位数
     * @return float
     */
    public static float div(float d1, float f, int len) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(f);
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    
   /* *//**
     * 启动登录页
     * 
     * @param mContext
     * void
     *//*
    public static void startLoginPage(MyApliction myapp, Context mContext) {
        
        SharedPreferences sp = mContext.getSharedPreferences("login_panther", Context.MODE_PRIVATE);
        
        final Editor editor = sp.edit();
        editor.putString("user", "");
        editor.putString("key", "");
        editor.commit();
        
        ActivityTaskManager activityTaskManager = ActivityTaskManager.getInstance();
        if (activityTaskManager != null) {
            activityTaskManager.closeAllActivity();
            myapp.userBean = null;
            MyApliction.isLogin = false;
        }
        
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
    */
    /**
     * 除法运算,当除不尽时，精确到小数点后10位
     * 
     * @param v1
     * @param v2
     *
     * @return 运算结果
     */
    public static float div(float v1, float v2) {

        if ((v2 < 0 ? -1 : (v2 == 0 ? 0 : 1)) == 0)
            return 0.0f;

        BigDecimal bgNum1 = new BigDecimal(Float.toString(v1));
        BigDecimal bgNum2 = new BigDecimal(Float.toString(v2));
        return bgNum1.divide(bgNum2, 10, BigDecimal.ROUND_HALF_UP)
                .floatValue();
    }
    
    /**
     * 得到字符串的宽度
     * @param paint 画笔
     * @param str 字符串
     * @return 宽度
     */
    public static float getTextWidth(Paint paint,String str) {
        if(str.length() == 0) return 0.0f;
        return paint.measureText(str, 0, str.length());
    }
    
    /**
     * 睡眠类型数据转化<br>
     * 将 0h0min 转化为对应的的 min
     * @param value  0h0min格式
     * @return
     * String
     */
    public static int changeSleepTime(String value) {
        
        try {
            if (TextUtils.isEmpty(value)) {
                value = "0h0min";
            }
            
            if (value.contains("h")) {
                String[] sleeps = value.split("h");
                if (value.contains("min")) {
                    // 去掉 min
                    sleeps[1] = sleeps[1].replace("min", "");
                    // 计算具体对应的分钟
                    int d = Integer.parseInt(sleeps[0]) * 60 + Integer.parseInt(sleeps[1]);
                    return d;
                }
            }else {
                if (value.contains("min")) {
                    // 去掉 min
                    value = value.replace("min", "");
                    // 计算具体对应的分钟
                    int d = Integer.parseInt(value);
                    return d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 设置睡眠时间进度
     * 
     * @param proValue 数据库查出来的进度值  int
     * @param maxValue 数据库查出来的目标值 0h0min格式
     *
     * void
     */
    public static int getSleepProgress(String proValue, String maxValue) {
        
        // 进度, 处理为空情况
        final String progressStr = isNullValue(proValue);
        // 获取目标对应的 min
        int target = changeSleepTime(maxValue);
        
        // 进度值
        final int progress = Integer.parseInt(progressStr);
        
        // 计算并设置进度条百分比
        int percent = getPercent(progress, target);
        
        return percent;
    }
    
    /**
     * 设置运动进度
     * 
     * @param proValue 数据库查出来的进度值  int
     * @param maxValue 数据库查出来的目标值 0h0min格式
     *
     * void
     */
    public static int getSportProgress(String proValue, String maxValue) {
        
        // 进度, 处理为空情况
        final String progressStr = isNullValue(proValue);
        // 目标, 处理为空情况
        final String targetStr = isNullValue(maxValue);
        
        // 进度值
        final float progress = Float.parseFloat(progressStr);
        // 目标值
        final float target = Float.parseFloat(targetStr);
        
        // 计算并设置进度条百分比
        int percent = getPercent(progress, target);
        
        return percent;
    }

    /**
     * 设置进度百分比
     * @param progress
     * @param max
     * void
     */
    public static int getPercent(float progress, float max) {

        if (max != 0) {
            float div = div(progress, max, 3);
            // 百分比
            int percent = (int) (div * 100);
            
            return percent;
        }else {
            // 如果没有目标值，就设置进度为 0
            return 0;
        }
    }
    
    /**
     * 获取 yyyy-MM-dd HH:mm:ss 对应的毫秒数
     * @param dateStr
     * @return
     * long
     */
    public static long getMillisecond(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
            Date parseDate = formatter.parse(dateStr);
            long time = parseDate.getTime();
            
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 得到二个日期间的间隔天数
     */
    public static int getTwoDay(Date date1, Date date2) {
        int day = 0;
        day = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
        return day;
    }
    
    /**
     * 得到二个日期间的间隔天数
     */
    public static int getTwoDay(String dateStr1, String dateStr2) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = formatter.parse(dateStr1);
            Date date2 = formatter.parse(dateStr2);
            
            int day = 0;
            day = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
            
            return Math.abs(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 得到几天后的日期
     * @param date 
     * @param day 间隔天数
     * @return
     * String
     */
    public static Date getNextDay(Date date, int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        long milliseconds = date.getTime() + (day * (24 * 60 * 60 * 1000));

        Date nextDate = new Date(milliseconds);

        return nextDate;
    }
    
    /**
     * float相加
     * @return
     * float
     */
    public static float addFloat(float f1, float f2) {
        BigDecimal bigDecimal1 = new BigDecimal(Float.toString(f1));
        BigDecimal bigDecimal2 = new BigDecimal(Float.toString(f2));
        return bigDecimal1.add(bigDecimal2).floatValue();
    }
    
}