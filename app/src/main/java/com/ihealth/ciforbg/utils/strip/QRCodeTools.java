package com.ihealth.ciforbg.utils.strip;

import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jiuan.androidBg.Comm.ZxingMethod;

/**
 * Created by lynn on 15-7-6.
 */
public class QRCodeTools {
    private static final String TAG = "QRCodeTools";

    public static Data_TB_StripBottleInfo workStripCode(String stripCodeOFScan) {
        L.i(TAG, "扫描的二维码的信息: " + stripCodeOFScan);
        Data_TB_StripBottleInfo bottleInfoObject = new Data_TB_StripBottleInfo();
        String bottleInfo = getBottleInfoByErWeiMa(stripCodeOFScan);

        final String[] bottle = changeJsonToString(
                "bottleInfo", bottleInfo);

        try {
            L.i(TAG, "bottle id:" + bottle[0]);
            L.i(TAG, "over time:" + bottle[1]);
            L.i(TAG, "strips number:" + bottle[2]);
        } catch (Exception e) {
            return null;
        }

        int year = Integer.parseInt(bottle[1].split("-")[0]) - 2000;
        int month = Integer.parseInt(bottle[1].split("-")[1]);
        int day = Integer.parseInt(bottle[1].split("-")[2]);
        int stripsNum = Integer.parseInt(bottle[2]);
        L.i(TAG, "扫描 二维码显示 year:" + year + " --- month:" + month + " --- day:" + day);
        if (year == 15 && month == 1 && day == 5) {
            L.i(TAG, "这个是特殊试条");
            stripsNum = 10;
        }

        //二维码上得过期时间
        long overTime_Code = stringDateToLong(bottle[1]) / 1000;
        //开瓶日期+90天
        long overTime_Compute = computeOverTime(System.currentTimeMillis()) / 1000;

        bottleInfoObject.setBottledId(Integer.parseInt(bottle[0]) + "");
        bottleInfoObject.setOpenDate(System.currentTimeMillis() / 1000);
        bottleInfoObject.setOverTime((overTime_Code < overTime_Compute)?overTime_Code:overTime_Compute);
        bottleInfoObject.setTS(System.currentTimeMillis() / 1000);
        bottleInfoObject.setStripCode(stripCodeOFScan);
        bottleInfoObject.setTimeZone(Method.getTimeZone());
        bottleInfoObject.setStripLeft(stripsNum);
        return bottleInfoObject;
    }

    public static String getBottleInfoByErWeiMa(String stripCodeOFScan) {
        byte buffer[];
        buffer = ZxingMethod.hexStringToByte(stripCodeOFScan);
        if (buffer != null && buffer.length == 30) {
            int bottleIDFromErweima = (int) ((buffer[26] & 0xFF) * 256 * 256
                    * 256 + (buffer[27] & 0xFF) * 256 * 256
                    + (buffer[24] & 0xFF) * 256 + (buffer[25] & 0xFF));
            int year = (buffer[24] & 0xFE) >> 1;
            int month = (buffer[24] & 0x01) * 8 + ((buffer[25] & 0xE0) >> 5);
            int day = buffer[25] & 0x1F;
            int barCount;
            if (year == 15 && month == 1 && day == 15) {
                barCount = 10;
            } else {
                barCount = (int) (buffer[22] & 0xFF);
                // 试条数据不超过25
                if (barCount > 25) {
                    barCount = 25;
                }
            }
            String overData = "20" + year + "-"
                    + ((month > 10) ? month : "0" + month) + "-"
                    + ((day > 10) ? day : "0" + day);
            String[] bottleInfo = new String[3];
            bottleInfo[0] = bottleIDFromErweima + "";
            bottleInfo[1] = overData;
            bottleInfo[2] = barCount + "";
            String arrName[] = new String[] { "bottleId", "overDate",
                    "stripNum" };
            String jsonData = changeStringToJson(
                    "bottleInfo", bottleInfo, arrName);
            return jsonData;
        } else {
            return null;
        }
    }

    public byte toByte(char cr) {
        byte b = (byte) "0123456789ABCDEF".indexOf(cr);
        return b;
    }

    public static String changeStringToJson(String objName,String[] arr,String[] arrName){
        String[] arrJson = arr;
        try {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            JSONObject bottleObject = new JSONObject();
            for (int i = 0; i < arrJson.length; i++) {
                bottleObject.put(arrName[i], arr[i]);
            }
            array.put(bottleObject);
            object.put(objName, array);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] changeJsonToString(String objName,String Json){
        String[] game = new String[3];
        try {
            JSONObject jsonObject = new JSONObject(Json);
            if (!jsonObject.isNull(objName)) {
                String aString = jsonObject.getString(objName);
                JSONArray aJsonArray = new JSONArray(aString);
                int length = aJsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject stoneJson = aJsonArray.getJSONObject(i);
                    String bottleId = stoneJson.getString("bottleId");
                    String date = stoneJson.getString("overDate");
                    String value = stoneJson.getString("stripNum");
                    game[0] = bottleId;
                    game[1] = date;
                    game[2] = value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public static long stringDateToLong(String day) {
        long dateLong = 0;
        SimpleDateFormat sdfResouce = new SimpleDateFormat(
                "yyyy-MM-dd");
        sdfResouce.setTimeZone(TimeZone.getDefault());

        Date fromData = new Date();
        try {
            fromData = sdfResouce.parse(day);
            dateLong = fromData.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateLong;
    }

    public static String longToDateString(long time){
        SimpleDateFormat sdfResouce = new SimpleDateFormat(
                "yyyy-MM-dd");
        sdfResouce.setTimeZone(TimeZone.getDefault());
        return sdfResouce.format(new Date(time));
    }

    /**
     * 返回开瓶日期加90天
     * @param openDate 开瓶时间
     * @return
     */
    public static long computeOverTime(long openDate){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(openDate);
        calendar.add(Calendar.DAY_OF_YEAR, 90);
        return calendar.getTimeInMillis();
    }
}
