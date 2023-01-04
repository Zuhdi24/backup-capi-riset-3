package org.odk.collect.android.pkl.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.UnitUsahaPariwisata;
import org.odk.collect.android.pkl.object.SampelRuta;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by isfann on 12/19/2016.
 */

public class Sampling {


    private static final String TAG = "SAMPLING";


    public static boolean ambilSampel(int nSampel, String kodeBs, Context context) {
        DatabaseSampling db = DatabaseSampling.getInstance();

        ArrayList<UnitUsahaPariwisata> frame = new ArrayList<>();
//        if (db.clearkanNoUrutUUPForSampling( kodeBs )) {
            frame = db.getListUUPForSampelLISTING( kodeBs );
//        }
        ArrayList<SampelRuta> sampelTerpilih;
        sampelTerpilih = new ArrayList<>();

        int nPopulasi = frame.size();
        Log.d( TAG, "ambilSampel: Frame total : " + frame.size() );


        if (nPopulasi < nSampel) {
//            if (db.getBlokSensusByKode( kodeBs ).getKodeBs().substring( 2, 4 ).equals( "05" )) {
//
//                Toast.makeText( context, "Jumlah Populasi = " + nPopulasi + ", Tidak Mencukupi untuk penarikan sampel", Toast.LENGTH_SHORT ).show();
//                db.clearkanNoUrutRuta( kodeBs );
//                return false;
//
//            } else {
                for (int i = 0; i < nPopulasi; i++) {
                    Log.d( "dahwan", String.valueOf( i/nPopulasi ) );
                    UnitUsahaPariwisata ruta = frame.get( i );
                    sampelTerpilih.add( new SampelRuta( ruta.getKodeBs(), ruta.getKodeUUP() ) );

                }

                if (db.deleteSampel( kodeBs )) {
                    if (db.insertSampel( sampelTerpilih )) {
                        Log.d( TAG, "ambilSampel: True" );
                        Toast.makeText( context, "Berhasil Memasukan ke Database", Toast.LENGTH_SHORT ).show();
                        return true;
                    } else {
                        Log.d( TAG, "ambilSampel: False" );
//                        db.clearkanNoUUP( kodeBs );
                        Toast.makeText( context, "Gagal Memasukan ke Database", Toast.LENGTH_SHORT ).show();
                        return false;
                    }
                } else {
//                    db.clearkanNoUUP( kodeBs );
                    Toast.makeText( context, "Gagal Memasukan ke Database", Toast.LENGTH_SHORT ).show();
                    return false;
                }


        }else{

            double k = nPopulasi / (double) nSampel; //TODO Fungsi Systematic Random Sampling
            Log.i( TAG, "k: " + k );
            int angkaRandom = (new Random()).nextInt( frame.size() ) + 1;
            Log.i( TAG, "AR: " + angkaRandom );
            Log.i( TAG, " = = = = = = = = " );

            for (int i = 1; i <= nSampel; i++) {
                Log.i( TAG, "AR + ik =  " + (angkaRandom + (i - 1) * k) );
                int index = (int) Math.floor( angkaRandom + (i - 1) * k );
                if (index > nPopulasi) {
                    index = index - nPopulasi;
                }
                Log.i( TAG, " = = = ["+i+"] = = = " );
                Log.i( TAG, "index ke-" + index );

                UnitUsahaPariwisata ruta = frame.get( index - 1 );
                Log.i( TAG, "--Nomor Urut Ruta (Listing): " + ruta.getNoUrutUUP() );
                Log.i( TAG, "--Nama KRT: " + ruta.getNamaPemilikUUP() );

                sampelTerpilih.add( new SampelRuta( ruta.getKodeBs(), ruta.getKodeUUP() ) );
            }
            if (db.deleteSampel( kodeBs )) {
                if (db.insertSampel( sampelTerpilih )) {
                    Log.d( TAG, "ambilSampel: True" );
                    Toast.makeText( context, "Berhasil Memasukan ke Database", Toast.LENGTH_SHORT ).show();
                    return true;
                } else {
                    Log.d( TAG, "ambilSampel: False" );
//                    db.clearkanNoUUP( kodeBs );
                    Toast.makeText( context, "Gagal Memasukan ke Database", Toast.LENGTH_SHORT ).show();
                    return false;
                }
            } else {
//                db.clearkanNoUUP( kodeBs );
                Toast.makeText( context, "Gagal Memasukan ke Database", Toast.LENGTH_SHORT ).show();
                return false;
            }

        }


        //Collections.sort(frame, new ComparatorPendidikan()); //TODO CEK

//        for (RumahTangga ruta : frame) {
//            String pendidikan = "";
//            switch (ruta.getLuasPanen()) {
//                case "1":
//                    pendidikan = "SD kebawah";
//                    break;
//                case "2":
//                    pendidikan = "SMP";
//                    break;
//                case "3":
//                    pendidikan = "SMA keatas";
//                    break;
//            }
//        }




    }

    public static boolean hapusHasilSampling(String kodeBs, Context context) {
        try {
            DatabaseSampling db = DatabaseSampling.getInstance();
            if (db.deleteSampel( kodeBs )) {
                Log.d( TAG, "hapusHasilSampling: true" );
                db.updateStatusBlokSensus( kodeBs, BlokSensus.FLAG_BS_READY );
                return true;
            } else {
                Log.d( TAG, "hapusHasilSampling: false" );
                return false;
            }
        } catch (Exception e) {
            Log.d( TAG, "hapusHasilSampling: " + e.getMessage() );
            return false;
        }
    }

}


