package org.odk.collect.android.pkl.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.text.Spannable;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.activity.ActivityListBlokSensus;
import org.odk.collect.android.pkl.activity.ActivitySync;
import org.odk.collect.android.pkl.activity.IsiRumahTanggaActivity;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.RumahTangga;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.pkl.util.MasterPassword;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by susi on 19/12/2016.
 */

public class RumahTanggaAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<RumahTangga> ruta;
    private ArrayList<RumahTangga> allRuta;
    String searchKey = "";
    DatabaseSampling dbSampling = DatabaseSampling.getInstance();
    CapiPreference pref = CapiPreference.getInstance();
    String nimKortim = (String) pref.get(CapiKey.KEY_NIM_KORTIM);
    String nim = (String) pref.get(CapiKey.KEY_NIM);


    public RumahTanggaAdapter(Activity activity, ArrayList<RumahTangga> ruta) {
        this.activity = activity;
        this.ruta = ruta;
        allRuta = new ArrayList<RumahTangga>();
        allRuta.addAll(ruta);
    }

    @Override
    public int getCount() {
        return ruta.size();
    }

    @Override
    public Object getItem(int location) {
        return ruta.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.kotak_ruta, null);

        LinearLayout kotak_ruta = (LinearLayout) convertView.findViewById(R.id.kotak_rutaa);
        TextView nomorbs = (TextView) convertView.findViewById(R.id.nomorbs);
        TextView nomorbf = (TextView) convertView.findViewById(R.id.nomorbf);
        TextView NoUrut = (TextView) convertView.findViewById(R.id.nomorurut);
        TextView NamaKRT = (TextView) convertView.findViewById(R.id.namakepalarumahtangga);

        Button update = (Button) convertView.findViewById(R.id.update);
        Button info = (Button) convertView.findViewById(R.id.info);
        info.setClickable(true);
        info.setLongClickable(true);


        TextView FlagBstttd = (TextView) convertView.findViewById(R.id.bstttd);


        final RumahTangga item = ruta.get(position);

        nomorbs.setText(item.getBs());
        nomorbf.setText(item.getBf());
        NoUrut.setText(String.valueOf(item.getNoUrutRuta()));
        NamaKRT.setText(item.getNamaKRT());

        try {
            String namas = item.getNamaKRT().toLowerCase(Locale.getDefault());
            if (namas.contains(searchKey) && searchKey.length() != 0) {
                int startPos = namas.indexOf(searchKey);
                int endPos = startPos + searchKey.length();

                Spannable spanText = Spannable.Factory.getInstance().newSpannable(NamaKRT.getText());
                spanText.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                NamaKRT.setText(spanText, TextView.BufferType.SPANNABLE);
            }
        } catch (Exception e) {

        }


        String nomorBs = item.getBs().toLowerCase(Locale.getDefault());
        if (nomorBs.contains(searchKey) && searchKey.length() != 0) {
            //Log.e("test", namas + " contains: " + search);
            int startPos = nomorBs.indexOf(searchKey);
            int endPos = startPos + searchKey.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(nomorbs.getText());
            spanText.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            nomorbs.setText(spanText, TextView.BufferType.SPANNABLE);
        }

        String nomorBf = item.getBf().toLowerCase(Locale.getDefault());
        if (nomorBf.contains(searchKey) && searchKey.length() != 0) {
            //Log.e("test", namas + " contains: " + search);
            int startPos = nomorBf.indexOf(searchKey);
            int endPos = startPos + searchKey.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(nomorbf.getText());
            spanText.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            nomorbf.setText(spanText, TextView.BufferType.SPANNABLE);
        }

//        fungsi tombol update


//        final View finalConvertView = convertView;
//        update.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (item.getKodeBs().substring( 2,4 ).equals( "05" ) ) {
//
//                    Intent toIsianListing = new Intent( activity,
//                            IsiRumahTanggaActivityD3.class );
//
//                    RumahTangga lastInsert = dbSampling.getLastRumahTangga();
//
//                    if (item == null) {
//                        toIsianListing.putExtra( "kodeBs", item.getKodeBs());
//                        if (lastInsert != null)
//                            toIsianListing.putExtra( StaticFinal.BUNDLE_INSERT, lastInsert );
//
//                    } else {
//                        toIsianListing.putExtra( "kodeBs", item.getKodeBs() );
//                        toIsianListing.putExtra( "kodeUUP", item.getKodeRuta() );
//                        toIsianListing.putExtra( "nama KRT", item.getNamaKrt() );
//                        toIsianListing.putExtra( "posisi", 0 );
//                        if (lastInsert != null)
//                            toIsianListing.putExtra( StaticFinal.BUNDLE_INSERT, lastInsert );
//
//                    }
//
//                    final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
//
//                    if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
//
//                        activity.startActivity( toIsianListing );
//
//                    } else {
//
//                        final AlertDialog.Builder builder = new AlertDialog.Builder( activity );
//
//                        builder.setTitle("Peringatan")
//                                .setMessage("Pastikan GPS anda berfungsi dengan benar")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();
//                    }
//
//
//                } else {
//
//                    Intent toIsianListing = new Intent( activity,
//                            IsiRumahTanggaActivity.class );
//                    RumahTangga lastInsert = dbSampling.getLastRumahTangga();
//
//                    if (item == null) {
//                        toIsianListing.putExtra( "kodeBs", item.getKodeBs());
//                        if (lastInsert != null)
//                            toIsianListing.putExtra( StaticFinal.BUNDLE_INSERT, lastInsert );
//
//                    } else {
//                        Log.e("update1:", item.getKodeRuta());
//                        toIsianListing.putExtra( "kodeBs", item.getKodeBs());
//                        toIsianListing.putExtra( "kodeUUP", item.getKodeRuta() );
//                        toIsianListing.putExtra( "nama KRT", item.getNamaKrt() );
//                        toIsianListing.putExtra( "posisi", 0 );
//                        if (lastInsert != null)
//                            toIsianListing.putExtra( StaticFinal.BUNDLE_INSERT, lastInsert );
//
//                    }
//
//                    final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
//
//                    if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
//
//                        activity.startActivity( toIsianListing );
//
//                    } else {
//
//                        final AlertDialog.Builder builder = new AlertDialog.Builder( activity );
//
//                        builder.setTitle("Peringatan")
//                                .setMessage("Pastikan GPS anda berfungsi dengan benar")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();
//                    }
//                }
//            }
//        });

        info.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.detail_ruta);
                dialog.setTitle("Detail Rumah Tangga Usaha Pariwisata");
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView noUrutUUP = (TextView) dialog.findViewById(R.id.no_uup);

                TextView noSegmen = (TextView) dialog.findViewById(R.id.noSegmen);
                noSegmen.setText(String.valueOf(item.getNoSLS()));

                TextView bf = (TextView) dialog.findViewById(R.id.bf);
                bf.setText(item.getBf());

                TextView bs = (TextView) dialog.findViewById(R.id.bs);
                bs.setText(item.getBs());

                TextView noUrutRuta = (TextView) dialog.findViewById(R.id.no_ruta);
                noUrutRuta.setText(String.valueOf(item.getNoUrutRuta()));

                TextView namaKRT = (TextView) dialog.findViewById(R.id.namaKRT);
                namaKRT.setText(item.getNamaKRT());

                TextView alamat = (TextView) dialog.findViewById(R.id.alamat);
                alamat.setText(item.getAlamat());

                TextView jumlah_isUUP = (TextView) dialog.findViewById(R.id.jumlah_isUUP);
                jumlah_isUUP.setText(item.getJumlahEligible());

                TextView no_urut_isUUP = (TextView) dialog.findViewById(R.id.no_urut_isUUP);
                no_urut_isUUP.setText(item.getNoUrutRuta());

                TextView namaPemilik = (TextView) dialog.findViewById(R.id.namaPemilik);
                if(item.getNamaKRT().equals("-")){
                    namaPemilik.setText("-");
                }else{
                    namaPemilik.setText(item.getNamaKRT());
                }

                TextView kedudukanUP = (TextView) dialog.findViewById(R.id.kedudukan_up);
//                if (item.getKedudukanUP().equals("1")) {
//                    kedudukanUP.setText("Pemilik");
//                } else if (item.getKedudukanUP().equals("2")) {
//                    kedudukanUP.setText("Pengelola");
//                } else {
//                    kedudukanUP.setText("-");
//                    noUrutUUP.setText("-");
//                }

                TextView statusKelola = (TextView) dialog.findViewById(R.id.status_kelola);
//                if (item.getStatusKelola().equals("1")) {
//                    statusKelola.setText("Ya, Bertanggung jawab");
//                } else if (item.getStatusKelola().equals("0")) {
//                    statusKelola.setText("Tidak");
//                } else {
//                    statusKelola.setText("-");
//                    noUrutUUP.setText("-");
//                }

                TextView lokasiUP = (TextView) dialog.findViewById(R.id.lokasiUP);
//                if (item.getLokasiUP().equals("1")) {
//                    lokasiUP.setText("Dalam Kota Batu");
//                    noUrutUUP.setText(item.getNoUrutUUP());
//                } else if (item.getLokasiUP().equals("0")) {
//                    lokasiUP.setText("Luar Kota Batu");
//                } else {
//                    lokasiUP.setText("-");
//                    noUrutUUP.setText("-");
//                }

                TextView jenisUP = (TextView) dialog.findViewById(R.id.jenis_pariwisata);
//                if (item.getJenisUUP().equals("1")) {
//                    jenisUP.setText("Jasa Transportasi Wisata");
//                } else if (item.getJenisUUP().equals("2")) {
//                    jenisUP.setText("Jasa Penyediaan Makanan dan Minuman");
//                } else if (item.getJenisUUP().equals("3")) {
//                    jenisUP.setText("Penyediaan Akomodasi");
//                } else {
//                    jenisUP.setText("-");
//                }
//                TextView noHp = (TextView) dialog.findViewById(R.id.no_hp);
//                if (item.getNoHp().equals("")) {
//                    noHp.setText("-");
//                } else {
//                    noHp.setText(item.getNoHp());
//                }

                TextView waktu = (TextView) dialog.findViewById(R.id.waktu);
                waktu.setText(item.getTime());


                //TODO CEK INI BUD

//                if (item.getKodeBs().substring( 2,4 ).equals( "05" )) {
//                    if ("1".equals(item.getLuasPanen())) {
//                        pendidikanKRT.setText(R.string.bngst42);
//                    } else if ("2".equals(item.getLuasPanen())) {
//                        pendidikanKRT.setText(R.string.bngst52);
//                    } else if ("3".equals(item.getLuasPanen())) {
//                        pendidikanKRT.setText(R.string.bngst62);
//                    } else {
//                        pendidikanKRT.setText("Kode Luas Pertanian tidak Diketahui");
//                    }
//                } else {
//                    if ("1".equals(item.getLuasPanen())) {
//                        pendidikanKRT.setText(R.string.bngst12);
//                    } else if ("2".equals(item.getLuasPanen())) {
//                        pendidikanKRT.setText(R.string.bngst22);
//                    } else if ("3".equals(item.getLuasPanen())) {
//                        pendidikanKRT.setText(R.string.bngst32);
//                    } else {
//                        pendidikanKRT.setText("Kode Luas Pertanian tidak Diketahui");
//                    }
//                }

                TextView dialogButton = (TextView) dialog.findViewById(R.id.closeButton);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

//        Log.d("infoHide", item.getNoUrutRuta());
        if (!nimKortim.equalsIgnoreCase(nim) && dbSampling.getBlokSensusByKode(item.getKodeBs()).getStatus().equalsIgnoreCase("listing")) {
            info.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    RumahTangga ruta = item;
                    option(ruta);
                    return true;
                }
            });
        }
        return convertView;
    }

    private void option(final RumahTangga ruta) {
        View convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.judul_ruta, null);
        TextView judulRuta = (TextView) convertView.findViewById(R.id.judulRT);

        judulRuta.setText("Aksi Untuk Ruta " + ruta.getNamaKRT());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCustomTitle(convertView)
                .setItems(R.array.menuRuta, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                            case 1:
                                toIsiListing(ruta, which);
                                break;
                            case 2:
                                //TODO TARO INTENT CALL NUMBER
//                                final Boolean hasNomor;
//                                final String nomor = ruta.getNoHp();
//                                final String nomor = "0";
//                                if (nomor.equalsIgnoreCase("")) {
//                                    hasNomor = false;
//                                } else {
//                                    hasNomor = true;
//                                }
//
//                                if (hasNomor) {
//                                    callContact(nomor);
//                                } else {
//                                    Toast.makeText(activity, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
//                                }

                                break;
                            case 3:
                                passwordHapusRutaKortim(ruta);
                                break;
                        }
                    }
                })
                .show();
    }

    private void callContact(String nomor) {
        Uri uri = Uri.parse("tel:" + nomor);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);

        try {
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Failed Call", Toast.LENGTH_SHORT).show();
        }
    }

    public void toIsiListing(RumahTangga rumahTangga, int posisi) {
//        Log.d( "nashir1", dbSampling.getBlokSensusByKode(kodeBs).getKodeBs());

            Intent toIsianListing = new Intent(activity,
                    IsiRumahTanggaActivity.class);
            RumahTangga lastInsert = dbSampling.getLastUUP();

            if (rumahTangga == null) {
//                toIsianListing.putExtra( "status", status );
                toIsianListing.putExtra("kodeBs", rumahTangga.getKodeBs());
                if (lastInsert != null)
                    toIsianListing.putExtra(StaticFinal.BUNDLE_INSERT, lastInsert);

            } else {
                toIsianListing.putExtra("kodeBs", rumahTangga.getKodeBs());
                toIsianListing.putExtra("kodeUUP", rumahTangga.getKodeRuta());
                toIsianListing.putExtra("nama KRT", rumahTangga.getNamaKRT());
                toIsianListing.putExtra("posisi", posisi);
                if (lastInsert != null)
                    toIsianListing.putExtra(StaticFinal.BUNDLE_INSERT, lastInsert);

            }
            activity.startActivity(toIsianListing);

    }

    public void passwordHapusRutaKortim(final RumahTangga ruta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Hapus Rumah Tangga membutuhkan persetujuan Kortim. \nMasukan password master yang diperoleh dari Kortim.");
        final EditText input = new EditText(activity);
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
                        Toast.makeText(activity, "berhasil dihapus", Toast.LENGTH_SHORT).show();
                        ActivitySync.backupLocal(activity);
                        activity.startActivity(new Intent(activity, ActivityListBlokSensus.class));
//                        activity.startActivity(new Intent(activity, ActivityListRumahTangga.class));


                    } catch (Exception e) {
                        Log.d("Hapus ", "Ruta Berhasil " + e);
                    }
                    dialog.dismiss();
                } else {
                    try {
                        Toast.makeText(activity, "Password salah, Rumah Tangga gagal dihapus", Toast.LENGTH_SHORT).show();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                ArrayList<RumahTangga> filteredRuta = new ArrayList<>();

                if (constraint == "") {
                    filteredRuta.addAll(allRuta);
                    searchKey = "";
                } else {
                    searchKey = constraint.toString();
                    for (RumahTangga rt : allRuta) {
                        String nama = rt.getNamaKRT().toLowerCase();
                        String noRuta = String.valueOf(rt.getNoUrutRuta()).toLowerCase();
                        String noBf = rt.getBf().toLowerCase();
                        String noBs = rt.getBs().toLowerCase();
                        String tipeBangunanSensus = "";
                        /*switch (rt.getType()) {
                            case 1:
                                tipeBangunanSensus = "bstt bangunan sensus tempat tinggal";
                                break;
                            case 2:
                                tipeBangunanSensus = "bsttc bangunan sensus tempat tinggal campuran";
                                break;
                            case 3:
                                tipeBangunanSensus = "bstttd bstttt bangunan sensus tempat tinggal tidak ditemui";
                                break;
                            case 4:
                                tipeBangunanSensus = "bsttrk rk bangunan sensus tempat tinggal ruta khusus";
                                break;
                            case 5:
                                tipeBangunanSensus = "bsbtt bangunan sensus bukan tempat tinggal";
                                break;
                        }

                        tipeBangunanSensus.toLowerCase();
*/
                        if (nama.contains(constraint) || noRuta.contains(constraint) || noBf.contains(constraint)
                                || noBs.contains(constraint)) {
                            filteredRuta.add(rt);
                        }


                    }

                }
                result.values = filteredRuta;
                result.count = filteredRuta.size();

                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ruta.clear();
                ruta.addAll((ArrayList<RumahTangga>) results.values);
                searchKey = constraint.toString();
                notifyDataSetChanged();
            }
        };
    }
}
