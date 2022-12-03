package org.odk.collect.android.pkl.object;

/**
 * Created by Lenovo on 1/26/2016.
 */
public class Notifikasi {
    public static final String TASKS_TABLE = "notifikasi";
    public static final String TASKS_TABLE_GAGAL = "notifikasi_gagal";
    public static final String TASK_ID = "id";
    public static final String TASK_INSTANCE = "unique_id_instance";
    public static final String TASK_NIM = "nim";
    public static final String TASK_NAMA = "nama";
    public static final String TASK_KORTIM = "kortim";
    public static final String TASK_STATUS_ISIAN = "status_isian";
    public static final String TASK_STATUS = "status";
    public static final String TASK_FORM = "form_id";
    public static final String TASK_FILENAME = "filename";

    private int id;
    private String unique_id_instance;
    private String nim;
    private String nama;
    private String kortim;
    private String status_isian;
    private String status;
    private String form_id;
    private String filename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnique_id_instance() {
        return unique_id_instance;
    }

    public void setUnique_id_instance(String unique_id_instance) {
        this.unique_id_instance = unique_id_instance;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getKortim() {
        return kortim;
    }

    public void setKortim(String kortim) {
        this.kortim = kortim;
    }

    public String getStatus_isian() {
        return status_isian;
    }

    public void setStatus_isian(String status_isian) {
        this.status_isian = status_isian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
