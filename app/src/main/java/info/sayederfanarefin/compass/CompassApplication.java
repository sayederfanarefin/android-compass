package info.sayederfanarefin.compass;

import android.app.Application;

import info.sayederfanarefin.compass.BuildConfig;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by Duy on 10/19/2017.
 */

public class CompassApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseCrash.setCrashCollectionEnabled(BuildConfig.DEBUG);
    }
}
