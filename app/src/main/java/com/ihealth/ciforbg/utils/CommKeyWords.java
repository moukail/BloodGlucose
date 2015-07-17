package com.ihealth.ciforbg.utils;

/**
 * Created by lynn on 15-6-5.
 */
public class CommKeyWords {

    public static final String clientID = "2a8387e3f4e94407a3a767a72dfd52ea";
    public static final String clientSecret = "fd5e845c47944a818bc511fb7edb0a77";

    /**
     * 跳转来源
     */
    public static final String FROM_ACTIVITY = "from_activity";
    public static final int FROM_CI = 0;
    /**
     *
     */
    public static final String CI_CALLBACK = "CallBack";
    public static final String CI_URLPATH = "urlPath";
    public static final String CI_URLCLASS = "urlClass";
    public static final String CI_ACCOUNTNAMR = "AccountName";
    public static final String CI_SUCCESS = "success";
    public static final String CI_ERROR = "error";


    public static final String CI_BG = "BG";
    public static final String CI_BGVALUE = "BGValue";

    public static final String CI_HS = "HS";
    public static final String CI_WEIGHT = "Weight";

    public static final String CI_SYSTOLIC = "Systolic";
    public static final String CI_DIASTOLIC = "Diastolic";
    public static final String CI_MEASUREMENT = "measurement";
    public static final String CI_BP = "BP";
    public static final String CI_HR = "HR";
    public static final String CI_MEASUREDATE = "MeasureDate";
    public static final String CI_MEASURETIME = "MeasureTime ";
    public static final String CI_TIMEZONE = "TimeZone";
    public static final String CI_MEASURETYPE = "MeasureType";
    public static final String CI_ARRHYTHMIA = "Arrhythmia";

    public static final String CI_DEVICE = "device";
    public static final String CI_DEVICEID = "DeviceID";
    public static final String CI_DEVICEMODEL = "DeviceModel";
    public static final String CI_DEVICEVERSION = "DeviceVersion";
    public static final String CI_DEVICEBATTERY = "DeviceBattery";

    public static int CI_ERROR_UNKNOWN = 0;
    public static int CI_DEVICE_NOT_FOUND = 1;
    public static int CI_TIMEOUT = 0;
    public class Device {
        public static final String HS4S = "HS4S";
        public static final String HS4 = "HS4";
    }
}
