package com.ihealth.ciforbg.dataclass;

public class Data_TB_StripBottleInfo {

    private String UserName = "";
	private String BottledId = "";
	private String StripCode = "";
	private long OpenDate;
    private long OverTime;
	private float TimeZone;
	private long TS;
	private int StripLeft;

    private boolean isTen;

    private boolean isNewStrip = false;//true:新试条瓶

	public Data_TB_StripBottleInfo(String bottledId, String stripCode, long openDate, float timeZone, long tS
            , int StripLeft, boolean isNewStrip) {
		super();
		this.BottledId = bottledId;
		this.StripCode = stripCode;
		this.OpenDate = openDate;
		this.TimeZone = timeZone;
		this.TS = tS;
		this.StripLeft = StripLeft;
        this.isNewStrip = isNewStrip;
	}

	public Data_TB_StripBottleInfo() {
		super();
	}

	public String getBottledId() {
		return BottledId;
	}

	public void setBottledId(String bottledId) {
		BottledId = bottledId;
	}

	public String getStripCode() {
		return StripCode;
	}

	public void setStripCode(String stripCode) {
		StripCode = stripCode;
	}

	public long getOpenDate() {
		return OpenDate;
	}

	public void setOpenDate(long openDate) {
		OpenDate = openDate;
	}

	public float getTimeZone() {
		return TimeZone;
	}

	public void setTimeZone(float timeZone) {
		TimeZone = timeZone;
	}

	public long getTS() {
		return TS;
	}

	public void setTS(long tS) {
		TS = tS;
	}

	public int getStripLeft() {
		return StripLeft;
	}

	public void setStripLeft(int stripLeft) {
		StripLeft = stripLeft;
	}

    public long getOverTime() {
        return OverTime;
    }

    public void setOverTime(long overTime) {
        OverTime = overTime;
    }

    public boolean isTen() {
        return isTen;
    }

    public void setTen(boolean isTen) {
        this.isTen = isTen;
    }

    public boolean isNewStrip() {
        return isNewStrip;
    }

    public void setNewStrip(boolean isNewStrip) {
        this.isNewStrip = isNewStrip;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
