package org.odk.collect.android.pkl.object;

/**
 * Created by TimHode on 1/19/2017.
 */

public class WilayahKerja {

    private String kodeBlokSensus;
    private String kecamatan;
    private String kabupaten;
    private String kelurahan;
    private String beban;

    public WilayahKerja(String kodeBlokSensus, String beban, String kabupaten, String kecamatan, String kelurahan) {
        this.beban = beban;
        this.kodeBlokSensus = kodeBlokSensus;
        this.kabupaten = kabupaten;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
    }

    public String getKodeBlokSensus() {
        return kodeBlokSensus;
    }

    public String getBeban() {
        return beban;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public String getKecamatan() {
        return kecamatan;
    }
}
