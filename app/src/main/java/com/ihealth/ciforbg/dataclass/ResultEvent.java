package com.ihealth.ciforbg.dataclass;

/**
 * Created by lynn on 15-7-5.
 */
public class ResultEvent {

    public static final int TYPE_RESULT = 1;
    public static final int TYPE_PERIOD = 2;
    public static final int TYPE_MEDICATION = 3;
    public static final int TYPE_ADDMEDICATION = 4;
    public static final int TYPE_DISMISS = 5;
    public static final int COMMON_MEDICATIONS = 6;
    public static final int COMMON_ADDMEDICATION_EXCEPTION = 7;

    private int EventType;

    private Data_TB_BGResult BgResult;

    public ResultEvent(int type){
        this.EventType = type;
    }

    public int getEventType() {
        return EventType;
    }

    public void setEventType(int eventType) {
        EventType = eventType;
    }

    public Data_TB_BGResult getBgResult() {
        return BgResult;
    }

    public void setBgResult(Data_TB_BGResult bgResult) {
        BgResult = bgResult;
    }
}
