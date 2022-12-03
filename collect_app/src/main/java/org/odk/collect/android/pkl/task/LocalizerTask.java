package org.odk.collect.android.pkl.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.util.BackupHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * @author isfann
 */

public class LocalizerTask extends AsyncTask<String, Void, String> {
    private final String TAG = "LOCALIZER";
    Context context;
    String nim;


    public LocalizerTask(Context context) {
        this.context = context;
        nim = (String) CapiPreference.getInstance().get(CapiKey.KEY_NIM);
    }

    private String backup(String json) {
        String message;

        Date time = new Date(System.currentTimeMillis());

        String state = Environment.getExternalStorageState();
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();
        File backupDir = new File(external + BackupHelper.BASE_PATH + nim + BackupHelper.BACKUP);
        String filename = BackupHelper.PREFIX + BackupHelper.SEPARATOR + nim + BackupHelper.SEPARATOR + time.toString() + ".JSON";

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "backup: Media Mounted");
            try {
                if (!backupDir.exists()) {
                    Log.d(TAG, "backup: dir not exist");
                    if (backupDir.mkdirs()) {
                        Log.d(TAG, "backup: Dir created");
                    } else {
                        Log.d(TAG, "backup: failed to create dir");
                    }
                } else {
                    Log.d(TAG, "backup: Dir exists");
                }

                File file = new File(backupDir, filename);
                Log.d(TAG, "backup: file created");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(json.getBytes());
                fos.close();
                message = "Backup Lokal Berhasil";

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                message = "Backup Lokal Gagal";
            } catch (IOException e) {
                e.printStackTrace();
                message = "Backup Lokal Gagal";
            }

            //membatasi jumlah file

            //mendapatkan listfile backup
            File[] listFile = backupDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(BackupHelper.PREFIX + BackupHelper.SEPARATOR + nim);

                }
            });

            Log.d(TAG, "backup: number of backup file" + listFile.length);

            ArrayList<File> arrayListFile = new ArrayList<>(Arrays.asList(listFile));

            //urutkan file berdasar last modified
            Collections.sort(arrayListFile, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return (int) (lhs.lastModified() - rhs.lastModified());
                }
            });

            while (arrayListFile.size() > BackupHelper.fileLimit) {
                arrayListFile.get(0).delete();
                arrayListFile.remove(0);
            }


        } else {
            message = "Media Penyimpanan Eksternal Tidak Tersedia";
        }
        Log.e("BACKUP",message);
        return message;
    }

    @Override
    protected String doInBackground(String... params) {
        return backup(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
//        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}