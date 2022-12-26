package org.odk.collect.android.pkl.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.AnggotaTimAdapter;
import org.odk.collect.android.pkl.adapter.StatusListingAdapter;
import org.odk.collect.android.pkl.adapter.WilayahKerjaAdapater;
import org.odk.collect.android.pkl.adapter.holder.ParentListAnggotaTim;
import org.odk.collect.android.pkl.database.DBhandler;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.network.HttpHandler;
import org.odk.collect.android.pkl.network.VolleySingleton;
import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.PopUpTest;
import org.odk.collect.android.pkl.object.ProgressBlokPcl;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.task.RestoreTask;
import org.odk.collect.android.pkl.task.SynchronizeTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author TimSPD
 */


public class BerandaFragment extends Fragment {


    List<ProgressBlokPcl> progressBlokPcls; //untuk pcl
    List<AnggotaTim> anggotaTims; //untuk kortim
    ArrayList<BlokSensus> blokSensuses;
    List<ParentListItem> parentListItems; //untuk kortim

    TextView namaKortim, nimKortim;

    private DBhandler db;
    private DatabaseSampling dbs;

    CardView kortimSaya;

    private SynchronizeTask sync;

    private Context context;

    private RecyclerView recyclerBebanOrAnggota, mStatusListing;

    private ProgressBar progressAva;

    CircleImageView fotoProfil;

    private boolean isKoor;

    SharedPreferences defaultSharedPreferences;

    String serviceBaseUrl;

    public String urlAvatar;

    private SwipeRefreshLayout mRefresh;

    private CapiPreference preference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.pkl_fragment_beranda, container, false);
        getActivity().setTitle("BERANDA");

        context = getContext();
        sync = new SynchronizeTask((AppCompatActivity) getActivity());

        db = DBhandler.getInstance();
        dbs = DatabaseSampling.getInstance();

        kortimSaya = (CardView) rootView.findViewById(R.id.kortim_saya);
        namaKortim = (TextView) rootView.findViewById(R.id.nama_kortim_saya);
        nimKortim = (TextView) rootView.findViewById(R.id.nim_kortim_saya);
        recyclerBebanOrAnggota = (RecyclerView) rootView.findViewById(R.id.list_beban_cacah);
        recyclerBebanOrAnggota.setHasFixedSize(false);
        mStatusListing = (RecyclerView) rootView.findViewById(R.id.list_statuslisting);
        mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_progres);
        progressAva = (ProgressBar) rootView.findViewById(R.id.progress_ava);
        fotoProfil = (de.hdodenhof.circleimageview.CircleImageView) rootView.findViewById(R.id.fotoprofil);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkConnected()) {
                    new getProgres().execute();
                } else {
                    mRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), "Tidak terhubung jaringan !!", Toast.LENGTH_LONG).show();
                }


            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerBebanOrAnggota.setLayoutManager(llm);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mStatusListing.setLayoutManager(mLayoutManager);

        preference = CapiPreference.getInstance();
        String namaPcl = (String) preference.get(CapiKey.KEY_NAMA);
        String jabatan = (String) preference.get(CapiKey.KEY_JABATAN);
        String idJabatan = (String) preference.get(CapiKey.KEY_ID_JABATAN);
        String namaTim = (String) preference.get(CapiKey.KEY_NAMA_TIM);
        String nim = (String) preference.get(CapiKey.KEY_NIM);
        String nimKortimSaya = (String) preference.get(CapiKey.KEY_NIM_KORTIM);
        final String namaKortimSaya = (String) preference.get(CapiKey.KEY_NAMA_KORTIM);
        final String nomorKortimSaya = (String) preference.get(CapiKey.KEY_NOMOR_KORTIM);

        TextView txNama = (TextView) rootView.findViewById(R.id.nama_pcl);
        txNama.setText(namaPcl);
        TextView txJabatan = (TextView) rootView.findViewById(R.id.jabatan_pcl);
        txJabatan.setText(jabatan);
        TextView txTim = (TextView) rootView.findViewById(R.id.nama_tim);
        txTim.setText(namaTim); //test
        TextView txJudulExtra = (TextView) rootView.findViewById(R.id.title_extra);

        if (idJabatan.equals("1")) {
            txJudulExtra.setText("ANGGOTA TIM");
            kortimSaya.setVisibility(View.GONE);
            isKoor = true;
        } else {
            txJudulExtra.setText("PROGRES CACAH");
            kortimSaya.setVisibility(View.VISIBLE);
            isKoor = false;
        }
        TextView txNim = (TextView) rootView.findViewById(R.id.nim_pcl);
        txNim.setText(nim);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        serviceBaseUrl = defaultSharedPreferences.getString("host", "https://capi.pkl59.stis.ac.id");
//        urlAvatar = serviceBaseUrl + "assets/img/" + preference.get(CapiKey.KEY_NIM) + ".png";
        urlAvatar = "https://capi62.stis.ac.id/web-service-62/assets/img/" + preference.get(CapiKey.KEY_NIM) + ".png";
        Log.d("avatarAang", urlAvatar);
        progressAva.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(urlAvatar)
                .placeholder(R.drawable.user)
                .centerCrop()
                .fit()
                .into(fotoProfil, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressAva.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressAva.setVisibility(View.GONE);
                    }
                });

        /*
         * Text database
         */

        anggotaTims = db.getAllAnggota();
        progressBlokPcls = db.getAllProgres();
        blokSensuses = dbs.getListBlokSensus();
        parentListItems = getListAnggotaTim();
        createLog();

        CapiPreference preference = CapiPreference.getInstance();
        TextView txBcBs = (TextView) rootView.findViewById(R.id.bc_bloksensus);
        TextView txBcRt = (TextView) rootView.findViewById(R.id.bc_rumahtangga);
        txBcBs.setText((String) preference.get(CapiKey.KEY_JUMLAH_BS));
        txBcRt.setText((String) preference.get(CapiKey.KEY_JUMLAH_RUTA));

        if (isKoor && (parentListItems != null)) {
            recyclerBebanOrAnggota.setAdapter(new AnggotaTimAdapter(parentListItems, context, getResources()));
        } else if (parentListItems != null) {
            recyclerBebanOrAnggota.setAdapter(new WilayahKerjaAdapater(progressBlokPcls));
        }
        mStatusListing.setAdapter(new StatusListingAdapter(blokSensuses, getActivity()));

        if (kortimSaya.isEnabled()) {
            namaKortim.setText(namaKortimSaya);
            nimKortim.setText(nimKortimSaya);
            kortimSaya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customJudul = LayoutInflater.from(context).inflate(R.layout.judul_popup, null);
                    TextView judul = (TextView) customJudul.findViewById(R.id.judulBS);
                    TextView nomorPclKortim = (TextView) customJudul.findViewById(R.id.nomor_pclkortim);
                    judul.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    judul.setText(namaKortimSaya);
                    judul.setTextSize(16.0f);
                    String nomor = nomorKortimSaya;
                    Log.d("masukTeleponBeranda1", nomor);
                    Log.d("masukTeleponBeranda1", nomorKortimSaya);
                    final Boolean hasNomor;
                    if (nomor.equalsIgnoreCase("")) {
                        hasNomor = false;
                        nomorPclKortim.setVisibility(View.GONE);
                    } else {
                        nomorPclKortim.setVisibility(View.VISIBLE);
                        nomorPclKortim.setText("+" + nomorKortimSaya);
                        hasNomor = true;
                    }

                    final PopUpTest[] popUpTests = {
                            //new PopUpTest("WhatsApp", R.drawable.wa_unf),
                            new PopUpTest("Hubungi lewat telepon", R.drawable.ic_phone_black_24dp),
                            new PopUpTest("Kirim pesan (SMS)", R.drawable.ic_email_black_24dp)
                    };
                    ListAdapter la = new ArrayAdapter<PopUpTest>(
                            getContext(),
                            R.layout.list_popup_anggota,
                            R.id.text_pop,
                            popUpTests) {

                        @NonNull
                        @Override
                        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            TextView tv = (TextView) v.findViewById(R.id.text_pop);
                            tv.setText(popUpTests[position].getText());

                            tv.setCompoundDrawablesWithIntrinsicBounds(popUpTests[position].getResource(), 0, 0, 0);
                            //Add margin between image and text (support various screen densities)
                            int dp5 = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
                            tv.setCompoundDrawablePadding(dp5);
                            return v;
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCustomTitle(customJudul);
                    builder.setAdapter(la, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            switch (i) {
                                case 0:
                                    if (hasNomor) {
                                        callContact("+" + nomorKortimSaya);
                                    } else {
                                        Toast.makeText(context, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    if (hasNomor) {
                                        sendMessage("+" + nomorKortimSaya);
                                    } else {
                                        Toast.makeText(context, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
//                                case 2:
//                                    if (hasNomor) {
//                                        openWhatsApp(nomorKortimSaya);
//                                    } else {
//                                        Toast.makeText(context, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
//                                    }
//                                    break;
                            }
                        }
                    }).show();
                }
            });
        }


        return rootView;
    }

    public class getProgres extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getAllRefresh();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String urlExtra = serviceBaseUrl + "/getextracontent?nim=" + preference.get(CapiKey.KEY_NIM);
            String jsonStrExtra = sh.makeServiceCall(urlExtra);
            if (jsonStrExtra != null) {
                Log.d("abcdefgh", "masuk");
                try {
                    JSONObject jsonOb = new JSONObject(jsonStrExtra);
                    //bukan kortim
                    if (!isKoor) {
                        JSONArray jsonArray = jsonOb.getJSONArray("wilayah_kerja");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            String kode_bs = jsonArray.getJSONObject(j).getString("nama_bs");
                            String jumlah = jsonArray.getJSONObject(j).getString("jumlah");
                            String beban_cacah = jsonArray.getJSONObject(j).getString("beban_cacah");
                            String progres;

                            if (jumlah.equalsIgnoreCase("null")) {
                                progres = "0/" + beban_cacah;
                                Log.d("abcdefgh", "doInBackground: null progress cacah");
                            } else {
                                Log.d("abcdefgh", "doInBackground:  ada progress cacah");
                                progres = jumlah + "/" + beban_cacah;
                            }

                            ProgressBlokPcl pb = new ProgressBlokPcl(kode_bs, progres);
                            boolean statusUpdate = db.updateProgresBsPcl(pb);
                            Log.e("update data pcl", String.valueOf(statusUpdate));
                        }
                    }
                    //kalau kortim
                    else {

                        JSONArray jsonArray = jsonOb.getJSONArray("anggota_tim");

                        for (int j = 0; j < jsonArray.length(); j++) {
                            Log.d("abcdefgh", "ulang");
                            String nama = jsonArray.getJSONObject(j).getString("nama");
                            String nim = jsonArray.getJSONObject(j).getString("nim");
                            String progres = jsonArray.getJSONObject(j).getString("total_progress");
                            Log.d("abcdefgh", progres);
                            progres = String.valueOf(Math.round(Float.parseFloat(progres)));
                            Log.d("abcdefgh", progres + "2");
                            String nomor = jsonArray.getJSONObject(j).getString("no_hp");
                            AnggotaTim at = new AnggotaTim(nim, nama, progres + "%", nomor);
                            Log.d("abcdefgh", progres + "3");
                            boolean statusUpdate = db.updateProgresTim(at);
                            Log.d("abcdefgh", String.valueOf(statusUpdate));
                        }
                    }
                } catch (JSONException e) {
                    Log.d("abcdefgh", "doInBackground: gagal update pcl");
                    e.printStackTrace();
                }
            } else {
                Log.e("LoginActivity", "Couldn't get jsonExtra from server.");
            }
            return null;
        }
    }

//    private void openPKLM(String idGrup) {
//
//        boolean isWhatsappInstalled = whatsappInstalledOrNot("id.ac.stis.pklmessenger");
//        if (isWhatsappInstalled) {
//
//            Intent sendIntent = new Intent("android.intent.action.MAIN");
//            sendIntent.setComponent(new ComponentName("id.ac.stis.pklmessenger", "id.ac.stis.pklmessenger.ConversationFragment"));
//            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
//
//            context.startActivity(sendIntent);
//        } else {
//            Uri uri = Uri.parse("market://details?id=com.whatsapp");
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            Toast.makeText(context, "WhatsApp not Installed",
//                    Toast.LENGTH_SHORT).show();
//            context.startActivity(goToMarket);
//        }
//    }
//
//    private boolean whatsappInstalledOrNot(String uri) {
//        PackageManager pm = context.getPackageManager();
//        boolean app_installed = false;
//        try {
//            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
//            app_installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            app_installed = false;
//        }
//        return app_installed;
//
//    }

    private void callContact(String nomor) {
        Uri uri = Uri.parse("tel:" + nomor);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);

        try {
            context.startActivity(it);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Failed Call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String nomor) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: " + nomor));
        smsIntent.putExtra("sms_body", "");

        try {
            context.startActivity(smsIntent);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context,
                    "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllRefresh() {
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(new StringRequest(Request.Method.POST, serviceBaseUrl + "/listing",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GAB", "onResponse: " + response);
                        new RestoreTask(getActivity()).restoreAll(response, RestoreTask.BS);
                        blokSensuses = dbs.getListBlokSensus();
                        anggotaTims = db.getAllAnggota();
                        progressBlokPcls = db.getAllProgres();
                        parentListItems = getListAnggotaTim();
                        if (isKoor) {
                            recyclerBebanOrAnggota.setAdapter(new AnggotaTimAdapter(parentListItems, context, getResources()));
                        } else {
                            recyclerBebanOrAnggota.setAdapter(new WilayahKerjaAdapater(progressBlokPcls));
                        }
                        mStatusListing.setAdapter(new StatusListingAdapter(blokSensuses, getActivity()));
                        mRefresh.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("k", SynchronizeTask.GET_ALL_BS); //ps pr gprk gprn gpsk gpsn
                params.put("nim", (String) preference.get(CapiKey.KEY_NIM));
                return params;
            }
        });
    }

    private List<ParentListItem> getListAnggotaTim() {
        List<ParentListItem> list = new ArrayList<>();
        for (AnggotaTim ag : anggotaTims) {
            Log.e("NIM COEQ", ag.getNimAng());
            list.add(new ParentListAnggotaTim(getBlokSensusesAnggota(ag.getNimAng()), ag));
        }
        return list;
    }

    private ArrayList<BlokSensus> getBlokSensusesAnggota(String nim) {
        ArrayList<BlokSensus> allBsAng = new ArrayList<>();
        for (BlokSensus bs : blokSensuses) {
            Log.e("CEOEQQ", bs.getNim());
            if (bs.getNim().equalsIgnoreCase(nim)) {
                allBsAng.add(bs);
            }
        }
        return allBsAng;
    }

    private void createLog() {
        for (BlokSensus bs : blokSensuses) {
            String namaBs = bs.getNoBs();
            String kec = bs.getNamaKecamatan();
            String kel = bs.getNamaDesa();
            String log = "NoBs: " + namaBs + ", Kec: " + kec + ", Kel: " + kel;
            Log.e("Tes COEQQ", log);
        }

        List<String> nims = db.getAllNim();
        for (String ag : nims) {
            String log = "NIM: " + ag + " ,Name: " + db.getNamaByNim(ag);
            Log.e("Tes DB Kortim: ", log);
        }
        for (AnggotaTim ag : anggotaTims) {
            String log = "NIM: " + ag.getNimAng() + " ,Name: " + ag.getNamaAng() + " ,progres: " +
                    ag.getProgres() + ", Nomor: " + ag.getNomorAng();
            Log.e("Tes DB Kortim 2: ", log);
        }
        for (ProgressBlokPcl ag : progressBlokPcls) {
            String log = "No BS: " + ag.getNoBlokSensus() + " ,Progres: " + ag.getProgressBlokSensus();
            Log.e("Tes DB Pcl: ", log);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
    }


}
