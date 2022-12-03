package org.odk.collect.android.pkl.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.UnitUsahaPariwisata;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.util.LocationService;
import org.odk.collect.android.pkl.util.TextColor;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class IsiRumahTanggaActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = "ISI RUTA";
    DatabaseSampling db;
    String kodeBs, kodeUUP;
    int posisi = 0, plus = 0, minus = 1;
    LocationService ls;

    EditText no_segmen, no_bf, no_bs, no_urt, no_urut, namaUUP, alamat, namaPemilik, no_hp;
    RadioGroup isPariwisata, isPandemi, jenisUUP;
    RadioButton rbyes, rbno, rbyesPandemi, rbnoPandemi;
    Button submit, segmenplus, segmenminus, bfplus, bfminus, bsplus, bsminus, no_urtplus, no_urtminus, uupplus, uupminus;
    TextView pertanyaanNoSegmen, pertanyaanNoBf, pertanyaanNoBs, pertanyaanNoURT, pertanyaanNoUrut, pertanyaanNamaUUP,
            pertanyaanAlamat, pertanyaanNamaPemilik, pertanyaanIsPariwisata, pertanyaanJenisUUP, pertanyaanNoHp;
    View noteView;
    LinearLayout lin_rtup_tp, lin_nortup_tp, lin_ciri_fisik;
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
        no_segmen = findViewById(R.id.no_urut_segmen);
        no_bf = findViewById(R.id.no_urut_bf);
        no_bs = findViewById(R.id.no_urut_bs);
        no_urt = findViewById(R.id.no_urt);
        no_urut = findViewById(R.id.no_urut_ruta);
        namaUUP = findViewById(R.id.nama_uup);
        namaPemilik = findViewById(R.id.nama_pemilik);
        no_hp = findViewById(R.id.nohp);
        alamat = findViewById(R.id.alamat);
        isPariwisata = findViewById(R.id.kategori_uup_yesno);
        rbyes = findViewById(R.id.kategori_uup_yes);
        rbno = findViewById(R.id.kategori_uup_no);
        isPandemi = findViewById(R.id.kategori_pandemi_yesno);
        rbyesPandemi = findViewById(R.id.kategori_pandemi_yes);
        rbnoPandemi = findViewById(R.id.kategori_pandemi_no);
        jenisUUP = findViewById(R.id.kategori_uup);
        segmenplus = findViewById(R.id.segmenplus);
        segmenminus = findViewById(R.id.segmenminus);
        bfplus = findViewById(R.id.bfplus);
        bfminus = findViewById(R.id.bfminus);
        bsplus = findViewById(R.id.bsplus);
        bsminus = findViewById(R.id.bsminus);
        no_urtplus = findViewById(R.id.no_urtplus);
        no_urtminus = findViewById(R.id.no_urtminus);
        uupplus = findViewById(R.id.rutaplus);
        uupminus = findViewById(R.id.rutaminus);
        submit = findViewById(R.id.next);

        pertanyaanNoSegmen = findViewById(R.id.textview1);
        pertanyaanNoBf = findViewById(R.id.textview2);
        pertanyaanNoBs = findViewById(R.id.textview3);
        pertanyaanNoURT = findViewById(R.id.textview15);
        pertanyaanNoUrut = findViewById(R.id.textview4);
        pertanyaanNamaUUP = findViewById(R.id.textview5);
        pertanyaanAlamat = findViewById(R.id.textview7);
        pertanyaanIsPariwisata = findViewById(R.id.textview13);
        pertanyaanNamaPemilik = findViewById(R.id.textview10);
        pertanyaanJenisUUP = findViewById(R.id.textview9);
        pertanyaanNoHp = findViewById(R.id.textview11);

        kodeUUP = getIntent().getStringExtra("kodeUUP");
        kodeBs = getIntent().getStringExtra("kodeBs");
        posisi = getIntent().getIntExtra("posisi", 0);

//        Log.e("update1", kodeUUP);
//        Log.e("update1", String.valueOf(posisi));

        noteView = findViewById(R.id.note_card);
        noSegmenTerakhir = findViewById(R.id.note_segmen);
        noBfTerakhir = findViewById(R.id.note_bf);
        noBsTerakhir = findViewById(R.id.note_bs);
        UnitUsahaPariwisata previousInsert = getIntent().getParcelableExtra(StaticFinal.BUNDLE_INSERT);
        setNoteContent(previousInsert);


        String nomorSegment = String.valueOf(no_segmen.getText());
        if(!nomorSegment.contains("S")){
            no_segmen.setText("S");
            no_segmen.setSelection(1);
        }


        InputFilter allCaps = new InputFilter.AllCaps();

        namaPemilik.setFilters(new InputFilter[]{allCaps});
        namaUUP.setFilters(new InputFilter[]{allCaps});
        alamat.setFilters(new InputFilter[]{allCaps});
        no_bf.setFilters(new InputFilter[]{allCaps});

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

        no_urtplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_urt, plus, art);
            }
        });

        no_urtminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_urt, minus, art);
            }
        });

        uupplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_urut, plus, art);
            }
        });

        uupminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int art = 0;
                setPlusMinus(no_urut, minus, art);
            }
        });

        LinearLayout llpandemi = (LinearLayout) findViewById(R.id.lin_uup_pandemi);
        LinearLayout lljenis = (LinearLayout) findViewById(R.id.lin_rtup_tp);
        LinearLayout llpemilik = (LinearLayout) findViewById(R.id.lin_nama_pemilik);
        LinearLayout llnortup = (LinearLayout) findViewById(R.id.lin_no_ruta);
        LinearLayout llnohp = (LinearLayout) findViewById(R.id.lin_nohp);

        llpandemi.setVisibility(View.GONE);
        lljenis.setVisibility(View.GONE);
        llpemilik.setVisibility(View.GONE);
        llnortup.setVisibility(View.GONE);
        llnohp.setVisibility(View.GONE);

        isPariwisata.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbyes.isChecked() == true){
                    llpandemi.setVisibility(View.VISIBLE);
                }else{
                    llpandemi.setVisibility(View.GONE);
                    lljenis.setVisibility(View.GONE);
                    llpemilik.setVisibility(View.GONE);
                    llnortup.setVisibility(View.GONE);
                    llnohp.setVisibility(View.GONE);
                }
            }
        });
        isPandemi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbyesPandemi.isChecked() == true){
                    lljenis.setVisibility(View.VISIBLE);
                    llpemilik.setVisibility(View.VISIBLE);
                    llnortup.setVisibility(View.VISIBLE);
                    llnohp.setVisibility(View.VISIBLE);
                }else{
                    lljenis.setVisibility(View.GONE);
                    llpemilik.setVisibility(View.GONE);
                    llnortup.setVisibility(View.GONE);
                    llnohp.setVisibility(View.GONE);
                }
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

    public void submitRT() {
        boolean isFormClear = false;
        String message = "";
        UnitUsahaPariwisata unitUsahaPariwisata = new UnitUsahaPariwisata();

        lokasi = ls.getBestLoc();

//        RumahTanggaRiset4 rTangga = db.getRumahTanggaByKode(kodeBs, kodeUUP);

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
        }else if (no_urt.getText().length() < 3) {
            message = "No urut ruta harus 3 digit dan diisi";
            pertanyaanNoURT.requestFocus();
            no_urt.requestFocus();
            Log.d(TAG, "submitRT: no urut ruta salah");
        } else if (namaUUP.getText().length() < 1) {
            message = "Nama Kepala Rumah Tangga harus diisi";
            pertanyaanNamaUUP.requestFocus();
            namaUUP.requestFocus();
            Log.d(TAG, "submitRT: nama kepala rt salah");
        } else if (alamat.getText().length() < 1) {
            message = "Alamat harus diisi";
            pertanyaanAlamat.requestFocus();
            alamat.requestFocus();
            Log.d(TAG, "submitRT: Alamat salah");
        } else if (alamat.getText().length()>200) {
            message = "RT/RW tidak boleh lebih dari 200 karakter"; //CEK
            pertanyaanAlamat.requestFocus();
            alamat.requestFocus();
            Log.d(TAG, "submitRT: RT/RW salah");
        } else if (!Pattern.compile("^[0-9]{3}/[0-9]{3}$").matcher(alamat.getText().toString()).find()) {
            message = "RT/RW tidak sesuai dengan format, cek kembali"; //CEK
            pertanyaanAlamat.requestFocus();
            alamat.requestFocus();
            Log.d(TAG, "submitRT: RT/RW salah");
        } else if (isPariwisata.getCheckedRadioButtonId() == -1) {
            message = "Apakah unit usaha pariwisata? harus diisi";
            pertanyaanIsPariwisata.requestFocus();
            Log.d(TAG, "submitRT: isPariwisata salah");
        } else if (isPariwisata.getCheckedRadioButtonId() == rbyes.getId()) {
            if(isPandemi.getCheckedRadioButtonId() == rbyesPandemi.getId()) {
                if (jenisUUP.getCheckedRadioButtonId() == -1) {
                    message = "Jenis unit usaha pariwisata harus diisi";
                    pertanyaanJenisUUP.requestFocus();
                    Log.d(TAG, "submitRT: jenisUUP salah");
                } else if (namaPemilik.getText().length() < 1) {
                    message = "Pemilik unit usaha pariwisata harus diisi";
                    pertanyaanNamaPemilik.requestFocus();
                    namaPemilik.requestFocus();
                    Log.d(TAG, "submitRT: nama pemilik salah");
                } else if (no_urut.getText().length() < 1) {
                    message = "No Urut Ruta Usaha Pariwisata harus diisi";
                    pertanyaanNoUrut.requestFocus();
                    no_urut.requestFocus();
                    Log.d(TAG, "submitRT: no_ruta salah");
                } else if (no_hp.getText().length() < 1) {
                    message = "No HP harus diisi";
                    pertanyaanNoHp.requestFocus();
                    no_hp.requestFocus();
                    Log.d(TAG, "submitRT: no hp salah");
                } else if (!Pattern.compile("^(62)8[1-9][0-9]{6,9}$").matcher(no_hp.getText().toString()).find()) {
                    message = "No HP tidak sesuai dengan format, cek kembali"; //CEK
                    pertanyaanNoHp.requestFocus();
                    no_hp.requestFocus();
                    Log.d(TAG, "submitRT: no hp salah");
                }else {
                    isFormClear = true;
                    Log.d(TAG, "submitRT: form clear");
                }
            }else {
                isFormClear = true;
                Log.d(TAG, "submitRT: form clear");
            }
        } else {
            isFormClear = true;
            Log.d(TAG, "submitRT: form clear");
        }

        Log.d(TAG, "submitRT: no urut ruta " + no_urut.getText().toString() + " ,posisi " + posisi);

        //TODO FUNGSI Noruta Usaha Pariwisata UNIK
        ArrayList<UnitUsahaPariwisata> listRutaUP = db.getListUnitUsahaPariwisata(kodeBs);
        for (int i = 0; i<listRutaUP.size() ; i++){
            if(no_urut.getText().toString().equals(listRutaUP.get(i).getNoUrutRuta()) && posisi != 0){
                if(no_urut.getText().toString().equals("000")){
                    isFormClear = true;
                }else{
                    message = "No Urut Ruta Usaha Pariwisata harus harus unik";
                    pertanyaanNoUrut.requestFocus();
                    no_urut.requestFocus();
                    isFormClear = false;
                    Log.d(TAG, "submitRT: No Urut Ruta Usaha Pariwisata salah");
                }
            }
            Log.d(TAG, "submitRT: UNIK!");
        }

        //TODO FUNGSI Noruta UNIK
        ArrayList<UnitUsahaPariwisata> listRuta = db.getListUnitUsahaPariwisata(kodeBs);
        for (int i = 0; i<listRuta.size() ; i++){
            if(no_urt.getText().toString().equals(listRuta.get(i).getNoUrutRuta()) && posisi != 0){
                if(no_urt.getText().toString().equals("000")){
                    isFormClear = true;
                }else{
                    message = "No Urut Ruta harus harus unik";
                    pertanyaanNoURT.requestFocus();
                    no_urt.requestFocus();
                    isFormClear = false;
                    Log.d(TAG, "submitRT: No Urut Ruta salah");
                }
            }
            Log.d(TAG, "submitRT: UNIK!");
        }

        if (isFormClear) {
            unitUsahaPariwisata.setKodeBs(kodeBs);
            unitUsahaPariwisata.setNoSegmen(no_segmen.getText().toString());
            unitUsahaPariwisata.setBf(no_bf.getText().toString().toUpperCase());
            unitUsahaPariwisata.setBs(no_bs.getText().toString());
            unitUsahaPariwisata.setNoUrutRuta(no_urt.getText().toString());
            unitUsahaPariwisata.setNoUrutUUP(no_urut.getText().toString());
            unitUsahaPariwisata.setNamaUUP(namaUUP.getText().toString());
            unitUsahaPariwisata.setAlamat(alamat.getText().toString());
            if(isPariwisata.getCheckedRadioButtonId() == R.id.kategori_uup_yes) {
                unitUsahaPariwisata.setIsPariwisata("1");
                if(isPandemi.getCheckedRadioButtonId() == R.id.kategori_pandemi_yes) {
                    unitUsahaPariwisata.setIsPandemi("1");
                    unitUsahaPariwisata.setNamaPemilikUUP(namaPemilik.getText().toString());
                    if(jenisUUP.getCheckedRadioButtonId() == R.id.kategori_uup_1) unitUsahaPariwisata.setJenisUUP("1");
                    else if(jenisUUP.getCheckedRadioButtonId() == R.id.kategori_uup_2) unitUsahaPariwisata.setJenisUUP("2");
                    else if(jenisUUP.getCheckedRadioButtonId() == R.id.kategori_uup_3) unitUsahaPariwisata.setJenisUUP("3");
                    else if(jenisUUP.getCheckedRadioButtonId() == R.id.kategori_uup_4) unitUsahaPariwisata.setJenisUUP("4");
                    else unitUsahaPariwisata.setJenisUUP("");
                    unitUsahaPariwisata.setNoHp(no_hp.getText().toString());
                }
                else if(isPandemi.getCheckedRadioButtonId() == R.id.kategori_pandemi_no) {
                    unitUsahaPariwisata.setIsPandemi("0");
                    unitUsahaPariwisata.setNamaPemilikUUP("");
                    unitUsahaPariwisata.setJenisUUP("");
                    unitUsahaPariwisata.setNoHp("");
                } else unitUsahaPariwisata.setIsPandemi("");
            }
            else if(isPariwisata.getCheckedRadioButtonId() == R.id.kategori_uup_no) {
                unitUsahaPariwisata.setIsPariwisata("0");
                unitUsahaPariwisata.setIsPandemi("");
                if(isPandemi.getCheckedRadioButtonId() == R.id.kategori_pandemi_yes) {
                    unitUsahaPariwisata.setIsPandemi("");
                    unitUsahaPariwisata.setNamaPemilikUUP("");
                    unitUsahaPariwisata.setJenisUUP("");
                    unitUsahaPariwisata.setNoHp("");
                } else {
                    unitUsahaPariwisata.setIsPandemi("");
                    unitUsahaPariwisata.setNamaPemilikUUP("");
                    unitUsahaPariwisata.setJenisUUP("");
                    unitUsahaPariwisata.setNoHp("");
                }
            } else unitUsahaPariwisata.setIsPariwisata("");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy kk:mm:ss");
            unitUsahaPariwisata.setTime(sdf.format(new Date()));

            int jumlahruta = 1 + db.getJumlahUUP(kodeBs);

            no_urt.setText("001");
            no_urut.setText("001");
            if (kodeUUP != null && posisi == 0) {
                if(no_urt.getText().toString().equals("000")){
                    unitUsahaPariwisata.setNoUrutRuta("000");
                }else{
//                    RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
//                    rumahTanggaRiset4.setNoUrutRuta(no_urt.getText().toString());
                }
            } else{
//                rumahTangga.setStatus(RumahTangga.STATUS_INSERT);
                if(no_urt.getText().toString().equals("000")){
                    unitUsahaPariwisata.setNoUrutRuta("000");
                }else{
                    unitUsahaPariwisata.setNoUrutRuta(ubahNomor(jumlahruta));
                }
            }
            Log.i(TAG, "submitRT: norut " + unitUsahaPariwisata.getNoUrutRuta());



            //TODO CEK kodeUUP setelah input
            if ((kodeUUP != null) && posisi == 0) {
                final LocationManager manager = (LocationManager) IsiRumahTanggaActivity.this.getSystemService(Context.LOCATION_SERVICE );
                UnitUsahaPariwisata rt = db.getRumahTanggaByKode(kodeBs, kodeUUP);
                Log.d(TAG, "updateruta");
                if(rt.getLatitude()==null || rt.getLatitude().equals("") ||
                        rt.getLongitude()==null || rt.getLongitude().equals("") ||
                        rt.getAkurasi()==null  || rt.getAkurasi().equals("") ){ //TODO GET LOKASI SEKALI AJA
                    Log.d(TAG, "submitRT: update lokasi");
                    if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
                        unitUsahaPariwisata.setStatus(rt.getStatus());
                        unitUsahaPariwisata.setLatitude(String.valueOf(lokasi.getLatitude()));
                        unitUsahaPariwisata.setLongitude(String.valueOf(lokasi.getLongitude()));
                        unitUsahaPariwisata.setAkurasi(String.valueOf(lokasi.getAccuracy()));
                        if (db.updateUUP(unitUsahaPariwisata, kodeBs, kodeUUP)) {
                            UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
                            unitUsahaPariwisata.setLatitude(ruta.getLatitude());
                            unitUsahaPariwisata.setLongitude(ruta.getLongitude());
                            unitUsahaPariwisata.setAkurasi(ruta.getAkurasi());
                            if (db.clearkanNoUUP(kodeBs)) {
                                Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga berhasil diperbaharui", Toast.LENGTH_SHORT).show();
                                ActivitySync.backupLocal(getApplicationContext());
                            }
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

                unitUsahaPariwisata.setStatus(rt.getStatus());
                unitUsahaPariwisata.setLatitude(rt.getLatitude());
                unitUsahaPariwisata.setLongitude(rt.getLongitude());
                unitUsahaPariwisata.setAkurasi(rt.getAkurasi());
                if (db.updateUUP(unitUsahaPariwisata, kodeBs, kodeUUP)) {
//                    RumahTangga ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
                    if (db.clearkanNoUUP(kodeBs)) {
                        Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga berhasil diperbaharui", Toast.LENGTH_SHORT).show();
                        ActivitySync.backupLocal(getApplicationContext());
                    }
                    finish();
                } else {
                    Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal diperbaharui", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (lokasi != null) {
                    unitUsahaPariwisata.setLatitude(String.valueOf(lokasi.getLatitude()));
                    unitUsahaPariwisata.setLongitude(String.valueOf(lokasi.getLongitude()));
                    unitUsahaPariwisata.setAkurasi(String.valueOf(lokasi.getAccuracy()));
                } else {
                    unitUsahaPariwisata.setLatitude("");
                    unitUsahaPariwisata.setLongitude("");
                    unitUsahaPariwisata.setAkurasi("");
                }

                if (db.insertUUP(unitUsahaPariwisata)) {
                    if (db.clearkanNoUUP(kodeBs)) {
                        Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga berhasil dimasukan", Toast.LENGTH_SHORT).show();
                        ActivitySync.backupLocal(getApplicationContext());
                        finish();
                    } else {
                        Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal dimasukan", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(IsiRumahTanggaActivity.this, "Rumah Tangga gagal dimasukan", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(IsiRumahTanggaActivity.this, message, Toast.LENGTH_SHORT).show();
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

    public void prefillFormEdit(String kodeBs, String kodeUUP) {
        UnitUsahaPariwisata uup = db.getRumahTanggaByKode(kodeBs, kodeUUP);
//        Log.e("NOMOR SEGMEN", ruta.getNoSegmen());
        no_segmen.setText(uup.getNoSegmen());
        no_bf.setText(uup.getBf());
        no_bs.setText(uup.getBs());
        no_urt.setText(uup.getNoUrutRuta());
        no_urut.setText(uup.getNoUrutUUP());
        namaUUP.setText(uup.getNamaUUP());
        alamat.setText(uup.getAlamat());

        RadioButton a1,a2;
        if(uup.getIsPariwisata().equals("1")) {
            a1 = isPariwisata.findViewById(R.id.kategori_uup_yes);
            a1.setChecked(true);
        }
        else if(uup.getIsPariwisata().equals("0")){
            a2 = isPariwisata.findViewById(R.id.kategori_uup_no);
            a2.setChecked(true);
        }

        RadioButton b1,b2;
        if(uup.getIsPandemi().equals("1")) {
            b1 = isPandemi.findViewById(R.id.kategori_pandemi_yes);
            b1.setChecked(true);
        }
        else if(uup.getIsPandemi().equals("0")){
            b2 = isPandemi.findViewById(R.id.kategori_pandemi_no);
            b2.setChecked(true);
        }

        namaPemilik.setText(uup.getNamaPemilikUUP());
        no_hp.setText(uup.getNoHp());
        RadioButton c1,c2,c3,c4;

        if(uup.getJenisUUP().equals("1")) {
            c1 = jenisUUP.findViewById(R.id.kategori_uup_1);
            c1.setChecked(true);
        }
        else if(uup.getJenisUUP().equals("2")){
            c2 = jenisUUP.findViewById(R.id.kategori_uup_2);
            c2.setChecked(true);
        }
        else if(uup.getJenisUUP().equals("3")){
            c3 = jenisUUP.findViewById(R.id.kategori_uup_3);
            c3.setChecked(true);
        }
        else if(uup.getJenisUUP().equals("4")){
            c4 = jenisUUP.findViewById(R.id.kategori_uup_4);
            c4.setChecked(true);
        }

    }

    public void prefillFromLastRuta(String kodeBs) {
        Log.d(TAG, "prefillFromLastRuta: masuk");
        int jumlahRuta = db.getListUnitUsahaPariwisata(kodeBs).size();
        Log.d(TAG, "prefillFromLastRuta: Jumlah Ruta" + String.valueOf(jumlahRuta));
        if (jumlahRuta > 0) {
//            int noBf = Integer.parseInt(lastRuta.getBf());
//            int noBs = Integer.parseInt(lastRuta.getBs());
            UnitUsahaPariwisata lastRuta = db.getLastRuta(kodeBs);
            String noBf = lastRuta.getBf();
            String noBs = lastRuta.getBs();
            int no_urutRuta = 0;
            try {
                no_urutRuta = Integer.parseInt(lastRuta.getNoUrutRuta());
            } catch (Exception e) {
            }
//
//            no_bf.setText(incBf());
//            no_bs.setText( incBs() );

//            if (noBf + 1 < 10) {
//                no_bf.setText(("00" + (noBf + 1)));
//            } else if (noBf + 1 < 100) {
//                no_bf.setText(("0" + (noBf + 1)));
//            } else {
//                no_bf.setText(String.valueOf((noBf + 1)));
//            }
//
//            if (noBs + 1 < 10) {
//                no_bs.setText(("00" + (noBs + 1)));
//            } else if (noBf + 1 < 100) {
//                no_bs.setText(("0" + (noBs + 1)));
//            } else {
//                no_bs.setText(String.valueOf((noBs + 1)));
//            }

            if (no_urutRuta + 1 < 10) {
                no_urt.setText(("00" + (no_urutRuta + 1)));
            } else if (no_urutRuta + 1 < 100) { //TADI noBf
                no_urt.setText(("0" + (no_urutRuta + 1)));
            } else {
                no_urt.setText(String.valueOf((no_urutRuta + 1)));
            }

            int no_urutUP = 0;
            try {
                no_urutUP = Integer.parseInt(lastRuta.getNoUrutUUP());
            } catch (Exception e) {
            }
            String apakahPariwisata = lastRuta.getIsPariwisata();
            String apakahPandemi = lastRuta.getIsPandemi();
            if(apakahPariwisata.equals("1")){
                if(apakahPandemi.equals("1")){
                    if (no_urutUP + 1 < 10) {
                        no_urut.setText(("00" + (no_urutUP + 1)));
                    } else if (no_urutUP + 1 < 100) { //TADI noBf
                        no_urut.setText(("0" + (no_urutUP + 1)));
                    } else {
                        no_urut.setText(String.valueOf((no_urutUP + 1)));
                    }
                } else {
                    no_urut.setText(lastRuta.getNoUrutUUP());
                }
            } else {
                no_urut.setText(lastRuta.getNoUrutUUP());
            }

//            int no_urutUP = 0;
//            try {
//                no_urutUP = Integer.parseInt(lastRuta.getNoUrutUUP());
//            } catch (Exception e) {
//            }
//            String jenisUUP = lastRuta.getJenisUUP();
//            if(jenisUUP == "1"){
//                if (no_urutUP + 1 < 10) {
//                    no_urut.setText(("00" + (no_urutUP + 1)));
//                } else if (no_urutUP + 1 < 100) { //TADI noBf
//                    no_urut.setText(("0" + (no_urutUP + 1)));
//                } else {
//                    no_urut.setText(String.valueOf((no_urutUP + 1)));
//                }
//            }

            //no segmen
            no_segmen.setText(lastRuta.getNoSegmen());

            //no bf
            no_bf.setText(noBf);

            //no bs
            no_bs.setText(noBs);

            //RT/RW
            alamat.setText(lastRuta.getAlamat());
//
//            //No Urut UUP
//            no_urut.setText(lastRuta.getNoUrutUUP());
        }
    }

    public void prefillSameBs(String kodeBs, String kodeUUP) {
        UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);

        no_bf.setText(ruta.getBf());

        no_bs.setText(ruta.getBs());

        no_segmen.setText(ruta.getNoSegmen());

        alamat.setText(ruta.getAlamat());
    }

    public void prefillNearBf(String kodeBs, String kodeUUP) {
        UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        UnitUsahaPariwisata lastRuta = db.getLastRuta(kodeBs);
        int noBs = Integer.parseInt(lastRuta.getBs());

        no_bf.setText(incBf());
        no_bs.setText( incBs() );

//        if (noBs + 1 < 10) {
//            no_bs.setText(("00" + (noBs + 1)));
//        } else if (noBs + 1 < 100) {
//            no_bs.setText(("0" + (noBs + 1)));
//        } else {
//            no_bs.setText(String.valueOf((noBs + 1)));
//        }

//        no_bs.setText(lastRua.getBs());

        no_segmen.setText(ruta.getNoSegmen());

        alamat.setText(ruta.getAlamat());
    }

    public void prefillSameBf(String kodeBs, String kodeUUP) {
        UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
        UnitUsahaPariwisata lastRuta = db.getLastRuta(kodeBs);
//        int noBf = Integer.parseInt(ruta.getBf());
        int noBs = Integer.parseInt(lastRuta.getBs());

//        if (noBs < 10) {
//            no_bs.setText(("00" + (noBs + 1)));
//        } else if (noBs < 100) {
//            no_bs.setText(("0" + (noBs + 1)));
//        } else {
//            no_bs.setText(String.valueOf((noBs + 1)));
//        }
        no_bs.setText( incBs() );

        no_bf.setText(ruta.getBf());

        no_segmen.setText(ruta.getNoSegmen());

        alamat.setText(ruta.getAlamat());
    }

    public void prefillMenuForm(String kodeBs, String kodeUUP, int posisi) {
        UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);

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
            no_segmen.setText(ruta.getNoSegmen());
            alamat.setText(ruta.getAlamat());
        } else if (posisi == 2) {
            no_bf.setText(ruta.getBf());
            no_segmen.setText(ruta.getNoSegmen());
            alamat.setText(ruta.getAlamat());
        } else {
            no_bs.setText(ruta.getBs());
            no_segmen.setText(ruta.getNoSegmen());
            alamat.setText(ruta.getAlamat());
        }
    }

    public String incBf() {
        DatabaseSampling db = DatabaseSampling.getInstance();
        UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
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
        UnitUsahaPariwisata ruta = db.getRumahTanggaByKode(kodeBs, kodeUUP);
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

    private void setNoteContent(UnitUsahaPariwisata unitUsahaPariwisata) {
        if (unitUsahaPariwisata == null) {
            noteView.setVisibility(View.GONE);
        } else {
            String noSegmen = TextColor.change(this, unitUsahaPariwisata.getNoSegmen(), R.color.colorPrimary);
            String noBf = TextColor.change(this, unitUsahaPariwisata.getBf(), R.color.colorPrimary);
            String noBs = TextColor.change(this, unitUsahaPariwisata.getBs(), R.color.colorPrimary);
            noSegmenTerakhir.setText(Html.fromHtml(String.format("%s: %s", "No Segmen", noSegmen)));
            noBfTerakhir.setText(Html.fromHtml(String.format("%s: %s", "No BF", noBf)));
            noBsTerakhir.setText(Html.fromHtml(String.format("%s: %s", "No BS", noBs)));
        }
    }
}
