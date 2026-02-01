package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class varausDialogController implements Initializable {

    @FXML
    public TableColumn<Palvelu, String> palvekuNimiColumn, palveluLkmColumn;
    @FXML
    public TableColumn<Palvelu, Double> palvekuHintaColumn, palveluAlvColumn;

    private boolean lippu;
    private int asiakas_id;
    private int mokki_id;
    private int vahvistus;
    private String toimintaalue;
    ObservableList<Palvelu> palvelut = FXCollections.observableArrayList();

    // Alustetaan connectClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    @FXML
    private DatePicker datePickerAlku, datePickerLoppu;
    @FXML
    private TextField tfNimi, tfVahvistus;
    @FXML
    private TableView<Palvelu> tableViewPalvelut;
    public paaNayttoController controller;
    public Label lblHallintaNotification, lblMokki, lblMokkiOsoite, lblHinta, lblOmistaja, lblAsiakasEmail, lblAsiakas,
            lblAsiakasPuhelin, lblValidateAsiakas, lblValidatePaivamaara, lblValidateVahvistus;

    public void setMokkiController(paaNayttoController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        datePickerAlku.setValue(LocalDate.now());
        datePickerLoppu.setValue(LocalDate.now());
        datePickerAlku.setShowWeekNumbers(false);
        datePickerLoppu.setShowWeekNumbers(false);
    }

    public void setPalvelutLista() {

        String query = "SELECT p.palvelu_id, p.nimi, p.hinta, p.alv FROM palvelu p INNER JOIN toimintaalue ta ON ta.toimintaalue_id = " +
                "p.toimintaalue_id WHERE ta.nimi = '" + this.toimintaalue + "'";

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);

            while (queryResult.next()) {
                int id = queryResult.getInt("p.palvelu_id");
                String nimi = queryResult.getString("p.nimi");
                double hinta = queryResult.getDouble("p.hinta");
                double alv = queryResult.getDouble("p.alv");
                String maara = "0";
                palvelut.add(new Palvelu(id, nimi, hinta, alv, maara));
            }

            palvekuNimiColumn.setCellValueFactory(
                    new PropertyValueFactory<Palvelu, String>("nimi"));
            palvekuHintaColumn.setCellValueFactory(
                    new PropertyValueFactory<Palvelu, Double>("hinta"));
            palveluAlvColumn.setCellValueFactory(
                    new PropertyValueFactory<Palvelu, Double>("alv"));
            palveluLkmColumn.setCellValueFactory(
                    new PropertyValueFactory<Palvelu, String>("maara"));
            palveluLkmColumn.setCellFactory(TextFieldTableCell.forTableColumn());

            tableViewPalvelut.setEditable(true);
            tableViewPalvelut.setItems(palvelut);

            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMokitObservableList(Mokki mokki) {

        String omistajaQuery = "SELECT etunimi, sukunimi FROM asiakas WHERE asiakas_id = " + mokki.getMokki_omistajaid();

        this.toimintaalue = mokki.getToiminta_alue();
        this.mokki_id = mokki.getMokki_id();
        lblMokki.setText(mokki.getMokki_nimi() + " (Id: " + this.mokki_id + ")");
        lblMokkiOsoite.setText(mokki.getMokki_osoite() + ", " + mokki.getMokki_postinro());
        lblHinta.setText(mokki.getMokki_hinta() + "€/vrk, " + mokki.getMokki_henkilot() + " henkilöä");

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(omistajaQuery);
            ResultSet queryResult = preparedStmt.executeQuery(omistajaQuery);

            while (queryResult.next()) {
                lblOmistaja.setText(queryResult.getString("etunimi") + " " + queryResult.getString("sukunimi") +
                        " (Id: " + mokki.getMokki_omistajaid() + ")");
            }
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPalvelutLista();
    }

    @FXML
    private void haeAsiakas(ActionEvent event) {

        if (tfNimi.getText().trim().isEmpty()) {
            lblAsiakas.setText("");
            lblAsiakasEmail.setText("Asiakasta ei löydy!");
            lblAsiakasPuhelin.setText("");
        } else {
            String asiakasQuery = "SELECT asiakas_id, etunimi, sukunimi, email, puhelinnro FROM asiakas WHERE " +
                    "asiakas_id = " + tfNimi.getText();
            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(asiakasQuery);
                ResultSet queryResult = preparedStmt.executeQuery(asiakasQuery);

                while (queryResult.next()) {
                    lblAsiakas.setText(queryResult.getString("etunimi") + " " + queryResult.getString("sukunimi"));
                    lblAsiakasEmail.setText(queryResult.getString("email"));
                    lblAsiakasPuhelin.setText(queryResult.getString("puhelinnro"));
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Metodi peruuta-napin toiminnolle.
*/
    @FXML
    private void peruutaToiminto(ActionEvent event) {
        closeStage(event);
        lblHallintaNotification.setText("Varauksen tekeminen peruutettu!");
        System.out.println("Varauksen tekeminen peruutettu!");
    }

    /*
        Metodi, joka sulkee dialokin/ikkunan.
    */
    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void btnLisaaVaraus(ActionEvent event) {

        System.out.println("Lisää-nappia painettu 'Tee varaus'-dialokissa.");
        this.lippu = false;

        java.sql.Date alkupvm = java.sql.Date.valueOf(datePickerAlku.getValue());
        java.sql.Date loppupvm = java.sql.Date.valueOf(datePickerLoppu.getValue());

        vahvistusPvm();
        validateAsiakas();
        vahvistusPvm();
        validatePaivamaarat();

        if (!lippu) {
            try {
                String insQuery = "INSERT INTO varaus (asiakas_id, mokki_mokki_id, varattu_pvm, vahvistus_pvm, varattu_alkupvm, varattu_loppupvm) " +
                        "VALUES (" + this.asiakas_id + ", " + this.mokki_id + ", CURDATE(), " + "DATE_SUB('" + alkupvm + "', INTERVAL " + this.vahvistus +
                        " DAY), '" + alkupvm + "', '" + loppupvm + "')";

                PreparedStatement preparedStmt = connectDB.prepareStatement(insQuery);

                // Suoritetaan tietokantaan lisäys.
                int rowsInserted = preparedStmt.executeUpdate();
                if (rowsInserted > 0) {
                    lisaaPalvelut();
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            closeStage(event);
        }
    }

    private void lisaaPalvelut() {

        for (Palvelu palvelu : palvelut) {
            try {
                String insPalvelut = "INSERT INTO varauksen_palvelut (varaus_id, palvelu_id, lkm) VALUES (LAST_INSERT_ID(), ?, ?)";
                PreparedStatement preparedStmt = connectDB.prepareStatement(insPalvelut);
                preparedStmt.setInt(1, palvelu.getPalvelu_id());
                preparedStmt.setInt(2, Integer.parseInt(palvelu.getMaara()));

                int rowsInserted = preparedStmt.executeUpdate();
                if (rowsInserted > 0) {
                    lblHallintaNotification.setText("Varaus tehtiin onnistuneesti!");
                    System.out.println("Varaus tehtiin onnistuneesti!");
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void validateAsiakas() {

        if (tfNimi.getText() == null || tfNimi.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateAsiakas.setText("Asiakasta ei ole annettu!");
        } else {
            try {
                this.asiakas_id = Integer.parseInt(tfNimi.getText().trim());
                if (checkAsiakasId(asiakas_id)) {
                    lblValidateAsiakas.setText("");
                } else {
                    this.lippu = true;
                    lblValidateAsiakas.setText("Asiakasta ei löydy tietokannasta!");
                }
            } catch (NumberFormatException e) {
                this.lippu = true;
            }
        }
    }

    private boolean checkAsiakasId(int asiakas_id) {

        String query = "SELECT asiakas_id FROM asiakas WHERE asiakas_id = '" + asiakas_id + "'";
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

    private void vahvistusPvm() {

        if (tfVahvistus.getText() != null || !tfVahvistus.getText().trim().isEmpty()) {
            try {
                this.vahvistus = Integer.parseInt(tfVahvistus.getText().trim());
                if (this.vahvistus < 0) {
                    lblValidateVahvistus.setText("Tarkista vahvistuspvm!");
                    this.lippu = true;
                }
            } catch (Exception ignored) {
            }
        } else {
            this.vahvistus = 5;
        }
    }

    private void validatePaivamaarat() {

        int laskuri = 0;
        List<LocalDate> alkupaivat = new ArrayList<>();
        List<LocalDate> loppupaivat = new ArrayList<>();
        Date alkupvm;
        Date loppupvm;

        String query = "SELECT varattu_alkupvm, varattu_loppupvm FROM varaus WHERE mokki_mokki_id = " + this.mokki_id;

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);

            while (queryResult.next()) {
                alkupvm = queryResult.getTimestamp("varattu_alkupvm");
                loppupvm = queryResult.getTimestamp("varattu_loppupvm");

                LocalDate localAlku = alkupvm.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localLoppu = loppupvm.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                alkupaivat.add(localAlku);
                loppupaivat.add(localLoppu);
            }
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalDate start = datePickerAlku.getValue();
        LocalDate end = datePickerLoppu.getValue();
        List<LocalDate> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start);
            start = start.plusDays(1);
        }

        for (LocalDate date : totalDates) {
            if (alkupaivat.contains(date) || loppupaivat.contains(date)) {
                laskuri++;
                this.lippu = true;
            }
        }

        if(laskuri > 0) {
            lblValidatePaivamaara.setText("Annettuina päivämäärinä löytyy ja aiempi varaus!");
            this.lippu = true;
        } else if ((datePickerAlku.getValue().compareTo(datePickerLoppu.getValue()) > 0)
                || (datePickerAlku.getValue().compareTo(LocalDate.now()) < 0)
                || (datePickerLoppu.getValue().compareTo(LocalDate.now()) < 0)) {
            lblValidatePaivamaara.setText("Tarkista päivämäärät!");
            this.lippu = true;
        } else {
            lblValidatePaivamaara.setText("");
        }
    }

    public void onEditChanged(TableColumn.CellEditEvent<Palvelu, String> palveluStringCellEditEvent) {

        Palvelu palvelu = tableViewPalvelut.getSelectionModel().getSelectedItem();
        palvelu.setMaara(palveluStringCellEditEvent.getNewValue());
    }
}
