package com.ihealth.ciforbg.result;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.ResultPresenter;

/**
 * Created by lynn on 15-7-3.
 */
public class PeriodFragment extends Fragment implements View.OnClickListener{

    private View mContentView;

    private RelativeLayout Rlyt_BeforeBreakfast, Rlyt_AfterBreakfast, Rlyt_BeforeLunch, Rlyt_AfterLunch,
            Rlyt_BeforeDinner, Rlyt_AfterDinner, Rlyt_BedTime, Rlyt_AfterSnacks, Rlyt_Random;
    private ImageView Iv_BeforeBreakfast, Iv_AfterBreakfast, Iv_BeforeLunch, Iv_AfterLunch,
            Iv_BeforeDinner, Iv_AfterDinner, Iv_BedTime, Iv_AfterSnacks, Iv_Random;
    private TextView Tv_Cancel;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContentView == null){
            mContentView = inflater.inflate(R.layout.fragment_time_period,null);
        }
        initWidget();
        return mContentView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            initView();
        }
    }

    private void initWidget(){
        Rlyt_BeforeBreakfast = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_before_breakfast);
        Rlyt_AfterBreakfast = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_after_breakfast);
        Rlyt_BeforeLunch = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_before_lunch);
        Rlyt_AfterLunch = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_after_lunch);
        Rlyt_BeforeDinner = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_before_dinner);
        Rlyt_AfterDinner = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_after_dinner);
        Rlyt_BedTime = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_bedtime);
        Rlyt_AfterSnacks = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_after_snacks);
        Rlyt_Random = (RelativeLayout) mContentView.findViewById(R.id.fragment_period_random);

        Iv_BeforeBreakfast = (ImageView) mContentView.findViewById(R.id.fragment_period_before_breakfast_sel);
        Iv_AfterBreakfast = (ImageView) mContentView.findViewById(R.id.fragment_period_after_breakfast_sel);
        Iv_BeforeLunch = (ImageView) mContentView.findViewById(R.id.fragment_period_before_lunch_sel);
        Iv_AfterLunch = (ImageView) mContentView.findViewById(R.id.fragment_period_after_lunch_sel);
        Iv_BeforeDinner = (ImageView) mContentView.findViewById(R.id.fragment_period_before_dinner_sel);
        Iv_AfterDinner = (ImageView) mContentView.findViewById(R.id.fragment_period_after_dinner_sel);
        Iv_BedTime = (ImageView) mContentView.findViewById(R.id.fragment_period_bedtime_sel);
        Iv_AfterSnacks = (ImageView) mContentView.findViewById(R.id.fragment_period_after_snacks_sel);
        Iv_Random = (ImageView) mContentView.findViewById(R.id.fragment_period_random_sel);

        Tv_Cancel = (TextView) mContentView.findViewById(R.id.fragment_period_cancel);

        Rlyt_BeforeBreakfast.setOnClickListener(this);
        Rlyt_AfterBreakfast.setOnClickListener(this);
        Rlyt_BeforeLunch.setOnClickListener(this);
        Rlyt_AfterLunch.setOnClickListener(this);
        Rlyt_BeforeDinner.setOnClickListener(this);
        Rlyt_AfterDinner.setOnClickListener(this);
        Rlyt_BedTime.setOnClickListener(this);
        Rlyt_AfterSnacks.setOnClickListener(this);
        Rlyt_Random.setOnClickListener(this);
        Tv_Cancel.setOnClickListener(this);
    }

    private void initView(){
        switch(MeasurePresenter.getInstance().mBgResult.getMTimeType()){
            case Data_TB_BGResult.Period.BEFORE_BREAKFAST:
                selectBeforeBreakfast();
                break;
            case Data_TB_BGResult.Period.AFTER_BREAKFAST:
                selectAfterBreakfast();
                break;
            case Data_TB_BGResult.Period.BEFORE_LUNCH:
                selectBeforeLunch();
                break;
            case Data_TB_BGResult.Period.AFTER_LUNCH:
                selectAfterLunch();
                break;
            case Data_TB_BGResult.Period.BEFORE_DINNER:
                selectBeforeDinner();
                break;
            case Data_TB_BGResult.Period.AFTER_DINNER:
                selectAfterDinner();
                break;
            case Data_TB_BGResult.Period.BEDTIME:
                selectBedTime();
                break;
            case Data_TB_BGResult.Period.AFTER_SNACKS:
                selectAfterSnacks();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_period_before_breakfast:
                selectBeforeBreakfast();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.BEFORE_BREAKFAST);
                break;
            case R.id.fragment_period_after_breakfast:
                selectAfterBreakfast();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.AFTER_BREAKFAST);
                break;
            case R.id.fragment_period_before_lunch:
                selectBeforeLunch();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.BEFORE_LUNCH);
                break;
            case R.id.fragment_period_after_lunch:
                selectAfterLunch();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.AFTER_LUNCH);
                break;
            case R.id.fragment_period_before_dinner:
                selectBeforeDinner();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.BEFORE_DINNER);
                break;
            case R.id.fragment_period_after_dinner:
                selectAfterDinner();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.AFTER_DINNER);
                break;
            case R.id.fragment_period_bedtime:
                selectBedTime();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.BEDTIME);
                break;
            case R.id.fragment_period_after_snacks:
                selectAfterSnacks();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.AFTER_SNACKS);
                break;
            case R.id.fragment_period_random:
                selectRandom();
                ResultPresenter.getInstance().selectPeriod(Data_TB_BGResult.Period.RANDOM);
                break;
            case R.id.fragment_period_cancel:
                ResultPresenter.getInstance().switchResult();
                break;
            default:
                break;
        }
    }

    private void selectBeforeBreakfast(){
        Iv_BeforeBreakfast.setVisibility(View.VISIBLE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectAfterBreakfast(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.VISIBLE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectBeforeLunch(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.VISIBLE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectAfterLunch(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.VISIBLE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectBeforeDinner(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.VISIBLE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectAfterDinner(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.VISIBLE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectBedTime(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.VISIBLE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectAfterSnacks(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.VISIBLE);
        Iv_Random.setVisibility(View.GONE);
    }

    private void selectRandom(){
        Iv_BeforeBreakfast.setVisibility(View.GONE);
        Iv_AfterBreakfast.setVisibility(View.GONE);
        Iv_BeforeLunch.setVisibility(View.GONE);
        Iv_AfterLunch.setVisibility(View.GONE);
        Iv_BeforeDinner.setVisibility(View.GONE);
        Iv_AfterDinner.setVisibility(View.GONE);
        Iv_BedTime.setVisibility(View.GONE);
        Iv_AfterSnacks.setVisibility(View.GONE);
        Iv_Random.setVisibility(View.VISIBLE);
    }
}
