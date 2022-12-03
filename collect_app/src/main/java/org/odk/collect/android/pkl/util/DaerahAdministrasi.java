package org.odk.collect.android.pkl.util;

/**
 * Created by Ridwan on 12/20/2016.
 */

public final class DaerahAdministrasi {

    private static String namaKabupaten;
    private static String namaKecamatan;
    private static String namaDesa;

    private static String[] listkabupaten = new String[]{"Pilih Kabupaten", "BANGKA", "BELITUNG", "BANGKA BARAT", "BANGKA TENGAH", "BANGKA SELATAN", "BELITUNG TIMUR", "PANGKAL PINANG", "DUMMY_KABUPATEN"};
    private static String[] listkodekabupaten = new String[]{"0", "01", "02", "03", "04", "05", "06", "71", "99"};

    private static String a = "Pilih Kecamatan";
    private static String[] bangka = new String[]{a, "MENDO BARAT", "MERAWANG", "PUDING BESAR", "SUNGAI LIAT", "PEMALI", "BAKAM", "BELINYU", "RIAU SILIP"};
    private static String[] belitung = new String[]{a, "MEMBALONG", "TANJUNG PANDAN", "BADAU", "SIJUK", "SELAT NASIK"};
    private static String[] bangkabarat = new String[]{a, "KELAPA", "TEMPILANG", "MENTOK", "SIMPANG TERITIP", "JEBUS"};
    private static String[] bangkatengah = new String[]{a, "KOBA", "LUBUK BESAR", "PANGKALAN BARU", "NAMANG", "SUNGAI SELAN", "SIMPANG KATIS"};
    private static String[] bangkaselatan = new String[]{a, "PAYUNG", "PULAU BESAR", "SIMPANG RIMBA", "TOBOALI", "TUKAK SADAI", "AIR GEGAS", "LEPAR PONGOK"};
    private static String[] belitungtimur = new String[]{a, "DENDANG", "SIMPANG PESAK", "GANTUNG", "SIMPANG RENGGIANG", "MANGGAR", "DAMAR", "KELAPA KAMPIT"};
    private static String[] pangkalpinang = new String[]{a, "RANGKUI", "BUKIT INTAN", "PANGKAL BALAM", "TAMAN SARI", "GERUNGGANG"};
    private static String[] dummykabupaten = new String[]{a, "DUMMY_KECAMATAN"};

    private static String[] bangka_kode = new String[]{"0", "070", "080", "081", "090", "091", "092", "130", "131"};
    private static String[] belitung_kode = new String[]{"0", "010", "060", "061", "062", "063"};
    private static String[] bangkabarat_kode = new String[]{"0", "010", "020", "030", "040", "050"};
    private static String[] bangkatengah_kode = new String[]{"0", "010", "011", "020", "021", "030", "040"};
    private static String[] bangkaselatan_kode = new String[]{"0", "010", "011", "020", "030", "031", "040", "050"};
    private static String[] belitungtimur_kode = new String[]{"0", "010", "011", "020", "021", "030", "031", "040"};
    private static String[] pangkalpinang_kode = new String[]{"0", "010", "020", "030", "040", "041"};
    private static String[] dummykabupaten_kode = new String[]{"0", "999"};

    private static String b = "Pilih Desa";

    //Bangka
    private static String[] mendobarat = new String[]{b, "KOTA KAPUR", "PENAGAN", "RUKAM", "AIR BULUH", "KACE", "CENGKONGABANG", "AIRDUREN", "PETALING", "MENDUK", "PAYABENUA", "KEMUJA", "ZED", "LABUH AIR PANDAN", "KACE TIMUR"};
    private static String[] merawang = new String[]{b, "KIMAK", "JADA BAHRIN", "BALUNIJUK", "PAGARAWAN", "BATURUSA", "AIR ANYIR", "RIDING PANJANG", "DWI MAKMUR", "JURUNG", "MERAWANG"};
    private static String[] pudingbesar = new String[]{b, "KOTAWARINGIN", "SAING", "TANAH BAWAH", "NIBUNG", "LABU", "PUDINGBESAR", "KAYU BESI"};
    private static String[] sungailiat = new String[]{b, "KENANGA", "REBO", "PARIT PADANG", "SRIMENANTI", "SUNGAILIAT", "KUDAY", "SINAR BARU"};
    private static String[] pemali = new String[]{b, "PENYAMUN", "PEMALI", "AIR DUREN", "AIR RUAI", "KARYA MAKMUR", "SEMPAN"};
    private static String[] bakam = new String[]{b, "MARAS SENANG", "KAPUK", "NEKNANG", "TIANG TARA", "DALIL", "BAKAM", "MANGKA", "MABAT", "BUKITLAYANG"};
    private static String[] belinyu = new String[]{b, "LUMUT", "RIDING PANJANG", "GUNUNG MUDA", "KUTO PANJI", "AIR JUKUNG", "BUKIT KETOK", "BINTET", "GUNUNG PELAWAN"};
    private static String[] riausilip = new String[]{b, "BANYU ASIN", "PANGKAL NIUR", "PUGUL", "CIT", "DENIANG", "MAPUR", "SILIP", "RIAU", "BERBURA"};

    private static String[] mendobarat_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014"};
    private static String[] merawang_kode = new String[]{"0", "010", "011", "012", "013", "014", "015", "016", "017", "018", "019"};
    private static String[] pudingbesar_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007"};
    private static String[] sungailiat_kode = new String[]{"0", "014", "015", "016", "017", "018", "019", "020"};
    private static String[] pemali_kode = new String[]{"0", "001", "002", "003", "004", "005", "006"};
    private static String[] bakam_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009"};
    private static String[] belinyu_kode = new String[]{"0", "009", "010", "011", "012", "013", "014", "015", "016"};
    private static String[] riausilip_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009"};

    //Belitung
    private static String[] membalong = new String[]{b, "PULAU SELIU", "MEMBALONG", "MENTIGI", "TANJUNG RUSA", "KEMBIRI", "PERPAT", "LASSAR", "SIMPANG RUSA", "BANTAN", "PULAU SUMEDANG", "GUNUNG RITING", "PADANG KANDIS"};
    private static String[] tanjungpandan = new String[]{b, "BULUH TUMBANG", "PERAWAS", "LESUNG BATANG", "PANGKAL LALANG", "DUKONG", "JURU SEBERANG", "KOTA TANJUNG PANDAN", "PARIT", "TANJUNG PENDAM", "AIR SAGA", "PAAL SATU", "AIR MERBAU"};
    private static String[] badau = new String[]{b, "PEGANTUNGAN", "SUNGAI SAMAK", "CERUCUK", "BADAU", "KACANG BOTOR", "AIR BATU BUDING"};
    private static String[] sijuk = new String[]{b, "BATU ITAM", "TERONG", "AIR SERU", "AIR SELUMAR", "TANJUNG BINGA", "KECIPUT", "SUNGAI PADANG"};
    private static String[] selatnasik = new String[]{b, "SUAK GUAL", "PETALING", "SELAT NASIK", "PULAU GERSIK"};

    private static String[] membalong_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012"};
    private static String[] tanjungpandan_kode = new String[]{"0", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018"};
    private static String[] badau_kode = new String[]{"0", "001", "002", "003", "004", "005", "006"};
    private static String[] sijuk_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008"};
    private static String[] selatnasik_kode = new String[]{"0", "001", "002", "003", "004"};

    //Bangka Barat
    private static String[] kelapa = new String[]{b, "KAYUARANG", "MANCUNG", "SINAR SARI", "KELAPA", "BERUAS", "PUSUK", "TEBING", "AIR BULIN", "DENDANG", "KACUNG", "TERENTANG", "TUGANG", "TUIK", "PANGKAL BERAS"};
    private static String[] tempilang = new String[]{b, "TANJUNGNIUR", "BENTENG KUTA", "AIR LINTANG", "SINAR SURYA", "TEMPILANG", "BUYAN KELUMBI", "SANGKU", "PENYAMPAK", "SIMPANG YUL"};
    private static String[] mentok = new String[]{b, "BELOLAUT", "AIR BELO", "SUNGAI BARU", "SUNGAI DAENG", "TANJUNG", "AIR PUTIH", "AIRLIMAU"};
    private static String[] simpangteritip = new String[]{b, "KUNDI", "SIMPANG TIGA", "MAYANG", "RAMBAT", "SIMPANG GONG", "PELANGAS", "BERANG", "IBUL", "PERADONG", "AIR NYATOH", "PANGEK", "AIR MENDUYUNG", "BUKIT TERAK"};
    private static String[] jebus = new String[]{b, "JEBUS", "TUMPAK PETAR", "LIMBUNG", "RUKAM", "BAKIT", "SEMULUT", "KAPIT", "TELAK", "RANGGI/ASAM", "SUNGAI BULUH", "KETAP", "PUPUT", "SEKAR BIRU", "AIR GANTANG", "KELABAT", "CUPAT", "TELUK LIMAU", "MISLAK", "AIR KUANG", "PEBUAR", "SINAR MANIK"};

    private static String[] kelapa_kode = new String[]{"0", "010", "011", "012", "013", "014", "015", "016", "017", "018", "019", "020", "021", "022", "023"};
    private static String[] tempilang_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009"};
    private static String[] mentok_kode = new String[]{"0", "01", "004", "005", "006", "007", "008", "009"};
    private static String[] simpangteritip_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013"};
    private static String[] jebus_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018", "019", "020", "021"};

    //Bangka Tengah
    private static String[] koba = new String[]{b, "NIBUNG", "KOBA", "ARUNG DALAM", "GUNTUNG", "TERENTANG TIGA", "PENYAK", "KURAU", "KURAU BARAT", "SIMPANG PERLANG", "PADANG MULIA", "BEROK"};
    private static String[] lubukbesar = new String[]{b, "KULUR", "KULUR ILIR", "TERUBUS", "PERLANG", "LUBUK BESAR", "LUBUK PABRIK", "LUBUK LINGKUK", "BATU BERIGA"};
    private static String[] pangkalanbaru = new String[]{b, "TANJUNG GUNUNG", "BENTENG", "AIRMESU", "DUL", "MANGKOL", "PADANG BARU", "JERUK", "BELULUK", "BATU BELUBANG", "PEDINDANG"};
    private static String[] namang = new String[]{b, "BELILIK", "NAMANG", "JELUTUNG", "CAMBAI", "KAYU BESI", "BUKIT KIJANG", "BASKARA BAKTI"};
    private static String[] sungaiselan = new String[]{b, "SUNGAI SELAN", "LAMPUR", "KERANTAI", "KERETAK", "SARANGMANDI", "MUNGGU", "KEMINGKING", "SUNGAI SELAN ATAS", "RAMADHON", "KERAKAS", "TANJUNG PURA"};
    private static String[] simpangkatis = new String[]{b, "SUNGKAP", "CELUAK", "PUPUT", "SIMPANG KATIS", "TERU", "BERUAS", "TERAK", "PASIR GARAM", "PINANG SEBATANG", "KATIS"};

    private static String[] koba_kode = new String[]{"0", "001", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015"};
    private static String[] lubukbesar_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008"};
    private static String[] pangkalanbaru_kode = new String[]{"0", "006", "007", "008", "009", "010", "012", "013", "014", "015", "16"};
    private static String[] namang_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007"};
    private static String[] sungaiselan_kode = new String[]{"0", "001", "002", "003", "008", "009", "010", "011", "012", "013", "014", "015"};
    private static String[] simpangkatis_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010"};

    //Bangka Selatan "Payung","Pulau Besar","Simpang Rimba","Toboali","Tukak Sadai","Air Gegas","Lepar Pongok"
    private static String[] payung = new String[]{b, "BEDENGUNG", "IRAT", "SENGIR", "PAYUNG", "NADUNG", "RANGGUNG", "PANGKALBULUH", "MALIK", "PAKU"};
    private static String[] pulaubesar = new String[]{b, "BATU BETUMPANG", "PANCA TUNGGAL", "FAJAR INDAH", "SUKAJAYA", "SUMBER JAYA PERMAI"};
    private static String[] simpangrimba = new String[]{b, "JELUTUNG II", "GUDANG", "SEBAGIN", "RAJIK", "SIMPANG RIMBA", "BANGKA KOTA", "PERMIS"};
    private static String[] toboali = new String[]{b, "RIAS", "TELADAN", "TANJUNG KETAPANG", "TOBOALI", "KAPOSANG", "GADUNG", "BIKANG", "JERIJI", "SERDANG", "RINDIK", "KEPOH"};
    private static String[] tukaksadai = new String[]{b, "SADAI", "PASIR", "PUTIH", "TUKAK", "TIRAM", "BUKIT TERAP"};
    private static String[] airgegas = new String[]{b, "PERGAM", "BENCAH", "TEPUS", "AIRGEGAS", "DELAS", "SIDOHARJO", "NYELANDING", "NANGKA", "RANGGAS", "AIR BARA"};
    private static String[] leparpongok = new String[]{b, "PENUTUK", "TANJUNGLABU", "PONGOK", "TANJUNG SANGKAR", "KUMBUNG", "CELAGEN"};

    private static String[] payung_kode = new String[]{"0", "005", "006", "007", "008", "009", "010", "017", "018", "020"};
    private static String[] pulaubesar_kode = new String[]{"0", "001", "002", "003", "004", "005"};
    private static String[] simpangrimba_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007"};
    private static String[] toboali_kode = new String[]{"0", "001", "002", "003", "004", "008", "009", "010", "012", "013", "014"};
    private static String[] tukaksadai_kode = new String[]{"0", "001", "002", "003", "004", "005"};
    private static String[] airgegas_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010"};
    private static String[] leparpongok_kode = new String[]{"0", "001", "002", "003", "004", "005", "006"};

    //Belitung Timur "Dendang","Simpang Pesak","Gantung","Simpang Renggiang","Manggar","Damar","Kelapa Kampit"
    private static String[] dendang = new String[]{b, "DENDANG", "JANGKANG", "NYURUK", "BALOK"};
    private static String[] simpangpesak = new String[]{b, "TANJUNG KELUMPANG", "TANJUNG BATU ITAM", "DUKONG", "SIMPANG PESAK"};
    private static String[] gantung = new String[]{b, "LILANGAN", "JANGKAR ASAM", "GANTUNG", "SELINGSING", "LIMBONGAN", "BATU PENYU", "LENGGANG"};
    private static String[] simpangrenggiang = new String[]{b, "LINTANG", "AIK MADU", "RENGGIANG", "SIMPANG TIGA"};
    private static String[] manggar = new String[]{b, "KELUBI", "PADANG", "LALANG", "LALANG JAYA", "KURNIA JAYA", "BARU", "BUKU LIMAU", "BANTAIAN JAYA", "MEKAR JAYA"};
    private static String[] damar = new String[]{b, "SUKAMANDI", "MENGKUBANG", "BURUNG MANDI", "MEMPAYA", "AIR KELIK"};
    private static String[] kelapakampit = new String[]{b, "CENDIL", "BUDING", "MENTAWAK", "SENYUBUK", "MAYANG", "PEMBAHARUAN"};

    private static String[] dendang_kode = new String[]{"0", "004", "005", "006", "008"};
    private static String[] simpangpesak_kode = new String[]{"0", "001", "002", "003", "004"};
    private static String[] gantung_kode = new String[]{"0", "001", "002", "003", "004", "008", "009", "010"};
    private static String[] simpangrenggiang_kode = new String[]{"0", "001", "002", "003", "004"};
    private static String[] manggar_kode = new String[]{"0", "001", "002", "003", "004", "005", "006", "010", "011", "012"};
    private static String[] damar_kode = new String[]{"0", "001", "002", "003", "004", "005"};
    private static String[] kelapakampit_kode = new String[]{"0", "001", "002", "003", "004", "005", "007"};

    //Pangkal Pinang "Rangkui","Bukit Intan","Pangkal Balam","Taman Sari","Gerunggang"
    private static String[] rangkui = new String[]{b, "ASAM", "PARIT LALANG", "BINTANG", "PASIR PUTIH", "MELINTANG", "KERAMAT", "MASJID JAMIK", "PASIR PADI", "PINTU AIR"};
    private static String[] bukitintan = new String[]{b, "SRIWIJAYA", "SEMABUNG LAMA", "BACANG", "AIR ITAM", "BUKIT BESAR", "BUKIT INTAN", "SEMABUNG BARU"};
    private static String[] pangkalbalam = new String[]{b, "AMPUI", "LONTONG PANCUR", "PASIR GARAM", "GABEK II", "GABEK I", "SELINDUNG BARU", "AIR SALEMBA", "REJOSARI", "KETAPANG", "SELINDUNG"};
    private static String[] tamansari = new String[]{b, "BATIN TIKAL", "RAWA BANGUN", "GEDUNG NASIONAL", "OPAS INDAH"};
    private static String[] gerunggang = new String[]{b, "KACANG PEDANG KEJAKSAAN", "KACANG PEDANG", "TUATUNU", "BUKIT MERAPIN", "BUKIT SARI", "TAMAN BUNGA"};

    private static String[] rangkui_kode = new String[]{"0", "001", "002", "004", "006", "009", "010", "013", "014", "015"};
    private static String[] bukitintan_kode = new String[]{"0", "001", "008", "009", "010", "011", "012", "013"};
    private static String[] pangkalbalam_kode = new String[]{"0", "006", "009", "010", "011", "012", "013", "014", "015", "016", "017"};
    private static String[] tamansari_kode = new String[]{"0", "019", "020", "021", "022"};
    private static String[] gerunggang_kode = new String[]{"0", "001", "002", "003", "004", "005", "006"};

    //    Dummy Kabupaten
    private static String[] dummykecamatan = new String[]{b, "DUMMY_DESA"};

    private static String[] dummykecamatan_kode = new String[]{"0", "999"};

    public static String getA() {
        return a;
    }

    public static String[] getBangka() {
        return bangka;
    }

    public static String[] getBangkabarat() {
        return bangkabarat;
    }

    public static String[] getBangkatengah() {
        return bangkatengah;
    }

    public static String[] getBangkaselatan() {
        return bangkaselatan;
    }

    public static String[] getBelitung() {
        return belitung;
    }

    public static String[] getBelitungtimur() {
        return belitungtimur;
    }

    public static String[] getPangkalpinang() {
        return pangkalpinang;
    }

    public static String getB() {
        return b;
    }

    public static String[] getSungailiat() {
        return sungailiat;
    }

    public static String[] getBelinyu() {
        return belinyu;
    }

    public static String[] getMerawang() {
        return merawang;
    }

    public static String[] getMendobarat() {
        return mendobarat;
    }

    public static String[] getPemali() {
        return pemali;
    }

    public static String[] getBakam() {
        return bakam;
    }

    public static String[] getRiausilip() {
        return riausilip;
    }

    public static String[] getPudingbesar() {
        return pudingbesar;
    }

    public static String[] getTanjungpandan() {
        return tanjungpandan;
    }

    public static String[] getMembalong() {
        return membalong;
    }

    public static String[] getSelatnasik() {
        return selatnasik;
    }

    public static String[] getSijuk() {
        return sijuk;
    }

    public static String[] getBadau() {
        return badau;
    }

    public static String[] getToboali() {
        return toboali;
    }

    public static String[] getLeparpongok() {
        return leparpongok;
    }

    public static String[] getAirgegas() {
        return airgegas;
    }

    public static String[] getSimpangrimba() {
        return simpangrimba;
    }

    public static String[] getPayung() {
        return payung;
    }

    public static String[] getKoba() {
        return koba;
    }

    public static String[] getPangkalanbaru() {
        return pangkalanbaru;
    }

    public static String[] getSungaiselan() {
        return sungaiselan;
    }

    public static String[] getSimpangkatis() {
        return simpangkatis;
    }

    public static String[] getMentok() {
        return mentok;
    }

    public static String[] getSimpangteritip() {
        return simpangteritip;
    }

    public static String[] getJebus() {
        return jebus;
    }

    public static String[] getKelapa() {
        return kelapa;
    }

    public static String[] getTempilang() {
        return tempilang;
    }

    public static String[] getManggar() {
        return manggar;
    }

    public static String[] getGantung() {
        return gantung;
    }

    public static String[] getDendang() {
        return dendang;
    }

    public static String[] getKelapakampit() {
        return kelapakampit;
    }

    public static String[] getBukitintan() {
        return bukitintan;
    }

    public static String[] getTamansari() {
        return tamansari;
    }

    public static String[] getPangkalbalam() {
        return pangkalbalam;
    }

    public static String[] getRangkui() {
        return rangkui;
    }

    public static String[] getGerunggang() {
        return gerunggang;
    }

    public static String[] getListkabupaten() {
        return listkabupaten;
    }

    public static String[] getListkodekabupaten() {
        return listkodekabupaten;
    }

    public static String[] getBangka_kode() {
        return bangka_kode;
    }

    public static String[] getBelitung_kode() {
        return belitung_kode;
    }

    public static String[] getBangkabarat_kode() {
        return bangkabarat_kode;
    }

    public static String[] getBangkatengah_kode() {
        return bangkatengah_kode;
    }

    public static String[] getBangkaselatan_kode() {
        return bangkaselatan_kode;
    }

    public static String[] getBelitungtimur_kode() {
        return belitungtimur_kode;
    }

    public static String[] getPangkalpinang_kode() {
        return pangkalpinang_kode;
    }

    public static String[] getMendobarat_kode() {
        return mendobarat_kode;
    }

    public static String[] getMerawang_kode() {
        return merawang_kode;
    }

    public static String[] getPudingbesar_kode() {
        return pudingbesar_kode;
    }

    public static String[] getSungailiat_kode() {
        return sungailiat_kode;
    }

    public static String[] getPemali_kode() {
        return pemali_kode;
    }

    public static String[] getBakam_kode() {
        return bakam_kode;
    }

    public static String[] getBelinyu_kode() {
        return belinyu_kode;
    }

    public static String[] getRiausilip_kode() {
        return riausilip_kode;
    }

    public static String[] getMembalong_kode() {
        return membalong_kode;
    }

    public static String[] getTanjungpandan_kode() {
        return tanjungpandan_kode;
    }

    public static String[] getBadau_kode() {
        return badau_kode;
    }

    public static String[] getSijuk_kode() {
        return sijuk_kode;
    }

    public static String[] getSelatnasik_kode() {
        return selatnasik_kode;
    }

    public static String[] getKelapa_kode() {
        return kelapa_kode;
    }

    public static String[] getTempilang_kode() {
        return tempilang_kode;
    }

    public static String[] getMentok_kode() {
        return mentok_kode;
    }

    public static String[] getSimpangteritip_kode() {
        return simpangteritip_kode;
    }

    public static String[] getJebus_kode() {
        return jebus_kode;
    }

    public static String[] getLubukbesar() {
        return lubukbesar;
    }

    public static String[] getNamang() {
        return namang;
    }

    public static String[] getKoba_kode() {
        return koba_kode;
    }

    public static String[] getLubukbesar_kode() {
        return lubukbesar_kode;
    }

    public static String[] getPangkalanbaru_kode() {
        return pangkalanbaru_kode;
    }

    public static String[] getNamang_kode() {
        return namang_kode;
    }

    public static String[] getSungaiselan_kode() {
        return sungaiselan_kode;
    }

    public static String[] getSimpangkatis_kode() {
        return simpangkatis_kode;
    }

    public static String[] getPulaubesar() {
        return pulaubesar;
    }

    public static String[] getTukaksadai() {
        return tukaksadai;
    }

    public static String[] getPayung_kode() {
        return payung_kode;
    }

    public static String[] getPulaubesar_kode() {
        return pulaubesar_kode;
    }

    public static String[] getSimpangrimba_kode() {
        return simpangrimba_kode;
    }

    public static String[] getToboali_kode() {
        return toboali_kode;
    }

    public static String[] getTukaksadai_kode() {
        return tukaksadai_kode;
    }

    public static String[] getAirgegas_kode() {
        return airgegas_kode;
    }

    public static String[] getLeparpongok_kode() {
        return leparpongok_kode;
    }

    public static String[] getSimpangpesak() {
        return simpangpesak;
    }

    public static String[] getSimpangrenggiang() {
        return simpangrenggiang;
    }

    public static String[] getDamar() {
        return damar;
    }

    public static String[] getDendang_kode() {
        return dendang_kode;
    }

    public static String[] getSimpangpesak_kode() {
        return simpangpesak_kode;
    }

    public static String[] getGantung_kode() {
        return gantung_kode;
    }

    public static String[] getSimpangrenggiang_kode() {
        return simpangrenggiang_kode;
    }

    public static String[] getManggar_kode() {
        return manggar_kode;
    }

    public static String[] getDamar_kode() {
        return damar_kode;
    }

    public static String[] getKelapakampit_kode() {
        return kelapakampit_kode;
    }

    public static String[] getRangkui_kode() {
        return rangkui_kode;
    }

    public static String[] getBukitintan_kode() {
        return bukitintan_kode;
    }

    public static String[] getPangkalbalam_kode() {
        return pangkalbalam_kode;
    }

    public static String[] getTamansari_kode() {
        return tamansari_kode;
    }

    public static String[] getGerunggang_kode() {
        return gerunggang_kode;
    }

    public static String[] getDummykabupaten() {
        return dummykabupaten;
    }

    public static String[] getDummykabupaten_kode() {
        return dummykabupaten_kode;
    }

    public static String[] getDummykecamatan() {
        return dummykecamatan;
    }

    public static String[] getDummykecamatan_kode() {
        return dummykecamatan_kode;
    }

    public static String getNamaKabupaten(String kodeKabupaten) {
        if (kodeKabupaten.equals("01")) {
            namaKabupaten = "BANGKA";
        } else if (kodeKabupaten.equals("02")) {
            namaKabupaten = "BELITUNG";
        } else if (kodeKabupaten.equals("03")) {
            namaKabupaten = "BANGKA BARAT";
        } else if (kodeKabupaten.equals("04")) {
            namaKabupaten = "BANGKA TENGAH";
        } else if (kodeKabupaten.equals("05")) {
            namaKabupaten = "BANGKA SELATAN";
        } else if (kodeKabupaten.equals("06")) {
            namaKabupaten = "BELITUNG TIMUR";
        } else if (kodeKabupaten.equals("71")) {
            namaKabupaten = "PANGKAL PINANG";
        } else if (kodeKabupaten.equals("99")) {
            namaKabupaten = "DUMMY_KABUPATEN";
        }
        return namaKabupaten;
    }

    public static String getNamaKecamatan(String kodeKabupaten, String kodeKecamatan) {
        int posKec = 0;
        if (kodeKabupaten.equals("01")) {
            for (String kodeKec : bangka_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = bangka[posKec];
        } else if (kodeKabupaten.equals("02")) {
            for (String kodeKec : belitung_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = belitung[posKec];
        } else if (kodeKabupaten.equals("03")) {
            for (String kodeKec : bangkabarat_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = bangkabarat[posKec];
        } else if (kodeKabupaten.equals("04")) {
            for (String kodeKec : bangkatengah_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = bangkatengah[posKec];
        } else if (kodeKabupaten.equals("05")) {
            for (String kodeKec : bangkaselatan_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = bangkaselatan[posKec];
        } else if (kodeKabupaten.equals("06")) {
            for (String kodeKec : belitungtimur_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = belitungtimur[posKec];
        } else if (kodeKabupaten.equals("71")) {
            for (String kodeKec : pangkalpinang_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = pangkalpinang[posKec];
        } else if (kodeKabupaten.equals("99")) {
            for (String kodeKec : dummykabupaten_kode) {
                if (kodeKecamatan.equals(kodeKec)) {
                    break;
                }
                posKec++;
            }
            namaKecamatan = dummykabupaten[posKec];
        }
        return namaKecamatan;
    }

    public static String getNamaDesa(String namaKecamatan, String kodeDesa) {
        int pos = 0;
        if (namaKecamatan.equals("MENDO BARAT")) {
            for (String kodeDes : mendobarat_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = mendobarat[pos];
        } else if (namaKecamatan.equals("MERAWANG")) {
            for (String kodeDes : merawang_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = merawang[pos];
        } else if (namaKecamatan.equals("PUDING BESAR")) {
            for (String kodeDes : pudingbesar_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = pudingbesar[pos];
        } else if (namaKecamatan.equals("SUNGAI LIAT")) {
            for (String kodeDes : sungailiat_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = sungailiat[pos];
        } else if (namaKecamatan.equals("PEMALI")) {
            for (String kodeDes : pemali_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = pemali[pos];
        } else if (namaKecamatan.equals("BAKAM")) {
            for (String kodeDes : bakam_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = bakam[pos];
        } else if (namaKecamatan.equals("BELINYU")) {
            for (String kodeDes : belinyu_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = belinyu[pos];
        } else if (namaKecamatan.equals("RIAU SILIP")) {
            for (String kodeDes : riausilip_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = riausilip[pos];
        } else if (namaKecamatan.equals("MEMBALONG")) {
            for (String kodeDes : membalong_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = membalong[pos];
        } else if (namaKecamatan.equals("TANJUNG PANDAN")) {
            for (String kodeDes : tanjungpandan_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = tanjungpandan[pos];
        } else if (namaKecamatan.equals("BADAU")) {
            for (String kodeDes : badau_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = badau[pos];
        } else if (namaKecamatan.equals("SIJUK")) {
            for (String kodeDes : sijuk_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = sijuk[pos];
        } else if (namaKecamatan.equals("SELAT NASIK")) {
            for (String kodeDes : selatnasik_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = selatnasik[pos];
        } else if (namaKecamatan.equals("KELAPA")) {
            for (String kodeDes : kelapa_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = kelapa[pos];
        } else if (namaKecamatan.equals("TEMPILANG")) {
            for (String kodeDes : tempilang_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = tempilang[pos];
        } else if (namaKecamatan.equals("MENTOK")) {
            for (String kodeDes : mentok_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = mentok[pos];
        } else if (namaKecamatan.equals("SIMPANG TERITIP")) {
            for (String kodeDes : simpangteritip_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = simpangteritip[pos];
        } else if (namaKecamatan.equals("JEBUS")) {
            for (String kodeDes : jebus_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = jebus[pos];
        } else if (namaKecamatan.equals("KOBA")) {
            for (String kodeDes : koba_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = koba[pos];
        } else if (namaKecamatan.equals("LUBUK BESAR")) {
            for (String kodeDes : lubukbesar_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = lubukbesar[pos];
        } else if (namaKecamatan.equals("PANGKALAN BARU")) {
            for (String kodeDes : pangkalanbaru_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = pangkalanbaru[pos];
        } else if (namaKecamatan.equals("NAMANG")) {
            for (String kodeDes : namang_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = namang[pos];
        } else if (namaKecamatan.equals("SUNGAI SELAN")) {
            for (String kodeDes : sungaiselan_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = sungaiselan[pos];
        } else if (namaKecamatan.equals("SIMPANG KATIS")) {
            for (String kodeDes : simpangkatis_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = simpangkatis[pos];
        } else if (namaKecamatan.equals("PAYUNG")) {
            for (String kodeDes : payung_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = payung[pos];
        } else if (namaKecamatan.equals("PULAU BESAR")) {
            for (String kodeDes : pulaubesar_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = pulaubesar[pos];
        } else if (namaKecamatan.equals("SIMPANG RIMBA")) {
            for (String kodeDes : simpangrimba_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = simpangrimba[pos];
        } else if (namaKecamatan.equals("TOBOALI")) {
            for (String kodeDes : toboali_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = toboali[pos];
        } else if (namaKecamatan.equals("TUKAK SADAI")) {
            for (String kodeDes : tukaksadai_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = tukaksadai[pos];
        } else if (namaKecamatan.equals("AIR GEGAS")) {
            for (String kodeDes : airgegas_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = airgegas[pos];
        } else if (namaKecamatan.equals("LEPAR PONGOK")) {
            for (String kodeDes : leparpongok_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = leparpongok[pos];
        } else if (namaKecamatan.equals("DENDANG")) {
            for (String kodeDes : dendang_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = dendang[pos];
        } else if (namaKecamatan.equals("SIMPANG PESAK")) {
            for (String kodeDes : simpangpesak_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = simpangpesak[pos];
        } else if (namaKecamatan.equals("GANTUNG")) {
            for (String kodeDes : gantung_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = gantung[pos];
        } else if (namaKecamatan.equals("SIMPANG RENGGIANG")) {
            for (String kodeDes : simpangrenggiang_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = simpangrenggiang[pos];
        } else if (namaKecamatan.equals("MANGGAR")) {
            for (String kodeDes : manggar_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = manggar[pos];
        } else if (namaKecamatan.equals("DAMAR")) {
            for (String kodeDes : damar_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = damar[pos];
        } else if (namaKecamatan.equals("KELAPA KAMPIT")) {
            for (String kodeDes : kelapakampit_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = kelapakampit[pos];
        } else if (namaKecamatan.equals("RANGKUI")) {
            for (String kodeDes : rangkui_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = rangkui[pos];
        } else if (namaKecamatan.equals("BUKIT INTAN")) {
            for (String kodeDes : bukitintan_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = bukitintan[pos];
        } else if (namaKecamatan.equals("PANGKAL BALAM")) {
            for (String kodeDes : pangkalbalam_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = pangkalbalam[pos];
        } else if (namaKecamatan.equals("TAMAN SARI")) {
            for (String kodeDes : tamansari_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = tamansari[pos];
        } else if (namaKecamatan.equals("GERUNGGANG")) {
            for (String kodeDes : gerunggang_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = gerunggang[pos];
        } else if (namaKecamatan.equals("DUMMY_KECAMATAN")) {
            for (String kodeDes : dummykecamatan_kode) {
                if (kodeDesa.equals(kodeDes)) {
                    break;
                }
                pos++;
            }
            namaDesa = dummykecamatan[pos];
        }
        return namaDesa;
    }
}