package org.odk.collect.android.pkl.object;

/**
 * Created by user on 2/14/2017.
 */

public class BebanBlokSensus {
    private String nim;
    private String kel;
    private String kec;

    public BebanBlokSensus(String nim, String kel, String kec) {
        this.nim = nim;
        this.kel = kel;
        this.kec = kec;
    }

    public String getNim() {
        return nim;
    }

    public String getKec() {
        return kec;
    }

    public String getKel() {
        return kel;
    }
}
