package sample;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Vuokraus {

    private SimpleIntegerProperty varaus_id;
    private SimpleIntegerProperty mokki_id;
    private SimpleStringProperty varattu_pvm;
    private SimpleStringProperty vahvistus_pvm;
    private SimpleStringProperty varattu_alkupvm;
    private SimpleStringProperty varattu_loppupvm;
    private SimpleIntegerProperty asiakas_id;
    private SimpleStringProperty mokkinimi;
    private SimpleStringProperty nimi;
    private SimpleBooleanProperty vahvistettu;
    private SimpleLongProperty kestoaika;
    private SimpleBooleanProperty laskutettu;



    public Vuokraus(int varaus_id, int mokki_id, String varattu_pvm, String vahvistus_pvm, String varattu_alkupvm,
                    String varattu_loppupvm, int asiakas_id, String mokkinimi, String nimi,
                    boolean vahvistettu, long kestoaika, boolean laskutettu) {
        this.setVaraus_id(new SimpleIntegerProperty(varaus_id));
        this.setMokki_id(new SimpleIntegerProperty(mokki_id));
        this.setVarattu_pvm(new SimpleStringProperty(varattu_pvm));
        this.setVahvistus_pvm(new SimpleStringProperty(vahvistus_pvm));
        this.setVarattu_alkupvm(new SimpleStringProperty(varattu_alkupvm));
        this.setVarattu_loppupvm(new SimpleStringProperty(varattu_loppupvm));
        this.setAsiakas_id(new SimpleIntegerProperty(asiakas_id));
        this.setMokkinimi(new SimpleStringProperty(mokkinimi));
        this.setNimi(new SimpleStringProperty(nimi));
        this.setVahvistettu(new SimpleBooleanProperty(vahvistettu));
        this.setKestoaika(new SimpleLongProperty(kestoaika));
        this.setLaskutettu(new SimpleBooleanProperty(laskutettu));
    }

    public int getVaraus_id() {
        return varaus_id.get();
    }

    public SimpleIntegerProperty varaus_idProperty() {
        return varaus_id;
    }

    public void setVaraus_id(SimpleIntegerProperty varaus_id) {
        this.varaus_id = varaus_id;
    }

    public int getMokki_id() {
        return mokki_id.get();
    }

    public SimpleIntegerProperty mokki_idProperty() {
        return mokki_id;
    }

    public void setMokki_id(SimpleIntegerProperty mokki_id) {
        this.mokki_id = mokki_id;
    }

    public String getVarattu_pvm() {
        return varattu_pvm.get();
    }

    public SimpleStringProperty varattu_pvmProperty() {
        return varattu_pvm;
    }

    public void setVarattu_pvm(SimpleStringProperty varattu_pvm) {
        this.varattu_pvm = varattu_pvm;
    }

    public String getVahvistus_pvm() {
        return vahvistus_pvm.get();
    }

    public SimpleStringProperty vahvistus_pvmProperty() {
        return vahvistus_pvm;
    }

    public void setVahvistus_pvm(SimpleStringProperty vahvistus_pvm) {
        this.vahvistus_pvm = vahvistus_pvm;
    }

    public String getVarattu_alkupvm() {
        return varattu_alkupvm.get();
    }

    public SimpleStringProperty varattu_alkupvmProperty() {
        return varattu_alkupvm;
    }

    public void setVarattu_alkupvm(SimpleStringProperty varattu_alkupvm) {
        this.varattu_alkupvm = varattu_alkupvm;
    }

    public String getVarattu_loppupvm() {
        return varattu_loppupvm.get();
    }

    public SimpleStringProperty varattu_loppupvmProperty() {
        return varattu_loppupvm;
    }

    public void setVarattu_loppupvm(SimpleStringProperty varattu_loppupvm) {
        this.varattu_loppupvm = varattu_loppupvm;
    }

    public int getAsiakas_id() {
        return asiakas_id.get();
    }

    public SimpleIntegerProperty asiakas_idProperty() {
        return asiakas_id;
    }

    public void setAsiakas_id(SimpleIntegerProperty asiakas_id) {
        this.asiakas_id = asiakas_id;
    }
    public String getMokkinimi() {
        return mokkinimi.get();
    }

    public SimpleStringProperty mokkinimiProperty() {
        return mokkinimi;
    }

    public void setMokkinimi(SimpleStringProperty mokkinimi) {
        this.mokkinimi = mokkinimi;
    }

    public String getNimi() {
        return nimi.get();
    }

    public SimpleStringProperty nimiProperty() {
        return nimi;
    }

    public void setNimi(SimpleStringProperty nimi) {
        this.nimi = nimi;
    }

    public Boolean getVahvistus() {return vahvistettu.get(); }

    public SimpleBooleanProperty vahvistettuProperty() { return vahvistettu; }

    public void setVahvistettu(SimpleBooleanProperty vahvistettu) {this.vahvistettu = vahvistettu; }

    public Boolean getLaskutus() {return laskutettu.get(); }

    public SimpleBooleanProperty laskutettuProperty() { return laskutettu; }

    public void setLaskutettu(SimpleBooleanProperty laskutettu) {this.laskutettu = laskutettu; }

    public long getKestoaika() {
        return kestoaika.get();
    }

    public SimpleLongProperty kestoaikaProperty() {
        return kestoaika;
    }

    public void setKestoaika(SimpleLongProperty kestoaika) {
        this.kestoaika = kestoaika;
    }


}
