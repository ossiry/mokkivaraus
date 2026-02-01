package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class asiakas {

    private SimpleStringProperty etunimi;
    private SimpleStringProperty sukunimi;
    private SimpleStringProperty lahiosoite;
    private SimpleStringProperty email;
    private SimpleIntegerProperty asiakas_id;
    private SimpleIntegerProperty postinro;
    private SimpleStringProperty puhelinnro;

    public asiakas(int asiakas_id, int postinro, String etunimi, String sukunimi, String lahiosoite, String email,
                   String puhelinnro) {
        this.setAsiakas_id(new SimpleIntegerProperty(asiakas_id));
        this.setPostinro(new SimpleIntegerProperty(postinro));
        this.setEtunimi(new SimpleStringProperty(etunimi));
        this.setSukunimi(new SimpleStringProperty(sukunimi));
        this.setLahiosoite(new SimpleStringProperty(lahiosoite));
        this.setEmail(new SimpleStringProperty(email));
        this.setPuhelinnro(new SimpleStringProperty(puhelinnro));
    }


    public String getEtunimi() {
        return etunimi.get();
    }

    public void setEtunimi(SimpleStringProperty etunimi) {
        this.etunimi = etunimi;
    }

    public String getSukunimi() {
        return sukunimi.get();
    }

    public void setSukunimi(SimpleStringProperty sukunimi) {
        this.sukunimi = sukunimi;
    }

    public String getLahiosoite() {
        return lahiosoite.get();
    }

    public void setLahiosoite(SimpleStringProperty lahiosoite) {
        this.lahiosoite = lahiosoite;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(SimpleStringProperty email) {
        this.email = email;
    }

    public Integer getAsiakas_id() {
        return asiakas_id.get();
    }

    public void setAsiakas_id(SimpleIntegerProperty asiakas_id) {
        this.asiakas_id = asiakas_id;
    }

    public Integer getPostinro() {
        return postinro.get();
    }

    public void setPostinro(SimpleIntegerProperty postinro) {
        this.postinro = postinro;
    }

    public String getPuhelinnro() {
        return puhelinnro.get();
    }

    public void setPuhelinnro(SimpleStringProperty puhelinnro) {
        this.puhelinnro = puhelinnro;
    }

}
