# CAPI 62 Riset Spesial

```
By Tim CAPI PKL 62
31 Januari 2023
```

## Instalasi Projek

1. Clone repository ini ke folder kesayangan Anda `git clone https://git.stis.ac.id/fosil-spd-pkl/62/capi-62-riset-3.git`
2. Buat dan export database yang dibutuhkan, yaitu `pkl62`, `pkl62_monitoring`, `pkl62_sikoko`, dan `pkl62_wilayah_riset3`
3. Clone juga repository `Web Service 62` ke dalam folder htdocs atau folder lainnya yang sesuai dengan konfigurasi web server anda
4. Uji `Web Service 62` dengan menjalankan `http://ALAMAT_IP_ANDA/web-service-62`. `ALAMAT_IP ANDA` disesuaikan dengan alamat IP komputer Anda. Cek ip dengan perintah `ipconfig` di command prompt.
5. Buka projek `Capi 62 Riset 3` dengan Android Studio
6. Replace semua alamat `https://capi62.stis.ac.id/web-service-62` dengan `http://ALAMAT_IP_ANDA/web-service-62` untuk menjalankan Projek dengan server lokal Anda. <br>**Tips : Gunakan CTRL+Shift+R untuk replace sekaligus**
7. Jalankan projek

## Build ke APK

1. Buka projek `Capi 62 Riset 3` dengan Android Studio
2. Pilih menu `Build->Build Bundle(s) / APK(s)-> Build APK(s)`
3. Tunggu hingga file `.apk` berhasil dibuild
