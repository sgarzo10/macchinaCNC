package com.prova.gui.settings.activity;

import android.view.View;
import static android.content.Context.MODE_PRIVATE;
import com.prova.bluetooth.R;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class AscoltatoreSettingsActivity implements View.OnClickListener {

    private SettingsActivity app;
    private HashMap<String, String> map;

    AscoltatoreSettingsActivity(SettingsActivity app)
    {
        map = new HashMap<>();
        map.put("Alluminio", "A");
        map.put("Plastica", "P");
        map.put("Legno", "L");
        this.app=app;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.salva:
                app.getManageXml().setDiametro(Integer.parseInt(app.getMandrino().getText().toString()));
                app.getManageXml().setMateriale(map.get(app.getMateriale().getSelectedItem().toString()));
                app.getManageXml().getLunghezze().set(0, Integer.parseInt(app.getLunghezzaX().getText().toString()));
                app.getManageXml().getLunghezze().set(1, Integer.parseInt(app.getLunghezzaY().getText().toString()));
                app.getManageXml().getLunghezze().set(2, Integer.parseInt(app.getLunghezzaZ().getText().toString()));
                try {
                    app.getManageXml().setOst(app.openFileOutput("config.xml", MODE_PRIVATE));
                    app.getManageXml().writeXml();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
