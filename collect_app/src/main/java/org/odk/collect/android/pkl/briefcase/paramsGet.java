package org.odk.collect.android.pkl.briefcase;

import java.io.File;

//import stis55.odk.org.briefcaseandroid.briefcase.model.BriefcaseFormDefinition;
//import stis55.odk.org.briefcaseandroid.briefcase.model.FormStatus;
//import stis55.odk.org.briefcaseandroid.briefcase.model.ServerConnectionInfo;

/**
 * Created by Cloud Walker on 19/02/2016.
 */
public class paramsGet {
    private File formInstancesDir;
    private BriefcaseFormDefinition lfd;
    private FormStatus fs;
    private String uri;
    private String instancedira;
    private ServerConnectionInfo serverInfo;

    public paramsGet(File formInstancesDir, BriefcaseFormDefinition lfd, FormStatus fs, String uri, String instancedira, ServerConnectionInfo serverInfo) {
        this.formInstancesDir = formInstancesDir;
        this.lfd = lfd;
        this.fs = fs;
        this.uri = uri;
        this.instancedira = instancedira;
        this.serverInfo = serverInfo;
    }

    public File getFormInstancesDir() {
        return formInstancesDir;
    }

    public void setFormInstancesDir(File formInstancesDir) {
        this.formInstancesDir = formInstancesDir;
    }

    public BriefcaseFormDefinition getLfd() {
        return lfd;
    }

    public void setLfd(BriefcaseFormDefinition lfd) {
        this.lfd = lfd;
    }

    public FormStatus getFs() {
        return fs;
    }

    public void setFs(FormStatus fs) {
        this.fs = fs;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getInstancedira() {
        return instancedira;
    }

    public void setInstancedira(String instancedira) {
        this.instancedira = instancedira;
    }

    public ServerConnectionInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerConnectionInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
