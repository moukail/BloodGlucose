package com.ihealth.ciforbg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.ihealth.ciforbg.dataclass.ResultEvent;
import com.ihealth.ciforbg.result.AddMedicineFragment;
import com.ihealth.ciforbg.result.MedicationFragment;
import com.ihealth.ciforbg.result.PeriodFragment;
import com.ihealth.ciforbg.result.ResultFragment;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.L;
import com.ypy.eventbus.EventBus;

/**
 * Created by lynn on 15-6-29.
 */
public class ResultActivity extends FragmentActivity {

    private final String TAG = "ResultActivity";

    private final int FRAGMENT_RESULT = 0;
    private final int FRAGMENT_PERIOD = 1;
    private final int FRAGMENT_MEDICATION = 2;
    private final int FRAGMENT_ADDMEDICATION = 3;

    private FragmentManager mFragmentManager;
    private Fragment[] mFragments;

    private AlertDialog mAlertDialog;

    private RelativeLayout Rlyt_Contaiter;

    private int mCurrentTabIndex = FRAGMENT_RESULT;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(TAG,"onCreate");
        setContentView(R.layout.activity_result);
        Rlyt_Contaiter = (RelativeLayout) findViewById(R.id.activity_result_container);
        initValue();
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ResultEvent event){
        switch(event.getEventType()){
            case ResultEvent.TYPE_PERIOD:
                hideSoftInput();
                switchPeriod();
                break;
            case ResultEvent.TYPE_MEDICATION:
                hideSoftInput();
                switchMedication();
                break;
            case ResultEvent.TYPE_RESULT:
                hideSoftInput();
                switchResult();
                break;
            case ResultEvent.TYPE_ADDMEDICATION:
                hideSoftInput();
                switchAddMedication();
                break;
            case ResultEvent.TYPE_DISMISS:
                finish();
                break;
            case ResultEvent.COMMON_MEDICATIONS:
                displayAlertDialog(getString(R.string.results_too_many_medications));
                break;
            case ResultEvent.COMMON_ADDMEDICATION_EXCEPTION:
                displayAlertDialog(getString(R.string.results_medication_name_null));
                break;
            default:
                break;
        }
    }

    private void initValue() {
        MeasurePresenter.mMeficines.clear();

        mFragmentManager = getSupportFragmentManager();
        mFragments = new Fragment[4];
        mFragments[0] = new ResultFragment();
        mFragments[1] = new PeriodFragment();
        mFragments[2] = new MedicationFragment();
        mFragments[3] = new AddMedicineFragment();
    }

    private void initView(){
        mFragmentManager.beginTransaction().add(R.id.activity_result_container, mFragments[FRAGMENT_RESULT])
                .add(R.id.activity_result_container,mFragments[FRAGMENT_PERIOD])
                .add(R.id.activity_result_container,mFragments[FRAGMENT_MEDICATION])
                .hide(mFragments[FRAGMENT_PERIOD])
                .hide(mFragments[FRAGMENT_MEDICATION])
                .show(mFragments[FRAGMENT_RESULT])
                .commit();
    }

    private void switchResult(){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mFragments[mCurrentTabIndex]);
        if(!mFragments[FRAGMENT_RESULT].isAdded()){
            fragmentTransaction.add(R.id.activity_result_container,mFragments[FRAGMENT_RESULT]);
        }
        fragmentTransaction.show(mFragments[FRAGMENT_RESULT]);
        fragmentTransaction.commit();
        mCurrentTabIndex = FRAGMENT_RESULT;
    }

    private void switchPeriod(){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mFragments[mCurrentTabIndex]);
        if(!mFragments[FRAGMENT_PERIOD].isAdded()){
            fragmentTransaction.add(R.id.activity_result_container,mFragments[FRAGMENT_PERIOD]);
        }
        fragmentTransaction.show(mFragments[FRAGMENT_PERIOD]);
        fragmentTransaction.commit();
        mCurrentTabIndex = FRAGMENT_PERIOD;
    }

    private void switchMedication(){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mFragments[mCurrentTabIndex]);
        if(!mFragments[FRAGMENT_MEDICATION].isAdded()){
            fragmentTransaction.add(R.id.activity_result_container,mFragments[FRAGMENT_MEDICATION]);
        }
        fragmentTransaction.show(mFragments[FRAGMENT_MEDICATION]);
        fragmentTransaction.commit();
        mCurrentTabIndex = FRAGMENT_MEDICATION;
    }

    private void switchAddMedication(){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mFragments[mCurrentTabIndex]);
        if(!mFragments[FRAGMENT_ADDMEDICATION].isAdded()){
            fragmentTransaction.add(R.id.activity_result_container,mFragments[FRAGMENT_ADDMEDICATION]);
        }
        fragmentTransaction.show(mFragments[FRAGMENT_ADDMEDICATION]);
        fragmentTransaction.commit();
        mCurrentTabIndex = FRAGMENT_ADDMEDICATION;
    }

    private void displayAlertDialog(String message) {
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        mAlertDialog.show();
    }

    private void hideSoftInput(){
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(Rlyt_Contaiter.getWindowToken(), 0);
//        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
