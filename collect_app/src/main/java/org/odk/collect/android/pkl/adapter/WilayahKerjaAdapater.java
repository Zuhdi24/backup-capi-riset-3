package org.odk.collect.android.pkl.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.holder.HolderWilayahKerja;
import org.odk.collect.android.pkl.object.ProgressBlokPcl;

import java.util.List;

/**
 * @author TimHode
 */

public class WilayahKerjaAdapater extends RecyclerView.Adapter<HolderWilayahKerja> {

    private List<ProgressBlokPcl> progressBlokPcls;

    public WilayahKerjaAdapater(List<ProgressBlokPcl> progressBlokPcls) {
        this.progressBlokPcls = progressBlokPcls;
    }

    @Override
    public HolderWilayahKerja onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_ruta, parent, false);

        return new HolderWilayahKerja(itemView);
    }

    @Override
    public void onBindViewHolder(HolderWilayahKerja holder, int position) {
        ProgressBlokPcl wk = this.progressBlokPcls.get(position);

        holder.getId_bs().setText(wk.getNoBlokSensus());

        holder.getProgres_bs().setText(wk.getProgressBlokSensus());

    }

    @Override
    public int getItemCount() {
        return progressBlokPcls.size();
    }

}
