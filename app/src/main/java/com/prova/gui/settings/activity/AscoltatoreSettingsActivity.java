package com.prova.gui.settings.activity;

import android.view.View;
import static android.content.Context.MODE_PRIVATE;
import com.prova.bluetooth.R;
import java.io.FileNotFoundException;

public class AscoltatoreSettingsActivity implements View.OnClickListener {

    private SettingsActivity app;

    AscoltatoreSettingsActivity(SettingsActivity app) { this.app=app; }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.salva:
                app.getManageXml().setDiametro(Double.parseDouble(app.getMandrino().getText().toString()));
                app.getManageXml().setVelocita(Integer.parseInt(app.getVelocita().getText().toString()));
                for (int i = 0; i < 2; i++) {
                    app.getManageXml().getLunghezze().set(i, Integer.parseInt(app.getLunghezze().get(i).getText().toString()));
                    app.getManageXml().getPrecisioni().set(i, Float.parseFloat(app.getPrecisioni().get(i).getText().toString()));
                    app.getManageXml().getGiriMillimetro().set(i, Integer.parseInt(app.getGiriMillimetro().get(i).getText().toString()));
                }
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
