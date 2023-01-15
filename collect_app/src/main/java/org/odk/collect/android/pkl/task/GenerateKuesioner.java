package org.odk.collect.android.pkl.task;

import androidx.appcompat.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.FormLoaderListener;
import org.odk.collect.android.listeners.FormSavedListener;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.RumahTanggaTerpilih;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.VariableGenerate;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.tasks.FormLoaderTask;
import org.odk.collect.android.tasks.SaveResult;
import org.odk.collect.android.tasks.SaveToDiskTask;
import org.odk.collect.android.utilities.FileUtils;

import java.io.File;
import java.util.Locale;

/**
 * @author raja
 */

public class GenerateKuesioner implements FormLoaderListener, FormSavedListener {

    private String ta = "GenerateKuesioner";
    private String formId;

    private CapiPreference preference;

    private final Object object = new Object();
    private final Object object2 = new Object();
    private int keberapa;
    private RumahTanggaTerpilih rtt;
    private DatabaseSampling dbsampling = DatabaseSampling.getInstance();
//    private ArrayList<RumahTanggaTerpilih> staticsa;

    private String ids = "";
    private String FormPath = "";

    private Context context;
    private ProgressDialog generateKuesionerProgress, buatFileProgress;
    private AlertDialog berhasilGenerateAlert, gagalGenerateAlert;

    public GenerateKuesioner(final Context context, String formId) {
        this.context = context;
        this.formId = formId;
        Log.e("SANDYyyyyyy", formId);

//        staticsa = new ArrayList<>();
//        keberapa = 0;

        Cursor c = null;
        try {
            String[] proj = {FormsProviderAPI.FormsColumns.FORM_FILE_PATH, FormsProviderAPI.FormsColumns._ID};

            String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
            String[] selectArgs = {formId};

            c = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, proj,
                    selection, selectArgs, null);
            Log.d(ta, String.valueOf(c.getCount()));


            if (c.moveToFirst()) {
                do {
                    this.FormPath = c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH));
                    this.ids = c.getString(c.getColumnIndex(FormsProviderAPI.FormsColumns._ID));
                } while (c.moveToNext());
            } else {
                Log.d(ta, "name kosong");
            }
        } catch (Exception ex) {
            Log.d(ta, "name " + ex);
        } finally {
            if (c != null) {
                c.close();
            }
        }

        generateKuesionerProgress = new ProgressDialog(context);
        generateKuesionerProgress.setCancelable(false);
        generateKuesionerProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        buatFileProgress = new ProgressDialog(context);
        buatFileProgress.setMessage("Harap Tunggu... \n PASTIKAN LAYAR TIDAK REDUP");
        buatFileProgress.setIndeterminate(true);
        buatFileProgress.setCancelable(false);
    }

    public void GenerateKuesInstance(RumahTanggaTerpilih rtt) {
        buatFileProgress.show();
        this.rtt = rtt;
        generateKuesionerProgress.setMax(1);
        if (FormPath != null) {
            Log.d("masuk", FormPath);
            FormLoaderTask mFormLoaderTask = new FormLoaderTask(null, null, null);
            synchronized (object) {
                mFormLoaderTask.setFormLoaderListener(this);
                mFormLoaderTask.execute(FormPath);
            }
        }
    }

    @Override
    public void onProgressStep(String stepMessage) {

    }

    @Override
    public void loadingComplete(FormLoaderTask task) {
        Log.d(ta, "loadingcomplete");
//        FormController formController = task.getFormController();
        FormController mformControl = task.getFormController();
        Collect.getInstance().setFormController(mformControl);
//        mformControl = task.getFormController();
//        mFormLoaderTask.setFormLoaderListener(null);
//        FormLoaderTask t = mFormLoaderTask;
//        mFormLoaderTask = null;
//        t.cancel(true);
//        t.destroy();
        //Collect.getInstance().setFormController(formController);
        //CompatibilityUtils.invalidateOptionsMenu(mcontext);

        Collect.getInstance().setExternalDataManager(task.getExternalDataManager());

        // Set the language if one has already been set in the past
        String[] languageTest = mformControl.getLanguages();
        if (languageTest != null) {
            String defaultLanguage = mformControl.getLanguage();
            String newLanguage = "";
            String selection = FormsProviderAPI.FormsColumns.FORM_FILE_PATH + "=?";
            String selectArgs[] = {FormPath};
            Cursor c = null;
            try {
                c = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, null,
                        selection, selectArgs, null);
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    newLanguage = c.getString(c
                            .getColumnIndex(FormsProviderAPI.FormsColumns.LANGUAGE));
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            // if somehow we end up with a bad language, set it to the default
            try {
                mformControl.setLanguage(newLanguage);
            } catch (Exception e) {
                mformControl.setLanguage(defaultLanguage);
            }
        }

//        mformControl=formController;
        buatFileProgress.dismiss();
        synchronized (object) {
            makedata(rtt, Collect.getInstance().getFormController());
        }
        // Set saved answer path
    }

    private void makedata(RumahTanggaTerpilih st, FormController fc) {
        generateKuesionerProgress.setMessage("Generate Kuesioner : \n" + st.getFileName());
        generateKuesionerProgress.setProgress(keberapa);
        String namaFile = st.getFileName();
//        if (FormPath.contains(" KM5")||FormPath.contains("/KM5 ")){
//            namaFile = namaFile+"-KM5";
//        }
//        if (FormPath.contains(" M5")||FormPath.contains("/M5 ")){
//            namaFile = namaFile+"-M5";
//        }
        Log.d(ta, "formcontroller" + (fc == null));
//        Log.d(ta + "nama_krt", st.getNama_pemilik());

        String path = Collect.INSTANCES_PATH + File.separator;

        Collect.getInstance().setFormController(fc);
        if (FileUtils.createFolder(path + namaFile)) {
            try {
                File baru = new File(path + namaFile + File.separator + namaFile + ".xml");
                fc.setInstancePath(baru);
                Log.d(ta + "isi", "" + baru.isFile());
            } catch (Exception ex) {
                Log.d(ta + "filepa", "" + ex);
            }
        }
        try {
            preference = CapiPreference.getInstance();
            fc.getreRrenceForElementName(VariableGenerate.latitude, st.getLatitude());
            fc.getreRrenceForElementName(VariableGenerate.longitude, st.getLongitude());
            fc.getreRrenceForElementName(VariableGenerate.accuracy, st.getAccuracy());
            fc.getreRrenceForElementName(VariableGenerate.nama_kabupaten, st.getNamaKabupaten().toUpperCase());
            fc.getreRrenceForElementName(VariableGenerate.kode_kabupaten, st.getKodeKabupaten());
            fc.getreRrenceForElementName(VariableGenerate.nama_kecamatan, st.getNamaKecamatan().toUpperCase());
            fc.getreRrenceForElementName(VariableGenerate.kode_kecamatan, st.getKodeKecamatan());
            fc.getreRrenceForElementName(VariableGenerate.nama_desa, st.getNamaDesa().toUpperCase());
            fc.getreRrenceForElementName(VariableGenerate.kode_desa, st.getKodeDesa());
            fc.getreRrenceForElementName(VariableGenerate.no_bs, st.getNoBs());
            fc.getreRrenceForElementName(VariableGenerate.namaPcl, String.valueOf(preference.get(CapiKey.KEY_NAMA)).toUpperCase());
            fc.getreRrenceForElementName(VariableGenerate.nimPcl, String.valueOf(preference.get(CapiKey.KEY_NIM)));
            fc.getreRrenceForElementName(VariableGenerate.namaKortim, String.valueOf(preference.get(CapiKey.KEY_NAMA_KORTIM)).toUpperCase());
            fc.getreRrenceForElementName(VariableGenerate.nimKortim, "2" + String.valueOf(preference.get(CapiKey.KEY_NIM_KORTIM)).substring(1));

            // Isian dari Listing
            fc.getreRrenceForElementName(VariableGenerate.noUrutRuta, st.getNoUrutRuta());
            fc.getreRrenceForElementName(VariableGenerate.namaKrt, st.getNamaKrt().toUpperCase());
            fc.getreRrenceForElementName(VariableGenerate.noHp, st.getNoHp());
            fc.getreRrenceForElementName(VariableGenerate.jumlahART, Integer.parseInt(st.getJumlahART()));
//            fc.getreRrenceForElementName(VariableGenerate.jumlahART10, Integer.parseInt(st.getJumlahART10()));

            fc.setBeforeMetadata();

        } catch (Exception ex) {
            Log.d(ta + "answers", "" + ex);
        }

        Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, Long.valueOf(this.ids));
        Log.d(ta + "FormID", "" + formUri + "|" + st.getFileName());

        synchronized (object2) {
            SaveToDiskTask mSaveToDiskTask = new SaveToDiskTask(formUri, true, false,
                    namaFile, Collect.getInstance().getFormController().getError(), 0);
            mSaveToDiskTask.setFormSavedListener(this);
            mSaveToDiskTask.execute();
        }
    }

    public void savingComplete(SaveResult saveStatus) {
        keberapa++;
        Log.d(ta + "iterasi", "iterasi1 " + keberapa);
//        if (keberapa < staticsa.size()) {
//            synchronized (object) {
//                Log.d(ta + "iterasi", "iterasi2 " + keberapa);
//                makedata(staticsa.get(keberapa), Collect.getInstance().getFormController());
//            }
//            GenerateKuesInstance(staticsa);
//        } else {
        Collect.getInstance().setFormController(null);

        berhasilGenerateAlert = new AlertDialog.Builder(context).create();
        berhasilGenerateAlert.setTitle("Berhasil di-generate");
        berhasilGenerateAlert.setMessage("Silakan pindah ke menu \n'Ubah Kuesioner'," + "\n\nKuesioner : " + formId +
                "\nKode : " + rtt.getFileName() + "\ntelah di-generate.");
        berhasilGenerateAlert.setButton(DialogInterface.BUTTON_POSITIVE,"Ubah Kuesioner", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent ii;
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "editSavedForm", "click");
                ii = new Intent(context,
                        InstanceChooserList.class);
                ii.putExtra("setBack", 1);
                context.startActivity(ii);
                dialog.dismiss();
            }
        });
        berhasilGenerateAlert.show();
//        }
    }

    @Override
    public void loadingError(String errorMsg) {
        gagalGenerateAlert.setMessage("Generate Kuesioner Gagal\n" + errorMsg);
        gagalGenerateAlert.show();
    }

    public String empat_digit(int val) {
        if (val < 10) return ("000" + val);
        else if (val < 100) return ("00" + val);
        else if (val < 1000) return ("0" + val);
        else return String.valueOf(val);
    }

}