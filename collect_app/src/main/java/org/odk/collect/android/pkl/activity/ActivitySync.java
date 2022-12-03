package org.odk.collect.android.pkl.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.FileBackupAdapter;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.SyncManager;
import org.odk.collect.android.pkl.task.LocalizerTask;
import org.odk.collect.android.pkl.task.RestoreTask;
import org.odk.collect.android.pkl.util.BackupHelper;

//import org.odk.collect.android.pkl56sams.Helper.Uploader;


/**
 * Created by isfann on 1/1/2017.
 */

public class ActivitySync extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, FileBackupAdapter.OnClickCallback {

    private static final String TAG = "TEST";

    private SyncManager syncManager;

    private SharedPreferences sp;

    EditText input, editHost;
    Button tombolKirim, tombolCloudBackup, tombolLocalBackup;
    ImageButton syncButton;
    Switch httpSwitch;
    TextView httpLabel, responseView;
    Toolbar toolbar;
    RecyclerView rv;
    String host;
    static String nim;
    static String deviceId;
    static Context context;
    Animation spin;
    FileBackupAdapter adapter;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("BACKUP AND RESTORE");

        context = getApplicationContext();

        syncManager = new SyncManager(context);
        sp = getSharedPreferences("sync_pkl56", MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);

        spin = AnimationUtils.loadAnimation(context, R.anim.spin);


        nim = (String) CapiPreference.getInstance().get(CapiKey.KEY_NIM);
        deviceId = syncManager.getDeviceId();
        Log.i(TAG, "onCreate: " + deviceId);
        input = (EditText) findViewById(R.id.data);
        tombolKirim = (Button) findViewById(R.id.buttonKirim);
        tombolCloudBackup = (Button) findViewById(R.id.buttonCloudBackup);
        tombolLocalBackup = (Button) findViewById(R.id.buttonLocalBackup);
        editHost = (EditText) findViewById(R.id.editHost);
        httpSwitch = (Switch) findViewById(R.id.httpSwitch);
        httpLabel = (TextView) findViewById(R.id.httpLabel);
        responseView = (TextView) findViewById(R.id.responseTextview);
        syncButton = (ImageButton) findViewById(R.id.buttonSync);
        rv = (RecyclerView) findViewById(R.id.rvListFile);

        input.setVisibility(View.GONE);
        tombolCloudBackup.setVisibility(View.GONE);
        tombolKirim.setVisibility(View.GONE);
        tombolLocalBackup.setVisibility(View.GONE);

        updateUi();
        adapter = new FileBackupAdapter(context, BackupHelper.getListFileBackup(nim, deviceId));
        adapter.setOnClickCallback(this);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);

        httpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    httpLabel.setText(httpSwitch.getTextOn());
                    host = httpSwitch.getTextOn() + editHost.getText().toString();
                } else {
                    httpLabel.setText(httpSwitch.getTextOff());
                    host = httpSwitch.getTextOff() + editHost.getText().toString();
                }
            }
        });

        httpSwitch.setChecked(false);

        tombolKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (httpSwitch.isChecked()) {
                    httpLabel.setText(httpSwitch.getTextOn());
                    host = httpSwitch.getTextOn() + editHost.getText().toString();
                } else {
                    httpLabel.setText(httpSwitch.getTextOff());
                    host = httpSwitch.getTextOff() + editHost.getText().toString();
                }
                String data = input.getText().toString();
                Log.d(TAG, "onClick: " + data);
//                new Uploader(context).execute(host, data);
            }
        });

        tombolCloudBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (httpSwitch.isChecked()) {
                    httpLabel.setText(httpSwitch.getTextOn());
                    host = httpSwitch.getTextOn() + editHost.getText().toString();
                } else {
                    httpLabel.setText(httpSwitch.getTextOff());
                    host = httpSwitch.getTextOff() + editHost.getText().toString();
                }
//                new Uploader(context).execute(host, BackupHelper.encodeAllData(context, nim, deviceId));
            }
        });

        tombolLocalBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LocalizerTask(context).execute(BackupHelper.encodeAllData(context, nim, deviceId));
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                backupLocal();
                if (httpSwitch.isChecked()) {
                    httpLabel.setText(httpSwitch.getTextOn());
                    host = httpSwitch.getTextOn() + editHost.getText().toString();
                } else {
                    httpLabel.setText(httpSwitch.getTextOff());
                    host = httpSwitch.getTextOff() + editHost.getText().toString();
                }
//                new Uploader(context).execute(host, BackupHelper.encodeAllData(context, nim, deviceId));
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("IsSync")) {
            updateUi();
        }
    }

    void updateUi() {
        if (syncManager.isSync()) {
            Log.i(TAG, "updateUi: uploading");
            syncButton.setEnabled(false);
            syncButton.setColorFilter(Color.rgb(200, 200, 200));
            syncButton.startAnimation(spin);
            responseView.setText("Synchronizing...");
        } else {
            Log.i(TAG, "updateUi: not uploadign");
            syncButton.setEnabled(true);
            syncButton.setColorFilter(Color.rgb(0, 139, 139));
            syncButton.clearAnimation();
            responseView.setText("");
        }
    }

    public static void backupLocal(Context context) {
        new LocalizerTask(context).execute(BackupHelper.encodeAllData(context, nim, deviceId));
    }

    @Override
    public void onClickDownloadBackup(int p) {
        String path = BackupHelper.getListFileBackup(nim, deviceId).get(p).getPath();
        Log.d(TAG, "onClickDownloadBackup: " + path);
        String json = BackupHelper.readFile(path);
        new RestoreTask(context).execute(json);
    }
}

