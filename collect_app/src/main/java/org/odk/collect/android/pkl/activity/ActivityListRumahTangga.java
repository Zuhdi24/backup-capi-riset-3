package org.odk.collect.android.pkl.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.adapter.DialogListKuesAdapter;
import org.odk.collect.android.pkl.adapter.RumahTanggaAdapter;
import org.odk.collect.android.pkl.adapter.RutaAdapter;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.DialogListKuesModel;
import org.odk.collect.android.pkl.object.RumahTangga;
import org.odk.collect.android.pkl.object.RumahTanggaTerpilih;
import org.odk.collect.android.pkl.object.SampelRuta;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.preference.SyncManager;
import org.odk.collect.android.pkl.task.GenerateKuesioner;
import org.odk.collect.android.pkl.task.SynchronizeTask;
import org.odk.collect.android.pkl.util.LocationService;
import org.odk.collect.android.pkl.util.MasterPassword;
import org.odk.collect.android.pkl.util.Sampling;
import org.odk.collect.android.provider.FormsProviderAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ActivityListRumahTangga extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    public final static String MODE_ALL = "all";
    public final static String MODE_SAMPEL = "sampel";
    private final String TAG = "LIST RUTA";
    private static final String SHOWCASE_ID = "info";
        private Location location;
    private LocationService ls;

    Parcelable state;
    ListView listV;
    ArrayList<RumahTangga> listruta;
    RutaAdapter rutaAdapter2;
    RumahTanggaAdapter rutaAdapter;
    String listMode;
    DatabaseSampling dbSampling;
    Toolbar toolbar;
    boolean refreshData;

    final Context context = this;
    private LayoutInflater inflater;
    private TextView empty2;
//    private TextView empty3;
    LinearLayout atribut, kotak_ruta;
    ActivityListRumahTangga activity;
    SyncManager syncManager;
    String nim, nimKortim, deviceId, kodeBs, status, keberadaan;
    FloatingActionButton addRuta;
    MenuItem menuUpload, menuSearch, menuSend, menuKembalikan, menuAmbilSampel, menuKirimSampel, menuSort;
    Animation spin;
    SearchView sv;
    Menu optionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_ruta);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        activity = this;
//        Log.i(TAG, "create berhasil");

        syncManager = new SyncManager(activity);
        CapiPreference preference = CapiPreference.getInstance();

        kotak_ruta = findViewById(R.id.kotak_rutaa);

        nim = (String) preference.get(CapiKey.KEY_NIM);
        nimKortim = (String) preference.get(CapiKey.KEY_NIM_KORTIM);
        deviceId = syncManager.getDeviceId();

        dbSampling = DatabaseSampling.getInstance();

        Intent i = getIntent();
        status = i.getStringExtra("status");
        kodeBs = i.getStringExtra("kodeBs");
        listMode = i.getStringExtra("mode");
        refreshData = i.getBooleanExtra("refreshData", false);
        keberadaan = i.getStringExtra("keberadaan");

        empty2 = findViewById(R.id.empty2);
        listV = findViewById(R.id.listRuta);
        atribut = findViewById(R.id.atribut);
        addRuta = findViewById(R.id.addruta_button);
        spin = AnimationUtils.loadAnimation(context, R.anim.spin);

        listV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (dbSampling.getListUnitUsahaPariwisata(kodeBs).size() == 0 || refreshData) {
            new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SYNC_BS, kodeBs);
        } else {
            try {
                showAllRuta();
                Log.i(TAG, "jumlah Sampel" + dbSampling.getRutaTerpilih(kodeBs));
            } catch (NullPointerException e) {
                Log.e(TAG, "onCreate: NPE");
                new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SYNC_BS, kodeBs);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionMenu = menu;

        getMenuInflater().inflate(R.menu.menu_ruta, menu);
        menuUpload = menu.findItem(R.id.action_sync);
        menuSearch = menu.findItem(R.id.action_search);
        menuSend = menu.findItem(R.id.action_send);
        menuKembalikan = menu.findItem(R.id.action_kembalikan);
        menuAmbilSampel = menu.findItem(R.id.action_ambil_sampel);
        menuKirimSampel = menu.findItem(R.id.action_kirim_sampel);
        menuSort = menu.findItem(R.id.action_sort);
        sv = (SearchView) MenuItemCompat.getActionView(menuSearch);
        sv.setOnQueryTextListener(this);
        sv.setQueryHint("Cari Ruta");
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = sv.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.textColorPrimary));

//        if (dbSampling.getListRumahTanggaByKeberadaan(kodeBs).size() > 0){
//            menuKirimSampel.setVisible(false);
//        }else{
//            menuKirimSampel.setVisible(true);
//        }

        switch (dbSampling.getBlokSensusByKode(kodeBs).getStatus()) {
            case BlokSensus.FLAG_BS_PROSES_LISTING:
                menuSend.setVisible(true);
                menuKembalikan.setVisible(false);
                menuAmbilSampel.setVisible(true);
                menuKirimSampel.setVisible(false);
                break;
            case BlokSensus.FLAG_BS_READY:
                menuSend.setVisible(false);
                menuKembalikan.setVisible(true);
                menuAmbilSampel.setVisible(true);
                menuKirimSampel.setVisible(false);
                break;
            case BlokSensus.FLAG_BS_SAMPLED:
                menuSend.setVisible(false);
                menuKembalikan.setVisible(false);
                menuAmbilSampel.setVisible(false);
                menuKirimSampel.setVisible(true);
                break;
            case BlokSensus.FLAG_BS_SAMPLE_UPLOADED:
                menuSend.setVisible(false);
                menuKembalikan.setVisible(false);
                menuAmbilSampel.setVisible(false);
                menuKirimSampel.setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SYNC_BS, kodeBs);
                break;
            case R.id.action_sort:
                sorting();
                break;
            case R.id.action_send:      //finalisasi BS
                if (dbSampling.getListUnitUsahaPariwisata(kodeBs).size() < CapiKey.N_MINIMUM_RUTA) {
                    warningBstttd2();
                } else {
                    send();
                }
                break;
            case R.id.action_kembalikan:
                kembalikanStatus(kodeBs);
                break;
            case R.id.action_ambil_sampel:
                if (dbSampling.getListUnitUsahaPariwisata(kodeBs).size() < CapiKey.N_MINIMUM_RUTA) {
                    warningBstttd();
                } else {
                    ambilSampel(kodeBs);
                }
                break;
            case R.id.action_kirim_sampel:
                new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SEND_SAMPLE, kodeBs);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Log.d(TAG, "saving listview state @ onPause");
        state = listV.onSaveInstanceState();
        super.onPause();
    }

    public void showAllRuta() {
        Log.d(TAG, "showAllRuta: " + listMode);

        if (optionMenu != null) {
            sv = (SearchView) MenuItemCompat.getActionView(menuSearch);
            sv.setOnQueryTextListener(this);
            sv.setQueryHint("Cari Ruta");
            int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = sv.findViewById(id);
            textView.setTextColor(getResources().getColor(R.color.textColorPrimary));
            if (dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_PROSES_LISTING)) {
                menuSend.setVisible(true);
                menuKembalikan.setVisible(false);
                menuAmbilSampel.setVisible(true);
                menuKirimSampel.setVisible(false);
            } else if (dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_READY)) {
                menuSend.setVisible(false);
                menuKembalikan.setVisible(true);
                menuAmbilSampel.setVisible(true);
                menuKirimSampel.setVisible(false);
            } else if (dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_SAMPLED)) {
                menuSend.setVisible(false);
                menuKembalikan.setVisible(false);
                menuAmbilSampel.setVisible(false);
                menuKirimSampel.setVisible(true);
            } else if (dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_SAMPLE_UPLOADED)) {
                menuSend.setVisible(false);
                menuKembalikan.setVisible(false);
                menuAmbilSampel.setVisible(false);
                menuKirimSampel.setVisible(false);
            }
        }
        listV = findViewById(R.id.listRuta);

        if (listMode.equals(MODE_ALL)) {

            setTitle("List RUTA-" + dbSampling.getBlokSensusByKode(kodeBs).getNoBs());
            listruta = dbSampling.getListUnitUsahaPariwisata(kodeBs);
            rutaAdapter = new RumahTanggaAdapter(this, listruta);
            listV.setAdapter(rutaAdapter);

            TextView noBs = findViewById(R.id.no_bs);
            TextView noBf = findViewById(R.id.no_bf);
            TextView noUrut = findViewById(R.id.no_urut);
            TextView namaKRT = findViewById(R.id.nama_KRT);
            noBs.setVisibility(View.VISIBLE);
            noBf.setVisibility(View.VISIBLE);
            namaKRT.setVisibility(View.VISIBLE);
            noUrut.setVisibility(View.VISIBLE);

//            listV.setSelectionFromTop(index, top);
            rutaAdapter.notifyDataSetChanged();
            View text = findViewById(R.id.infox);
            new MaterialShowcaseView.Builder(this)
                    .setTarget(text)
                    .setDismissText("OK")
                    .setContentText("Tekan dan tahan beberapa saat agar dapat menggunakan aksi tambahan terhadap ruta yang terpilih")
                    .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                    .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                    .show();


            if (dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_PROSES_LISTING)) {
//                btn.setVisibility(View.VISIBLE);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (isGpsActive()) {
//                            toIsiListing(null, -1);
//                        } else
//                            Toast.makeText(context, "GPS diperlukan untuk listing", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
                addRuta.setVisibility(View.VISIBLE);
                addRuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isGpsActive()) {
                            toIsiListing(null, -1);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityListRumahTangga.this);
                            builder.setTitle("Peringatan")
                                    .setMessage("Pastikan GPS anda berfungsi dengan benar")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                });

                listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        RumahTangga ruta = listruta.get(position);
                        option(ruta);
                        return true;
                    }
                });

            } else {
//                btn.setVisibility(View.GONE);
                listV.setOnItemLongClickListener(null);
                addRuta.setVisibility(View.GONE);
            }
            final CapiPreference preference = CapiPreference.getInstance();
            listV.setOnScrollListener(new AbsListView.OnScrollListener() {

                boolean hasStarted, hasStopped, hasFlied;
                int firstItem, total, itemCount;

                @Override
                public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                    hasStarted = scrollState == SCROLL_STATE_TOUCH_SCROLL;
                    hasStopped = scrollState == SCROLL_STATE_IDLE;
                    hasFlied = scrollState == SCROLL_STATE_FLING;
                    if (!StaticFinal.JABATAN_KORTIM_ID.equals(preference.get(CapiKey.KEY_ID_JABATAN))) {
                        if (firstItem > (total - itemCount - 1)
                                && total != itemCount
                                && total > (itemCount + 1)
                        ) {
                            addRuta.hide();
                        } else if (!hasStarted && !hasFlied) {
                            if (dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_PROSES_LISTING)) {
                                addRuta.show();
                            } else {
                                addRuta.hide();
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int firstItem, int itemCount, int total) {
                    this.firstItem = firstItem;
                    this.itemCount = itemCount;
                    this.total = total;
                }
            });

            if (!dbSampling.getBlokSensusByKode(kodeBs).getStatus().equals(BlokSensus.FLAG_BS_PROSES_LISTING)) {
                addRuta.setVisibility(View.GONE); //Masih gagal cuy
            }
            sortby(6);

            if (state != null) {
                Log.d(TAG, "trying to restore listview state..");
                listV.onRestoreInstanceState(state);
            }

        } else if (listMode.equals(MODE_SAMPEL)) {
            Log.d(TAG, "onCreate: mode sampel");
            getSupportActionBar().setTitle("List Sampel-" + dbSampling.getBlokSensusByKode(kodeBs).getNoBs());

            Log.d(TAG, "showAllRuta: " + kodeBs);
            atribut.setVisibility(View.GONE);
            addRuta.setVisibility(View.GONE);
            listruta = dbSampling.getRutaTerpilih(kodeBs);
            rutaAdapter2 = new RutaAdapter(this, listruta);
            listV.setAdapter(rutaAdapter2);
            rutaAdapter2.notifyDataSetChanged();

            listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        //Pilih Kuesioner dulu
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityListRumahTangga.this);
                        builderSingle.setTitle("Pilih Kuesioner untuk Digenerate : ");

                        String sortOrder = FormsProviderAPI.FormsColumns.DISPLAY_NAME + " ASC, " + FormsProviderAPI.FormsColumns.JR_VERSION + " DESC";
                        Cursor c = managedQuery(FormsProviderAPI.FormsColumns.CONTENT_URI, null, null, null, sortOrder);

                        LinkedList<DialogListKuesModel> dlkms = new LinkedList<>();

                        if (c.moveToFirst()) {
                            DialogListKuesModel dlkm;
                            do {
                                dlkm = new DialogListKuesModel(c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_NAME)),
                                        c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_NAME)),
                                        c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT)));
//                            dlkms.addLast(dlkm);
                                if (dlkm.getDisplayName().contains("R3") || dlkm.getDisplayName().contains("Riset3")) {
                                    dlkms.addLast(dlkm);
                                    Log.d("NAMA FORM", dlkm.getDisplayName());
                                } else {
                                    Log.e("", "masuk99 nim1, masuk else");
                                }
                            } while (c.moveToNext());
                        }
                        c.close();

                        final DialogListKuesAdapter arrayAdapter = new DialogListKuesAdapter(ActivityListRumahTangga.this, dlkms);

                        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String strName = ((DialogListKuesModel) arrayAdapter.getItem(which)).getDisplayName();
                                BlokSensus bs = dbSampling.getBlokSensusByKode(kodeBs);
                                ArrayList<SampelRuta> sampelRuta = dbSampling.getAllSampelRuta();
                                for (SampelRuta sampel : sampelRuta) {
                                    Log.e("WASKITHO", kodeBs + "*" + sampel.getKodeRuta());
                                }
                                RumahTangga rta = rutaAdapter2.getItem(position);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
                                String timeStamp = dateFormat.format(new Date());
//                                String fileName = "R3-35-" + bs.getKabupaten() + "-" + bs.getKecamatan() + "-" + bs.getDesa() + "-" + bs.getNoBs() + "-" + rta.getNoUrutRuta();S
                                String fileName = "R3-" + bs.getDesa() + "-" + bs.getNoBs() + "-" + rta.getNamaKRT() + "-" + rta.getNoUrutRuta();
                                if (strName.contains("KM5")) {
                                    fileName = fileName + "-KM5";
                                } else if (strName.contains("M5")) {
                                    fileName = fileName + "-M5";
                                }

                                fileName = fileName + "_" + timeStamp;
                                Log.e("FILENAME SANDY", strName);
                                Log.d("yoow", fileName);

                                final RumahTanggaTerpilih ask = new RumahTanggaTerpilih(
                                        fileName,
//                                    String.valueOf(location.getLatitude()),
//                                    String.valueOf(location.getLongitude()),
//                                    String.valueOf(location.getAccuracy()),
                                        rta.getLatitude(), rta.getLongitude(), rta.getAkurasi(),
                                        bs.getKodeBs(),
                                        bs.getKabupaten(), bs.getKecamatan(),
                                        bs.getDesa(), bs.getNoBs(),
                                        bs.getNamaKabupaten(), bs.getNamaKecamatan(),
                                        bs.getNamaDesa(), rta.getNoUrutRuta(), rta.getNamaKRT(), rta.getAlamat(),
                                        rta.getJumlahART(), rta.getJumlahART10(), rta.getNoHp(), rta.getKodeEligible()
                                );

                                AlertDialog.Builder builderInner = new AlertDialog.Builder(ActivityListRumahTangga.this);
                                builderInner.setTitle("Detail");
                                builderInner.setMessage("Kuesioner : " + strName +
                                        "\nKode : " + fileName +
                                        "\n\nKuesioner akan digenerate berdasarkan isian listing dan ditampilkan pada menu Ubah Kuesioner.");
                                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GenerateKuesioner gen = new GenerateKuesioner(context, strName);
                                        gen.GenerateKuesInstance(ask);
                                        dialog.dismiss();
                                    }
                                });
                                builderInner.show();
                            }
                        });
                        builderSingle.show();
                }
            });

            sortby(6);
        }
        if (listruta.size() == 0) {
            setVisible(empty2);
//            setInvisible(empty3);
            setInvisible(atribut);
        } else {
            setInvisible(empty2);
//            setInvisible(empty3);
            setVisible(atribut);
        }

        CapiPreference preference = CapiPreference.getInstance();
        if (StaticFinal.JABATAN_KORTIM_ID.equals(preference.get(CapiKey.KEY_ID_JABATAN))) {
            Log.d(TAG, "showAllRuta: MASUK KORITM");
            addRuta.setVisibility(View.GONE);
            listV.setOnItemLongClickListener(null);
        }
    }

    private void sorting() {
        View convertView;
        listV = findViewById(R.id.listRuta);
        if (inflater == null)
            inflater = (LayoutInflater) ActivityListRumahTangga.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.judul_ruta, null);
        TextView judulRuta = convertView.findViewById(R.id.judulRT);
        judulRuta.setText("Urutkan Berdasarkan");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(convertView)
                .setItems(R.array.menuSort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                sortby(0);
                                break;
                            case 1:
                                sortby(1);
                                break;
                            case 2:
                                sortby(2);
                                break;
                            case 3:
                                sortby(3);
                                break;
                            case 4:
                                sortby(4);
                                break;
                            case 5:
                                sortby(5);
                                break;
                        }
                    }
                })
                .show();
    }

    public void sortby(int i) {
        listV = (ListView) findViewById(R.id.listRuta);
        if (listMode.equals(MODE_ALL)) {
            listruta = dbSampling.getListUnitUsahaPariwisata(kodeBs);
        } else if (listMode.equals(MODE_SAMPEL)) {
            listruta = dbSampling.getRutaTerpilih(kodeBs);
        }
        switch (i) {
            case 0:
                Collections.sort(listruta, new Comparator<RumahTangga>() {
                    @Override
                    public int compare(RumahTangga o1, RumahTangga o2) {
                        return Integer.valueOf(o1.getNoUrutRuta()).compareTo(Integer.valueOf(o2.getNoUrutRuta()));
                    }
                });
                break;
            case 1:
                Collections.sort(listruta, new Comparator<RumahTangga>() {
                    @Override
                    public int compare(RumahTangga o1, RumahTangga o2) {
                        return Integer.valueOf(o2.getNoUrutRuta()).compareTo(Integer.valueOf(o1.getNoUrutRuta()));
                    }
                });
                break;
            case 2:
                Collections.sort(listruta, new Comparator<RumahTangga>() {
                    @Override
                    public int compare(RumahTangga o1, RumahTangga o2) {
                        return o1.getBf().compareTo(o2.getBf());
                    }
                });
                break;
//            case 3:
//                Collections.sort(listruta, new Comparator<RumahTangga>() {
//                    @Override
//                    public int compare(RumahTangga o1, RumahTangga o2) {
//                        return Integer.valueOf(o1.getBs().compareTo(o2.getBs()));
//                    }
//                });
//                break;
            case 3:
                Collections.sort(listruta, new Comparator<RumahTangga>() {
                    @Override
                    public int compare(RumahTangga o1, RumahTangga o2) {
                        return o1.getNamaKRT().compareTo(o2.getNamaKRT());
                    }
                });
                break;
//            case 5:
//                Collections.sort(listruta, new Comparator<RumahTanggaRiset4>() {
//                    @Override
//                    public int compare(RumahTanggaRiset4 o1, RumahTanggaRiset4 o2) {
//                        return Integer.valueOf(o1.getRutaInternet()).compareTo(Integer.valueOf(o2.getRutaInternet()));
//                    }
//                });
//                break;
        }
        if (listruta.size() == 0) {
//            setInvisible(empty3);
            setInvisible(atribut);
            setVisible(empty2);
        } else {
//            setInvisible(empty3);
            setInvisible(empty2);
            setVisible(atribut);
        }
        if (listMode.equals(MODE_ALL)) {
            rutaAdapter = new RumahTanggaAdapter(this, listruta);
            listV.setAdapter(rutaAdapter);
            rutaAdapter.notifyDataSetChanged();
        } else if (listMode.equals(MODE_SAMPEL)) {
            rutaAdapter2 = new RutaAdapter(this, listruta);
            listV.setAdapter(rutaAdapter2);
            rutaAdapter2.notifyDataSetChanged();
        }
    }

    private void setVisible(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void setInvisible(View v) {
        v.setVisibility(View.GONE);
    }

    private void option(final RumahTangga ruta) {
        View convertView;
        if (inflater == null)
            inflater = (LayoutInflater) ActivityListRumahTangga.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.judul_ruta, null);
        TextView judulRuta = convertView.findViewById(R.id.judulRT);

        judulRuta.setText("Aksi Untuk Ruta " + ruta.getNamaKRT());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(convertView)
                .setItems(R.array.menuRuta, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                            case 1:
                                toIsiListing(ruta, which);
                                break;
                            case 2:
                                //TODO MASUKIN INTENT CALL NUMBER
                                final Boolean hasNomor;
//                                final String nomor = ruta.getNoHp();
                                final String nomor = "0";
                                hasNomor = !nomor.equalsIgnoreCase("");

                                if (hasNomor) {
//                                    callContact("+" + ruta.getNoHp());
                                    Toast.makeText(context, "Tidak Ada Nomor", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 3:
                                passwordHapusRutaKortim(ruta);
                                break;
                        }
                    }
                })
                .show();
    }

    public void passwordHapusRutaKortim(final RumahTangga ruta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityListRumahTangga.this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Hapus Rumah Tangga membutuhkan persetujuan PML. \nMasukan password master yang diperoleh dari PML.");
        final EditText input = new EditText(ActivityListRumahTangga.this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        input.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MasterPassword.getPassword(nimKortim).equals(input.getText().toString())) {
                    try {
//                        dbSampling.updateStatusRumahTangga(ruta, RumahTangga.STATUS_DELETE);
                        dbSampling.deleteRumahtangga(ruta);
                        dbSampling.clearkanNoUUP(ruta.getKodeBs());
                        Toast.makeText(ActivityListRumahTangga.this, "berhasil dihapus", Toast.LENGTH_SHORT).show();
                        ActivitySync.backupLocal(getApplicationContext());
                        showAllRuta();
                    } catch (Exception e) {
                        Log.d("Hapus ", "Ruta Berhasil " + e);
                    }
                    dialog.dismiss();
                } else {
                    try {
                        Toast.makeText(ActivityListRumahTangga.this, "Password salah, Rumah Tangga gagal dihapus", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("Hapus ", "Ruta Gagal " + e);
                    }
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog ad = builder.show();
        ad.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button negButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(20, 0, 0, 0);
                negButton.setLayoutParams(params);
            }
        });
    }

    private void kembalikanStatus(final String kodeBs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pengembalian status BS");
        builder.setMessage("Pengembalian status BS memerlukan persetujuan Kortim. \nMasukan password master yang diperoleh dari Kortim.");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        input.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MasterPassword.getPassword(nimKortim).equals(input.getText().toString())) {
                    new SynchronizeTask(activity).execute(SynchronizeTask.MODE_RETURN_BS, kodeBs);
//                    new SynchronizeTask(activity).changeStatusBs(kodeBs, BlokSensus.FLAG_BS_PROSES_LISTING, activity);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Password salah!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = builder.show();
        ad.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button negButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(20, 0, 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    negButton.setTextColor(context.getColor(R.color.colorPrimaryDark));
                    negButton.setBackgroundColor(context.getColor(R.color.pkl61_bg));
                }
                negButton.setLayoutParams(params);
            }
        });
    }

    private void ambilSampel(final String kodeBs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sampel belum diambil");
        builder.setMessage("Pengambilan Sampel memerlukan persetujuan PML. \nMasukan password master yang diperoleh dari PML.");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        input.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MasterPassword.getPassword(nimKortim).equals(input.getText().toString())) {
                    try {
                        if (Sampling.ambilSampel(CapiKey.N_SAMPLE, kodeBs, context)) {
                            if (dbSampling.updateStatusBlokSensus(kodeBs, BlokSensus.FLAG_BS_SAMPLED)) {
                                Toast.makeText(context, "Sampling Berhasil", Toast.LENGTH_SHORT).show();
                                listMode = ActivityListRumahTangga.MODE_SAMPEL;
                                if (isNetworkConnected()) {
                                    new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SEND_SAMPLE, kodeBs);
                                } else {
                                    Log.d(TAG, "Gagal mengirim Sampel karena Jaringan atau server");
                                }
                                //                                new SynchronizeTask(activity).kirimSampel(kodeBs);

                                ActivitySync.backupLocal(context);
                            } else {
                                Toast.makeText(context, "Sampling ditarik, Status BS belum diubah", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "Sampling Gagal", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d("Sampling ", "Berhasil " + e);
                    }
                    dialog.dismiss();
                } else {
                    try {
                        Toast.makeText(context, "Password salah, Sampling tidak dilakukan!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("Sampling ", "Gagal " + e);
                    }
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = builder.show();
        ad.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button negButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(20, 0, 0, 0);
                negButton.setLayoutParams(params);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        dbSampling = DatabaseSampling.getInstance();
        showAllRuta();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (listMode.equals(MODE_ALL)) {
            if (TextUtils.isEmpty(newText)) {
                rutaAdapter.getFilter().filter("");
            } else {
                rutaAdapter.getFilter().filter(newText.toLowerCase());
            }
        } else {
            if (TextUtils.isEmpty(newText)) {
                rutaAdapter2.getFilter().filter("");
            } else {
                rutaAdapter2.getFilter().filter(newText.toLowerCase());
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!sv.isIconified() || sv.isFocused()) {
            sv.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    private void send() {
        listV = findViewById(R.id.listRuta);
        if (inflater == null)
            inflater = (LayoutInflater) ActivityListRumahTangga.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_persetujuan);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        CheckBox cekBox = dialog.findViewById(R.id.checkBox);
        final Button kirimListing = dialog.findViewById(R.id.kirimListing);
        kirimListing.setEnabled(false);

        kirimListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SynchronizeTask(activity).execute(SynchronizeTask.MODE_FINALIZE_BS, kodeBs);
//                new SynchronizeTask(activity).finalisasiBs(kodeBs, activity);
                dialog.dismiss();
            }
        });

        cekBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kirimListing.setEnabled(buttonView.isChecked());
            }
        });
        dialog.show();
    }

    private boolean isGpsActive() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void warningBstttd() {
        listV = findViewById(R.id.listRuta);
        if (inflater == null)
            inflater = (LayoutInflater) ActivityListRumahTangga.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_persetujuan);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView title = dialog.findViewById(R.id.warningTitle);
        title.setText(R.string.warning_sampling);

        CheckBox cekBox = dialog.findViewById(R.id.checkBox);
        cekBox.setText(R.string.warning_listing);
        final Button kirimListing = dialog.findViewById(R.id.kirimListing);
        kirimListing.setText(R.string.text_sampling);
        kirimListing.setEnabled(false);

        kirimListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilSampel(kodeBs);
                dialog.dismiss();
            }
        });

        cekBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kirimListing.setEnabled(buttonView.isChecked());
            }
        });
        dialog.show();
    }

    private void warningBstttd2() {
        listV = findViewById(R.id.listRuta);
        if (inflater == null)
            inflater = (LayoutInflater) ActivityListRumahTangga.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_persetujuan);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView title = dialog.findViewById(R.id.warningTitle);
        title.setText(R.string.warning_sampling);

        CheckBox cekBox = dialog.findViewById(R.id.checkBox);
        cekBox.setText(R.string.warning_listing);
        final Button kirimListing = dialog.findViewById(R.id.kirimListing);
        kirimListing.setText(R.string.text_finalisasi);
        kirimListing.setEnabled(false);

        kirimListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
                dialog.dismiss();
            }
        });

        cekBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kirimListing.setEnabled(buttonView.isChecked());
            }
        });
        dialog.show();
    }

    private void toIsiListing(RumahTangga rumahTangga, int posisi) {

        if (dbSampling.getBlokSensusByKode(kodeBs).getKodeBs().startsWith("02", 2)) {

            Intent toIsianListing = new Intent(ActivityListRumahTangga.this,
                    IsiRumahTanggaActivity.class);

            RumahTangga lastInsert = dbSampling.getLastUUP();

            if (rumahTangga == null) {
                toIsianListing.putExtra("status", status);
                toIsianListing.putExtra("kodeBs", kodeBs);
                toIsianListing.putExtra("posisi", posisi);
//                toIsianListing.putExtra("rutaBr", true);
                Log.d(TAG,"masuk ruta baru");
                if (lastInsert != null)
                    toIsianListing.putExtra(StaticFinal.BUNDLE_INSERT, lastInsert);

            } else {
                toIsianListing.putExtra("kodeBs", kodeBs);
                toIsianListing.putExtra("kodeUUP", rumahTangga.getKodeRuta());
                toIsianListing.putExtra("nama KRT", rumahTangga.getNamaKRT());
                toIsianListing.putExtra("posisi", posisi);
                if (lastInsert != null)
                    toIsianListing.putExtra(StaticFinal.BUNDLE_INSERT, lastInsert);

            }
            startActivity(toIsianListing);


        } else {
            Intent toIsianListing = new Intent(ActivityListRumahTangga.this,
                    IsiRumahTanggaActivity.class);
            RumahTangga lastInsert = dbSampling.getLastUUP();

            if (rumahTangga == null) {
                toIsianListing.putExtra("status", status);
                toIsianListing.putExtra("kodeBs", kodeBs);
//                toIsianListing.putExtra("rutaBr", true);
                Log.d(TAG,"masuk ruta baru");
                if (lastInsert != null)
                    toIsianListing.putExtra(StaticFinal.BUNDLE_INSERT, lastInsert);

            } else {
                toIsianListing.putExtra("kodeBs", kodeBs);
                toIsianListing.putExtra("kodeUUP", rumahTangga.getKodeRuta());
                toIsianListing.putExtra("nama KRT", rumahTangga.getNamaKRT());
                toIsianListing.putExtra("posisi", posisi);
                if (lastInsert != null)
                    toIsianListing.putExtra(StaticFinal.BUNDLE_INSERT, lastInsert);
            }
            startActivity(toIsianListing);
        }
    }

    private void callContact(String nomor) {
        Uri uri = Uri.parse("tel:" + nomor);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);

        try {
            context.startActivity(it);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Failed Call", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}