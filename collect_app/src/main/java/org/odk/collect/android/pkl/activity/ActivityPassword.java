package org.odk.collect.android.pkl.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.util.MasterPassword;

import java.util.Calendar;
import java.util.HashMap;

/**
 * @author isfann
 */

public class ActivityPassword extends AppCompatActivity {
    private final String TAG = "LIST BS";
    HashMap<String, String> userDetails;
    String nim, nimKortim, passKortim;
    TextView btnCopyPass;
    Button btnResetPass;
    TextView tv, cd;
    Toolbar toolbar;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnResetPass = (Button) findViewById(R.id.refreshPassword);
        btnCopyPass = (TextView) findViewById(R.id.copyPassword);
        tv = (TextView) findViewById(R.id.tvPassword);
        cd = (TextView) findViewById(R.id.cd);

        setTitle("PASSWORD MASTER");

        CapiPreference preference = CapiPreference.getInstance();

        nim = (String) preference.get(CapiKey.KEY_NIM);
        nimKortim = (String) preference.get(CapiKey.KEY_NIM_KORTIM);
        passKortim = (String) preference.get(CapiKey.KEY_PASS_KORTIM);

        tv.setText(MasterPassword.getPassword(nimKortim));

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("masterPassword", tv.getText().toString());
                clipboard.setPrimaryClip(clip);

                tv.setText(MasterPassword.getPassword(nimKortim));
            }
        });

        btnCopyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("masterPassword", tv.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Password Disalin", Toast.LENGTH_SHORT).show();

                tv.setText(MasterPassword.getPassword(nimKortim));
            }
        });


        resetPassword();

    }

    private void resetPassword() {
        tv.setText(MasterPassword.getPassword(nimKortim));
        Calendar now = Calendar.getInstance();
        Calendar nextHour = Calendar.getInstance();
        nextHour.add(Calendar.HOUR, 1);
        nextHour.set(Calendar.MINUTE, 0);
        nextHour.set(Calendar.SECOND, 0);

        long difference = nextHour.getTimeInMillis() - now.getTimeInMillis();
        new CountDownTimer(difference, 1000) {

            public void onTick(long millisUntilFinished) {
                long menit = millisUntilFinished / 60000;
                long detik = (millisUntilFinished / 1000) % 60;
                cd.setText("Password akan di-reset dalam " + String.format("%02d", menit) + ":" + String.format("%02d", detik));
            }

            public void onFinish() {
                resetPassword();
            }
        }.start();
    }

}
