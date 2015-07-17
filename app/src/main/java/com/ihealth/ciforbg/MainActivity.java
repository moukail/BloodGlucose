package com.ihealth.ciforbg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.ciforbg.dataclass.CommonEvent;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.dataclass.DeviceEvent;
import com.ihealth.ciforbg.utils.CommKeyWords;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.ResultPresenter;
import com.ihealth.ciforbg.utils.SharePreferenceUtil;
import com.ihealth.ciforbg.utils.dialog.DialogTest;
import com.ihealth.ciforbg.utils.dialog.MyProgressDialog;
import com.ihealth.ciforbg.utils.dialog.ScanQRResultDialog;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.strip.QRCodeTools;
import com.ypy.eventbus.EventBus;
import com.ihealth.ciforbg.utils.Method;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    public static final int FRAGMENT_MEASURE = 0;
    public static final int FRAGMENT_SETTINGS = 1;

    private FragmentManager mFragmentManager;
    private Fragment[] mFragments;

    private TextView Tv_Measure, Tv_Settings;
    private ImageView Iv_Measure, Iv_Settings;
    private LinearLayout Llyt_Measure;
    private LinearLayout Llyt_Settings;

    private Dialog mScanResultDialog;
    private AlertDialog mStripIsEmpty;
    private ProgressDialog mProcressDialog;
    private MyProgressDialog mProgressDialog;

    private DialogTest mDialogTest;

    private SharePreferenceUtil mSharePreferenceUtil;

    private int mCurrentTabIndex = FRAGMENT_MEASURE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initWidget();
        initValue();
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        MeasurePresenter.getInstance().saveBGTarget();
        switch (v.getId()) {
            case R.id.activity_main_measure:
                switchMeasure();
                break;
            case R.id.activity_main_settings:
                switchSettings();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        L.i(TAG, "onDestroy");
        super.onDestroy();
        dismissTestDialog();
        MeasurePresenter.getInstance().destroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.i(TAG, "onActivityResult");
        if (data == null) {
            L.i(TAG, "onActivityResult intent = null");
        } else {
            L.i(TAG, "onActivityResult intent != null");
        }
        MeasurePresenter.getInstance().activityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            cancel();
            return true;
        }
        return false;
    }

    public void onEventMainThread(CommonEvent event) {
        switch (event.getEventType()) {
            case CommonEvent.COMMON_STRIPEMPTY:
                L.i(TAG, "onEventMainThread: Strip is empty");
                displayAlertDialog(getString(R.string.alert_stripnum0));
                dismissProgressDialog();
                break;
            case CommonEvent.COMMON_SCANRESULT:
                L.i(TAG, "onEventMainThread: Bottle info");
                displayScanResultDialog((Data_TB_StripBottleInfo) event.getObj());
                dismissProgressDialog();
                break;
            case CommonEvent.COMMON_STRIPEXPIRED:
                L.i(TAG, "onEventMainThread: Strip expired");
                displayAlertDialog(getString(R.string.strip_expired));
                dismissProgressDialog();
                break;
            case CommonEvent.COMMON_TESTING:
                displayTestDialog();
                break;
            case CommonEvent.COMMON_BGRESULT:
                dismissTestDialog();
                popResultWindow();
                dismissProgressDialog();
                break;
            case CommonEvent.COMMON_EXIT:
                cancel();
                break;
            case CommonEvent.COMMON_NEXTSTEP:
                nextStep();
                break;
            case CommonEvent.COMMON_DECODEQRCODE:
                displayProgressDialog();
                break;
            default:
                break;
        }
    }

    public void onEventMainThread(DeviceEvent event) {
        L.i(TAG,"Device event");
        switch (event.getDeviceState()){
            case DeviceEvent.DEVICE_CONNECT:
                displayProgressDialog();
                break;
            case DeviceEvent.DEVICE_PREPARE:
                dismissProgressDialog();
                break;
            case DeviceEvent.DEVICE_DISCONNECT:
                dismissProgressDialog();
                break;
            case DeviceEvent.DEVICE_INSERT:
                break;
            case DeviceEvent.DEVICE_TESTING:
                break;
            case DeviceEvent.DEVICE_OUTPUT:
                break;
            default:
                break;
        }
    }

    private void initWidget() {
        Llyt_Measure = (LinearLayout) findViewById(R.id.activity_main_measure);
        Llyt_Settings = (LinearLayout) findViewById(R.id.activity_main_settings);
        Llyt_Measure.setOnClickListener(this);
        Llyt_Settings.setOnClickListener(this);

        Tv_Measure = (TextView) findViewById(R.id.activity_main_measure_txt);
        Tv_Settings = (TextView) findViewById(R.id.activity_main_settings_txt);

        Iv_Measure = (ImageView) findViewById(R.id.activity_main_measure_img);
        Iv_Settings = (ImageView) findViewById(R.id.activity_main_settings_img);
    }

    private void initValue() {
        MeasurePresenter.getInstance().initBGTarget();

        mFragmentManager = getSupportFragmentManager();
        mFragments = new Fragment[2];
        mFragments[0] = new MeasureFragment();
        mFragments[1] = new SettingsFragment();

        mSharePreferenceUtil = new SharePreferenceUtil(this);

        //从CI跳转来
        Intent intent = getIntent();
        if (intent.getIntExtra(CommKeyWords.FROM_ACTIVITY, CommKeyWords.FROM_CI) == CommKeyWords.FROM_CI) {
            String CallBack = intent.getStringExtra(CommKeyWords.CI_CALLBACK);
            String AccountName = intent.getStringExtra(CommKeyWords.CI_ACCOUNTNAMR);
            JSONTokener jsonTokener;
            JSONObject jsonObject;
            if (AccountName != null && !TextUtils.isEmpty(AccountName)) {
                MyApplication.isFromCI = true;
                mSharePreferenceUtil.setUserEmail(AccountName);
                MyApplication.UserName = AccountName;
            } else {
                MyApplication.isFromCI = false;
            }
            if (CallBack != null && !TextUtils.isEmpty(CallBack)) {
                jsonTokener = new JSONTokener(CallBack);
                try {
                    jsonObject = (JSONObject) jsonTokener.nextValue();
                    mSharePreferenceUtil.setUrlpath(jsonObject.getString(CommKeyWords.CI_URLPATH));
                    mSharePreferenceUtil.setUrlclass(jsonObject.getString(CommKeyWords.CI_URLCLASS));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initView() {
//        mFragmentManager.beginTransaction().add(R.id.activity_main_container, mFragments[FRAGMENT_MEASURE])
//                .add(R.id.activity_main_container, mFragments[FRAGMENT_SETTINGS]).hide(mFragments[FRAGMENT_SETTINGS])
//                .show(mFragments[FRAGMENT_MEASURE]).commit();
        mFragmentManager.beginTransaction().add(R.id.activity_main_container, mFragments[FRAGMENT_MEASURE]).commit();
    }

    private void switchMeasure() {
        if(mCurrentTabIndex == FRAGMENT_MEASURE){
            return;
        }
        Tv_Measure.setTextColor(getResources().getColor(R.color.blue));
        Iv_Measure.setImageResource(R.drawable.measure_sel);

        Tv_Settings.setTextColor(getResources().getColor(R.color.text_1));
        Iv_Settings.setImageResource(R.drawable.settings);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_right_in,R.anim.push_left_out);
        fragmentTransaction.hide(mFragments[mCurrentTabIndex]);
        if (!mFragments[FRAGMENT_MEASURE].isAdded()) {
            fragmentTransaction.add(R.id.activity_main_container, mFragments[FRAGMENT_MEASURE]);
        }
        fragmentTransaction.show(mFragments[FRAGMENT_MEASURE]);
        fragmentTransaction.commit();
        mCurrentTabIndex = FRAGMENT_MEASURE;
    }

    private void switchSettings() {
        if(mCurrentTabIndex == FRAGMENT_SETTINGS){
            return;
        }
        Tv_Measure.setTextColor(getResources().getColor(R.color.text_1));
        Iv_Measure.setImageResource(R.drawable.measure);

        Tv_Settings.setTextColor(getResources().getColor(R.color.blue));
        Iv_Settings.setImageResource(R.drawable.settings_sel);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_left_in,R.anim.push_right_out);
        fragmentTransaction.hide(mFragments[mCurrentTabIndex]);
        if (!mFragments[FRAGMENT_SETTINGS].isAdded()) {
            fragmentTransaction.add(R.id.activity_main_container, mFragments[FRAGMENT_SETTINGS]);
        }
        fragmentTransaction.show(mFragments[FRAGMENT_SETTINGS]);
        fragmentTransaction.commit();
        mCurrentTabIndex = FRAGMENT_SETTINGS;
    }

    private void displayScanResultDialog(Data_TB_StripBottleInfo bottleInfo) {
        if(mScanResultDialog == null) {
            mScanResultDialog = new ScanQRResultDialog(this, R.style.scanQRDialog, bottleInfo, QRCodeTools.longToDateString(bottleInfo.getOpenDate() * 1000),
                    QRCodeTools.longToDateString(bottleInfo.getOverTime() * 1000));
            mScanResultDialog.setCanceledOnTouchOutside(false);
        }
        if(mScanResultDialog.isShowing()){
            mScanResultDialog.dismiss();
        }
        mScanResultDialog.show();
    }

    private void displayAlertDialog(String message) {
        L.i(TAG,"displayAlertDialog");
        if(mStripIsEmpty == null) {
            mStripIsEmpty = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MeasurePresenter.getInstance().scanQRCode(MainActivity.this);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();
        } if(mStripIsEmpty.isShowing()){
            mStripIsEmpty.dismiss();
        }
        mStripIsEmpty.show();
    }

    private void displayTestDialog() {
        L.i(TAG,"displayTestDialog");
        if (mDialogTest == null) {
            mDialogTest = new DialogTest(this, R.style.DialogStyle);
            mDialogTest.setCanceledOnTouchOutside(false);
            mDialogTest.setCancelable(false);
        }
        if(mDialogTest.isShowing()){
            mDialogTest.dismiss();
        }
        mDialogTest.show();
    }

    private void displayProgressDialog(){
        L.i(TAG,"displayProgressDialog");
//        if(mProcressDialog == null) {
//            mProcressDialog = new ProgressDialog(this);
//        }
//        mProcressDialog.show();
        if(mProgressDialog == null){
            mProgressDialog = new MyProgressDialog(this, R.style.MyDialogStyle);

        }
        if(!mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
//        if(mProcressDialog != null && mProcressDialog.isShowing()){
//            mProcressDialog.dismiss();
//        }
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    private void dismissTestDialog() {
        if (mDialogTest != null) {
            mDialogTest.dismiss();
        }
    }

    private void popResultWindow() {
        ResultPresenter.getInstance().setBgResult(MeasurePresenter.getInstance().mBgResult);
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        startActivity(intent);
    }

    public void cancel() {
        if (MyApplication.isFromCI) {
            L.i(TAG, "Back to CI");
            Intent intent = new Intent();
            intent.putExtra(CommKeyWords.CI_SUCCESS, "false");
            if (!MeasurePresenter.getInstance().isDeviceConnect()) {
                intent.putExtra(CommKeyWords.CI_ERROR, CommKeyWords.CI_DEVICE_NOT_FOUND);
            } else {
                intent.putExtra(CommKeyWords.CI_ERROR, CommKeyWords.CI_ERROR_UNKNOWN);
            }
            intent.putExtra(CommKeyWords.CI_MEASUREMENT, "");
            intent.putExtra(CommKeyWords.CI_DEVICE, "");
            intent.setComponent(new ComponentName(mSharePreferenceUtil.getUrlPath(), mSharePreferenceUtil.getUrlclass()));
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    public void nextStep(){
        if(MyApplication.isFromCI){
            Intent intent = new Intent();
            JSONObject measurement = new JSONObject();
            try {
                measurement.put(CommKeyWords.CI_MEASURETYPE, CommKeyWords.CI_BG);
                measurement.put(CommKeyWords.CI_BGVALUE, MeasurePresenter.getInstance().mBgResult.getBGValue());
                measurement.put(CommKeyWords.CI_TIMEZONE, MeasurePresenter.getInstance().mBgResult.getTimeZone());
                measurement.put(CommKeyWords.CI_MEASUREDATE, Method.getDateString(MeasurePresenter.getInstance().mBgResult.getMeasureTime()));
                measurement.put(CommKeyWords.CI_MEASURETIME, Method.timeToString(MeasurePresenter.getInstance().mBgResult.getMeasureTime()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject device = new JSONObject();
            try {
                device.put(CommKeyWords.CI_DEVICEBATTERY, 0);
                device.put(CommKeyWords.CI_DEVICEID, "");
                device.put(CommKeyWords.CI_DEVICEMODEL, "");
                device.put(CommKeyWords.CI_DEVICEVERSION, "");
                intent.putExtra(CommKeyWords.CI_DEVICE, device.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra(CommKeyWords.CI_SUCCESS, "true");
            intent.putExtra(CommKeyWords.CI_MEASUREMENT, measurement.toString());
            intent.putExtra(CommKeyWords.CI_ERROR,CommKeyWords.CI_ERROR_UNKNOWN);
            intent.setComponent(new ComponentName(mSharePreferenceUtil.getUrlPath(), mSharePreferenceUtil.getUrlclass()));
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
