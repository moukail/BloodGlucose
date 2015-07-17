package com.ihealth.ciforbg.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;

public class SharePreferenceUtil {

    public static final String BG_UNIT = "bg_unit";

    public static final String USER_EMAIL = "e_mail";

    public static final String FILE_NAME = "ci_for_bg";

    public static final String DEVICE_MAC = "device_mac";

    public static final String SINGLEAPP = "SingleApp";

    public static final String CALLBACLURL = "CallBackUrl";

    public static final String URLPATH = "urlPath";

    public static final String URLCLASS = "urlClass";

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context) {
		sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

    public void setBGUnit(int unit){
        editor.putInt(BG_UNIT,unit);
        editor.commit();
    }

    public int getBgUnit(){
        return sp.getInt(BG_UNIT, Data_TB_BGResult.Unit.MMOL_L);
    }

    public void setUserEmail(String email){
        editor.putString(USER_EMAIL,email);
        editor.commit();
    }

    public String getUserEmail(){
        return sp.getString(USER_EMAIL,"");
    }

    public void setSingleApp(boolean arg){
        editor.putBoolean(SINGLEAPP,arg);
        editor.commit();
    }

    public boolean isSingleApp(){
        return sp.getBoolean(SINGLEAPP,false);
    }

    public void setDeviceMac(String mac){
        editor.putString(DEVICE_MAC,mac);
        editor.commit();
    }

    public String getDeviceMac(){
        return sp.getString(DEVICE_MAC,"");
    }

    public void setCallBackUrl(String url){
        editor.putString(CALLBACLURL,url);
        editor.commit();
    }

    public String getCallBackUrl(){
        return sp.getString(CALLBACLURL,"");
    }

    public void setUrlpath(String urlPath){
        editor.putString(URLPATH,urlPath);
        editor.commit();
    }

    public String getUrlPath(){
        return sp.getString(URLPATH,"");
    }

    public void setUrlclass(String urlClass){
        editor.putString(URLCLASS,urlClass);
        editor.commit();
    }

    public String getUrlclass(){
        return sp.getString(URLCLASS,"");
    }
}
