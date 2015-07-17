package com.ihealth.ciforbg.database;

/**
 * Created by lynn on 15-7-6.
 */
public class Constants_DB {
    public static final String TABLE_TB_USERINFO= "TB_UserInfo";
    public static final String USERINFO_USERNAME = "username";
    public static final String USERINFO_BOTTLEID = "BottleId";

    public static final String TABLE_TB_BOTTLEINFO = "TB_BottleInfo";
    public static final String BOTTLEINFO_BOTTLEID = "bottle_id";
    public static final String BOTTLEINFO_OPENDATE = "OpenDate";
    public static final String BOTTLEINFO_TIMEZONE = "timezone";
    public static final String BOTTLEINFO_TS = "ts";
    public static final String BOTTLEINFO_STRIPLEFT = "StripLeft";
    public static final String BOTTLEINFO_OVERTIME = "OverTime";
    public static final String BOTTLEINFO_STRIPCODE = "StripCode";
    public static final String BOTTLEINFO_USERNAME = "UserName";

    //药物
    public static final String TABLE_TB_MEDICINE = "TB_Medicine";
    public static final String MEDICINE_CHANGETYPE = "ChangeType";
    public static final String MEDICINE_PHONECREATTIME = "PhoneCreatTime";
    public static final String MEDICINE_LASTCHANGETIME = "LastChangeTime";
    public static final String MEDICINE_ISRECENTLYUSED = "IsRecentlyUsed";
    public static final String MEDICINE_NAME = "Name";
    public static final String MEDICINE_PHONEDATAID = "PhoneDataID";
    public static final String MEDICINE_IHEALTHID = "iHealthID";
    public static final String MEDICINE_TYPE = "type";
    public static final String MEDICINE_DATETYPE = "DateType";
    public static final String MEDICINE_NICKNAME = "NickName";
    public static final String MEDICINE_MEDICINE_VALUE = "medicineValue";

    //用户药物
    public static final String TABLE_TB_MEDICATIONSELECT = "TB_MedicationSelect";
    public static final String MEDICATIONSELECT_CHANGETYPE = "ChangeType";
    public static final String MEDICATIONSELECT_PHONECREATTIME = "PhoneCreatTime";
    public static final String MEDICATIONSELECT_LASTCHANGETIME = "LastChangeTime";
    public static final String MEDICATIONSELECT_ISRECENTLYUSED = "IsRecentlyUsed";
    public static final String MEDICATIONSELECT_NAME = "Name";
    public static final String MEDICATIONSELECT_PHONEDATAID = "PhoneDataID";
    public static final String MEDICATIONSELECT_IHEALTHID = "iHealthID";
    public static final String MEDICATIONSELECT_TYPE = "type";
    public static final String MEDICATIONSELECT_DATETYPE = "DateType";
    public static final String MEDICATIONSELECT_NICKNAME = "NickName";
    public static final String MEDICATIONSELECT_MEDICINE_VALUE = "medicineValue";

    // 血糖目标
    public static final String TABLE_TB_BGPLAN = "Data_DB_BGPlan";
    public static final String BGPLAN_PRE_1 = "pre_1";
    public static final String BGPLAN_PRE_2 = "pre_2";
    public static final String BGPLAN_PRE_3 = "pre_3";
    public static final String BGPLAN_POST_1 = "post_1";
    public static final String BGPLAN_POST_2 = "post_2";
    public static final String BGPLAN_POST_3 = "post_3";
}
