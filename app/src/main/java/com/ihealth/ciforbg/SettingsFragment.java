package com.ihealth.ciforbg;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihealth.ciforbg.settings.AboutFragment;
import com.ihealth.ciforbg.settings.TargetRangeFragment;
import com.ihealth.ciforbg.settings.TermsFragment;
import com.ihealth.ciforbg.settings.UnitFragment;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.WeakHandler;

/**
 * Created by lynn on 15-6-29.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    private final int TAB_TARGETRANGE = 0;
    private final int TAB_UNIT = 1;
    private final int TAB_TERMS = 2;
    private final int TAB_ABOUTUS = 3;

    private View mContentView;

    private Fragment[] mSettings;

    private RelativeLayout Rlyt_TargetRange, Rlyt_Unit, Rlyt_Terms, Rlyt_About;
    private TextView Tv_TargetRange, Tv_Unit, Tv_Terms, Tv_About, Tv_UserName;

    private int mCurrentTabIndex = TAB_TARGETRANGE;

    private Context mContext;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_settings, null);
        }
        initWidget(mContentView);
        initValue();
        initView();
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        ((ViewGroup)mContentView.getParent()).removeView(mContentView);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        MeasurePresenter.getInstance().saveBGTarget();
        switch (v.getId()){
            case R.id.fragment_settings_target_range:
                hideSoftInput();
                switchTargetRange();
                break;
            case R.id.fragment_settings_unit:
                hideSoftInput();
                switchUnit();
                break;
            case R.id.fragment_settings_terms:
                hideSoftInput();
                switchTerms();
                break;
            case R.id.fragment_settings_about_us:
                hideSoftInput();
                switchAbout();
                break;
            default:
                break;
        }
        selectMenu(mCurrentTabIndex);
    }

    private void initWidget(View v){
        Rlyt_TargetRange = (RelativeLayout) v.findViewById(R.id.fragment_settings_target_range);
        Rlyt_Unit = (RelativeLayout) v.findViewById(R.id.fragment_settings_unit);
        Rlyt_Terms = (RelativeLayout) v.findViewById(R.id.fragment_settings_terms);
        Rlyt_About = (RelativeLayout) v.findViewById(R.id.fragment_settings_about_us);
        Rlyt_TargetRange.setOnClickListener(this);
        Rlyt_Unit.setOnClickListener(this);
        Rlyt_Terms.setOnClickListener(this);
        Rlyt_About.setOnClickListener(this);

        Tv_TargetRange = (TextView) v.findViewById(R.id.fragment_settings_target_range_txt);
        Tv_Terms = (TextView) v.findViewById(R.id.fragment_settings_target_terms_txt);
        Tv_Unit = (TextView) v.findViewById(R.id.fragment_settings_target_unit_txt);
        Tv_About = (TextView) v.findViewById(R.id.fragment_settings_target_about_txt);
        Tv_UserName = (TextView) v.findViewById(R.id.fragment_settings_username);
    }

    private void initValue(){
        mSettings = new Fragment[4];
        mSettings[0] = new TargetRangeFragment();
        mSettings[1] = new UnitFragment();
        mSettings[2] = new TermsFragment();
        mSettings[3] = new AboutFragment();
    }

    private void initView(){
        if(MyApplication.isFromCI){
            Tv_UserName.setText(MyApplication.UserName);
        }
        getChildFragmentManager().beginTransaction().add(R.id.fragment_settings_content,mSettings[TAB_TARGETRANGE])
                .add(R.id.fragment_settings_content,mSettings[TAB_ABOUTUS])
                .add(R.id.fragment_settings_content,mSettings[TAB_TERMS])
                .add(R.id.fragment_settings_content,mSettings[TAB_UNIT])
                .show(mSettings[TAB_TARGETRANGE])
                .hide(mSettings[TAB_ABOUTUS])
                .hide(mSettings[TAB_TERMS])
                .hide(mSettings[TAB_UNIT])
                .commit();
        selectMenu(mCurrentTabIndex);
    }

    private void switchTargetRange() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(mSettings[mCurrentTabIndex]);
        if(!mSettings[TAB_TARGETRANGE].isAdded()){
            transaction.add(R.id.fragment_settings_content,mSettings[TAB_TARGETRANGE]);
        }
        transaction.show(mSettings[TAB_TARGETRANGE]);
        transaction.commit();
        mCurrentTabIndex = TAB_TARGETRANGE;
    }

    private void switchUnit() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(mSettings[mCurrentTabIndex]);
        if(!mSettings[TAB_UNIT].isAdded()){
            transaction.add(R.id.fragment_settings_content,mSettings[TAB_UNIT]);
        }
        transaction.show(mSettings[TAB_UNIT]);
        transaction.commit();
        mCurrentTabIndex = TAB_UNIT;
    }

    private void switchTerms() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(mSettings[mCurrentTabIndex]);
        if(!mSettings[TAB_TERMS].isAdded()){
            transaction.add(R.id.fragment_settings_content,mSettings[TAB_TERMS]);
        }
        transaction.show(mSettings[TAB_TERMS]);
        transaction.commit();
        mCurrentTabIndex = TAB_TERMS;
    }

    private void switchAbout() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(mSettings[mCurrentTabIndex]);
        if(!mSettings[TAB_ABOUTUS].isAdded()){
            transaction.add(R.id.fragment_settings_content,mSettings[TAB_ABOUTUS]);
        }
        transaction.show(mSettings[TAB_ABOUTUS]);
        transaction.commit();
        mCurrentTabIndex = TAB_ABOUTUS;
    }

    private void selectMenu(int index){
        switch(index){
            case TAB_TARGETRANGE:
                Rlyt_TargetRange.setBackgroundColor(getResources().getColor(R.color.blue));
                Rlyt_Unit.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_Terms.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_About.setBackgroundColor(getResources().getColor(R.color.white));
                Tv_TargetRange.setTextColor(getResources().getColor(R.color.white));
                Tv_About.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_Unit.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_Terms.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                break;
            case TAB_UNIT:
                Rlyt_TargetRange.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_Unit.setBackgroundColor(getResources().getColor(R.color.blue));
                Rlyt_Terms.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_About.setBackgroundColor(getResources().getColor(R.color.white));
                Tv_TargetRange.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_About.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_Unit.setTextColor(getResources().getColor(R.color.white));
                Tv_Terms.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                break;
            case TAB_ABOUTUS:
                Rlyt_TargetRange.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_Unit.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_Terms.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_About.setBackgroundColor(getResources().getColor(R.color.blue));
                Tv_TargetRange.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_About.setTextColor(getResources().getColor(R.color.white));
                Tv_Unit.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_Terms.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                break;
            case TAB_TERMS:
                Rlyt_TargetRange.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_Unit.setBackgroundColor(getResources().getColor(R.color.white));
                Rlyt_Terms.setBackgroundColor(getResources().getColor(R.color.blue));
                Rlyt_About.setBackgroundColor(getResources().getColor(R.color.white));
                Tv_TargetRange.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_About.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_Unit.setTextColor(getResources().getColor(R.color.settings_item_txt_color));
                Tv_Terms.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
    }

    private void hideSoftInput(){
        InputMethodManager inputmanger = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(Tv_Unit.getWindowToken(), 0);
//        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
