package rikkei.android.common.sample.config;

import rikkei.android.common.lib.RkLogger;

/**
 * Created by tuyenpx on 04/05/2016.
 */
public class Config {

    private static final boolean DE_BUG = true;

    public static void initDebug() {
        RkLogger.setEnableLogcat(DE_BUG);
    }
}
