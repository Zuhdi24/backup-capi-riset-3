package org.odk.collect.android.pkl.task;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.DownloadPcl;
import org.odk.collect.android.pkl.briefcase.BriefcaseFormDefinition;
import org.odk.collect.android.pkl.briefcase.FormStatus;
import org.odk.collect.android.pkl.briefcase.OdkCollectFormDefinition;
import org.odk.collect.android.pkl.briefcase.ServerConnectionInfo;
import org.odk.collect.android.pkl.briefcase.getxml;
import org.odk.collect.android.pkl.briefcase.paramsGet;
import org.odk.collect.android.pkl.object.Notifikasi;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.io.File;

/**
 * Created by Cloud Walker on 24/02/2016.
 */
public class DownloadIsian2 {
    private Notifikasi ds;
    private String finalFinalPath;
    private Context mcontext;
    private DownloadPcl mdownloadpcl;

    public DownloadIsian2(Notifikasi ds, String finalFinalPath, Context mcontext, DownloadPcl mdownloadpcl) {
        this.ds = ds;
        this.finalFinalPath = finalFinalPath;
        this.mcontext = mcontext;
        this.mdownloadpcl = mdownloadpcl;
    }

    public void exscute() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mcontext);
        final String server = settings.getString(PreferencesActivity.KEY_SERVER_URL, Collect.getInstance().getString(R.string.default_server_url));
        final String storedUsername = settings.getString(PreferencesActivity.KEY_USERNAME, mcontext.getResources().getString(R.string.default_username));
        final String storedPassword = settings.getString(PreferencesActivity.KEY_PASSWORD, mcontext.getResources().getString(R.string.default_password));

        ServerConnectionInfo serverinfo = new ServerConnectionInfo(server, storedUsername, storedPassword.toCharArray());
//        ServerFetcher serverFetcher = new ServerFetcher(serverinfo);
        BriefcaseFormDefinition lfd = null;
        FormStatus fs = null;
        File dir = null;
        File formxml = null;
        try {
            formxml = new File(finalFinalPath);
            Log.d("MainActiviry xml", formxml.getAbsolutePath());
            Log.d("MainActiviry xml", FileUtils.readFileToString(formxml));
        } catch (Exception Ex) {
            Log.d("Kortim", "formxml " + Ex);
        }
        try {
            dir = new File(Collect.INSTANCES_PATH);
        } catch (Exception ex) {
            Log.d("Kortim", "dir path" + ex);
        }
        try {
            lfd = new BriefcaseFormDefinition(dir, formxml);
        } catch (Exception ex) {
            Log.d("Kortim", "lfd " + ex);
        }
        try {
            fs = new FormStatus(FormStatus.TransferType.GATHER, new OdkCollectFormDefinition(formxml));
        } catch (Exception ex) {
            Log.d("Kortim", "form stat" + ex);
        }
        paramsGet as = new paramsGet(dir, lfd, fs, ds.getUnique_id_instance(), Collect.INSTANCES_PATH, serverinfo);
        getxml get = new getxml(mcontext);
        get.setDownloadpcl(mdownloadpcl, ds);
        get.doInBackground(as);
    }

}
