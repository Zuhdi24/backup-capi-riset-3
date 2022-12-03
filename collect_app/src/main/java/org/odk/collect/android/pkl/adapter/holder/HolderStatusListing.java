package org.odk.collect.android.pkl.adapter.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.odk.collect.android.R;

/**
 * Created by Debut on 2/17/2017.
 */

public class HolderStatusListing extends RecyclerView.ViewHolder {

    private TextView nomorBs;
    private TextView statusListing;
    private TextView kecBs;
    private TextView kelBs;

    public HolderStatusListing(View itemView) {
        super(itemView);
        nomorBs = (TextView) itemView.findViewById(R.id.nomor_bs);
        statusListing = (TextView) itemView.findViewById(R.id.status_bs_listing);
        kecBs = (TextView) itemView.findViewById(R.id.kecamatan_statuslisting);
        kelBs = (TextView) itemView.findViewById(R.id.kelurahan_statuslisting);
    }

    public TextView getKecBs() {
        return kecBs;
    }

    public TextView getKelBs() {
        return kelBs;
    }

    public TextView getNomorBs() {
        return nomorBs;
    }

    public TextView getStatusListing() {
        return statusListing;
    }
}
