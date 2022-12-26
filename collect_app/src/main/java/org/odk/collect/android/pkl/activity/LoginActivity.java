package org.odk.collect.android.pkl.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.briefcase.BCrypt;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.task.LoginTask;
import org.odk.collect.android.preferences.PreferencesActivity;

public class LoginActivity extends AppCompatActivity implements LoginTask.LoginListener {

    Button loginButton;
    ImageButton settingButton;

    TextInputLayout nimKotak, passKotak;
    TextInputEditText nimText, passwordText;
    private String serviceBaseUrl;
    private ProgressDialog progressDialog;
    private static final String TAG = "LoginActivity";
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.button);
        settingButton = findViewById(R.id.button_setting);
        nimText = findViewById(R.id.editText1);
        passwordText = findViewById(R.id.editTextPw);
        nimKotak = findViewById(R.id.kotak1);
        passKotak = findViewById(R.id.kotak2);

        nimText.setTextColor(Color.parseColor("#7bb4e0"));
        passwordText.setTextColor(Color.parseColor("#7bb4e0"));

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, PreferencesActivity.class);
                startActivity(i);
            }
        });

        nimText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nimKotak.setError(null);
                passKotak.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nimKotak.setError(null);
                passKotak.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nimKotak.setError("");
                passKotak.setError("");
                if (isNetworkConnected()) {
                    String nim = nimText.getText().toString().trim();
                    String pass = passwordText.getText().toString().trim();
                    if (nim.length() > 0 && pass.length() > 0) {
                        if (nim.length() > 6) {
                            execLogin(nim, pass);
                        } else {
                            nimKotak.setError("NIM minimal 7 karakter!");
                        }
                    } else if (nim.length() <= 0 && pass.length() > 0) {
                        nimKotak.setError("NIM Tidak Boleh Kosong!");
                    } else if (nim.length() > 0 && pass.length() <= 0) {
                        if (nim.length() <= 6) {
                            nimKotak.setError("NIM minimal 7 karakter!");
                            passKotak.setError("Password tidak boleh kosong!");
                        } else {
                            passKotak.setError("Password tidak boleh kosong!");
                        }
                    } else {
                        nimKotak.setError("NIM Tidak Boleh Kosong!");
                        passKotak.setError("Password tidak boleh kosong!");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak terhubung jaringan !!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


//    Sebelumnya pakai md5
//    ---------------------------
//    public String getEncrypt(String ori) {
//
//        String encrypted = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(ori.getBytes());
//            byte[] digest = md.digest();
//            StringBuilder sb = new StringBuilder();
//            for (byte b : digest) {
//                sb.append(String.format("%02x", b & 0xff));
//            }
//            encrypted = sb.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return encrypted;
//    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return null != cm && cm.getActiveNetworkInfo() != null;
    }

    public void timerDelayRemoveDialog(long time, final Dialog d) {
        new Handler().postDelayed(() -> d.dismiss(), time);
    }

    private void execLogin(final String nim, final String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Harap tunggu...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        timerDelayRemoveDialog(10000, progressDialog);

        serviceBaseUrl = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).getString("host", "https://capi62.stis.ac.id/web-service-62/public");

        String hashPassUrl = serviceBaseUrl + "/gethashpw?nim=" + nim;

        Log.d(TAG, hashPassUrl);
        mQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, hashPassUrl, null,
                response -> {
                    try {
                        String hashpassword = response.getString("password");
                        Log.d("kucing", "onResponse: " + hashpassword);

                        if (hashpassword == "null") {
                            Log.d("kucing", "onResponse: masuk ");
                            hashpassword = "$2y$12$/HwYAs1tPlF2lrDnSS7iLui94FvbQg5wTMnVe0tO52kh.I/imo1SO"; //bukan password mahasiswa pastinyaaaaaaaaaasdasdasdwq
                        }

                        String generatedSecuredPasswordHash = hashpassword;
                        Log.d("coba hash", generatedSecuredPasswordHash);
                        String hashYangMauDicocokin = generatedSecuredPasswordHash.replaceFirst("2y", "2a");
                        Log.d("coba hash 2y", generatedSecuredPasswordHash);
                        Log.d("coba hash 2a", hashYangMauDicocokin);

                        boolean matched = BCrypt.checkpw(password, hashYangMauDicocokin);
                        Log.d("coba hash", String.valueOf(matched));

                        if (matched) {


                            String loginUrl = serviceBaseUrl + "/login?nim=" + nim + "&password=" + generatedSecuredPasswordHash;
                            String extraUrl = serviceBaseUrl + "/getextracontent?nim=" + nim;
                            Log.e("Encrypt String Password", generatedSecuredPasswordHash);
                            Log.e("url login", loginUrl);
                            Log.e("url extra", extraUrl);

                            //execute Task
                            new LoginTask(LoginActivity.this).execute(loginUrl, extraUrl, generatedSecuredPasswordHash);
                            progressDialog.dismiss();

                        } else {

                            Toast.makeText(LoginActivity.this, "Terjadi kesalahan, pastikan NIM dan password benar!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        //berhenti
        mQueue.add(request);

    }

    @Override
    public void onLoginFinish(String result) {
        switch (result) {
            case LoginTask.LOGIN_SUCCESS:
                final Handler handler = new Handler();
                CapiPreference preference = CapiPreference.getInstance();
                //Tags needed to identify device identity
                JSONObject oneTag = new JSONObject();
                try {
                    oneTag.put("isKoor",
                            StaticFinal.JABATAN_KORTIM_ID.equals(preference.get(CapiKey.KEY_ID_JABATAN)));
                    oneTag.put("nim", preference.get(CapiKey.KEY_NIM));
                    oneTag.put("capi", "konven");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Send the tags
                OneSignal.sendTags(oneTag);

                //Set New Directory
                Log.d("Login", "LoginActivity NIM : " + preference.get(CapiKey.KEY_NIM));
                Collect.setNewDirectory((String) preference.get(CapiKey.KEY_NIM));

                startActivity(new Intent(LoginActivity.this, PklMainActivity.class));
                finish();
                handler.postDelayed(() -> progressDialog.dismiss(), 500);
                break;

            case LoginTask.LOGIN_FAIL_USER:
                Log.e("LoginActivity Status", result);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, pastikan Anda mendapatkan wilayah kerja!",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;

            case LoginTask.LOGIN_FAIL_URL:
                Log.e("LoginActivity Status", result);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, server sedang dalam perbaikan.",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;

            case LoginTask.LOGIN_FAIL_EXTRA:
                Toast.makeText(this, "Gagal mendapat konten tambahan", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;

            default:
                Log.e("LoginActivity Status", result);
                Toast.makeText(getApplicationContext(), "Couldn't get data from server", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;
        }
    }
}
