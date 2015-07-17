package com.ihealth.ciforbg.dataclass;

/**
 * Created by lynn on 15-7-2.
 */
public class DeviceEvent {
    public static final int DEVICE_DISCONNECT = 0;
    public static final int DEVICE_CONNECT = 1;
    public static final int DEVICE_INSERT = 2;
    public static final int DEVICE_TESTING = 3;
    public static final int DEVICE_OUTPUT = 4;
    public static final int DEVICE_PREPARE = 5;

    private int DeviceState;
    public DeviceEvent(int state){
        this.DeviceState = state;
    }

    public int getDeviceState() {
        return DeviceState;
    }

    public void setDeviceState(int deviceState) {
        DeviceState = deviceState;
    }
}
