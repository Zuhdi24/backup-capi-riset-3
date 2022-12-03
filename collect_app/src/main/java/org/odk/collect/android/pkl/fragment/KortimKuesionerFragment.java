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
import org.odk.collect.android.pkl.activity.KortimListActivity;
import org.odk.collect.android.preferences.PreferencesActivity;

public class KortimKuesionerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_ekstra,container, false);

        View rootView = inflater.inflate(R.layout.fragment_kues_kortim, container, false);
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
//                CapiPreference preference = CapiPreference.getInstance();
//                String path = Collect.INSTANCES_PATH + "/" + preference.get(CapiKey.KEY_NIM);
////                String path = Collect.INSTANCES_PATH ;
//                String kuesionerPath = Collect.FORMS_PATH;
//                Log.d("path-name", path);
//                File directorory = new File(path);
//                File[] files = directorory.listFiles();
//                String formId = "";
//                File[] filesKuesioner = new File(kuesionerPath).listFiles();
//                Log.e("KUES PATH SANDY", kuesionerPath);
//
//                int idWilayah = 0;
//                CapiPreference pref = CapiPreference.getInstance();
//                switch (((String) pref.get(CapiKey.KEY_NAMA_KABUPATEN)).toUpperCase()){
//                    case "KOTA BENGKULU" :
//                        idWilayah = 1;
//                        break;
//                    case "BENGKULU SELATAN" :
//                        idWilayah = 1;
//                        break;
//                    case "SELUMA" :
//                        idWilayah = 2;
//                        break;
//                    case "BENGKULU UTARA" :
//                        idWilayah = 2;
//                        break;
//                    case "MUKO-MUKO" :
//                        idWilayah = 3;
//                        break;
//                    case "BENGKULU TENGAH" :
//                        idWilayah = 3;
//                        break;
//                    case "REJANG LEBONG" :
//                        idWilayah = 4;
//                        break;
//                    case "KAUR" :
//                        idWilayah = 4;
//                        break;
//                    case "LEBONG" :
//                        idWilayah = 5;
//                        break;
//                    case "KEPAHIANG" :
//                        idWilayah = 5;
//                        break;
//                }
//
//
//                for (int z = 0; z<filesKuesioner.length; z++){
//                    if(filesKuesioner[z].isFile()){
//                        Log.e("KUESIONER YG ADA SANDY", filesKuesioner[z].getPath());
//                        Log.e("KUESIONER YG ADA SANDY", filesKuesioner[z].getName());
////                        if(filesKuesioner[z].getName().contains("xml")&&filesKuesioner[z].getName().contains("M"+String.valueOf(idWilayah))){
//                        if(filesKuesioner[z].getName().contains("xml")){
//                            formId = filesKuesioner[z].getName();
//                            Log.e("FORM ID SANDY", formId);
//                            formId = formId.split("\\.")[0];
//                            Log.e("FORM ID SANDY", formId);
//                            formId = formId.replace(" ",".");
//                            Log.e("FORM ID SANDY", formId);
//                            break;
//                        }
//                    }
//                }
//
//                String formPath = "";
//                String ids = "";
//                Cursor c = null;
//                try {
//                    String[] proj = {FormsProviderAPI.FormsColumns.FORM_FILE_PATH, FormsProviderAPI.FormsColumns._ID};
//                    String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + " =? ";
//                    String[] selectArgs = {formId};
//                    c = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, proj,
//                            selection, selectArgs, null);
//                    if (c.moveToFirst()) {
//                        do {
//                            formPath = c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH));
//                            ids = c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns._ID));
//                        } while (c.moveToNext());
//                    } else {
//                        Log.d("ERROR", "name kosong");
//                    }
//                } catch (Exception ex) {
//                    Log.d("ERROR", "name " + ex);
//                } finally {
//                    if (c != null) {
//                        c.close();
//                    }
//                }
//
//                Log.d("Files", "Size  :"+files.length);
//                Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, Long.valueOf(ids));
//                for(int j = 0 ; j<files.length; j++){
//                    Log.d("Files SANDY", "Filename :"+files[j].getName());
////                    SaveToDiskTask mSaveToDiskTask = new SaveToDiskTask(formUri, true, false,
////                            formId, Collect.getInstance().getFormController().getError(), 0);
////                    mSaveToDiskTask.setFormSavedListener(this);
////                    mSaveToDiskTask.execute();
//                }



                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "editSavedForm", "click");
                i = new Intent(getActivity(),
                        InstanceChooserList.class);
                startActivity(i);
            }
        });
//
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

        ImageButton bmonit = (ImageButton) rootView.findViewById(R.id.monitoring_button);
        bmonit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "Monitoring", "click");
                i = new Intent(getActivity(),
                        KortimListActivity.class);
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
//                case 5: mImageButton.setImageResource(R.drawable.ic_bin_56);
//                    mTextView.setText("Monitoring");
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
//                                    .logAction(this, "deleteSavedForms", "click");
//                            i = new Intent(getActivity(),
//                                    KortimListActivity.class);
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

