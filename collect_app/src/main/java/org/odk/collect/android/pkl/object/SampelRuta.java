package org.odk.collect.android.pkl.object;

/**
 * Created by isfann on 12/24/2016.
 */

public class SampelRuta {

    private String kodeBs;
    private String kodeUUP;

    public String getKodeUUP() {
        return kodeUUP;
    }

    public void setKodeUUP(String kodeUUP) {
        this.kodeUUP = kodeUUP;
    }

    public SampelRuta(String kodeBs, String kodeUUP) {
        this.setKodeBs(kodeBs);
        this.setKodeUUP(kodeUUP);
    }

    public SampelRuta() {

    }

    public String getKodeBs() {
        return kodeBs;
    }

    public void setKodeBs(String kodeBs) {
        this.kodeBs = kodeBs;
    }

}
