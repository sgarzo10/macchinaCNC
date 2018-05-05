package com.prova.gui.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.prova.bluetooth.BluetoothConnection;
import com.prova.bluetooth.R;

public class ConnectionActivity extends AppCompatActivity {

    private EditText testo;
    private LinearLayout output;
    private BluetoothConnection bluetooth;
    private String nome;
    private String mac;
    private JoystickView joystickView;

    BluetoothConnection getBluetooth() { return bluetooth;}
    EditText getTesto() { return testo; }
    LinearLayout getOutput() { return output; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        joystickView = new JoystickView(this);
        nome = getIntent().getExtras().getString("nome");
        mac = getIntent().getExtras().getString("mac");
        AscoltatoreConnectionActivity ascoltatore = new AscoltatoreConnectionActivity(this);
        Button invia = (Button) findViewById(R.id.invia);
        LinearLayout linearLayoutJoystick = (LinearLayout) findViewById(R.id.joystick);
        View v = findViewById(R.id.joystick);
        testo = (EditText) findViewById(R.id.da_inviare);
        output = (LinearLayout) findViewById(R.id.outSeriale);
        invia.setOnClickListener(ascoltatore);
        linearLayoutJoystick.addView(joystickView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
       TextView t = new TextView(this);
        boolean connesso = false;
        bluetooth = new BluetoothConnection();
        while (!connesso) {
            if (bluetooth.connetti(mac)) {
                t.setText(String.format("%s %s", getResources().getString(R.string.connected), nome));
                output.addView(t);
                connesso = true;
                bluetooth.start();
            } else {
                t.setText(R.string.error);
                output.addView(t);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        joystickView.drawJoystick(joystickView.getWidth() / 2, joystickView.getHeight() / 2);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        while (!bluetooth.disconnetti()) {}
        bluetooth.interrupt();
    }
}
