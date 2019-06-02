package com.itsoluations.vavisa.darhaa;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Paper.init(this);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ReceivedHandler())
                .setNotificationOpenedHandler(new OpenHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(
                new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        Paper.book("DarHaa").write("oneSignalPlayerId", userId);
                    }
                });

    }

    private class ReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {

        }
    }

    private class OpenHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

        }
    }

}
