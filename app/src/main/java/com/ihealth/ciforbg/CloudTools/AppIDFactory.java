package com.ihealth.ciforbg.CloudTools;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;
import android.util.Log;

public class AppIDFactory {

	protected static final String PREFS_FILE = "device_id.txt";
	protected static final String PREFS_DEVICE_ID = "device_id";
	protected static final String TAG = "getAppIDFactory";

	String AppID;
	Context context;

	public AppIDFactory(Context context) {
		// this.AppID = new String();    
		this.context = context;
		// Log.e(TAG, "调用 getAppIDFactory 构造函数");
	}

	public String getAppID() {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		AppID = prefs.getString(PREFS_DEVICE_ID, "");

		if (AppID.equals("")) {
			AppID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			Log.e(TAG, "AppID SecureRandom before = " + AppID);
			final SecureRandom random = new SecureRandom();
			AppID = new BigInteger(64, random).toString(16);
			Log.e(TAG, "AppID SecureRandom after = " + AppID);

			Editor editor = prefs.edit();
			editor.putString(PREFS_DEVICE_ID, AppID);
			editor.commit();
			editor.clear();
//			if(editor!=null)
//				editor = null;
		} else {
			Log.e(TAG, "SharedPreferences 已经有 AppID 直接取 " + AppID);
		}

		return AppID;
	}
	
	
	public String reSetAppID() {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		AppID = prefs.getString(PREFS_DEVICE_ID, "");
		
		if (!AppID.equals("")) {
			Editor editor = prefs.edit();
			editor.putString(PREFS_DEVICE_ID, "");
			editor.commit();
			editor.clear();
			Log.e(TAG, " 登出用户 SharedPreferences  AppID reSet");
		} else {
			Log.e(TAG, "登出用户 不用RsSet APPID");
		}

		return AppID;
	}

}
