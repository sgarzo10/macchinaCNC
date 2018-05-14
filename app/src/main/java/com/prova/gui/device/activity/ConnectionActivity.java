package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
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
import com.prova.gui.device.utility.MovePoint;
import com.prova.gui.device.utility.MyHandler;
import com.prova.gui.device.utility.QuadratoView;

import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity {

    private EditText testo;
    private TextView posizioni;
    private TextView textLunghezze;
    private LinearLayout output;
    private BluetoothConnection bluetooth;
    private JoystickView joystickView;
    private QuadratoView quadratoView;
    private MovePoint mp;
    private Switch rotazione_attiva;
    private AscoltatoreConnectionActivity ascoltatore;
    private boolean initLunghezze;
    private boolean initPosizioni;

    public BluetoothConnection getBluetooth() { return bluetooth;}
    EditText getTesto() { return testo; }
    Switch getRotazione_attiva() {return rotazione_attiva;}
    public QuadratoView getQuadratoView() {return quadratoView;}
    public AscoltatoreConnectionActivity getAscoltatore() { return ascoltatore; }
    public TextView getPosizioni() { return posizioni; }
    public TextView getTextLunghezze() { return textLunghezze; }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        joystickView = new JoystickView(this);
        quadratoView = new QuadratoView(this);
        String nome = Objects.requireNonNull(getIntent().getExtras()).getString("nome");
        String mac = Objects.requireNonNull(getIntent().getExtras().getString("mac"));
        ascoltatore = new AscoltatoreConnectionActivity(this);
        Button invia = (Button) findViewById(R.id.invia);
        Button dove = (Button) findViewById(R.id.dove);
        Button seriale = (Button) findViewById(R.id.seriale);
        Button lunghezze = (Button) findViewById(R.id.lunghezze);
        ImageButton sali = (ImageButton) findViewById(R.id.sali);
        ImageButton scendi = (ImageButton) findViewById(R.id.scendi);
        LinearLayout linearLayoutJoystick = (LinearLayout) findViewById(R.id.joystick);
        RelativeLayout relativeLayoutQuadrato = (RelativeLayout) findViewById(R.id.quadrato);
        rotazione_attiva = (Switch) findViewById(R.id.rotazione_attiva);
        testo = (EditText) findViewById(R.id.da_inviare);
        output = (LinearLayout) findViewById(R.id.outSeriale);
        posizioni = (TextView) findViewById(R.id.posizioni);
        textLunghezze = (TextView) findViewById(R.id.text_lunghezze);
        linearLayoutJoystick.addView(joystickView);
        relativeLayoutQuadrato.addView(quadratoView);
        invia.setOnClickListener(ascoltatore);
        dove.setOnClickListener(ascoltatore);
        lunghezze.setOnClickListener(ascoltatore);
        seriale.setOnClickListener(ascoltatore);
        sali.setOnTouchListener(ascoltatore);
        scendi.setOnTouchListener(ascoltatore);
        rotazione_attiva.setOnCheckedChangeListener(ascoltatore);
        mp = new MovePoint(this, joystickView);
        mp.start();
        TextView t = new TextView(this);
        boolean connesso = false;
        bluetooth = new BluetoothConnection(new MyHandler(this));
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
        initLunghezze = false;
        initPosizioni = false;
        ascoltatore.inviaMessaggio("ssb");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
        rotazione_attiva.setTextSize(rotazione_attiva.getHeight() / 10);
        posizioni.setTextSize(posizioni.getHeight() / 4);
        textLunghezze.setTextSize(textLunghezze.getHeight() / 4);
        getValoriInziaili();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.disconnetti();
        bluetooth.interrupt();
        mp.interrupt();
    }

    public void addView(String s){
        TextView t = new TextView(this);
        t.setText(s);
        output.addView(t);
    }

    private void getValoriInziaili(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!bluetooth.getH().isFine())
                    getValoriInziaili();
                else {
                    if (!initLunghezze) {
                        ascoltatore.inviaMessaggio("la");
                        initLunghezze = true;
                        bluetooth.getH().setFine(false);
                        getValoriInziaili();
                    }
                    else {
                        if (!initPosizioni) {
                            ascoltatore.inviaMessaggio("da");
                            initPosizioni = true;
                            bluetooth.getH().setFine(false);
                            getValoriInziaili();
                        }
                        else
                            ascoltatore.getMessaggi().clear();
                    }
                }
            }
        }, 100);
    }
}
