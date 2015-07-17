package com.ihealth.ciforbg.utils;

import android.content.Context;
import android.util.Log;

import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.dataclass.CommonEvent;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.dataclass.DeviceEvent;
import com.ihealth.ciforbg.utils.strip.QRCodeManager;
import com.ihealth.ciforbg.utils.strip.QRCodeTools;
import com.ypy.eventbus.EventBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import jiuan.androidBg.Bluetooth.BG5Control;
import jiuan.androidBg.CloudBGData.BottleInfo;
import jiuan.androidBg.CloudBGData.Data_TB_BottleInfo;

/**
 * Created by lynn on 15-7-7.
 */
public class InitializeConnection implements Runnable {
    private final String TAG = "InitializeConnection";
    private BG5Control mBG5Control;
    private Context mContext;
    private DeviceExplorer.OnExplorListener mListener;

    public InitializeConnection(Context context, BG5Control control, DeviceExplorer.OnExplorListener listener) {
        this.mBG5Control = control;
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void run() {
        if (mBG5Control.setParams(1, 1)) {

            int bottleId = mBG5Control.readBottleId();
            Log.i(TAG, "bottleId:" + bottleId);

            try {
                String offline = mBG5Control.readOfflineData();
                Log.i(TAG, "offline:" + offline);
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }

            String code = mBG5Control.getCode();
            Log.i(TAG, "code:" + code);

            final Data_TB_StripBottleInfo cloudBottleInfo;//用户云端bottle信息
            Data_TB_StripBottleInfo localBottleInfo;//用户本地bottle信息
            if (bottleId == 0) { //bottle id ==0 重新扫码
                mBG5Control.deleteOfflineData();
                EventBus.getDefault().post(new CommonEvent(CommonEvent.COMMON_STRIPEMPTY));
                return;
            } else {
                cloudBottleInfo = syncBottleInfoToLocal(MyApplication.UserName);//同步用户bottle 信息
                localBottleInfo = QRCodeManager.readStripInfoFromDB(mContext, bottleId + "", MyApplication.UserName); //从数据库读取试条瓶信息
                if(cloudBottleInfo != null){//用户云端数据不为空
                    L.i(TAG, "Download user bottle info success, bottle id:" + cloudBottleInfo.getBottledId() + ", left number:" + cloudBottleInfo.getStripLeft());
                    if(cloudBottleInfo.getBottledId().equals(bottleId + "")){
                        if(localBottleInfo == null){
                            QRCodeManager.storeStipInfoToDB(mContext, cloudBottleInfo, MyApplication.UserName);
                        } else {
                            if(cloudBottleInfo.getStripLeft() > localBottleInfo.getStripLeft()){
                                cloudBottleInfo.setStripLeft(localBottleInfo.getStripLeft());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        syncBottleInfoToCloud(cloudBottleInfo);
                                    }
                                }).start();
                            } else {
                                QRCodeManager.storeStipInfoToDB(mContext, cloudBottleInfo, MyApplication.UserName);
                            }
                        }
                        localBottleInfo = cloudBottleInfo;
                    }
                }

                if(localBottleInfo == null){
                    L.i(TAG,"this bottle id is not exist in cloud and database");
                    EventBus.getDefault().post(new CommonEvent(CommonEvent.COMMON_STRIPEMPTY));
                    return;
                } else if (localBottleInfo.getStripLeft() == 0) {
                    L.i(TAG,"this bottle id is left 0");
                    EventBus.getDefault().post(new CommonEvent(CommonEvent.COMMON_STRIPEMPTY));
                    return;
                } else if (localBottleInfo.getOverTime() * 1000 < System.currentTimeMillis()) {
                    L.i(TAG,"this bottle id is expired");
                    EventBus.getDefault().post(new CommonEvent(CommonEvent.COMMON_STRIPEXPIRED));
                    return;
                }
            }
            mListener.setBottleId(bottleId + "");
            L.i(TAG, "QR code:" + localBottleInfo.getStripCode());
            L.i(TAG, "date:" + QRCodeTools.longToDateString(localBottleInfo.getOverTime() * 1000));
            L.i(TAG, "trip left:" + localBottleInfo.getStripLeft());
            mBG5Control.sendCode(localBottleInfo.getStripCode(), QRCodeTools.longToDateString(localBottleInfo.getOverTime() * 1000), localBottleInfo.getStripLeft());
            mListener.devicePrepare();
        }
    }

    private void deviceConnect() {
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_CONNECT);
        EventBus.getDefault().post(event);
    }

    private void devicePrepare() {
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_PREPARE);
        EventBus.getDefault().post(event);
    }

    public long stringDateToLong(String day) {
        long dateLong = 0;
        SimpleDateFormat sdfResouce = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
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

    private Data_TB_StripBottleInfo syncBottleInfoToLocal(String userName){
        BottleInfo info = new BottleInfo();
        info.setBottleId("0");
        Data_TB_BottleInfo cloudBottleInfo = mBG5Control.BgBottleSync(userName,info);
        Data_TB_StripBottleInfo localBottleInfo = null;
        if(cloudBottleInfo != null) {
            localBottleInfo = new Data_TB_StripBottleInfo();
            localBottleInfo.setBottledId(cloudBottleInfo.getBottledId());
            localBottleInfo.setOpenDate(cloudBottleInfo.getOpenDate());
            localBottleInfo.setStripCode(cloudBottleInfo.getStripCode());
            localBottleInfo.setTimeZone(cloudBottleInfo.getTimeZone());
            localBottleInfo.setTS(cloudBottleInfo.getTS());
            localBottleInfo.setStripLeft(cloudBottleInfo.getStripLeft());
            //二维码上得过期时间
            long overTime_Code = QRCodeTools.workStripCode(cloudBottleInfo.getStripCode()).getOverTime();
            //开瓶日期+90天
            long overTime_Compute = QRCodeTools.computeOverTime(cloudBottleInfo.getOpenDate() * 1000) / 1000;
            localBottleInfo.setOverTime((overTime_Code < overTime_Compute) ? overTime_Code : overTime_Compute);

            localBottleInfo.setUserName(userName);
        }
        return localBottleInfo;
    }

    public void syncBottleInfoToCloud(final Data_TB_StripBottleInfo stripBottleInfo) {
        L.i(TAG, "syncBottleInfoToCloud");
        BottleInfo info = new BottleInfo();
        info.setOpenDate(stripBottleInfo.getOpenDate());
        info.setBottleId(stripBottleInfo.getBottledId());
        info.setStripCode(stripBottleInfo.getStripCode());
        info.setTimeZone(stripBottleInfo.getTimeZone());
        info.setTS(stripBottleInfo.getTS());
        mBG5Control.BgBottleSync(MyApplication.UserName, info);
    }
}
