package org.odk.collect.android.pkl.object;

/**
 * Created by isfann on 1/11/2017.
 */

public class FileBackup {
    String date;
    String deviceId;
    String source;
    String path;

    public String getDate() {
        return date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSource() {
        return source;
    }

    public String getPath() {
        return path;
    }

    public FileBackup(String date, String device, String source, String path) {
        this.date = date;
        this.deviceId = device;
        this.source = source;
        this.path = path;
    }
}
