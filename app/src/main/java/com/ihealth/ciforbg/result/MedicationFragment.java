package com.ihealth.ciforbg.result;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.Method;
import com.ihealth.ciforbg.utils.Pharmacy;
import com.ihealth.ciforbg.view.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lynn on 15-7-3.
 */
public class MedicationFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "MedicationFragment";
    private View mContentView;

    private MyListView Lv_Insulin, Lv_OralMeds;
    private EditText Edit_Search;
    private TextView Tv_Back;
    private ImageView Iv_AddMeds ,Iv_DelSearchText;

    private MedicineAdapter mOralMedsAdapter;
    private MedicineAdapter mInsulinAdapter;

    private ArrayList<Data_TB_Medicine> mOralMeds;
    private ArrayList<Data_TB_Medicine> mInsilins;

    private Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_medicine, null);
            initWidget();
        }
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initValue();
            initView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_medicine_back:
                MeasurePresenter.getInstance().switchResult();
                break;
            case R.id.fragment_medicine_add:
                MeasurePresenter.getInstance().switchAddMedication();
                break;
            case R.id.fragment_medicine_search_del:
                Edit_Search.setText("");
                break;
            default:
                break;
        }
    }

    private void initWidget() {
        Lv_Insulin = (MyListView) mContentView.findViewById(R.id.fragment_medicine_insulin_list);
        Lv_OralMeds = (MyListView) mContentView.findViewById(R.id.fragment_medicine_oral_list);
        Edit_Search = (EditText) mContentView.findViewById(R.id.fragment_medicine_search_box);
        Tv_Back = (TextView) mContentView.findViewById(R.id.fragment_medicine_back);
        Iv_AddMeds = (ImageView) mContentView.findViewById(R.id.fragment_medicine_add);
        Iv_DelSearchText = (ImageView) mContentView.findViewById(R.id.fragment_medicine_search_del);

        Iv_AddMeds.setOnClickListener(this);
        Tv_Back.setOnClickListener(this);
        Iv_DelSearchText.setOnClickListener(this);
        Edit_Search.addTextChangedListener(mTextWatcher);
        Lv_OralMeds.setOnItemClickListener(mOralItemClickListener);
        Lv_Insulin.setOnItemClickListener(mInsulinIntemClickListener);
    }

    private void initValue() {
        mOralMeds = Pharmacy.getAllOralMedicine(mContext);
        mInsilins = Pharmacy.getAllInsulin(mContext);
        initAdapter();
    }

    private void initAdapter(){
        mOralMedsAdapter = new MedicineAdapter(mContext, mOralMeds, R.layout.listitem_medicine);
        mInsulinAdapter = new MedicineAdapter(mContext, mInsilins, R.layout.listitem_medicine);
    }

    private void initView() {
        L.i(TAG, "initView");
        Lv_OralMeds.setAdapter(mOralMedsAdapter);
        Lv_Insulin.setAdapter(mInsulinAdapter);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            L.i(TAG, "afterTextChanged " + s.toString());
            if(!TextUtils.isEmpty(s.toString())) {
                ArrayList<Data_TB_Medicine> oralMedsBuffer = Pharmacy.getAllOralMedicine(mContext);
                ArrayList<Data_TB_Medicine> insulinBuffer = Pharmacy.getAllInsulin(mContext);

                mOralMeds.clear();
                mInsilins.clear();
                for (Data_TB_Medicine medicine : oralMedsBuffer) {
                    if (Method.isSimilar(s.toString(), medicine.getName())) {
                        mOralMeds.add(medicine);
                    }
                }
                for (Data_TB_Medicine medicine : insulinBuffer) {
                    if (Method.isSimilar(s.toString(), medicine.getName())) {
                        mInsilins.add(medicine);
                    }
                }
            } else {
                mOralMeds.clear();
                mInsilins.clear();
                mOralMeds.addAll(Pharmacy.getAllOralMedicine(mContext));
                mInsilins.addAll(Pharmacy.getAllInsulin(mContext));
            }
            mInsulinAdapter.notifyDataSetChanged();
            mOralMedsAdapter.notifyDataSetChanged();
        }
    };

    private AdapterView.OnItemClickListener mOralItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(MeasurePresenter.getInstance().mMeficines.contains(mOralMeds.get(position))){
                MeasurePresenter.getInstance().mMeficines.remove(mOralMeds.get(position));
            } else {
                MeasurePresenter.getInstance().mMeficines.add(mOralMeds.get(position));
            }
            mOralMedsAdapter.notifyDataSetChanged();
            MeasurePresenter.getInstance().switchResult();

        }
    };

    private AdapterView.OnItemClickListener mInsulinIntemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(MeasurePresenter.getInstance().mMeficines.contains(mInsilins.get(position))){
                MeasurePresenter.getInstance().mMeficines.remove(mInsilins.get(position));
            } else {
                MeasurePresenter.getInstance().mMeficines.add(mInsilins.get(position));
            }
            mInsulinAdapter.notifyDataSetChanged();
            MeasurePresenter.getInstance().switchResult();
        }
    };

    private static class MedicineAdapter extends BaseAdapter{

        private Context mContext;
        private List<Data_TB_Medicine> medicines;
        private int id;
        private LayoutInflater mInflater;

        public MedicineAdapter(Context context, ArrayList<Data_TB_Medicine> medicines, int id){
            this.mContext = context;
            this.medicines = medicines;
            this.id = id;
            this.mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return medicines.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listitem_medicine, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Data_TB_Medicine medicine = medicines.get(position);
            if(!TextUtils.isEmpty(medicine.getNickName())){
                holder.Tv_Name.setText(medicine.getName() + "(" +medicine.getNickName() + ")");
            } else {
                holder.Tv_Name.setText(medicine.getName());
            }

            if (MeasurePresenter.getInstance().mMeficines.contains(medicine)) {
                holder.Iv_Select.setVisibility(View.VISIBLE);
            } else {
                holder.Iv_Select.setVisibility(View.GONE);
            }
            if(position == getCount() - 1){
                holder.View_Divider.setVisibility(View.GONE);
            } else {
                holder.View_Divider.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        private class ViewHolder{
            private TextView Tv_Name;
            private ImageView Iv_Select;
            private View View_Divider;
            public ViewHolder(View v){
                this.Tv_Name = (TextView)v.findViewById(R.id.listitem_medicine_name);
                this.Iv_Select = (ImageView)v.findViewById(R.id.listitem_medicine_sel);
                this.View_Divider = v.findViewById(R.id.listitem_medicine_divider);
            }
        }
    }
}
