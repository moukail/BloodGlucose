package com.ihealth.ciforbg.result;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_BGResult;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.Method;
import com.ihealth.ciforbg.utils.ResultPresenter;
import com.ihealth.ciforbg.utils.SharePreferenceUtil;
import com.ihealth.ciforbg.view.MyListView;
import com.ihealth.ciforbg.view.switchbutton.TimerPicker.TimePicker;


/**
 * Created by lynn on 15-7-3.
 */
public class ResultFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "ResultFragment";

    private View mContentView;

    private RelativeLayout Rlyt_TimePeriod, Rlyt_BgResult, Rlyt_AddMedication, Rlyt_Activity;
    private LinearLayout Llyt_TookOral, Llyt_TookInsulin, Llyt_TookExercised;
    private ImageView Iv_TookOral, Iv_TookInsulin, Iv_TookExercised, Iv_Period;
    private TextView Tv_TookOral, Tv_TookInsulin, Tv_TookExercised, Tv_BgResult, Tv_BgUnit, Tv_Date, Tv_Time, Tv_Period, Tv_Activity, Tv_Cancel, Tv_NextStep;
    private EditText Edtv_Note;
    private MyListView Lv_Mecication;
    private PopupWindow mPopWindow;

    private MedicationAdapter mMedicationAdapter;

    private String[] mPeriodTexts;
    private int[] mPeriodImgs;

    private SharePreferenceUtil mSharePreferenceUtil;

    private Context mContext;

    private int mMaxBGResultValue = 600;//血糖值 >=600 =< 0不显示
    private int mMinBGResultValue = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_result, null);
            initWidget();
        }
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        L.i(TAG, "onStart");
        initValue();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i(TAG, "onResume");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_result_time_period:
                ResultPresenter.getInstance().switchTimePeriod();
                break;
            case R.id.fragment_result_took_oral:
                if ((ResultPresenter.getInstance().getBgResult().getMedication() == Data_TB_BGResult.MEDICATION_YES) &&
                        !MeasurePresenter.hasOralMeds()) {
                    unTookOral();
                    MeasurePresenter.getInstance().mBgResult.setMedication(Data_TB_BGResult.MEDICATION_NO);
                } else {
                    tookOral();
                    MeasurePresenter.getInstance().mBgResult.setMedication(Data_TB_BGResult.MEDICATION_YES);
                }
                break;
            case R.id.fragment_result_took_insulin:
                if ((ResultPresenter.getInstance().getBgResult().getInsulin() == Data_TB_BGResult.INSULIN_YES) &&
                        !MeasurePresenter.hasInsulin()) {
                    unTookInsulin();
                    MeasurePresenter.getInstance().mBgResult.setInsulin(Data_TB_BGResult.INSULIN_NO);
                } else {
                    tookInsulin();
                    MeasurePresenter.getInstance().mBgResult.setInsulin(Data_TB_BGResult.INSULIN_YES);
                }
                break;
            case R.id.fragment_result_took_exercised:
                if ((MeasurePresenter.getInstance().mBgResult.getSports() == Data_TB_BGResult.SPORTS_YES) &&
                        (MeasurePresenter.getInstance().mBgResult.getSportTime() == 0)) {
                    unTookExercised();
                    MeasurePresenter.getInstance().mBgResult.setSports(Data_TB_BGResult.SPORTS_NO);
                } else {
                    tookExercised();
                    MeasurePresenter.getInstance().mBgResult.setSports(Data_TB_BGResult.SPORTS_YES);
                }
                break;
            case R.id.fragment_result_add_medication:
                ResultPresenter.getInstance().switchMedication();
                break;
            case R.id.fragment_result_activity_ly:
                displayTimerPicker();
                break;
            case R.id.fragment_result_cancel:
                ResultPresenter.getInstance().dismissResultWindiow();
                break;
            case R.id.fragment_result_next:
                MeasurePresenter.getInstance().nextApplication();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initView();
        }
    }

    private void initWidget() {
        Tv_BgResult = (TextView) mContentView.findViewById(R.id.fragment_result_bgresult);
        Tv_BgUnit = (TextView) mContentView.findViewById(R.id.fragment_result_bgunit);
        Tv_Date = (TextView) mContentView.findViewById(R.id.fragment_result_date);
        Tv_Time = (TextView) mContentView.findViewById(R.id.fragment_result_time);
        Tv_TookOral = (TextView) mContentView.findViewById(R.id.fragment_result_took_oral_txt);
        Tv_TookInsulin = (TextView) mContentView.findViewById(R.id.fragment_result_fragment_result_took_insulin_txt);
        Tv_TookExercised = (TextView) mContentView.findViewById(R.id.fragment_result_took_exercised_txt);
        Tv_Period = (TextView) mContentView.findViewById(R.id.fragment_result_time_period_txt);
        Tv_Activity = (TextView) mContentView.findViewById(R.id.fragment_result_activity_txt);
        Tv_Cancel = (TextView) mContentView.findViewById(R.id.fragment_result_cancel);
        Tv_NextStep = (TextView) mContentView.findViewById(R.id.fragment_result_next);

        Iv_TookOral = (ImageView) mContentView.findViewById(R.id.fragment_result_took_oral_img);
        Iv_TookInsulin = (ImageView) mContentView.findViewById(R.id.fragment_result_fragment_result_took_insulin_img);
        Iv_TookExercised = (ImageView) mContentView.findViewById(R.id.fragment_result_took_exercised_img);
        Iv_Period = (ImageView) mContentView.findViewById(R.id.fragment_result_time_period_icon);

        Rlyt_TimePeriod = (RelativeLayout) mContentView.findViewById(R.id.fragment_result_time_period);
        Rlyt_BgResult = (RelativeLayout) mContentView.findViewById(R.id.fragment_result_bgresult_ly);
        Rlyt_AddMedication = (RelativeLayout) mContentView.findViewById(R.id.fragment_result_add_medication);
        Rlyt_Activity = (RelativeLayout) mContentView.findViewById(R.id.fragment_result_activity_ly);

        Llyt_TookOral = (LinearLayout) mContentView.findViewById(R.id.fragment_result_took_oral);
        Llyt_TookInsulin = (LinearLayout) mContentView.findViewById(R.id.fragment_result_took_insulin);
        Llyt_TookExercised = (LinearLayout) mContentView.findViewById(R.id.fragment_result_took_exercised);

        Edtv_Note = (EditText) mContentView.findViewById(R.id.fragment_result_notes);

        Lv_Mecication = (MyListView) mContentView.findViewById(R.id.fragment_result_medication);

        Rlyt_TimePeriod.setOnClickListener(this);
        Llyt_TookOral.setOnClickListener(this);
        Llyt_TookExercised.setOnClickListener(this);
        Llyt_TookInsulin.setOnClickListener(this);
        Rlyt_AddMedication.setOnClickListener(this);
        Rlyt_Activity.setOnClickListener(this);
        Tv_Cancel.setOnClickListener(this);
        Tv_NextStep.setOnClickListener(this);
    }

    private void initValue() {
        mPeriodTexts = getResources().getStringArray(R.array.period_txt);
        mPeriodImgs = new int[mPeriodTexts.length];
        mPeriodImgs[0] = R.drawable.before_breakfast;
        mPeriodImgs[1] = R.drawable.after_breakfast;
        mPeriodImgs[2] = R.drawable.before_lunch;
        mPeriodImgs[3] = R.drawable.after_lunch;
        mPeriodImgs[4] = R.drawable.before_dinner;
        mPeriodImgs[5] = R.drawable.after_dinner;
        mPeriodImgs[6] = R.drawable.bedtime;
        mPeriodImgs[7] = R.drawable.after_snacks;

        mMedicationAdapter = new MedicationAdapter();

        mSharePreferenceUtil = new SharePreferenceUtil(mContext);
    }

    private void initView() {
        if (MeasurePresenter.getInstance().mBgResult.getBGValue() > mMinBGResultValue && MeasurePresenter.getInstance().mBgResult.getBGValue() < mMaxBGResultValue) {
            if (mSharePreferenceUtil.getBgUnit() == Data_TB_BGResult.Unit.MG_L) {
                Tv_BgResult.setText((int) MeasurePresenter.getInstance().mBgResult.getBGValue() + "");
                Tv_BgUnit.setText(getResources().getString(R.string.unit_mg));
            } else if (mSharePreferenceUtil.getBgUnit() == Data_TB_BGResult.Unit.MMOL_L) {
                Tv_BgResult.setText(Method.getBgmgTommlo(MeasurePresenter.getInstance().mBgResult.getBGValue()) + "");
                Tv_BgUnit.setText(getResources().getString(R.string.unit_mmol));
            }
        }

        Tv_Date.setText(Method.getDateStr(MeasurePresenter.getInstance().mBgResult.getMeasureTime() * 1000));
        Tv_Time.setText(Method.getTimeStr(MeasurePresenter.getInstance().mBgResult.getMeasureTime() * 1000));

        Tv_Period.setText(mPeriodTexts[MeasurePresenter.getInstance().mBgResult.getMTimeType() - 1]);
        Iv_Period.setImageResource(mPeriodImgs[MeasurePresenter.getInstance().mBgResult.getMTimeType() - 1]);

        setResultColor();


        if (MeasurePresenter.mMeficines.size() > 0) {
            Lv_Mecication.setVisibility(View.VISIBLE);
            mMedicationAdapter.notifyDataSetChanged();
            Lv_Mecication.setAdapter(mMedicationAdapter);

            if (MeasurePresenter.hasInsulin()) {
                tookInsulin();
            }
            if (MeasurePresenter.hasOralMeds()) {
                tookOral();
            }
        }
    }

    private void tookOral() {
        Iv_TookOral.setImageResource(R.drawable.medicine_yes);
        Tv_TookOral.setTextColor(getResources().getColor(R.color.blue));
    }

    private void unTookOral() {
        Iv_TookOral.setImageResource(R.drawable.medicine_no);
        Tv_TookOral.setTextColor(getResources().getColor(R.color.result_text_color_1));
    }

    private void tookInsulin() {
        Iv_TookInsulin.setImageResource(R.drawable.insulin_yes);
        Tv_TookInsulin.setTextColor(getResources().getColor(R.color.blue));
    }

    private void unTookInsulin() {
        Iv_TookInsulin.setImageResource(R.drawable.insulin_no);
        Tv_TookInsulin.setTextColor(getResources().getColor(R.color.result_text_color_1));
    }

    private void tookExercised() {
        Iv_TookExercised.setImageResource(R.drawable.sport_yes);
        Tv_TookExercised.setTextColor(getResources().getColor(R.color.blue));
    }

    private void unTookExercised() {
        Iv_TookExercised.setImageResource(R.drawable.sport_no);
        Tv_TookExercised.setTextColor(getResources().getColor(R.color.result_text_color_1));
    }

    private void setResultColor() {
        int period = MeasurePresenter.getInstance().mBgResult.getMTimeType();
        if (period == Data_TB_BGResult.Period.AFTER_BREAKFAST ||
                period == Data_TB_BGResult.Period.AFTER_LUNCH ||
                period == Data_TB_BGResult.Period.AFTER_DINNER ||
                period == Data_TB_BGResult.Period.AFTER_SNACKS) {
            if ((MeasurePresenter.getInstance().mBgResult.getBGValue() >= MyApplication.glucose_targets_post_1) &&
                    (MeasurePresenter.getInstance().mBgResult.getBGValue() < mMaxBGResultValue)) {
                // 红色
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_3);
            } else if (MeasurePresenter.getInstance().mBgResult.getBGValue() >= MyApplication.glucose_targets_post_2
                    && (MeasurePresenter.getInstance().mBgResult.getBGValue() < MyApplication.glucose_targets_post_1)) {
                // 黄色
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_2);
            } else if ((MeasurePresenter.getInstance().mBgResult.getBGValue() < MyApplication.glucose_targets_post_2) &&
                    (MeasurePresenter.getInstance().mBgResult.getBGValue() > mMinBGResultValue)) {
                // 黄色
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_1);
            } else {
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_4);
            }
        } else {
            if ((MeasurePresenter.getInstance().mBgResult.getBGValue() >= MyApplication.glucose_targets_pre_1) &&
                    (MeasurePresenter.getInstance().mBgResult.getBGValue() < mMaxBGResultValue)) {
                // 红色
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_3);
            } else if (MeasurePresenter.getInstance().mBgResult.getBGValue() >= MyApplication.glucose_targets_pre_2
                    && MeasurePresenter.getInstance().mBgResult.getBGValue() < MyApplication.glucose_targets_pre_1) {
                // 黄色
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_2);
            } else if ((MeasurePresenter.getInstance().mBgResult.getBGValue() < MyApplication.glucose_targets_pre_2) &&
                    (MeasurePresenter.getInstance().mBgResult.getBGValue() > mMinBGResultValue)) {
                // 绿色
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_1);
            } else {
                Rlyt_BgResult.setBackgroundResource(R.drawable.result_circle_4);
            }
        }
    }

    private class MedicationAdapter extends BaseAdapter {
        private final String TAG = "MedicationAdapter";
        private LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            int count = MeasurePresenter.mMeficines.size();
            L.i(TAG, "getCount : " + count);
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_medicine_select, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.Tv_MedicationName.setText(MeasurePresenter.mMeficines.get(position).getName());
            if (MeasurePresenter.mMeficines.get(position).getMedicineValue() > 0) {
                holder.Edit_Units.setText(MeasurePresenter.mMeficines.get(position).getMedicineValue() + "");
            }
            holder.Edit_Units.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        holder.Tv_Units.setVisibility(View.VISIBLE);
                    } else {
                        holder.Tv_Units.setVisibility(View.GONE);
                    }
                }
            });
            holder.Edit_Units.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        String str = ((EditText)v).getText().toString();
                        if(!TextUtils.isEmpty(str)){
                            MeasurePresenter.mMeficines.get(position).setMedicineValue(Float.parseFloat(str));
                        }
                    }
                }
            });
            holder.Rlyt_DelMedications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MeasurePresenter.mMeficines.remove(position);
                    if (MeasurePresenter.mMeficines.size() == 0) {
                        Lv_Mecication.setVisibility(View.GONE);
                    } else {
                        notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }

        private class ViewHolder {
            public TextView Tv_MedicationName, Tv_Units;
            public EditText Edit_Units;
            public RelativeLayout Rlyt_DelMedications;

            public ViewHolder(View v) {
                Tv_MedicationName = (TextView) v.findViewById(R.id.listitem_medicine_name_txt);
                Tv_Units = (TextView) v.findViewById(R.id.listitem_medicine_unit);
                Edit_Units = (EditText) v.findViewById(R.id.listitem_medicine_dosage);
                Rlyt_DelMedications = (RelativeLayout) v.findViewById(R.id.listitem_medicine_remove);
            }
        }
    }

    private void displayTimerPicker() {
        View popView = LayoutInflater.from(mContext).inflate(R.layout.ppw_timepicker, null);
        mPopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TimePicker timePicker = (TimePicker) popView.findViewById(R.id.ppw_timepicker);
        TextView tv_Cancel = (TextView) popView.findViewById(R.id.ppw_timepicker_cancel);
        TextView tv_Done = (TextView) popView.findViewById(R.id.ppw_timepicker_done);
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        tv_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int activityHour = timePicker.getCurrentHour(), activityMinute = timePicker.getCurrentMinute();
                if (activityMinute != 0 || activityHour != 0) {
                    Tv_Activity.setText(activityHour + " hour " + activityMinute + " minute");
                    MeasurePresenter.getInstance().mBgResult.setSportTime(activityHour * 60 + activityMinute);
                    tookExercised();
                } else {
                    Tv_Activity.setText("");
                    MeasurePresenter.getInstance().mBgResult.setSportTime(0);
                }
                mPopWindow.dismiss();
            }
        });

        popView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        popView.setBackgroundColor(Color.parseColor("#000000"));
        popView.getBackground().setAlpha(100);

        mPopWindow.setAnimationStyle(R.style.MenuStyle);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.showAtLocation(mContentView.findViewById(R.id.fragment_result_content), Gravity.BOTTOM, popView.getLayoutParams().width, popView.getLayoutParams().height);
    }
}
