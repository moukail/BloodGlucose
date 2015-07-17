package com.ihealth.ciforbg.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.SharePreferenceUtil;

/**
 * Created by lynn on 15-7-1.
 */
public class UnitFragment extends Fragment implements View.OnClickListener{
    private View mContentView;

    private TextView Tv_Unit_1, Tv_Unit_2;

    private Context mContext;

    private SharePreferenceUtil mSharePreferenceUtil;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_unit, null);
            initWidget();
        }
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initValue();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            initView();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_unit_mmol:
                MeasurePresenter.getInstance().setUnit_mmol();
                selectUnit_1();
                break;
            case R.id.fragment_unit_mg:
                MeasurePresenter.getInstance().setUnit_mg();
                selectUnit_2();
                break;
            default:
                break;
        }
    }

    private void initWidget(){
        Tv_Unit_1 = (TextView) mContentView.findViewById(R.id.fragment_unit_mmol);
        Tv_Unit_2 = (TextView) mContentView.findViewById(R.id.fragment_unit_mg);
        Tv_Unit_1.setOnClickListener(this);
        Tv_Unit_2.setOnClickListener(this);
    }

    private void initValue(){
        mSharePreferenceUtil = new SharePreferenceUtil(mContext);
    }

    private void initView(){
        if(mSharePreferenceUtil.getBgUnit() == Data_TB_BGResult.Unit.MG_L){
            selectUnit_2();
        } else if(mSharePreferenceUtil.getBgUnit() == Data_TB_BGResult.Unit.MMOL_L){
            selectUnit_1();
        }
    }

    private void selectUnit_1(){
        Tv_Unit_1.setBackgroundResource(R.drawable.switch_button_normal);
        Tv_Unit_1.setTextColor(getResources().getColor(R.color.blue));
        Tv_Unit_2.setBackgroundResource(0);
        Tv_Unit_2.setTextColor(getResources().getColor(R.color.white));
    }

    private void selectUnit_2(){
        Tv_Unit_2.setBackgroundResource(R.drawable.switch_button_normal);
        Tv_Unit_2.setTextColor(getResources().getColor(R.color.blue));
        Tv_Unit_1.setBackgroundResource(0);
        Tv_Unit_1.setTextColor(getResources().getColor(R.color.white));
    }
}
