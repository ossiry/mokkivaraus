package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class vuokrausDialogController {

    @FXML
    private ChoiceBox<String> cbToimintaalue;
    @FXML
    private TextField tfNimi, tfOsoite, tfPostinro, tfHenkilomaara, tfHinta, tfOmistajaId;
    @FXML
    private TextArea taVarustelu, taKuvaus;
    @FXML
    private Label lblValidateNimi, lblValidateOsoite, lblValidateToimintaalue, lblValidatePostinro, lblValidateHinta,
            lblValidateHenkilomaara, lblValidateOmistajaid, lblValidateVarustelu, lblValidateKuvaus;
    @FXML
    private DatePicker dpVarauspvm, dpVahvistuspvm, dpVarauksenAlku, dpVarauksenLoppu;


    // Alustetaan connectClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    private int varaus_id;
    public mokkienVuokrausController controller;

    public void setVuokrausController(mokkienVuokrausController controller) {
        this.controller = controller;
    }

    public void setVuokrausObservableList(Vuokraus vuokraus) {
        dpVarauspvm.setValue(LocalDate.parse(vuokraus.getVarattu_pvm()));
        dpVahvistuspvm.setValue(LocalDate.parse(vuokraus.getVahvistus_pvm()));
        dpVarauksenAlku.setValue(LocalDate.parse(vuokraus.getVarattu_alkupvm()));
        dpVarauksenLoppu.setValue(LocalDate.parse(vuokraus.getVarattu_loppupvm()));
        varaus_id = vuokraus.getVaraus_id();

    }
    @FXML
    public void paivitaVuokraus (ActionEvent event) {

        System.out.println("Hyväksy-nappia painettu Muokkaa-mökkiä dialokissa.");
            try {
                java.sql.Date varattu_pvm = java.sql.Date.valueOf(dpVarauspvm.getValue());
                java.sql.Date vahvistuspvm = java.sql.Date.valueOf(dpVahvistuspvm.getValue());
                java.sql.Date varauksenalku = java.sql.Date.valueOf(dpVarauksenAlku.getValue());
                java.sql.Date varauksenloppu = java.sql.Date.valueOf(dpVarauksenLoppu.getValue());
                // Muodostetaan SQL-lause mökin päivittämiseen.
                String updQuery = "UPDATE varaus SET varattu_pvm=?, vahvistus_pvm=?, varattu_alkupvm=?, varattu_loppupvm=?" +
                        "WHERE varaus_id=" + varaus_id;

                PreparedStatement preparedStmt = connectDB.prepareStatement(updQuery);
                preparedStmt.setDate(1, varattu_pvm);
                preparedStmt.setDate(2, vahvistuspvm);
                preparedStmt.setDate(3, varauksenalku);
                preparedStmt.setDate(4, varauksenloppu);

                // Suoritetaan tietokantaan lisäys.
                int rowsUpdated = preparedStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Mökkiä muokattiin onnistuneesti!");
                    // Haetaan päivityksen jälkeen kaikki mökit taululle.
                    // TODO hae vain päivitetty mökki?
                    controller.haeAktiiviset();
                }
                preparedStmt.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            closeStage(event);
        }
    @FXML
    private void peruutaToiminto(ActionEvent event) {
        closeStage(event);
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
