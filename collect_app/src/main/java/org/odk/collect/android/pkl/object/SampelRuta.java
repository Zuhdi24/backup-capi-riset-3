package org.odk.collect.android.pkl.object;

/**
 * Created by isfann on 12/24/2016.
 */

public class SampelRuta {

    private String kodeBs;
    private String kodeRuta;

    public String getKodeRuta() {
        return kodeRuta;
    }

    public void setKodeRuta(String kodeRuta) {
        this.kodeRuta = kodeRuta;
    }

    public SampelRuta(String kodeBs, String kodeRuta) {
        this.setKodeBs(kodeBs);
        this.setKodeRuta(kodeRuta);
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
