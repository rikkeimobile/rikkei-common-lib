package rikkei.android.sns.lib;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by tuyenpx on 01/06/2016.
 */

@SuppressWarnings("UnusedDeclaration")
public class RkFaceBookApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
