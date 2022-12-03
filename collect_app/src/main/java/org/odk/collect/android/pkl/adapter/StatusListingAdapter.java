package org.odk.collect.android.pkl.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.activity.ActivityListRumahTangga;
import org.odk.collect.android.pkl.adapter.holder.HolderStatusListing;
import org.odk.collect.android.pkl.object.BlokSensus;

import java.util.ArrayList;


/**
 * @author TimHode
 */

public class StatusListingAdapter extends RecyclerView.Adapter<HolderStatusListing> {

    ArrayList<BlokSensus> blokSensuses;
    Activity activity;

    public StatusListingAdapter(ArrayList<BlokSensus> bs, Activity a) {
        this.blokSensuses = bs;
        this.activity = a;
    }

    @Override
    public HolderStatusListing onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_statuslisting, parent, false);

        return new HolderStatusListing(itemView);
    }

    @Override
    public void onBindViewHolder(HolderStatusListing holder, int position) {
        final BlokSensus bs = blokSensuses.get(position);
        holder.getNomorBs().setText(bs.getNoBs());
//            String kec = DaerahAdministrasi.getNamaKecamatan(bs.getKabupaten(),bs.getKecamatan());
        String kec = bs.getNamaKecamatan();

        holder.getStatusListing().setText(bs.getStatus());
        holder.getKecBs().setText("KEC: " + kec);
//            holder.kelBs.setText("KEL: "+DaerahAdministrasi.getNamaDesa(kec,bs.getKode_desa()));
        holder.getKelBs().setText("KEL: " + bs.getNamaDesa());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, ActivityListRumahTangga.class);
                i.putExtra("kodeBs", bs.getKodeBs());
                i.putExtra("status", bs.getStatus());
//                  Log.d("kodeBs", listBs.get(position).getKodeBs());
                i.putExtra("mode", ActivityListRumahTangga.MODE_ALL);
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blokSensuses.size();
    }
}
