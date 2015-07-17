package com.ihealth.ciforbg.dataclass;

/**
 * Created by lynn on 15-7-2.
 */
public class CommonEvent {
    public static final int COMMON_SCANQRCODE = 1;
    public static final int COMMON_STRIPEMPTY = 2;
    public static final int COMMON_SCANRESULT = 3;
    public static final int COMMON_STRIPEXPIRED = 4;
    public static final int COMMON_TESTING = 5;
    public static final int COMMON_BGRESULT = 6;
    public static final int COMMON_EXIT = 7;
    public static final int COMMON_NEXTSTEP = 8;
    public static final int COMMON_DECODEQRCODE = 9;
    public static final int COMMON_SAVETARGETS = 10;

    private int mEventType;
    private Object obj;
    public CommonEvent(int type){
        this.mEventType = type;
    }

    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int eventType) {
        this.mEventType = eventType;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
