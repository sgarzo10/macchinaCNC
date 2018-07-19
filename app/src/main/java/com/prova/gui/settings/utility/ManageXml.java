package com.prova.gui.settings.utility;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

public class ManageXml {

    private InputStream ist;
    private FileOutputStream ost;
    private XmlPullParser xrp;
    private double diametro;
    private int velocita;
    private ArrayList<Integer> lunghezze;
    private ArrayList<Float> precisioni;
    private ArrayList<Integer> giriMillimetro;

    public ManageXml(){
        lunghezze = new ArrayList<>(3);
        precisioni = new ArrayList<>(3);
        giriMillimetro = new ArrayList<>(3);
        diametro = 0;
        velocita = 0;
    }

    public void writeXml() {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag("", "settings");
            xmlSerializer.startTag("", "mandrino");
            xmlSerializer.attribute("", "diametro", Double.toString(diametro));
            xmlSerializer.endTag("", "mandrino");
            xmlSerializer.startTag("", "materiale");
            xmlSerializer.attribute("", "velocita", Integer.toString(velocita));
            xmlSerializer.endTag("", "materiale");
            xmlSerializer.startTag("", "lunghezze");
            xmlSerializer.attribute("", "x", Integer.toString(lunghezze.get(0)));
            xmlSerializer.attribute("", "y", Integer.toString(lunghezze.get(1)));
            xmlSerializer.attribute("", "z", Integer.toString(lunghezze.get(2)));
            xmlSerializer.endTag("", "lunghezze");
            xmlSerializer.startTag("", "precisioni");
            xmlSerializer.attribute("", "x", Float.toString(precisioni.get(0)));
            xmlSerializer.attribute("", "y", Float.toString(precisioni.get(1)));
            xmlSerializer.attribute("", "z", Float.toString(precisioni.get(2)));
            xmlSerializer.endTag("", "precisioni");
            xmlSerializer.startTag("", "giri-millimetro");
            xmlSerializer.attribute("", "x", Integer.toString(giriMillimetro.get(0)));
            xmlSerializer.attribute("", "y", Integer.toString(giriMillimetro.get(1)));
            xmlSerializer.attribute("", "z", Integer.toString(giriMillimetro.get(2)));
            xmlSerializer.endTag("", "giri-millimetro");
            xmlSerializer.endTag("", "settings");
            xmlSerializer.endDocument();
            ost.write(writer.toString().getBytes());
            ost.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readXml(boolean my_config){
        try {
            if (my_config){
                xrp = Xml.newPullParser();
                xrp.setInput(ist, null);
            }
            int event = xrp.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)  {
                String name=xrp.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("mandrino"))
                            diametro = Double.parseDouble(xrp.getAttributeValue(0));
                        if(name.equals("materiale"))
                            velocita = Integer.parseInt(xrp.getAttributeValue(0));
                        if(name.equals("lunghezze")){
                            lunghezze.add(Integer.parseInt(xrp.getAttributeValue(0)));
                            lunghezze.add(Integer.parseInt(xrp.getAttributeValue(1)));
                            lunghezze.add(Integer.parseInt(xrp.getAttributeValue(2)));
                        }
                        if(name.equals("precisioni")){
                            precisioni.add(Float.parseFloat(xrp.getAttributeValue(0)));
                            precisioni.add(Float.parseFloat(xrp.getAttributeValue(1)));
                            precisioni.add(Float.parseFloat(xrp.getAttributeValue(2)));
                        }
                        if(name.equals("giri-millimetro")){
                            giriMillimetro.add(Integer.parseInt(xrp.getAttributeValue(0)));
                            giriMillimetro.add(Integer.parseInt(xrp.getAttributeValue(1)));
                            giriMillimetro.add(Integer.parseInt(xrp.getAttributeValue(2)));
                        }
                        break;
                }
                event = xrp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setXrp(XmlPullParser xrp) {
        this.xrp = xrp;
    }

    public void setIst(InputStream ist) {
        this.ist = ist;
    }

    public void setOst(FileOutputStream ost) {
        this.ost = ost;
    }

    public ArrayList<Integer> getLunghezze() { return lunghezze; }

    public double getDiametro() { return diametro; }

    public int getVelocita() { return velocita; }

    public void setDiametro(double diametro) { this.diametro = diametro; }

    public void setVelocita(int velocita) { this.velocita = velocita; }

    public ArrayList<Float> getPrecisioni() { return precisioni; }

    public ArrayList<Integer> getGiriMillimetro() { return giriMillimetro; }
}
