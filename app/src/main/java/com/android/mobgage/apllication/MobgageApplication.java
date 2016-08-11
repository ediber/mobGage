package com.android.mobgage.apllication;

import android.app.Application;
//import com.parse.Parse;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Israel on 07/04/2015.
 */
public class MobgageApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
// Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
//
//        Parse.initialize(this, "jEE7lHukZ2SYAgMjGCYmP84odnifn97ZqCTim0eW", "3fkibTLE0vIdtgCVlmQPTIT8tHuFOQmzCuUo8qgP");
		Fabric.with(this, new Crashlytics());

    }
}
