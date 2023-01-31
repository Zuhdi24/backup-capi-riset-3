package org.odk.collect.android.pkl.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.RumahTangga;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.util.LocationService;
import org.odk.collect.android.pkl.util.TextColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class IsiRumahTanggaActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = "ISI RUTA";
    DatabaseSampling db;
    String kodeBs, kodeUUP;
    int posisi = 0, plus = 0, minus = 1;
    LocationService ls;

    EditText no_segmen, no_bf, no_bs, no_urut_ruta, namaKRT, alamat, jumlahART, jumlahART10, noHp, noHp2;
    RadioGroup kodeEligible;
//    RadioButton rbEligibleYa, rbEligibleTidak;
    Button submit, segmenplus, segmenminus, bfplus, bfminus, bsplus, bsminus, no_rutaplus, no_rutaminus, kuesionerBlokV;
    TextView pertanyaanNoSegmen, pertanyaanNoBf, pertanyaanNoBs, pertanyaanNoRuta, pertanyaanNamaKRT, pertanyaanAlamat,
            pertanyaanJumlahART, pertanyaanJumlahART10, pertanyaanNoHp, pertanyaanNoHp2, pertanyaanKodeEligible;
    View noteView;
    TextView noSegmenTerakhir, noBfTerakhir, noBsTerakhir;
    Toolbar toolbar;
    Location lokasi;


    @Override
    public boolean onSupportNavigateUp() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_rumah_tangga);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("ISI RUMAH TANGGA");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Fine Location Permision NOT Granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION);
        } else {
            Log.d(TAG, "onCreate: Fine Location Permision Granted");
        }

        db = DatabaseSampling.getInstance();

        // Blok V
        no_segmen = findViewById(R.id.no_urut_segmen);
        no_bf = findViewById(R.id.no_urut_bf);
        no_bs = findViewById(R.id.no_urut_bs);
        no_urut_ruta = findViewById(R.id.no_urt); //no urut rumah tangga
        namaKRT = findViewById(R.id.nama_KRT);
        alamat = findViewById(R.id.alamat);
        jumlahART = findViewById(R.id.jumlah_art);
        jumlahART10 = findViewById(R.id.jumlah_art10);
        noHp = findViewById(R.id.nohp);
        noHp2 = findViewById(R.id.nohp2);
        kodeEligible = findViewById(R.id.kategori_kode_eligible);
        kuesionerBlokV = findViewById(R.id.kuesioner_blok5);

        // Hide Kode Eligible dan Tombol Simpan
        LinearLayout linKodeEligible = findViewById(R.id.lin_kode_eligible);
        linKodeEligible.setVisibility(View.GONE);

        submit = findViewById(R.id.next);
        submit.setVisibility(View.GONE);

        pertanyaanKodeEligible = findViewById(R.id.textview10);

        kuesionerBlokV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "fillBlankForm", "click");
                i = new Intent(IsiRumahTanggaActivity.this,
                        FormChooserList.class);
                startActivity(i);

                // Tampilkan Kode Eligible dan Tombol Simpan
                linKodeEligible.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);

            }
        });

        // Inisasi tombol-tombol
        segmenplus = findViewById(R.id.segmenplus);
        segmenminus = findViewById(R.id.segmenminus);
        bfplus = findViewById(R.id.bfplus);
        bfminus = findViewById(R.id.bfminus);
        bsplus = findViewById(R.id.bsplus);
        bsminus = findViewById(R.id.bsminus);
        no_rutaplus = findViewById(R.id.no_urtplus);
        no_rutaminus = findViewById(R.id.no_urtminus);
        submit = findViewById(R.id.next);

        // Daftar Pertanyaan
        pertanyaanNoSegmen = findViewById(R.id.textview1);
        pertanyaanNoBf = findViewById(R.id.textview2);
        pertanyaanNoBs = findViewById(R.id.textview3);
        pertanyaanNoRuta = findViewById(R.id.textview4);
        pertanyaanNamaKRT = findViewById(R.id.textview5);
        pertanyaanAlamat = findViewById(R.id.textview6);
        pertanyaanJumlahART = findViewById(R.id.textview7);
        pertanyaanJumlahART10 = findViewById(R.id.textview8);
        pertanyaanNoHp = findViewById(R.id.textview9);
        pertanyaanNoHp2 = findViewById(R.id.textview9b);
        pertanyaanKodeEligible = findViewById(R.id.textview10);

        kodeUUP = getIntent().getStringExtra("kodeUUP");
        kodeBs = getIntent().getStringExtra("kodeBs");
        posisi = getIntent().getIntExtra("posisi", 0);

        noteView = findViewById(R.id.note_card);
        noSegmenTerakhir = findViewById(R.id.note_segmen);
        noBfTerakhir = findViewById(R.id.note_bf);
        noBsTerakhir = findViewById(R.id.note_bs);
        RumahTangga previousInsert = getIntent().getParcelableExtra(StaticFinal.BUNDLE_INSERT);
        setNoteContent(previousInsert);


        String nomorSegment = String.valueOf(no_segmen.getText());
        if (!nomorSegment.contains("S")) {
            no_segmen.setText("S");
            no_segmen.setSelection(1);
        }

        // SET CAPSLOCK ON EDITTEXT
        InputFilter allCaps = new InputFilter.AllCaps();
        namaKRT.setFilters(new InputFilter[]{allCaps});
        alamat.setFilters(new InputFilter[]{allCaps});
        no_bf.setFilters(new InputFilter[]{allCaps});
        no_bs.setFilters(new InputFilter[]{allCaps});
        no_segmen.setFilters(new InputFilter[]{allCaps});

        // SET NUMBER ON CLICK BUTTON
        no_bf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (no_bf.getText().length() < 3) {
                        no_bf.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        no_bf.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            }
        });
        no_bf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (no_bf.getText().length() < 3) {
                    no_bf.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    no_bf.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        no_bs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (no_bs.getText().length() < 3) {
                        no_bs.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        no_bs.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            }
        });
        no_bs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (no_bs.getText().length() < 3) {
                    no_bs.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    no_bs.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        no_segmen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("S")) {
                    if (s.length() > 0) {
                        if (s.toString().substring(1).startsWith("S")) {
                            no_segmen.setText(s.toString().substring(1));
                            no_segmen.setSelection(s.length() - 1);
                        } else {
                            no_segmen.setText("S" + s);
                            no_segmen.setSelection(s.length() + 1);
                        }
                    } else {
                        no_segmen.setText("S");
                        no_segmen.setSelection(1);
                    }
                }
            }
        });

        no_urut_ruta.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (no_urut_ruta.getText().length() < 3) {
                        no_urut_ruta.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        no_urut_ruta.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            }
        });

        no_urut_ruta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (no_urut_ruta.getText().length() < 3) {
                    no_urut_ruta.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    no_urut_ruta.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        segmenplus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int art = 0;
                setPlusMinusTen(no_segmen, plus, art);
                return true;
            }
        });

        segmenminus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int art = 0;
                setPlusMinusTen(no_segmen, minus, art);
                return true;
            }
        });

        segmenplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_segmen, plus, art);
            }
        });

        segmenminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_segmen, minus, art);
            }
        });

        bfplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_bf, plus, art);
            }
        });

        bfminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_bf, minus, art);
            }
        });

        bsplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_bs, plus, art);
            }
        });

        bsminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_bs, minus, art);
            }
        });

        no_rutaplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_urut_ruta, plus, art);
            }
        });

        no_rutaminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_urut_ruta, minus, art);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRT();
            }
        });

        if ((kodeUUP != null) && posisi == 0) {
            Log.e("update", "sini");
            prefillFormEdit(kodeBs, kodeUUP);
        } else if (posisi == 1) {
            prefillSameBs(kodeBs, kodeUUP);
//            prefillNearBf(kodeBs, kodeUUP);
//            disableEditNoUrut();
        } else {
            prefillFromLastRuta(kodeBs);
        }
    }

    /**
     * Method yang dijalankan ketika tombol submit ditekan
     */
    public void submitRT() {
        boolean isFormClear = false;
        String message = "";
        RumahTangga rumahTangga = new RumahTangga();

        lokasi = ls.getBestLoc();

        // Validasi isian listing rumah tangga
        if (no_segmen.getText().length() != 4 || no_segmen.getText().toString().indexOf("S") != 0) {
            message = "No segmen harus 4 digit dan diisi";
            pertanyaanNoSegmen.requestFocus();
            no_segmen.requestFocus();
            Log.d(TAG, "submitRT: No segmen salah");
        } else if (no_bf.getText().length() < 3) {
            message = "No urut BF harus 3 digit dan diisi";
            pertanyaanNoBf.requestFocus();
            no_bf.requestFocus();
            Log.d(TAG, "submitRT: no urut bf salah");
        } else if (no_bs.getText().length() < 3) {
            message = "No urut bs harus 3 digit dan diisi";
            pertanyaanNoBs.requestFocus();
            no_bs.requestFocus();
            Log.d(TAG, "submitRT: no urut bs salah");
        } else if (Integer.parseInt(no_bf.getText().toString().substring(0, 3)) > Integer.parseInt(no_bs.getText().toString().substring(0, 3))) {
            message = "No BF tidak boleh lebih dari No BS";
            pertanyaanNoBs.requestFocus();
            no_bs.requestFocus();
            Log.d(TAG, "submitRT: no urut bs/bf salah");
        } else if (no_urut_ruta.getText().length() < 3) {
            message = "No urut ruta harus 3 digit dan diisi";
            pertanyaanNoRuta.requestFocus();
            no_urut_ruta.requestFocus();
            Log.d(TAG, "submitRT: no urut ruta salah");
        } else if (namaKRT.getText().length() < 1) {
            message = "Nama Kepala Rumah Tangga harus diisi";
            pertanyaanNamaKRT.requestFocus();
            namaKRT.requestFocus();
            Log.d(TAG, "submitRT: nama kepala rt salah");
        } else if (alamat.getText().length() < 1) {
            message = "Alamat harus diisi";
            pertanyaanAlamat.requestFocus();
            alamat.requestFocus();
            Log.d(TAG, "submitRT: Alamat salah");
        } else if (jumlahART.getText().length() < 1) {
            message = "Jumlah Anggota Rumah Tangga harus diisi";
            pertanyaanJumlahART.requestFocus();
            jumlahART.requestFocus();
            Log.d(TAG, "submitRT: Jumlah ART salah");
        } else if (jumlahART.getText().toString().matches("[0-9]+") == false) {
            message = "Jumlah Anggota Rumah Tangga harus berupa angka";
            pertanyaanJumlahART.requestFocus();
            jumlahART.requestFocus();
            Log.d(TAG, "submitRT: Jumlah ART salah");
        } else if (jumlahART10.getText().length() < 1) {
            message = "Jumlah Anggota Rumah Tangga harus diisi";
            pertanyaanJumlahART10.requestFocus();
            jumlahART10.requestFocus();
            Log.d(TAG, "submitRT: Jumlah ART 10 tahun salah");
        } else if (jumlahART10.getText().toString().matches("[0-9]+") == false) {
            message = "Jumlah Anggota Rumah Tangga harus berupa angka";
            pertanyaanJumlahART10.requestFocus();
            jumlahART10.requestFocus();
            Log.d(TAG, "submitRT: Jumlah ART 10 tahun salah");
        } else if (Integer.parseInt(jumlahART10.getText().toString()) > Integer.parseInt(jumlahART.getText().toString())) {
            message = "Jumlah ART 10 tahun ke atas tidak boleh lebih dari jumlah ART";
            pertanyaanJumlahART10.requestFocus();
            jumlahART10.requestFocus();
            Log.d(TAG, "submitRT: Jumlah ART 10 tahun salah");
        } else if (noHp.getText().length() < 1) {
            message = "No HP harus diisi";
            pertanyaanNoHp.requestFocus();
            noHp.requestFocus();
            Log.d(TAG, "submitRT: No HP salah");
        } else if (!Pattern.compile("^(62)8[1-9][0-9]{6,10}$").matcher(noHp.getText().toString()).find()) {
            message = "Harap masukkan nomor telepon valid yang diawali dengan 62";
            pertanyaanNoHp.requestFocus();
            noHp.requestFocus();
            Log.d(TAG, "submitRT: No HP salah");
        } else if (noHp2.getText().length() > 0) {
            if (!Pattern.compile("^(62)8[1-9][0-9]{6,10}$").matcher(noHp2.getText().toString()).find()) {
                message = "Harap masukkan nomor telepon valid yang diawali dengan 62";
                pertanyaanNoHp2.requestFocus();
                noHp2.requestFocus();
                Log.d(TAG, "submitRT: No HP 2 salah");
            } else {
                isFormClear = true;
            }
        } else if (kodeEligible.getCheckedRadioButtonId() == -1) {
            message = "Pilih salah satu status eligible";
            pertanyaanKodeEligible.requestFocus();
            Log.d(TAG, "submitRT: kode eligible salah");
        } else {
            isFormClear = true;
            Log.d(TAG, "submitRT: form clear");
        }

        Log.d(TAG, "submitRT: no urut ruta " + no_urut_ruta.getText().toString() + " ,posisi " + posisi);

        // jika validasi sudah terisi dengan benar
        if (isFormClear) {
            rumahTangga.setKodeBs(kodeBs);
            rumahTangga.setNoSLS(no_segmen.getText().toString());
            rumahTangga.setBf(no_bf.getText().toString().toUpperCase());
            rumahTangga.setBs(no_bs.getText().toString());
            rumahTangga.setNoUrutRuta(no_urut_ruta.getText().toString());
            rumahTangga.setNamaKRT(namaKRT.getText().toString());
            rumahTangga.setAlamat(alamat.getText().toString());
            rumahTangga.setJumlahART(jumlahART.getText().toString());
            rumahTangga.setJumlahART10(jumlahART10.getText().toString());
            rumahTangga.setNoHp(noHp.getText().toString());
            rumahTangga.setNoHp2(noHp2.getText().toString());

            switch (kodeEligible.getCheckedRadioButtonId()) {
                case R.id.kode_eligible_pernah_bekerja:
                    rumahTangga.setKodeEligible("1");
                    break;
                case R.id.kode_eligible_sedang_bekerja:
                    rumahTangga.setKodeEligible("2");
                    break;
                case R.id.kode_eligible_sedang_dan_pernah_bekerja:
                    rumahTangga.setKodeEligible("3");
                    break;
                default:
                    rumahTangga.setKodeEligible("0");
                    break;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy kk:mm:ss");
            rumahTangga.setTime(sdf.format(new Date()));

            //TODO CEK kodeUUP setelah input
            if ((kodeUUP != null) && posisi == 0) {
                final LocationManager manager = (LocationManager) IsiRumahTanggaActivity.this.getSystemService(Context.LOCATION_SERVICE);
                RumahTangga rt = db.getRumahTanggaByKode(kodeBs, kodeUUP);
                Log.d(TAG, "updateruta");
                if (rt.getLatitude() == null || rt.getLatitude().equals("") ||
                        rt.getLongitude() == null || rt.getLongitude().equals("") ||
                        rt.getAkurasi() == null || rt.getAkurasi().equals("")) { //TODO GET LOKASI SEKALI AJA
                    Log.d(TAG, "submitRT: update lokasi");
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        rumahTangga.setStatus(rt.getStatus());
                        rumahTangga.setLatitude(String.valueOf(lokasi.getLatitude()));
                        rumahTangga.setLongitude(String.valueOf(lokasi.getLongitude()));
                        rumahTangga.setAkurasi(String.valueOf(lokasi.getAccuracy()));
                        if (db.updateUUP(rumahTangga, kodeBs, kodeUUP)) {
                            RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
                            rumahTangga.setLatitude(ruta.getLatitude());
                            rumahTangga.setLongitude(ruta.getLongitude());
                            rumahTangga.setAkurasi(ruta.getAkurasi());
                            Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga berhasil diperbaharui", Toast.LENGTH_SHORT).show();
                            ActivitySync.backupLocal(getApplicationContext());
//                            if (db.clearkanNoUUP(kodeBs)) {
//
//                            }
                            finish();
                        } else {
                            Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal diperbaharui", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(IsiRumahTanggaActivity.this);

                        builder.setTitle("Peringatan")
                                .setMessage("Pastikan GPS anda berfungsi dengan benar")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }

                rumahTangga.setStatus(rt.getStatus());
                rumahTangga.setLatitude(rt.getLatitude());
                rumahTangga.setLongitude(rt.getLongitude());
                rumahTangga.setAkurasi(rt.getAkurasi());
                if (db.updateUUP(rumahTangga, kodeBs, kodeUUP)) {
//                    RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
                    Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga berhasil diperbaharui", Toast.LENGTH_SHORT).show();
                    ActivitySync.backupLocal(getApplicationContext());
//                    if (db.clearkanNoUUP(kodeBs)) {

//                    }
                    finish();
                } else {
                    Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal diperbaharui", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (lokasi != null) {
                    rumahTangga.setLatitude(String.valueOf(lokasi.getLatitude()));
                    rumahTangga.setLongitude(String.valueOf(lokasi.getLongitude()));
                    rumahTangga.setAkurasi(String.valueOf(lokasi.getAccuracy()));
                } else {
                    rumahTangga.setLatitude("");
                    rumahTangga.setLongitude("");
                    rumahTangga.setAkurasi("");
                }

                if (db.insertUUP(rumahTangga)) {
                    Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga berhasil dimasukan", Toast.LENGTH_SHORT).show();
                    ActivitySync.backupLocal(getApplicationContext());
                    finish();
//                    if (db.clearkanNoUUP(kodeBs)) {
//
//
//                    } else {
//                        Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal dimasukan", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal dimasukan", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(IsiRumahTanggaActivity.this, message, Toast.LENGTH_SHORT).show();
        }

    }

    public void setPlusMinus1(EditText form, int x, int art) {
        int myNum = 0;
        if (form.equals(no_segmen)) {
            try {
                myNum = Integer.parseInt(form.getText().toString().substring(1));
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        } else if (form.getText().toString().equals("")) {
            myNum = 0;
        }

        try {
            int i = 1;
            while (true) {
                myNum = Integer.parseInt(form.getText().toString().substring(0, i++));
            }
        } catch (Exception e) {

        }

        if (x == 0) {
            myNum = myNum + 1;
        } else {
            if (myNum == 0) {
                myNum = 0;
            } else {
                myNum = myNum - 1;
            }
        }
        int length = String.valueOf(myNum).length();
        if (art == 0) {
            switch (length) {
                case 1:
                    form.setText("0" + String.valueOf(myNum));
                    break;
                case 2:
                    form.setText(String.valueOf(myNum));
            }
        } else {
            form.setText(String.valueOf(myNum));
        }
    }

    public void setPlusMinus(EditText form, int x, int art) {
        int myNum = 0;
        if (form.equals(no_segmen)) {
            try {
                myNum = Integer.parseInt(form.getText().toString().substring(1));
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        } else if (form.getText().toString().equals("")) {
            myNum = 0;
        }

        try {
            int i = 1;
            while (true) {
                myNum = Integer.parseInt(form.getText().toString().substring(0, i++));
            }
        } catch (Exception e) {

        }

        if (x == 0) {
            myNum = myNum + 1;
        } else {
            if (myNum == 0) {
                myNum = 0;
            } else {
                myNum = myNum - 1;
            }
        }
        int length = String.valueOf(myNum).length();
        if (art == 0) {
            switch (length) {
                case 1:
                    form.setText("00" + String.valueOf(myNum));
                    break;
                case 2:
                    form.setText("0" + String.valueOf(myNum));
                    break;
                case 3:
                    form.setText(String.valueOf(myNum));
            }
        } else {
            form.setText(String.valueOf(myNum));
        }
    }

    public void setPlusMinusTen(EditText Form, int x, int art) {
        int myNum = 0;
        if (Form.equals(no_segmen)) {
            try {
                myNum = Integer.parseInt(Form.getText().toString().substring(1));
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        } else if (Form.getText().toString().equals("")) {
            myNum = 0;
        }

        try {
            int i = 1;
            while (true) {
                myNum = Integer.parseInt(Form.getText().toString().substring(0, i++));
            }
        } catch (Exception e) {

        }

        if (x == 0) {
            myNum = myNum + 10;
        } else {
            if (myNum < 10) {
                myNum = 0;
            } else {
                myNum = myNum - 10;
            }
        }
        int length = String.valueOf(myNum).length();
        if (art == 0) {
            switch (length) {
                case 1:
                    Form.setText("00" + String.valueOf(myNum));
                    break;
                case 2:
                    Form.setText("0" + String.valueOf(myNum));
                    break;
                case 3:
                    Form.setText(String.valueOf(myNum));
            }
        } else {
            Form.setText(String.valueOf(myNum));
        }
        Form.requestFocus();
        Log.d(TAG, "setPlusMinusTen: request focus");
        Form.setSelection(Form.getText().toString().length() - 1);
        Log.d(TAG, "setPlusMinusTen: set cursor");
    }

    /**
     * Method untuk mengisi form edit
     * @param kodeBs
     * @param kodeUUP
     */
    public void prefillFormEdit(String kodeBs, String kodeUUP) {
        RumahTangga uup = db.getRumahTanggaByKode(kodeBs, kodeUUP);
//        Log.e("NOMOR SEGMEN", ruta.getNoSegmen());
        no_segmen.setText(uup.getNoSLS());
        no_bf.setText(uup.getBf());
        no_bs.setText(uup.getBs());
        no_urut_ruta.setText(uup.getNoUrutRuta());
        namaKRT.setText(uup.getNamaKRT());
        alamat.setText(uup.getAlamat());
        jumlahART.setText(uup.getJumlahART());
        jumlahART10.setText(uup.getJumlahART10());
        noHp.setText(uup.getNoHp());
        noHp2.setText(uup.getNoHp2());

        LinearLayout linKodeEligible = findViewById(R.id.lin_kode_eligible);
        linKodeEligible.setVisibility(View.VISIBLE);

        submit = findViewById(R.id.next);
        submit.setVisibility(View.VISIBLE);

        Button listingART = findViewById(R.id.kuesioner_blok5);
        listingART.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "editSavedForm", "click");
                i = new Intent(IsiRumahTanggaActivity.this,
                        InstanceChooserList.class);
                startActivity(i);
            }
        });

        RadioButton a1, a2, a3, a4;
        switch (uup.getKodeEligible()) {
            case "1":
                a1 = (RadioButton) findViewById(R.id.kode_eligible_pernah_bekerja);
                a1.setChecked(true);
                break;
            case "2":
                a2 = (RadioButton) findViewById(R.id.kode_eligible_sedang_bekerja);
                a2.setChecked(true);
                break;
            case "3":
                a3 = (RadioButton) findViewById(R.id.kode_eligible_sedang_dan_pernah_bekerja);
                a3.setChecked(true);
                break;
            default:
                a4 = (RadioButton) findViewById(R.id.kode_eligible_tidak_eligible);
                a4.setChecked(true);
                break;
        }
    }

    /**
     * Method untuk mengisi berdasarkan Ruta terakhir
     * @param kodeBs
     */
    public void prefillFromLastRuta(String kodeBs) {
        Log.d(TAG, "prefillFromLastRuta: masuk");
        int jumlahRuta = db.getListUnitUsahaPariwisata(kodeBs).size();
        Log.d(TAG, "prefillFromLastRuta: Jumlah Ruta" + String.valueOf(jumlahRuta));
        if (jumlahRuta > 0) {
//            int noBf = Integer.parseInt(lastRuta.getBf());
//            int noBs = Integer.parseInt(lastRuta.getBs());
            RumahTangga lastRuta = db.getLastRuta(kodeBs);
            String noBf = lastRuta.getBf();
            String noBs = lastRuta.getBs();
            int no_urutRuta = 0;
            try {
                no_urutRuta = Integer.parseInt(lastRuta.getNoUrutRuta());
            } catch (Exception e) {
            }

            if (no_urutRuta + 1 < 10) {
                no_urut_ruta.setText(("00" + (no_urutRuta + 1)));
            } else if (no_urutRuta + 1 < 100) { //TADI noBf
                no_urut_ruta.setText(("0" + (no_urutRuta + 1)));
            } else {
                no_urut_ruta.setText(String.valueOf((no_urutRuta + 1)));
            }

            int no_urutUP = 0;
            try {
//                no_urutUP = Integer.parseInt(lastRuta.getNoUrutUUP());
            } catch (Exception e) {
            }

            //no segmen
            no_segmen.setText(lastRuta.getNoSLS());
            //no bf
            no_bf.setText(noBf);
            //no bs
            no_bs.setText(noBs);
        }
    }

    /**
     * Method untuk prefill salin isian Ruta
     * @param kodeBs
     * @param kodeUUP
     */
    public void prefillSameBs(String kodeBs, String kodeUUP) {
        RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        RumahTangga lastRuta = db.getLastRuta(kodeBs);

        no_segmen.setText(ruta.getNoSLS());
        no_bf.setText(ruta.getBf());
        no_bs.setText(ruta.getBs());
        no_urut_ruta.setText(ruta.getNoUrutRuta());
        namaKRT.setText(ruta.getNamaKRT());
        alamat.setText(ruta.getAlamat());
        jumlahART.setText(ruta.getJumlahART());
        jumlahART10.setText(ruta.getJumlahART10());
        noHp.setText(ruta.getNoHp());
        noHp2.setText(ruta.getNoHp2());

        RadioButton a1, a2, a3, a4;
        switch (ruta.getKodeEligible()) {
            case "1":
                a1 = (RadioButton) findViewById(R.id.kode_eligible_pernah_bekerja);
                a1.setChecked(true);
                break;
            case "2":
                a2 = (RadioButton) findViewById(R.id.kode_eligible_sedang_bekerja);
                a2.setChecked(true);
                break;
            case "3":
                a3 = (RadioButton) findViewById(R.id.kode_eligible_sedang_dan_pernah_bekerja);
                a3.setChecked(true);
                break;
            default:
                a4 = (RadioButton) findViewById(R.id.kode_eligible_tidak_eligible);
                a4.setChecked(true);
                break;
        }
    }

    public void prefillNearBf(String kodeBs, String kodeUUP) {
        RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        RumahTangga lastRuta = db.getLastRuta(kodeBs);
        int noBs = Integer.parseInt(lastRuta.getBs());

        no_bf.setText(incBf());
        no_bs.setText(incBs());

//        if (noBs + 1 < 10) {
//            no_bs.setText(("00" + (noBs + 1)));
//        } else if (noBs + 1 < 100) {
//            no_bs.setText(("0" + (noBs + 1)));
//        } else {
//            no_bs.setText(String.valueOf((noBs + 1)));
//        }

//        no_bs.setText(lastRua.getBs());

        no_segmen.setText(ruta.getNoSLS());

        alamat.setText(ruta.getAlamat());
    }

    public void prefillSameBf(String kodeBs, String kodeUUP) {
        RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        RumahTangga lastRuta = db.getLastRuta(kodeBs);
//        int noBf = Integer.parseInt(ruta.getBf());
        int noBs = Integer.parseInt(lastRuta.getBs());

//        if (noBs < 10) {
//            no_bs.setText(("00" + (noBs + 1)));
//        } else if (noBs < 100) {
//            no_bs.setText(("0" + (noBs + 1)));
//        } else {
//            no_bs.setText(String.valueOf((noBs + 1)));
//        }
        no_bs.setText(incBs());

        no_bf.setText(ruta.getBf());

        no_segmen.setText(ruta.getNoSLS());

        alamat.setText(ruta.getAlamat());
    }

    public void prefillMenuForm(String kodeBs, String kodeUUP, int posisi) {
        RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);

        if (posisi == 1) {
            Log.d(TAG, "prefillMenuForm: " + ruta.getBf());
            int bf = Integer.parseInt(ruta.getBf());
            Log.d(TAG, "prefillMenuForm: " + bf);
            int s = bf + 1;
            if (String.valueOf(s).length() == 1) {
                no_bf.setText(("00" + s));
            } else if (String.valueOf(s).length() == 2) {
                no_bf.setText(("0" + s));
            } else {
                no_bf.setText(String.valueOf(s));
            }
            no_segmen.setText(ruta.getNoSLS());
            alamat.setText(ruta.getAlamat());
        } else if (posisi == 2) {
            no_bf.setText(ruta.getBf());
            no_segmen.setText(ruta.getNoSLS());
            alamat.setText(ruta.getAlamat());
        } else {
            no_bs.setText(ruta.getBs());
            no_segmen.setText(ruta.getNoSLS());
            alamat.setText(ruta.getAlamat());
        }
    }

    public String incBf() {
        DatabaseSampling db = DatabaseSampling.getInstance();
        RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        String noBf = ruta.getBf();

        while (db.isNoBfExist(kodeBs, noBf)) {
            if (noBf.length() == 3) {
                noBf = noBf + "A";
            } else {
                noBf = noBf.substring(0, 3) + tambahKode(noBf.substring(3));
            }
        }

        return noBf;
    }

    public String incBs() {
        DatabaseSampling db = DatabaseSampling.getInstance();
        RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        String noBs = ruta.getBs();

        while (db.isNoBfExist(kodeBs, noBs)) {
            if (noBs.length() == 3) {
                noBs = noBs + "A";
            } else {
                noBs = noBs.substring(0, 3) + tambahKode(noBs.substring(3));
            }
        }

        return noBs;
    }

    String tambahKode(String kt) {
        int n = kt.length();
        if (n == 0) {
            return "A";
        }

        char[] kode = new char[n];

        for (int i = 0; i < n; i++) {
            kode[i] = kt.charAt(i);
        }

        if (kode[n - 1] == 'Z') {
            kode[n - 1] = 'A';
            if (n == 0) {
                return String.valueOf(kode) + "A";
            } else {
                tambahKode(kt.substring(0, n - 1));
            }
        } else {
            kode[n - 1]++;
        }
        return String.valueOf(kode);
    }

    public String ubahNomor(int nomor) {
        int length = String.valueOf(nomor).length();
        String hasil = "";
        switch (length) {
            case 1:
                hasil = "00" + String.valueOf(nomor);
                break;
            case 2:
                hasil = "0" + String.valueOf(nomor);
                break;
            case 3:
                hasil = String.valueOf(nomor);
        }
        return hasil;
    }


    void disableEditNoUrut() {
        no_bf.setEnabled(false);
        no_bs.setEnabled(false);
        no_segmen.setEnabled(false);

        bfminus.setEnabled(false);
        bfplus.setEnabled(false);

        bsminus.setEnabled(false);
        bsplus.setEnabled(false);

        segmenminus.setEnabled(false);
        segmenplus.setEnabled(false);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ls = LocationService.getLocationManager(getApplicationContext());

    }

    @Override
    protected void onStop() {
        super.onStop();
        ls.stop(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void setNoteContent(RumahTangga rumahTangga) {
        if (rumahTangga == null) {
            noteView.setVisibility(View.GONE);
        } else {
            String noSegmen = TextColor.change(this, rumahTangga.getNoSLS(), R.color.colorPrimary);
            String noBf = TextColor.change(this, rumahTangga.getBf(), R.color.colorPrimary);
            String noBs = TextColor.change(this, rumahTangga.getBs(), R.color.colorPrimary);
            noSegmenTerakhir.setText(Html.fromHtml(String.format("%s: %s", "No Segmen", noSegmen)));
            noBfTerakhir.setText(Html.fromHtml(String.format("%s: %s", "No BF", noBf)));
            noBsTerakhir.setText(Html.fromHtml(String.format("%s: %s", "No BS", noBs)));
        }
    }
}
