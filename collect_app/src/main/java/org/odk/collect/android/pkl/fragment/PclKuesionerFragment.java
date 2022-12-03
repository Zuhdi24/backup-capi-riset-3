package org.odk.collect.android.pkl.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.FileManagerTabs;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.activities.FormDownloadList;
import org.odk.collect.android.activities.GoogleDriveActivity;
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.activities.InstanceUploaderList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.activity.ArsipActivity;
import org.odk.collect.android.pkl.activity.PclListActivity;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.io.File;

public class PclKuesionerFragment extends Fragment {

    int NUMBER_OF_PAGES = 6;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_ekstra,container, false);

        View rootView = inflater.inflate(R.layout.fragment_kues_pcl, container, false);
        getActivity().setTitle("KUESIONER");

        ImageButton isiKues = (ImageButton) rootView.findViewById(R.id.isikues_button);
        isiKues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "fillBlankForm", "click");
                i = new Intent(getActivity(),
                        FormChooserList.class);
                startActivity(i);
            }
        });

        ImageButton editKues = (ImageButton) rootView.findViewById(R.id.editkues_button);
        editKues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = Collect.INSTANCES_PATH;
                File directory = new File(path);
                File[] files = directory.listFiles();
                if(files != null) {
                    Log.d("size_file", files.toString());
//                File directory = new File(path);
//                File[] files = directory.listFiles();
//                Log.d("Files", "Size: "+ files.length);
//                for (int i = 0; i < files.length; i++)
//                {
//                    Log.d("Files", "FileName:" + files[i].getName());
//                }
                }

                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "editSavedForm", "click");
                i = new Intent(getActivity(),
                        InstanceChooserList.class);
                startActivity(i);
            }
        });
//
        ImageButton arsipKues = (ImageButton) rootView.findViewById(R.id.restore_button);
        arsipKues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "ARSIP", "click");
                i = new Intent(getActivity(),
                        ArsipActivity.class);
                Log.d("Sandy", "ARSIP CLICKED");
                startActivity(i);
            }
        });

        ImageButton kirimKues = (ImageButton) rootView.findViewById(R.id.kirimkues_button);
        kirimKues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "uploadForms", "click");
                i = new Intent(getActivity(),
                        InstanceUploaderList.class);
                startActivity(i);
            }
        });

        ImageButton downloadKues = (ImageButton) rootView.findViewById(R.id.downloadkues_button);
        downloadKues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "downloadBlankForms", "click");
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String protocol = sharedPreferences.getString(
                        PreferencesActivity.KEY_PROTOCOL, getString(R.string.protocol_odk_default));
                if (protocol.equalsIgnoreCase(getString(R.string.protocol_google_sheets))) {
                    i = new Intent(getActivity(),
                            GoogleDriveActivity.class);
                } else {
                    i = new Intent(getActivity(),
                            FormDownloadList.class);
                }
                startActivity(i);
            }
        });

        ImageButton hapusKues = (ImageButton) rootView.findViewById(R.id.hapuskues_button);
        hapusKues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "deleteSavedForms", "click");
                i = new Intent(getActivity(),
                        FileManagerTabs.class);
                startActivity(i);
            }
        });

        ImageButton notifb = (ImageButton) rootView.findViewById(R.id.notifikasi_button);
        notifb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "Notifikasi", "click");
                i = new Intent(getActivity(),
                        PclListActivity.class);
                startActivity(i);
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

//    ViewListener viewListener = new ViewListener() {
//
//        @Override
//        public View setViewForPosition(final int position) {
//            LayoutInflater lf = getActivity().getLayoutInflater();
//            View customView = lf.inflate(R.layout.extra_costumview_carausel, null);
//
//            ImageButton mImageButton = (ImageButton) customView.findViewById(R.id.c_mbutton);
//            ImageView mImageView = (ImageView) customView.findViewById(R.id.bg_image);
//            final TextView mTextView = (TextView) customView.findViewById(R.id.c_mtext);
//            //set view attributes here
//            switch (position){
//                case 0: mImageButton.setImageResource(R.drawable.ic_note_56);
//                    mTextView.setText("Isi Kuesioner");
////                    mImageView.setImageResource(R.drawable.bg_piechart);
//                    break;
//                case 1: mImageButton.setImageResource(R.drawable.ic_search_56);
//                    mTextView.setText("Edit Kuesioner");
////                    mImageView.setImageResource(R.drawable.bg_ruta);
//                    break;
//                case 2: mImageButton.setImageResource(R.drawable.ic_upload_56);
//                    mTextView.setText("Kirim Kuesioner");
////                    mImageView.setImageResource(R.drawable.bg_barchart);
//                    break;
//                case 3: mImageButton.setImageResource(R.drawable.ic_download_56);
//                    mTextView.setText("Download Kuesioner");
////                    mImageView.setImageResource(R.drawable.bg_contact);
//                    break;
//                case 4: mImageButton.setImageResource(R.drawable.ic_bin_56);
//                    mTextView.setText("Hapus Kuesioner");
////                    mImageView.setImageResource(R.drawable.bg_contact);
//                    break;
//                case 5: mImageButton.setImageResource(R.drawable.ic_note_56);
//                    mTextView.setText("Notifiasi");
////                    mImageView.setImageResource(R.drawable.bg_contact);
//                    break;
//            }
//            mImageButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i;
//                    switch (position){
//                        case 0:
//                            Collect.getInstance().getActivityLogger()
//                                    .logAction(this, "fillBlankForm", "click");
//                            i = new Intent(getActivity(),
//                                    FormChooserList.class);
//                            startActivity(i);
//                            break;
//                        case 1:
//                            Collect.getInstance().getActivityLogger()
//                                    .logAction(this, "editSavedForm", "click");
//                            i = new Intent(getActivity(),
//                                    InstanceChooserList.class);
//                            startActivity(i);
//                            break;
//                        case 2:
//                            Collect.getInstance().getActivityLogger()
//                                    .logAction(this, "uploadForms", "click");
//                            i = new Intent(getActivity(),
//                                    InstanceUploaderList.class);
//                            startActivity(i);
//                            break;
//                        case 3:
//                            Collect.getInstance().getActivityLogger()
//                                    .logAction(this, "downloadBlankForms", "click");
//                            SharedPreferences sharedPreferences = PreferenceManager
//                                    .getDefaultSharedPreferences(getActivity());
//                            String protocol = sharedPreferences.getString(
//                                    PreferencesActivity.KEY_PROTOCOL, getString(R.string.protocol_odk_default));
//                            if (protocol.equalsIgnoreCase(getString(R.string.protocol_google_sheets))) {
//                                i = new Intent(getActivity(),
//                                        GoogleDriveActivity.class);
//                            } else {
//                                i = new Intent(getActivity(),
//                                        FormDownloadList.class);
//                            }
//                            startActivity(i);
//                            break;
//                        case 4:
//                            Collect.getInstance().getActivityLogger()
//                                    .logAction(this, "deleteSavedForms", "click");
//                            i = new Intent(getActivity(),
//                                    FileManagerTabs.class);
//                            startActivity(i);
//                            break;
//
//                        case 5:
//                            Collect.getInstance().getActivityLogger()
//                                    .logAction(this, "Notifikasi", "click");
//                            i = new Intent(getActivity(),
//                                    PclListActivity.class);
//                            startActivity(i);
//                            break;
//                    }
//
//
//                }
//            });
//
//            return customView;
//        }
//    };

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}

