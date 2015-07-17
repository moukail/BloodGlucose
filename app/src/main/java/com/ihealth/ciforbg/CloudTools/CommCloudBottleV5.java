//package com.ihealth.ciforbg.CloudTools;
//
//import java.net.SocketTimeoutException;
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//
//import android.content.Context;
//import android.util.Log;
//
//import com.ihealth.ciforbg.MyApplication;
//import com.ihealth.ciforbg.dataclass.Data_TB_BottleInfo;
//
//public class CommCloudBottleV5 {
//
//	public static final String TAG = "CommCloudBottleV5";
//    private final String SC = "7c789858c0ec4ebf8189ebb14b6730a5";
//	Context context;
//	public String messageReturn = "";
//	// public ArrayList<BgNetData> bgRetrunObjectArr;
//	public int result = 0;
//	public long TS = 0;
//	public float resultMessage = 0;
//	public int queueNum = 0;
//	// public static final String Address =
//	// "http://test.ihealthlabs.com:8008/api/"; // IHServer_Tokyo
//	// public static final String Address =
//	// "https://api.ihealthlabs.com:8443/api4/"; // 正式地址
//
//	public static final String SV_BgBottleSync = "e5267ce66196444b96df9a8b88dbf71a";
//	public static final String SV_BgBottle_leftnumber_sync = "bd7f012e138b4f1dba86a205093045c2";
//	public static final String SV_setBgBottleLeftNumber = "ad5d50cb7e004b9cb38247dd2c521f9d";
//	public static final String SV_bgbottle_leftnumber_get = "5941f19c1126425ab722a26a62adf50d";
//
//	public ArrayList<Data_TB_BottleInfo> data_TB_BottleInfo_Arr;
//
//	public Data_TB_BottleInfo data_TB_BottleInfo_Ob;
//
//	/**
//	 * 构造函数
//	 */
//	public CommCloudBottleV5(Context context) {
//		this.context = context;
//	}
//
//	/**
//	 * @param userName
//	 * @return
//	 * @throws org.apache.http.conn.ConnectTimeoutException
//	 * @throws java.net.SocketTimeoutException
//	 *             author_GYL 2013年11月26日 上午11:25:41
//	 */
//	public boolean BgBottleSync(String userName, String token, /*
//																 * ArrayList<
//																 * BottleInfo>
//																 * biArr
//																 */
//                                Data_TB_BottleInfo BI) throws ConnectTimeoutException, SocketTimeoutException,Exception {
//
//		data_TB_BottleInfo_Ob = new Data_TB_BottleInfo();
//
//		// 编辑发送参数
//		Map<String, String> bg_uploadParams = new HashMap<String, String>();
//		bg_uploadParams.put("sc", SC);
//		bg_uploadParams.put("sv", SV_BgBottleSync);
//		bg_uploadParams.put("AppVersion", MyApplication.App_Version);
//		bg_uploadParams.put("AppGuid", NetTools.getAppID());
//		bg_uploadParams.put("PhoneOS", android.os.Build.VERSION.RELEASE);
//		bg_uploadParams.put("PhoneName", android.os.Build.MODEL);
//		bg_uploadParams.put("PhoneID", NetTools.getDeviceID());
//		bg_uploadParams.put("PhoneLanguage", Locale.getDefault().getLanguage());
//		bg_uploadParams.put("PhoneRegion", Locale.getDefault().getCountry());
//		bg_uploadParams.put("QueueNum", "1");
//		bg_uploadParams.put("Token", "");
//
//		// // 编辑参数UploadData
//		JSONObject jsonBIData = new JSONObject();
//		try {
//			jsonBIData.put("BottleId", BI.getBottledId());
//			jsonBIData.put("StripCode", BI.getStripCode());
//			jsonBIData.put("OpenDate", BI.getOpenDate());
//			jsonBIData.put("TimeZone", BI.getTimeZone());
//			jsonBIData.put("TS", BI.getTS());
//
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//
//		bg_uploadParams.put("Un", userName);
//		bg_uploadParams.put("VerifyToken", token);
//		bg_uploadParams.put("UploadData", jsonBIData.toString());
//
//		String webAddress = MyApplication.Address + "/api5/BgBottleSync.htm";
//		Log.d(TAG, "试条瓶信息同步 = " + bg_uploadParams.toString());
//
//		if(MyApplication.ISFORNNIT) {
//			try {
//				if(MyApplication.isNNITOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, bg_uploadParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, bg_uploadParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		} else {
//			try {
//				if(MyApplication.isOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, bg_uploadParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, bg_uploadParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
//
//		Log.i(TAG, "试条瓶信息同步返回 =" + messageReturn);
//		if (messageReturn.length() == 0)
//			return false;
//
//		// 分析收取的数据
//		try {
//			JSONTokener jsonTParser = new JSONTokener(messageReturn);
//			JSONObject jsonBO = (JSONObject) jsonTParser.nextValue();
//			this.result = jsonBO.getInt("Result");
//			this.TS = Long.parseLong(jsonBO.getString("TS"));
//			this.resultMessage = Float.parseFloat(jsonBO.getString("ResultMessage"));
//			this.queueNum = jsonBO.getInt("QueueNum");
//
//			if (resultMessage == 100.0) {
//
//				JSONObject jsOB = new JSONObject(jsonBO.getString("ReturnValue"));
//
//				data_TB_BottleInfo_Ob.setBottledId(jsOB.getString("BottleId"));
//
//				if(!jsOB.getString("BottleId").equals("0")) {
//					data_TB_BottleInfo_Ob.setStripCode(jsOB.getString("StripCode"));
//					// Log.i("aa", "云上的StripCode:" +
//					// data_TB_BottleInfo_Ob.getStripCode());
//					data_TB_BottleInfo_Ob.setOpenDate(jsOB.getLong("OpenDate"));
//					// Log.i("aa", "云上的OpenDate:" +
//					// data_TB_BottleInfo_Ob.getOpenDate());
//					data_TB_BottleInfo_Ob.setStripLeft(jsOB.getInt("StripLeft"));
//					// Log.i("aa", "云上的StripLeft:" +
//					// data_TB_BottleInfo_Ob.getStripLeft());
//					data_TB_BottleInfo_Ob.setTimeZone(Float.parseFloat(jsOB.getString("TimeZone")));
//					// Log.i("aa", "云上的TimeZone:" +
//					// data_TB_BottleInfo_Ob.getTimeZone());
////					int UserId = jsOB.getInt("UserId");
//					return true;
//				} else {
//					return false;
//				}
//
//
//			} else if (resultMessage == 212 || resultMessage == 221) {
//				CommCloudUserV5 user = new CommCloudUserV5(context);
//				if (resultMessage == 212) {
//					user.token_refresh(userName, MyApplication.RefreshToken);
//				} else if (resultMessage == 221) {
//					user.verify(userName, MyApplication.CurrentPWD, 0);
//				}
//				user = null;
//				return BgBottleSync(userName, MyApplication.AccessToken, BI);
//
//			} else {
//				return false;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * @param userName
//	 * @param passWord
//	 * @param bpArr
//	 * @return
//	 * @throws org.apache.http.conn.ConnectTimeoutException
//	 * @throws java.net.SocketTimeoutException
//	 *             author_GYL 2013年11月26日 上午11:25:41
//	 */
//	public boolean BgBottle_leftnumber_sync(ArrayList<BottleInfo4StripSync> bissArr
//	/* BottleInfo4StripSync bis */) throws ConnectTimeoutException, SocketTimeoutException {
//
//		// 编辑发送参数
//		Map<String, String> bg_downloadParams = new HashMap<String, String>();
//		bg_downloadParams.put("sc", MyApplication.SC);
//		bg_downloadParams.put("sv", SV_BgBottle_leftnumber_sync);
//		bg_downloadParams.put("AppVersion", MyApplication.App_Version);
//		bg_downloadParams.put("AppGuid", NetTools.getAppID());
//		bg_downloadParams.put("PhoneOS", android.os.Build.VERSION.RELEASE);
//		bg_downloadParams.put("PhoneName", android.os.Build.MODEL);
//		bg_downloadParams.put("PhoneID", NetTools.getDeviceID());
//		bg_downloadParams.put("PhoneLanguage", Locale.getDefault().getLanguage());
//		bg_downloadParams.put("PhoneRegion", Locale.getDefault().getCountry());
//		bg_downloadParams.put("QueueNum", "1");
//		bg_downloadParams.put("Token", "");
//
//		JSONArray jsonBISSArr = new JSONArray();
//		// JSONObject jsonBIData = new JSONObject();;
//		try {
//			JSONObject jsonBISData;
//			// 生成BottleInfo JSON
//			for (int i = 0; i < bissArr.size(); i++) {
//				jsonBISData = new JSONObject();
//
//				jsonBISData.put("DataID", bissArr.get(i).getDataID());
//				jsonBISData.put("BottleId", bissArr.get(i).getBottleId());
//				jsonBISData.put("StripCode", bissArr.get(i).getStripCode());
//				jsonBISData.put("OpenDate", bissArr.get(i).getOpenDate());
//				jsonBISData.put("TimeZone", bissArr.get(i).getTimeZone());
//				jsonBISData.put("ReduceLeft", bissArr.get(i).getReduceLeft());
//				jsonBISData.put("StripTS", bissArr.get(i).getStripTS());
//
//				jsonBISSArr.put(i, jsonBISData);
//			}
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//
//		bg_downloadParams.put("UploadData", jsonBISSArr.toString());
//
//		// String webAddress = Address + "BgBottle_leftnumber_sync.ashx";
//		String webAddress = MyApplication.Address + "/api5/BgBottle_leftnumber_sync.htm";
//
//		Log.d(TAG, "血糖试条瓶同步剩余条数 = " + bg_downloadParams.toString());
//
//		if(MyApplication.ISFORNNIT) {
//			try {
//				if(MyApplication.isNNITOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, bg_downloadParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, bg_downloadParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		} else {
//			try {
//				if(MyApplication.isOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, bg_downloadParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, bg_downloadParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		}
//
//		Log.i(TAG, "血糖试条瓶同步剩余条数返回 =" + messageReturn);
//
//		if (messageReturn.length() == 0)
//			return false;
//
//		// 分析收取的数据
//		try {
//			JSONTokener jsonTParser = new JSONTokener(messageReturn);
//			JSONObject jsonBg = (JSONObject) jsonTParser.nextValue();
//			this.result = jsonBg.getInt("Result");
//			this.TS = Long.parseLong(jsonBg.getString("TS"));
//			this.resultMessage = Float.parseFloat(jsonBg.getString("ResultMessage"));
//			this.queueNum = jsonBg.getInt("QueueNum");
//
//			if (resultMessage == 100.0) {
//
//				return true;
//			} else {
//				return false;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public boolean setBgBottleLeftNumber(ArrayList<BottleInfo4StripSet> bissArr
//	/* BottleInfo4StripSet biss */) throws ConnectTimeoutException, SocketTimeoutException {
//
//		// 编辑发送参数
//		Map<String, String> setBgBottleLeftNumberParams = new HashMap<String, String>();
//		setBgBottleLeftNumberParams.put("sc", MyApplication.SC);
//		setBgBottleLeftNumberParams.put("sv", SV_setBgBottleLeftNumber);
//		setBgBottleLeftNumberParams.put("AppVersion", MyApplication.App_Version);
//		setBgBottleLeftNumberParams.put("AppGuid", NetTools.getAppID());
//		setBgBottleLeftNumberParams.put("PhoneOS", android.os.Build.VERSION.RELEASE);
//		setBgBottleLeftNumberParams.put("PhoneName", android.os.Build.MODEL);
//		setBgBottleLeftNumberParams.put("PhoneID", NetTools.getDeviceID());
//		setBgBottleLeftNumberParams.put("PhoneLanguage", Locale.getDefault().getLanguage());
//		setBgBottleLeftNumberParams.put("PhoneRegion", Locale.getDefault().getCountry());
//		setBgBottleLeftNumberParams.put("QueueNum", "1");
//		setBgBottleLeftNumberParams.put("Token", "");
//
//		// bg_downloadParams.put("TS", System.currentTimeMillis()/1000+"");
//		// bg_downloadParams.put("dv", getAppID() + "a");
//		// bg_downloadParams.put("ver", AppsDeviceParameters.App_Version);
//
//		JSONArray jsonBISSArr = new JSONArray();
//		// 编辑参数UploadData
//		try {
//			JSONObject jsonBISSData;
//			for (int i = 0; i < bissArr.size(); i++) {
//				jsonBISSData = new JSONObject();
//				jsonBISSData.put("BottleId", bissArr.get(i).getBottleId());
//				jsonBISSData.put("StripCode", bissArr.get(i).getStripCode());
//				jsonBISSData.put("OpenDate", bissArr.get(i).getOpenDate());
//				jsonBISSData.put("TimeZone", bissArr.get(i).getTimeZone());
//				jsonBISSData.put("StripNumber", bissArr.get(i).getStripNumber());
//				jsonBISSData.put("StripTS", bissArr.get(i).getStripTS());
//				jsonBISSArr.put(i, jsonBISSData);
//			}
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//
//		setBgBottleLeftNumberParams.put("UploadData", jsonBISSArr.toString());
//
//		String webAddress = MyApplication.Address + "/api5/setBgBottleLeftNumber.htm";
//		Log.d(TAG, "设置试条瓶剩余条数 = " + setBgBottleLeftNumberParams.toString());
//
//		if(MyApplication.ISFORNNIT) {
//			try {
//				if(MyApplication.isNNITOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, setBgBottleLeftNumberParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, setBgBottleLeftNumberParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		} else {
//			try {
//				if(MyApplication.isOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, setBgBottleLeftNumberParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, setBgBottleLeftNumberParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
//
//		Log.i(TAG, "设置试条瓶剩余条数返回 =" + messageReturn);
//
//		if (messageReturn.length() == 0)
//			return false;
//
//		// 分析收取的数据
//		try {
//			JSONTokener jsonTParser = new JSONTokener(messageReturn);
//			JSONObject jsonBO = (JSONObject) jsonTParser.nextValue();
//			this.result = jsonBO.getInt("Result");
//			this.TS = Long.parseLong(jsonBO.getString("TS"));
//			this.resultMessage = Float.parseFloat(jsonBO.getString("ResultMessage"));
//			this.queueNum = jsonBO.getInt("QueueNum");
//
//			if (resultMessage == 100.0) {
//				// ID 字段为预留，服务器端暂不发送
//				// JSONTokener jsonTokener = new
//				// JSONTokener(jsonORegist.getString("ReturnValue"));
//				// JSONObject msgValue = (JSONObject) jsonTokener.nextValue();
//				// int ID = msgValue.getInt("ID");
//
//				return true;
//			} else {
//				return false;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public boolean bgbottle_leftnumber_get(String bottleID) throws ConnectTimeoutException, SocketTimeoutException {
//
//		data_TB_BottleInfo_Ob = new Data_TB_BottleInfo();
//
//		// 编辑发送参数
//		Map<String, String> bgbottle_leftnumber_getParams = new HashMap<String, String>();
//		bgbottle_leftnumber_getParams.put("sc", MyApplication.SC);
//		bgbottle_leftnumber_getParams.put("sv", SV_bgbottle_leftnumber_get);
//		bgbottle_leftnumber_getParams.put("AppVersion", MyApplication.App_Version);
//		bgbottle_leftnumber_getParams.put("AppGuid", NetTools.getAppID());
//		bgbottle_leftnumber_getParams.put("PhoneOS", android.os.Build.VERSION.RELEASE);
//		bgbottle_leftnumber_getParams.put("PhoneName", android.os.Build.MODEL);
//		bgbottle_leftnumber_getParams.put("PhoneID", NetTools.getDeviceID());
//		bgbottle_leftnumber_getParams.put("PhoneLanguage", Locale.getDefault().getLanguage());
//		bgbottle_leftnumber_getParams.put("PhoneRegion", Locale.getDefault().getCountry());
//		bgbottle_leftnumber_getParams.put("QueueNum", "1");
//		bgbottle_leftnumber_getParams.put("Token", "");
//
//		bgbottle_leftnumber_getParams.put("BottleId", bottleID);
//
//		// String webAddress = Address + "bgbottle_leftnumber_get.ashx";
//		String webAddress = MyApplication.Address + "/api5/bgbottle_leftnumber_get.htm";
//
//		Log.d(TAG, "血糖试条瓶剩余条数查询 = " + bgbottle_leftnumber_getParams.toString());
//
//		if(MyApplication.ISFORNNIT) {
//			try {
//				if(MyApplication.isNNITOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, bgbottle_leftnumber_getParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, bgbottle_leftnumber_getParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		} else {
//			try {
//				if(MyApplication.isOfficial){
//					httpsPost ht = new httpsPost();
//					messageReturn = ht.requireClass(webAddress, bgbottle_leftnumber_getParams, "UTF-8");
//				}else{
//					httpPost ht = new httpPost();
//					messageReturn = ht.requireClass(webAddress, bgbottle_leftnumber_getParams, "UTF-8");
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		}
//
//		Log.i(TAG, "血糖试条瓶剩余条数查询返回 =" + messageReturn);
//
//		if (messageReturn.length() == 0)
//			return false;
//
//		// 分析收取的数据
//		try {
//			JSONTokener jsonTParser = new JSONTokener(messageReturn);
//			JSONObject jsonBgObRe = (JSONObject) jsonTParser.nextValue();
//			this.result = jsonBgObRe.getInt("Result");
//			this.TS = Long.parseLong(jsonBgObRe.getString("TS"));
//			this.resultMessage = Float.parseFloat(jsonBgObRe.getString("ResultMessage"));
//			this.queueNum = jsonBgObRe.getInt("QueueNum");
//
//			if (resultMessage == 100.0) {
//				JSONTokener jsonTokener = new JSONTokener(jsonBgObRe.getString("ReturnValue"));
//				JSONObject msgValue = (JSONObject) jsonTokener.nextValue();
//
//				data_TB_BottleInfo_Ob.setBottledId(msgValue.getString("BottleId"));
//				if(!msgValue.getString("BottleId").equals("0")) {
//					data_TB_BottleInfo_Ob.setOpenDate(msgValue.getLong("OpenDate"));
//					data_TB_BottleInfo_Ob.setStripLeft(msgValue.getInt("StripLeft"));
//					data_TB_BottleInfo_Ob.setTS(msgValue.getInt("StripTS"));
//					data_TB_BottleInfo_Ob.setTimeZone(Float.parseFloat(msgValue.getString("TimeZone")));
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//}
