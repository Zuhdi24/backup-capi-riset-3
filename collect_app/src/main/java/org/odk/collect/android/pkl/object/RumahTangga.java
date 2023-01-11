package org.odk.collect.android.pkl.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Tim PKL
 * WARNING!!! Parcelable hanya untuk sebagian variabel saja
 * jika diperlukan variabel lain maka perlu penyesuaian
 */

public class RumahTangga implements Parcelable {

    public static final String STATUS_INSERT = "insert";
    public static final String STATUS_DELETE = "delete";
    public static final String STATUS_UPDATE = "update";
    public static final String STATUS_UPLOADED = "uploaded";

    private String kodeRuta;
    private String kodeBs;
    private String noSLS;
    private String bf;
    private String bs;
    private String noUrutRuta;
    private String namaKRT;
    private String alamat;
    private String jumlahART;
    private String jumlahART10;
    private String noHp;
    private String kodeEligible;
//    private String jumlahEligible;
//    private String listNamaEligible;
    private String latitude;
    private String longitude;
    private String akurasi;
    private String status;
    private String time;

//    private String noUrutART;
//    private String nama;
//    private boolean isBekerjaSeminggu;
//    private boolean isCuti;
//    private boolean isPernahBekerja;
//    private String deskripsi1;
//    private String kodeUsaha1;
//    private boolean isMelayani1;
//    private boolean isPernahBekerjaPariwisata;
//    private String deskripsi2;
//    private String kodeUsaha2;
//    private boolean isMelayani2;


    public RumahTangga(String kodeRuta, String kodeBs, String noSLS, String bf, String bs, String noUrutRuta, String namaKRT, String alamat, String jumlahART, String jumlahART10, String noHp, String kodeEligible, String latitude, String longitude, String akurasi, String status, String time) {
        this.kodeRuta = kodeRuta;
        this.kodeBs = kodeBs;
        this.noSLS = noSLS;
        this.bf = bf;
        this.bs = bs;
        this.noUrutRuta = noUrutRuta;
        this.namaKRT = namaKRT;
        this.alamat = alamat;
        this.jumlahART = jumlahART;
        this.jumlahART10 = jumlahART10;
        this.noHp = noHp;
        this.kodeEligible = kodeEligible;
//        this.jumlahEligible = jumlahEligible;
//        this.listNamaEligible = listNamaEligible;
        this.latitude = latitude;
        this.longitude = longitude;
        this.akurasi = akurasi;
        this.status = status;
        this.time = time;
    }

    public String getKodeRuta() {
        return kodeRuta;
    }

    public void setKodeRuta(String kodeRuta) {
        this.kodeRuta = kodeRuta;
    }

    public String getKodeBs() {
        return kodeBs;
    }

    public void setKodeBs(String kodeBs) {
        this.kodeBs = kodeBs;
    }

    public String getNoSLS() {
        return noSLS;
    }

    public void setNoSLS(String noSLS) {
        this.noSLS = noSLS;
    }

    public String getBf() {
        return bf;
    }

    public void setBf(String bf) {
        this.bf = bf;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public String getNoUrutRuta() {
        return noUrutRuta;
    }

    public void setNoUrutRuta(String noUrutRuta) {
        this.noUrutRuta = noUrutRuta;
    }

    public String getNamaKRT() {
        return namaKRT;
    }

    public void setNamaKRT(String namaKRT) {
        this.namaKRT = namaKRT;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJumlahART() {
        return jumlahART;
    }

    public void setJumlahART(String jumlahART) {
        this.jumlahART = jumlahART;
    }

    public String getJumlahART10() {
        return jumlahART10;
    }

    public void setJumlahART10(String jumlahART10) {
        this.jumlahART10 = jumlahART10;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getKodeEligible() {
        return kodeEligible;
    }

    public void setKodeEligible(String kodeEligible) {
        this.kodeEligible = kodeEligible;
    }

//    public String getJumlahEligible() {
//        return jumlahEligible;
//    }

//    public void setJumlahEligible(String jumlahEligible) {
//        this.jumlahEligible = jumlahEligible;
//    }

//    public String getListNamaEligible() {
//        return listNamaEligible;
//    }

//    public void setListNamaEligible(String listNamaEligible) {
//        this.listNamaEligible = listNamaEligible;
//    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAkurasi() {
        return akurasi;
    }

    public void setAkurasi(String akurasi) {
        this.akurasi = akurasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static Creator<RumahTangga> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{noSLS, bf, bs});
    }

    public RumahTangga() {
    }

    ;

    public RumahTangga(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        noSLS = data[0];
        bf = data[1];
        bs = data[2];
    }

    public static final Creator<RumahTangga> CREATOR = new Creator<RumahTangga>() {
        @Override
        public RumahTangga createFromParcel(Parcel in) {
            return new RumahTangga(in);
        }

        @Override
        public RumahTangga[] newArray(int size) {
            return new RumahTangga[size];
        }
    };


}
