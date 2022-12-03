package org.odk.collect.android.pkl.adapter.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.odk.collect.android.R;

/**
 * Created by Debut on 2/17/2017.
 */

public class HolderWilayahKerja extends RecyclerView.ViewHolder {

    private TextView id_bs;
    private TextView progres_bs;

    public HolderWilayahKerja(View itemView) {
        super(itemView);
        id_bs = (TextView) itemView.findViewById(R.id.id_bs);
        progres_bs = (TextView) itemView.findViewById(R.id.progres_bs);
    }

    public TextView getId_bs() {
        return id_bs;
    }

    public TextView getProgres_bs() {
        return progres_bs;
    }
}
