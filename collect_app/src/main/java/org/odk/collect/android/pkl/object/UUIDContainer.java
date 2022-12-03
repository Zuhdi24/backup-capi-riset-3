package org.odk.collect.android.pkl.object;

/**
 * Created by isfann on 2/5/2017.
 */

public class UUIDContainer {
    private String kodeUUP;

    public UUIDContainer(String kodeUUP) {
        this.kodeUUP = kodeUUP;
    }

    public String getKodeRuta() {
        return kodeUUP;
    }

    public void setKodeRuta(String kodeUUP) {
        this.kodeUUP = kodeUUP;
    }
}
