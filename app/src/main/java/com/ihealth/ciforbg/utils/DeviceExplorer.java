package com.ihealth.ciforbg.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ihealth.ciforbg.MainActivity;
import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.dataclass.CommonEvent;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.dataclass.DeviceEvent;
import com.ihealth.ciforbg.utils.strip.QRCodeManager;
import com.ihealth.ciforbg.utils.strip.QRCodeTools;
import com.ypy.eventbus.EventBus;

import java.lang.reflect.*;
import java.util.ArrayList;

import jiuan.androidBg.Bluetooth.BG5Control;
import jiuan.androidBg.CloudBGData.BottleInfo;
import jiuan.androidBg.CloudBGData.BottleInfo4StripSet;
import jiuan.androidBg.CloudBGData.BottleInfo4StripSync;
import jiuan.androidBg.CloudBGData.Data_TB_BottleInfo;
import jiuan.androidBg.Comm.BGCommManager;
import jiuan.androidBg.Observer.Interface_Observer_BG;
import jiuan.androidBg.Observer.Interface_Observer_CoomMsg;

/**
 * Created by lynn on 15-7-3.
 */
public class DeviceExplorer implements Interface_Observer_CoomMsg, Interface_Observer_BG {
    private final String TAG = "Explorer";

    public interface OnExplorListener {
        void deviceTesting();

        void deviceFetchStrip();

        void deviceInsertStrip();

        void deviceDisconnect();

        void deviceConnect();

        void devicePrepare();

        void receiveResult(int arg);

        void setBottleId(String bottleId);
    }

    private OnExplorListener mOnExplorListener = new OnExplorListener() {
        @Override
        public void deviceTesting() {

        }

        @Override
        public void deviceFetchStrip() {

        }

        @Override
        public void deviceInsertStrip() {

        }

        @Override
        public void deviceDisconnect() {

        }

        @Override
        public void deviceConnect() {

        }

        @Override
        public void devicePrepare() {

        }

        @Override
        public void receiveResult(int arg) {

        }

        @Override
        public void setBottleId(String bottleId) {

        }
    };

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BGCommManager mBgCommManager;
    private BG5Control mBg5Control = null;

    private boolean isConnecting = false;
    private boolean isStop = false;

    public DeviceExplorer(Context context) {
        this.mContext = context;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBgCommManager = new BGCommManager(mContext);
        this.mBgCommManager.msgSubject.attach(this);
        initBroadcast();
    }

    public void close() {
        isStop = true;
        mBluetoothAdapter.cancelDiscovery();
        mContext.unregisterReceiver(BluetoothReceiver);
        mBgCommManager.msgSubject.detach(this);
        mBgCommManager.stop();
        if (mBg5Control != null) {
            mBg5Control.bg5subject.detach(this);
            mBg5Control.stop();
        }
    }

    private void initBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(BluetoothReceiver, intentFilter);
    }

    private BroadcastReceiver BluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                L.i(TAG, "start discovery");
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                L.i(TAG, "find device:" + device.getName() + "--" + device.getAddress());
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    L.e(TAG, "device was bonded");
                    if (!isConnecting) {
                        isConnecting = true;
                        mBluetoothAdapter.cancelDiscovery();
                        mBgCommManager.ConnectBluetoothDevice(device);
                        isConnecting = false;
                    }
                }
            }
            if (action.equals(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                L.i(TAG, "discovery end");
                if (!isConnecting && !isStop) {
                    mBluetoothAdapter.startDiscovery();
                }
            }
        }
    };

    public void startScanDevice() {
        isStop = false;
        mBluetoothAdapter.startDiscovery();
    }

    public void stopScanDevice() {
        isStop = true;
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void msgBluetoothDeviceConnect(String s) {
        L.i(TAG, "BluetoothDeviceConnect:" + s);
        this.mBg5Control = mBgCommManager.getBG5Control(s);
        if (this.mBg5Control != null) {
            this.mBg5Control.bg5subject.attach(this);
            this.mBg5Control.connect(mContext, MyApplication.UserName, MyApplication.clientID, MyApplication.clientSecret);
            stopScanDevice();
        }
    }

    @Override
    public void msgBluetoothDeviceDisconnect(String s) {
        L.i(TAG, "BluetoothDeviceDisconnect:" + s);
        mOnExplorListener.deviceDisconnect();
        startScanDevice();
    }

    @Override
    public void msgHeadsetPluIn() {

    }

    @Override
    public void msgHeadsetPullOut() {

    }

    @Override
    public void msgUserStatus(int status) {
        L.i(TAG, "msgUserStatus:" + status);
        if (status == 1 || status == 2 || status == 3 || status == 4) {
            mOnExplorListener.deviceConnect();
            new Thread(new InitializeConnection(mContext, mBg5Control, mOnExplorListener)).start();
        }
    }

    @Override
    public void msgBGError(int i) {
        L.i(TAG, "msgBGError:" + i);
    }

    @Override
    public void msgBGStripIn() {
        L.i(TAG, "msgBGStripIn");
        mOnExplorListener.deviceInsertStrip();
    }

    @Override
    public void msgBGGetBlood() {
        L.i(TAG, "msgBGGetBlood");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                QRCodeManager.stripNumberDecreasing(mContext, )
//                mBg5Control.setBgBottleLeftNumber();
//            }
//        }).start();
        mOnExplorListener.deviceTesting();
    }

    @Override
    public void msgBGStripOut() {
        L.i(TAG, "msgBGStripOut");
        mOnExplorListener.deviceFetchStrip();
    }

    @Override
    public void msgBGResult(int i) {
        L.i(TAG, "msgBGResult:" + i);
        mOnExplorListener.receiveResult(i);
    }

    @Override
    public void msgBGPowerOff() {

    }

    @Override
    public void msgDeviceReady_new() {
        L.i(TAG, "msgDeviceReady_new");
    }

    @Override
    public void msgIdentify(boolean b) {
        L.i(TAG, "msgIdentify");
    }

    private void devicePrepare() {
        DeviceEvent event = new DeviceEvent(DeviceEvent.DEVICE_PREPARE);
        EventBus.getDefault().post(event);
    }

    synchronized public void sendCodeToDevice(final Data_TB_StripBottleInfo bottleInfo) {
        L.i(TAG, "sendCodeToDevice");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mBg5Control != null) {
//                    mBg5Control.setParams(1, 1);
//
//                        int bottleId = mBg5Control.readBottleId();
//                        Log.i("sendCodeToDevice", "bottleId:" + bottleId);
//
//                        Thread.sleep(200);
//                        try {
//                            String offline = mBg5Control.readOfflineData();
//                            Log.i("sendCodeToDevice", "offline:" + offline);
//                            Thread.sleep(200);
//                        } catch (ArrayIndexOutOfBoundsException e) {
//                            e.printStackTrace();
//                        }
//
////							if (bg5Control.deleteOfflineData()) {
////								Message message4 = new Message();
////								message4.what = 4;
////								handler.sendMessage(message4);
////							}   `
//
//                        String code = mBg5Control.getCode();
//                        Log.i("sendCodeToDevice", "code:" + code);
//                        Thread.sleep(200);
//                             mBg5Control.getBottleInfoByErWeiMa(bottleInfo.getStripCode());
//                             mBg5Control.deleteBottleId();
                        Thread.sleep(200);
                        L.i("sendCodeToDevice", "send code:" + bottleInfo.getStripCode());
                        mBg5Control.sendCode(bottleInfo.getStripCode(), QRCodeTools.longToDateString(bottleInfo.getOverTime()), bottleInfo.getStripLeft());
//                        mOnExplorListener.devicePrepare();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setOnExplorListener(OnExplorListener listener) {
        this.mOnExplorListener = listener;
    }

    /**
     * 同步试条数量到云
     *
     * @param stripBottleInfo
     */
    public void syncStripNumber(final Data_TB_StripBottleInfo stripBottleInfo) {
        L.i(TAG, "syncStripNumber");
        new Thread(new Runnable() {
            @Override
            public void run() {
                BottleInfo4StripSet bottleInfo4StripSet = new BottleInfo4StripSet();
                bottleInfo4StripSet.setTimeZone(stripBottleInfo.getTimeZone());
                bottleInfo4StripSet.setStripCode(stripBottleInfo.getStripCode());
                bottleInfo4StripSet.setBottleId(stripBottleInfo.getBottledId());
                bottleInfo4StripSet.setOpenDate(stripBottleInfo.getOpenDate());
                bottleInfo4StripSet.setStripNumber(stripBottleInfo.getStripLeft());
                bottleInfo4StripSet.setStripTS(System.currentTimeMillis() / 1000);
                ArrayList<BottleInfo4StripSet> bottleInfo4StripSets = new ArrayList<BottleInfo4StripSet>();
                bottleInfo4StripSets.add(bottleInfo4StripSet);
                if (mBg5Control.setBgBottleLeftNumber(MyApplication.UserName, bottleInfo4StripSets)) {
                    L.i(TAG, "剩余试条数上传成功 bottle id:" + stripBottleInfo.getBottledId() + ", left number:" + stripBottleInfo.getStripLeft());
                } else {
                    L.i(TAG, "剩余试条数上传失败");
                }
            }
        }).start();
    }

    /**
     * 从云端下载试条瓶信息
     *
     * @param bottleId
     * @return
     */
    public Data_TB_StripBottleInfo getLeftNumberFromCloud(String bottleId) {
        L.i(TAG, "getBottleInfoFromCloud");
        Data_TB_StripBottleInfo bottleInfo = null;
        Data_TB_BottleInfo bottle = mBg5Control.bgbottle_leftnumber_get(MyApplication.UserName, bottleId);//从云端下载试条瓶信息
        if (bottle != null) { //下载成功
            L.i(TAG, "下载成功 bottle id = " + bottle.getBottledId() + "left strip = " + bottle.getStripLeft());
            bottleInfo = new Data_TB_StripBottleInfo();
            bottleInfo.setBottledId(bottle.getBottledId());
            bottleInfo.setOpenDate(bottle.getOpenDate());
            bottleInfo.setStripCode(bottle.getStripCode());
            bottleInfo.setTimeZone(bottle.getTimeZone());
            bottleInfo.setTS(bottle.getTS());
            bottleInfo.setStripLeft(bottle.getStripLeft());
        }
        return bottleInfo;
    }

    public void syncBottleInfoToCloud(final Data_TB_StripBottleInfo stripBottleInfo) {
        L.i(TAG, "syncBottleInfoToCloud");
        BottleInfo info = new BottleInfo();
        info.setOpenDate(stripBottleInfo.getOpenDate());
        info.setBottleId(stripBottleInfo.getBottledId());
        info.setStripCode(stripBottleInfo.getStripCode());
        info.setTimeZone(stripBottleInfo.getTimeZone());
        info.setTS(stripBottleInfo.getTS());
        mBg5Control.BgBottleSync(MyApplication.UserName, info);
    }

    /**
     * 下载用户bottle信息
     *
     * @return 同步成功返回bottle info 失败返回null
     */
    public Data_TB_StripBottleInfo syncBottleInfoToLocal(String userName) {
        BottleInfo info = new BottleInfo();
        info.setBottleId("0");
        Data_TB_BottleInfo cloudBottleInfo = mBg5Control.BgBottleSync(userName, info);
        Data_TB_StripBottleInfo localBottleInfo = null;
        if (cloudBottleInfo != null) {
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
}
