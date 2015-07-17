package com.ihealth.ciforbg.dataclass;

import java.io.Serializable;

public class Data_TB_Medicine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phoneDataID;
	private String name;
	private String nickName;
	private int type;//1:口服药；2：胰岛素要
	private int isRecentlyUsed;//最近是否用过:1:用过；0：没用过
	private long createTime;
	private int changeType;//1:保存；2：删除
	private long lastChangeTime;
	private String iHealthID;//用户名
	private int DateType;//是否是时间分类标签 1：是   2：不是
	private float medicineValue;//药量
	
	private String ts;//药物被添加的时间，用来区分重复药品
	public Data_TB_Medicine(){
		
	}
	
	public Data_TB_Medicine(String phoneDataID, String name, String nickName, int type, int isRecentlyUsed, long createTime, int changeType, long lastChangeTime, String iHealthID,int medicineValue, String ts) {
		super();
		if(phoneDataID==null||phoneDataID==""){
			phoneDataID="";
		}
		this.phoneDataID = phoneDataID;
		if(name==null){
			name="";
		}
		this.name = name;
		if(nickName==null){
			nickName="";
		}
		this.nickName = nickName;
		this.type = type;
		this.isRecentlyUsed = isRecentlyUsed;
		this.createTime = createTime;
		this.changeType = changeType;
		this.lastChangeTime = lastChangeTime;
		this.iHealthID = iHealthID;
		this.medicineValue = medicineValue;
		this.ts = ts;
	}
	public String getPhoneDataID() {
		return phoneDataID;
	}
	public void setPhoneDataID(String phoneDataID) {
		this.phoneDataID = phoneDataID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getChangeType() {
		return changeType;
	}
	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}
	public long getLastChangeTime() {
		return lastChangeTime;
	}
	public void setLastChangeTime(long lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}
	public int getIsRecentlyUsed() {
		return isRecentlyUsed;
	}
	public void setIsRecentlyUsed(int isRecentlyUsed) {
		this.isRecentlyUsed = isRecentlyUsed;
	}
	public String getiHealthID() {
		return iHealthID;
	}
	public void setiHealthID(String iHealthID) {
		this.iHealthID = iHealthID;
	}
	public int getDateType() {
		return DateType;
	}
	public void setDateType(int dateType) {
		DateType = dateType;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public float getMedicineValue() {
		return medicineValue;
	}

	public void setMedicineValue(float medicineValue) {
		this.medicineValue = medicineValue;
	}

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public static class Type{
        public static final int ORAL_MEDS = 1;
        public static final int INSULIN = 2;
    }
	
}
