package org.odk.collect.android.pkl.object;

import android.text.SpannableStringBuilder;

import org.odk.collect.android.pkl.util.HtmlString;

import java.io.Serializable;

/**
 * Created by Rahadi on 30/01/2017.
 */

public class NotificationModel implements Serializable {
    private int id;
    private String kategori;
    private String judul;
    private String konten;
    private SpannableStringBuilder kontenHTML;
    private String date;
    private String nim;
    private int isRead;

    public NotificationModel(int id, String kategori, String judul, String konten, String date, String nim, int isRead) {
        this.id = id;
        this.kategori = kategori;
        this.judul = judul;
        this.konten = konten;
        this.kontenHTML = HtmlString.stringToHTML(konten);
        this.date = date;
        this.nim = nim;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public String getKategori() {
        return kategori;
    }

    public String getJudul() {
        return judul;
    }

    public String getKonten() {
        return konten;
    }

    public String getDate() {
        return date;
    }

    public String getNim() {
        return nim;
    }

    public SpannableStringBuilder getKontenHTML() {
        return kontenHTML;
    }

    public int isRead() {
        return isRead;
    }
}
