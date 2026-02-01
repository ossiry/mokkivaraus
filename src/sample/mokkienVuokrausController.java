package sample;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

public class mokkienVuokrausController implements Initializable {

    public mokkienVuokrausController() {

    }

    // Alustetaan connectClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    // Tehdään mukautetut näppäimet dialokeihin
    ButtonType jatka = new ButtonType("Jatka", ButtonBar.ButtonData.OK_DONE);
    ButtonType peruuta = new ButtonType("Peruuta", ButtonBar.ButtonData.CANCEL_CLOSE);

    @FXML
    private Label lblHallintaNotification;
    @FXML
    TextField varausIDHaku, asiakasIDHaku, mokkiNimiHaku;
    @FXML
    DatePicker dpVarausHaku;
    @FXML
    Button haeMokki, haeKaikki, varausMuokkaa, varausPoista;
    @FXML
    ChoiceBox<String> cbAlue;
    @FXML
    TableView<Vuokraus> mokkiTiedot;
    @FXML
    TableColumn<Mokki, Integer> idColumn;
    @FXML
    TableColumn<Mokki, String> varattupvmColumn, vahvistuspvmColumn, varauksenalkupvmColumn, varauksenloppupvmColumn, asiakasidColumn, mokkinimiColumn, alueColumn, kestoColumn;
    @FXML
    ListView<String> taPalvelutLista;

    // Alustetaan taulukkoon sarakkeet
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("varaus_id"));
        varattupvmColumn.setCellValueFactory(new PropertyValueFactory<>("varattu_pvm"));
        vahvistuspvmColumn.setCellValueFactory(new PropertyValueFactory<>("vahvistus_pvm"));
        varauksenalkupvmColumn.setCellValueFactory(new PropertyValueFactory<>("varattu_alkupvm"));
        varauksenloppupvmColumn.setCellValueFactory(new PropertyValueFactory<>("varattu_loppupvm"));
        asiakasidColumn.setCellValueFactory(new PropertyValueFactory<>("asiakas_id"));
        mokkinimiColumn.setCellValueFactory(new PropertyValueFactory<>("mokkinimi"));
        alueColumn.setCellValueFactory(new PropertyValueFactory<>("nimi"));
        kestoColumn.setCellValueFactory(new PropertyValueFactory<>("kestoaika"));
        tekstinTasaus(mokkinimiColumn);
        dpVarausHaku.setValue(LocalDate.now());
        dpVarausHaku.setShowWeekNumbers(false);

        mokkiTiedot.setRowFactory(tv -> new TableRow<Vuokraus>() {
            @Override
            public void updateItem(Vuokraus item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getVahvistus()) {
                    setStyle("-fx-background-color: LimeGreen;");
                    if (item.getLaskutus()) {
                        setStyle("-fx-background-color: DeepSkyBlue;");
                    }
                } else if (!item.getVahvistus()) {
                    setStyle("-fx-background-color: yellow;");
                    if (LocalDate.now().compareTo(LocalDate.parse(item.getVahvistus_pvm())) > 0) {
                        setStyle("-fx-background-color: red;");
                    }
                } else {
                    setStyle("");
                }
            }
        });

        haeAktiiviset();
    }

    private void tekstinTasaus(TableColumn<Mokki, String> col) {
        col.setCellFactory(tc -> {
            TableCell<Mokki, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(col.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    public void varauksenValinta() {

        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();
            taPalvelutLista.setItems(haePalvelut(vuokraus));
        }
    }

    public ObservableList<String> haePalvelut(Vuokraus vuokraus) {

        ObservableList<String> palvelut = FXCollections.observableArrayList();

        String query = "SELECT p.nimi, vp.lkm FROM palvelu p INNER JOIN varauksen_palvelut vp ON " +
                "p.palvelu_id = vp.palvelu_id WHERE vp.varaus_id = " + vuokraus.getVaraus_id();

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStatement.executeQuery(query);

            while (queryResult.next()) {
                String palvelu = queryResult.getString("p.nimi") + ", " + queryResult.getInt("vp.lkm") + " kpl";
                palvelut.add(palvelu);
            }

            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return palvelut;
    }

    @FXML
    public void pudotusValikko(MouseEvent event) {

        ObservableList<String> toimintaAlueet = haeToimintaAlueet();
        cbAlue.setItems(toimintaAlueet);
        cbAlue.setValue("Valitse toiminta-alue");
    }

    @FXML
    public void haeArkistoidut() {

        String query = "SELECT v.varaus_id, v.varattu_pvm, v.vahvistus_pvm, v.varattu_alkupvm, " +
                "v.varattu_loppupvm, v.asiakas_id, v.vahvistettu, v.laskutettu, m.mokki_id, m.mokkinimi, ta.nimi " +
                "FROM varaus v INNER JOIN mokki m ON v.mokki_mokki_id = m.mokki_id INNER JOIN toimintaalue ta ON " +
                "m.toimintaalue_id = ta.toimintaalue_id WHERE v.laskutettu = " + true;

        mokkiHaku(query);
    }

    @FXML
    public void haeAktiiviset() {

        String query = "SELECT v.varaus_id, v.varattu_pvm, v.vahvistus_pvm, v.varattu_alkupvm, " +
                "v.varattu_loppupvm, v.asiakas_id, v.vahvistettu, v.laskutettu, m.mokki_id, m.mokkinimi, ta.nimi " +
                "FROM varaus v INNER JOIN mokki m ON v.mokki_mokki_id = m.mokki_id INNER JOIN toimintaalue ta ON " +
                "m.toimintaalue_id = ta.toimintaalue_id WHERE v.laskutettu = " + false;

        mokkiHaku(query);
    }

    @FXML
    public ObservableList<String> haeToimintaAlueet() {

        ObservableList<String> toimintaAlueet = FXCollections.observableArrayList();

        String query = "SELECT * FROM toimintaalue"; // SQL-lause kaikkien toiminta-alueiden hakemiseen.

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while (queryResult.next()) {
                toimintaAlueet.add(queryResult.getString("nimi"));
            }
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return toimintaAlueet;
    }

    public void mokkiHaku(String SQL) {

        ObservableList<Vuokraus> vuokraus = FXCollections.observableArrayList();
        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(SQL);
            ResultSet queryResult = preparedStmt.executeQuery(SQL);
            System.out.println(queryResult);

            while (queryResult.next()) {
                int varaus_id = queryResult.getInt("v.varaus_id");
                int asiakasid = queryResult.getInt("v.asiakas_id");
                int mokki_id = queryResult.getInt("m.mokki_id");
                String mokkinimi = queryResult.getString("m.mokkinimi");
                String toimintaalue = queryResult.getString("ta.nimi");
                String varattupvm = queryResult.getDate("v.varattu_pvm").toString();
                String vahvistuspvm = queryResult.getDate("v.vahvistus_pvm").toString();
                String varattualkupvm = queryResult.getDate("v.varattu_alkupvm").toString();
                String varattuloppupvm = queryResult.getDate("v.varattu_loppupvm").toString();
                boolean vahvistettu = queryResult.getBoolean("v.vahvistettu");
                boolean laskutettu = queryResult.getBoolean("v.laskutettu");
                long kesto = Math.abs(queryResult.getDate("v.varattu_loppupvm").getTime() - queryResult.getDate("v.varattu_alkupvm").getTime());
                long kestoaika = (TimeUnit.DAYS.convert(kesto, TimeUnit.MILLISECONDS)) + 1;

                vuokraus.add(new Vuokraus(varaus_id, mokki_id, varattupvm, vahvistuspvm, varattualkupvm, varattuloppupvm,
                        asiakasid, mokkinimi, toimintaalue, vahvistettu, kestoaika, laskutettu));

            }
            mokkiTiedot.setItems(vuokraus);
            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void haeEhdoilla() {

        int id = -1;
        int asiakas = -1;
        String alue = cbAlue.getValue();
        String nimi = mokkiNimiHaku.getText();
        java.sql.Date pvmHaku = java.sql.Date.valueOf(dpVarausHaku.getValue());

        try {
            id = Integer.parseInt(varausIDHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        try {
            asiakas = Integer.parseInt(asiakasIDHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        String query = "SELECT v.varaus_id, v.varattu_pvm, v.vahvistus_pvm, v.varattu_alkupvm, v.varattu_loppupvm, v.asiakas_id, " +
                "v.vahvistettu, v.laskutettu, m.mokkinimi, m.mokki_id, ta.nimi FROM varaus v JOIN mokki m ON v.mokki_mokki_id = m.mokki_id JOIN " +
                "toimintaalue ta ON m.toimintaalue_id = ta.toimintaalue_id WHERE ";

        if (id > 0) {
            query += "v.varaus_id = " + id;
            if (asiakas > 0) {
                query += " AND v.asiakas_id = " + asiakas;
            }
            if (!alue.equals("Valitse toiminta-alue")) {
                query += " AND ta.nimi = '" + alue + "'";
            }
            if (!nimi.equals("")) {
                query += " AND m.mokkinimi = '" + nimi + "'";
            }
            if (dpVarausHaku.getValue() != null) {
                query += " AND v.varattu_alkupvm = '" + pvmHaku + "'";
            }
        } else if (asiakas > 0) {
            query += "v.asiakas_id = " + asiakas;
            if (!alue.equals("Valitse toiminta-alue")) {
                query += " AND ta.nimi = '" + alue + "'";
            }
            if (!nimi.equals("")) {
                query += " AND m.mokkinimi = '" + nimi + "'";
            }
            if (dpVarausHaku.getValue() != null) {
                query += " AND v.varattu_alkupvm = '" + pvmHaku + "'";
            }
        } else if (!alue.equals("Valitse toiminta-alue")) {
            query += "ta.nimi = '" + alue + "'";
            if (!nimi.equals("")) {
                query += " AND m.mokkinimi = '" + nimi + "'";
            }
            if (dpVarausHaku.getValue() != null) {
                query += " AND v.varattu_alkupvm = '" + pvmHaku + "'";
            }
        } else if (!nimi.equals("")) {
            query += "m.mokkinimi = '" + nimi + "'";
            if (dpVarausHaku.getValue() != null) {
                query += " AND v.varattu_alkupvm = '" + pvmHaku + "'";
            }
        } else if (dpVarausHaku.getValue() != null) {
            query += "v.varattu_alkupvm = '" + pvmHaku + "'";
        }

        if (id > 0 || asiakas > 0 || !alue.equals("Valitse toiminta-alue") || !nimi.equals("") || dpVarausHaku.getValue() != null) {
            mokkiHaku(query);
        } else {
            System.out.println("Haku epäonnistui");
        }
    }

    /*
        Muokkaa-napin toiminto kutsuu metodin, joka avaa dialokin taulukosta valitun mökin tietojen muuttamiseksi.
        Jos mitään mökkiä ei ole valittu, metodi ei tee mitään.
     */
    @FXML
    public void mokinMuokkaus(ActionEvent event) throws IOException {
        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            openDialog("update", "Muuta vuokrauksen tietoja");
        }
    }

    /*
        Poista-napin toiminto, jolla voidaan poistaa valittu mökki tietokannasta.
        Jos mitään mökkiä ei ole valittu, metodi ei tee mitään. Sisältää vahvistus-dialokin, josta toiminnon voi perua.
     */

    @FXML
    public void mokinPoisto(ActionEvent event) {

        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {

            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();
            boolean result = false;
            int delete_varaus_id = vuokraus.getVaraus_id();

            String query = "SELECT * FROM varauksen_palvelut WHERE varaus_id = " + delete_varaus_id;

            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(query);
                ResultSet queryResult = preparedStmt.executeQuery(query);
                result = queryResult.next(); // True, jos query palauttaa rivin.
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            vahvistusPoistolle(delete_varaus_id);

          /*  if (result) { // Tämä turha, jos poistaa myös varauksen palvelut samalla

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Huomautus");
                alert.setHeaderText("Toiminto ei onnistunut!");
                alert.setContentText("Varausta ei voi poistaa tietokannasta, koska siihen liittyy palveluita.");
                alert.showAndWait();
            } */
        }
    }

    @FXML
    public void openDialog(String toiminto, String otsikko) throws IOException {

        if (toiminto.equals("update")) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/paivitaVuokraus.fxml"));
            Parent parent = fxmlLoader.load();
            vuokrausDialogController controller = fxmlLoader.getController();
            controller.setVuokrausController(this);
            controller.setVuokrausObservableList(vuokraus);
            Scene scene = new Scene(parent, 700, 230);
            Stage stage = new Stage();
            stage.setTitle(otsikko);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.getScene().getWindow().centerOnScreen();
            stage.showAndWait();
        }
    }

    /*
        Metodi hakee tietokannasta toiminta-alueiden nimet ja palauttaa ne listana.
     */

    private void vahvistusPoistolle(int id) {

        // Muodostetaan SQL-lause mökin tai toimialueen poistoon.
        String delPalvelut;
        String delVaraus;

        delPalvelut = "DELETE FROM varauksen_palvelut WHERE varaus_id = " + id;
        delVaraus = "DELETE FROM varaus WHERE varaus_id = " + id;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", jatka, peruuta);
        alert.setTitle("Vahvistaminen");
        alert.getDialogPane().setHeaderText("Haluatko varmasti poistaa valinnan: " + id + "?");
        alert.getDialogPane().setContentText("Valitsemasi kohde poistuu tietokannasta.");
        Optional<ButtonType> confirmationResult = alert.showAndWait();
        if (confirmationResult.isPresent()) {
            if (confirmationResult.get() == jatka) {
                try {
                    PreparedStatement preparedStmt = connectDB.prepareStatement(delPalvelut);
                    preparedStmt.execute();

                    preparedStmt = connectDB.prepareStatement(delVaraus);
                    preparedStmt.execute();
                    System.out.println("Varaus (Id: " + id + ") on poistettu tietokannasta!");
                    // Haetaan lopuksi kaikki mökit tauluun
                    haeAktiiviset();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (confirmationResult.get() == peruuta) {
                System.out.println("Poisto peruutettu!");
            }
        }
    }

    @FXML
    public void mokinVahvistus() {

        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();

            boolean vahvistaVaraus = vuokraus.getVahvistus();

            if (!vahvistaVaraus) {
                String updQuery = "UPDATE varaus SET vahvistettu = true WHERE varaus_id = " + vuokraus.getVaraus_id();

                try {
                    PreparedStatement preparedStmt = connectDB.prepareStatement(updQuery);

                    int rowsUpdated = preparedStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        // lblHallintaNotification.setText("Varaus vahvistettiin onnistuneesti!");
                        System.out.println("Varaus vahvistettiin onnistuneesti!");
                        haeAktiiviset();
                    }
                    preparedStmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void laskunTekeminen() {

        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();

            double palveluMaksut = haePalveluMaksut(vuokraus);
            double alvProsentti = 0.10;
            double summa = (vuokraus.getKestoaika() * haeSumma(vuokraus)) + palveluMaksut;
            double alv = summa * alvProsentti;

            String insQuery = "INSERT INTO lasku (varaus_id, summa, alv) VALUES (?, ?, ?)";

            if (vuokraus.getVahvistus()) {
                try {
                    PreparedStatement preparedStmt = connectDB.prepareStatement(insQuery);
                    preparedStmt.setInt(1, vuokraus.getVaraus_id());
                    preparedStmt.setDouble(2, summa);
                    preparedStmt.setDouble(3, alv);

                    // Suoritetaan tietokantaan lisäys.
                    int rowsInserted = preparedStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        //lblHallintaNotification.setText("Mökki lisättiin tietokantaan!");
                        System.out.println("Lasku lisättiin tietokantaan!");
                        naytaLasku(vuokraus);
                        arkistoiVaraus(vuokraus);
                        haeArkistoidut();
                    }
                    preparedStmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                rekisterointiPonnahdusIkkuna.display("ERROR", "Varausta ei ole vahvistettu! Vahvista varaus ja kokeile uudelleen.");
            }
        }
    }

    public void naytaLasku(Vuokraus vuokraus) {
        String query = ("SELECT etunimi, sukunimi, lahiosoite, postinro FROM asiakas WHERE asiakas_id= " + vuokraus.getAsiakas_id());

        try {
            ResultSet rs = connectDB.createStatement().executeQuery(query);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/aktiivisetLaskut.fxml"));
            Parent root = loader.load();
            aktiivisetController aktiivisetController = loader.getController();
            LocalDate localDate = LocalDate.now();

            while (rs.next()) {
                aktiivisetController.etunimiLabel.setText(rs.getString(1));
                aktiivisetController.sukunimiLabel.setText(rs.getString(2));
                aktiivisetController.lahiosoiteLabel.setText(rs.getString(3));
                aktiivisetController.postinumeroLabel.setText(rs.getString(4));
                System.out.println(vuokraus.getKestoaika());
                System.out.println(haeSumma(vuokraus));
                System.out.println(haePalveluMaksut(vuokraus));
                aktiivisetController.summaLabel.setText(Double.toString((vuokraus.getKestoaika() * (haeSumma(vuokraus)) + haePalveluMaksut(vuokraus))));
                aktiivisetController.erapaivaLabel.setText(localDate.plusDays(14).toString());
                aktiivisetController.paivamaaraLabel.setText(localDate.toString());
                aktiivisetController.alkuaikaLabel.setText(vuokraus.getVarattu_alkupvm());
                aktiivisetController.loppuaikaLabel.setText(vuokraus.getVarattu_loppupvm());
                aktiivisetController.laskuIDLabel.setText(String.valueOf(vuokraus.getVaraus_id()));
                aktiivisetController.mokinNimiLabel.setText(vuokraus.getMokkinimi());
                aktiivisetController.varausLabel.setText(Integer.toString(vuokraus.getVaraus_id()));
            }

            //Luodaan laskutus näkymä
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mökkisovellus 5000");
            stage.show();

        } catch (IOException |
                SQLException e) {
            e.printStackTrace();
        }
    }

    private void arkistoiVaraus(Vuokraus vuokraus) {

        try {
            // Muodostetaan SQL-lause mökin päivittämiseen.
            String updQuery = "UPDATE varaus SET laskutettu = true WHERE varaus_id = " + vuokraus.getVaraus_id();

            PreparedStatement preparedStmt = connectDB.prepareStatement(updQuery);

            // Suoritetaan tietokantaan lisäys.
            int rowsUpdated = preparedStmt.executeUpdate();
            if (rowsUpdated > 0) {
                // lblHallintaNotification.setText("Mökkiä muokattiin onnistuneesti!");
                System.out.println("Varaus arkistoitu!");
            }
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double haeSumma(Vuokraus vuokraus) {

        double summa = 0;
        String query = "SELECT vrk_hinta FROM mokki WHERE mokki_id = " + vuokraus.getMokki_id();

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);

            while (queryResult.next()) {
                summa += queryResult.getInt("vrk_hinta");
            }

            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return summa;
    }

    public double haePalveluMaksut(Vuokraus vuokraus) {

        double palveluMaksut = 0;
        String haePalveluMaksut = "SELECT p.hinta, p.alv, vp.lkm FROM palvelu p INNER JOIN varauksen_palvelut vp ON " +
                "p.palvelu_id = vp.palvelu_id INNER JOIN varaus v ON vp.varaus_id = v.varaus_id WHERE v.varaus_id = "
                + vuokraus.getVaraus_id();

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(haePalveluMaksut);
            ResultSet queryResult = preparedStatement.executeQuery(haePalveluMaksut);

            while (queryResult.next()) {
                palveluMaksut += queryResult.getInt("vp.lkm") * queryResult.getDouble("p.hinta");
            }

            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return palveluMaksut;
    }

    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
