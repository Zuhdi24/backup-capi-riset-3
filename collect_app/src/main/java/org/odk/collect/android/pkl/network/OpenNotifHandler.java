package org.odk.collect.android.pkl.network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.pkl.activity.ActivityListBlokSensus;
import org.odk.collect.android.pkl.activity.ActivityListRumahTangga;
import org.odk.collect.android.pkl.activity.KortimListActivity;
import org.odk.collect.android.pkl.activity.NotificationActivity;
import org.odk.collect.android.pkl.activity.PclListActivity;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.UnitUsahaPariwisata;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;

/**
 * @author Rahadi
 */

public class OpenNotifHandler implements OneSignal.NotificationOpenedHandler {

    private Context context;

    private static OpenNotifHandler sInstance;
    private CapiPreference preference;

    public static synchronized OpenNotifHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new OpenNotifHandler(context);
        }
        return sInstance;
    }

    private OpenNotifHandler(Context context) {
        this.context = context;
        preference = CapiPreference.getInstance();
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String url = result.notification.payload.launchURL;
//        Log.d("RAHADI", "data : " + data);
        String kodeBs, status, mode;

        if (data != null) {
            try {
                String treatment = data.getString(StaticFinal.ONEKEY_TYPE);
//                Log.d("RAHADI", "Treatment : " + treatment);
                switch (treatment) {
                    case StaticFinal.ONETREAT_KORTIMPCL:
                        Intent intent;
                        if (StaticFinal.JABATAN_KORTIM_ID.equals(preference.get(CapiKey.KEY_ID_JABATAN))) {
                            intent = new Intent(context, KortimListActivity.class);
                        } else {
                            intent = new Intent(context, PclListActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case StaticFinal.ONETREAT_SOFTWAREUPDATE:
                        final String appPackageName = context.getPackageName();
                        try {
//                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
//                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(i);
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        } catch (android.content.ActivityNotFoundException anfe) {
//                            Intent i = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(i);
                            Intent i = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                        break;
                    case StaticFinal.ONETREAT_SAMS_SAMPELTERAMBIL:
                        if (result.notification.groupedNotifications != null && result.notification.groupedNotifications.size() > 1) {
                            Log.d("RAHADI", "GroupedNotifications Size : " + result.notification.groupedNotifications.size());
                            Intent i = new Intent(context, ActivityListBlokSensus.class);
                            i.putExtra(StaticFinal.EXTRA_REFRESHDATA, true);
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        } else {
                            kodeBs = data.getString(StaticFinal.ONEKEY_KODEBS);
                            status = UnitUsahaPariwisata.STATUS_UPLOADED;
                            mode = ActivityListRumahTangga.MODE_SAMPEL;

                            Intent i = new Intent(context, ActivityListRumahTangga.class);
                            i.putExtra(StaticFinal.EXTRA_KODEBS, kodeBs);
                            i.putExtra(StaticFinal.EXTRA_STATUS, status);
                            i.putExtra(StaticFinal.EXTRA_MODE, mode);
                            i.putExtra(StaticFinal.EXTRA_REFRESHDATA, true);
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                        break;
                    case StaticFinal.ONETREAT_SAMS_SYNCRUTA:
                        if (result.notification.groupedNotifications != null && result.notification.groupedNotifications.size() > 1) {
                            Log.d("RAHADI", "GroupedNotifications Size : " + result.notification.groupedNotifications.size());
                            Intent i = new Intent(context, ActivityListBlokSensus.class);
                            i.putExtra(StaticFinal.EXTRA_REFRESHDATA, true);
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        } else {
                            kodeBs = data.getString(StaticFinal.ONEKEY_KODEBS);
                            DatabaseSampling db = DatabaseSampling.getInstance();
                            status = db.getBlokSensusByKode(kodeBs).getStatus();
                            mode = ActivityListRumahTangga.MODE_ALL;

                            Intent i = new Intent(context, ActivityListRumahTangga.class);
                            i.putExtra(StaticFinal.EXTRA_KODEBS, kodeBs);
                            i.putExtra(StaticFinal.EXTRA_STATUS, status);
                            i.putExtra(StaticFinal.EXTRA_MODE, mode);
                            i.putExtra(StaticFinal.EXTRA_REFRESHDATA, true);
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                        break;
                    case StaticFinal.ONETREAT_SAMS_OTHER:
                        Intent i2 = new Intent(context, ActivityListBlokSensus.class);
                        i2.putExtra(StaticFinal.EXTRA_REFRESHDATA, true);
                        i2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i2);
                        break;
                    case StaticFinal.ONETREAT_BERITA:
                        Intent i4 = new Intent(context, NotificationActivity.class);
                        i4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i4);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        if (actionType == OSNotificationAction.ActionType.ActionTaken)
//            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
    }
}
