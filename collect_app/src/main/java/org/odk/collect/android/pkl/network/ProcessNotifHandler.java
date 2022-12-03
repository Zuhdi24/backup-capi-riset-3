package org.odk.collect.android.pkl.network;

import android.location.Location;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.pkl.database.DatabaseHandler;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.util.LocationService;

/**
 * @author TimPKL
 */

public class ProcessNotifHandler extends NotificationExtenderService {

    private Location lokasi;
    private LocationService ls;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult result) {
        JSONObject data = result.payload.additionalData;
        CapiPreference preference = CapiPreference.getInstance();

        if (data != null) {
            try {
                String treatment = data.getString(StaticFinal.ONEKEY_TYPE);
                Log.d("RAHADI", "Treatment : " + treatment);
                switch (treatment) {
                    case StaticFinal.ONETREAT_MONITORING:
                        ls = LocationService.getLocationManager(getApplicationContext());
                        lokasi = ls.getBestLoc();
                        String nama = (String) preference.get(CapiKey.KEY_NAMA);

                        String sendstring = "{ " +
                                "'" + StaticFinal.ONESEND_INCLUDEPLAYERIDS + "' : ['" + data.getString(StaticFinal.ONETREAT_ID) + "'], " +
                                "'" + StaticFinal.ONESEND_DATA + "' : {" +
                                "'" + StaticFinal.ONESEND_LONGITUDE + "' : '" + lokasi.getLongitude() + "'," +
                                "'" + StaticFinal.ONESEND_LATITUDE + "' : '" + lokasi.getLatitude() + "'," +
                                "'" + StaticFinal.ONESEND_AKURASI + "' : '" + lokasi.getAccuracy() + "'," +
                                "'" + StaticFinal.ONESEND_NAMA + "' : '" + nama + "'" +
                                "}, " +
                                "'" + StaticFinal.ONESEND_CHROMEWEBICON + "' : '" + StaticFinal.monitoringLogoURL + "'," +
                                "'" + StaticFinal.ONESEND_URL + "' : '" + StaticFinal.monitoringURL + "'," +
                                "'" + StaticFinal.ONESEND_TTL + "' : '" + 15 + "'," +
                                "'" + StaticFinal.ONESEND_CONTENTS + "' : {'" + StaticFinal.ONESEND_LANG_EN + "' : 'Lokasi dari " + nama + " ditemukan.'}" +
                                "}";

                        Log.d("RAHADI", "Send Data : " + sendstring);

                        OneSignal.postNotification(sendstring, new OneSignal.PostNotificationResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.d("RAHADI", "onSuccess postNotification : " + response);
                                ls.stop(getApplicationContext());
                            }

                            @Override
                            public void onFailure(JSONObject response) {
                                Log.d("RAHADI", "onFailure postNotification : " + response);
                            }
                        });
                        return true;
                    case StaticFinal.ONETREAT_BERITA:
                        DatabaseHandler.getInstance(getApplicationContext()).getNotifications(
                                (String) preference.get(CapiKey.KEY_NIM), true);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
