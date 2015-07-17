package com.ihealth.ciforbg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.ciforbg.dataclass.DeviceEvent;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.L;
import com.ypy.eventbus.EventBus;

/**
 * Created by lynn on 15-6-29.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener{
    private final String TAG = "MeasureFragment";
    private ImageView Iv_Bluetooth, Iv_Insertion, Iv_DropBlood, Iv_Scan, Iv_GuideImage;
    private TextView Tv_Scan, Tv_GuideTxt, Tv_Cancel;
    private RelativeLayout Rlyt_Scan;

    private View mContentView;
    private Context mContext;
    private Activity mActivity;

    private boolean mScanEnable = false;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_measure, null);
        }
        initView(mContentView);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);//注册事件
    }

    @Override
    public void onStart() {
        super.onStart();
        MeasurePresenter.getInstance().connectDevice();
    }

    @Override
    public void onDestroyView() {
        ((ViewGroup)mContentView.getParent()).removeView(mContentView);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_measure_scan:
                Toast.makeText(mContext,"Scan",Toast.LENGTH_LONG).show();
                MeasurePresenter.getInstance().scanQRCode(mActivity);
                break;
            case R.id.fragment_measure_cancel:
                MeasurePresenter.getInstance().exitApplication();
                break;
            default:
                break;
        }
    }

    public void onEventMainThread(DeviceEvent event) {
        L.i(TAG,"Device event");
        switch (event.getDeviceState()){
            case DeviceEvent.DEVICE_CONNECT:
                setScanEnable();
                break;
            case DeviceEvent.DEVICE_PREPARE:
                setStep_2();
                break;
            case DeviceEvent.DEVICE_DISCONNECT:
                setStep_1();
                setScanDisable();
                break;
            case DeviceEvent.DEVICE_INSERT:
                setStep_3();
                break;
            case DeviceEvent.DEVICE_TESTING:
                break;
            case DeviceEvent.DEVICE_OUTPUT:
                setStep_2();
                break;
            default:
                break;
        }
    }

    private void initView(View v){
        v.findViewById(R.id.fragment_measure_imaginary_line).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Iv_Bluetooth = (ImageView) v.findViewById(R.id.fragment_measure_step_1);
        Iv_Insertion = (ImageView) v.findViewById(R.id.fragment_measure_step_2);
        Iv_DropBlood = (ImageView) v.findViewById(R.id.fragment_measure_step_3);
        Iv_Scan = (ImageView) v.findViewById(R.id.fragment_measure_scan_img);
        Iv_GuideImage = (ImageView) v.findViewById(R.id.fragment_measure_guide_img);

        Tv_Scan = (TextView) v.findViewById(R.id.fragment_measure_scan_txt);
        Tv_GuideTxt = (TextView) v.findViewById(R.id.fragment_measure_guide_txt);
        Rlyt_Scan = (RelativeLayout) v.findViewById(R.id.fragment_measure_scan);
        Tv_Cancel = (TextView) v.findViewById(R.id.fragment_measure_cancel);

        Tv_Cancel.setOnClickListener(this);
    }

    private void setScanEnable(){
        Iv_Scan.setImageResource(R.drawable.scan_enable);
        Tv_Scan.setTextColor(getResources().getColor(R.color.blue));
        Rlyt_Scan.setClickable(true);
        Rlyt_Scan.setOnClickListener(this);
    }

    private void setScanDisable(){
        Iv_Scan.setImageResource(R.drawable.scan_disable);
        Tv_Scan.setTextColor(getResources().getColor(R.color.text_2));
        Rlyt_Scan.setClickable(false);
    }

    private void setStep_1(){
        Iv_Bluetooth.setImageResource(R.drawable.lignt_step_1_1);
        Iv_Insertion.setImageResource(R.drawable.lignt_step_2_1);
        Iv_DropBlood.setImageResource(R.drawable.lignt_step_3_1);
        Iv_GuideImage.setImageResource(R.drawable.guide_image_1);
        Tv_GuideTxt.setText(R.string.guide_1);
    }

    private void setStep_2(){
        Iv_Bluetooth.setImageResource(R.drawable.lignt_step_1_2);
        Iv_Insertion.setImageResource(R.drawable.lignt_step_2_2);
        Iv_DropBlood.setImageResource(R.drawable.lignt_step_3_1);
        Iv_GuideImage.setImageResource(R.drawable.guide_image_2);
        Tv_GuideTxt.setText(R.string.guide_2);
    }
    private void setStep_3(){
        Iv_Bluetooth.setImageResource(R.drawable.lignt_step_1_2);
        Iv_Insertion.setImageResource(R.drawable.lignt_step_2_3);
        Iv_DropBlood.setImageResource(R.drawable.lignt_step_3_2);
        Iv_GuideImage.setImageResource(R.drawable.guide_image_3);
        Tv_GuideTxt.setText(R.string.guide_3);
    }
}
