package com.ihealth.ciforbg.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ihealth.ciforbg.dataclass.Data_DB_BGPlan;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;
import com.ihealth.ciforbg.dataclass.Data_TB_UserInfo;

public class DataBaseTools {

    private SQLiteDatabase db;

    public DataBaseTools(Context context) {
        db = DataBaseOpenHelper.getInstance(context);
    }

    /**
     * @param tableName
     * @param conditionStr
     * @return 删除表中某条数据的方法
     */
    public Boolean deleteData(String tableName, String conditionStr) {
        Boolean iResult = false;
        String sql = "";
        if (conditionStr.length() > 0) {
            sql = "DELETE FROM  " + tableName + " where " + conditionStr;
        } else {
            sql = "DELETE FROM  " + tableName;
        }

        try {
            db.execSQL(sql);
            iResult = true;
        } catch (SQLException e) {
            e.printStackTrace();
            iResult = false;
        } finally {

        }
        return iResult;
    }


    /**
     * @param tableName
     * @return 直接删除表的方法
     */
    public Boolean deleteData(String tableName) {
        Boolean isResult = false;

        String sql = "";
        sql = "DELETE FROM  " + tableName;
        try {
            this.db.execSQL(sql);
            isResult = true;
        } catch (SQLException e) {
            e.printStackTrace();
            isResult = false;
        } finally {

        }
        return isResult;
    }

    /**
     * 打印出有DB 中所有的表
     *
     * @return
     */
    public Boolean dsa1() {
        Boolean isResult = false;

        Cursor cursor = db
                .rawQuery(
                        "select name from sqlite_master where type='table' order by name",
                        null);
        while (cursor.moveToNext()) {
            // 遍历出表名
            String name = cursor.getString(0);
            Log.i("System.out", name);
        }
        return isResult;
    }

    /**
     * 删除所有表的数据
     */
    public Boolean deleteAllTableData() {
        Boolean isResult = false;

        Cursor cursor = db
                .rawQuery(
                        "select name from sqlite_master where type='table' order by name",
                        null);
        while (cursor.moveToNext()) {
            // 遍历出表名
            String name = cursor.getString(0);
            Log.i("System.out", name);
            String sql = "DELETE FROM  " + name;

            try {
                this.db.execSQL(sql);
                isResult = true;
            } catch (SQLException e) {
                e.printStackTrace();
                isResult = false;
            } finally {

            }
        }
        return isResult;
    }

    /**
     * @param tableName
     * @param columns
     * @param conditionStr
     * @return 查数据 author_GYL 2013年12月23日 下午2:22:43
     */
    public Cursor selectData(String tableName, String columns[],
                             String conditionStr) {
        Cursor cur = null;

        try {
            cur = this.db.query(tableName, columns, conditionStr, null, null,
                    null, null);
            return cur;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cur;
    }

    /**
     * 带过滤的查询
     * zhanglei
     * 2015年1月15日 下午1:52:06
     *
     * @param tableName
     * @param columns
     * @param conditionStr
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public Cursor selectData(String tableName, String columns[], String conditionStr, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor cur = null;
        try {
            cur = this.db.query(tableName, columns, conditionStr, selectionArgs, groupBy, having, orderBy);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cur;
    }

    /**
     * @param tableName
     * @param conditionStr
     * @param valueStr
     * @return 修改数据
     */
    public Boolean updateData(String tableName, String conditionStr,
                              String valueStr) {
        Boolean isResult = false;
        String sql = "";
        if (valueStr.length() > 0) {
            sql = "UPDATE " + tableName + " SET " + valueStr + " where "
                    + conditionStr + ";";
        } else {
            sql = "UPDATE " + tableName + " SET " + valueStr;
        }
        try {
            this.db.execSQL(sql);
            isResult = true;
            Log.i("", "更新sql=" + sql);
        } catch (SQLException e) {
            e.printStackTrace();
            isResult = false;
        } finally {
        }
        return isResult;
    }

    public Boolean addData(String tableName, Object obj) {
        Boolean isResult = false;
        String sql;

        //用户信息 表
        if (tableName.equals(Constants_DB.TABLE_TB_USERINFO)) {
            try {
                sql = "insert into " + Constants_DB.TABLE_TB_USERINFO + " ("
                        + Constants_DB.USERINFO_USERNAME + ","
                        + Constants_DB.USERINFO_BOTTLEID + ")VALUES('"
                        + ((Data_TB_UserInfo) obj).getUserName() + "','"
                        + ((Data_TB_UserInfo) obj).getBottleId() + "')";
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            try {
                this.db.execSQL(sql);
                isResult = true;
            } catch (SQLException e) {
                e.printStackTrace();
                isResult = false;
            }
        }

        //bottle info 表
        else if (tableName.equals(Constants_DB.TABLE_TB_BOTTLEINFO)) {
            try {
                sql = "insert into " + Constants_DB.TABLE_TB_BOTTLEINFO + " ("
                        + Constants_DB.BOTTLEINFO_TIMEZONE + ","
                        + Constants_DB.BOTTLEINFO_TS + ","
                        + Constants_DB.BOTTLEINFO_STRIPLEFT + ","
                        + Constants_DB.BOTTLEINFO_OVERTIME + ","
                        + Constants_DB.BOTTLEINFO_OPENDATE + ","
                        + Constants_DB.BOTTLEINFO_BOTTLEID + ","
                        + Constants_DB.BOTTLEINFO_STRIPCODE + ","
                        + Constants_DB.BOTTLEINFO_USERNAME
                        + ")VALUES("
                        + ((Data_TB_StripBottleInfo) obj).getTimeZone() + ","
                        + ((Data_TB_StripBottleInfo) obj).getTS() + ","
                        + ((Data_TB_StripBottleInfo) obj).getStripLeft() + ","
                        + ((Data_TB_StripBottleInfo) obj).getOverTime() + ","
                        + ((Data_TB_StripBottleInfo) obj).getOpenDate() + ",'"
                        + ((Data_TB_StripBottleInfo) obj).getBottledId() + "','"
                        + ((Data_TB_StripBottleInfo) obj).getStripCode() + "','"
                        + ((Data_TB_StripBottleInfo) obj).getUserName() + "')";
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            try {
                this.db.execSQL(sql);
                isResult = true;
            } catch (SQLException e) {
                e.printStackTrace();
                isResult = false;
            }
        }

        else if(tableName.equals(Constants_DB.TABLE_TB_MEDICINE)){
            //血糖药物表
            try{
                sql = "insert into "+ Constants_DB.TABLE_TB_MEDICINE+ " ("
                        + Constants_DB.MEDICINE_CHANGETYPE + " ,"
                        + Constants_DB.MEDICINE_LASTCHANGETIME + ","
                        + Constants_DB.MEDICINE_PHONECREATTIME + ","
                        + Constants_DB.MEDICINE_PHONEDATAID + ","
                        + Constants_DB.MEDICINE_IHEALTHID + ","
                        + Constants_DB.MEDICINE_NAME + ","
                        + Constants_DB.MEDICINE_NICKNAME + ","
                        + Constants_DB.MEDICINE_ISRECENTLYUSED + ","
                        + Constants_DB.MEDICINE_MEDICINE_VALUE + ","
                        + Constants_DB.MEDICINE_TYPE + ")VALUES("
                        + ((Data_TB_Medicine) obj).getChangeType() + ","
                        + ((Data_TB_Medicine) obj).getLastChangeTime() + ","
                        + ((Data_TB_Medicine) obj).getCreateTime() + ",'"
                        + ((Data_TB_Medicine) obj).getPhoneDataID() + "','"
                        + ((Data_TB_Medicine) obj).getiHealthID() + "','"
                        + ((Data_TB_Medicine) obj).getName() + "','"
                        + ((Data_TB_Medicine) obj).getNickName() + "',"
                        + ((Data_TB_Medicine) obj).getIsRecentlyUsed() + ","
                        + ((Data_TB_Medicine) obj).getMedicineValue() + ","
                        + ((Data_TB_Medicine) obj).getType() +");";
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            try {
                db.execSQL(sql);
                isResult = true;
            } catch (SQLException e) {
                e.printStackTrace();
                isResult = false;
            }
        }

        else if(tableName.equals(Constants_DB.TABLE_TB_MEDICATIONSELECT)){
            //用户药物表
            try{
                sql = "insert into "+ Constants_DB.TABLE_TB_MEDICATIONSELECT + " ("
                        + Constants_DB.MEDICATIONSELECT_CHANGETYPE + " ,"
                        + Constants_DB.MEDICATIONSELECT_LASTCHANGETIME + ","
                        + Constants_DB.MEDICATIONSELECT_PHONECREATTIME + ","
                        + Constants_DB.MEDICATIONSELECT_PHONEDATAID + ","
                        + Constants_DB.MEDICATIONSELECT_IHEALTHID + ","
                        + Constants_DB.MEDICATIONSELECT_NAME + ","
                        + Constants_DB.MEDICATIONSELECT_NICKNAME + ","
                        + Constants_DB.MEDICATIONSELECT_ISRECENTLYUSED + ","
                        + Constants_DB.MEDICATIONSELECT_MEDICINE_VALUE + ","
                        + Constants_DB.MEDICATIONSELECT_TYPE + ")VALUES("
                        + ((Data_TB_Medicine) obj).getChangeType() + ","
                        + ((Data_TB_Medicine) obj).getLastChangeTime() + ","
                        + ((Data_TB_Medicine) obj).getCreateTime() + ",'"
                        + ((Data_TB_Medicine) obj).getPhoneDataID() + "','"
                        + ((Data_TB_Medicine) obj).getiHealthID() + "','"
                        + ((Data_TB_Medicine) obj).getName() + "','"
                        + ((Data_TB_Medicine) obj).getNickName() + "',"
                        + ((Data_TB_Medicine) obj).getIsRecentlyUsed() + ","
                        + ((Data_TB_Medicine) obj).getMedicineValue() + ","
                        + ((Data_TB_Medicine) obj).getType() +");";
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            try {
                db.execSQL(sql);
                isResult = true;
            } catch (SQLException e) {
                e.printStackTrace();
                isResult = false;
            }
        }

        //血糖目标
        else if(tableName.equals(Constants_DB.TABLE_TB_BGPLAN)){
            try{
                sql = "insert into " + Constants_DB.TABLE_TB_BGPLAN + " ("
                        + Constants_DB.BGPLAN_PRE_3 + ","
                        + Constants_DB.BGPLAN_PRE_2 + ","
                        + Constants_DB.BGPLAN_PRE_1 + ","
                        + Constants_DB.BGPLAN_POST_3 + ","
                        + Constants_DB.BGPLAN_POST_2 + ","
                        + Constants_DB.BGPLAN_POST_1  + ")VALUES("
                        + ((Data_DB_BGPlan) obj).getGlucose_targets_pre_3() + ","
                        + ((Data_DB_BGPlan) obj).getGlucose_targets_pre_2() + ","
                        + ((Data_DB_BGPlan) obj).getGlucose_targets_pre_1() + ","
                        + ((Data_DB_BGPlan) obj).getGlucose_targets_post_3() + ","
                        + ((Data_DB_BGPlan) obj).getGlucose_targets_post_2() + ","
                        + ((Data_DB_BGPlan) obj).getGlucose_targets_post_1() + ");";
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            try {
                this.db.execSQL(sql);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return isResult;
    }
}
