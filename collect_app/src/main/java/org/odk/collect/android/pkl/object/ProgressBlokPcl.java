package org.odk.collect.android.pkl.object;

/**
 * Created by TimHode on 1/24/2017.
 */

public class ProgressBlokPcl {
    private String noBlokSensus;
    private String progressBlokSensus;

    public ProgressBlokPcl(String noBlokSensus, String progressBlokSensus) {
        this.noBlokSensus = noBlokSensus;
        this.progressBlokSensus = progressBlokSensus;
    }

    public String getNoBlokSensus() {
        return noBlokSensus;
    }

    public String getProgressBlokSensus() {
        return progressBlokSensus;
    }
}
