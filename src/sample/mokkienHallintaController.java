package sample;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import sample.connectivity.connectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class mokkienHallintaController implements Initializable {

    public mokkienHallintaController() {
    }

    // Alustetaan connectionClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    // Tehdään mukautetut näppäimet dialokeihin
    ButtonType jatka = new ButtonType("Jatka", ButtonBar.ButtonData.OK_DONE);
    ButtonType peruuta = new ButtonType("Peruuta", ButtonBar.ButtonData.CANCEL_CLOSE);

    @FXML
    private Label lblHallintaNotification;
    @FXML
    private ChoiceBox<String> taChoiceBox;
    @FXML
    private TextField tfIdHaku, tfOmistajaHaku, tfPostiHaku, tfSanaHaku;
    @FXML
    private ListView<String> taListView;
    @FXML
    private TableView<Mokki> mokkiTableView;
    @FXML
    public TableColumn<Mokki, Integer> mokki_idColumn, mokki_henkilotColumn, mokki_hintaColumn, mokki_omistajaidColumn;
    @FXML
    private TableColumn<Mokki, String> mokki_alueColumn, mokki_nimiColumn, mokki_osoiteColumn, mokki_postinroColumn,
            mokki_varusteluColumn, mokki_kuvausColumn;

    // Alustetaan taulukkoon sarakkeet
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mokki_idColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_id"));
        mokki_alueColumn.setCellValueFactory(new PropertyValueFactory<>("toiminta_alue"));
        mokki_nimiColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_nimi"));
        mokki_osoiteColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_osoite"));
        mokki_postinroColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_postinro"));
        mokki_henkilotColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_henkilot"));
        mokki_hintaColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_hinta"));
        mokki_varusteluColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_varustelu"));
        mokki_kuvausColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_kuvaus"));
        mokki_omistajaidColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_omistajaid"));
        tekstinTasaus(mokki_osoiteColumn);
        tekstinTasaus(mokki_varusteluColumn);
        tekstinTasaus(mokki_kuvausColumn);

        // Haetaan toiminta-alueet listalle näytön avautuessa.
        haeKaikki("alueet");
    }

    /*
        Metodi tasaa parametrina annetun taulukon sarakkeen sisältämän tekstin.
     */
    public static void tekstinTasaus(TableColumn<Mokki, String> col) {
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

    /*
        Metodi toiminta-alueiden hakuun. Metodi suorittuu alasvetovalikkoa hiirellä painettaessa.
     */
    @FXML
    public void pudotusValikko(MouseEvent event) {

        ObservableList<String> toimintaAlueet = haeToimintaAlueet();
        taChoiceBox.setItems(toimintaAlueet);
        taChoiceBox.setValue("Valitse toiminta-alue");
    }

    /*
        Metodilla haetaan näytölle mökkejä tietokannasta tekstikentissä syötetyillä tiedoilla.
     */
    @FXML
    public void haeEhdoilla() {

        int id = -1;
        int omistaja = -1;
        String alue = taChoiceBox.getValue();
        String postinro = tfPostiHaku.getText().trim();

        try {
            id = Integer.parseInt(tfIdHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        try {
            omistaja = Integer.parseInt(tfOmistajaHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        String query = "SELECT m.mokki_id, ta.nimi, m.postinro, m.mokkinimi, m.katuosoite, m.kuvaus, " +
                "m.henkilomaara, m.varustelu, m.vrk_hinta, m.omistaja_id FROM mokki m INNER JOIN toimintaalue ta" +
                " ON m.toimintaalue_id=ta.toimintaalue_id WHERE ";

        if (id > 0) {
            query += "m.mokki_id = " + id;
            if (!alue.equals("Valitse toiminta-alue")) {
                query += " AND ta.nimi = '" + alue + "'";
            }
            if (!postinro.equals("")) {
                query += " AND m.postinro = " + postinro;
            }
            if (omistaja > 0) {
                query += " AND m.omistaja_id = " + omistaja;
            }
        }
        else if (!alue.equals("Valitse toiminta-alue")) {
            System.out.println(alue);
            query += "ta.nimi = '" + alue + "'";
            if (!postinro.equals("")) {
                query += " AND m.postinro = " + postinro;
            }
            if (omistaja > 0) {
                    query += " AND m.omistaja_id = " + omistaja;
            }
        }
        else if (!postinro.equals("")) {
            query += "m.postinro = " + postinro;
            if (omistaja > 0) {
                query += " AND m.omistaja_id = " + omistaja;
            }
        }
        else if (omistaja > 0) {
            query += "m.omistaja_id = " + omistaja;
        }

        if (id > 0 || !alue.equals("Valitse toiminta-alue") || !postinro.equals("") || omistaja > 0) {
            mokkiHaku(query);
            lblHallintaNotification.setText("Haku onnistui!");
        } else {
            lblHallintaNotification.setText("Hakuehdot puuttuvat tai ne on syötetty väärin!");
        }
    }

    @FXML
    public void haeKaikkiButtonOnAction() {
        haeKaikki("kaikki");
    }

    /*
        Metodilla haetaan kaikki mökit sekä toiminta-alueet tietokannasta.
        Mökit esitetään näytöllä TableView-elementissä ja toiminta-alueet ListView-elementissä.
     */
    @FXML
    public void haeKaikki(String haettava) {

        ObservableList<String> alueet = haeToimintaAlueet();

        String query = "SELECT m.mokki_id, ta.nimi, m.postinro, m.mokkinimi, m.katuosoite, m.kuvaus, " +
                "m.henkilomaara, m.varustelu, m.vrk_hinta, m.omistaja_id " +
                "FROM mokki m INNER JOIN toimintaalue ta ON m.toimintaalue_id=ta.toimintaalue_id";

        switch (haettava) {
            case "kaikki":
                taListView.setItems(alueet);
                mokkiHaku(query);
                break;
            case "mokit":
                mokkiHaku(query);
                break;
            case "alueet":
                taListView.setItems(alueet);
                break;
        }
    }

    /*
        Sanahaku. Metodi suorittuu kun Sanahaku-tekstikentässä painetaan ENTER.
     */
    @FXML
    public void onEnterPressed(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ENTER)) {

            String sana = tfSanaHaku.getText().trim();
            String query = "SELECT m.mokki_id, ta.nimi, m.postinro, m.mokkinimi, m.katuosoite, m.kuvaus, " +
                    "m.henkilomaara, m.varustelu, m.vrk_hinta, m.omistaja_id FROM mokki m INNER JOIN toimintaalue ta" +
                    " ON m.toimintaalue_id=ta.toimintaalue_id WHERE mokkinimi LIKE '%" + sana + "%' OR katuosoite LIKE" +
                    " '%" + sana + "%' OR varustelu LIKE '%" + sana + "%' OR kuvaus LIKE '%" + sana + "%'";

            mokkiHaku(query);
        }
    }

    /*
        Metodi Takaisin-napin toiminnalle, jolla siirrytään takaisin päänäyttöön.
     */
    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/paaNaytto.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /*
        Metodi suorittaa parametrina saamansa SQL-lauseen ja muodostaa tietokannan palauttamista tiedoista
        Mokki lista-olion.
     */
    public void mokkiHaku(String SQL) {

        ObservableList<Mokki> mokki = FXCollections.observableArrayList();
        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(SQL);
            ResultSet queryResult = preparedStmt.executeQuery(SQL);

            while (queryResult.next()) {
                int id = queryResult.getInt("m.mokki_id");
                String ta = queryResult.getString("ta.nimi");
                String postinro = queryResult.getString("m.postinro");
                String nimi = queryResult.getString("m.mokkinimi");
                String osoite = queryResult.getString("m.katuosoite");
                String kuvaus = queryResult.getString("m.kuvaus");
                int henkilot = queryResult.getInt("m.henkilomaara");
                String varustelu = queryResult.getString("m.varustelu");
                int hinta = queryResult.getInt("m.vrk_hinta");
                int omistajaid = queryResult.getInt("m.omistaja_id");

                mokki.add(new Mokki(id, ta, nimi, osoite, postinro, henkilot, hinta, varustelu, kuvaus, omistajaid));
            }
            mokkiTableView.setItems(mokki);
            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Lisää-napin toiminto kutsuu metodin, joka avaa dialokin mökin tietojen lisäämiseksi.
     */
    @FXML
    public void mokinLisays(ActionEvent event) throws IOException {
        openDialog("add", "Lisää mökki");
    }

    /*
        Muokkaa-napin toiminto kutsuu metodin, joka avaa dialokin taulukosta valitun mökin tietojen muuttamiseksi.
        Jos mitään mökkiä ei ole valittu, metodi ei tee mitään.
     */
    @FXML
    public void mokinMuokkaus(ActionEvent event) throws IOException {
        if (mokkiTableView.getSelectionModel().getSelectedItem() != null) {
            openDialog("update", "Muuta mökin tietoja");
        }
    }

    /*
        Poista-napin toiminto, jolla voidaan poistaa valittu mökki tietokannasta.
        Jos mitään mökkiä ei ole valittu, metodi ei tee mitään. Sisältää vahvistus-dialokin, josta toiminnon voi perua.
     */
    @FXML
    public void mokinPoisto(ActionEvent event) {

        if (mokkiTableView.getSelectionModel().getSelectedItem() != null) {
            Mokki mokki = mokkiTableView.getSelectionModel().getSelectedItem();

            int delete_mokki_id = mokki.getMokki_id();
            String delete_mokki_nimi = mokki.getMokki_nimi();
            boolean result = false;

            String query = "SELECT * FROM varaus WHERE mokki_mokki_id = " + delete_mokki_id;

            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(query);
                ResultSet queryResult = preparedStmt.executeQuery(query);
                result = queryResult.next(); // True, jos query palauttaa rivin.
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!result) {
                vahvistusPoistolle(delete_mokki_id, delete_mokki_nimi);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Huomautus");
                alert.setHeaderText("Toiminto ei onnistunut!");
                alert.setContentText("Mökkiä ei voi poistaa tietokannasta, koska siihen liittyy varauksia.");
                alert.showAndWait();
            }
        }
    }

    /*
        Metodi hakee tietokannasta toiminta-alueiden nimet ja palauttaa ne listana.
     */
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

    /*
        Metodi toiminta-aluiden lisäämiseksi tietokantaan.
    */
    @FXML
    public void lisaaToimintaAlue(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.getDialogPane().getButtonTypes().addAll(jatka, peruuta);
        dialog.getDialogPane().getButtonTypes().remove(0, 2);
        dialog.setTitle("Lisää toiminta-alue");
        dialog.setHeaderText("Lisätään uusi toiminta-alue tietokantaan.");
        dialog.setContentText("Toiminta-alueen nimi:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String alue = result.get();

            try {
                String check = "SELECT toimintaalue_id FROM toimintaalue WHERE nimi = '" + alue + "'";
                boolean loytyyko;

                // Tarkistetaan onko toiminta-alue jo tietokannassa.
                PreparedStatement preparedStmt = connectDB.prepareStatement(check);
                ResultSet queryResult = preparedStmt.executeQuery(check);
                loytyyko = queryResult.next(); // True, jos query palauttaa rivin.
                preparedStmt.close();

                if (!loytyyko) {
                    String insQuery = "INSERT INTO toimintaalue (nimi) VALUES ('" + alue + "')";
                    PreparedStatement preparedStmt2 = connectDB.prepareStatement(insQuery);
                    int rowsInserted = preparedStmt2.executeUpdate();
                    if (rowsInserted > 0) {
                        lblHallintaNotification.setText("Toiminta-alue lisättiin tietokantaan!");
                        System.out.println("Toiminta-alue lisättiin tietokantaan!");
                        // Haetaan lisäyksen jälkeen kaikki toiminta-alueet listalle.
                        haeKaikki("alueet");
                    }
                    preparedStmt.close();
                } else {
                    lblHallintaNotification.setText("Toiminta-alue löytyy jo tietokannasta!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Metodi avaa dialokin toiminta-alueen poistamiseksi tietokannasta.
     */
    @FXML
    public void poistaToimintaAlue(ActionEvent event) {

        ObservableList<String> valinnat = haeToimintaAlueet();
        boolean tulos = false;

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Valitse toiminta-alue", valinnat);
        dialog.getDialogPane().getButtonTypes().addAll(jatka, peruuta);
        dialog.getDialogPane().getButtonTypes().remove(0, 2);
        dialog.setTitle("Poista toiminta-alue");
        dialog.setHeaderText("Toiminta-alue poistetaan tietokannasta");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().equals("Valitse toiminta-alue")) {
            String delete_toimintaalue = result.get();

            String query = "SELECT p.palvelu_id FROM palvelu p INNER JOIN toimintaalue ta " +
                    "ON p.toimintaalue_id = ta.toimintaalue_id WHERE ta.nimi = '" + delete_toimintaalue + "'";

            String query2 = "SELECT m.mokki_id FROM mokki m INNER JOIN toimintaalue ta " +
                    "ON m.toimintaalue_id = ta.toimintaalue_id WHERE ta.nimi = '" + delete_toimintaalue + "'";

            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(query);
                ResultSet queryResult = preparedStmt.executeQuery(query);
                tulos = queryResult.next(); // True, jos query palauttaa rivin.
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(query2);
                ResultSet queryResult = preparedStmt.executeQuery(query2);
                tulos = queryResult.next(); // True, jos query palauttaa rivin.
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!tulos) {
                vahvistusPoistolle(-1, delete_toimintaalue);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Huomautus");
                alert.setHeaderText("Toiminto ei onnistunut!");
                alert.setContentText("Toiminta-aluetta ei voi poistaa tietokannasta, koska siihen liittyy palveluita tai mökkejä.");
                alert.showAndWait();
            }
        }
    }

    /*
        Metodi avaa dialokin johon mökkien tietoja voi lisätä/muuttaa.
     */
    @FXML
    public void openDialog(String toiminto, String otsikko) throws IOException {

        if (toiminto.equals("add")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/lisaaMokki.fxml"));
            Parent parent = fxmlLoader.load();
            mokkiDialogController mokkiController = fxmlLoader.getController();
            mokkiController.setMokkiController(this);
            mokkiController.lblHallintaNotification = lblHallintaNotification;
            Scene scene = new Scene(parent, 560, 415);
            Stage stage = new Stage();
            stage.setTitle(otsikko);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.getScene().getWindow().centerOnScreen();
            stage.showAndWait();
        }

        if (toiminto.equals("update")) {
            Mokki mokki = mokkiTableView.getSelectionModel().getSelectedItem();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/paivitaMokki.fxml"));
            Parent parent = fxmlLoader.load();
            mokkiDialogController mokkiController = fxmlLoader.getController();
            mokkiController.setMokkiController(this);
            mokkiController.lblHallintaNotification = lblHallintaNotification;
            mokkiController.setMokitObservableList(mokki);
            Scene scene = new Scene(parent, 560, 415);
            Stage stage = new Stage();
            stage.setTitle(otsikko);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.getScene().getWindow().centerOnScreen();
            stage.showAndWait();
        }
    }

    /*
        Metodi avaa varmistus-dialokin mökkiä tai toiminta-aluetta poistettaessa.
        Jos dialokista painaa "Jatka"-nappia, suoritetaan tietokannasta poistaminen SQL:n DELETE lausunto.
    */
    private void vahvistusPoistolle(int id, String nimi) {

        // Muodostetaan SQL-lause mökin tai toimialueen poistoon.
        String delQuery;

        if (id == -1) {
            delQuery = "DELETE FROM toimintaalue WHERE nimi = '" + nimi + "'"; // Nimi-kenttä on tietokannassa uniikki.
        } else {
            delQuery = "DELETE FROM mokki WHERE mokki_id = " + id;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", jatka, peruuta);
        alert.setTitle("Vahvistaminen");
        alert.getDialogPane().setHeaderText("Haluatko varmasti poistaa valinnan: " + nimi + "?");
        alert.getDialogPane().setContentText("Valitsemasi kohde poistuu tietokannasta.");
        Optional<ButtonType> confirmationResult = alert.showAndWait();
        if (confirmationResult.isPresent()) {
            if (confirmationResult.get() == jatka) {
                try {
                    PreparedStatement preparedStmt = connectDB.prepareStatement(delQuery);
                    preparedStmt.execute();
                    if (id == -1) {
                        // TODO tarkista voiko toiminta-alueen poistaa, vai onko sille fk-rajoitteita tietokannassa
                        lblHallintaNotification.setText("Toiminta-alue (" + nimi + ") on poistettu tietokannasta!");
                        System.out.println("Toiminta-alue (" + nimi + ") on poistettu tietokannasta!");
                        // Haetaan poiston jälkeen kaikki toiminta-alueet listalle
                        haeKaikki("alueet");
                    } else {
                        lblHallintaNotification.setText("Mökki (Id: " + id + " " + nimi + ") on poistettu tietokannasta!");
                        System.out.println("Mökki (Id: " + id + " " + nimi + ") on poistettu tietokannasta!");
                        // Haetaan lopuksi kaikki mökit tauluun
                        // TODO hae vain saman alueen mökit?
                        haeKaikki("mokit");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (confirmationResult.get() == peruuta) {
                lblHallintaNotification.setText("Poisto peruutettu!");
                System.out.println("Poisto peruutettu!");
            }
        }
    }
}


