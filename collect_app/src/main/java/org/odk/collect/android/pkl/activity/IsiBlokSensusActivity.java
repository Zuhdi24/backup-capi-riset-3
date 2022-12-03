package org.odk.collect.android.pkl.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.util.DaerahAdministrasi;

public class IsiBlokSensusActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    DatabaseSampling db;
    EditText no_bs, sls_rt_rw, sls_dusun;
    Button submit;
    Spinner spinner_kabupaten, spinner_kec, spinner_desa;
    TextView provinsi, kabupaten, kecamatan, desa;
    Toolbar toolbar;
    RadioGroup stratifikasi;
    RadioButton klasifikasi;
    int pos, idBs;
    String kodeBs;

    final String[] kosong = new String[]{""};
    String[] listkabupaten;
    String[] listkodekabupaten;
    String[] listKecamatan = new String[]{};
    String[] listkodeKecamatan = new String[]{};
    String[] listDesa = new String[]{};
    String[] listkodeDesa = new String[]{};

    private ArrayAdapter<String> adpt_area1;
    private ArrayAdapter<String> adpt_area2;
    private ArrayAdapter<String> adpt_area;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_blok_sensus);

        Intent a = getIntent();
        idBs = a.getIntExtra("idBs", 0);
        kodeBs = a.getStringExtra("kodeBs");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Isi Blok Sensus");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        db = DatabaseSampling.getInstance();

        no_bs = (EditText) findViewById(R.id.no_bs);
        sls_rt_rw = (EditText) findViewById(R.id.sls_rt_rw);
        sls_dusun = (EditText) findViewById(R.id.sls_dusun);
        submit = (Button) findViewById(R.id.submit_bs);
        spinner_desa = (Spinner) findViewById(R.id.spinner_desa);
        spinner_kec = (Spinner) findViewById(R.id.spinner_kec);
        spinner_kabupaten = (Spinner) findViewById(R.id.spinner_kota);
        provinsi = (TextView) findViewById(R.id.textview1);
        kabupaten = (TextView) findViewById(R.id.textView);
        kecamatan = (TextView) findViewById(R.id.textview2);
        desa = (TextView) findViewById(R.id.textview4);
        stratifikasi = (RadioGroup) findViewById(R.id.stratifikasi);

        listkabupaten = DaerahAdministrasi.getListkabupaten();
        listkodekabupaten = DaerahAdministrasi.getListkodekabupaten();
        adpt_area = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, listkabupaten);
        spinner_kabupaten.setAdapter(adpt_area);

        spinner_kec.post(new Runnable() {
            public void run() {
                spinner_kec.setOnItemSelectedListener(IsiBlokSensusActivity.this);
            }
        });

        spinner_kabupaten.post(new Runnable() {
            public void run() {
                spinner_kabupaten.setOnItemSelectedListener(IsiBlokSensusActivity.this);
            }
        });

        if (idBs != 0) {
            prefillForm();
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBS();
            }
        });
    }

    private void submitBS() {
        if (spinner_kabupaten.getSelectedItem().toString().equals("Pilih Kabupaten")) {
            Toast.makeText(IsiBlokSensusActivity.this, "Harus memilih Kabupaten", Toast.LENGTH_SHORT).show();
        } else if (spinner_kec.getSelectedItem().toString().equals("Pilih Kecamatan")) {
            Toast.makeText(IsiBlokSensusActivity.this, "Harus memilih Kecamatan", Toast.LENGTH_SHORT).show();
        } else if (spinner_desa.getSelectedItem().toString().equals("Pilih Desa")) {
            Toast.makeText(IsiBlokSensusActivity.this, "Harus memilih Desa", Toast.LENGTH_SHORT).show();
        } else if (stratifikasi.getCheckedRadioButtonId() == -1) {
            Toast.makeText(IsiBlokSensusActivity.this, "Harus memilih perkotaan atau perdesaan", Toast.LENGTH_SHORT).show();
        } else if (no_bs.getText().length() != 4 || no_bs.getText().toString().indexOf("B") != 3) {
            Toast.makeText(IsiBlokSensusActivity.this, "Nomor Blok Sensus harus 4 digit dan benar", Toast.LENGTH_SHORT).show();
        } else if (sls_rt_rw.getText().length() < 1) {
            Toast.makeText(IsiBlokSensusActivity.this, "Harus isi keterangan RT/RW", Toast.LENGTH_SHORT).show();
        } else if (sls_dusun.getText().length() < 1) {
            Toast.makeText(IsiBlokSensusActivity.this, "Harus isi keterangan dusun", Toast.LENGTH_SHORT).show();
        } else {
            BlokSensus bs = new BlokSensus();
            bs.setProvinsi("19");
            bs.setKabupaten(listkodekabupaten[spinner_kabupaten.getSelectedItemPosition()]);
            bs.setKecamatan(listkodeKecamatan[spinner_kec.getSelectedItemPosition()]);
            bs.setDesa(listkodeDesa[spinner_desa.getSelectedItemPosition()]);
            int selectedId = stratifikasi.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            klasifikasi = (RadioButton) findViewById(selectedId);
            Toast.makeText(IsiBlokSensusActivity.this, klasifikasi.getText(), Toast.LENGTH_SHORT).show();
            if (selectedId == R.id.perdesaan) {
                bs.setStratifikasi("1");
            } else {
                bs.setStratifikasi("2");
            }
//            bs.setStratifikasi(klasifikasi.getText().toString());
            bs.setNoBs(no_bs.getText().toString());

            if (idBs == 0) {
                if (db.insertBlokSensus(bs)) {
                    Toast.makeText(this, "Tambah BS Berhasil", Toast.LENGTH_SHORT).show();
                    ActivitySync.backupLocal(getApplicationContext());
                    finish();
                } else {
                    Toast.makeText(this, "Tambah BS Gagal", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (db.UpdateBlokSensus(bs, kodeBs)) {
                    Toast.makeText(this, "Edit BS Berhasil", Toast.LENGTH_SHORT).show();
                    ActivitySync.backupLocal(getApplicationContext());
                    finish();
                } else {
                    Toast.makeText(this, "Edit BS Gagal", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void setAdapterKec(String[] listKecamatan) {
        spinner_kec.setEnabled(true);
        spinner_kec = (Spinner) findViewById(R.id.spinner_kec);
        adpt_area1 = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, listKecamatan);
        spinner_kec.setAdapter(adpt_area1);
    }

    private void setAdapterDesa(String[] listDesa) {
        spinner_desa.setEnabled(true);
        spinner_desa = (Spinner) findViewById(R.id.spinner_desa);
        adpt_area2 = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, listDesa);
        spinner_desa.setAdapter(adpt_area2);
    }

    private void setAdapterDesaKosong() {
        spinner_desa.setEnabled(false);
        spinner_desa = (Spinner) findViewById(R.id.spinner_desa);
        adpt_area2 = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, kosong);
        spinner_desa.setAdapter(adpt_area2);
    }

    private void setPilihKecamatan() {
        if (spinner_kabupaten.getSelectedItem().toString().equals("Pilih Kabupaten")) {
            spinner_kec.setEnabled(false);
            spinner_kec = (Spinner) findViewById(R.id.spinner_kec);
            adpt_area1 = new ArrayAdapter<String>(getBaseContext(),
                    android.R.layout.simple_spinner_dropdown_item, kosong);
            spinner_kec.setAdapter(adpt_area1);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("BANGKA")) {
            listKecamatan = DaerahAdministrasi.getBangka();
            listkodeKecamatan = DaerahAdministrasi.getBangka_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("BANGKA BARAT")) {
            listKecamatan = DaerahAdministrasi.getBangkabarat();
            listkodeKecamatan = DaerahAdministrasi.getBangkabarat_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("BANGKA TENGAH")) {
            listKecamatan = DaerahAdministrasi.getBangkatengah();
            listkodeKecamatan = DaerahAdministrasi.getBangkatengah_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("BANGKA SELATAN")) {
            listKecamatan = DaerahAdministrasi.getBangkaselatan();
            listkodeKecamatan = DaerahAdministrasi.getBangkaselatan_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("BELITUNG")) {
            listKecamatan = DaerahAdministrasi.getBelitung();
            listkodeKecamatan = DaerahAdministrasi.getBelitung_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("BELITUNG TIMUR")) {
            listKecamatan = DaerahAdministrasi.getBelitungtimur();
            listkodeKecamatan = DaerahAdministrasi.getBelitungtimur_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("PANGKAL PINANG")) {
            listKecamatan = DaerahAdministrasi.getPangkalpinang();
            listkodeKecamatan = DaerahAdministrasi.getPangkalpinang_kode();
            setAdapterKec(listKecamatan);
        } else if (spinner_kabupaten.getSelectedItem().toString().equals("DUMMY_KABUPATEN")) {
            listKecamatan = DaerahAdministrasi.getDummykabupaten();
            listkodeKecamatan = DaerahAdministrasi.getDummykabupaten_kode();
            setAdapterKec(listKecamatan);
        }
    }

    private void setPilihDesa() {
        if (spinner_kec.getSelectedItem().toString().equals("Pilih Kecamatan")) {
            setAdapterDesaKosong();
        } else if (spinner_kec.getSelectedItem().toString().equals("")) {
            setAdapterDesaKosong();
        } else if (spinner_kec.getSelectedItem().toString().equals("MENDO BARAT")) {
            listDesa = DaerahAdministrasi.getMendobarat();
            listkodeDesa = DaerahAdministrasi.getMendobarat_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("MERAWANG")) {
            listDesa = DaerahAdministrasi.getMerawang();
            listkodeDesa = DaerahAdministrasi.getMerawang_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("PUDING BESAR")) {
            listDesa = DaerahAdministrasi.getPudingbesar();
            listkodeDesa = DaerahAdministrasi.getPudingbesar_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SUNGAI LIAT")) {
            listDesa = DaerahAdministrasi.getSungailiat();
            listkodeDesa = DaerahAdministrasi.getSungailiat_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("PEMALI")) {
            listDesa = DaerahAdministrasi.getPemali();
            listkodeDesa = DaerahAdministrasi.getPemali_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("BAKAM")) {
            listDesa = DaerahAdministrasi.getBakam();
            listkodeDesa = DaerahAdministrasi.getBakam_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("BELINYU")) {
            listDesa = DaerahAdministrasi.getBelinyu();
            listkodeDesa = DaerahAdministrasi.getBelinyu_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("RIAU SILIP")) {
            listDesa = DaerahAdministrasi.getRiausilip();
            listkodeDesa = DaerahAdministrasi.getRiausilip_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("MEMBALONG")) {
            listDesa = DaerahAdministrasi.getMembalong();
            listkodeDesa = DaerahAdministrasi.getMembalong_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("TANJUNG PANDAN")) {
            listDesa = DaerahAdministrasi.getTanjungpandan();
            listkodeDesa = DaerahAdministrasi.getTanjungpandan_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("BADAU")) {
            listDesa = DaerahAdministrasi.getBadau();
            listkodeDesa = DaerahAdministrasi.getBadau_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SIJUK")) {
            listDesa = DaerahAdministrasi.getSijuk();
            listkodeDesa = DaerahAdministrasi.getSijuk_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SELAT NASIK")) {
            listDesa = DaerahAdministrasi.getSelatnasik();
            listkodeDesa = DaerahAdministrasi.getSelatnasik_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("KELAPA")) {
            listDesa = DaerahAdministrasi.getKelapa();
            listkodeDesa = DaerahAdministrasi.getKelapa_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("TEMPILANG")) {
            listDesa = DaerahAdministrasi.getTempilang();
            listkodeDesa = DaerahAdministrasi.getTempilang_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("MENTOK")) {
            listDesa = DaerahAdministrasi.getMentok();
            listkodeDesa = DaerahAdministrasi.getMentok_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SIMPANG TERITIP")) {
            listDesa = DaerahAdministrasi.getSimpangteritip();
            listkodeDesa = DaerahAdministrasi.getSimpangteritip_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("JEBUS")) {
            listDesa = DaerahAdministrasi.getJebus();
            listkodeDesa = DaerahAdministrasi.getJebus_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("KOBA")) {
            listDesa = DaerahAdministrasi.getKoba();
            listkodeDesa = DaerahAdministrasi.getKoba_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("LUBUK BESAR")) {
            listDesa = DaerahAdministrasi.getLubukbesar();
            listkodeDesa = DaerahAdministrasi.getLubukbesar_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("PANGKALAN BARU")) {
            listDesa = DaerahAdministrasi.getPangkalanbaru();
            listkodeDesa = DaerahAdministrasi.getPangkalanbaru_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("NAMANG")) {
            listDesa = DaerahAdministrasi.getNamang();
            listkodeDesa = DaerahAdministrasi.getNamang_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SUNGAI SELAN")) {
            listDesa = DaerahAdministrasi.getSungaiselan();
            listkodeDesa = DaerahAdministrasi.getSungaiselan_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SIMPANG KATIS")) {
            listDesa = DaerahAdministrasi.getSimpangkatis();
            listkodeDesa = DaerahAdministrasi.getSimpangkatis_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("PAYUNG")) {
            listDesa = DaerahAdministrasi.getPayung();
            listkodeDesa = DaerahAdministrasi.getPayung_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("PULAU BESAR")) {
            listDesa = DaerahAdministrasi.getPulaubesar();
            listkodeDesa = DaerahAdministrasi.getPulaubesar_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SIMPANG RIMBA")) {
            listDesa = DaerahAdministrasi.getSimpangrimba();
            listkodeDesa = DaerahAdministrasi.getSimpangrimba_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("TOBOALI")) {
            listDesa = DaerahAdministrasi.getToboali();
            listkodeDesa = DaerahAdministrasi.getToboali_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("TUKAK SADAI")) {
            listDesa = DaerahAdministrasi.getTukaksadai();
            listkodeDesa = DaerahAdministrasi.getTukaksadai_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("AIR GEGAS")) {
            listDesa = DaerahAdministrasi.getAirgegas();
            listkodeDesa = DaerahAdministrasi.getAirgegas_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("LEPAR PONGOK")) {
            listDesa = DaerahAdministrasi.getLeparpongok();
            listkodeDesa = DaerahAdministrasi.getLeparpongok_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("DENDANG")) {
            listDesa = DaerahAdministrasi.getDendang();
            listkodeDesa = DaerahAdministrasi.getDendang_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SIMPANG PESAK")) {
            listDesa = DaerahAdministrasi.getSimpangpesak();
            listkodeDesa = DaerahAdministrasi.getSimpangpesak_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("GANTUNG")) {
            listDesa = DaerahAdministrasi.getGantung();
            listkodeDesa = DaerahAdministrasi.getGantung_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("SIMPANG RENGGIANG")) {
            listDesa = DaerahAdministrasi.getSimpangrenggiang();
            listkodeDesa = DaerahAdministrasi.getSimpangrenggiang_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("MANGGAR")) {
            listDesa = DaerahAdministrasi.getManggar();
            listkodeDesa = DaerahAdministrasi.getManggar_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("DAMAR")) {
            listDesa = DaerahAdministrasi.getDamar();
            listkodeDesa = DaerahAdministrasi.getDamar_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("KELAPA KAMPIT")) {
            listDesa = DaerahAdministrasi.getKelapakampit();
            listkodeDesa = DaerahAdministrasi.getKelapakampit_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("RANGKUI")) {
            listDesa = DaerahAdministrasi.getRangkui();
            listkodeDesa = DaerahAdministrasi.getRangkui_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("BUKIT INTAN")) {
            listDesa = DaerahAdministrasi.getBukitintan();
            listkodeDesa = DaerahAdministrasi.getBukitintan_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("PANGKAL BALAM")) {
            listDesa = DaerahAdministrasi.getPangkalbalam();
            listkodeDesa = DaerahAdministrasi.getPangkalbalam_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("TAMAN SARI")) {
            listDesa = DaerahAdministrasi.getTamansari();
            listkodeDesa = DaerahAdministrasi.getTamansari_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("GERUNGGANG")) {
            listDesa = DaerahAdministrasi.getGerunggang();
            listkodeDesa = DaerahAdministrasi.getGerunggang_kode();
            setAdapterDesa(listDesa);
        } else if (spinner_kec.getSelectedItem().toString().equals("DUMMY_KECAMATAN")) {
            listDesa = DaerahAdministrasi.getDummykecamatan();
            listkodeDesa = DaerahAdministrasi.getDummykecamatan_kode();
            setAdapterDesa(listDesa);
        }
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //set spinner kecamatan berdasarkan kabupaten terpilih
        if (parent.getId() == R.id.spinner_kota) {
            setPilihKecamatan();
            //set spinner kode_desa berdasarkan kecamatan terpilih
        } else if (parent.getId() == R.id.spinner_kec) {
            //Log.d("You Selected :", (String) spinner_kec.getSelectedItem());
            setPilihDesa();
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void prefillForm() {

        BlokSensus bsL;
        bsL = db.getBlokSensusByKode(kodeBs);

        if (!bsL.getKabupaten().equals("")) {
            String kab = bsL.getKabupaten();
            int posKab = 0;
            for (String kodeKab : listkodekabupaten) {
                Log.d("EditBs ", "kodeKab " + kodeKab);
                if (kab.equals(kodeKab)) {
                    break;
                }
                posKab++;
            }
            Log.d("EditBs ", kab + " posKab " + posKab);
            spinner_kabupaten.setSelection(posKab);
        }


        if (!bsL.getKecamatan().equals("")) {
            String kec = bsL.getKecamatan();
            String helpKab = bsL.getKabupaten();
            int posKec = 0;
            setPilihKecamatan();
            for (String kodeKec : listkodeKecamatan) {
                Log.d("EditBs ", "kodeKec " + kodeKec);
                if (kec.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            Log.d("EditBs ", kec + " posKec " + posKec);
            spinner_kec.setSelection(posKec);
        }


        if (!bsL.getDesa().equals("")) {
            String desa = bsL.getDesa();
            int posdesa = 0;
            setPilihDesa();
            for (String kodeDesa : listkodeDesa) {
                Log.d("EditBs ", "kodeDesa " + kodeDesa);
                if (desa.equals(kodeDesa)) {
                    break;
                }
                posdesa++;
            }
            Log.d("EditBs ", desa + " posDesa " + posdesa);
            spinner_desa.setSelection(posdesa);
        }

        if (!bsL.getStratifikasi().equals("")) {
            String stra = bsL.getStratifikasi();
            if (stra.equals("Perkotaan")) {
                stratifikasi.check(R.id.perkotaan);
            } else {
                stratifikasi.check(R.id.perdesaan);
            }
        }

        no_bs.setText(bsL.getNoBs());
    }
}
