package com.prova.gui.search;

import android.content.IntentFilter;
import android.os.Bundle;
import android.bluetooth.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.prova.bluetooth.R;

public class MainActivity extends AppCompatActivity {

    private Ricevitore ricevitore;
    private AscoltatoreMainActivity ascoltatore;
    private Button associati;
    private ImageButton cerca;
    private TextView messaggio;
    private ListView nomi;
    private ListView address;
    private LinearLayout bottoni;
    private Switch stato_bluetooth;
    private boolean collegato = false;

    public TextView getMessaggio() {return messaggio;}
    public Button getAssociati() {return associati;}
    public ImageButton getCerca() {return cerca;}
    public ListView getNomi() {return nomi;}
    public ListView getAddress() {return address;}
    public LinearLayout getBottoni() {return bottoni;}
    public Switch getStato_bluetooth(){return stato_bluetooth;}
    public AscoltatoreMainActivity getAscoltatore() { return ascoltatore; }
    public void setCollegato (boolean collegato){ this.collegato = collegato; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nomi = (ListView) findViewById(R.id.nomi);
        address = (ListView)findViewById(R.id.address);
        bottoni =(LinearLayout) findViewById(R.id.bottoni);
        stato_bluetooth = (Switch) findViewById(R.id.stato_bluetooth);
        messaggio = (TextView) findViewById(R.id.messaggio);
        associati = (Button) findViewById(R.id.associati);
        cerca = (ImageButton) findViewById(R.id.cerca);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ricevitore = new Ricevitore(this);
        registerReceiver(ricevitore, filter);
        ascoltatore= new AscoltatoreMainActivity(this);
        associati.setOnClickListener(ascoltatore);
        cerca.setOnClickListener(ascoltatore);
        stato_bluetooth.setOnCheckedChangeListener(ascoltatore);
    }

    @Override
    protected void onResume(){
        super.onResume();
        changeView();
        if (collegato) {
            TextView t = (TextView) findViewById(R.id.messaggio);
            t.setText(R.string.finish_connection);
            collegato = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ricevitore);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ascoltatore.inRicerca();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        int size = stato_bluetooth.getHeight();
        stato_bluetooth.setTextSize(size/8);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
        buttonLayoutParams.setMargins(associati.getWidth()/10,associati.getHeight()/6,associati.getWidth()/10,associati.getHeight()/6);
        associati.setLayoutParams(buttonLayoutParams);
    }

    public void changeView(){
        if(ascoltatore.getBluetooth().getBluetoothAdapter().isEnabled()){
            cerca.setVisibility(View.VISIBLE);
            associati.setVisibility(View.VISIBLE);
            stato_bluetooth.setChecked(true);
            stato_bluetooth.setText(getResources().getString(R.string.on));
        }
        else{
            cerca.setVisibility(View.INVISIBLE);
            associati.setVisibility(View.INVISIBLE);
            stato_bluetooth.setChecked(false);
            stato_bluetooth.setText(getResources().getString(R.string.off));
        }
    }
}
