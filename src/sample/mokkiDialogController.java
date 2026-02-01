package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class mokkiDialogController {

    private static int laskuri = 0;
    private boolean lippu;
    private int mokki_id;
    private int henkilomaara = -1;
    private int hinta = -1;
    private int omistajaid = -1;
    private String nimi = "";
    private String ta = "";
    private String osoite = "";
    private String postinro = "";
    private String varustelu = "";
    private String kuvaus = "";

    public mokkiDialogController () {
        laskuri = 0;
        lippu = false;
    }

    // Alustetaan connectClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    public mokkienHallintaController controller;
    public Label lblHallintaNotification;

    @FXML
    private ChoiceBox<String> cbToimintaalue;
    @FXML
    private TextField tfNimi, tfOsoite, tfPostinro, tfHenkilomaara, tfHinta, tfOmistajaId;
    @FXML
    private TextArea taVarustelu, taKuvaus;
    @FXML
    private Label lblValidateNimi, lblValidateOsoite, lblValidateToimintaalue, lblValidatePostinro, lblValidateHinta,
            lblValidateHenkilomaara, lblValidateOmistajaid, lblValidateVarustelu, lblValidateKuvaus;


    public void setMokkiController(mokkienHallintaController controller) {
        this.controller = controller ;
    }

    public void setMokitObservableList(Mokki mokki) {

        tfNimi.setText(mokki.getMokki_nimi());
        tfOsoite.setText(mokki.getMokki_osoite());
        tfPostinro.setText(mokki.getMokki_postinro());
        tfHenkilomaara.setText(Integer.toString(mokki.getMokki_henkilot()));
        tfHinta.setText(Integer.toString(mokki.getMokki_hinta()));
        tfOmistajaId.setText(Integer.toString(mokki.getMokki_omistajaid()));
        taVarustelu.setText(mokki.getMokki_varustelu());
        taKuvaus.setText(mokki.getMokki_kuvaus());
        cbToimintaalue.setValue(mokki.getToiminta_alue());
        mokki_id = mokki.getMokki_id();
    }

    /*
        Metodi Lisää-napille "Lisää mökki"-dialokissa. Metodi lisää mökin tietokantaan dialokin kenttiin
        syötetyillä tiedoilla.
     */
    @FXML
    public void lisaaMokki(ActionEvent event) {

        System.out.println("Lisää-nappia painettu Lisää-mökki dialokissa.");

        this.lippu = false;
        validateToimintaAlue();
        validateNimi();
        validateOsoite();
        validatePostinro();
        validateHenkilomaara();
        validateHinta();
        validateOmistaja();
        validateVarustelu();
        validateKuvaus();

        int ta_id = getToimintaAlueId(this.ta);

        if (!lippu && ta_id != -1) {
            try {
                // Muodostetaan SQL-lause mökin lisäämiseen.
                String insQuery = "INSERT INTO mokki (toimintaalue_id, postinro, mokkinimi, katuosoite, kuvaus, " +
                        "henkilomaara, varustelu, vrk_hinta, omistaja_id) VALUES (?,?,?,?,?,?,?,?, ?)";

                PreparedStatement preparedStmt = connectDB.prepareStatement(insQuery);
                preparedStmt.setInt(1, ta_id);
                preparedStmt.setString(2, this.postinro);
                preparedStmt.setString(3, this.nimi);
                preparedStmt.setString(4, this.osoite);
                preparedStmt.setString(5, this.kuvaus);
                preparedStmt.setInt(6, this.henkilomaara);
                preparedStmt.setString(7, this.varustelu);
                preparedStmt.setInt(8, this.hinta);
                preparedStmt.setInt(9, this.omistajaid);

                // Suoritetaan tietokantaan lisäys.
                int rowsInserted = preparedStmt.executeUpdate();
                if (rowsInserted > 0) {
                    lblHallintaNotification.setText("Mökki lisättiin tietokantaan!");
                    System.out.println("Mökki lisättiin tietokantaan!");
                    // Haetaan lisäyksen jälkeen kaikki mökit taululle.
                    // TODO hae vain lisätty mökki?
                    controller.haeKaikki("mokit");
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            closeStage(event);
        }
    }

    /*
        Metodi Hyväksy-napille "Muokkaa mökkiä"-dialokissa. Metodi päivittää mökin tietokantaan dialokin kenttiin
        syötetyillä tiedoilla.
    */

    @FXML
    public void paivitaMokki (ActionEvent event) {

        System.out.println("Hyväksy-nappia painettu Muokkaa-mökkiä dialokissa.");

        this.lippu = false;
        validateToimintaAlue();
        validateNimi();
        validateOsoite();
        validatePostinro();
        validateHenkilomaara();
        validateHinta();
        validateOmistaja();
        validateVarustelu();
        validateKuvaus();

        int ta_id = getToimintaAlueId(this.ta);

        if (!lippu && ta_id != -1) {
            try {
                // Muodostetaan SQL-lause mökin päivittämiseen.
                String updQuery = "UPDATE mokki SET toimintaalue_id=?, postinro=?, mokkinimi=?, katuosoite=?, kuvaus=?, " +
                        "henkilomaara=?, varustelu=?, vrk_hinta=?, omistaja_id=? WHERE mokki_id=" + mokki_id;

                PreparedStatement preparedStmt = connectDB.prepareStatement(updQuery);
                preparedStmt.setInt(1, ta_id);
                preparedStmt.setString(2, postinro);
                preparedStmt.setString(3, nimi);
                preparedStmt.setString(4, osoite);
                preparedStmt.setString(5, kuvaus);
                preparedStmt.setInt(6, henkilomaara);
                preparedStmt.setString(7, varustelu);
                preparedStmt.setInt(8, hinta);
                preparedStmt.setInt(9, omistajaid);

                // Suoritetaan tietokantaan lisäys.
                int rowsUpdated = preparedStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    lblHallintaNotification.setText("Mökkiä muokattiin onnistuneesti!");
                    System.out.println("Mökkiä muokattiin onnistuneesti!");
                    // Haetaan päivityksen jälkeen kaikki mökit taululle.
                    // TODO hae vain päivitetty mökki?
                    controller.haeKaikki("mokit");
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            closeStage(event);
        }
    }

    private void validateToimintaAlue() {
        if (cbToimintaalue.getValue().equals("Valitse toiminta-alue")) {
            this.lippu = true;
            lblValidateToimintaalue.setText("Toiminta-alue puuttuu!");
        } else {
            this.ta = cbToimintaalue.getValue();
            lblValidateToimintaalue.setText("");
        }
    }

    private void validateNimi() {
        if (tfNimi.getText() == null || tfNimi.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateNimi.setText("Nimi puuttuu!");
        } else {
            this.nimi = tfNimi.getText().trim();
            lblValidateNimi.setText("");
        }
    }

    private void validateOsoite() {
        if (tfOsoite.getText() == null ||tfOsoite.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateOsoite.setText("Osoite puuttuu!");
        }  else {
            this.osoite = tfOsoite.getText().trim();
            lblValidateOsoite.setText("");
        }
    }

    private void validatePostinro() {
        if (tfPostinro.getText() == null || tfPostinro.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidatePostinro.setText("Postinumero puuttuu!");
        } else {
            this.postinro = tfPostinro.getText().trim();
            if (checkPostinro(postinro)) {
                lblValidatePostinro.setText("");
            } else {
                this.lippu = true;
                lblValidatePostinro.setText("Postinumero ei ole tietokannassa!");
            }
        }
    }

    private void validateHenkilomaara() {
        if (tfHenkilomaara.getText() == null || tfHenkilomaara.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateHenkilomaara.setText("Henkilömäärä puuttuu!");
        } else {
            try {
                this.henkilomaara = Integer.parseInt(tfHenkilomaara.getText().trim());
                lblValidateHenkilomaara.setText("");
            } catch (NumberFormatException e) {
                this.lippu = true;
            }
        }
    }

    private void validateHinta() {
        if (tfHinta.getText() == null || tfHinta.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateHinta.setText("Hinta puuttuu!");
        } else {
            try {
                this.hinta = Integer.parseInt(tfHinta.getText().trim());
                lblValidateHinta.setText("");
            } catch (NumberFormatException e) {
                this.lippu = true;
            }
        }
    }

    private void validateOmistaja() {
        if (tfOmistajaId.getText() == null || tfOmistajaId.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateOmistajaid.setText("Omistaja-Id puuttuu!");
        } else {
            try {
                this.omistajaid = Integer.parseInt(tfOmistajaId.getText().trim());
                if (checkOmistajaId(omistajaid)) {
                    lblValidateOmistajaid.setText("");
                } else {
                    this.lippu = true;
                    lblValidateOmistajaid.setText("Omistajaa ei löydy tietokannasta!");
                }
            } catch (NumberFormatException e) {
                lippu = true;
            }
        }
    }

    private void validateVarustelu() {
        if (taVarustelu.getText().trim().length() > 100) {
            this.lippu = true;
            lblValidateVarustelu.setText("Liian monta merkkiä!");
        } else {
            this.varustelu = taVarustelu.getText().trim();
            lblValidateVarustelu.setText("");
        }
    }

    private void validateKuvaus() {
        if (taKuvaus.getText().trim().length() > 150) {
            this.lippu = true;
            lblValidateKuvaus.setText("Liian monta merkkiä!");
        } else {
            this.kuvaus = taKuvaus.getText().trim();
            lblValidateKuvaus.setText("");
        }
    }

    /*
        Metodi, jolla testataan löytyykö tekstikenttään syötetty omistaja-id tietokannasta.
        Palauttaa true, jos id löytyy, muuten false.
    */
    private boolean checkOmistajaId(int omistajaid) {

        String query = "SELECT asiakas_id FROM asiakas WHERE asiakas_id = '" + omistajaid + "'";
        boolean result = false;

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);
            result = queryResult.next(); // True, jos query palauttaa rivin.
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    Metodi, jolla testataan löytyykö tekstikenttään syötetty postinro tietokannasta.
    Palauttaa true, jos postinro löytyy, muuten false.
    */
    private boolean checkPostinro(String postinro) {

        String query = "SELECT postinro FROM posti WHERE postinro = '" + postinro + "'";
        boolean result = false;

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);
            result = queryResult.next(); // True, jos query palauttaa rivin.
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
      Metodi toiminta-alueiden hakuun pudotusvalikkoon. Metodi suorittuu pudotusvalikkoa hiirellä painettaessa.
    */
    @FXML
    public void taMenu() {

        if (laskuri == 0) { // SQL-kutsu tehdään vain kerran dialokin elinkaaren aikana.

            String query = "SELECT * FROM toimintaalue"; // SQL-lause kaikkien toiminta-alueiden hakemiseen.

            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(query);
                ResultSet queryResult = preparedStmt.executeQuery(query);

                while (queryResult.next()) {
                    String alue = queryResult.getString("nimi");
                    cbToimintaalue.getItems().add(alue);
                }
                preparedStmt.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            cbToimintaalue.setValue("Valitse toiminta-alue");
            laskuri++;
        }
    }

    /*
        Metodi hakee tietokannasta ja palauttaa dialokin pudotusvalikosta valitun toiminta-alueen id:n.
        Jos valintaa ei ole tehty, metodi palauttaa arvon -1.
    */

    private int getToimintaAlueId(String taNimi) {

        String query;
        int ta_id = -1;

        // SQL-lause toiminta-alueen id:n hakemiseen listasta valitulla toiminta-alueella.
        if (!taNimi.equals("Valitse toiminta-alue")) {
            query = "SELECT toimintaalue_id FROM toimintaalue WHERE nimi='" + taNimi + "'";
        } else {
            return ta_id;
        }
        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);

            while (queryResult.next()) {
                ta_id = queryResult.getInt("toimintaalue_id");
            }
            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ta_id;
    }

    /*
        Metodi peruuta-napin toiminnolle.
    */
    @FXML
    private void peruutaToiminto(ActionEvent event) {
        closeStage(event);
        lblHallintaNotification.setText("Mökin lisäys peruutettu!");
        System.out.println("Mökin lisäys peruutettu!");
    }

    /*
        Metodi, joka sulkee dialokin/ikkunan.
    */
    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
