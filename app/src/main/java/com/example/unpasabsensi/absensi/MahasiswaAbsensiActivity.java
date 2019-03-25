package com.example.unpasabsensi.absensi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasabsensi.LoginActivity;
import com.example.unpasabsensi.R;
import com.example.unpasabsensi.utility.controller.AESUtil;
import com.example.unpasabsensi.utility.controller.DBHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MahasiswaAbsensiActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GetAddressActivity";
    //KEMUNGKINAN YANG TERJADI PADA SAAT PAGE DI LOAD
    private ConstraintLayout displayLoading,displayFailed,displaySuccess;
    //CONNECTION FAILED
    private CardView cardViewUlangiKoneksi;
    //CONNECTION SUCCESS
    private DBHandler dbHandler;
    private final static int QrWidth = 500;
    private final static int QrHeight = 500;
    private ImageView imageViewMhsAbsenQRImage;
    private String nim, nama;
    private Button buttonMhsAbsenStatus;
    private SimpleDateFormat dtf;
    private ProgressBar progressBar2;
    private CountDownTimer countd;
    private final int statusDiscover=1;
    private BluetoothAdapter bluetoothAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa_absen);
        initView();
        initListener();
        tampilanToolbar();
        initRunning();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pengaktifanBluetooth();
        btnEnableDisable_Discoverable();

        countd = new CountDownTimer(11000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(String.valueOf(millisUntilFinished/1000).equals("0")){
                    buttonMhsAbsenStatus.setText("Memperbaharui");
                }else{
                    buttonMhsAbsenStatus.setText(String.valueOf(millisUntilFinished/1000));
                }
            }

            @Override
            public void onFinish() {
                progressBar2.setVisibility(View.VISIBLE);
                imageViewMhsAbsenQRImage.setVisibility(View.GONE);
                AsyncTask<Void,Void,String> execute = new AmbilData();
                execute.execute();

            }
        };
        buttonMhsAbsenStatus.setVisibility(View.VISIBLE);
        countd.start();
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    private void enabledisableBT(){
        if(!bluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(bluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            bluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    public void btnEnableDisable_Discoverable() {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        }
        if(!bluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        }
    }

    private void nonaktifkanBluetooth(){
        if(bluetoothAdapter.enable()){

        }else{
            Toast.makeText(this, "BLUETOOTH SUDAN OFF", Toast.LENGTH_SHORT).show();
        }

        if(bluetoothAdapter.isDiscovering()){
            Toast.makeText(this, "BLUETOOTH SUDAH DINONAKTIFKAN", Toast.LENGTH_SHORT).show();
        }
    }

    private void pengaktifanBluetooth(){
        if(!bluetoothAdapter.enable()){

        }else{
            Toast.makeText(this, "BLUETOOTH SUDAN ON", Toast.LENGTH_SHORT).show();
        }

        Log.e("DISCOVER", "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

//        if(!bluetoothAdapter.isDiscovering()) {
//            checkBTPermissions();
//            Log.e("DISCOVER", "BLUETOOTH DISCOVER");
//            bluetoothAdapter.startDiscovery();
//        }
//        if(bluetoothAdapter.isDiscovering()){
//
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d("CAKUPAN", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()){
            case R.id.MhsAbsenCardViewUlangiKoneksi:
                initRunning();
                break;
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

    private void initView(){
        //KEMUNGKINAN YANG TERJADI PADA SAAT PAGE DI LOAD
        displayLoading = findViewById(R.id.MhsAbsenDisplayLoading);
        displayFailed = findViewById(R.id.MhsAbsenDisplayFailed);
        displaySuccess = findViewById(R.id.MhsAbsenDisplaySuccess);

        //CONNECTION FAILED
        cardViewUlangiKoneksi = findViewById(R.id.MhsAbsenCardViewUlangiKoneksi);

        //CONNECTION SUCCESS
        imageViewMhsAbsenQRImage = findViewById(R.id.imageViewMhsAbsenQRImage);
        buttonMhsAbsenStatus = findViewById(R.id.buttonMhsAbsenStatus);
        progressBar2 = findViewById(R.id.progressBar2);
//        textView8 = findViewById(R.id.textView8);
    }

    @SuppressLint("SimpleDateFormat")
    private void initListener(){
        //CONNECTION FAILED
        cardViewUlangiKoneksi.setOnClickListener(this);

        //CONNECTION SUCCESS
        nim = Objects.requireNonNull(getIntent().getExtras()).getString("NIM");
        nama = getIntent().getExtras().getString("NAMA");
        dbHandler = new DBHandler(this);
        dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        buttonMhsAbsenStatus.setOnClickListener(this);
        buttonMhsAbsenStatus.setVisibility(View.GONE);
    }

    //TOOLBAR
    private void tampilanToolbar() {
        Toolbar toolbar= findViewById(R.id.MhsAbsenToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Unpas Absensi");
        toolbar.setSubtitle("Absen Mahasiswa");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icon_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.todoDialogLight);
        builder.setIcon(R.drawable.icon_info)
                .setTitle("Putuskan akses ke sistem?")
                .setMessage("Anda perlu memasukan kembali Username dan Password \nHalaman Login akan ditampilkan")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nonaktifkanBluetooth();
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

    private void keHalamanLogin() {
        nonaktifkanBluetooth();
        dbHandler.deleteAll();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //APLIKASI BERJALAN
    @SuppressLint("SetTextI18n")
    private void initRunning() {

        //buttonMhsAbsenStatus.setVisibility(View.GONE);
        Calendar datetimeKalender = Calendar.getInstance();
        Date date= datetimeKalender.getTime();
        String dateformat = dtf.format(date);
//        int i = nama.indexOf(' ');
//        String namaDepan = nama.substring(0,i);
        String sourceStr = nim;
        try {
            String encrypted = AESUtil.encrypt(sourceStr);
            imageViewMhsAbsenQRImage.setImageBitmap(TextToImageEncode(encrypted));
            displaySuccess();

            imageViewMhsAbsenQRImage.setVisibility(View.VISIBLE);
//            new CountDownTimer(60000, 1000) {
//                public void onTick(long millisUntilFinished) {
//                    textView8.setText("Masa Berlaku " + millisUntilFinished / 1000);
//                }
//                public void onFinish() {

//                    textView8.setText("");
//                }
//            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value, BarcodeFormat.QR_CODE,QrWidth,QrHeight,null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        //aa
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y=0; y<bitMatrixHeight; y++){
            int offset = y * bitMatrixWidth;
            for (int x=0; x<bitMatrixWidth; x++){
                pixels[offset + x] = bitMatrix.get(x,y) ?
                        getResources().getColor(R.color.colorPrimaryDark):getResources().getColor(R.color.colorAccent);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels,0,500,0,0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public class AmbilData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                initRunning();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar2.setVisibility(View.GONE);
            imageViewMhsAbsenQRImage.setVisibility(View.VISIBLE);
            countd.start();
        }
    }


}
