package com.ihealth.ciforbg.utils;

import android.content.Context;
import android.text.TextUtils;

import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;
import com.ihealth.ciforbg.dataclass.ResultEvent;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by lynn on 15-7-10.
 */
public class ResultPresenter {
    private Context mContext;
    private volatile Data_TB_BGResult mBgResult;
    private static ResultPresenter sPresenter;
//    public static final Vector<Data_TB_Medicine> mMeficines = new Vector<Data_TB_Medicine>();

    public static ResultPresenter getInstance(){
        if(sPresenter == null){
            sPresenter = new ResultPresenter(MyApplication.getContext());
        }
        return sPresenter;
    }

    private ResultPresenter(Context context){
        mContext = context;
    }

    public void switchTimePeriod(){
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_PERIOD);
        EventBus.getDefault().post(event);
    }

    public void switchResult(){
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_RESULT);
        EventBus.getDefault().post(event);
    }

    public void switchMedication(){
        if(MeasurePresenter.mMeficines.size() >= 3){
            ResultEvent event = new ResultEvent(ResultEvent.COMMON_MEDICATIONS);
            EventBus.getDefault().post(event);
        } else {
            ResultEvent event = new ResultEvent(ResultEvent.TYPE_MEDICATION);
            EventBus.getDefault().post(event);
        }
    }

    public void switchAddMedication(){
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_ADDMEDICATION);
        EventBus.getDefault().post(event);
    }

    public void dismissResultWindiow(){
        ResultEvent event = new ResultEvent(ResultEvent.TYPE_DISMISS);
        EventBus.getDefault().post(event);
    }

    public void selectPeriod(int arg){
        if(arg > 9 || arg < 1){
            return;
        } else if(arg == Data_TB_BGResult.Period.RANDOM){
            arg = (int)(Math.random() * 8) +1;
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

    public void addMedication(Data_TB_Medicine medicine){
        if(!TextUtils.isEmpty(medicine.getName())){
            Pharmacy.addMedication(mContext, medicine);
            switchMedication();
        } else {
            ResultEvent event = new ResultEvent(ResultEvent.COMMON_ADDMEDICATION_EXCEPTION);
            EventBus.getDefault().post(event);
        }
    }

    public Data_TB_BGResult getBgResult(){
        return mBgResult;
    }

    public void setBgResult(Data_TB_BGResult result){
        this.mBgResult = result;
    }
}
