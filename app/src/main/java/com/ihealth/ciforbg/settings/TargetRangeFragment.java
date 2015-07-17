package com.ihealth.ciforbg.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.dataclass.CommonEvent;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.Method;

import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;
import com.ihealth.ciforbg.utils.SharePreferenceUtil;

/**
 * Created by lynn on 15-7-1.
 */
public class TargetRangeFragment extends Fragment implements View.OnFocusChangeListener {
    private final String TAG = "TargetRangeFragment";

    public static final int PRE_LOWER_MIN = 20;
    public static final int PRE_LOWER_MAX = 124;
    public static final int PRE_UPPER_MIN = 21;
    public static final int PRE_UPPER_MAX = 125;

    public static final int POST_LOWER_MIN = 20;
    public static final int POST_LOWER_MAX = 198;
    public static final int POST_UPPER_MIN = 21;
    public static final int POST_UPPER_MAX = 199;

    private float mPreLowerMin;
    private float mPreLowerMax;
    private float mPreUpperMin;
    private float mPreUpperMax;
    private float mPostLowerMin;
    private float mPostLowerMax;
    private float mPostUpperMin;
    private float mPostUpperMax;

    private TextView Tv_Unit1, Tv_Unit2, Tv_Unit3, Tv_Unit4, Tv_NormalPre,
            Tv_NormalPost, Tv_DiabetesPre, Tv_DiabetesPost;
    private EditText Edit_PreUpper, Edit_PreLower, Edit_PostLower, Edit_PostUpper;

    private View mContentView;

    private Context mContext;

    private int mUnit;
    private SharePreferenceUtil mSharePreferenceUtil;

    public static boolean isEdit;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_target, null);
        }
        initWidget(mContentView);
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initValue();
        initView();
    }

    @Override
    public void onDestroyView() {
        ((ViewGroup) mContentView.getParent()).removeView(mContentView);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        L.i(TAG, "onHiddenChanged :" + hidden);
        if (!hidden) {
            initValue();
            initView();
        } else {
            setBGTarget();
        }
    }

    private void initWidget(View v) {
        Tv_Unit1 = (TextView) v.findViewById(R.id.fragment_range_pre_unit_lower);
        Tv_Unit2 = (TextView) v.findViewById(R.id.fragment_range_pre_unit_upper);
        Tv_Unit3 = (TextView) v.findViewById(R.id.fragment_range_post_unit_lower);
        Tv_Unit4 = (TextView) v.findViewById(R.id.fragment_range_post_unit_upper);
        Edit_PreLower = (EditText) v.findViewById(R.id.fragment_target_pre_lower);
        Edit_PreUpper = (EditText) v.findViewById(R.id.fragment_target_pre_upper);
        Edit_PostLower = (EditText) v.findViewById(R.id.fragment_target_post_lower);
        Edit_PostUpper = (EditText) v.findViewById(R.id.fragment_target_post_upper);
        Tv_NormalPre = (TextView) v.findViewById(R.id.fragment_target_normal_pre);
        Tv_NormalPost = (TextView) v.findViewById(R.id.fragment_target_normal_post);
        Tv_DiabetesPre = (TextView) v.findViewById(R.id.fragment_target_diabetes_pre);
        Tv_DiabetesPost = (TextView) v.findViewById(R.id.fragment_target_diabetes_post);

        Edit_PreLower.setOnFocusChangeListener(this);
        Edit_PreUpper.setOnFocusChangeListener(this);
        Edit_PostLower.setOnFocusChangeListener(this);
        Edit_PostUpper.setOnFocusChangeListener(this);

        Edit_PreLower.addTextChangedListener(mTextWatcher1);
        Edit_PreUpper.addTextChangedListener(mTextWatcher2);
        Edit_PostLower.addTextChangedListener(mTextWatcher3);
        Edit_PostUpper.addTextChangedListener(mTextWatcher4);
    }

    private void initValue() {
        isEdit = false;
        mSharePreferenceUtil = new SharePreferenceUtil(mContext);
        mUnit = mSharePreferenceUtil.getBgUnit();
    }

    private void initView() {
        if (mUnit == Data_TB_BGResult.Unit.MG_L) {
            this.mPreLowerMin = PRE_LOWER_MIN;
            this.mPreLowerMax = PRE_LOWER_MAX;
            this.mPreUpperMin = PRE_UPPER_MIN;
            this.mPreUpperMax = PRE_UPPER_MAX;
            this.mPostLowerMin = POST_LOWER_MIN;
            this.mPostLowerMax = POST_LOWER_MAX;
            this.mPostUpperMin = POST_UPPER_MIN;
            this.mPostUpperMax = POST_UPPER_MAX;
            setUnitmg();
        } else if (mUnit == Data_TB_BGResult.Unit.MMOL_L) {
            this.mPreLowerMin = Method.getBgmgTommlo(PRE_LOWER_MIN);
            this.mPreLowerMax = Method.getBgmgTommlo(PRE_LOWER_MAX);
            this.mPreUpperMin = Method.getBgmgTommlo(PRE_UPPER_MIN);
            this.mPreUpperMax = Method.getBgmgTommlo(PRE_UPPER_MAX);
            this.mPostLowerMin = Method.getBgmgTommlo(POST_LOWER_MIN);
            this.mPostLowerMax = Method.getBgmgTommlo(POST_LOWER_MAX);
            this.mPostUpperMin = Method.getBgmgTommlo(POST_UPPER_MIN);
            this.mPostUpperMax = Method.getBgmgTommlo(POST_UPPER_MAX);
            setUnitmmol();
        }
    }

    private void setUnitmg() {
        Edit_PreUpper.setText(MyApplication.glucose_targets_pre_2 + "");
        Edit_PreLower.setText(MyApplication.glucose_targets_pre_3 + "");
        Edit_PostLower.setText(MyApplication.glucose_targets_post_3 + "");
        Edit_PostUpper.setText(MyApplication.glucose_targets_post_2 + "");

        Edit_PreUpper.setInputType(InputType.TYPE_CLASS_NUMBER);
        Edit_PreLower.setInputType(InputType.TYPE_CLASS_NUMBER);
        Edit_PostLower.setInputType(InputType.TYPE_CLASS_NUMBER);
        Edit_PostUpper.setInputType(InputType.TYPE_CLASS_NUMBER);

        Tv_Unit1.setText(R.string.unit_mg);
        Tv_Unit2.setText(R.string.unit_mg);
        Tv_Unit3.setText(R.string.unit_mg);
        Tv_Unit4.setText(R.string.unit_mg);

        Tv_NormalPre.setText(R.string.pre_meal_normal_1);
        Tv_NormalPost.setText(R.string.post_meal_normal_1);
        Tv_DiabetesPre.setText(R.string.pre_meal_diabetes_1);
        Tv_DiabetesPost.setText(R.string.post_meal_diabetes_1);
    }

    private void setUnitmmol() {
        Edit_PreUpper.setText(Method.getBgmgTommlo(MyApplication.glucose_targets_pre_2) + "");
        Edit_PreLower.setText(Method.getBgmgTommlo(MyApplication.glucose_targets_pre_3) + "");
        Edit_PostLower.setText(Method.getBgmgTommlo(MyApplication.glucose_targets_post_3) + "");
        Edit_PostUpper.setText(Method.getBgmgTommlo(MyApplication.glucose_targets_post_2) + "");

        Edit_PreUpper.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Edit_PreLower.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Edit_PostLower.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Edit_PostUpper.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Tv_Unit1.setText(R.string.unit_mmol);
        Tv_Unit2.setText(R.string.unit_mmol);
        Tv_Unit3.setText(R.string.unit_mmol);
        Tv_Unit4.setText(R.string.unit_mmol);

        Tv_NormalPre.setText(R.string.pre_meal_normal_2);
        Tv_NormalPost.setText(R.string.post_meal_normal_2);
        Tv_DiabetesPre.setText(R.string.pre_meal_diabetes_2);
        Tv_DiabetesPost.setText(R.string.post_meal_diabetes_2);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        L.i(TAG,"onFocusChange");
        isEdit = true;
        String str = ((EditText) v).getText().toString();
        if (TextUtils.isEmpty(str)) {
            return;
        }
        float value = 0;
        if(!TextUtils.isEmpty(str)) {
            value = Float.parseFloat(str);
        }
        if(mUnit == Data_TB_BGResult.Unit.MMOL_L) {
            switch (v.getId()) {
                case R.id.fragment_target_pre_lower:
                    if (value < mPreLowerMin) {
                        Edit_PreLower.setText(mPreLowerMin + "");
                    } else if (value > Float.parseFloat(Edit_PreUpper.getText().toString())) {
                        Edit_PreLower.setText(Float.parseFloat(Edit_PreUpper.getText().toString()) - 1 + "");
                    }
                    break;
                case R.id.fragment_target_pre_upper:
                    if (value > mPreUpperMax) {
                        Edit_PreUpper.setText(mPreUpperMax + "");
                    } else if (value < Float.parseFloat(Edit_PreLower.getText().toString())) {
                        Edit_PreUpper.setText(Float.parseFloat(Edit_PreLower.getText().toString()) + 1 + "");
                    }
                    break;
                case R.id.fragment_target_post_lower:
                    if (value < mPostLowerMin) {
                        Edit_PostLower.setText(mPostLowerMin + "");
                    } else if (value > Float.parseFloat(Edit_PostUpper.getText().toString())) {
                        Edit_PostLower.setText(Float.parseFloat(Edit_PostUpper.getText().toString()) - 1 + "");
                    }
                    break;
                case R.id.fragment_target_post_upper:
                    if (value > mPostUpperMax) {
                        Edit_PostUpper.setText(mPostUpperMax + "");
                    } else if (value < Float.parseFloat(Edit_PostLower.getText().toString())) {
                        Edit_PostUpper.setText(Float.parseFloat(Edit_PostLower.getText().toString()) + 1 + "");
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.fragment_target_pre_lower:
                    if (value < mPreLowerMin) {
                        Edit_PreLower.setText(mPreLowerMin + "");
                    } else if (value > Integer.parseInt(Edit_PreUpper.getText().toString())) {
                        Edit_PreLower.setText(Integer.parseInt(Edit_PreUpper.getText().toString()) - 1 + "");
                    }
                    break;
                case R.id.fragment_target_pre_upper:
                    if (value > mPreUpperMax) {
                        Edit_PreUpper.setText(mPreUpperMax + "");
                    } else if (value < Integer.parseInt(Edit_PreLower.getText().toString())) {
                        Edit_PreUpper.setText(Integer.parseInt(Edit_PreLower.getText().toString()) + 1 + "");
                    }
                    break;
                case R.id.fragment_target_post_lower:
                    if (value < mPostLowerMin) {
                        Edit_PostLower.setText(mPostLowerMin + "");
                    } else if (value > Integer.parseInt(Edit_PostUpper.getText().toString())) {
                        Edit_PostLower.setText(Integer.parseInt(Edit_PostUpper.getText().toString()) - 1 + "");
                    }
                    break;
                case R.id.fragment_target_post_upper:
                    if (value > mPostUpperMax) {
                        Edit_PostUpper.setText(mPostUpperMax + "");
                    } else if (value < Integer.parseInt(Edit_PostLower.getText().toString())) {
                        Edit_PostUpper.setText(Integer.parseInt(Edit_PostLower.getText().toString()) + 1 + "");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private TextWatcher mTextWatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s)) {
                L.i("TextChange", s.toString());
                MeasurePresenter.glucose_targets_pre_3 = Float.parseFloat(s.toString());
            }
        }
    };
    private TextWatcher mTextWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s)) {
                L.i("TextChange", s.toString());
                MeasurePresenter.glucose_targets_pre_2 = Float.parseFloat(s.toString());
            }
        }
    };
    private TextWatcher mTextWatcher3 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s)) {
                L.i("TextChange", s.toString());
                MeasurePresenter.glucose_targets_post_3 = Float.parseFloat(s.toString());
            }
        }
    };
    private TextWatcher mTextWatcher4 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s)) {
                L.i("TextChange", s.toString());
                MeasurePresenter.glucose_targets_post_2 = Float.parseFloat(s.toString());
            }
        }
    };

    private boolean setBGTarget(){
        if(!isEdit){
            return false;
        }
        try {
            float preLower = Float.parseFloat(Edit_PreLower.getText().toString());
            float preUpper = Float.parseFloat(Edit_PreUpper.getText().toString());
            float postLower = Float.parseFloat(Edit_PostLower.getText().toString());
            float postUpper = Float.parseFloat(Edit_PostUpper.getText().toString());
            if(mUnit == Data_TB_BGResult.Unit.MG_L){
                MyApplication.glucose_targets_pre_2 = (int)preUpper;
                MyApplication.glucose_targets_pre_3 = (int)preLower;
                MyApplication.glucose_targets_post_2 = (int)postUpper;
                MyApplication.glucose_targets_post_3 = (int)postLower;
            } else {
                MyApplication.glucose_targets_pre_2 = (int)Method.getBgmmloTomg(preUpper);
                MyApplication.glucose_targets_pre_3 = (int)Method.getBgmmloTomg(preLower);
                MyApplication.glucose_targets_post_2 = (int)Method.getBgmmloTomg(postUpper);
                MyApplication.glucose_targets_post_3 = (int)Method.getBgmmloTomg(postLower);
            }
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
