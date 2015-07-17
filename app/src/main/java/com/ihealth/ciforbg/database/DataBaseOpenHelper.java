package com.ihealth.ciforbg.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ihealth.ciforbg.MyApplication;
import com.ihealth.ciforbg.dataclass.Data_TB_Medicine;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.Method;

import java.util.ArrayList;

public class DataBaseOpenHelper extends SQLiteOpenHelper {
    private final String TAG = "DataBaseOpenHelper";
	private static DataBaseOpenHelper dbT;
	private static SQLiteDatabase myDatabase;

	Context context;
	public static final String DATABASE_NAME = "CiForBg.DB";
	public static final String PACKAGE_NAME = "CiForBg.DB";
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_PATH = "";

	public static SQLiteDatabase getInstance(Context context) {
		if (dbT == null) {
			synchronized (DataBaseOpenHelper.class) {
				if (dbT == null) {
					dbT = new DataBaseOpenHelper(context);
				}
			}
		}

		if (myDatabase == null) {
			synchronized (DataBaseOpenHelper.class) {
				if (myDatabase == null && dbT != null) {
					myDatabase = dbT.getWritableDatabase();
				}
			}
		}
		return myDatabase;
	}

	public DataBaseOpenHelper(Context context) {
		super(context, DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		// myDatabase = getInstance(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "DataBaseOpenHelper onCreate()");
		this.tableCreat(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase myDataBase, int oldVersion,
			int newVersion) {
		Log.d(TAG, "onUpgrade() oldVersion:" + oldVersion + " newVersion:"
				+ newVersion);
		//		transferTable(myDataBase);
		dropTable(myDataBase);
		tableCreat(myDataBase);
	}

	public Boolean dropTable(SQLiteDatabase db) {
		Boolean iResult = false;

		String sql1 = "DROP TABLE " + Constants_DB.TABLE_TB_USERINFO + " ; ";
        String sql2 = "DROP TABLE " + Constants_DB.TABLE_TB_BOTTLEINFO + " ; ";
        String sql3 = "DROP TABLE " + Constants_DB.TABLE_TB_MEDICINE + " ; ";
		try {
			db.execSQL(sql1);
            db.execSQL(sql2);
            db.execSQL(sql3);
			iResult = true;
			return iResult;
		} catch (SQLException e) {
			L.d(TAG, e.getMessage());
			return iResult;
		} finally {
		}
	}

	/**
	 * @param db
	 * 
	 */
	public void tableCreat(SQLiteDatabase db) {
		L.d(TAG, "tableCreat()  start");
		String sql;

        // 用户信息表
        sql = "CREATE TABLE IF NOT EXISTS "
                + Constants_DB.TABLE_TB_USERINFO
                + " ("
                + Constants_DB.USERINFO_BOTTLEID + " varchar(32,0),"
                + Constants_DB.USERINFO_USERNAME + " varchar(32,0)"
                + ");";

        try {
            db.execSQL(sql);
            L.i(TAG, "用户信息 表创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //bottle 信息表
        sql = "CREATE TABLE IF NOT EXISTS "
                + Constants_DB.TABLE_TB_BOTTLEINFO
                + " ("
                + Constants_DB.BOTTLEINFO_BOTTLEID + " varchar(64,0),"
                + Constants_DB.BOTTLEINFO_OPENDATE + " long(10,0),"
                + Constants_DB.BOTTLEINFO_OVERTIME + " long(10,0),"
                + Constants_DB.BOTTLEINFO_STRIPLEFT + " int(10,0),"
                + Constants_DB.BOTTLEINFO_TIMEZONE + " double(20,0),"
                + Constants_DB.BOTTLEINFO_STRIPCODE + " varchar(128,0),"
                + Constants_DB.BOTTLEINFO_USERNAME + " varchar(64,0),"
                + Constants_DB.BOTTLEINFO_TS + " long(10,0)"
                + ");";

        try {
            db.execSQL(sql);
            L.i(TAG, "bottle info 表创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //药物表
        sql = "CREATE TABLE IF NOT EXISTS "
                + Constants_DB.TABLE_TB_MEDICINE
                + " ("
                + Constants_DB.MEDICINE_CHANGETYPE + " int(4,0) default 1,"
                + Constants_DB.MEDICINE_LASTCHANGETIME + " long(25,0),"
                + Constants_DB.MEDICINE_PHONECREATTIME + " long(25,0),"
                + Constants_DB.MEDICINE_PHONEDATAID + " varchar(128,0),"
                + Constants_DB.MEDICINE_IHEALTHID + "  varchar(128,0),"
                + Constants_DB.MEDICINE_NICKNAME + " varchar(128,0),"
                + Constants_DB.MEDICINE_NAME + "  varchar(128,0),"
                + Constants_DB.MEDICINE_MEDICINE_VALUE + "  float(10,0),"
                + Constants_DB.MEDICINE_ISRECENTLYUSED + "  int(4,0) default 0,"
                + Constants_DB.MEDICINE_TYPE + "  int(4,0)"
                + ");";
        try {
            db.execSQL(sql);

            ArrayList<Data_TB_Medicine> list= Method.creatMedicineList();
            String[] sqls=new String[list.size()];

            db.beginTransaction();

            try{
                for(int i=0;i<sqls.length;i++){
                    sqls[i]="insert into "+ Constants_DB.TABLE_TB_MEDICINE+ " ("
                            + Constants_DB.MEDICINE_CHANGETYPE + " ,"
                            + Constants_DB.MEDICINE_LASTCHANGETIME + ","
                            + Constants_DB.MEDICINE_PHONECREATTIME + ","
                            + Constants_DB.MEDICINE_PHONEDATAID + ","
                            + Constants_DB.MEDICINE_IHEALTHID + ","
                            + Constants_DB.MEDICINE_NAME + ","
                            + Constants_DB.MEDICINE_NICKNAME + ","
                            + Constants_DB.MEDICINE_ISRECENTLYUSED + ","
                            + Constants_DB.MEDICINE_TYPE + ")VALUES("
                            + list.get(i).getChangeType() + ","
                            + list.get(i).getLastChangeTime() + ","
                            + list.get(i).getCreateTime() + ",'"
                            + list.get(i).getPhoneDataID() + "','"
                            + list.get(i).getiHealthID() + "','"
                            + list.get(i).getName() + "','"
                            + list.get(i).getNickName() + "',"
                            + list.get(i).getIsRecentlyUsed() + ","
                            + list.get(i).getType() +");";
                    db.execSQL(sqls[i]);
                }
                db.setTransactionSuccessful();
                Log.i("","保存药物成功");
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                db.endTransaction();
            }
            Log.i("chen", "血糖药物表创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //用户药物表
        sql = "CREATE TABLE IF NOT EXISTS "
                + Constants_DB.TABLE_TB_MEDICATIONSELECT
                + " ("
                + Constants_DB.MEDICATIONSELECT_CHANGETYPE + " int(4,0) default 1,"
                + Constants_DB.MEDICATIONSELECT_LASTCHANGETIME + " long(25,0),"
                + Constants_DB.MEDICATIONSELECT_PHONECREATTIME + " long(25,0),"
                + Constants_DB.MEDICATIONSELECT_PHONEDATAID + " varchar(128,0),"
                + Constants_DB.MEDICATIONSELECT_IHEALTHID + "  varchar(128,0),"
                + Constants_DB.MEDICATIONSELECT_NICKNAME + " varchar(128,0),"
                + Constants_DB.MEDICATIONSELECT_NAME + "  varchar(128,0),"
                + Constants_DB.MEDICATIONSELECT_MEDICINE_VALUE + "  float(10,0),"
                + Constants_DB.MEDICATIONSELECT_ISRECENTLYUSED + "  int(4,0) default 0,"
                + Constants_DB.MEDICATIONSELECT_TYPE + "  int(4,0)"
                + ");";
        try {
            db.execSQL(sql);
            L.i(TAG, "用户药物 表创建成功");
        } catch (SQLException e){
            e.printStackTrace();
        }

        //血糖目标
        sql = "CREATE TABLE IF NOT EXISTS " + Constants_DB.TABLE_TB_BGPLAN
                + " (" + Constants_DB.BGPLAN_POST_1 + " int(10,0),"
                + Constants_DB.BGPLAN_POST_2 + " int(10,0),"
                + Constants_DB.BGPLAN_POST_3 + " int(10,0),"
                + Constants_DB.BGPLAN_PRE_1 + " int(10,0),"
                + Constants_DB.BGPLAN_PRE_2 + " int(10,0),"
                + Constants_DB.BGPLAN_PRE_3 + " int(10,0)"
                + ");";

        try {
            db.execSQL(sql);

            String sql_c = "insert into "+ Constants_DB.TABLE_TB_BGPLAN+ " ("
                    + Constants_DB.BGPLAN_POST_1 + " ,"
                    + Constants_DB.BGPLAN_POST_2 + " ,"
                    + Constants_DB.BGPLAN_POST_3 + " ,"
                    + Constants_DB.BGPLAN_PRE_1 + " ,"
                    + Constants_DB.BGPLAN_PRE_2 + " ,"
                    + Constants_DB.BGPLAN_PRE_3 +  ")VALUES("
                    + MyApplication.glucose_targets_post_1 + ","
                    + MyApplication.glucose_targets_post_2 + ","
                    + MyApplication.glucose_targets_post_3 + ","
                    + MyApplication.glucose_targets_pre_1 + ","
                    + MyApplication.glucose_targets_pre_2 + ","
                    + MyApplication.glucose_targets_pre_3  +");";
            db.execSQL(sql_c);
            L.i(TAG, "目标范围 表创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

}
