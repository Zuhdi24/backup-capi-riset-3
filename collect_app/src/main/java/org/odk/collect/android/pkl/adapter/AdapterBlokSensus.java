package org.odk.collect.android.pkl.adapter;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import org.apache.commons.lang3.text.WordUtils;
import org.odk.collect.android.R;
import org.odk.collect.android.pkl.activity.ActivityListBlokSensus;
import org.odk.collect.android.pkl.activity.ActivityListRumahTangga;
import org.odk.collect.android.pkl.activity.ActivitySync;
import org.odk.collect.android.pkl.database.DBhandler;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.task.SynchronizeTask;
import org.odk.collect.android.pkl.util.MasterPassword;
import org.odk.collect.android.pkl.util.Pesan;
import org.odk.collect.android.pkl.util.Sampling;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tim PKL
 */

public class AdapterBlokSensus extends BaseAdapter {
    private ActivityListBlokSensus context;
    private LayoutInflater inflater;
    private ArrayList<BlokSensus> bs;
    private DatabaseSampling dbSampling;
    private DBhandler dbExtra;
    String nim, nimKortim, kodeJabatan;
    private CapiPreference preference;


    public AdapterBlokSensus(ActivityListBlokSensus context, ArrayList<BlokSensus> bs) {
        this.context = context;
        this.bs = bs;
        dbSampling = DatabaseSampling.getInstance();
        dbExtra = DBhandler.getInstance();
        preference = CapiPreference.getInstance();
    }

    private void ambilSampel(final String kodeBs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sampel belum diambil");
        builder.setMessage("Pengambilan Sampel memerlukan persetujuan Kortim. \nMasukan password master yang diperoleh dari Kortim.");
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

        nimKortim = (String) preference.get(CapiKey.KEY_NIM_KORTIM);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MasterPassword.getPassword(nimKortim).equals(input.getText().toString())) {
                    try {
                        if (dbSampling.getBlokSensusByKode(kodeBs).getKodeBs().substring(2, 4).equals("02")) {
                            if (Sampling.ambilSampel(CapiKey.N_SAMPLE_D3, kodeBs, context)) {
                                if (dbSampling.updateStatusBlokSensus(kodeBs, BlokSensus.FLAG_BS_SAMPLED)) {
                                    Toast.makeText(context, "Sampling Berhasil", Toast.LENGTH_SHORT).show();
                                    new SynchronizeTask(context).execute(SynchronizeTask.MODE_SEND_SAMPLE, kodeBs);
                                    //                                new SynchronizeTask(activity).kirimSampel(kodeBs);
                                    ActivitySync.backupLocal(context);
                                } else {
                                    Toast.makeText(context, "Sampling ditarik, Status BS belum diubah", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, "Sampling Gagal", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (Sampling.ambilSampel(CapiKey.N_SAMPLE_D4, kodeBs, context)) {
                                if (dbSampling.updateStatusBlokSensus(kodeBs, BlokSensus.FLAG_BS_SAMPLED)) {
                                    Toast.makeText(context, "Sampling Berhasil", Toast.LENGTH_SHORT).show();
                                    new SynchronizeTask(context).execute(SynchronizeTask.MODE_SEND_SAMPLE, kodeBs);
                                    //                                new SynchronizeTask(activity).kirimSampel(kodeBs);
                                    ActivitySync.backupLocal(context);
                                } else {
                                    Toast.makeText(context, "Sampling ditarik, Status BS belum diubah", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, "Sampling Gagal", Toast.LENGTH_SHORT).show();
                            }
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

    private void warningBstttd(final String kodeBs) { //TODO UBAH INI BUD
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_persetujuan);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView title = (TextView) dialog.findViewById(R.id.warningTitle);
        title.setText("Masih ada rumah tangga yang belum ditemui. Apakah anda yakin ingin melakukan sampling?");

        CheckBox cekBox = (CheckBox) dialog.findViewById(R.id.checkBox);
        cekBox.setText("Dengan ini saya menyatakan bahwa apa yang saya lakukan dapat saya pertanggung jawabkan");
        final Button kirimListing = (Button) dialog.findViewById(R.id.kirimListing);
        kirimListing.setText("Lakukan Sampling");
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
                if (buttonView.isChecked()) {
                    kirimListing.setEnabled(true);
                } else {
                    kirimListing.setEnabled(false);
                }
            }
        });
        dialog.show();
    }

    @Override
    public int getCount() {
        return bs.size();
    }

    @Override
    public Object getItem(int location) {
        return bs.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.kotak_bs, null);


        TextView noBs = (TextView) convertView.findViewById(R.id.noBs);
        TextView namaPcl = (TextView) convertView.findViewById(R.id.namaPCL);
        TextView desa = (TextView) convertView.findViewById(R.id.desa);
        TextView kecamatan = (TextView) convertView.findViewById(R.id.kecamatan);
        TextView kabupaten = (TextView) convertView.findViewById(R.id.kabupaten);
        TextView provinsi = (TextView) convertView.findViewById(R.id.provinsi);
//        TextView stratifikasi = (TextView) convertView.findViewById(R.id.stratifikasi);

        TextView jumlahRuta = (TextView) convertView.findViewById(R.id.jumlahRuta);
//        TextView ruta_tp = (TextView) convertView.findViewById(R.id.ruta_pertanian);
//        TextView bukan_ruta_tp = (TextView) convertView.findViewById(R.id.bukan_ruta_pertanian);

        TextView statusBs = (TextView) convertView.findViewById(R.id.status_bs);
        TextView statusBsListing = (TextView) convertView.findViewById(R.id.status_listing);
        TextView statusBsReady = (TextView) convertView.findViewById(R.id.status_ready);
        TextView statusBsSampled = (TextView) convertView.findViewById(R.id.status_sampled);
        TextView statusBssampleUploaded = (TextView) convertView.findViewById(R.id.status_sample_uploaded);

        final BlokSensus item = bs.get(position);

        nim = item.getNim();
        kodeJabatan = (String) preference.get(CapiKey.KEY_ID_JABATAN);

        String kab = item.getNamaKabupaten();
        String kec = item.getNamaKecamatan();
        String des = item.getNamaDesa();

        noBs.setText("Blok Sensus " + item.getNoBs());
        des = des.toLowerCase();
        desa.setText(", Kel. " + WordUtils.capitalize(des)); //TODO Capitalize
        kecamatan.setText("Kec. " + kec);
        kabupaten.setText(kab + ", ");
        provinsi.setText("Jawa Timur");
//        if ("1".equals(item.getStratifikasi())) {
//            stratifikasi.setText("Perkotaan");
//        } else if ("2".equals(item.getStratifikasi())) {
//            stratifikasi.setText("Perdesaan");
//        } else {
//            stratifikasi.setText("Kode Stratifikasi tidak diketahui");
//        }

//        Log.d("hasil", String.valueOf(Integer.valueOf(item.getJumlahRTBaru())-Integer.valueOf(item.getJumlahRTInternet())) );
//        ruta_tp.setText(item.getJumlahRTInternet());
//        bukan_ruta_tp.setText(String.valueOf(Integer.valueOf(item.getJumlahRTBaru())-Integer.valueOf(item.getJumlahRTInternet())));
//        jumlahRuta.setText(item.getJumlahRTBaru());

//        ruta_tp.setText(String.valueOf(dbSampling.getJumlahUUPByType(item.getKodeBs(), 1)));
//        bukan_ruta_tp.setText(String.valueOf(dbSampling.getJumlahUUPByType(item.getKodeBs(), 2)));
        jumlahRuta.setText(String.valueOf(dbSampling.getJumlahUUP(item.getKodeBs())));

        statusBs.setText("STATUS " + item.getStatus().toUpperCase());

        Typeface QUICKSAND_BOLD = ResourcesCompat.getFont(context, R.font.quicksand_bold);
        switch (item.getStatus()) {
            case BlokSensus.FLAG_BS_PROSES_LISTING:
                statusBsListing.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                statusBsListing.setTypeface(QUICKSAND_BOLD);
                break;

            case BlokSensus.FLAG_BS_READY:
                statusBsReady.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                statusBsReady.setTypeface(QUICKSAND_BOLD);
                break;

            case BlokSensus.FLAG_BS_SAMPLED:
                statusBsSampled.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                statusBsSampled.setTypeface(QUICKSAND_BOLD);
                break;

            case BlokSensus.FLAG_BS_SAMPLE_UPLOADED:
                statusBssampleUploaded.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                statusBssampleUploaded.setTypeface(QUICKSAND_BOLD);
                break;
        }

        final LinearLayout kdr = (LinearLayout) convertView.findViewById(R.id.kotak_bs);
        final LinearLayout cd = (LinearLayout) convertView.findViewById(R.id.cardBS);
        final LinearLayout contentView = (LinearLayout) convertView.findViewById(R.id.bsContent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            ((LinearLayout) vg.findViewById(R.id.bsContent)).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            kdr.setLayoutTransition(layoutTransition);
            cd.setLayoutTransition(layoutTransition);
        }
//        final Boolean isExpanded = false;
//        final LinearLayout cardView = (LinearLayout) convertView.findViewById(R.id.cardDesa);
        final ImageButton less = (ImageButton) convertView.findViewById(R.id.less);
        final ImageButton more = (ImageButton) convertView.findViewById(R.id.more);

//        if(isExpanded){
        more.setVisibility(View.VISIBLE);
        less.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
//        }else{
//            less.setVisibility(View.VISIBLE);
//            more.setVisibility(View.GONE);
//            contentView.setVisibility(View.VISIBLE);
//        }

        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.setVisibility(View.VISIBLE);
                less.setVisibility(View.GONE);
                contentView.setVisibility(View.GONE);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                less.setVisibility(View.VISIBLE);
                more.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
            }
        });

        Button samplingButton = (Button) convertView.findViewById(R.id.samplingButton);

        String bsFlag = item.getBsFlag(context);
        String status = item.getStatus();
        switch (status) {
            case BlokSensus.FLAG_BS_PROSES_LISTING:
                samplingButton.setText("Ambil Sampel");
                samplingButton.setTextColor(context.getResources().getColor(R.color.accent_grey));
                samplingButton.setEnabled(false);
                samplingButton.setVisibility(View.GONE);
                break;
            case BlokSensus.FLAG_BS_READY:
                samplingButton.setText("Ambil Sampel");
                samplingButton.setEnabled(true);
                samplingButton.setVisibility(View.GONE);
                break;
            case BlokSensus.FLAG_BS_SAMPLED:
                samplingButton.setText("Lihat Sampel");
                samplingButton.setEnabled(true);
                break;
            case BlokSensus.FLAG_BS_SAMPLE_UPLOADED:
                samplingButton.setText("Lihat Sampel");
                samplingButton.setEnabled(true);
                break;
        }

        samplingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bsFlag = item.getBsFlag(context);
                String status = item.getStatus();
                String kodeBs = item.getKodeBs();
                switch (status) {
                    case BlokSensus.FLAG_BS_PROSES_LISTING:
                        Pesan.tampilkan("Listing Belum Selesai", "Selesaikan Listing sebelum memulai proses sampling", context);
                        break;
                    case BlokSensus.FLAG_BS_READY:
                        if (dbSampling.getJumlahUUP(kodeBs) < 10) {
                            warningBstttd(kodeBs);
                        } else {
                            ambilSampel(kodeBs);
                        }
                        notifyDataSetChanged();
                        break;
                    case BlokSensus.FLAG_BS_SAMPLED:
                        Intent i = new Intent(context, ActivityListRumahTangga.class);
                        i.putExtra("kodeBs", kodeBs);
                        i.putExtra("mode", ActivityListRumahTangga.MODE_SAMPEL);
                        context.startActivity(i);
                        break;
                    case BlokSensus.FLAG_BS_SAMPLE_UPLOADED:
                        Intent in = new Intent(context, ActivityListRumahTangga.class);
                        in.putExtra("kodeBs", kodeBs);
                        in.putExtra("mode", ActivityListRumahTangga.MODE_SAMPEL);
                        context.startActivity(in);
                        break;
                }

            }
        });

        Button listRutaButton = (Button) convertView.findViewById(R.id.listRutaButton);
        listRutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kodeBs = item.getKodeBs();
                Intent i = new Intent(context, ActivityListRumahTangga.class);
                i.putExtra("kodeBs", kodeBs);
                i.putExtra("mode", ActivityListRumahTangga.MODE_ALL);
                context.startActivity(i);
            }
        });

        if ("0".equals(kodeJabatan)) {
            namaPcl.setVisibility(View.GONE);
        } else {
            List<AnggotaTim> listAnggota = dbExtra.getAllAnggota();
            loop:
            for (AnggotaTim anggota : listAnggota) {
                if (nim.equals(anggota.getNimAng())) {
                    namaPcl.setText(anggota.getNamaAng());
                    break loop;
                }
            }
        }

        return convertView;
    }

}
