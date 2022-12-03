package org.odk.collect.android.database;

import org.odk.collect.android.provider.InstanceProviderAPI;

/**
 * Created by Cloud Walker on 15/01/2016.
 */
public class DBErrorModel {
    private int _id;
    private String JR_Forn_Id;
    private String JR_Forn_Version;
    private String Instance_Path;
    private String XPath;
    private String NullXPath;
    private String UUID;
    private String check;
    private String kind;

    public DBErrorModel() {
        setCheck(InstanceProviderAPI.NOs);
    }

    public void setNullXPath(String a) {
        NullXPath = a;
    }

    public String getNullXPath() {
        return NullXPath;
    }

    public void setInstance_Path(String a) {
        Instance_Path = a;
    }

    public String getInstance_Path() {
        return Instance_Path;
    }

    public void setId(int a) {
        _id = a;
    }

    public void setJr_Form_Id(String a) {
        JR_Forn_Id = a;
    }

    public void setJR_Forn_Version(String a) {
        JR_Forn_Version = a;
    }

    public void setXPath(String a) {
        XPath = a;
    }

    public void setUUID(String a) {
        UUID = a;
    }

    public String getUUID() {
        return UUID;
    }

    public int getId() {
        return _id;
    }

    public String getJR_Forn_Version() {
        return JR_Forn_Version;
    }

    public String getJR_Forn_Id() {
        return JR_Forn_Id;
    }

    public String getXPath() {
        return XPath;
    }


    public String isCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
