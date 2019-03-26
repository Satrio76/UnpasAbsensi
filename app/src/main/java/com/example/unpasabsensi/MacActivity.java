package com.example.unpasabsensi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.unpasabsensi.utility.netowrk.GetTokenUnpas;

public class MacActivity extends AppCompatActivity implements View.OnClickListener{
    Intent intentGetUser;
    private EditText editTextMacAddress;
    private String id_user,nomor_induk,userMacBluetooth;
    private static final String TAG = "MacActivity";
    private Button buttonSendMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mac);
        intentGetUser = getIntent();
        editTextMacAddress = findViewById(R.id.mac_input);
        buttonSendMac = findViewById(R.id.buttonSendMacBT);

    }

    private void sendMacBluetooth(){
        if (TextUtils.isEmpty(editTextMacAddress.getText())){
            editTextMacAddress.setError("Mac harus dimasukan");
        }else{
            id_user = intentGetUser.getStringExtra("ID");
            nomor_induk = intentGetUser.getStringExtra("NOMOR_INDUK");
            GetTokenUnpas sendmac = new GetTokenUnpas(this,"Send_Mac");
            sendmac.sendMacBluetooth(id_user,nomor_induk,userMacBluetooth);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSendMacBT:
                Log.e(TAG, "onClick: "+"Keluar " );
                sendMacBluetooth();
                break;
        }
    }

}
