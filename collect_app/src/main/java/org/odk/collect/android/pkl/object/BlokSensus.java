package org.odk.collect.android.pkl.object;

import android.content.Context;

import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.preference.CapiKey;

/**
 * Created by Geri_ on 12/19/2016.
 */
public class BlokSensus {

    public static final String FLAG_BS_PROSES_LISTING = "listing";
    public static final String FLAG_BS_READY = "ready";
    public static final String FLAG_BS_SAMPLED = "sampled";
    public static final String FLAG_BS_SAMPLE_UPLOADED = "uploaded";

    private String kodeBs;
    private String provinsi;
    private String kabupaten;
    private String kecamatan;
    private String desa;
    private String stratifikasi; //1=perdesaan, 2=perkotaan
    private String noBs;
    private String namaKabupaten;
    private String namaKecamatan;
    private String namaDesa;
    private String sls = "";
    private String nim;
    private String status;
//    private String jumlahRTBaru = "0";
//    private String jumlahRTInternet ;
//    private String nks;
//    private String jumlahRTLama ;

    public BlokSensus(String kodeBs, String provinsi, String kabupaten, String kecamatan, String desa, String stratifikasi, String noBs, String sls,
                      String namaKabupaten, String namaKecamatan, String namaDesa, String nim, String status) {
        this.kodeBs = kodeBs;
        this.provinsi = provinsi;
        this.kabupaten = kabupaten;
        this.kecamatan = kecamatan;
        this.desa = desa;
        this.stratifikasi = stratifikasi;
        this.noBs = noBs;
        this.sls = sls;
        this.nim = nim;
        this.status = status;
        this.namaDesa = namaDesa;
        this.namaKecamatan = namaKecamatan;
        this.namaKabupaten = namaKabupaten;
//        this.jumlahRTBaru = jumlahRTBaru;
//        this.jumlahRTInternet = jumlahRTInternet;
//        this.jumlahRTLama = jumlahRTLama;
    }

    public BlokSensus() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public String getStratifikasi() {
        return stratifikasi;
    }

    public void setStratifikasi(String stratifikasi) {
        this.stratifikasi = stratifikasi;
    }

    public String getNoBs() {
        return noBs;
    }

    public void setNoBs(String noBs) {
        this.noBs = noBs;
    }

    public String getKodeBs() {
        return kodeBs;
    }

    public void setKodeBs(String kodeBs) {
        this.kodeBs = kodeBs;
    }

    public String getSls() {
        return sls;
    }

    public void setSls(String sls) {
        this.sls = sls;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getBsFlag(Context context) {
        DatabaseSampling db = DatabaseSampling.getInstance();
        if (db.getBlokSensusByKode(kodeBs).getStatus() == FLAG_BS_SAMPLED) {
            return FLAG_BS_SAMPLED;
        } else {
            if (((db.getJumlahUUP(kodeBs)) >= CapiKey.N_MINIMUM_RUTA) && db.getBlokSensusByKode(kodeBs).getStatus() == FLAG_BS_READY) {
//                Toast.makeText(context, String.valueOf(db.getBlokSensusByKode(status).getJumlahRuta()), Toast.LENGTH_SHORT);
                return FLAG_BS_READY;
            } else {
//                Toast.makeText(context, String.valueOf(db.getBlokSensusByKode(status).getJumlahRuta()), Toast.LENGTH_SHORT);
                return FLAG_BS_PROSES_LISTING;
            }
        }
    }

    public String getNamaKabupaten() {
        return namaKabupaten;
    }

    public void setNamaKabupaten(String namaKabupaten) {
        this.namaKabupaten = namaKabupaten;
    }

    public String getNamaKecamatan() {
        return namaKecamatan;
    }

    public void setNamaKecamatan(String namaKecamatan) {
        this.namaKecamatan = namaKecamatan;
    }

    public String getNamaDesa() {
        return namaDesa;
    }

    public void setNamaDesa(String namaDesa) {
        this.namaDesa = namaDesa;
    }

//    public String getNks() {
//        return nks;
//    }
//
//    public void setNks(String nks) {
//        this.nks = nks;
//    }


//    public String getJumlahRTLama() {
//        return jumlahRTLama;
//    }
//
//    public void setJumlahRTLama(String jumlahRTlama) {
//        this.jumlahRTLama = jumlahRTLama;
//    }

//    public String getJumlahRTBaru() {
//        return jumlahRTBaru;
//    }
//
//    public void setJumlahRTBaru(String jumlahRTBaru) {
//        this.jumlahRTBaru = jumlahRTBaru;
//    }
//
//    public String getJumlahRTInternet() {
//        return jumlahRTInternet;
//    }
//
//    public void setJumlahRTInternet(String jumlahRTInternet) {
//        this.jumlahRTInternet = jumlahRTInternet;
//    }
}
