package org.odk.collect.android.pkl.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.FileBackup;
import org.odk.collect.android.pkl.object.ObjekUpload;
import org.odk.collect.android.pkl.object.UnitUsahaPariwisata;
import org.odk.collect.android.pkl.object.SampelRuta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

//import com.google.gson.TypeAdapter;
//import com.google.gson.TypeAdapterFactory;

/**
 * Created by isfann on 1/21/2017.
 */

public class BackupHelper {

    private static final String TAG = "BackupHelper";
    public static final String BASE_PATH = "/capi61/listing/";
    public static final String BACKUP = "/backup/";
    public static final String RESTORE_POINT = "/restore_point/";
    public static final String PREFIX = "capi61-listing";
    public static final String SEPARATOR = "_";
    public static final int fileLimit = 10;
    public static final String SOURCE_LOCAL = "Local Backup";
    public static final String SOURCE_CLOUD = "Cloud Backup";
    public static final String HOST = "https://capi.pkl58.stis.ac.id/listing_kv.php";
//    public static final String HOST = "https://pkl.stis.ac.id/service/listing_try.php";

    public static final String RUTA = "ruta";
    public static final String SAMPEL = "sampel";


    public static ArrayList<FileBackup> getListFileBackup(final String nim, final String deviceId) {
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = external + BASE_PATH + nim + BACKUP;
        File backupDir = new File(path);

        //mendapatkan listfile backup
        File[] listFile = backupDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(PREFIX + SEPARATOR + nim);

            }
        });

        if (listFile != null) {
            Log.d(TAG, "backup: number of backup file" + listFile.length);

            ArrayList<File> arrayListFile = new ArrayList<>(Arrays.asList(listFile));

            //urutkan file berdasar last modified
            Collections.sort(arrayListFile, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return (int) (lhs.lastModified() - rhs.lastModified());
                }
            });

            ArrayList<FileBackup> listFileBackup = new ArrayList<>();

            for (File file : arrayListFile) {
                String fileName = file.getName();
                String[] fileNameParts = fileName.split(SEPARATOR);
                listFileBackup.add(new FileBackup(fileNameParts[2], deviceId, SOURCE_LOCAL, path + fileName));
            }

            return listFileBackup;

        } else {
            return new ArrayList<FileBackup>();
        }
    }

    public static String readFile(String path) {
        String line, json = "";
        FileReader fr;
        BufferedReader br;


        File file = new File(path);
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                json = json + line + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String encodeAllData(Context context, String nim, String deviceId) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        DatabaseSampling db = DatabaseSampling.getInstance();
        ArrayList<BlokSensus> listBs = db.getListBlokSensus();
        ArrayList<UnitUsahaPariwisata> listRuta = db.getAllRumahTangga();
        ArrayList<SampelRuta> listSampel = db.getAllSampelRuta();
        return gson.toJson(new ObjekUpload(nim, deviceId, listBs, listRuta, listSampel));
    }

    public static String encodeToJson(Context context, String mode) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DatabaseSampling db = DatabaseSampling.getInstance();
        if (mode == RUTA) {
            ArrayList<UnitUsahaPariwisata> listRuta = db.getAllRumahTangga();
            return gson.toJson(listRuta);
        } else if (mode == SAMPEL) {
            ArrayList<SampelRuta> listRuta = db.getAllSampelRuta();
            return gson.toJson(listRuta);
        } else {
            return "Encode Mode Not Found";
        }

    }


}
