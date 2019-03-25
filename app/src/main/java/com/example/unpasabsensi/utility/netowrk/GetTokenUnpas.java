package com.example.unpasabsensi.utility.netowrk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unpasabsensi.LoginActivity;
import com.example.unpasabsensi.MainActivity;
import com.example.unpasabsensi.absensi.model.UserMhs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class GetTokenUnpas {
    private String error;
    private String bagian;
    private Context context;
    private String message;

    public GetTokenUnpas(Context context, String bagian){
        this.context = context;
        this.bagian = bagian;
    }

    //AMBIL DATA USER
    public void getToken (final String username, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlUnpas.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (bagian){
                                case "Login":
                                    if(jsonObject.optString("error").equals("true")){
                                        String tidakadadata = jsonObject.getString("message");
                                        Log.e("YARUD", tidakadadata);
                                        LoginActivity loginActivity = (LoginActivity) context;
                                        loginActivity.displaySuccess();
                                        Toast.makeText(context, tidakadadata, Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onResponse: Error");
                                    }else if(jsonObject.optString("error").equals("false")){
                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
                                        UserMhs datamhs = new UserMhs();
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject datajson = jsonArray.getJSONObject(i);
                                            datamhs.setNim(datajson.getString("nomor_induk"));
                                            datamhs.setNama(datajson.getString("nama"));
                                            datamhs.setStatus(datajson.getString("status"));
                                            Log.e("NAMA",datamhs.getNama());
                                            Log.e("STATUS",datamhs.getStatus());
//                                            Toast.makeText(context, jsonArray.toString(), Toast.LENGTH_SHORT).show();

                                        }
                                        Log.e(TAG, "onResponse: SUCCESS");
                                        LoginActivity loginActivity = (LoginActivity) context;
                                        loginActivity.setUserToDatabase(datamhs.getNama(),datamhs.getStatus(),datamhs.getUpload_date());
                                        loginActivity.RunningPage();
                                    }
                                    break;
                                case "Match_data":
                                    if(jsonObject.optString("error").equals("true")){
                                        String tidakadadata = jsonObject.getString("message");
                                        MainActivity mainActivity = (MainActivity) context;
                                        mainActivity.keHalamanLogin();
                                    }else if(jsonObject.optString("error").equals("false")){
                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
                                        UserMhs datamhs = new UserMhs();
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject datajson = jsonArray.getJSONObject(i);
                                            datamhs.setId(datajson.getInt("id"));
                                            datamhs.setNim(datajson.getString("nomor_induk"));
                                            datamhs.setNama(datajson.getString("nama"));
                                            datamhs.setStatus(datajson.getString("status"));
                                            datamhs.setMac_user(datajson.getString("mac_user"));
                                        }
                                        MainActivity mainActivity = (MainActivity) context;
                                        mainActivity.setId_user(String.valueOf(datamhs.getId()));
                                        mainActivity.setNomor_induk(datamhs.getNim());
                                        mainActivity.setMac_user(datamhs.getMac_user());
                                        mainActivity.matchData();
                                    }
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (bagian) {
                            case "Login": {
                                LoginActivity activity = (LoginActivity) context;
                                activity.displaySuccess();
                                Toast.makeText(context, "Username Password anda salah / Tidak ada Koneksi Internet", Toast.LENGTH_LONG).show();
                                Log.e(TAG, "onErrorResponse: ", error);
                                break;
                            }
                            case "Penugasan": {
//                                PenugasanActivity activity = (PenugasanActivity) context;
//                                activity.displayFailed();
                                break;
                            }
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("nomor_induk", username);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //KIRIM DATA MAC BLUETOOTH
    public void sendMacBluetooth (final String id_user, final String nomor_induk, final String mac_user){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlUnpas.URL_MAC_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (bagian){
                                case "Send_Mac":
                                    if(jsonObject.optString("error").equals("true")){
                                        String tidakadadata = jsonObject.getString("message");
                                        Log.e("SEND MAC", tidakadadata);
                                        MainActivity mainActivity = (MainActivity) context;
                                        mainActivity.displaySuccess();
                                        mainActivity.matchData();
                                        Toast.makeText(context, tidakadadata, Toast.LENGTH_SHORT).show();
                                    }else if(jsonObject.optString("error").equals("false")){
//                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
//                                        UserMhs datamhs = new UserMhs();
//                                        for (int i=0; i < jsonArray.length(); i++){
//                                            JSONObject datajson = jsonArray.getJSONObject(i);
//                                            datamhs.setNim(datajson.getString("nomor_induk"));
//                                            datamhs.setNama(datajson.getString("nama"));
//                                            datamhs.setStatus(datajson.getString("status"));
//                                            Log.e("NAMA",datamhs.getNama());
//                                            Log.e("STATUS",datamhs.getStatus());
//                                            Toast.makeText(context, jsonArray.toString(), Toast.LENGTH_SHORT).show();
//
//                                        }
                                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        MainActivity mainActivity = (MainActivity) context;
                                        mainActivity.displaySuccess();
                                        mainActivity.initRunning();
                                    }
                                    break;
                                case "Check_Mac":
//                                    if(jsonObject.optString("error").equals("true")){
//                                        String tidakadadata = jsonObject.getString("message");
//                                        Log.e("YARUD", tidakadadata);
//                                        MainActivity mainActivity = (MainActivity) context;
//                                        mainActivity.keHalamanLogin();
//                                    }else if(jsonObject.optString("error").equals("false")){
//                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
//                                        UserMhs datamhs = new UserMhs();
//                                        for (int i=0; i < jsonArray.length(); i++){
//                                            JSONObject datajson = jsonArray.getJSONObject(i);
//                                            datamhs.setNim(datajson.getString("nomor_induk"));
//                                            datamhs.setNama(datajson.getString("nama"));
//                                            datamhs.setStatus(datajson.getString("status"));
//                                            datamhs.setMac_user(datajson.getString("mac_user"));
//                                            Log.e("NAMA",datamhs.getNama());
//                                            Log.e("STATUS",datamhs.getStatus());
//                                            Toast.makeText(context, jsonArray.toString(), Toast.LENGTH_SHORT).show();
//                                        }
//                                        MainActivity mainActivity = (MainActivity) context;
//                                        mainActivity.matchData(datamhs.getId(),datamhs.getNim(),datamhs.getMac_user());
//                                    }
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (bagian) {
                            case "Login": {
                                LoginActivity activity = (LoginActivity) context;
                                activity.displaySuccess();
                                Toast.makeText(context, "Username Password anda salah / Tidak ada Koneksi Internet", Toast.LENGTH_LONG).show();
                                break;
                            }
                            case "Penugasan": {
//                                PenugasanActivity activity = (PenugasanActivity) context;
//                                activity.displayFailed();
                                break;
                            }
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("id_user", id_user);
                params.put("nomor_induk", nomor_induk);
                params.put("mac_user", mac_user);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
