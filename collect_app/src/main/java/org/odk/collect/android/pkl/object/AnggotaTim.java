package org.odk.collect.android.pkl.object;

/**
 * Created by TimHode on 1/20/2017.
 */

public class AnggotaTim {
    private String nim;
    private String nama;
    private String progres;
    private String nomor;


    public AnggotaTim(String nim, String nama, String progres, String nomor) {
        this.nama = nama;
        this.nim = nim;
        this.progres = progres;
        this.nomor = nomor;

    }


    public String getNomorAng() {
        return nomor;
    }

    public String getNimAng() {
        return nim;
    }

    public String getNamaAng() {
        return nama;
    }

    public String getProgres() {
        return progres;
    }
}
