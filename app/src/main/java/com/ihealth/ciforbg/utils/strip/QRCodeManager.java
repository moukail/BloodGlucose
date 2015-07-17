package com.ihealth.ciforbg.utils.strip;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.ihealth.ciforbg.database.Constants_DB;
import com.ihealth.ciforbg.database.DataBaseTools;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.dataclass.Data_TB_UserInfo;

import jiuan.androidBg.CloudTools.Method;

/**
 * Created by lynn on 15-7-6.
 */
public class QRCodeManager {
    private static String QRCODE = "";

    public static String getCurrentQRCode() {
        return QRCODE;
    }

    public static void setCurrentQRCode(String code) {
        QRCODE = code;
    }

    public static boolean storeUserInfoToDB(Context context, Data_TB_UserInfo obj) {
        DataBaseTools tools = new DataBaseTools(context);
        return tools.addData(Constants_DB.TABLE_TB_USERINFO, obj);
    }

    public static Data_TB_UserInfo readUserInfoFromDB(Context context, @NonNull String userName) {
        Data_TB_UserInfo userInfo = null;
        DataBaseTools tools = new DataBaseTools(context);
        Cursor cursor = tools.selectData(Constants_DB.TABLE_TB_USERINFO, null, Constants_DB.USERINFO_USERNAME + " = '" + userName + "'");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            userInfo = new Data_TB_UserInfo();
            userInfo.setBottleId(cursor.getString(cursor.getColumnIndex(Constants_DB.USERINFO_BOTTLEID)));
            userInfo.setUserName(cursor.getString(cursor.getColumnIndex(Constants_DB.USERINFO_USERNAME)));
            cursor.close();
        }
        return userInfo;
    }

    public static boolean storeStipInfoToDB(Context context, Data_TB_StripBottleInfo info, String userName) {
        DataBaseTools tools = new DataBaseTools(context);
        if (readStripInfoFromDB(context, info.getBottledId(), userName) != null) {
            tools.deleteData(Constants_DB.TABLE_TB_BOTTLEINFO, Constants_DB.BOTTLEINFO_USERNAME + " = '" + userName + "'" +
                    " and " +Constants_DB.BOTTLEINFO_BOTTLEID + " = '" + info.getBottledId() + "'");
        }
        return tools.addData(Constants_DB.TABLE_TB_BOTTLEINFO, info);
    }

    public static Data_TB_StripBottleInfo readStripInfoFromDB(Context context, String bottleId, String userName) {
        Data_TB_StripBottleInfo bottleInfo = null;
        DataBaseTools tools = new DataBaseTools(context);
        Cursor cursor = tools.selectData(Constants_DB.TABLE_TB_BOTTLEINFO, null,  Constants_DB.BOTTLEINFO_USERNAME + " = '" + userName + "'" +
                " and " +Constants_DB.BOTTLEINFO_BOTTLEID + " = '" + bottleId + "'");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            bottleInfo = new Data_TB_StripBottleInfo();
            bottleInfo.setOverTime(cursor.getLong(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_OVERTIME)));
            bottleInfo.setOpenDate(cursor.getLong(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_OPENDATE)));
            bottleInfo.setStripCode(cursor.getString(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_STRIPCODE)));
            bottleInfo.setBottledId(cursor.getString(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_BOTTLEID)));
            bottleInfo.setStripLeft(cursor.getInt(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_STRIPLEFT)));
            bottleInfo.setTimeZone(cursor.getFloat(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_TIMEZONE)));
            bottleInfo.setTS(cursor.getLong(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_TS)));
            bottleInfo.setUserName(cursor.getString(cursor.getColumnIndex(Constants_DB.BOTTLEINFO_USERNAME)));
            cursor.close();
        }
        return bottleInfo;
    }

    public static boolean updateStripNumber(Context context, String bottleId, String userName, int leftNum){
        DataBaseTools tools = new DataBaseTools(context);
        return tools.updateData(Constants_DB.TABLE_TB_BOTTLEINFO, Constants_DB.BOTTLEINFO_BOTTLEID + " = '" + bottleId + "'" +
                " and " + Constants_DB.BOTTLEINFO_USERNAME + " = '" + userName + "'", Constants_DB.BOTTLEINFO_STRIPLEFT + " = " + leftNum + "," +
                Constants_DB.BOTTLEINFO_TS + " = " + Method.getTS());
    }

//    public static boolean stripNumberDecreasing(Context context, String bottleId, String userName) {
//        DataBaseTools tools = new DataBaseTools(context);
//        Data_TB_StripBottleInfo bottleInfo = readStripInfoFromDB(context, bottleId, userName);
//        if (bottleInfo == null) {
//            return false;
//        }
//        return tools.updateData(Constants_DB.TABLE_TB_BOTTLEINFO, Constants_DB.BOTTLEINFO_BOTTLEID + " = '" + bottleInfo.getBottledId() + "'" +
//                " and " + Constants_DB.BOTTLEINFO_USERNAME + " = '" + userName + "'", Constants_DB.BOTTLEINFO_STRIPLEFT + " = " + (bottleInfo.getStripLeft() - 1));
//    }
}
