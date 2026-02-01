package sample;

import javafx.beans.property.SimpleIntegerProperty;


public class lasku {

    private SimpleIntegerProperty lasku_id;
    private SimpleIntegerProperty varaus_id;
    private SimpleIntegerProperty summa;
    private SimpleIntegerProperty alv;

    public lasku(int lasku_id, int varaus_id, int summa, int alv) {
    this.setLasku_id(new SimpleIntegerProperty(lasku_id));
    this.setVaraus_id(new SimpleIntegerProperty(varaus_id));
    this.setSumma(new SimpleIntegerProperty(summa));
    this.setAlv(new SimpleIntegerProperty(alv));




    }


    public int getLasku_id() {
        return lasku_id.get();
    }

    public void setLasku_id(SimpleIntegerProperty lasku_id) {
        this.lasku_id = lasku_id;
    }

    public int getVaraus_id() {
        return varaus_id.get();
    }

    public void setVaraus_id(SimpleIntegerProperty varaus_id) {
        this.varaus_id = varaus_id;
    }

    public int getSumma() {
        return summa.get();
    }

    public void setSumma(SimpleIntegerProperty summa) {
        this.summa = summa;
    }

    public int getAlv() {
        return alv.get();
    }

    public void setAlv(SimpleIntegerProperty alv) {
        this.alv = alv;
    }
}
