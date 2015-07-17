package com.ihealth.ciforbg.CloudTools;

import java.security.MessageDigest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;

import com.ihealth.ciforbg.MyApplication;

public class NetTools {

	public static final String TAG = "NetTools";
	public static final String key_accessToken = "AccessToken";
	public static final String key_refreshToken_ = "RefreshToken";
	public static final String key_address = "Address";

	protected static final String AUTHOR_FILE = "author.txt";

	public NetTools() {

	}

	/**
	 * @param str
	 * @return MD5加密算法 author_GYL 2013年11月21日 下午1:48:44
	 */
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = md5Bytes[i] & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		// Log.i("MD5",hexValue.toString());
		return hexValue.toString();
	}

	public static String getDeviceID() {

		TelephonyManager telephonyManager = (TelephonyManager) MyApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);

		return (telephonyManager.getDeviceId() == null) ? getAppID() : telephonyManager.getDeviceId();
	}

	public static String getAppID() {
		// String appID = getDeviceID();
		String appID = "appID";
		AppIDFactory appIDFactory = new AppIDFactory(MyApplication.getContext());
		appID = appIDFactory.getAppID();
		return appID;
	}

	public static boolean saveNetAuthor(String accessToken, String refreshToken, String address) {

		SharedPreferences prefs = MyApplication.getContext().getSharedPreferences(AUTHOR_FILE, 0);
		Editor editor = prefs.edit();
		if (!accessToken.equals("")) {
			editor.putString(key_accessToken, accessToken);
		}
		if (!refreshToken.equals("")) {
			editor.putString(key_refreshToken_, refreshToken);
		}
		if (!address.equals("")) {
			editor.putString(key_address, address);
		}
		editor.commit();
		editor.clear();

		return true;
	}

	public static String getAccressToken() {

		SharedPreferences prefs = MyApplication.getContext().getSharedPreferences(AUTHOR_FILE, 0);
		String AccressToken = prefs.getString(key_accessToken, "");

		return AccressToken;
	}

	public static String getRefreshToken() {

		SharedPreferences prefs = MyApplication.getContext().getSharedPreferences(AUTHOR_FILE, 0);
		String RefreshToken = prefs.getString(key_refreshToken_, "");

		return RefreshToken;
	}

	public static String getAddress() {

		SharedPreferences prefs = MyApplication.getContext().getSharedPreferences(AUTHOR_FILE, 0);
		String Address = prefs.getString(key_address, "");

		return Address;
	}

}
