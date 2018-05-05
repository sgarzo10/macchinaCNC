package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.prova.bluetooth.BluetoothConnection;
import com.prova.bluetooth.R;
import com.prova.gui.device.utility.JoystickView;
import com.prova.gui.device.utility.QuadratoView;

import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity {

    private EditText testo;
    private LinearLayout output;
    private BluetoothConnection bluetooth;
    private String nome;
    private String mac;
    private JoystickView joystickView;
    private QuadratoView quadratoView;
    private Switch rotazione_attiva;

    BluetoothConnection getBluetooth() { return bluetooth;}
    EditText getTesto() { return testo; }
    LinearLayout getOutput() { return output; }
    Switch getRotazione_attiva() {return rotazione_attiva;}
    public QuadratoView getQuadratoView() {return quadratoView;}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        joystickView = new JoystickView(this);
        quadratoView = new QuadratoView(this);
        nome = Objects.requireNonNull(getIntent().getExtras()).getString("nome");
        mac = Objects.requireNonNull(getIntent().getExtras().getString("mac"));
        AscoltatoreConnectionActivity ascoltatore = new AscoltatoreConnectionActivity(this);
        Button invia = (Button) findViewById(R.id.invia);
        ImageButton sali = (ImageButton) findViewById(R.id.sali);
        ImageButton scendi = (ImageButton) findViewById(R.id.scendi);
        LinearLayout linearLayoutJoystick = (LinearLayout) findViewById(R.id.joystick);
        RelativeLayout relativeLayoutQuadrato = (RelativeLayout) findViewById(R.id.quadrato);
        rotazione_attiva = (Switch) findViewById(R.id.rotazione_attiva);
        testo = (EditText) findViewById(R.id.da_inviare);
        output = (LinearLayout) findViewById(R.id.outSeriale);
        linearLayoutJoystick.addView(joystickView);
        relativeLayoutQuadrato.addView(quadratoView);
        invia.setOnClickListener(ascoltatore);
        sali.setOnTouchListener(ascoltatore);
        scendi.setOnTouchListener(ascoltatore);
        rotazione_attiva.setOnCheckedChangeListener(ascoltatore);
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
        if (rotazione_attiva.isChecked())
            rotazione_attiva.setText(getResources().getString(R.string.rot_on));
        else
            rotazione_attiva.setText(getResources().getString(R.string.rot_off));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        joystickView.calcolaDimensioni();
        joystickView.drawJoystick(joystickView.getWidth() / 2, joystickView.getHeight() / 2);
        quadratoView.calcolaDimensioni();
        quadratoView.drawPoint(quadratoView.getWidth() / 2, quadratoView.getHeight() / 2);
        int size = rotazione_attiva.getHeight();
        rotazione_attiva.setTextSize(size/10);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.disconnetti();
        bluetooth.interrupt();
        joystickView.getMp().interrupt();
    }
}
