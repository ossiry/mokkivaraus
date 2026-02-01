package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Mokki {

    private SimpleIntegerProperty mokki_id;
    private SimpleStringProperty toiminta_alue;
    private SimpleStringProperty mokki_nimi;
    private SimpleStringProperty mokki_osoite;
    private SimpleStringProperty mokki_postinro;
    private SimpleIntegerProperty mokki_henkilot;
    private SimpleIntegerProperty mokki_hinta;
    private SimpleStringProperty mokki_varustelu;
    private SimpleStringProperty mokki_kuvaus;
    private SimpleIntegerProperty mokki_omistajaid;

    public Mokki(int mokki_id, String toiminta_alue, String mokki_nimi,
                 String mokki_osoite, String mokki_postinro, int mokki_henkilot,
                 int mokki_hinta, String mokki_varustelu, String mokki_kuvaus, int mokki_omistajaid) {

        this.setMokki_id(new SimpleIntegerProperty(mokki_id));
        this.setToiminta_alue(new SimpleStringProperty(toiminta_alue));
        this.setMokki_nimi(new SimpleStringProperty(mokki_nimi));
        this.setMokki_osoite(new SimpleStringProperty(mokki_osoite));
        this.setMokki_postinro(new SimpleStringProperty(mokki_postinro));
        this.setMokki_henkilot(new SimpleIntegerProperty(mokki_henkilot));
        this.setMokki_hinta(new SimpleIntegerProperty(mokki_hinta));
        this.setMokki_varustelu(new SimpleStringProperty(mokki_varustelu));
        this.setMokki_kuvaus(new SimpleStringProperty(mokki_kuvaus));
        this.setMokki_omistajaid(new SimpleIntegerProperty(mokki_omistajaid));
    }

    public Mokki() {

    }

    public int getMokki_id() {
        return mokki_id.get();
    }

    public void setMokki_id(SimpleIntegerProperty mokki_id) {
        this.mokki_id = mokki_id;
    }

    public String getToiminta_alue() {
        return toiminta_alue.get();
    }

    public void setToiminta_alue(SimpleStringProperty toiminta_alue) {
        this.toiminta_alue = toiminta_alue;
    }

    public String getMokki_nimi() {
        return mokki_nimi.get();
    }

    public void setMokki_nimi(SimpleStringProperty mokki_nimi) {
        this.mokki_nimi = mokki_nimi;
    }

    public String getMokki_osoite() {
        return mokki_osoite.get();
    }

    public void setMokki_osoite(SimpleStringProperty mokki_osoite) {
        this.mokki_osoite = mokki_osoite;
    }

    public String getMokki_postinro() {
        return mokki_postinro.get();
    }

    public void setMokki_postinro(SimpleStringProperty mokki_postinro) {
        this.mokki_postinro = mokki_postinro;
    }

    public int getMokki_henkilot() {
        return mokki_henkilot.get();
    }

    public void setMokki_henkilot(SimpleIntegerProperty mokki_henkilot) {
        this.mokki_henkilot = mokki_henkilot;
    }

    public int getMokki_hinta() {
        return mokki_hinta.get();
    }

    public void setMokki_hinta(SimpleIntegerProperty mokki_hinta) {
        this.mokki_hinta = mokki_hinta;
    }

    public String getMokki_varustelu() {
        return mokki_varustelu.get();
    }

    public void setMokki_varustelu(SimpleStringProperty mokki_varustelu) {
        this.mokki_varustelu = mokki_varustelu;
    }

    public String getMokki_kuvaus() {
        return mokki_kuvaus.get();
    }

    public void setMokki_kuvaus(SimpleStringProperty mokki_kuvaus) {
        this.mokki_kuvaus = mokki_kuvaus;
    }

    public int getMokki_omistajaid() { return mokki_omistajaid.get(); }

    public void setMokki_omistajaid(SimpleIntegerProperty mokki_omistajaid) { this.mokki_omistajaid = mokki_omistajaid; }
}