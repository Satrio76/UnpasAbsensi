package com.example.unpasabsensi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasabsensi.absensi.model.UserMhs;
import com.example.unpasabsensi.utility.controller.DBHandler;
import com.example.unpasabsensi.utility.netowrk.CheckConnection;
import com.example.unpasabsensi.utility.netowrk.GetTokenUnpas;
import com.example.unpasabsensi.utility.netowrk.UrlUnpas;

import net.vidageek.mirror.dsl.Mirror;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ConstraintLayout displayLoading,displayFailed,displaySuccess;
    private String nim, nama, upload_date, status;
    private AppCompatEditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private Intent intenMain,intent;
    private DBHandler db;
    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initView();
        initListener();
        initRunning();
    }

    private void initView() {
        //KEMUNGKINAN YANG TERJADI PADA SAAT PAGE DI LOAD
        displayLoading = findViewById(R.id.LoginDisplayLoading);
        displayFailed = findViewById(R.id.LoginDisplayFailed);
        displaySuccess = findViewById(R.id.LoginDisplaySuccess);

        editTextUsername = findViewById(R.id.LoginEditTextUsername);
        editTextPassword = findViewById(R.id.LoginEditTextPassword);
        buttonLogin = findViewById(R.id.LoginButton);

    }

    private void initListener() {
        intenMain = new Intent(LoginActivity.this,MainActivity.class);
        db = new DBHandler(LoginActivity.this);
        buttonLogin.setOnClickListener(LoginActivity.this);
    }

    private void initRunning() {
//        if (!new CheckConnection().apakahTerkoneksiKeInternet(LoginActivity.this)){
//            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.LoginButton:
                validationForm();

//                String macAddress = android.provider.Settings.Secure.getString(this.getContentResolver(), "bluetooth_address");
//                Log.e("MAC BLUETOOTH ",getWifiMacAddress());
                //Log.e("MAC BLUETOOTH ",getBtAddressViaReflection());
                break;
        }
    }

    private void validationForm() {
        if (TextUtils.isEmpty(editTextUsername.getText())){
            editTextUsername.setError("Username harus dimasukan");
        } else if (TextUtils.isEmpty(editTextPassword.getText())){
            editTextPassword.setError("Password harus dimasukan");
        } else {
            displayLoading();

            GetTokenUnpas token = new GetTokenUnpas(LoginActivity.this, "Login");
            synchronized (LoginActivity.this){
                token.getToken(
                        Objects.requireNonNull(editTextUsername.getText()).toString().trim(),
                        Objects.requireNonNull(editTextPassword.getText()).toString().trim()
                );
            }
        }
    }

    public void displayLoading(){
        displayLoading.setVisibility(View.VISIBLE);
        displayFailed.setVisibility(View.GONE);
        displaySuccess.setVisibility(View.GONE);
    }
    public void displaySuccess(){
        displayLoading.setVisibility(View.GONE);
        displayFailed.setVisibility(View.GONE);
        displaySuccess.setVisibility(View.VISIBLE);
    }
    public void displayFailed() {
        displayLoading.setVisibility(View.GONE);
        displayFailed.setVisibility(View.VISIBLE);
        displaySuccess.setVisibility(View.GONE);
    }

    public void RunningPage() {
        intenMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intenMain);
        finish();
//        new UrlUnpas();
//        ApiAuthenticationClientJWT apiAuthenticationClientJWT = new ApiAuthenticationClientJWT(UrlUnpas.URL_LOGIN,token);
//        AsyncTask<Void,Void,String> execute = new AmbilData(apiAuthenticationClientJWT);
//        execute.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void setUserToDatabase(String nama, String status, String upload_date){
        Log.e("LOGGING",nama+" "+status+" "+upload_date);

        displaySuccess();
        try {
                db.addUser(
                        new UserMhs(1,
                                editTextUsername.getText().toString(),
                                editTextPassword.getText().toString(),
                                status,
                                upload_date,
                                nama
                        )
                );
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }



//    @SuppressLint("StaticFieldLeak")
//    public class AmbilData extends AsyncTask<Void, Void, String> {
//
//        private ApiAuthenticationClientJWT apiAuthenticationClientJWT;
//
//        AmbilData(ApiAuthenticationClientJWT apiAuthenticationClientJWT) {
//            this.apiAuthenticationClientJWT = apiAuthenticationClientJWT;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            try {
//                apiAuthenticationClientJWT.execute();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            displaySuccess();
//            try {
//                JSONObject jsonObject = new JSONObject((apiAuthenticationClientJWT.getLastResponseAsJsonObject()).getJSONObject("dt_user").toString());
//                User user = new User(db.getUser(1));
//                if (user.getUsername().equals("")){
//                    db.addUser(
//                            new User(1,
//                                    editTextUsername.getText().toString(),
//                                    editTextPassword.getText().toString(),
//                                    jsonObject.getString("STATUS"),
//                                    jsonObject.getString("KODEDOSEN"),
//                                    jsonObject.getString("GELARNAMA")
//                            )
//                    );
//                } else {
//                    db.updateUser(
//                            new User(1,
//                                    editTextUsername.getText().toString(),
//                                    editTextPassword.getText().toString(),
//                                    jsonObject.getString("STATUS"),
//                                    jsonObject.getString("KODEDOSEN"),
//                                    jsonObject.getString("GELARNAMA")
//                            )
//                    );
//                }
//                db.close();
//                startActivity(intenMain);
//                finish();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
