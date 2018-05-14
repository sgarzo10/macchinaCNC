package com.prova.gui.search.utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import com.prova.bluetooth.R;
import com.prova.gui.search.activity.MainActivity;

public class Ricevitore extends BroadcastReceiver {

    private MainActivity app;
    private int trovati;

    public Ricevitore(MainActivity app) {
        this.app=app;
        trovati=0;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            start();
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            finish();
        else if (BluetoothDevice.ACTION_FOUND.equals(action))
            found(intent);
    }

    private void start(){
        app.getMessaggio().setText(R.string.do_search);
        trovati=0;
    }

    private void finish(){
        app.getMessaggio().setText(R.string.search_finish);
        ImageButton b = (ImageButton) app.findViewById(R.id.cerca);
        b.setImageDrawable(ContextCompat.getDrawable(app, R.drawable.lente));
        app.getAscoltatore().setRicerca();
    }

    private void found(Intent intent){
        try {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            app.getAscoltatore().getNomi().add(device.getName());
            app.getAscoltatore().getMac().add(device.getAddress());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(app, android.R.layout.simple_list_item_1, app.getAscoltatore().getNomi());
            app.getNomi().setAdapter(adapter);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(app, android.R.layout.simple_list_item_1, app.getAscoltatore().getMac());
            app.getAddress().setAdapter(adapter1);
            Button pr= new Button(app);
            pr.setId(R.id.connetti);
            pr.setText(R.string.connect);
            pr.setTextSize(10);
            pr.setTag(trovati);
            pr.setOnClickListener(app.getAscoltatore());
            app.getBottoni().addView(pr);
            trovati++;
        }
        catch (Exception e){
            Log.e("EXCEPTION FOUND", e.getMessage());
        }
    }
}
