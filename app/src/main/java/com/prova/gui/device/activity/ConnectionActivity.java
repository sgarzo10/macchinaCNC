package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import com.prova.bluetooth.BluetoothConnection;
import com.prova.bluetooth.R;
import com.prova.gui.device.view.JoystickView;
import com.prova.gui.device.utility.MovePoint;
import com.prova.gui.device.utility.MyHandler;
import com.prova.gui.settings.utility.ManageXml;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity {

    private ArrayList<EditText> input;
    private ArrayList<CheckBox> check;
    private TextView textLunghezze;
    private TextView textGiri;
    private TextView numMex;
    private ArrayList<TextView> textPosizioni;
    private LinearLayout output;
    private BluetoothConnection bluetooth;
    private JoystickView joystickView;
    private MovePoint mp;
    private Switch rotazione_attiva;
    private AscoltatoreConnectionActivity ascoltatore;
    private ArrayList<AlertDialog> dialog;
    private ProgressBar progressBar;
    private boolean initLunghezze;
    private boolean initPosizioni;
    private boolean initGiri;
    private boolean primo;
    private boolean killBluetoothConnection;
    private boolean killMovePoint;
    private ManageXml manageXml;
    private Map<Integer, String> map;

    public BluetoothConnection getBluetooth() { return bluetooth;}
    ArrayList<EditText> getInput() { return input; }
    ArrayList<CheckBox> getCheck() { return check; }
    ArrayList<AlertDialog> getDialog() {return dialog;}
    Switch getRotazione_attiva() {return rotazione_attiva;}
    public AscoltatoreConnectionActivity getAscoltatore() { return ascoltatore; }
    public ArrayList<TextView> getTextPosizioni() { return textPosizioni; }
    public TextView getTextLunghezze() { return textLunghezze; }
    public TextView getTextGiri() { return textGiri; }
    public TextView getNumMex() { return numMex; }
    public ManageXml getManageXml() { return manageXml; }
    public ProgressBar getProgressBar() { return progressBar; }
    public boolean isKillBluetoothConnection() { return killBluetoothConnection; }
    public boolean isKillMovePoint() { return killMovePoint; }

    @SuppressLint({"UseSparseArrays", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        killBluetoothConnection = false;
        killMovePoint = false;
        map = new HashMap<>();
        map.put(0, "x");
        map.put(1, "y");
        map.put(2, "z");
        setContentView(R.layout.activity_connection);
        input = new ArrayList<>();
        check = new ArrayList<>();
        dialog = new ArrayList<>();
        textPosizioni = new ArrayList<>();
        joystickView = new JoystickView(this);
        String nome = Objects.requireNonNull(getIntent().getExtras()).getString("nome");
        String mac = Objects.requireNonNull(getIntent().getExtras().getString("mac"));
        ascoltatore = new AscoltatoreConnectionActivity(this);
        Button reset = findViewById(R.id.reset);
        Button posiziona = findViewById(R.id.posiziona);
        ImageButton linea = findViewById(R.id.linea);
        ImageButton curva = findViewById(R.id.curva);
        ImageButton rettangolo = findViewById(R.id.rettangolo);
        ImageButton triangolo = findViewById(R.id.triangolo);
        ImageButton parallelo = findViewById(R.id.parallelo);
        ImageButton cerchio = findViewById(R.id.cerchio);
        ImageButton trapezio = findViewById(R.id.trapezio);
        ImageButton sali = findViewById(R.id.sali);
        ImageButton scendi = findViewById(R.id.scendi);
        ImageButton settings = findViewById(R.id.settings);
        LinearLayout linearLayoutJoystick = findViewById(R.id.joystick);
        rotazione_attiva = findViewById(R.id.rotazione_attiva);
        output = findViewById(R.id.outSeriale);
        textPosizioni.add((TextView) findViewById(R.id.posizioneX));
        textPosizioni.add((TextView) findViewById(R.id.posizioneY));
        textPosizioni.add((TextView) findViewById(R.id.posizioneZ));
        textLunghezze = findViewById(R.id.text_lunghezze);
        textGiri = findViewById(R.id.text_giri);
        progressBar = findViewById(R.id.avanzamento);
        numMex = findViewById(R.id.numeroMex);
        linearLayoutJoystick.addView(joystickView);
        reset.setOnClickListener(ascoltatore);
        linea.setOnClickListener(ascoltatore);
        curva.setOnClickListener(ascoltatore);
        rettangolo.setOnClickListener(ascoltatore);
        triangolo.setOnClickListener(ascoltatore);
        parallelo.setOnClickListener(ascoltatore);
        cerchio.setOnClickListener(ascoltatore);
        trapezio.setOnClickListener(ascoltatore);
        posiziona.setOnClickListener(ascoltatore);
        settings.setOnClickListener(ascoltatore);
        sali.setOnTouchListener(ascoltatore);
        scendi.setOnTouchListener(ascoltatore);
        rotazione_attiva.setOnCheckedChangeListener(ascoltatore);
        createDialog("Scegli posizione", new String[]{"X", "Y", "Z"});
        createDialog("Scegli parametri linea", new String[]{"Lunghezza", "Angolo", "Profondità"});
        createDialog("Scegli parametri curva", new String[]{"Raggio", "Quadrante", "Profondità"});
        createDialog("Scegli dimensioni rettangolo", new String[]{"Base", "Altezza", "Profondità", "Spessore Base", "Spessore Altezza", "Riempi"});
        createDialog("Scegli dimensioni triangolo", new String[]{"Base", "Lato1", "Lato2", "Profondità", "Spessore Base", "Spessore Lato1", "Spessore Lato2", "Riempi"});
        createDialog("Scegli dimensioni parallelogramma",new String[]{"Base", "Diagonale", "Altezza", "Profondità", "Spessore Base", "Spessore Diagonale", "Riempi"});
        createDialog("Scegli dimensioni cerchio", new String[]{"Raggio", "Profondità", "Spessore", "Riempi"});
        createDialog("Scegli dimensioni trapezio", new String[]{"Base Maggiore", "Lato1", "Base Minore", "Lato2", "Altezza", "Profondità", "Riempi"});
        createDialog("RESET", new String[]{"Reset X", "Reset Y", "Reset Z"});
        mp = new MovePoint(this, joystickView);
        mp.start();
        TextView t = new TextView(this);
        boolean connesso = false;
        bluetooth = new BluetoothConnection(new MyHandler(this), this);
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
        initGiri = false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        manageXml = new ManageXml();
        File f = new File(getFilesDir(), "config.xml");
        if (f.exists()) {
            try {
                manageXml.setIst(openFileInput("config.xml"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            manageXml.readXml(true);
        }
        else{
            manageXml.setXrp(getResources().getXml(R.xml.config));
            manageXml.readXml(false);
        }
        if (!primo) {
            checkValori(false);
            getValoriInziaili();
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
        rotazione_attiva.setTextSize(rotazione_attiva.getHeight() / 4);
        textGiri.setTextSize(textGiri.getHeight() / 4);
        textLunghezze.setTextSize(textGiri.getHeight() / 4);
        for (int i = 0; i < 3; i++)
            textPosizioni.get(i).setTextSize(textGiri.getHeight() / 4);
        if (primo) {
            getValoriInziaili();
            primo = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.disconnetti();
        bluetooth.interrupt();
        mp.interrupt();
        killBluetoothConnection = true;
        killMovePoint = true;
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
                    if (!initGiri) {
                        ascoltatore.inviaMessaggio("ga");
                        initGiri = true;
                        bluetooth.getH().setFine(false);
                        getValoriInziaili();
                    } else {
                        if (!initLunghezze) {
                            ascoltatore.inviaMessaggio("la");
                            initLunghezze = true;
                            bluetooth.getH().setFine(false);
                            getValoriInziaili();
                        } else {
                            if (!initPosizioni) {
                                checkValori(true);
                                initPosizioni = true;
                                bluetooth.getH().setFine(false);
                                getValoriInziaili();
                            }
                        }
                    }
                }
            }
        }, 100);
    }

    private void createDialog(String title, String[] value){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setView(dialogView(value));
        builder.setPositiveButton("Conferma", ascoltatore);
        AlertDialog dialogElement = builder.create();
        dialog.add(dialogElement);
    }

    private View dialogView(String[] value){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View myScrollView = inflater.inflate(R.layout.scroll, null, false);
        LinearLayout l = myScrollView.findViewById(R.id.scrollLayout);
        LinearLayout.LayoutParams lp;
        for (String aValue : value) {
            LinearLayout l1 = new LinearLayout(this);
            TextView testoDialog = new TextView(this);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            l1.setLayoutParams(lp);
            l1.setWeightSum(1);
            l1.setOrientation(LinearLayout.HORIZONTAL);
            lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f);
            testoDialog.setText(aValue);
            testoDialog.setLayoutParams(lp);
            l1.addView(testoDialog);
            if (!aValue.equals("Riempi") && !aValue.contains("Reset")) {
                EditText inputDialog = new EditText(this);
                lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
                inputDialog.setLayoutParams(lp);
                inputDialog.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (!aValue.equals("Profondità"))
                    inputDialog.setText("0");
                else
                    inputDialog.setText("1");
                input.add(inputDialog);
                l1.addView(inputDialog);
            } else {
                CheckBox inputDialog = new CheckBox(this);
                lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
                inputDialog.setLayoutParams(lp);
                inputDialog.setChecked(true);
                check.add(inputDialog);
                l1.addView(inputDialog);
            }
            l.addView(l1);
        }
        return myScrollView;
    }

    private void checkValori(boolean posizioni){
        ArrayList<String> messaggi = new ArrayList<>();
        int size = 0;
        for (int i = 0; i < 3; i++) {
            if (!(ascoltatore.getLunghezze()[i] == manageXml.getLunghezze().get(i)))
                messaggi.add("sl" + map.get(i) + manageXml.getLunghezze().get(i));
        }
        if (messaggi.size() > 0) {
            initLunghezze = false;
            size = messaggi.size();
        }
        for (int i = 0; i < 3; i++) {
            if (!(ascoltatore.getGiriMillimetro()[i] == manageXml.getGiriMillimetro().get(i)))
                messaggi.add("sg" + map.get(i) + manageXml.getGiriMillimetro().get(i));
        }
        if (messaggi.size() > size)
            initGiri = false;
        if (posizioni)
            messaggi.add(0, "da");
        if (messaggi.size() > 0) {
            if (!posizioni)
                bluetooth.getH().setFine(true);
            ascoltatore.addMex(messaggi);
        }
    }
}
