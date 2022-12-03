package org.odk.collect.android.pkl.object;

/**
 * Created by Rahadi on 01/02/2017.
 */

public class Kondef {
    //DATABASE HANDLER
    //Table Names (prefix: TABLE_)
    public static final String TABLE_JARKOM = "jarkom";
    public static final String TABLE_KATEGORI_LIST = "kategori_list";
    public static final String TABLE_STATUS = "status";
    public static final String TABLE_NOTIFICATION = "notifikasi";
    //Attributes (prefix: AT_)
    public static final String AT_ID = "id";
    public static final String AT_NIM = "nim";
    public static final String AT_NIM_KORTIM = "nim_kortim";
    public static final String AT_WILAYAH = "wilayah";
    public static final String AT_KATEGORI = "kategori";
    public static final String AT_DESKRIPSI = "deskripsi";
    public static final String AT_NAMA_KATEGORI = "nama_kategori";
    public static final String AT_PERTANYAAN = "pertanyaan";
    public static final String AT_JAWABAN = "jawaban";
    public static final String AT_GOLONGAN = "golongan";
    public static final String AT_STATUS = "status";
    public static final String AT_TIMESTAMP = "timestamp";
    public static final String AT_PENANGGUNG_JAWAB = "penanggung_jawab";
    public static final String AT_BOOKMARKED = "bookmarked";
    public static final String AT_SHOWED = "showed";
    public static final String AT_JUDUL = "judul";
    public static final String AT_KONTEN = "konten";
    public static final String AT_READ = "read";

    //REQUEST HANDLER
    //Request URL(prefix: REQ_)
    public static final String REQ_BASE_URL = "https://pkl56.stis.ac.id/service";
    public static final String REQ_GET_CATEGORIES = "/get_daftar_kategori.php";
    public static final String REQ_GET_PROBLEMS = "/get_daftar_pertanyaan.php";
    public static final String REQ_GET_UNSOLVED_PROBLEMS = "/get_daftar_pertanyaan_unsolved.php";
    public static final String REQ_SEND_PROBLEMS = "/insert_pertanyaan.php";
    public static final String REQ_SEND_KORTIM_ANSWER = "/respon_pertanyaan.php";
    public static final String REQ_SEND_FORWARD_COMMAND = "/respon_pertanyaan.php";
    public static final String REQ_GET_MY_PROBLEMS = "/get_daftar_pertanyaan.php";
    public static final String REQ_GET_STATUS = "/get_daftar_status.php";
    public static final String REQ_GET_NOTIFICATIONS = "/get_notifikasi.php";
    //Request Parameters(prefix: PAR_)
    public static final String PAR_NIM = "nim";
    public static final String PAR_ID = "id";
    public static final String PAR_NIM_KORTIM = "nim_kortim";
    public static final String PAR_KATEGORI = "kategori";
    public static final String PAR_GOLONGAN = "golongan";
    public static final String PAR_PERTANYAAN = "pertanyaan";
    public static final String PAR_JAWABAN = "jawaban";
    //Response Key(prefix: RESP_)
    public static final String RESP_DAFTAR_KATEGORI = "daftar_kategori";
    public static final String RESP_DAFTAR_PERTANYAAN = "daftar_pertanyaan";
    public static final String RESP_DAFTAR_NOTIFIKASI = "daftar_notifikasi";
    public static final String RESP_DETAIL_NOTIFIKASI = "detail_notifikasi";
    public static final String RESP_DAFTAR_STATUS = "daftar_status";
    public static final String RESP_PERTANYAAN_BARU = "pertanyaan_baru";
    public static final String RESP_PERTANYAAN_TERJAWAB = "pertanyaan_terjawab";
    public static final String RESP_PERTANYAAN_DITERUSKAN = "pertanyaan_diteruskan";
    public static final String RESP_ID = "id";
    public static final String RESP_NIM = "nim";
    public static final String RESP_NIM_KORTIM = "nim_kortim";
    public static final String RESP_KATEGORI = "kategori";
    public static final String RESP_DESKRIPSI = "deskripsi";
    public static final String RESP_PENANGGUNG_JAWAB = "penanggung_jawab";
    public static final String RESP_WILAYAH = "wilayah";
    public static final String RESP_PERTANYAAN = "pertanyaan";
    public static final String RESP_JAWABAN = "jawaban";
    public static final String RESP_GOLONGAN = "golongan";
    public static final String RESP_STATUS = "status";
    public static final String RESP_TIMESTAMP = "timestamp";
    public static final String RESP_JUDUL = "judul";
    public static final String RESP_KONTEN = "konten";

    //EXTRA
    //Extra Keys(prefix: EXTRA_)
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_PERTANYAAN = "pertanyaan";
    public static final String EXTRA_KATEGORI = "kategori";
    public static final String EXTRA_NAMA_KATEGORI = "nama_kategori";

    //TEMPORARY
    //Temporary Keys(prefix: TMP_)
}
