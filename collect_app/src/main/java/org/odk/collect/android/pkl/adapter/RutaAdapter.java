package org.odk.collect.android.pkl.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.RumahTangga;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by baykris on 1/24/2017.
 */

public class RutaAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<RumahTangga> ruta;
    private ArrayList<RumahTangga> allRuta;
    private Button generateKues;
    DatabaseSampling dbSampling;
    String kodeBs;
    String searchKey = "";

    public RutaAdapter(Activity activity, ArrayList<RumahTangga> ruta) {
        this.activity = activity;
        this.ruta = ruta;
        allRuta = ruta;
    }

    @Override
    public int getCount() {
        return ruta.size();
    }

    @Override
    public RumahTangga getItem(int location) {
        return ruta.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Button getGenerateKues (){
        return this.generateKues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.kotak_ruta2, null);

        TextView NoSegmen = (TextView) convertView.findViewById(R.id.no_segmen);
        TextView NoBf = (TextView) convertView.findViewById(R.id.bf);
        TextView NoBs = (TextView) convertView.findViewById(R.id.bs);
        TextView NoUrut = (TextView) convertView.findViewById(R.id.no_urt);
        TextView Alamat = (TextView) convertView.findViewById(R.id.alamat);
        TextView JumlahART = (TextView) convertView.findViewById(R.id.jumlah_art);
        TextView NamaPemilik = (TextView) convertView.findViewById(R.id.nama_pemilik);
        TextView statusEligible = (TextView) convertView.findViewById(R.id.status_eligible);
        Button petunjukArah = (Button) convertView.findViewById(R.id.petunjuk_arah_button);
        Button hubungiRuta = (Button) convertView.findViewById(R.id.hubungi_ruta_button);

        RumahTangga item = ruta.get(position);
        petunjukArah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?daddr=" + item.getLatitude() + "," + item.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                activity.startActivity(intent);
            }
        });

        hubungiRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+" + item.getNoHp()));
                activity.startActivity(intent);
            }
        });

        NoSegmen.setText(item.getNoSLS());
        NoUrut.setText("" + item.getNoUrutRuta());
        NoBf.setText(item.getBf());
        NoBs.setText(item.getBs());
        Alamat.setText(item.getAlamat());
        JumlahART.setText("" + item.getJumlahART());
        switch (item.getKodeEligible()){
            case "1":
                statusEligible.setText("Pernah Bekerja");
                break;
            case "2":
                statusEligible.setText("Sedang Bekerja");
                break;
            case "3":
                statusEligible.setText("Pernah dan Sedang Bekerja");
                break;
            default:
                statusEligible.setText("Tidak Eligible");
                break;
        }
        if (("").equals(item.getNamaKRT())) {
            NamaPemilik.setText("-");
        } else {
            NamaPemilik.setText(item.getNamaKRT());
        }

        String alamat = item.getAlamat().toLowerCase(Locale.getDefault());
        if (alamat.contains(searchKey) && searchKey.length() != 0) {
            //Log.e("test", namas + " contains: " + search);
            int startPos = alamat.indexOf(searchKey);
            int endPos = startPos + searchKey.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(Alamat.getText());
            spanText.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Alamat.setText(spanText, TextView.BufferType.SPANNABLE);
        }

        final RumahTangga rt = ruta.get(position);
        return convertView;
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
                        String alamat = rt.getAlamat().toLowerCase();
                        String tipeBangunanSensus = "";

                        /*switch (rt.getType()) {
                            case 1:
                                tipeBangunanSensus = "bstt bangunan sensus tempat tinggal";
                                break;
                            case 2:
                                tipeBangunanSensus = "bsttc bangunan sensus tempat tinggal campuran";
                                break;
                            case 3:
                                tipeBangunanSensus = "bstttd bstttt bangunan sensus tempat tinggal kosong";
                                break;
                            case 4:
                                tipeBangunanSensus = "bsttrk rk bangunan sensus tempat tinggal ruta khusus";
                                break;
                            case 5:
                                tipeBangunanSensus = "bsbtt bangunan sensus bukan tempat tinggal";
                                break;
                        }*/

                        //tipeBangunanSensus.toLowerCase();

                        if (nama.contains(constraint) || noRuta.contains(constraint) ||
                                noBf.contains(constraint) || noBs.contains(constraint) ||
                                alamat.contains(constraint)) {
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
//                ruta.clear();
                ruta = (ArrayList<RumahTangga>) results.values;
                searchKey = constraint.toString();
                notifyDataSetChanged();
            }
        };
    }
}
