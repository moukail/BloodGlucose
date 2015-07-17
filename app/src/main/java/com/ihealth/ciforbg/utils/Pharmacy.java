package com.ihealth.ciforbg.utils;

import android.content.Context;
import android.database.Cursor;

import com.ihealth.ciforbg.database.Constants_DB;
import com.ihealth.ciforbg.database.DataBaseTools;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;

import java.util.ArrayList;

/**
 * Created by lynn on 15-7-8.
 */
public class Pharmacy {

    public static ArrayList<Data_TB_Medicine> getAllOralMedicine(Context context){
        L.i("getAllOralMedicine");
        ArrayList<Data_TB_Medicine> medicines = new ArrayList<>();
        DataBaseTools tools = new DataBaseTools(context);
//        Cursor cursor = tools.selectData(Constants_DB.TABLE_TB_MEDICINE, null, Constants_DB.MEDICINE_TYPE + " = " + Data_TB_Medicine.Type.ORAL_MEDS);
        Cursor cursor = tools.selectData(Constants_DB.TABLE_TB_MEDICINE, null, Constants_DB.MEDICINE_TYPE + " = " + Data_TB_Medicine.Type.ORAL_MEDS, null, null, null, Constants_DB.MEDICINE_NAME + " ASC");
        if(cursor != null){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                Data_TB_Medicine medicine = new Data_TB_Medicine();
                medicine.setChangeType(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_CHANGETYPE)));
                medicine.setCreateTime(cursor.getLong(cursor.getColumnIndex(Constants_DB.MEDICINE_PHONECREATTIME)));
                medicine.setiHealthID(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_IHEALTHID)));
                medicine.setIsRecentlyUsed(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_ISRECENTLYUSED)));
                medicine.setLastChangeTime(cursor.getLong(cursor.getColumnIndex(Constants_DB.MEDICINE_LASTCHANGETIME)));
                medicine.setName(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_NAME)));
                medicine.setNickName(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_NICKNAME)));
                medicine.setPhoneDataID(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_PHONEDATAID)));
                medicine.setType(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_TYPE)));
                medicine.setMedicineValue(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_MEDICINE_VALUE)));
                medicines.add(medicine);
            }
            cursor.close();
        }
        return medicines;
    }

    public static ArrayList<Data_TB_Medicine> getAllInsulin(Context context){
        L.i("getAllInsulin");
        ArrayList<Data_TB_Medicine> medicines = new ArrayList<>();
        DataBaseTools tools = new DataBaseTools(context);
        Cursor cursor = tools.selectData(Constants_DB.TABLE_TB_MEDICINE, null, Constants_DB.MEDICINE_TYPE + " = " + Data_TB_Medicine.Type.INSULIN);
        if(cursor != null){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                Data_TB_Medicine medicine = new Data_TB_Medicine();
                medicine.setChangeType(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_CHANGETYPE)));
                medicine.setCreateTime(cursor.getLong(cursor.getColumnIndex(Constants_DB.MEDICINE_PHONECREATTIME)));
                medicine.setiHealthID(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_IHEALTHID)));
                medicine.setIsRecentlyUsed(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_ISRECENTLYUSED)));
                medicine.setLastChangeTime(cursor.getLong(cursor.getColumnIndex(Constants_DB.MEDICINE_LASTCHANGETIME)));
                medicine.setName(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_NAME)));
                medicine.setNickName(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_NICKNAME)));
                medicine.setPhoneDataID(cursor.getString(cursor.getColumnIndex(Constants_DB.MEDICINE_PHONEDATAID)));
                medicine.setType(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_TYPE)));
                medicine.setMedicineValue(cursor.getInt(cursor.getColumnIndex(Constants_DB.MEDICINE_MEDICINE_VALUE)));
                medicines.add(medicine);
            }
            cursor.close();
        }
        return medicines;
    }

    public static boolean addMedication(Context context, Data_TB_Medicine medicine){
        L.i("getAllInsulin");
        DataBaseTools tools = new DataBaseTools(context);
        return tools.addData(Constants_DB.TABLE_TB_MEDICINE,medicine);
    }
}
