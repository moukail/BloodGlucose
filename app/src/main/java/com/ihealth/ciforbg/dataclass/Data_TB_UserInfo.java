package com.ihealth.ciforbg.dataclass;

/**
 * Created by lynn on 15-7-7.
 */
public class Data_TB_UserInfo {

    private String UserName = "";
    private String BottleId = "";

    public Data_TB_UserInfo(String userName, String bottleId){
        this.UserName = userName;
        this.BottleId = bottleId;
    }

    public Data_TB_UserInfo(){
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBottleId() {
        return BottleId;
    }

    public void setBottleId(String bottleId) {
        BottleId = bottleId;
    }
}
