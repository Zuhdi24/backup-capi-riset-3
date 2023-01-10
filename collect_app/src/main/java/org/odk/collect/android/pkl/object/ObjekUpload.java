package org.odk.collect.android.pkl.object;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by isfann on 1/1/2017.
 */

public class ObjekUpload {
    public ObjekUpload(String uploader, String deviceId, ArrayList<BlokSensus> dataBs, ArrayList<RumahTangga> dataRt, ArrayList<SampelRuta> dataSt) {
        this.uploader = uploader;
        this.deviceId = deviceId;
        time = new Date(System.currentTimeMillis());
        this.dataBs = dataBs;
        this.dataRt = dataRt;
        this.dataSt = dataSt;
    }

    String uploader;
    Date time;
    String deviceId;
    ArrayList<BlokSensus> dataBs;
    ArrayList<RumahTangga> dataRt;
    ArrayList<SampelRuta> dataSt;

    public ArrayList<BlokSensus> getDataBs() {
        return dataBs;
    }

    public ArrayList<RumahTangga> getDataRt() {
        return dataRt;
    }

    public ArrayList<SampelRuta> getDataSt() {
        return dataSt;
    }
}
