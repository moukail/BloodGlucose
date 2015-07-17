package com.ihealth.ciforbg;

import android.app.Application;
import android.content.Context;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by lynn on 15-7-2.
 */
public class MyApplication extends Application {

    private static Context context;

    public static final String clientID = "dbbe1fa8b5634ffaa52448c3b70b2abe";
    public static final String clientSecret = "81ad7936c17849168e23586ef9be8f67";

    public static final String AppID = "2882303761517357798";
    public static final String AppKey = "5661735739798";
    public static final String AppSecret = "mOdbrXPQCfGkOyuQMNO27A==";
    public static final String CHANNEL = "test";

    public static final String App_Version = "CiForBg_V1.0.0";

    public static String UserName = "bdyz1016@126.com";

    public static int glucose_targets_pre_1 = 126;
    public static int glucose_targets_pre_2 = 110;
    public static int glucose_targets_pre_3 = 76;
    public static int glucose_targets_post_1 = 200;
    public static int glucose_targets_post_2 = 140;
    public static int glucose_targets_post_3 = 79;

    public static boolean isFromCI = false;
    @Override
    public void onCreate() {
        super.onCreate();
        if(context == null) {
            context = getApplicationContext();
        }
        MiStatInterface.initialize(this, AppID, AppKey, CHANNEL);
        MiStatInterface.setUploadPolicy(MiStatInterface.UPLOAD_POLICY_REALTIME, 0);
        MiStatInterface.enableExceptionCatcher(true);
    }

    public static Context getContext(){
        return context;
    }
}
