package com.example.unpasabsensi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.unpasabsensi.utility.netowrk.GetTokenUnpas;

public class MacActivity extends AppCompatActivity implements View.OnClickListener{
    Intent intentGetUser;
    private EditText editTextMacAddress;
    private String id_user,nomor_induk,userMacBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mac);
        intentGetUser = getIntent();
        editTextMacAddress = findViewById(R.id.mac_input);
    }

    private void sendMacBluetooth(){
        if (TextUtils.isEmpty(editTextMacAddress.getText())){
            editTextMacAddress.setError("Username harus dimasukan");
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
                sendMacBluetooth();
                break;
        }
    }

}
