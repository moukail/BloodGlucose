package com.ihealth.ciforbg.dataclass;

import java.util.Calendar;

public class Data_TB_BGResult {
    public static final int SPORTS_YES = 1;
    public static final int SPORTS_NO = 2;
    public static final int INSULIN_YES = 1;
    public static final int INSULIN_NO = 0;
    public static final int MEDICATION_YES = 1;
    public static final int MEDICATION_NO = 2;

	private String PhoneDataID; // 数据id
	private long PhoneCreateTime; // 第一次存入数据库时间
	private float TimeZone;// 时区
	private float BGValue;// 血糖值，单位：mg/dL
    private int Snacks;// 是否吃过零食（ 1：是 2：否）
	private int MTimeType;// 7点时间点（早餐前，后，午餐前，后，晚饭前，后，半夜），按顺序：1，2，3，4，5，6，7 8(新版本加一个8 代表零食后)
	private long MeasureTime;// 测量时间
	private String Note;// 备注
	private String BottleId;// 试条瓶ID
	private int Sports;// 是否运动锻炼（ 1：是 2：否）
    private int Insulin;// 是否注射胰岛素（0：no 1:yes）
    private int Medication;// 1:有 2:没有(旧版)  //新版：1：吃口服药：2：没吃口服药

    private int SportTime;//运动时间 (分钟)

    public Data_TB_BGResult(){
        this.Sports = 2;
        this.Snacks = 2;
        this.Insulin = 0;
        this.Note = "";
        this.PhoneDataID = "";
        this.MTimeType = getCurrentTimePeriod();
    }

    public String getPhoneDataID() {
        return PhoneDataID;
    }

    public void setPhoneDataID(String phoneDataID) {
        PhoneDataID = phoneDataID;
    }

    public long getPhoneCreateTime() {
        return PhoneCreateTime;
    }

    public void setPhoneCreateTime(long phoneCreateTime) {
        PhoneCreateTime = phoneCreateTime;
    }

    public float getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(float timeZone) {
        TimeZone = timeZone;
    }

    public float getBGValue() {
        return BGValue;
    }

    public void setBGValue(float BGValue) {
        this.BGValue = BGValue;
    }

    public int getMedication() {
        return Medication;
    }

    public void setMedication(int medication) {
        Medication = medication;
    }

    public int getInsulin() {
        return Insulin;
    }

    public void setInsulin(int insulin) {
        Insulin = insulin;
    }

    public int getMTimeType() {
        return MTimeType;
    }

    public void setMTimeType(int MTimeType) {
        this.MTimeType = MTimeType;
    }

    public long getMeasureTime() {
        return MeasureTime;
    }

    public void setMeasureTime(long measureTime) {
        MeasureTime = measureTime;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getBottleId() {
        return BottleId;
    }

    public void setBottleId(String bottleId) {
        BottleId = bottleId;
    }

    public int getSports() {
        return Sports;
    }

    public void setSports(int sports) {
        Sports = sports;
    }

    public int getSnacks() {
        return Snacks;
    }

    public void setSnacks(int snacks) {
        Snacks = snacks;
    }

    public int getSportTime() {
        return SportTime;
    }

    public void setSportTime(int sportTime) {
        SportTime = sportTime;
    }

    public static int getCurrentTimePeriod(){
        int period = Period.BEFORE_BREAKFAST;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour >= 5 && hour < 8){
            period = Period.BEFORE_BREAKFAST;
        } else if(hour >= 8 && hour < 10){
            period = Period.AFTER_BREAKFAST;
        } else if(hour >= 10 && hour < 12){
            period = Period.BEFORE_LUNCH;
        } else if(hour >= 12 && hour < 16){
            period = Period.AFTER_LUNCH;
        } else if(hour >= 16 && hour < 19){
            period = Period.BEFORE_DINNER;
        } else if(hour >= 19 && hour < 21){
            period = Period.AFTER_DINNER;
        } else if(hour >= 21 || hour < 5){
            period = Period.BEDTIME;
        }
        return period;
    }

    public static class Period{
        public static final int BEFORE_BREAKFAST = 1;
        public static final int AFTER_BREAKFAST = 2;
        public static final int BEFORE_LUNCH = 3;
        public static final int AFTER_LUNCH = 4;
        public static final int BEFORE_DINNER = 5;
        public static final int AFTER_DINNER = 6;
        public static final int BEDTIME = 7;
        public static final int AFTER_SNACKS = 8;
        public static final int RANDOM = 9;
    }

    public static class Unit{
        public static final int MMOL_L = 0;
        public static final int MG_L = 1;
    }
}
