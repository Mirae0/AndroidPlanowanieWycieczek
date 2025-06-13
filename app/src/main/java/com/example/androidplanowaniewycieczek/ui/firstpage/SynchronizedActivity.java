package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.database.DBHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SynchronizedActivity extends AppCompatActivity {

    private BluetoothSocket socket;
    Dialog dialog;
    DBHandler db  = new DBHandler();

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        //Jeśli aplikacja nie ma uprawnień do bt to crashuje

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    return;
                }
                return;
            }
            startActivity(enableBtIntent);
        }

        setContentView(R.layout.activity_synchronized);

        ListView listView = findViewById(R.id.connectedBT);
        Set<BluetoothDevice> dev = bluetoothAdapter.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<BluetoothDevice> list2 = new ArrayList<>(dev);
        for (BluetoothDevice d: dev
             ) {
            list.add(d.getName());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.listview_item,R.id.item_text,list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bt = list2.get(position);
                System.out.println(list.get(position));

                choose();

                if (ActivityCompat.checkSelfPermission(parent.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                ParcelUuid[] uuidP = bt.getUuids();
                UUID uuid = UUID.fromString(uuidP[0].toString());
                try{
                    socket = bt.createRfcommSocketToServiceRecord(uuid);
                    socket.connect();

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        ImageView quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(SynchronizedActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });
    }

    protected void choose(){
        ArrayList<String> arr = new ArrayList<>();
        arr = db.getFutureTripsNames();
        Spinner spinner = findViewById(R.id.spinner_synch);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arr);
        spinner.setAdapter(adapter1);


        dialog = new Dialog(SynchronizedActivity.this);
        dialog.setContentView(R.layout.alert_choose_plan_synch);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();
    }

}