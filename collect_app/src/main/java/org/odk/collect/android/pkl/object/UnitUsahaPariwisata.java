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
    private String noUrutUUP;
    private String namaUUP;
    private String alamat;
    private String namaPemilikUUP;
    private String isPariwisata;
    private String isPandemi;
    private String jenisUUP;
    private String noHp;
    private String latitude;
    private String longitude;
    private String akurasi;
    private String status;
    private String time;

    public UnitUsahaPariwisata(String kodeUUP, String kodeBs, String noSegmen, String bf, String bs, String noUrutRuta, String noUrutUUP, String namaUUP, String alamat, String namaPemilikUUP, String isPariwisata, String isPandemi, String jenisUUP, String noHp, String latitude, String longitude, String akurasi, String status, String time) {
        this.kodeUUP = kodeUUP;
        this.kodeBs = kodeBs;
        this.noSegmen = noSegmen;
        this.bf = bf;
        this.bs = bs;
        this.noUrutRuta = noUrutRuta;
        this.noUrutUUP = noUrutUUP;
        this.namaUUP = namaUUP;
        this.alamat = alamat;
        this.namaPemilikUUP = namaPemilikUUP;
        this.isPariwisata = isPariwisata;
        this.isPandemi = isPandemi;
        this.jenisUUP = jenisUUP;
        this.noHp = noHp;
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

    public String getNoUrutUUP() {
        return noUrutUUP;
    }

    public void setNoUrutUUP(String noUrutUUP) {
        this.noUrutUUP = noUrutUUP;
    }

    public String getNamaUUP() {
        return namaUUP;
    }

    public void setNamaUUP(String namaUUP) {
        this.namaUUP = namaUUP;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNamaPemilikUUP() {
        return namaPemilikUUP;
    }

    public void setNamaPemilikUUP(String namaPemilikUUP) {
        this.namaPemilikUUP = namaPemilikUUP;
    }

    public String getIsPariwisata() {
        return isPariwisata;
    }

    public void setIsPariwisata(String isPariwisata) {
        this.isPariwisata = isPariwisata;
    }

    public String getIsPandemi() {
        return isPandemi;
    }

    public void setIsPandemi(String isPandemi) {
        this.isPandemi = isPandemi;
    }

    public String getJenisUUP() {
        return jenisUUP;
    }

    public void setJenisUUP(String jenisUUP) {
        this.jenisUUP = jenisUUP;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

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
