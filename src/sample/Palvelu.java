package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import sample.connectivity.connectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Palvelu {


    private SimpleIntegerProperty palvelu_id;
    private SimpleIntegerProperty toimintaalue_id;
    private SimpleStringProperty nimi;
    private SimpleIntegerProperty tyyppi;
    private SimpleStringProperty kuvaus;
    private SimpleDoubleProperty hinta;
    private SimpleDoubleProperty alv;
    private String maara;

    public Palvelu() {
    }

    public Palvelu(int palvelu_id, int toimintaalue_id, String nimi, int tyyppi, String kuvaus, double hinta, double alv) {
        this.setPalvelu_id(new SimpleIntegerProperty(palvelu_id));
        this.setToimintaalue_id(new SimpleIntegerProperty(toimintaalue_id));
        this.setNimi(new SimpleStringProperty(nimi));
        this.setTyyppi(new SimpleIntegerProperty(tyyppi));
        this.setKuvaus(new SimpleStringProperty(kuvaus));
        this.setHinta(new SimpleDoubleProperty(hinta));
        this.setAlv(new SimpleDoubleProperty(alv));
    }

    public Palvelu(int palvelu_id, String nimi, double hinta, double alv, String maara) {
        this.setPalvelu_id(new SimpleIntegerProperty(palvelu_id));
        this.setNimi(new SimpleStringProperty(nimi));
        this.setHinta(new SimpleDoubleProperty(hinta));
        this.setAlv(new SimpleDoubleProperty(alv));
        this.maara = maara;
    }


    public int getPalvelu_id() {
        return palvelu_id.get();
    }

    public void setPalvelu_id(SimpleIntegerProperty palvelu_id) {
        this.palvelu_id = palvelu_id;
    }

    public int getToimintaalue_id() {
        return toimintaalue_id.get();
    }

    public void setToimintaalue_id(SimpleIntegerProperty toimintaalue_id) {
        this.toimintaalue_id = toimintaalue_id;
    }

    public String getNimi() {
        return nimi.get();
    }

    public void setNimi(SimpleStringProperty nimi) {
        this.nimi = nimi;
    }

    public int getTyyppi() {
        return tyyppi.get();
    }

    public void setTyyppi(SimpleIntegerProperty tyyppi) {
        this.tyyppi = tyyppi;
    }

    public String getKuvaus() {
        return kuvaus.get();
    }

    public void setKuvaus(SimpleStringProperty kuvaus) {
        this.kuvaus = kuvaus;
    }

    public double getHinta() {
        return hinta.get();
    }

    public void setHinta(SimpleDoubleProperty hinta) {
        this.hinta = hinta;
    }

    public double getAlv() {
        return alv.get();
    }

    public void setAlv(SimpleDoubleProperty alv) {
        this.alv = alv;
    }

    public String getMaara() {
        return maara;
    }

    public void setMaara(String maara) {
        this.maara = maara;
    }
}

