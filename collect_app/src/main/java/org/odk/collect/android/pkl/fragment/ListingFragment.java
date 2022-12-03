package org.odk.collect.android.pkl.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.activity.ActivityListBlokSensus;
import org.odk.collect.android.pkl.activity.ActivityPassword;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;

import java.util.ArrayList;

public class ListingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        CapiPreference preference = CapiPreference.getInstance();
        String kodeJabatan = (String) preference.get(CapiKey.KEY_ID_JABATAN);

        View rootView = inflater.inflate(R.layout.fragment_listing_menu, container, false);
        getActivity().setTitle("SAMPLING MANAGEMENT");


        ImageButton listingButton = (ImageButton) rootView.findViewById(R.id.listing_button);
        ImageButton restoreButton = (ImageButton) rootView.findViewById(R.id.restore_button);
        LinearLayout restoreLayout = (LinearLayout) rootView.findViewById(R.id.restore_layout);


        DatabaseSampling db = DatabaseSampling.getInstance();

        if (StaticFinal.JABATAN_PENCACAH_ID.equals(kodeJabatan)) {
            restoreLayout.setVisibility(View.GONE);
        }

        listingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(getActivity(), ActivityListBlokSensus.class);
                startActivity(i);
            }
        });

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(getActivity(), ActivityPassword.class);
                startActivity(i);
            }
        });


        ArrayList<BlokSensus> listBS = db.getListBlokSensus();
        Boolean a = true;

        for (int i = 0; i < listBS.size(); i++) {
            if (listBS.get(i).getKodeBs().substring(2, 4).equals("02")) a = false;
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}
