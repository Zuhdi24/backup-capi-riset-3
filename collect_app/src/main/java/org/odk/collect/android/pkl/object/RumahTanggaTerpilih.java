package org.odk.collect.android.pkl.object;

/**
 * Created by Geri_ on 12/19/2016.
 */

public class RumahTanggaTerpilih {
    private String fileName;
    private String latitude;
    private String longitude;
    private String accuracy;
    private String kodeBs;
    private String kodeKabupaten;
    private String kodeKecamatan;
    private String kodeDesa;
    private String noBs;
    private String namaKabupaten;
    private String namaKecamatan;
    private String namaDesa;
    // Isian menu listing
    private String noUrutRuta;
    private String namaKrt;
    private String alamat;
    private String jumlahART;
    private String jumlahART10;
    private String noHp;
    private String kodeEligible;


    public static final int SUDAHCACAH = 1;
    public static final int PROSESCACAH = 2;
    public static final int BELUMCACAH = 3;

    public RumahTanggaTerpilih(String fileName, String latitude, String longitude, String accuracy, String kodeBs, String kodeKabupaten, String kodeKecamatan, String kodeDesa, String noBs, String namaKabupaten, String namaKecamatan, String namaDesa, String noUrutRuta, String namaKrt, String alamat, String jumlahART, String jumlahART10, String noHp, String kodeEligible) {
        this.fileName = fileName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.kodeBs = kodeBs;
        this.kodeKabupaten = kodeKabupaten;
        this.kodeKecamatan = kodeKecamatan;
        this.kodeDesa = kodeDesa;
        this.noBs = noBs;
        this.namaKabupaten = namaKabupaten;
        this.namaKecamatan = namaKecamatan;
        this.namaDesa = namaDesa;
        this.noUrutRuta = noUrutRuta;
        this.namaKrt = namaKrt;
        this.alamat = alamat;
        this.jumlahART = jumlahART;
        this.jumlahART10 = jumlahART10;
        this.noHp = noHp;
        this.kodeEligible = kodeEligible;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getKodeBs() {
        return kodeBs;
    }

    public void setKodeBs(String kodeBs) {
        this.kodeBs = kodeBs;
    }

    public String getKodeKabupaten() {
        return kodeKabupaten;
    }

    public void setKodeKabupaten(String kodeKabupaten) {
        this.kodeKabupaten = kodeKabupaten;
    }

    public String getKodeKecamatan() {
        return kodeKecamatan;
    }

    public void setKodeKecamatan(String kodeKecamatan) {
        this.kodeKecamatan = kodeKecamatan;
    }

    public String getKodeDesa() {
        return kodeDesa;
    }

    public void setKodeDesa(String kodeDesa) {
        this.kodeDesa = kodeDesa;
    }

    public String getNoBs() {
        return noBs;
    }

    public void setNoBs(String noBs) {
        this.noBs = noBs;
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

    public String getNoUrutRuta() {
        return noUrutRuta;
    }

    public void setNoUrutRuta(String noUrutRuta) {
        this.noUrutRuta = noUrutRuta;
    }

    public String getNamaKrt() {
        return namaKrt;
    }

    public void setNamaKrt(String namaKrt) {
        this.namaKrt = namaKrt;
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
}
