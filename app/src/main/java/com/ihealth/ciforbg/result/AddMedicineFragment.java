package com.ihealth.ciforbg.result;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.ResultPresenter;

/**
 * Created by lynn on 15-7-9.
 */
public class AddMedicineFragment extends Fragment implements View.OnClickListener {
    private View mContentView;

    private LinearLayout Llyt_OralMeds, Llyt_Insulin;
    private ImageView Iv_OralMedsSel, Iv_InsulinSel;
    private TextView Tv_Cancel, Tv_Done;
    private EditText Edit_MedicineName;

    private Context mContext;

    private Data_TB_Medicine mMedicine;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_addmedicine, null);
            initWidget();
            initValue();
        }
        return mContentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_addmedicine_oral:
                mMedicine.setType(Data_TB_Medicine.Type.ORAL_MEDS);
                selectOralMeds();
                break;
            case R.id.fragment_addmedicine_insulin:
                mMedicine.setType(Data_TB_Medicine.Type.INSULIN);
                selectInsulin();
                break;
            case R.id.fragment_addmedicine_cancel:
                ResultPresenter.getInstance().switchMedication();
                break;
            case R.id.fragment_addmedicine_done:
                mMedicine.setName(Edit_MedicineName.getText().toString());
                ResultPresenter.getInstance().addMedication(mMedicine);
                break;
            default:
                break;
        }
    }

    private void initWidget() {
        Llyt_OralMeds = (LinearLayout) mContentView.findViewById(R.id.fragment_addmedicine_oral);
        Llyt_Insulin = (LinearLayout) mContentView.findViewById(R.id.fragment_addmedicine_insulin);
        Iv_OralMedsSel = (ImageView) mContentView.findViewById(R.id.fragment_addmedicine_oral_sel);
        Iv_InsulinSel = (ImageView) mContentView.findViewById(R.id.fragment_addmedicine_insulin_sel);
        Tv_Cancel = (TextView) mContentView.findViewById(R.id.fragment_addmedicine_cancel);
        Tv_Done = (TextView) mContentView.findViewById(R.id.fragment_addmedicine_done);
        Edit_MedicineName = (EditText) mContentView.findViewById(R.id.fragment_addmedicine_name);

        Tv_Cancel.setOnClickListener(this);
        Tv_Done.setOnClickListener(this);
        Llyt_OralMeds.setOnClickListener(this);
        Llyt_Insulin.setOnClickListener(this);
    }

    private void initValue() {
        mMedicine = new Data_TB_Medicine();
        mMedicine.setType(Data_TB_Medicine.Type.ORAL_MEDS);
    }

    private void initView() {
        if (mMedicine.getType() == Data_TB_Medicine.Type.ORAL_MEDS) {
            selectOralMeds();
        } else {
            selectInsulin();
        }
    }

    private void selectOralMeds() {
        Iv_OralMedsSel.setVisibility(View.VISIBLE);
        Iv_InsulinSel.setVisibility(View.INVISIBLE);
    }

    private void selectInsulin() {
        Iv_OralMedsSel.setVisibility(View.INVISIBLE);
        Iv_InsulinSel.setVisibility(View.VISIBLE);
    }
}
