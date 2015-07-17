package com.ihealth.ciforbg.utils;

import static java.text.DateFormat.DEFAULT;
import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;

//extends Activity
@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles", "SimpleDateFormat" })
public class Method {
	// public final static String GuestUserName = "Guest";// guest 用户名
	public static boolean isUpdateDB = false;
	public final static String versionStr = "1.0";// 版本号
	private final static String currentImageFilePath = "/mnt/sdcard/iGlucose";// 存放图像路径
	public static Boolean isFristIntoUserInfo = true;// userinfo 照完像调用oncreate
	// 判断是否是第一次调用oncreate方法
	public static String isImageNote2ImageName = "";


	/**
	 * 检测app是否显示
	 * 2014年11月21日上午10:13:58
	 * @author chen
	 * @param context
	 * @return
	 */
	public static boolean isRunningForeground (Context context){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		try{
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			String currentPackageName = cn.getPackageName();
			if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())){
				return true ;
			}
		}catch(Exception e){
			return false;
		}

		return false ;
	}

	/**
	 * 求两个日期间相差的天数
	 */
	public static int calculateDaysDiff(Date oldDate, Date dateNew) {
		long hourOld = oldDate.getHours();
		long minOld = oldDate.getMinutes();
		long diff = dateNew.getTime() - oldDate.getTime();
		long diffDay = diff / (24 * 60 * 60 * 1000);
		long diffHour = (diff / (60 * 60 * 1000) - diffDay * 24);
		long diffmin = ((diff / (60 * 1000)) - diffDay * 24 * 60 - diffHour * 60);
		if (diffHour * 60 + diffmin + hourOld * 60 + minOld >= 24 * 60) { // 不足一天补一天
			diffDay++;
		}
		return (int) diffDay;

	}
	
	/**
	 * 判断是不是手机号
	 * @author 赵玮
	 * 2014-12-2下午3:09:03
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {

		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();

	}
	

	/**
	 * Email 验证
	 * 
	 * @param email
	 * @return
	 */
	public static Boolean checkEmail(String email) {
		String str = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * password 验证密码长度在6到128位
	 * 
	 * @param password
	 * @return
	 */
	public static Boolean checkPassword(String password) {
		String str = "^([0-9a-zA-Z]){6,128}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(password);
		return m.matches();
	}

	/**
	 * password 验证密码必须有数字大小写字母
	 * 
	 * @param password
	 * @return
	 */
	public static Boolean checkPassword_word(String password) {
		String str = "^(?![0-9a-z]+$)(?![0-9A-Z]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]+$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(password);
		return m.matches();
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


	// /**
	// * 保存人员身高单位
	// *
	// * @param activitycontext
	// * @param HeightUnit_int
	// * 血压单位
	// * @param Data_TB_UserInfo
	// * 人员实例
	// * @return
	// */
	// public Boolean upDataHeightUnit(Context activitycontext, int
	// HeightUnit_int, Data_TB_UserInfo dtu) {
	// Boolean result = false;
	// // 操作数据库不可更新云标识---licheng
	// AppsDeviceParameters.isUpdateCloud = false;
	// DataBaseOperator db = new DataBaseOperator(activitycontext);
	// String conditionStr = DataBaseOperator.USERINFO_IHEALTHCLOUD + " = '" +
	// dtu.getiHealthCloud() + "'";
	// String valueStr = DataBaseOperator.USERINFO_HEIGHTUNIT + " = " +
	// HeightUnit_int;
	// result = db.updateData(DataBaseOperator.TABLE_TB_USERINFO, conditionStr,
	// valueStr);
	// db.close();
	// // 操作数据库不可更新云标识---licheng
	// AppsDeviceParameters.isUpdateCloud = true;
	// if (result = true) {
	//
	// dtu.setHeightUnit(HeightUnit_int);
	// Method.currentUser.setHeightUnit(HeightUnit_int);
	// this.saveCurrentUser(dtu);
	// }
	// return result;
	// }

	/**
	 * 获取系统当前时间
	 */
	public static String getcurrenttime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);

		return str;
	}

	public static Date stringToDate1(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	// /**
	// * 保存人员体重单位
	// *
	// * @param activitycontext
	// * @param HeightUnit_int 血压单位
	// * @param Data_TB_UserInfo 人员实例
	// * @return
	// */
	// public Boolean upDataWeightUnit(Context activitycontext, int
	// WeightUnit_int, Data_TB_UserInfo dtu) {
	// Boolean result = false;
	// // 操作数据库不可更新云标识---licheng
	// AppsDeviceParameters.isUpdateCloud = false;
	// DataBaseOperator db = new DataBaseOperator(activitycontext);
	// String conditionStr = DataBaseOperator.USERINFO_IHEALTHCLOUD + " = '" +
	// dtu.getiHealthCloud() + "'";
	//
	// String valueStr = DataBaseOperator.USERINFO_WEIGHTUNIT + " = " +
	// WeightUnit_int;
	//
	// result = db.updateData(DataBaseOperator.TABLE_TB_USERINFO, conditionStr,
	// valueStr);
	// db.close();
	// // 操作数据库不可更新云标识---licheng
	// AppsDeviceParameters.isUpdateCloud = true;
	// if (result = true) {
	// dtu.setWeightUnit(WeightUnit_int);
	// Method.currentUser.setWeightUnit(WeightUnit_int);
	// this.saveCurrentUser(dtu);
	// }
	// return result;
	// }

	public static float[] getColourRect(int unit, Context context) {
		float[] arr = new float[6];
		SharedPreferences sharedPrefrences = context.getSharedPreferences("glucose_values", Context.MODE_WORLD_READABLE);

		String pre1 = sharedPrefrences.getString("glucose_targets_tv_pre1", 126 + "");
		String pre2 = sharedPrefrences.getString("glucose_targets_tv_pre2", 110 + "");
		String pre3 = sharedPrefrences.getString("glucose_targets_tv_pre3", 76 + "");
		String post1 = sharedPrefrences.getString("glucose_targets_tv_post1", 200 + "");
		String post2 = sharedPrefrences.getString("glucose_targets_tv_post2", 140 + "");
		String post3 = sharedPrefrences.getString("glucose_targets_tv_post3", 79 + "");

		if (unit == 1) {
			float prF1 = Float.parseFloat(pre1);
			float prF2 = Float.parseFloat(pre2);
			float prF3 = Float.parseFloat(pre3);
			float poF1 = Float.parseFloat(post1);
			float poF2 = Float.parseFloat(post2);
			float poF3 = Float.parseFloat(post3);
			pre1 = Method.getBgmgTommlo(prF1) + "";
			pre2 = Method.getBgmgTommlo(prF2) + "";
			pre3 = Method.getBgmgTommlo(prF3) + "";
			post1 = Method.getBgmgTommlo(poF1) + "";
			post2 = Method.getBgmgTommlo(poF2) + "";
			post3 = Method.getBgmgTommlo(poF3) + "";
		}
		arr[0] = Float.parseFloat(pre1);
		arr[1] = Float.parseFloat(pre2);
		arr[2] = Float.parseFloat(pre3);
		arr[3] = Float.parseFloat(post1);
		arr[4] = Float.parseFloat(post2);
		arr[5] = Float.parseFloat(post3);

		return arr;
	}

	/**
	 * 网络下载用户头像的方法
	 * 
	 * @param imageUrl
	 *            用户头像url
	 * @param imageName
	 *            要保存的头像名 = 用户头像保存时的TS
	 * @return
	 */
	public static boolean getNetWorkPicture(String imageUrl, String imageName) {
		boolean isOK = false;
		try {
			// byte[] data = getImage("http://" + imageUrl);
			byte[] data = getImage(imageUrl);
//			Log.i("tutu", data + "");
			if (data != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
				saveMyBitmap(imageName, bitmap);
				isOK = true;
			} else {
				isOK = false;
			}
		} catch (Exception ex) {
			isOK = false;
		}
		return isOK;
	}

	/**
	 * 通用下载的方法
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] getImage(String path) {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream inStream = conn.getInputStream();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return readStream(inStream);
			}
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * 处理下载流的方法
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 保存图像
	 */
	public static void saveMyBitmap(String bitName, Bitmap mBitmap) throws IOException {

		if (isHaveSdcard()) {
			createImageSaveDirectory();
		}
		File f = new File(currentImageFilePath + "/" + bitName + ".png");
//		Log.d("tutu", "saveBitmap:" + currentImageFilePath + "/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		mBitmap.compress(CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * MM/dd/yyyy
	 * 2014年11月5日上午10:38:56
	 * @author chen
	 * @param ts
	 * @return
	 */
	public static String getDateString(long ts){
	    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	    return sdf.format(new Date(ts*1000l));
	}
	
	@SuppressLint("SimpleDateFormat")
    public static boolean isToday(String day){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String today=sdf.format(new Date());
		if(day.equalsIgnoreCase(today)){
			return true;
		}
		return false;
	}
	
	/**
	 * 确认是否有SD卡
	 * 
	 * @return
	 */
	public static boolean isHaveSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 确认是否有本路径
	 * 
	 * @return
	 */
	public static void createImageSaveDirectory() {

		File path = new File(currentImageFilePath);
		if (!path.exists())
			path.mkdirs();
	}

	/**
	 * 取时间戳
	 */
	public static long getTS() {
		long val = System.currentTimeMillis() / 1000;
		return val;
	}

	/**
	 * lb --> kg
	 */
	public static float getWeight_formLbtoKg(float val) {

		double kg = val * 0.4535939;
		BigDecimal b = new BigDecimal(kg);
		Float result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		System.out.println(result);
		return result;
	}

	/**
	 * kg --> lb
	 */
	public static float getWeight_formKgtoLb(float val) {

		double lb = val * 2.2046154;
		BigDecimal b = new BigDecimal(lb);
		Float result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return result;
	}

	/**
	 * kg --> st
	 */
	public static String getWeight_fromKgtoSt(float val) {

		float temp_f = (float) ((val * 2.2046226218488) / 14);
		String temp_s = String.valueOf(temp_f);
		int i = temp_s.indexOf(".");
		String st = temp_s.substring(0, i);
		String temp_bl = "0" + temp_s.substring(i, temp_s.length());
		BigDecimal b = new BigDecimal(Float.parseFloat(temp_bl) * 14);
		float lb_val = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		if ((int) lb_val == 14) {
			return String.valueOf(Integer.parseInt(st) + 1) + ":" + "0.0";
		}
		return st + ":" + String.valueOf(lb_val);
	}

	/**
	 * st --> kg
	 */
	public static float getWeight_fromSttoKg(String val) {
		String yingshi = "";
		String bang = "";

		if (val.split(":").length > 1) {
			yingshi = val.split(":")[0];
			bang = val.split(":")[1];
		} else {
			yingshi = val.split(":")[0];
			bang = "0";
		}
		float result = Float.parseFloat(yingshi) * 6.35029318f;
		result += Float.parseFloat(bang) * 0.4535929f;
		BigDecimal b = new BigDecimal(result);
		result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return result;
	}

	/**
	 * st --> lb
	 */
	public static float getWeight_fromSttoLb(String val) {

		String yingshi = val.split(":")[0];
		String bang = val.split(":")[1];

		float result = Float.parseFloat(yingshi) * 6.35029318f;
		result = result + Float.parseFloat(bang) * 0.4535929f;
		BigDecimal b = new BigDecimal(result * 2.2046f);
		result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return result;
	}

	/**
	 * lb --> st
	 */
	public static String getWeight_fromLbtoSt(float val) {

		String result;
		float resultkg = val * 0.4535929f;
		int IntValue = (int) (resultkg * 2.20462f / 14.0f);
		float FloatValue = (resultkg * 2.20462f - (float) IntValue * 14.0f);
		int myValue = (int) (FloatValue);
		if (myValue == 14) {
			IntValue++;
			FloatValue = 0.0f;
		}
		if (FloatValue >= 10.0f) {
			BigDecimal b = new BigDecimal(FloatValue);
			FloatValue = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			result = IntValue + ":" + FloatValue;
			return result;
		} else {
			BigDecimal b = new BigDecimal(FloatValue);
			FloatValue = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			result = IntValue + ":" + FloatValue;
			return result;
		}
	}

	/**
	 * st,lb,kg --> kg
	 */
	public static String getWeightfromSTorLBtoKG(String val) {
//		Log.v("Measure_Scale_Manualinput.java getWeightfromSTorLBtoKG()", val);
		float result;
		if (val.indexOf("st") > -1) {
			String valstr = val.split(" ")[0];
			float st_temp, lb_temp;
			if (valstr.split(":").length > 1) {
				st_temp = Float.parseFloat(valstr.split(":")[0]);
				lb_temp = Float.parseFloat(valstr.split(":")[1]);
			} else {
				st_temp = Float.parseFloat(valstr.split(":")[0]);
				lb_temp = 0;
			}

			BigDecimal b = new BigDecimal(st_temp * 6.35029318f + lb_temp * 0.4535929f);
			result = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

		} else if (val.indexOf("lb(s)") > -1) {

			String valstr = val.split(" ")[0];
			BigDecimal b = new BigDecimal(Float.parseFloat(valstr) * 0.4535939f);
			result = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

		} else {

			String valstr = val.split(" ")[0];
			BigDecimal b = new BigDecimal(Float.parseFloat(valstr));
			result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			result = Float.parseFloat(valstr);
		}

		return String.valueOf(result);
	}

	/**
	 * cm -->feet
	 */
	public static String getHeightFromCmToFeet(String val) {

//		Log.d("log", "getHeight_formcmtofeet==========================>" + val);
		String result = "0'00";
		// double ft;
		// double temp;
		// double inch;

		try {

			// String vals[] = val.split(" ");
			// float feet = Float.parseFloat(val);
			// ft = feet / 30.48;
			// temp = feet % 30.48;
			// inch = temp / 2.54;
			float cmValue = Float.parseFloat(val);
			int footValue = (int) (cmValue / 2.54 / 12.0);
			int inchValue = (int) (cmValue / 2.54 - footValue * 12.0 + 0.5);
			if (inchValue == 12) {
				footValue++;
				inchValue = 0;
			}
			result = footValue + "'" + inchValue + "''";
			// result = (int) ft + "'" + Math.round(inch);
			// Log.i("cmValue", "cmValue=" + cmValue);
			// Log.i("footValue", "footValue=" + footValue);
			// Log.i("inchValue", "inchValue=" + inchValue);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * feet -->cm
	 */
	public static float getHeightFromFeetToCm(String val) {

//		Log.d("log", "getHeight_formfeettocm===============================>" + val);
		float result = 170.0f;
		try {
			String chiStr = val.split("'")[0];
			String cunStr = val.split("'")[1];
			float chi = Float.parseFloat(chiStr);
			float cun = Float.parseFloat(cunStr);
			result = (cun + chi * 12) * 2.54f;
			BigDecimal b = new BigDecimal(result);
			result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			// Log.i("chi", "chi=" + chi);
			// Log.i("cun", "cun=" + cun);
			// Log.i("result", "result=" + result);
		} catch (Exception ex) {
			return result;
		}
		return result;
	}

	/**
	 * 图像转base64编码
	 */
	public static String PicPathToBase64(String picturename) {
		String stringBase64 = "";
		// 将Bitmap转换成字符串
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(currentImageFilePath + "/" + picturename);
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bStream);
			byte[] bytes = bStream.toByteArray();
			stringBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (Exception ex) {
			return stringBase64;
		}
		return stringBase64;
	}

	/**
	 * 读取图像
	 */
	public static Bitmap readMyBitmap(String fileName) throws IOException {
		Bitmap bp = null;
		if (isHaveSdcard()) {
			createImageSaveDirectory();
			FileInputStream fis = new FileInputStream(currentImageFilePath + "/" + fileName);
			bp = BitmapFactory.decodeStream(fis);
		}
		return bp;
	}

	/**
	 * 根据Unit单位，换算数值
	 * 
	 * @param val
	 * @param heightUnit_int
	 * @return
	 */
	public static String heightConvert(int val, int heightUnit_int) {
		String result = "";
		if (heightUnit_int == 0) {
			result = val + "";
		}
		if (heightUnit_int == 1) {
			int ychi = (int) (val / 2.54f / 12.0f);

			int ycun = (int) (val / 2.54f - ychi * 12.0f + 0.5f);

			if (ycun == 12) {
				ychi++;
				ycun = 0;
			}
			result = ychi + "'" + ycun + "''";
		}
		return result;
	}

	/**
	 * 保留小数位数
	 * 
	 * @param result
	 *            值
	 * @param decimalDigits
	 *            保留小数位数
	 * @return
	 */
	public static float floatFormat(float result, int decimalDigits) {
		BigDecimal b = new BigDecimal(result);
		return b.setScale(decimalDigits, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static void saveMyBitmap_low(String bitName, Bitmap mBitmap) throws IOException {

		if (isHaveSdcard()) {
			createImageSaveDirectory();
		}
		File f = new File(currentImageFilePath + "/" + bitName + ".png");
//		Log.d("log", "saveBitmap:" + currentImageFilePath + "/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		mBitmap.compress(CompressFormat.PNG, 30, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param i
	 * @return
	 * 
	 */
	public static String intToString(int i) {
		String str = "";
		switch (i) {
		case 0:
			str = "Jan";
			break;
		case 1:
			str = "Jan";
			break;
		case 2:
			str = "Feb";
			break;
		case 3:
			str = "Mar";
			break;
		case 4:
			str = "Apr";

			break;
		case 5:
			str = "May";
			break;
		case 6:
			str = "Jun";
			break;

		case 7:
			str = "Jul";
			break;
		case 8:
			str = "Aug";
			break;
		case 9:
			str = "Sep";
			break;
		case 10:
			str = "Oct";
			break;
		case 11:
			str = "Nov";
			break;
		case 12:
			str = "Dec";
			break;
		case 13:
			break;
		}
		return str;
	}

	// /**
	// * 通过时间，设备ID等信息算出数据ID
	// *
	// * @param MacId
	// * @param DatTimd
	// * @param bakd
	// * @return
	// */
	// public static String getDataID(String MacId, String spo2) {
	// String mac = MacId.replaceAll(":", "");
	// String DataGetId = mac + Method.getTS() + spo2 + "00000000";
	// if (null == mac) {
	// DataGetId = Method.getTS() + spo2 + "00000000";
	// }
	//
	// return DataGetId;
	// }

	// /**
	// * 取两位的随机数
	// * @return
	// */
	// public static String getRandomInt32()
	// {
	// String result="00";
	// Random random= new Random(99);
	// int i = random.nextInt();
	// if(i<10)
	// result="0"+i;
	// else
	// result=""+i;
	// return result;
	// }

	/**
	 * 取两位的随机数
	 * 
	 * @return
	 */
	public static String getRandomInt32() {
		String result = "00";
		Random random = new Random(99);
		int i = random.nextInt();
		if (i < 10)
			result = "0" + i;
		else
			result = "" + i;
		return result;
	}

	/**
	 * 字符串转Date
	 * 
	 * @param strDate
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date stringToDate(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	/**
	 * 获取时和分
	 * 返回的时间为24小时制
	 * 2014年11月21日上午9:24:27
	 * @author chen
	 * @return
	 */
	public static String timeToString(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(time*1000));
	}

	/**
	 * 格式化字符串，补...
	 * 
	 * @param val
	 *            要处理的字符串
	 * @param length
	 *            保留长度
	 * @return
	 */
	public static String stringDelteFormat(String val, int length) {
		if (val.length() > length) {
			val = val.substring(0, length) + "...";
		}
		return val;
	}

	/**
	 * 将DB存储的（yyyy-mm-dd）格式的字符串改为云格式的（mm-dd-yyyy）
	 * 
	 * @return
	 */
	public static String dateStringDBtoCloud(String DBDateString) {
//		Log.d("LL", " =" + DBDateString + "= ");
		String[] tempA = DBDateString.split("-");
		String reString = "";
		try {
			if (tempA.length == 3) {
				reString = tempA[1] + "-" + tempA[2] + "-" + tempA[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reString;
	}

	/**
	 * 将云格式的（mm-dd-yyyy）字符串改为DB存储的（yyyy-mm-dd）格式
	 * 
	 * @return
	 */
	public static String dateStringCloudtoDB(String CloudDateString) {
		String[] tempA = CloudDateString.split("-");
		return tempA[2] + "-" + tempA[0] + "-" + tempA[1];
	}

	/**
	 * yyyy-m(mm)-d(dd) --->yyyy-mm-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(String date) {
		String result = "";
		String datelist[] = date.split("-");
		if (datelist[1].length() == 1) {
			datelist[1] = "0" + datelist[1];
		}
		if (datelist[2].length() == 1) {
			datelist[2] = "0" + datelist[2];
		}
		result = datelist[0] + "-" + datelist[1] + "-" + datelist[2];
		return result;
	}

	/**
	 * hh:m(mm):s(ss)--->hh:mm:ss
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(String time) {
		String result = "";
		String timelist[] = time.split(":");
		for (int i = 0; i < timelist.length; i++) {

			if (timelist[i].length() == 1) {
				timelist[i] = "0" + timelist[i];
			}
			if (i != timelist.length - 1) {
				result = result + timelist[i] + ":";
			} else {
				result = result + timelist[i];
			}
		}
		return result;
	}

	/**
	 * @return 判断是否有sd卡
	 * 
	 */
	public static boolean isSdcardExit() {
		boolean sdCardExist = false;
		sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}

	/**
	 * 获取sd卡根目录
	 */
	public static String getSDPath() {
		File sdDir = null;
		sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		return sdDir.toString();
	}



	/**
	 * @return 获取当前系统语言 （1.en, 2.zh） author_GYL 2013-7-12 下午5:08:08
	 */
	public static int getLanguage(String language) {
		int languageInt = 0;

		if (language.equals("en")) {
			languageInt = 1;
		} else if (language.equals("zh")) {
			languageInt = 2;
		}

		return languageInt;
	}

	/**
	 * @return 获取手机可使用最大内存 author_GYL 2013-8-12 上午9:22:54
	 */
	public int getMaxMemory() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//		Log.d("asd", "Max memory is " + maxMemory + "KB");
		return maxMemory;
	}

	/**
	 * @return 身高cm从float转int
	 */
	public static int getHeightfloatToint(float height) {
		BigDecimal b = new BigDecimal(height);
		float f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
		int height_int = (int) f1;
		return height_int;
	}

	/**
	 * 通过时间，设备ID等信息算出数据ID
	 * @return
	 */
	public static String getBgDataId(String macId, long Ts, float bgValue) {
		String result = "";

		result = macId + Ts + get2bgValye(bgValue) + "00000000";
		Log.e("test", "phonedataid=" + result);

		return result;

	}

	public static String get2bgValye(float bgValue) {
		String result = "00";

		BigDecimal bg = new BigDecimal(bgValue);
		int value = bg.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();

		if (value < 10) {
			result = "0" + value;
		} else {
			String aaa = value + "";
			if (aaa.length() == 2) {
				result = aaa;
			} else {
				result = aaa.substring(aaa.length() - 2, aaa.length());
			}
		}

		return result;
	}

	// public synchronized void LogA() {
	// try {
	// Thread.sleep(2000);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// Log.e("aa", "LogA");
	// }
	//
	// public synchronized void LogB() {
	// try {
	// Thread.sleep(1000);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// Log.e("aa", "LogB");
	// }
	//
	// public synchronized void LogC() {
	// synchronized (this) {
	// Log.e("aa", "LogC");
	// }
	// }

	/**
	 * @return 血糖单位转换（mg/dL转mmol/L）
	 * 
	 */
	public static float getBgmgTommlo(float bgvalue) {
		double bg = bgvalue / 18.0;
		BigDecimal b = new BigDecimal(bg);
		float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}

	/**
	 * @param value
	 * @return 返回保留一位小数 author_GYL 2014年3月14日 下午2:12:27
	 */
	public static float getOnePoint(float value) {
		BigDecimal b = new BigDecimal(value);
		float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}

	/**
	 * @return 血糖单位转换（mmol/dL转mg/L）
	 * 
	 */
	public static float getBgmmloTomg(float bgvalue) {
		double bg = bgvalue * 18.0;
		BigDecimal b = new BigDecimal(bg);
		float f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
		int bg_int = (int) f1;
		return bg_int;
	}

	/**
	 * @return 四舍五入
	 * 
	 */
	public static float get4_5(float value) {
		BigDecimal b = new BigDecimal(value);
		float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		// int bg_int = (int) f1;
		return f1;
	}

	/**
	 * @return 血糖范围四舍五入
	 * 
	 */
	public static int target_get4_5(float value) {
		BigDecimal b = new BigDecimal(value);
		float f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
		int bg_int = (int) f1;
		return bg_int;
	}

	/**
	 * @param time
	 * @return 根据小时，分钟，算出秒数 author_GYL 2013-9-15 下午1:08:47
	 */
	public static int takeTime2Second(String time) {
		int second = 0;
		// String sample = "2013-09-15 10:10:10";
		int hourS = 0;
		int minuteS = 0;
		try {
			time.substring(0, 16);
			hourS = Integer.parseInt(time.substring(11, 13));
			minuteS = Integer.parseInt(time.substring(14, 16));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		second = hourS * 3600 + minuteS * 60;
		return second;
	}

	/**
	 * @return 按时区返回时间字符串 author_GYL 2013-11-5 上午11:51:24
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getUnitTimeStr(String str, int id) {

		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// sdfResouce.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		sdfResouce.setTimeZone(TimeZone.getDefault());

		SimpleDateFormat sdfCurrent;

		switch (id) {

		case 0:
			sdfCurrent = new SimpleDateFormat("MM/dd/yyyy");
			// "MM/dd/yyyy" -> "04/06/2013 "
			break;
		case 1:
			sdfCurrent = new SimpleDateFormat("MM/dd/yyyy h:mmaa");
			// "MM/dd/yyyy h:mmaa" -> "04/06/2013 3:23am"
			break;
		case 2:
			sdfCurrent = new SimpleDateFormat("MMM dd, yyyy h:mmaa");
			// "MMM dd, yyyy h:mmaa" -> "Apr 6, 1970 3:23am"
			break;
		case 3:
			sdfCurrent = new SimpleDateFormat("MMMM dd, yyyy h:mmaa");
			// "MMMM dd, yyyy h:mmaa" -> "April 6, 1970 3:23am"
			break;
		case 4:
			sdfCurrent = new SimpleDateFormat("E, MMMM dd, yyyy h:mmaa");
			// "E, MMMM dd, yyyy h:mmaa" -> "Mon, April 6, 19703:23am
			break;

		case 5:
			sdfCurrent = new SimpleDateFormat("EEEE, MMMM dd, yyyy h:mmaa");
			// "EEEE, MMMM dd, yyyy h:mmaa" -> "Monday, April 6, 1970 3:23am"
			break;

		case 6:
			sdfCurrent = new SimpleDateFormat("E, MMMM dd, yyyy h:mmaa");
			// "E, MMMM dd, yyyy h:mmaa" -> "Mon, April 6, 19703:23am
			break;

		default:
			sdfCurrent = new SimpleDateFormat("MMM dd, yyyy h:mmaa");
			break;
		}
		// 设置 DateFormat的时间区域为GMT
		sdfCurrent.setTimeZone(TimeZone.getDefault());

		// 把字符串转化为Date对象，然后格式化输出这个Date
		Date fromDate = new Date();
		// 时间string解析成GMT时间
		try {
			fromDate = sdfResouce.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// GMT时间转成当前时区的时间
		String to = sdfCurrent.format(fromDate);

		return to;
	}
	
	

	/**
	 * @param context
	 * @param timeStr
	 * @param id
	 *            （1，日期， 2，时间， 3日期+时间）
	 * @return author_GYL 2013年11月13日 上午10:08:52
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDefaultTimerStr(Context context, String timeStr, int id) {
		String outPut = "";

		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// sdfResouce.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		sdfResouce.setTimeZone(TimeZone.getDefault());

		java.text.DateFormat df = null;
		switch (id) {
		case 1:
			df = getDateInstance(DEFAULT, Locale.getDefault());
			break;
		case 2:
			df = getTimeInstance(SHORT, Locale.getDefault());
			break;
		case 3:
			df = getDateTimeInstance(DEFAULT, SHORT, Locale.getDefault());
			break;

		default:
			break;
		}

		SimpleDateFormat sdf = (SimpleDateFormat) df;
		// SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mmaa",
		// Locale.getDefault());

		Date fromData = new Date();
		try {
			fromData = sdfResouce.parse(timeStr);

			outPut = sdf.format(fromData);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return outPut;
	}

	public static String getDefaultTimerStr(Context context, Date date, int id) {
		String outPut = "";

		java.text.DateFormat df = null;
		switch (id) {
		case 1:
			df = getDateInstance(DEFAULT, Locale.getDefault());
			break;
		case 2:
			df = getTimeInstance(SHORT, Locale.getDefault());
			break;
		case 3:
			df = getDateTimeInstance(DEFAULT, SHORT, Locale.getDefault());
			break;

		default:
			break;
		}

		SimpleDateFormat sdf = (SimpleDateFormat) df;

		try {
			outPut = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outPut;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDefaultTimerStr(Context context, Date date, String ID) {
		String outPut = "";

		SimpleDateFormat sdf = new SimpleDateFormat();
		switch (Integer.parseInt(ID)) {
		case 1:
			sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
			sdf.setTimeZone(TimeZone.getDefault());
			break;

		case 2:
			sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
			sdf.setTimeZone(TimeZone.getDefault());
			break;

		case 3:
			sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			sdf.setTimeZone(TimeZone.getDefault());
			break;

		default:
			break;
		}

		try {
			outPut = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outPut;
	}

	public static Date StringToData(String dateStr) {

		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfResouce.setTimeZone(TimeZone.getDefault());

		Date fromData = new Date();
		try {
			fromData = sdfResouce.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fromData;
	}

	public static Date DateToUtcDate() {

		long TS = System.currentTimeMillis();
		long offent = TimeZone.getDefault().getRawOffset();

		Date asd = new Date(TS - offent);

		return asd;
	}

	/**
	 * 生日格式转换成可以变成long的格式 2010-3-3转为毫秒数
	 */
	public static long BirthdayToLong(String Birthday) {
		long BirthLong = 0;

		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfResouce.setTimeZone(TimeZone.getDefault());

		Date fromData = new Date();
		try {
			fromData = sdfResouce.parse(Birthday + " 00:00:00");
			BirthLong = fromData.getTime() / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return BirthLong;
	}

	/**
	 * Long生日格式转换成普通格式 long-------2010-3-3
	 */
	public static String LongToBirthday(long Birthday) {
		String birthStr = "1987-01-01";

		Date ori = new Date(Birthday * 1000);
		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd");
		sdfResouce.setTimeZone(TimeZone.getDefault());

		try {
			birthStr = sdfResouce.format(ori);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return birthStr;
	}

	/**
	 * 测量日期转换成可以变成long的格式 2010-3-3 11:11:00转为毫秒数
	 */
	public static long DateToLong(String Date) {
		long DateLong = 0;

		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfResouce.setTimeZone(TimeZone.getDefault());

		Date fromData = new Date();
		try {
			fromData = sdfResouce.parse(Date);
			DateLong = fromData.getTime() / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return DateLong;
	}

	/**
	 * 测量日期转换成可以变成long的格式 2010-3-3转为毫秒数
	 */
	public static String LongToDateSlash(long time) {
		SimpleDateFormat sdfResouce = new SimpleDateFormat("MM/dd/yyyy");
		sdfResouce.setTimeZone(TimeZone.getDefault());
		Date fromData = new Date(time*1000);
		return sdfResouce.format(fromData);
	}
	/**
	 * 测量日期转换成可以变成long的格式 2010-3-3 11:11:00转为毫秒数
	 */
	public static String LongToTime(long time) {
		SimpleDateFormat sdfResouce = new SimpleDateFormat("hh:mm aa");
		sdfResouce.setTimeZone(TimeZone.getDefault());
		Date fromData = new Date(time*1000);
		return sdfResouce.format(fromData);
	}
	
	/**
	 * 测量日期转换Long转正常日期 毫秒数转为2010-3-3 11:11:00
	 */
	public static String LongToDate(Long Date) {
		String birthStr = "1987-01-01 00:00:00";

		Date ori = new Date(Date * 1000);
		SimpleDateFormat sdfResouce = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfResouce.setTimeZone(TimeZone.getDefault());

		try {
			birthStr = sdfResouce.format(ori);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return birthStr;
	}

	/**
	 * @param srcX
	 * @param srcY
	 * @param dstX
	 * @param dstY
	 * @param rect
	 * @return 判断指定点，是否在指定区域 author_GYL 2014年3月5日 下午2:15:48
	 */
	public static boolean isInnerPoint(float srcX, float srcY, float dstX, float dstY, Rect rect) {
		boolean isIn = false;

		if (srcX > rect.left && srcY > rect.top && rect.right > srcX && rect.bottom > srcY) {
			isIn = true;
		} else {
			return false;
		}

		if (Math.abs(dstX - srcX) < 10 && Math.abs(dstY - srcY) < 10) {
			isIn = true;
		} else {
			return false;
		}

		return isIn;
	}

	/**
	 * @param srcX
	 * @param srcY
	 * @param rect
	 * @return 判断指定点，是否在指定区域 author_GYL 2014年3月5日 下午2:16:48
	 */
	public static boolean isInnerPoint(float srcX, float srcY, Rect rect) {
		boolean isIn = false;

		if (srcX > rect.left && srcY > rect.top && rect.right > srcY && rect.bottom > srcY) {
			isIn = true;
		} else {
			return false;
		}
		return isIn;
	}

	/**
	 * 读取用户DeviceID
	 * 
	 * @param context
	 * @param key
	 *            用户ID
	 * @return author_GYL 2014年3月11日 下午3:16:37
	 */
	public static String[] getSharedPreference(Context context, String key) {
		String regularEx = "#";
		String[] str = null;
		SharedPreferences sp = context.getSharedPreferences("UserMechineDiveceID", Context.MODE_WORLD_WRITEABLE);
		String values;
		values = sp.getString(key, "");
		str = values.split(regularEx);
		return str;
	}

	/**
	 * 保存用户DeviceID
	 * 
	 * @param context
	 * @param key
	 *            用户ID
	 * @param values
	 *            MechineDeviceID author_GYL 2014年3月11日 下午3:16:39
	 */
	public static void setSharedPreference(Context context, String key, String[] values) {
		String regularEx = "#";
		SharedPreferences sp = context.getSharedPreferences("UserMechineDiveceID", Context.MODE_WORLD_WRITEABLE);
		String str = sp.getString(key, "");
		String[] srcValue = str.split("#");
		if (values != null && values.length > 0) {
			for (String value : values) {
				boolean isRepeat = false;
				for (int i = 0; i < srcValue.length; i++) {
					if (srcValue[i].equals(value)) {
						isRepeat = true;
					}
				}
				if (!isRepeat) {
					str += value;
					str += regularEx;
				}
			}
			Editor et = sp.edit();
			et.putString(key, str);
			et.commit();
		}
	}

	/**
	 * @param context
	 * @return 判断程序 是否在后台 author_GYL 2014年6月13日 上午10:01:00
	 */
	public static boolean isRunInBackground(Context context) {

		String packageName = "jiuan.androidBg.YiTang.start";
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(100);
		if (tasksInfo.size() > 0) {
			System.out.println("---------------包名-----------" + tasksInfo.get(0).topActivity.getPackageName());
			for (int i = 0; i < tasksInfo.size(); i++) {
				// Log.e("aa", tasksInfo.get(i).topActivity.getPackageName());
			}
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	/**
	 * @return 返回当前时区
	 */
	public static float getTimeZone1() {
		float timeZone = 0;

		float timeZoneDstSaving = (float) TimeZone.getDefault().getDSTSavings() / (float) 3600000;
		// Log.v("UpLoadBg", " timeZoneDstSaving = " + timeZoneDstSaving);

		boolean isDst = TimeZone.getDefault().inDaylightTime(new Date());
		// Log.v("UpLoadBg", " aa = " + isDst);

		float timeZoneUTC = (float) TimeZone.getDefault().getRawOffset() / (float) 3600000;
		// Log.v("UpLoadBg", " timeZoneUTC = " + timeZoneUTC);

		timeZone = timeZoneUTC + ((isDst) ? timeZoneDstSaving : 0);
		// Log.v("UpLoadBg", " timeZone = " + timeZone);

		return timeZone;
	}
	/**
	 * @return 返回当前时区
	 */
	public static float getTimeZone() {
		float timeZone = 0;

		float timeZoneDstSaving = (float) TimeZone.getDefault().getDSTSavings() / (float) 3600000;
		// Log.v("UpLoadBg", " timeZoneDstSaving = " + timeZoneDstSaving);

		boolean isDst = TimeZone.getDefault().inDaylightTime(new Date());
		// Log.v("UpLoadBg", " aa = " + isDst);

		float timeZoneUTC = (float) TimeZone.getDefault().getRawOffset() / (float) 3600000;
		// Log.v("UpLoadBg", " timeZoneUTC = " + timeZoneUTC);

		timeZone = timeZoneUTC + ((isDst) ? timeZoneDstSaving : 0);
		// Log.v("UpLoadBg", " timeZone = " + timeZone);

		return timeZone;
	}

	/**
	 * 根据时区返回时间
	 * 
	 * @return yyyy-MM-dd wz 2013-4-27 下午3:03:05
	 */
	public static String getZonesDatestr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getDefault());
		String s = sdf.format(new Date());
		// DateFormat d1 = DateFormat.getDateInstance(); //
		// 默认语言（汉语）下的默认风格（MEDIUM风格，比如：2008-6-16
		// // 20:54:53
		// String str1 = d1.format(date);
		// str1 = s.substring(0, s.indexOf(' ')); // 取 yyyy-MM-dd
		return s;
	}

	/**
	 * 音频文件转base64编码
	 * @author 赵玮
	 * @param path 文件路径
	 * @return
	 * @throws java.io.IOException
	 */
	public static String RecordToBase64(String path) throws IOException {
		String strBase64 = null;

		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[inputFile.available()];
		int length = inputFile.read(buffer);
		strBase64 = Base64.encodeToString(buffer, 0, length, Base64.DEFAULT);
		inputFile.close();
		return strBase64;

	}


	/**
	 * 1:一周前;2:2周前;3:一个月前;4:3个月前;
	 * 2014年11月25日下午3:16:04
	 * @author chen
	 * @param today
	 * @param type
	 * @return
	 * @throws java.text.ParseException
	 */
	public static long getLastPeriodTime(long today,int type,boolean isLandTrendActivity) throws ParseException{
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
//		int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
		int month=calendar.get(Calendar.MONTH)+1;
		int monthToReduce=month-2;
		if(month==1){
			monthToReduce=11;
		}else if(month==2){
			monthToReduce=12;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getDefault());
		String date=sdf.format(new Date(today*1000));
//		String str=date.split("-")[0]+"-"+monthToReduce+"-01 00:00:00";
		long time=0;
		if(type>4||type<1){
			type=1;
		}
		switch(type){
		case 1:
			if(isLandTrendActivity){
				time=sdf.parse(date+" 00:00:00").getTime()/1000-7*24*3600;
			}else{
				time=sdf.parse(date+" 00:00:00").getTime()/1000-6*24*3600;
			}
			break;
		case 2:
			time=sdf.parse(date+" 00:00:00").getTime()/1000-14*24*3600;//多一天，是因为要画出8条线
			break;
		case 3:
			time= sdf.parse(date+" 00:00:00").getTime()/1000-28*24*3600;//(dayOfMonth-1)
			break;
		case 4:
			if(isLandTrendActivity){
				time=sdf.parse(date+" 00:00:00").getTime()/1000-84*24*3600;
			}else{
				time=sdf.parse(date+" 00:00:00").getTime()/1000-90*24*3600;
			}
			break;
		}
		return time;
	}

	/**
	 * 图像转base64编码
	 */
	public static String PhotoPathToBase64(String picturepath) {
		String stringBase64 = "";
		// 将Bitmap转换成字符串
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(picturepath);
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bStream);
			byte[] bytes = bStream.toByteArray();
			stringBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (Exception ex) {
			return stringBase64;
		}
		return stringBase64;
	}

	public static String getException(Exception e) {
		String result = "";

		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		e.printStackTrace(printWriter);
		result = info.toString();
		printWriter.close();

		return result;
	}
	
	
	/**
	 * YYYY-MM-dd转换成MM/dd/YYYY时间格式
	 * @author 赵玮
	 * 2014-12-10下午4:36:12
	 * @param str
	 * @return
	 */
	public static String getDataMMddYY(String str){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		String newD = "";
		try {
			date = format.parse(str);
			format = new SimpleDateFormat("MM/dd/yy");
			newD = format.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//有异常要捕获
		
		
		return newD;
		
	}

	/**
     * 判断是否有网络连接
     * 
     * @param context
     * @return zhanglei 2015年3月23日 上午10:04:21
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获得本机MAC
     * @param context
     * @return mac地址
     */
	public static String getLocalMacAddres(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String MacId = info.getMacAddress();
        String mac = MacId.replaceAll(":", "");
        return mac;
    }

    /**
     *
     * @param millisecond
     * @return Jul 8,2015
     */
    public static String getDateStr(long millisecond){
        String outPut = "";
        java.text.DateFormat df = getDateInstance(DEFAULT, Locale.getDefault());

        SimpleDateFormat sdf = (SimpleDateFormat) df;

        try {
            outPut = sdf.format(new Date(millisecond));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outPut;
    }

    /**
     *
     * @param millisecond
     * @return 8:20 AM
     */
    public static String getTimeStr(long millisecond){
        String outPut = "";
        java.text.DateFormat df = getTimeInstance(SHORT, Locale.getDefault());;

        SimpleDateFormat sdf = (SimpleDateFormat) df;

        try {
            outPut = sdf.format(new Date(millisecond));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outPut;
    }

    public static ArrayList<Data_TB_Medicine> creatMedicineList(){

        ArrayList<Data_TB_Medicine> datas = new ArrayList<Data_TB_Medicine>();

        Data_TB_Medicine data1 = new Data_TB_Medicine("", "Acarbose", "Precose", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data2 = new Data_TB_Medicine("", "Actos", "Pioglitazone", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data3 = new Data_TB_Medicine("", "Amaryl", "Glimepiride", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data4 = new Data_TB_Medicine("", "Glucophage", "Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data5 = new Data_TB_Medicine("", "Riomet", "Metformin liguid", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data6 = new Data_TB_Medicine("", "Glucophage XR", "Metformin extended release", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data7 = new Data_TB_Medicine("", "Fortamet", "Metformin extended release", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data8 = new Data_TB_Medicine("", "Glumetza", "Metformin extended release", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data9 = new Data_TB_Medicine("", "Diabeta", "Glyburide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data10 = new Data_TB_Medicine("", "Micronase", "Glyburide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data11 = new Data_TB_Medicine("", "Glucotrol", "Glipizide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data12 = new Data_TB_Medicine("", "Glucotrol XL", "Glipizide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data13 = new Data_TB_Medicine("", "Glynase", "Micronized glyburide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data14 = new Data_TB_Medicine("", "Prandin", "Repaglinide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data15 = new Data_TB_Medicine("", "Starlix", "Nateglinide", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data16 = new Data_TB_Medicine("", "Januvia", "Sitagliptin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data17 = new Data_TB_Medicine("", "Onglyza", "Saxagliptin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data18 = new Data_TB_Medicine("", "Tradjenta", "Linagliptin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data19 = new Data_TB_Medicine("", "Glyset", "Miglitol", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data20 = new Data_TB_Medicine("", "Welchol", "Colesevelam", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data21 = new Data_TB_Medicine("", "Actoplus Met", "Pioglitazone & Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data22 = new Data_TB_Medicine("", "Glucovance", "Glyburide & Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data23 = new Data_TB_Medicine("", "Metaglip", "Glipizide & Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data24 = new Data_TB_Medicine("", "Janumet", "Sitagliptin & Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data25 = new Data_TB_Medicine("", "Kombiglyze", "Saxagliptin & Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data26 = new Data_TB_Medicine("", "Prandimet", "Repaglinide & Metformin", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data27 = new Data_TB_Medicine("", "Duetact", "Pioglitazone & Glimepiride", 1, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data28 = new Data_TB_Medicine("", "Apidra", "", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data29 = new Data_TB_Medicine("", "Humalog", "", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data30 = new Data_TB_Medicine("", "Insulin", "Regular", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data31 = new Data_TB_Medicine("", "Insulin NPH", "", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data32 = new Data_TB_Medicine("", "Injectables", "", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data33 = new Data_TB_Medicine("", "Lantus", "", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data34 = new Data_TB_Medicine("", "Levemir", "", 2, 0, 0, 1, 0, "",0,"");
        Data_TB_Medicine data35 = new Data_TB_Medicine("", "Novolog", "", 2, 0, 0, 1, 0, "",0,"");
        datas.add(data1);
        datas.add(data2);
        datas.add(data3);
        datas.add(data4);
        datas.add(data5);
        datas.add(data6);
        datas.add(data7);
        datas.add(data8);
        datas.add(data9);
        datas.add(data10);
        datas.add(data11);
        datas.add(data12);
        datas.add(data13);
        datas.add(data14);
        datas.add(data15);
        datas.add(data16);
        datas.add(data17);
        datas.add(data18);
        datas.add(data19);
        datas.add(data20);
        datas.add(data21);
        datas.add(data22);
        datas.add(data23);
        datas.add(data24);
        datas.add(data25);
        datas.add(data26);
        datas.add(data27);
        datas.add(data28);
        datas.add(data29);
        datas.add(data30);
        datas.add(data31);
        datas.add(data32);
        datas.add(data33);
        datas.add(data34);
        datas.add(data35);
        return datas;
    }

    public static boolean isSimilar(String prefix, String src) {
        if(prefix.length() > src.length()) {
            return false;
        }
        return src.substring(0,prefix.length()).equalsIgnoreCase(prefix);
    }
}