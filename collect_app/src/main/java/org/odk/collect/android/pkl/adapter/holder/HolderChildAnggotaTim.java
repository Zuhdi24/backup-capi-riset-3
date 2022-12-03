package org.odk.collect.android.pkl.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import org.odk.collect.android.R;

/**
 * Created by Debut on 2/17/2017.
 */

public class HolderChildAnggotaTim extends ChildViewHolder {
    private TextView beban_bs;
    private TextView kel;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public HolderChildAnggotaTim(View itemView) {
        super(itemView);
        this.beban_bs = (TextView) itemView.findViewById(R.id.nomor_bebanBS);
        this.kel = (TextView) itemView.findViewById(R.id.kelurahan_bebanBs);
    }

    public TextView getBeban_bs() {
        return beban_bs;
    }

    public TextView getKel() {
        return kel;
    }
}
