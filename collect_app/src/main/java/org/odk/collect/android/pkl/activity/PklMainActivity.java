package org.odk.collect.android.pkl.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.database.DBhandler;
import org.odk.collect.android.pkl.database.DatabaseHandler;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.fragment.BerandaFragment;
import org.odk.collect.android.pkl.fragment.KortimKuesionerFragment;
import org.odk.collect.android.pkl.fragment.ListingFragment;
import org.odk.collect.android.pkl.fragment.PclKuesionerFragment;
import org.odk.collect.android.pkl.network.VolleySingleton;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.task.SynchronizeTask;
import org.odk.collect.android.pkl.util.LocationService;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PklMainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private CapiPreference preference;
    private String idJabatan;
    private int unreadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity
            startActivity(new Intent(PklMainActivity.this, OnBoardingActivity.class));
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
        setContentView(R.layout.pkl_activity_main);

        preference = CapiPreference.getInstance();
        idJabatan = (String) preference.get(CapiKey.KEY_ID_JABATAN);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.tab_beranda);

        Collect.getInstance().getOneSignalBuilder().setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
            @Override
            public void notificationReceived(OSNotification notification) {
                JSONObject data = notification.payload.additionalData;
//        Log.d("RAHADI", "data : " + data);
                String customKey;

                if (data != null) {
                    try {
                        String treatment = data.getString(StaticFinal.ONEKEY_TYPE);
                        Log.d("RAHADI", "Treatment : " + treatment);
                        switch (treatment) {
                            case StaticFinal.ONETREAT_BERITA:
                                Log.d("RAHADI", "Received Handler Works");
                                unreadCount = DatabaseHandler.getInstance(getApplicationContext()).getNotificationUnreadCount();
                                invalidateOptionsMenu();
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        // FUNGSI UPDATE POSISI PCL
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("LOC", "LOC permission granted");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String serviceBaseUrl = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).getString("host", "https://capi62.stis.ac.id/web-service-62/public");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        DefaultRetryPolicy drp = new DefaultRetryPolicy(8000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000,
                20, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("LOC", (String) preference.get(CapiKey.KEY_NIM));
                        Log.d("LOC", String.valueOf(location.getLatitude()));
                        Log.d("LOC", String.valueOf(location.getLongitude()));
                        Log.d("LOC", String.valueOf(location.getAccuracy()));

                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                RequestFuture<String> requestFuture = RequestFuture.newFuture();

                                StringRequest request = new StringRequest(
                                        Request.Method.POST,
                                        serviceBaseUrl + "/posisipcl",
                                        requestFuture,
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.e("ERROR SENDLOC", Arrays.toString(error.networkResponse.data));
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("nim", (String) preference.get(CapiKey.KEY_NIM));
                                        params.put("latitude", String.valueOf(location.getLatitude()));
                                        params.put("longitude", String.valueOf(location.getLongitude()));
                                        params.put("akurasi", String.valueOf(location.getAccuracy()));
                                        return params;
                                    }

                                    @Override
                                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                        return super.parseNetworkResponse(response);
                                    }
                                };

                                request.setRetryPolicy(drp);
                                VolleySingleton.getInstance(PklMainActivity.this).addToRequestQueue(request);
                            }
                        });
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {
                        Log.d("LOC", "Provider enabled");
                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

        AppUpdater appUpdater = new AppUpdater(this)
                .setTitleOnUpdateAvailable("Update Aplikasi Tersedia")
                .setButtonUpdate("Update")
                .setButtonDismiss("Nanti")
                .setButtonDoNotShowAgain(null)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://capi62.stis.ac.id/web-service-62/latestVersion/index/4");
        appUpdater.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        unreadCount = DatabaseHandler.getInstance(getApplicationContext()).getNotificationUnreadCount();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() != R.id.tab_beranda) {
            bottomNavigationView.setSelectedItemId(R.id.tab_beranda);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
//        if (unreadCount > 0) {
//            ActionItemBadge.update(this, menu.findItem(R.id.notifikasi), getResources().getDrawable(R.drawable.ic_notifications),
//                    new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge,
//                            ContextCompat.getColor(getApplicationContext(), R.color.colorAccent),
//                            ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark),
//                            Color.WHITE), unreadCount);
////            ActionItemBadge.update(this, menu.findItem(R.id.notifikasi), getResources().getDrawable(R.drawable.ic_notifications),
////                    ActionItemBadge.BadgeStyles.RED, unreadCount);
//        } else {
//            ActionItemBadge.update(this, menu.findItem(R.id.notifikasi), getResources().getDrawable(R.drawable.ic_notifications_none),
//                    new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge,
//                            Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT), unreadCount);
//        }
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // if (id == R.id.notifikasi) {
        //     startActivity(new Intent(PklMainActivity.this, NotificationActivity.class));
        // }
        if (id == R.id.mt_b_panduan) {
            startActivity(new Intent(PklMainActivity.this, OnBoardingActivity.class));
        }
        if (id == R.id.pengaturan) {
            startActivity(new Intent(PklMainActivity.this, PreferencesActivity.class));
        }

        if (id == R.id.keluar) {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar")
                    .setMessage("Pastikan anda terhubung ke internet, data akan disinkronisasi" +
                            "\n\nKeluar dari akun ini ?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (isNetworkConnected()) {
                                new SynchronizeTask(PklMainActivity.this).execute(SynchronizeTask.MODE_SYNC_LOGOUT);
//                                new LogoutTask().execute();
                            } else {
                                Toast toast = Toast.makeText(PklMainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // nggak ngapa2in
                        }
                    })
                    .setIcon(R.drawable.power)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_beranda:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentContainer, new BerandaFragment())
                        .commit();
                break;
            case R.id.tab_kuesioner:
                if (StaticFinal.JABATAN_KORTIM_ID.equals(idJabatan)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentContainer, new KortimKuesionerFragment())
                            .commit();
                } else if (StaticFinal.JABATAN_PENCACAH_ID.equals(idJabatan)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentContainer, new PclKuesionerFragment())
                            .commit();
                }
                break;
            case R.id.tab_listing:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentContainer, new ListingFragment())
                        .commit();
                break;
        }
        return true;
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        final DBhandler db = DBhandler.getInstance();
        final DatabaseSampling dbs = DatabaseSampling.getInstance();
        final DatabaseHandler dbProblem = DatabaseHandler.getInstance(getApplication());
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PklMainActivity.this);
            pDialog.setMessage("Harap tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            CapiPreference.getInstance().clearCapiData();
            preference.clearCapiData();
            db.dropTable();
            dbs.LogoutDropAllTable();
            dbProblem.dropAllTables();
            Collect.setOldDirectory();

            OneSignal.deleteTag("nim");
            OneSignal.deleteTag("isKoor");
            OneSignal.clearOneSignalNotifications();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            Intent toLogin = new Intent(PklMainActivity.this, LoginActivity.class);
            toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toLogin);
            finish();
        }
    }
}
