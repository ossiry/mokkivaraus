package sample;

import javafx.beans.property.SimpleStringProperty;

public class kayttaja {

    private SimpleStringProperty kayttajaNimi;
    private SimpleStringProperty salasana;


    public kayttaja(String kayttajaNimi, String salasana) {
        this.setKayttajaNimi(new SimpleStringProperty(kayttajaNimi));
        this.setSalasana(new SimpleStringProperty(salasana));
    }

    public SimpleStringProperty getKayttajaNimi() {
        return kayttajaNimi;
    }

    public void setKayttajaNimi(SimpleStringProperty kayttajaNimi) {
        this.kayttajaNimi = kayttajaNimi;
    }

    public SimpleStringProperty getSalasana() {
        return salasana;
    }

    public void setSalasana(SimpleStringProperty salasana) {
        this.salasana = salasana;
    }
}
