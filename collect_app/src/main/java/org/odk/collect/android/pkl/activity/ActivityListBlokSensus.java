package org.odk.collect.android.pkl.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.AdapterBlokSensus;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.SyncManager;
import org.odk.collect.android.pkl.task.SynchronizeTask;
import org.odk.collect.android.pkl.util.MasterPassword;
import org.odk.collect.android.pkl.util.Pesan;
import org.odk.collect.android.pkl.util.Sampling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author isfann
 */

public class ActivityListBlokSensus extends AppCompatActivity {
    ListView listViewBs;
    AdapterBlokSensus adapterBlokSensus;
    private final String TAG = "LIST BS";
    DatabaseSampling db;
    Toolbar toolbar;
    ArrayList<BlokSensus> listBs;
    ImageButton btnTmbBS;
    private LayoutInflater inflater;
    private TextView empty;
    ActivityListBlokSensus activity;
    HashMap<String, String> userDetails;
    SyncManager syncManager;
    String nim, deviceId, nimKortim;
    MenuItem menuUpload;
    boolean refreshData;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bs, menu);
        menuUpload = menu.findItem(R.id.action_sync);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SYNC_ALL);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blok_sensus);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("LIST BLOK SENSUS");

        activity = this;

        syncManager = new SyncManager(activity);
        CapiPreference preference = CapiPreference.getInstance();

        nim = (String) preference.get(CapiKey.KEY_NIM);
        nimKortim = (String) preference.get(CapiKey.KEY_NIM_KORTIM);
        deviceId = syncManager.getDeviceId();

        db = DatabaseSampling.getInstance();

        empty = (TextView) findViewById(R.id.empty);
        listViewBs = (ListView) findViewById(R.id.list);

        listBs = new ArrayList<>();
        listBs = db.getListBlokSensus();

        refreshData = getIntent().getBooleanExtra("refreshData", true);

        if (refreshData) {
            new SynchronizeTask(activity).execute(SynchronizeTask.MODE_SYNC_ALL);
        } else {
            showAllBs();
        }


        listViewBs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ActivityListBlokSensus.this, ActivityListRumahTangga.class);
                i.putExtra("kodeBs", listBs.get(position).getKodeBs());
                i.putExtra("status", listBs.get(position).getStatus());
//                Log.d("kodeBs", listBs.get(position).getKodeBs());
                i.putExtra("mode", ActivityListRumahTangga.MODE_ALL);
                Log.d(TAG, "onItemClick: " + listBs.get(position).getKodeBs());
                startActivity(i);
            }
        });

//        listViewBs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                parent.setSelection(position);
//                option(listBs.get(position));
//                Log.v("Long Clicked", "pos" + String.valueOf(listBs.get(position).getStatus()));
//                return true;
//            }
//        });

//        btnTmbBS = (ImageButton) findViewById( R.id.add_button );
//        btnTmbBS.setVisibility( View.GONE );
//        btnTmbBS.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent a = new Intent( ActivityListBlokSensus.this,
//                        IsiBlokSensusActivity.class );
//                startActivity( a );
//            }
//        } );

    }

    private void setVisible(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void setInvisible(View v) {
        v.setVisibility(View.GONE);
    }

    private void option(final BlokSensus ds) {
        View convertView;
        if (inflater == null)
            inflater = (LayoutInflater) ActivityListBlokSensus.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.judul_bs, null);
        TextView judulBS = (TextView) convertView.findViewById(R.id.judulBS);
        judulBS.setText("Blok Sensus " + ds.getNoBs());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(convertView)
                .setItems(R.array.menu, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent a = new Intent(ActivityListBlokSensus.this,
                                        IsiBlokSensusActivity.class);
                                a.putExtra("idBs", ds.getStatus());
                                a.putExtra("no_bs", ds.getNoBs());
                                a.putExtra("kodeBs", ds.getKodeBs());
                                startActivity(a);
                                break;
                            case 1:
                                konfirmasiHapusBs(ds);
                                break;
                            case 2:
                                String bsFlag = ds.getBsFlag(getApplicationContext());
                                String status = ds.getStatus();
                                String kodeBs = ds.getKodeBs();
                                switch (status) {
                                    case BlokSensus.FLAG_BS_PROSES_LISTING:
                                        Pesan.tampilkan("Listing Belum Selesai", "Selesaikan listing dahulu", ActivityListBlokSensus.this);
                                        break;
                                    case BlokSensus.FLAG_BS_READY:
                                        Pesan.tampilkan("Sampling Belum Dilakukan", "Tidak bisa menghapus hasil sampling", ActivityListBlokSensus.this);
                                        break;
                                    case BlokSensus.FLAG_BS_SAMPLED:
                                        hapusHasilSampling(kodeBs);
                                        break;
                                    case BlokSensus.FLAG_BS_SAMPLE_UPLOADED:
                                        hapusHasilSampling(kodeBs);
                                        break;
                                }
                        }

//                        if (which == 0) {
//                            Intent a = new Intent(ActivityListBlokSensus.this,
//                                    IsiBlokSensusActivity.class);
//                            a.putExtra("idBs", ds.getStatus());
//                            a.putExtra("no_bs", ds.getNoBs());
//                            startActivity(a);
//                        } else {
//                            konfirmasiHapusBs(ds);
//                        }
                    }
                })
                .show();
    }

    private void konfirmasiHapusBs(final BlokSensus ds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityListBlokSensus.this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Hapus Blok Sensus membutuhkan persetujuan Kortim. \nMasukan password master yang diperloeh dari Kortim.");
        final EditText input = new EditText(ActivityListBlokSensus.this);
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
                        db.deleteBS(ds.getKodeBs());
                        Toast.makeText(ActivityListBlokSensus.this, "Blok Sensus berhasil dihapus", Toast.LENGTH_SHORT).show();
                        ActivitySync.backupLocal(getApplicationContext());
//                        new UpdateUi(activity).execute();
                        showAllBs();
                    } catch (Exception e) {
                        Log.d("Hapus ", "BS Berhasil " + e);
                    }
                    dialog.dismiss();
                } else {
                    try {
                        Toast.makeText(ActivityListBlokSensus.this, "Password salah, Blok Sensus gagal dihapus", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("Hapus ", "BS Gagal " + e);
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

    private void hapusHasilSampling(final String kodeBs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityListBlokSensus.this);
        builder.setTitle("Sampel Sudah diambil")
                .setMessage("Penghapusan hasil sampling memerlukan persetujuan Kortim." +
                        "\nLanjutkan penghapusan sampel?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        konfirmasiHapusHasilSampling(kodeBs);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void konfirmasiHapusHasilSampling(final String kodeBs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityListBlokSensus.this);
        builder.setTitle("Konfirmasi Kortim");
        builder.setMessage("Masukan Password Master yang diperoleh dari Koritm.");
        final EditText input = new EditText(ActivityListBlokSensus.this);
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
                        if (Sampling.hapusHasilSampling(kodeBs, getApplicationContext())) {
                            ActivitySync.backupLocal(getApplicationContext());
                            Toast.makeText(ActivityListBlokSensus.this, "Penghapusan Hasil Sampling Berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityListBlokSensus.this, "Penghapusan Hasil Sampling Gagal", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ActivityListBlokSensus.this, "Penghapusan Hasil Sampling Gagal", Toast.LENGTH_SHORT).show();
                        Log.d("Sampling ", "Gagal: " + e);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(ActivityListBlokSensus.this, "Password salah, Penghapusan Hasil Sampling tidak dilakukan!", Toast.LENGTH_LONG).show();
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
        showAllBs();
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals("IsSync")){
//            updateUi();
//        }
//    }

    public void showAllBs() {
        listBs = new ArrayList<>();
        DatabaseSampling dbSampling = DatabaseSampling.getInstance();
        listBs = dbSampling.getListBlokSensus();
        if (listBs.size() == 0) {
            setVisible(empty);
        } else {
            setInvisible(empty);
        }
        adapterBlokSensus = new AdapterBlokSensus(activity, listBs);
        listViewBs.setAdapter(adapterBlokSensus);
        adapterBlokSensus.notifyDataSetChanged();

    }

//    void updateUi(){
//        if (syncManager.isSync()){
//            Log.i(TAG, "updateUi: uploading");
//            menuUpload.setEnabled(false);
//            menuUpload.setIcon(R.drawable.ic_action_upload_disabled);
//        }else{
//            Log.i(TAG, "updateUi: not uploadign");
//            menuUpload.setEnabled(true);
//            menuUpload.setIcon(R.drawable.ic_action_upload);
//        }
//    }
}