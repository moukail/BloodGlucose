package com.ihealth.ciforbg.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.database.Constants_DB;
import com.ihealth.ciforbg.database.DataBaseTools;
import com.ihealth.ciforbg.dataclass.CommonEvent;
import com.ihealth.ciforbg.dataclass.Data_DB_BGPlan;
import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;
import com.ihealth.ciforbg.dataclass.DeviceEvent;
import com.ihealth.ciforbg.dataclass.ResultEvent;
import com.ihealth.ciforbg.settings.TargetRangeFragment;
import com.ihealth.ciforbg.utils.strip.QRCodeManager;
import com.ihealth.ciforbg.utils.strip.QRCodeTools;
import com.ihealth.ciforbg.utils.strip.ScanQRcodeActivity;
import com.ypy.eventbus.EventBus;

import java.util.Vector;

/**
 * Created by lynn on 15-7-2.
 */
public class MeasurePresenter implements DeviceExplorer.OnExplorListener {
    private final String TAG = "BgPresenter";

    //    public static float glucose_targets_pre_1 = 126;
    public static float glucose_targets_pre_2 = 110;
    public static float glucose_targets_pre_3 = 76;
    public static float glucose_targets_post_1 = 200;
    public static float glucose_targets_post_2 = 140;
    public static float glucose_targets_post_3 = 79;

    private static MeasurePresenter sMeasurePresenter;

    private Context mContext;

    private boolean isDeviceConnected = false;

    private DeviceExplorer mExplorer;

    private SharePreferenceUtil mSharePreferenceUtil;

    private DataBaseTools tools;

    private DataBaseTools mDataBaseTools;

    public final Data_TB_BGResult mBgResult;
    public static final Vector<Data_TB_Medicine> mMeficines = new Vector<>();
//    public Data_TB_StripBottleInfo mBottleInfo;
    /**
     * 正在使用的bottleId
     */
    private String mBottleId;

    private boolean mScanEnable = true;

    private MeasurePresenter(Context context) {
        this.mContext = context;
        this.mBgResult = new Data_TB_BGResult();
        this.mExplorer = new DeviceExplorer(mContext);
        this.mExplorer.setOnExplorListener(this);
        this.mSharePreferenceUtil = new SharePreferenceUtil(mContext);
        this.mDataBaseTools = new DataBaseTools(mContext);
        this.tools = new DataBaseTools(mContext);
    }

    public static MeasurePresenter getInstance() {
        if (sMeasurePresenter == null) {
            sMeasurePresenter = new MeasurePresenter(MyApplication.getContext());
        }
        return sMeasurePresenter;
    }

    public void connectDevice() {
        mExplorer.startScanDevice();
    }

    public void scanQRCode(Activity activity) {
        if(mScanEnable) {
            Intent intent = new Intent(activity, ScanQRcodeActivity.class);
            activity.startActivityForResult(intent, 1);
            mScanEnable = false;
        }
    }

    public void activityResult(final int requestCode, int resultCode, Intent intent) {
        L.i(TAG, "activityResult  " + "requestCode:" + requestCode + ", resultCode:" + resultCode);

        mScanEnable = true;
        if (requestCode == 1 && resultCode == 1) {
            final Bundle bundle = intent.getExtras();
            //开始解析二维码
            CommonEvent event = new CommonEvent(CommonEvent.COMMON_DECODEQRCODE);
            EventBus.getDefault().post(event);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String str = bundle.getString("zxingText");
                    L.i(TAG, "QR Code:" + str);
                    QRCodeManager.setCurrentQRCode(str);
                    //解码后的bottleInfo
                    Data_TB_StripBottleInfo decodeResult = QRCodeTools.workStripCode(str);
                    L.i(TAG, "Decode bottle info success,bottleID:" + decodeResult.getBottledId());
                    //从云端下载的bottleInfo
                    final Data_TB_StripBottleInfo cloudBottleInfo = mExplorer.getLeftNumberFromCloud(decodeResult.getBottledId());
                    //本地存储的bottleInfo
                    Data_TB_StripBottleInfo localBottleInfo = QRCodeManager.readStripInfoFromDB(mContext, decodeResult.getBottledId() + "", MyApplication.UserName);

                    if (cloudBottleInfo != null) {//云端数据不为空
                        cloudBottleInfo.setOverTime(QRCodeTools.computeOverTime(cloudBottleInfo.getOpenDate() * 1000) / 1000);
                        cloudBottleInfo.setStripCode(decodeResult.getStripCode());
                        if(localBottleInfo != null) {
                            if (cloudBottleInfo.getStripLeft() > localBottleInfo.getStripLeft()) {
                                cloudBottleInfo.setStripLeft(localBottleInfo.getStripLeft());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mExplorer.syncBottleInfoToCloud(cloudBottleInfo);
                                    }
                                }).start();
                            } else {
                                QRCodeManager.storeStipInfoToDB(mContext, cloudBottleInfo, MyApplication.UserName);
                            }
                        }
                        localBottleInfo = cloudBottleInfo;
                    } else if (localBottleInfo != null) { //云端无试条瓶信息，本地有，同步到云
                        L.i(TAG, "this bottle id is not exist in cloud but exist in database");
                        L.i(TAG, "Local bottle info,bottleID:" + localBottleInfo.getBottledId() + ", left Number:" + localBottleInfo.getStripLeft());
                        final Data_TB_StripBottleInfo bottleInfo = localBottleInfo;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mExplorer.syncBottleInfoToCloud(bottleInfo);
                            }
                        }).start();
                    }
                    if (localBottleInfo == null) {  //云端本地都没有试条信息
                        L.i(TAG, "this bottle id is not exist in cloud and database");
                        localBottleInfo = decodeResult;
                        localBottleInfo.setUserName(MyApplication.UserName);
                        localBottleInfo.setNewStrip(true);
                    }
                    CommonEvent event = new CommonEvent(CommonEvent.COMMON_SCANRESULT);
                    event.setObj(localBottleInfo);
                    EventBus.getDefault().post(event);
                }
            }).start();
        }
    }


    public void addMedication(Data_TB_Medicine medicine) {
        if (!TextUtils.isEmpty(medicine.getName())) {
            Pharmacy.addMedication(mContext, medicine);
            switchMedication();
        } else {
            ResultEvent event = new ResultEvent(ResultEvent.COMMON_ADDMEDICATION_EXCEPTION);
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 保存试条瓶信息
     */
    public void saveBottleInfo(final Data_TB_StripBottleInfo bottleInfo) {
        mBottleId = bottleInfo.getBottledId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                QRCodeManager.storeStipInfoToDB(mContext, bottleInfo, MyApplication.UserName);//保存到本地
                mExplorer.sendCodeToDevice(bottleInfo);//发送到下位机
                mExplorer.syncBottleInfoToCloud(bottleInfo);//同步到云
            }
        }).start();
    }

    /**
     * 开瓶
     */
    public void openBottle(final Data_TB_StripBottleInfo bottleInfo) {
        mBottleId = bottleInfo.getBottledId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                QRCodeManager.storeStipInfoToDB(mContext, bottleInfo, MyApplication.UserName);//保存到本地
                mExplorer.sendCodeToDevice(bottleInfo);//发送到下位机
                mExplorer.syncBottleInfoToCloud(bottleInfo);//同步到云
            }
        }).start();
    }


    public void switchTimePeriod() {
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_PERIOD);
        EventBus.getDefault().post(event);
    }

    public void switchResult() {
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_RESULT);
        EventBus.getDefault().post(event);
    }

    public void switchMedication() {
        if (mMeficines.size() >= 3) {
            ResultEvent event = new ResultEvent(ResultEvent.COMMON_MEDICATIONS);
            EventBus.getDefault().post(event);
        } else {
            ResultEvent event = new ResultEvent(ResultEvent.TYPE_MEDICATION);
            EventBus.getDefault().post(event);
        }
    }

    public void switchAddMedication() {
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_ADDMEDICATION);
        EventBus.getDefault().post(event);
    }

    public void dismissResultWindiow() {
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_DISMISS);
        EventBus.getDefault().post(event);
    }

    public void selectPeriod(int arg) {
        if (arg > 9 || arg < 1) {
            return;
        } else if (arg == Data_TB_BGResult.Period.RANDOM) {
            arg = (int) (Math.random() * 8) + 1;
        }
        mBgResult.setMTimeType(arg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    switchResult();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setUnit_mg() {
        mSharePreferenceUtil.setBGUnit(Data_TB_BGResult.Unit.MG_L);
    }

    public void setUnit_mmol() {
        mSharePreferenceUtil.setBGUnit(Data_TB_BGResult.Unit.MMOL_L);
    }

    public boolean isDeviceConnect() {
        return this.isDeviceConnected;
    }

    public void exitApplication() {
        CommonEvent event = new CommonEvent(CommonEvent.COMMON_EXIT);
        EventBus.getDefault().post(event);
    }

    public void nextApplication() {
        CommonEvent event = new CommonEvent(CommonEvent.COMMON_NEXTSTEP);
        EventBus.getDefault().post(event);
    }

    public void initBGTarget() {
        L.i(TAG, "initBGTarget");
        Cursor cursor = tools.selectData(Constants_DB.TABLE_TB_BGPLAN, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            MyApplication.glucose_targets_post_1 = cursor.getInt(cursor.getColumnIndex(Constants_DB.BGPLAN_POST_1));
            MyApplication.glucose_targets_post_2 = cursor.getInt(cursor.getColumnIndex(Constants_DB.BGPLAN_POST_2));
            MyApplication.glucose_targets_post_3 = cursor.getInt(cursor.getColumnIndex(Constants_DB.BGPLAN_POST_3));
            MyApplication.glucose_targets_pre_1 = cursor.getInt(cursor.getColumnIndex(Constants_DB.BGPLAN_PRE_1));
            MyApplication.glucose_targets_pre_2 = cursor.getInt(cursor.getColumnIndex(Constants_DB.BGPLAN_PRE_2));
            MyApplication.glucose_targets_pre_3 = cursor.getInt(cursor.getColumnIndex(Constants_DB.BGPLAN_PRE_3));
            cursor.close();
        }
    }

    public void saveBGTarget() {
        if (!TargetRangeFragment.isEdit) {
            return;
        }
        int mUnit = mSharePreferenceUtil.getBgUnit();
        if (mUnit == Data_TB_BGResult.Unit.MG_L) {
            MyApplication.glucose_targets_pre_2 = (int) glucose_targets_pre_2;
            MyApplication.glucose_targets_pre_3 = (int) glucose_targets_pre_3;
            MyApplication.glucose_targets_post_2 = (int) glucose_targets_post_2;
            MyApplication.glucose_targets_post_3 = (int) glucose_targets_post_3;
        } else {
            MyApplication.glucose_targets_pre_2 = (int) Method.getBgmmloTomg(glucose_targets_pre_2);
            MyApplication.glucose_targets_pre_3 = (int) Method.getBgmmloTomg(glucose_targets_pre_3);
            MyApplication.glucose_targets_post_2 = (int) Method.getBgmmloTomg(glucose_targets_post_2);
            MyApplication.glucose_targets_post_3 = (int) Method.getBgmmloTomg(glucose_targets_post_3);
        }

        Data_DB_BGPlan plan = new Data_DB_BGPlan();
        plan.setGlucose_targets_post_1(MyApplication.glucose_targets_post_1);
        plan.setGlucose_targets_post_2(MyApplication.glucose_targets_post_2);
        plan.setGlucose_targets_post_3(MyApplication.glucose_targets_post_3);
        plan.setGlucose_targets_pre_1(MyApplication.glucose_targets_pre_1);
        plan.setGlucose_targets_pre_2(MyApplication.glucose_targets_pre_2);
        plan.setGlucose_targets_pre_3(MyApplication.glucose_targets_pre_3);
        tools.deleteData(Constants_DB.TABLE_TB_BGPLAN);
        tools.addData(Constants_DB.TABLE_TB_BGPLAN, plan);
    }

    public static boolean hasInsulin() {
        for (Data_TB_Medicine medicine : mMeficines) {
            if (medicine.getType() == Data_TB_Medicine.Type.INSULIN) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasOralMeds() {
        for (Data_TB_Medicine medicine : mMeficines) {
            if (medicine.getType() == Data_TB_Medicine.Type.ORAL_MEDS) {
                return true;
            }
        }
        return false;
    }

    public void destroy() {
        saveBGTarget();
        EventBus.getDefault().post(new CommonEvent(CommonEvent.COMMON_SAVETARGETS));
        mExplorer.close();
        System.exit(0);
    }

    @Override
    public void deviceTesting() {
        L.i(TAG, "deviceTesting");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Data_TB_StripBottleInfo bottleInfo = QRCodeManager.readStripInfoFromDB(mContext, mBottleId, MyApplication.UserName);
                if(bottleInfo != null){
                    int leftNum = bottleInfo.getStripLeft() - 1;
                    L.i("Testing", "left number:" + leftNum);
                    bottleInfo.setStripLeft(leftNum);
                    if(QRCodeManager.updateStripNumber(mContext, mBottleId, MyApplication.UserName,leftNum)){
                        mExplorer.syncStripNumber(bottleInfo);
                    }
                }
                CommonEvent event = new CommonEvent(CommonEvent.COMMON_TESTING);
                EventBus.getDefault().post(event);
            }
        }).start();
    }

    @Override
    public void deviceFetchStrip() {
        L.i(TAG, "deviceFetchStrip");
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_OUTPUT);
        EventBus.getDefault().post(event);
    }

    @Override
    public void deviceInsertStrip() {
        L.i(TAG, "deviceInsertStrip");
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_INSERT);
        EventBus.getDefault().post(event);
    }

    @Override
    public void deviceDisconnect() {
        L.i(TAG, "deviceDisconnect");
        isDeviceConnected = false;
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_DISCONNECT);
        EventBus.getDefault().post(event);
    }

    @Override
    public void deviceConnect() {
        L.i(TAG, "deviceConnect");
        isDeviceConnected = true;
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_CONNECT);
        EventBus.getDefault().post(event);
    }

    @Override
    public void devicePrepare() {
        L.i(TAG, "devicePrepare");
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_PREPARE);
        EventBus.getDefault().post(event);
    }

    @Override
    public void receiveResult(int arg) {
        L.i(TAG, "receiveResult");
        mMeficines.clear();
        mBgResult.setTimeZone(Method.getTimeZone());
        mBgResult.setBGValue(arg);
        mBgResult.setMeasureTime(System.currentTimeMillis() / 1000);
        mBgResult.setBottleId(mBottleId);
        mBgResult.setPhoneDataID(Method.getBgDataId(Method.getLocalMacAddres(mContext), System.currentTimeMillis(), arg));
        CommonEvent event = new CommonEvent(CommonEvent.COMMON_BGRESULT);
        EventBus.getDefault().post(event);
    }

    @Override
    public void setBottleId(String bottleId) {
        this.mBottleId = bottleId;
    }

//    @Override
//    public void refreshBottleInfo(String bottleId) {
////        return QRCodeManager.readStripInfoFromDB(mContext, bottleId + "");
//    }
}
