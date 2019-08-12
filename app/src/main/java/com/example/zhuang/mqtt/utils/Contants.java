package com.example.zhuang.mqtt.utils;

public class Contants {

    public static final String IMEI = "imei";

    /**
     * A notification with no importance: does not show in the shade.
     */
    public static final int IMPORTANCE_NONE = 0;

    /**
     * Min notification importance: only shows in the shade, below the fold.
     */
    public static final int IMPORTANCE_MIN = 1;

    /**
     * Low notification importance: shows everywhere, but is not intrusive.
     */
    public static final int IMPORTANCE_LOW = 2;

    /**
     * Default notification importance: shows everywhere, makes noise, but does not visually
     * intrude.
     */
    public static final int IMPORTANCE_DEFAULT = 3;

    /**
     * Higher notification importance: shows everywhere, makes noise and peeks. May use full screen
     * intents.
     */
    public static final int IMPORTANCE_HIGH = 4;

    /**
     * Unused.
     */
    public static final int IMPORTANCE_MAX = 5;
}
