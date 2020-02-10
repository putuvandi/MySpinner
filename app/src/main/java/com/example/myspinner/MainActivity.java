package com.example.myspinner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myspinner.ApiHelper.BaseApiService;
import com.example.myspinner.ApiHelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    BaseApiService mApiService;
    String stats;

    Spinner spinnerStatus;

    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
        mContext = this;
        mApiService = UtilsApi.getAPIService();

        ambilStatusPegawai("00610");
        initSpinnerStatus();

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
//                requestDetailDosen(selectedName);
                Toast.makeText(mContext, "Kamu memilih status " + selectedName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerStatus(){
        loading = ProgressDialog.show(mContext, null, "harap tunggu...", true, false);

        mApiService.getAllStatusPegawai().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        loading.dismiss();

                        JSONObject jsonResults = new JSONObject(response.body().string());
                        JSONArray result = jsonResults.getJSONArray("result");
                        List<String> str = new ArrayList<String>();
                        for (int i = 0; i<result.length(); i++) {
                            JSONObject jo = result.getJSONObject(i);
                            String status = jo.getString("status_pegawai");
                            str.add(status);
                        }

                        String compareValue = stats;
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                android.R.layout.simple_spinner_item, str);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStatus.setAdapter(adapter);
                        if (compareValue != null) {
                            int spinnerPosition = adapter.getPosition(compareValue);
                            spinnerStatus.setSelection(spinnerPosition);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    loading.dismiss();
                    Toast.makeText(mContext, "Gagal mengambil data dosen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(mContext, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ambilStatusPegawai(String kodepegawai) {
        mApiService.getStatusPegawai(kodepegawai).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResults = new JSONObject(response.body().string());
                        if (jsonResults.getString("error").equals("false")) {
                            stats = jsonResults.getString("status_pegawai");
                        } else {
                            // jika gagal
                            String error_msg = jsonResults.getString("error_msg");
                            Toast.makeText(mContext, error_msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> mCall, Throwable mThrowable) {
                Log.e("debug", "onFailure: ERROR > " + mThrowable.toString());
            }
        });
    }
}
