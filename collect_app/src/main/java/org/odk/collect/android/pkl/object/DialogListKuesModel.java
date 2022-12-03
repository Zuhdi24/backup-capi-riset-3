package org.odk.collect.android.pkl.object;

import java.io.Serializable;

/**
 * Created by Rahadi on 20/02/2017.
 */

public class DialogListKuesModel implements Serializable {

    private String displayName, displaySubText, JRVersion;

    public DialogListKuesModel(String JRVersion, String displayName, String displaySubText) {
        this.JRVersion = JRVersion;
        this.displayName = displayName;
        this.displaySubText = displaySubText;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplaySubText() {
        return displaySubText;
    }

    public String getJRVersion() {
        return JRVersion;
    }
}
