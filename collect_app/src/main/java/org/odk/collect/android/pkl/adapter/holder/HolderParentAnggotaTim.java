package org.odk.collect.android.pkl.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.white.progressview.CircleProgressView;

import org.odk.collect.android.R;

/**
 * Created by Debut on 2/17/2017.
 */

public class HolderParentAnggotaTim extends ParentViewHolder {

    private TextView nama;
    private TextView nim;
    private CircleProgressView progress;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public HolderParentAnggotaTim(View itemView) {
        super(itemView);
        this.nama = (TextView) itemView.findViewById(R.id.nama_anggota);
        this.nim = (TextView) itemView.findViewById(R.id.nim_anggota);
        this.progress = (CircleProgressView ) itemView.findViewById(R.id.progres_ang);
    }

    public TextView getNama() {
        return nama;
    }

    public TextView getNim() {
        return nim;
    }

    public CircleProgressView getProgress() {
        return progress;
    }
}
