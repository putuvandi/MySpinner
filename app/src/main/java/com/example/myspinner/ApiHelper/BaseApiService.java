package com.example.myspinner.ApiHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BaseApiService {

    // Fungsi untuk memanggil API http://localhost/sia/TampilAllStatusPegawai.php
    @GET("TampilAllStatusPegawai.php")
    Call<ResponseBody> getAllStatusPegawai();

    // Fungsi untuk memanggil API http://localhost/sia/TampilStatusPegawai.php
    @GET("TampilStatusPegawai.php")
    Call<ResponseBody> getStatusPegawai(@Query("kode_pegawai") String kode_pegawai);
}
