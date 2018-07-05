package com.prova.gui.device.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.dxf.DXFLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity {

    private ArrayList<EditText> input;
    private ArrayList<CheckBox> check;
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
    public void setResume() {this.resume = false;}
    ArrayList<EditText> getInput() { return input; }
    ArrayList<CheckBox> getCheck() { return check; }
    ArrayList<AlertDialog> getDialog() {return dialog;}
    Switch getRotazione_attiva() {return rotazione_attiva;}
    public QuadratoView getQuadratoView() {return quadratoView;}
    public AscoltatoreConnectionActivity getAscoltatore() { return ascoltatore; }
    public TextView getPosizioni() { return posizioni; }
    public TextView getTextLunghezze() { return textLunghezze; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Parser parser = ParserBuilder.createDefaultParser();
        try {
            parser.parse("path/file.dxf", DXFParser.DEFAULT_ENCODING);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DXFDocument doc = parser.getDocument();
        DXFLayer layer = doc.getDXFLayer("layer_name");
        List<DXFCircle> arcs = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE);*/
        resume = false;
        setContentView(R.layout.activity_connection);
        input = new ArrayList<>();
        check = new ArrayList<>();
        dialog = new ArrayList<>();
        joystickView = new JoystickView(this);
        quadratoView = new QuadratoView(this);
        String nome = Objects.requireNonNull(getIntent().getExtras()).getString("nome");
        String mac = Objects.requireNonNull(getIntent().getExtras().getString("mac"));
        ascoltatore = new AscoltatoreConnectionActivity(this);
        Button reset = findViewById(R.id.reset);
        Button clear = findViewById(R.id.clear);
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
        LinearLayout linearLayoutJoystick = findViewById(R.id.joystick);
        RelativeLayout relativeLayoutQuadrato = findViewById(R.id.quadrato);
        rotazione_attiva = findViewById(R.id.rotazione_attiva);
        output = findViewById(R.id.outSeriale);
        posizioni = findViewById(R.id.posizioni);
        textLunghezze = findViewById(R.id.text_lunghezze);
        linearLayoutJoystick.addView(joystickView);
        relativeLayoutQuadrato.addView(quadratoView);
        reset.setOnClickListener(ascoltatore);
        linea.setOnClickListener(ascoltatore);
        curva.setOnClickListener(ascoltatore);
        rettangolo.setOnClickListener(ascoltatore);
        triangolo.setOnClickListener(ascoltatore);
        parallelo.setOnClickListener(ascoltatore);
        cerchio.setOnClickListener(ascoltatore);
        trapezio.setOnClickListener(ascoltatore);
        clear.setOnClickListener(ascoltatore);
        posiziona.setOnClickListener(ascoltatore);
        sali.setOnTouchListener(ascoltatore);
        scendi.setOnTouchListener(ascoltatore);
        rotazione_attiva.setOnCheckedChangeListener(ascoltatore);
        createDialog("Scegli posizione", "Inserisci le coordinate", new String[]{"X", "Y", "Z"});
        createDialog("Scegli parametri linea", "Inserisci i parametri", new String[]{"Lunghezza", "Angolo"});
        createDialog("Scegli parametri curva", "Inserisci i parametri", new String[]{"Raggio", "Quadrante"});
        createDialog("Scegli dimensioni rettangolo", "Inserisci le dimensioni", new String[]{"Base", "Altezza", "Profondità", "Riempi"});
        createDialog("Scegli dimensioni triangolo", "Inserisci le dimensioni", new String[]{"Base", "Lato1", "Lato2", "Profondità", "Riempi"});
        createDialog("Scegli dimensioni parallelogramma", "Inserisci le dimensioni", new String[]{"Base", "Diagonale", "Altezza", "Profondità", "Riempi"});
        createDialog("Scegli dimensioni cerchio", "Inserisci le dimensioni", new String[]{"Raggio", "Profondità", "Riempi"});
        createDialog("Scegli dimensioni trapezio", "Inserisci le dimensioni", new String[]{"Base Maggiore", "Lato1", "Base Minore", "Lato2", "Altezza", "Profondità", "Riempi"});
        createDialog("RESET", "Scegli assi", new String[]{"Reset X", "Reset Y", "Reset Z"});
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
                        bluetooth.getH().setFine();
                        getValoriInziaili();
                    }
                    else {
                        if (!initPosizioni) {
                            ascoltatore.inviaMessaggio("da");
                            initPosizioni = true;
                            bluetooth.getH().setFine();
                            getValoriInziaili();
                        }
                        else
                            ascoltatore.getMessaggi().clear();
                    }
                }
            }
        }, 100);
    }

    private void createDialog(String title, String messagge, String[] value){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(messagge);
        builder.setView(dialogView(value));
        builder.setPositiveButton("Conferma", ascoltatore);
        builder.setNegativeButton(android.R.string.cancel, ascoltatore);
        AlertDialog dialogElement = builder.create();
        dialog.add(dialogElement);
    }

    private LinearLayout dialogView(String[] value){
        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(lp);
        l.setWeightSum(value.length);
        l.setOrientation(LinearLayout.HORIZONTAL);
        for (String aValue : value) {
            TextView testoDialog = new TextView(this);
            testoDialog.setText(aValue);
            lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f);
            testoDialog.setLayoutParams(lp);
            l.addView(testoDialog);
            if (!aValue.equals("Riempi") && !aValue.contains("Reset")) {
                EditText inputDialog = new EditText(this);
                lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f);
                inputDialog.setLayoutParams(lp);
                inputDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (aValue.equals("Profondità"))
                    inputDialog.setText("0");
                else
                    inputDialog.setText("1");
                input.add(inputDialog);
                l.addView(inputDialog);
            } else {
                CheckBox inputDialog = new CheckBox(this);
                lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f);
                inputDialog.setLayoutParams(lp);
                inputDialog.setChecked(true);
                check.add(inputDialog);
                l.addView(inputDialog);
            }
        }
        return l;
    }
}
