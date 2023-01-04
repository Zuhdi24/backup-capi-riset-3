package org.odk.collect.android.pkl.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Tim PKL
 * WARNING!!! Parcelable hanya untuk sebagian variabel saja
 * jika diperlukan variabel lain maka perlu penyesuaian
 */

public class UnitUsahaPariwisata implements Parcelable {

    public static final String STATUS_INSERT = "insert";
    public static final String STATUS_DELETE = "delete";
    public static final String STATUS_UPDATE = "update";
    public static final String STATUS_UPLOADED = "uploaded";

    private String kodeUUP;
    private String kodeBs;
    private String noSegmen;
    private String bf;
    private String bs;
    private String noUrutRuta;
    private String namaKRT;
    private String alamat;
    private String jumlahisUUP;
    private String noUrutPemilikUUP;
    private String namaPemilikUUP;
    private String kedudukanUP;
    private String statusKelola;
    private String lokasiUP;
    private String jenisUUP;
    private String noUrutUUP;
//    private String noHp;
    private String latitude;
    private String longitude;
    private String akurasi;
    private String status;
    private String time;

    public UnitUsahaPariwisata(String kodeUUP, String kodeBs, String noSegmen, String bf, String bs, String noUrutRuta, String namaKRT,
                               String alamat, String jumlahisUUP, String noUrutPemilikUUP, String namaPemilikUUP, String kedudukanUP,
                               String statusKelola, String lokasiUP, String jenisUUP, String noUrutUUP, String latitude, String longitude,
                               String akurasi, String status, String time) {
        this.kodeUUP = kodeUUP;
        this.kodeBs = kodeBs;
        this.noSegmen = noSegmen;
        this.bf = bf;
        this.bs = bs;
        this.noUrutRuta = noUrutRuta;
        this.namaKRT = namaKRT;
        this.alamat = alamat;
        this.jumlahisUUP = jumlahisUUP;
        this.noUrutPemilikUUP = noUrutPemilikUUP;
        this.namaPemilikUUP = namaPemilikUUP;
        this.kedudukanUP = kedudukanUP;
        this.statusKelola = statusKelola;
        this.lokasiUP = lokasiUP;
        this.jenisUUP = jenisUUP;
        this.noUrutUUP = noUrutUUP;
        this.latitude = latitude;
        this.longitude = longitude;
        this.akurasi = akurasi;
        this.status = status;
        this.time = time;
    }

    public String getKodeUUP() {
        return kodeUUP;
    }

    public void setKodeUUP(String kodeUUP) {
        this.kodeUUP = kodeUUP;
    }

    public String getKodeBs() {
        return kodeBs;
    }

    public void setKodeBs(String kodeBs) {
        this.kodeBs = kodeBs;
    }

    public String getNoSegmen() {
        return noSegmen;
    }

    public void setNoSegmen(String noSegmen) {
        this.noSegmen = noSegmen;
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

    public String getJumlahisUUP() {
        return jumlahisUUP;
    }

    public void setJumlahisUUP(String jumlahisUUP) {
        this.jumlahisUUP = jumlahisUUP;
    }

    public String getNoUrutPemilikUUP() {
        return noUrutPemilikUUP;
    }

    public void setNoUrutPemilikUUP(String noUrutPemilikUUP) {
        this.noUrutPemilikUUP = noUrutPemilikUUP;
    }

    public String getNamaPemilikUUP() {
        return namaPemilikUUP;
    }

    public void setNamaPemilikUUP(String namaPemilikUUP) {
        this.namaPemilikUUP = namaPemilikUUP;
    }

    public String getKedudukanUP() {
        return kedudukanUP;
    }

    public void setKedudukanUP(String kedudukanUP) {
        this.kedudukanUP = kedudukanUP;
    }

    public String getStatusKelola() {
        return statusKelola;
    }

    public void setStatusKelola(String statusKelola) {
        this.statusKelola = statusKelola;
    }

    public String getLokasiUP() {
        return lokasiUP;
    }

    public void setLokasiUP(String lokasiUP) {
        this.lokasiUP = lokasiUP;
    }

    public String getJenisUUP() {
        return jenisUUP;
    }

    public void setJenisUUP(String jenisUUP) {
        this.jenisUUP = jenisUUP;
    }

    public String getNoUrutUUP() {
        return noUrutUUP;
    }

    public void setNoUrutUUP(String noUrutUUP) {
        this.noUrutUUP = noUrutUUP;
    }

//    public String getNoHp() {
//        return noHp;
//    }
//
//    public void setNoHp(String noHp) {
//        this.noHp = noHp;
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

    public static Creator<UnitUsahaPariwisata> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{noSegmen, bf, bs});
    }

    public UnitUsahaPariwisata() {
    }

    ;

    public UnitUsahaPariwisata(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        noSegmen = data[0];
        bf = data[1];
        bs = data[2];
    }

    public static final Creator<UnitUsahaPariwisata> CREATOR = new Creator<UnitUsahaPariwisata>() {
        @Override
        public UnitUsahaPariwisata createFromParcel(Parcel in) {
            return new UnitUsahaPariwisata(in);
        }

        @Override
        public UnitUsahaPariwisata[] newArray(int size) {
            return new UnitUsahaPariwisata[size];
        }
    };


}
