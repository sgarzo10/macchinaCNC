package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.prova.bluetooth.BluetoothConnection;
import com.prova.bluetooth.R;
import com.prova.gui.device.view.JoystickView;
import com.prova.gui.device.utility.MovePoint;
import com.prova.gui.device.utility.MyHandler;
import com.prova.gui.device.view.QuadratoView;
import java.util.ArrayList;
import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity {

    private EditText testo;
    private ArrayList<EditText> input;
    private TextView posizioni;
    private TextView textLunghezze;
    private LinearLayout output;
    private BluetoothConnection bluetooth;
    private JoystickView joystickView;
    private QuadratoView quadratoView;
    private MovePoint mp;
    private Switch rotazione_attiva;
    private AscoltatoreConnectionActivity ascoltatore;
    private ArrayList<AlertDialog> dialog;
    private boolean initLunghezze;
    private boolean initPosizioni;
    private boolean primo;
    private boolean resume;

    public BluetoothConnection getBluetooth() { return bluetooth;}
    public boolean isResume() { return resume; }
    public void setResume(boolean resume) {this.resume = resume;}
    EditText getTesto() { return testo; }
    ArrayList<EditText> getInput() { return input; }
    ArrayList<AlertDialog> getDialog() {return dialog;}
    Switch getRotazione_attiva() {return rotazione_attiva;}
    public QuadratoView getQuadratoView() {return quadratoView;}
    public AscoltatoreConnectionActivity getAscoltatore() { return ascoltatore; }
    public TextView getPosizioni() { return posizioni; }
    public TextView getTextLunghezze() { return textLunghezze; }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resume = false;
        setContentView(R.layout.activity_connection);
        input = new ArrayList<>();
        dialog = new ArrayList<>();
        joystickView = new JoystickView(this);
        quadratoView = new QuadratoView(this);
        String nome = Objects.requireNonNull(getIntent().getExtras()).getString("nome");
        String mac = Objects.requireNonNull(getIntent().getExtras().getString("mac"));
        ascoltatore = new AscoltatoreConnectionActivity(this);
        Button invia = (Button) findViewById(R.id.invia);
        Button resetX = (Button) findViewById(R.id.resetx);
        Button resetY = (Button) findViewById(R.id.resety);
        Button resetZ = (Button) findViewById(R.id.resetz);
        Button resetA = (Button) findViewById(R.id.reseta);
        Button clear = (Button) findViewById(R.id.clear);
        Button posiziona = (Button) findViewById(R.id.posiziona);
        ImageButton rettangolo = (ImageButton) findViewById(R.id.rettangolo);
        ImageButton triangolo = (ImageButton) findViewById(R.id.triangolo);
        ImageButton parallelo = (ImageButton) findViewById(R.id.parallelo);
        ImageButton cerchio = (ImageButton) findViewById(R.id.cerchio);
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
        resetX.setOnClickListener(ascoltatore);
        resetY.setOnClickListener(ascoltatore);
        resetZ.setOnClickListener(ascoltatore);
        resetA.setOnClickListener(ascoltatore);
        rettangolo.setOnClickListener(ascoltatore);
        triangolo.setOnClickListener(ascoltatore);
        parallelo.setOnClickListener(ascoltatore);
        cerchio.setOnClickListener(ascoltatore);
        clear.setOnClickListener(ascoltatore);
        posiziona.setOnClickListener(ascoltatore);
        sali.setOnTouchListener(ascoltatore);
        scendi.setOnTouchListener(ascoltatore);
        rotazione_attiva.setOnCheckedChangeListener(ascoltatore);
        createDialog("Scegli posizione", "Inserisci le coordinate", 3, new String[]{"X", "Y", "Z"});
        createDialog("Scegli dimensioni rettangolo", "Inserisci le dimensioni", 2, new String[]{"Base", "Altezza"});
        createDialog("Scegli dimensioni triangolo", "Inserisci le dimensioni", 1, new String[]{"Lato"});
        createDialog("Scegli dimensioni parallelogramma", "Inserisci le dimensioni", 3, new String[]{"Base", "Altezza", "Sfasamento"});
        createDialog("Scegli dimensioni cerchio", "Inserisci le dimensioni", 1, new String[]{"Raggio"});
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
        primo = true;
        initLunghezze = false;
        initPosizioni = false;
        ascoltatore.inviaMessaggio("ssb");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!primo)
            resume = true;
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
        if (primo) {
            getValoriInziaili();
            primo = false;
        }
        else
            quadratoView.drawPoint(ascoltatore.getPosizioni()[0], ascoltatore.getPosizioni()[1]);
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

    private void createDialog(String title, String messagge, int element, String[] value){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(messagge);
        builder.setView(dialogView(element, value));
        builder.setPositiveButton("Conferma", ascoltatore);
        builder.setNegativeButton(android.R.string.cancel, ascoltatore);
        AlertDialog dialogElement = builder.create();
        dialog.add(dialogElement);
    }

    private LinearLayout dialogView(int element, String[] value){
        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(lp);
        l.setWeightSum(element);
        l.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < element; i++) {
            TextView testoDialog = new TextView(this);
            testoDialog.setText(value[i]);
            lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f);
            testoDialog.setLayoutParams(lp);
            l.addView(testoDialog);
            EditText inputDialog = new EditText(this);
            lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f);
            inputDialog.setLayoutParams(lp);
            inputDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputDialog.setText("0");
            input.add(inputDialog);
            l.addView(inputDialog);
        }
        return l;
    }
}
