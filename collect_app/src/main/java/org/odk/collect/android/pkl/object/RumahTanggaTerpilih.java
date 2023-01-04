package org.odk.collect.android.pkl.object;

/**
 * Created by Geri_ on 12/19/2016.
 */

public class RumahTanggaTerpilih {

    private int idRtTerpilih;
    private String formId;
    private String fileName;
    private String kode_kabupaten;
    private String nama_kabupaten;
    private String kode_kecamatan;
    private String nama_kecamatan;
    private String kode_desa;
    private String nama_desa;
    private String klasifikasi;
    private String stratifikasi;
    private String noBs;
//    private String noSegmen;
    private String noUrutRuta;
    private String RTRW;
    private String nama_pemilik;
    private String kodebs;
    private String no_bangunan;
    private String no_urut_uup;
    private String jenisusaha;
//    private String jumlahART_10;

    public static final int SUDAHCACAH = 1;
    public static final int PROSESCACAH = 2;
    public static final int BELUMCACAH = 3;

    public RumahTanggaTerpilih(String fileName, String kode_kabupaten, String nama_kabupaten,
                               String kode_kecamatan, String nama_kecamatan,
                               String kode_desa, String nama_desa, String klasifikasi,
                               String noBs, String noUrutRuta, String RTRW,
                               String nama_pemilik, String kodebs, String no_bangunan, String no_urut_uup,
                               String jenisusaha) {

        this.setFileName(fileName);
        this.setKode_kabupaten(kode_kabupaten);
        this.setNama_kabupaten(nama_kabupaten);
        this.setKode_kecamatan(kode_kecamatan);
        this.setNama_kecamatan(nama_kecamatan);
        this.setKode_desa(kode_desa);
        this.setNamaDesa(nama_desa);
        this.setKlasifikasi(klasifikasi);
//        this.setStratifikasi(stratifikasi);
        this.setNoBs(noBs);
        this.setNoUrutRuta(noUrutRuta);
        this.setRTRW(RTRW);
        this.setNama_pemilik(nama_pemilik);
        this.setKodebs(kodebs);
        this.setNo_bangunan(no_bangunan);
        this.setNo_urut_uup(no_urut_uup);
        this.setJenisusaha(jenisusaha);
//        this.setJumlahART_10(jumlahART_10);

    }

    public int getIdRtTerpilih() {
        return idRtTerpilih;
    }//    public static final String alamat = "b109";

    public String getKodebs() {
        return kodebs;
    }

    public void setKodebs(String kodebs) {
        this.kodebs = kodebs;
    }

    public void setIdRtTerpilih(int idRtTerpilih) {
        this.idRtTerpilih = idRtTerpilih;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKode_kabupaten() {
        return kode_kabupaten;
    }

    public void setKode_kabupaten(String kode_kabupaten) {
        this.kode_kabupaten = kode_kabupaten;
    }

    public String getNama_kabupaten() {
        return nama_kabupaten;
    }

    public void setNama_kabupaten(String nama_kabupaten) {
        this.nama_kabupaten = nama_kabupaten;
    }

    public String getKode_kecamatan() {
        return kode_kecamatan;
    }

    public void setKode_kecamatan(String kode_kecamatan) {
        this.kode_kecamatan = kode_kecamatan;
    }

    public String getNama_kecamatan() {
        return nama_kecamatan;
    }

    public void setNama_kecamatan(String nama_kecamatan) {
        this.nama_kecamatan = nama_kecamatan;
    }

    public String getKode_desa() {
        return kode_desa;
    }

    public void setKode_desa(String kode_desa) {
        this.kode_desa = kode_desa;
    }

    public String getNamaDesa() {
        return nama_desa;
    }

    public void setNamaDesa(String nama_desa) {
        this.nama_desa = nama_desa;
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

    public String getNo_bangunan() {
        return no_bangunan;
    }

    public void setNo_bangunan(String no_bangunan) {
        this.no_bangunan = no_bangunan;
    }

    public String getNo_urut_uup() {
        return no_urut_uup;
    }

    public void setNo_urut_uup(String no_urut_uup) {
        this.no_urut_uup = no_urut_uup;
    }

    public String getJenisusaha() {
        return jenisusaha;
    }

    public void setJenisusaha(String jenisusaha) {
        this.jenisusaha = jenisusaha;
    }

//    public String getNoSegmen() {
//        return noSegmen;
//    }
//
//    public void setNoSegmen(String noSegmen) {
//        this.noSegmen = noSegmen;
//    }

    public String getNoUrutRuta() {
        return noUrutRuta;
    }

    public void setNoUrutRuta(String noUrutRuta) {
        this.noUrutRuta = noUrutRuta;
    }

    public String getRTRW() {
        return RTRW;
    }

    public void setRTRW(String RTRW) {
        this.RTRW = RTRW;
    }

    public String getNama_pemilik() {
        return nama_pemilik;
    }

    public void setNama_pemilik(String nama_pemilik) {
        this.nama_pemilik = nama_pemilik;
    }

    public String getKlasifikasi() {
        return klasifikasi;
    }

    public void setKlasifikasi(String klasifikasi) {
        this.klasifikasi = klasifikasi;
    }


//    public String getJumlahART_10() {
//        return jumlahART_10;
//    }
//
//    public void setJumlahART_10(String jumlahART_10) {
//        this.jumlahART_10 = jumlahART_10;
//    }
}
