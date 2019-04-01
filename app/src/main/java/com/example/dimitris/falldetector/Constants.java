package com.example.dimitris.falldetector;

public interface Constants {

    // Message types sent from the Accelerometer Handler
    public static final int MESSAGE_CHANGED = 1;
    public static final int MESSAGE_EMERGENCY = 2;
    public static final int MESSAGE_TOAST = 3;

    // Key names received from the Accelerometer Handler
    public static final String VALUE = "value";
    public static final String TOAST = "toast";

    // Shared preferences keys
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Code = "codeKey";
    public static final String Phone = "phoneKey";
    public static final String History = "historyKey";

    // action broadcast
    public static final String ACTION_EVENT_ENTRY = "ACTION_EVENT_ENTRY";
    public static final String ACTION_EVENT_CHECKED = "ACTION_EVENT_CHECKED";
    public static final String ACTION_EVENT_TIMER_START = "ACTION_EVENT_TIMER_START";
    public static final String ACTION_EVENT_TIMER_FINISHED = "ACTION_EVENT_TIMER_FINISHED";
    public static final String ACTION_EVENT_STOP_TIMER = "ACTION_EVENT_STOP_TIMER";


}
