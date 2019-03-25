package com.example.unpasabsensi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasabsensi.absensi.MahasiswaAbsensiActivity;
import com.example.unpasabsensi.absensi.model.User;
import com.example.unpasabsensi.absensi.model.UserMhs;
import com.example.unpasabsensi.utility.controller.DBHandler;
import com.example.unpasabsensi.utility.netowrk.CheckConnection;
import com.example.unpasabsensi.utility.netowrk.GetTokenUnpas;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //KEMUNGKINAN YANG TERJADI PADA SAAT PAGE DI LOAD
    private ConstraintLayout displayLoading,displayFailed,displaySuccess;
    //CONNECTION FAILED
    private CardView cardViewUlangiKoneksi;
    //CONNECTION SUCCESS
    private TextView textViewGelarNama, textViewDeskripsi;
    private TextView textViewGreeting;
    private CardView cardViewPenugasan, cardViewKontrakMK, cardViewForum, cardViewMhsAbsen, cardViewLapDosen;
    private DBHandler dbHandler;
    private String nim, nama, upload_date, status, password;
    private ConstraintLayout mhsAbsenConstraint;
    private String pembeda;
    private Intent intent;
    private String id_user,nomor_induk,userMacBluetooth, mac_user;
    private final String TAG="Main Activity";
    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNomor_induk() {
        return nomor_induk;
    }

    public void setNomor_induk(String nomor_induk) {
        this.nomor_induk = nomor_induk;
    }

    public String getMac_user() {
        return mac_user;
    }

    public void setMac_user(String mac_user) {
        this.mac_user = mac_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        initView();
        initListener();
        tampilanToolbar();
        initRunning();
    }

    //BUTTON ON CLICK
    @Override public void onClick(View view) {
        switch (view.getId()){
            case R.id.MainCardViewUlangiKoneksi:
                initRunning();
                break;
            case R.id.MainCardViewDosenPenugasan:
                initPenugasan();
                break;
            case R.id.MainCardViewMahasiswaKontrakMK:
                initMahasiswaKontrak();
                break;
            case R.id.MainCardViewForum:
                initForum();
                break;
            case R.id.MainCardViewMhsAbsen:
                initMhsAbsen();
                break;

        }
    }

    //INISIASI
    private void initView(){
        //KEMUNGKINAN YANG TERJADI PADA SAAT PAGE DI LOAD
        displayLoading = findViewById(R.id.MainDisplayLoading);
        displayFailed = findViewById(R.id.MainDisplayFailed);
        displaySuccess = findViewById(R.id.MainDisplaySuccess);

        //CONNECTION FAILED
        cardViewUlangiKoneksi = findViewById(R.id.MainCardViewUlangiKoneksi);

        //CONNECTION SUCCESS
        textViewGelarNama = findViewById(R.id.MainTvGelarNama);
        cardViewPenugasan = findViewById(R.id.MainCardViewDosenPenugasan);
        cardViewKontrakMK = findViewById(R.id.MainCardViewMahasiswaKontrakMK);
        cardViewForum = findViewById(R.id.MainCardViewForum);
        cardViewMhsAbsen = findViewById(R.id.MainCardViewMhsAbsen);
        cardViewLapDosen = findViewById(R.id.MainCardViewLapDosen);
        mhsAbsenConstraint = findViewById(R.id.mhsAbsenConstraint);
        textViewDeskripsi = findViewById(R.id.textViewDeskripsi);
        textViewGreeting = findViewById(R.id.textViewMhsAbsenGreeting);

    }

    private void initListener(){
        //CONNECTION FAILED
        cardViewUlangiKoneksi.setOnClickListener(MainActivity.this);

        //CONNECTION SUCCESS
        dbHandler = new DBHandler(MainActivity.this);
        cardViewPenugasan.setOnClickListener(MainActivity.this);
        cardViewKontrakMK.setOnClickListener(MainActivity.this);
        cardViewForum.setOnClickListener(MainActivity.this);
        cardViewMhsAbsen.setOnClickListener(MainActivity.this);
    }
//aaa
    //KEMUNGKINAN YANG TERJADI PADA SAAT PAGE DI LOAD
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

    //TOOLBAR
    private void tampilanToolbar() {
        Toolbar mainToolbar = findViewById(R.id.MainToolbar);
        setSupportActionBar(mainToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem hideItem = menu.findItem(R.id.action_search);
        hideItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mainLogout:
                alert_logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void alert_logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.todoDialogLight);
        builder.setIcon(R.drawable.icon_info)
                .setTitle("Putuskan akses ke sistem?")
                .setMessage("Anda perlu memasukan kembali Username dan Password \nHalaman Login akan ditampilkan")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        keHalamanLogin();
                    }
                })
                .setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button yes = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        yes.setTextColor(Color.rgb(29,145,36));
    }
    public void keHalamanLogin() {
        dbHandler.deleteAll();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //APLIKASI BERJALAN
    public void initRunning() {
        displayLoading();
        ambilUserDiDatabase();
        if (nim == null){
            keHalamanLogin();
        } else if (textViewGelarNama.equals("Gelar Nama")){
            keHalamanLogin();
        }

        //CEK KONEKSI INTERNET
        else if (!new CheckConnection().apakahTerkoneksiKeInternet(MainActivity.this)){
            Toast.makeText(getApplicationContext(),"Tidak ada koneksi Internet",Toast.LENGTH_SHORT).show();
            displayFailed();
        }else{
            //APAKAH USER ADA PADA DATABASE
            ambilUserDiDatabase();
            if (nim.equals("")){
                keHalamanLogin();
            }else{
                GetTokenUnpas token = new GetTokenUnpas(MainActivity.this, "Match_data");
                synchronized (MainActivity.this){
                    token.getToken(
                            nim,
                            password.trim()
                    );
                }
            }
        }
    }

    public void matchData(){
        Log.e(TAG,nim.substring(0,1));
        if(nim.substring(0,1).equals("9")){
            pembeda="Dosen";
        }else{
            pembeda="Mahasiswa";
        }

        //Jika mac kosong
        if(!mac_user.equals("")){
            switch (pembeda) {
                case "Mahasiswa":
                    intent = getIntent();
                    //textViewGreeting.setText(macbluetooth);
                    cardViewLapDosen.setVisibility(View.GONE);
                    cardViewKontrakMK.setVisibility(View.VISIBLE);
                    cardViewPenugasan.setVisibility(View.GONE);
                    mhsAbsenConstraint.setVisibility(View.VISIBLE);
                    textViewDeskripsi.setVisibility(View.GONE);
                    break;
                case "Dosen":
                    cardViewLapDosen.setVisibility(View.VISIBLE);
                    cardViewKontrakMK.setVisibility(View.GONE);
                    cardViewPenugasan.setVisibility(View.GONE);
                    mhsAbsenConstraint.setVisibility(View.GONE);
                    textViewDeskripsi.setVisibility(View.VISIBLE);
                    textViewDeskripsi.setText("Dosen");
                    break;
                default:
                    keHalamanLogin();
                    break;
            }
            textViewGelarNama.setText(nama);
            textViewGelarNama.setAllCaps(false);
            displaySuccess();
        }else{
            userMacBluetooth = "";
            View subview = getLayoutInflater().inflate(R.layout.dialog_layout,null);
            final EditText editTextMacBluetooth = subview.findViewById(R.id.userMacAddress);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.todoDialogLight);
            builder.setIcon(R.drawable.ic_info)
                    .setTitle("Keamanan")
                    .setMessage("Mac Address Bluetooth Anda")
                    .setView(subview)
                    .setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (editTextMacBluetooth.getText().toString().equals("")){
                                Toast.makeText(MainActivity.this, "Masukan Mac Address Bluetooth", Toast.LENGTH_SHORT).show();
                            }else{
                                userMacBluetooth=editTextMacBluetooth.getText().toString();
                                Log.e("DATA USER",id_user+" "+nomor_induk+" "+userMacBluetooth);
                                displayLoading();
                                GetTokenUnpas sendmac = new GetTokenUnpas(MainActivity.this,"Send_Mac");
                                sendmac.sendMacBluetooth(id_user,nomor_induk,userMacBluetooth);
                            }
                        }
                    })
                    .setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {keHalamanLogin();}
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            Button yes = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            yes.setTextColor(Color.rgb(29,145,36));
        }
    }

    private void ambilUserDiDatabase() {
        try {
            List<UserMhs> listUser = dbHandler.getAllUser();
            for (UserMhs user : listUser){
                nim = user.getNim();
                password = user.getPassword();
                nama= user.getNama();
                upload_date = user.getUpload_date();
                status = user.getStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbHandler.close();
    }

    //INIT PENUGASAN
    private void initPenugasan() {
//        Intent intentPenugasan = new Intent(MainActivity.this,PenugasanActivity.class);
//        intentPenugasan.putExtra("KODEDOSEN",kodedosen);
//        startActivity(intentPenugasan);
    }

    //INIT MAHASISWA KONTRAK
    private void initMahasiswaKontrak() {
//        Intent intentMhsKontrak = new Intent(MainActivity.this,MahasiswaKontrakActivity.class);
//        startActivity(intentMhsKontrak);
    }

    //INIT FORUM
    private void initForum() {
//        if (status.equals("Dosen")){
//            Intent intentForum = new Intent(MainActivity.this, ForumActivity.class);
//            intentForum.putExtra("KODEDOSEN",kodedosen);
//            intentForum.putExtra("STATUS", status);
//            startActivity(intentForum);
//        } else if (status.equals("Mahasiswa")){
//            Intent intentForumMhs = new Intent(MainActivity.this, ForumMhsActivity.class);
//            intentForumMhs.putExtra("KODEDOSEN",kodedosen);
//            intentForumMhs.putExtra("STATUS", status);
//            startActivity(intentForumMhs);
//        }
    }

    //INIT ABSEN
    private void initMhsAbsen() {
        Intent intenMhsAbsen = new Intent(MainActivity.this, MahasiswaAbsensiActivity.class);
        intenMhsAbsen.putExtra("NIM", nim);
        intenMhsAbsen.putExtra("NAMA", nama);
        startActivity(intenMhsAbsen);
        displayLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displaySuccess();
    }

}
