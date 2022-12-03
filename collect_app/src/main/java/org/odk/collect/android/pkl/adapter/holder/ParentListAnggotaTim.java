package org.odk.collect.android.pkl.adapter.holder;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.object.BlokSensus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Debut on 2/17/2017.
 */

public class ParentListAnggotaTim implements ParentListItem {

    private ArrayList<BlokSensus> bebanBs;
    private AnggotaTim at;

    public ParentListAnggotaTim(ArrayList<BlokSensus> bebanBs, AnggotaTim at) {
        this.bebanBs = bebanBs;
        this.at = at;
    }

    @Override
    public List<BlokSensus> getChildItemList() {
        return bebanBs;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public AnggotaTim getAt() {
        return at;
    }
}
