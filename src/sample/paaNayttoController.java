package sample;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.*;
import java.util.*;

import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.connectivity.connectionClass;

public class paaNayttoController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    // Alustetaan connectionClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    @FXML
    private Label lblValittuMokki, lblHallintaNotification;
    @FXML
    private TextField tfIdHaku, tfHenkilomaaraHaku, tfPostiHaku, tfSanaHaku;
    @FXML
    private ChoiceBox<String> taChoiceBox;
    @FXML
    private Slider minHinta, maxHinta;
    @FXML
    public ListView<String> taListView;
    @FXML
    public DatePicker datePicker;
    @FXML
    private TableView<Mokki> mokkiTableView;
    @FXML
    private TableColumn<Mokki, Integer> mokki_idColumn, mokki_henkilotColumn, mokki_hintaColumn;
    @FXML
    private TableColumn<Mokki, String> mokki_alueColumn, mokki_nimiColumn, mokki_varusteluColumn, mokki_kuvausColumn;

    public paaNayttoController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mokki_idColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_id"));
        mokki_alueColumn.setCellValueFactory(new PropertyValueFactory<>("toiminta_alue"));
        mokki_nimiColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_nimi"));
        mokki_henkilotColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_henkilot"));
        mokki_hintaColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_hinta"));
        mokki_varusteluColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_varustelu"));
        mokki_kuvausColumn.setCellValueFactory(new PropertyValueFactory<>("mokki_kuvaus"));
        mokkienHallintaController.tekstinTasaus(mokki_nimiColumn);
        mokkienHallintaController.tekstinTasaus(mokki_varusteluColumn);
        mokkienHallintaController.tekstinTasaus(mokki_kuvausColumn);
        datePicker.setValue(LocalDate.now());
        datePicker.setShowWeekNumbers(false);
        haeKaikki();
    }

    @FXML
    public void haeKaikki() {

        String query = "SELECT m.mokki_id, ta.nimi, m.postinro, m.mokkinimi, m.katuosoite, m.kuvaus, " +
                "m.henkilomaara, m.varustelu, m.vrk_hinta, m.omistaja_id " +
                "FROM mokki m INNER JOIN toimintaalue ta ON m.toimintaalue_id=ta.toimintaalue_id";

        mokkiHaku(query);
        lblHallintaNotification.setText("Kaikki mökit haettu!");
    }

    public void mokinValinta() {

        if (mokkiTableView.getSelectionModel().getSelectedItem() != null) {

            Mokki mokki = mokkiTableView.getSelectionModel().getSelectedItem();
            taListView.setItems(haePalvelut(mokki));
            int id = mokki.getMokki_id();
            List<LocalDate> alkupaivat = new ArrayList<>();
            List<LocalDate> loppupaivat = new ArrayList<>();
            Date alkupvm;
            Date loppupvm;
            lblValittuMokki.setText(mokki.getMokki_nimi());

            String query = "SELECT v.varattu_alkupvm, v.varattu_loppupvm from mokki m INNER JOIN varaus v " +
                    "ON m.mokki_id = v.mokki_mokki_id WHERE m.mokki_id = " + id;

            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(query);
                ResultSet queryResult = preparedStmt.executeQuery(query);

                while (queryResult.next()) {
                    alkupvm = queryResult.getTimestamp("v.varattu_alkupvm");
                    loppupvm = queryResult.getTimestamp("v.varattu_loppupvm");

                    LocalDate localAlku = alkupvm.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate localLoppu = loppupvm.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    alkupaivat.add(localAlku);
                    loppupaivat.add(localLoppu);
                }
                preparedStmt.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            Callback<DatePicker, DateCell> dayCellFactory =
                    new Callback<>() {
                        @Override
                        public DateCell call(DatePicker datePicker) {
                            return new DateCell() {
                                @Override
                                public void updateItem(LocalDate item, boolean empty) {
                                    super.updateItem(item, empty);
                                    LocalDate itemAlku;
                                    LocalDate itemLoppu;
                                    LocalDate paivays = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    setStyle("-fx-background-color: #00FF00;");
                                    if (item.equals(paivays)) {
                                        setStyle("-fx-background-color: #00BFFF;");
                                    }
                                    for (int i = 0; i < alkupaivat.size(); i++) {
                                        itemAlku = alkupaivat.get(i);
                                        itemLoppu = loppupaivat.get(i);
                                        if (item.isAfter(itemAlku.minusDays(1)) && item.isBefore(itemLoppu.plusDays(1))) {
                                            setDisable(true);
                                            setStyle("-fx-background-color: #ffc0cb;");
                                        }
                                    }
                                }
                            };
                        }
                    };
            datePicker.setDayCellFactory(dayCellFactory);
            datePicker.show();
            datePicker.requestFocus();
        }
    }

    public ObservableList<String> haePalvelut(Mokki mokki) {

        ObservableList<String> palvelut = FXCollections.observableArrayList();

        String query = "SELECT p.nimi, p.hinta, p.alv FROM palvelu p INNER JOIN toimintaalue ta ON ta.toimintaalue_id = " +
                "p.toimintaalue_id WHERE ta.nimi = '" + mokki.getToiminta_alue() + "'";

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);

            while (queryResult.next()) {
                String nimi = queryResult.getString("p.nimi");
                Double hinta = queryResult.getDouble("p.hinta");
                // Double alv = queryResult.getDouble("p.alv");

                String palvelu = nimi + " (" +String.format("%.2f", hinta) + "/kpl)";

                palvelut.add(palvelu);
            }
            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return palvelut;
    }

    @FXML
    public void teeVaraus() throws IOException {

        if (mokkiTableView.getSelectionModel().getSelectedItem() != null) {
            Mokki mokki = mokkiTableView.getSelectionModel().getSelectedItem();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/lisaaVaraus.fxml"));
            Parent parent = fxmlLoader.load();
            varausDialogController varausController = fxmlLoader.getController();
            varausController.setMokkiController(this);
            varausController.lblHallintaNotification = lblHallintaNotification;
            varausController.setMokitObservableList(mokki);
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("Mökin varaaminen");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.getScene().getWindow().centerOnScreen();
            stage.showAndWait();
        }
    }

    @FXML
    public void pudotusValikko() {

        ObservableList<String> toimintaAlueet = haeToimintaAlueet();
        taChoiceBox.setItems(toimintaAlueet);
        taChoiceBox.setValue("Valitse toiminta-alue");
    }

    @FXML
    public ObservableList<String> haeToimintaAlueet() {

        ObservableList<String> toimintaAlueet = FXCollections.observableArrayList();

        String query = "SELECT * FROM toimintaalue";

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

    @FXML
    public void haeEhdoilla() {

        int id = -1;
        int henkilomaara = -1;
        double alaraja = minHinta.getValue();
        double ylaraja = maxHinta.getValue();
        String alue = taChoiceBox.getValue();
        String postinro = tfPostiHaku.getText().trim();

        try {
            id = Integer.parseInt(tfIdHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        try {
            henkilomaara = Integer.parseInt(tfHenkilomaaraHaku.getText().trim());
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
            if (henkilomaara > 0) {
                query += " AND m.henkilomaara >= " + henkilomaara;
            }
            if (alaraja > 0 || ylaraja < 300) {
                query += " AND m.vrk_hinta >= " + alaraja + " AND m.vrk_hinta <= " + ylaraja;
            }
        } else if (!alue.equals("Valitse toiminta-alue")) {
            System.out.println(alue);
            query += "ta.nimi = '" + alue + "'";
            if (!postinro.equals("")) {
                query += " AND m.postinro = " + postinro;
            }
            if (henkilomaara > 0) {
                query += " AND m.henkilomaara >= " + henkilomaara;
            }
            if (alaraja > 0 || ylaraja < 400) {
                query += " AND m.vrk_hinta >= " + alaraja + " AND m.vrk_hinta<= " + ylaraja;
            }
        } else if (!postinro.equals("")) {
            query += "m.postinro = " + postinro;
            if (henkilomaara > 0) {
                query += " AND m.henkilomaara >= " + henkilomaara;
            }
            if (alaraja > 0 || ylaraja < 300) {
                query += " AND m.vrk_hinta >= " + alaraja + " AND m.vrk_hinta <= " + ylaraja;
            }
        } else if (henkilomaara > 0) {
            query += "m.henkilomaara >= " + henkilomaara;
            if (alaraja > 0 || ylaraja < 300) {
                query += " AND m.vrk_hinta >= " + alaraja + " AND m.vrk_hinta <= " + ylaraja;
            }
        } else if (alaraja > 0 || ylaraja < 300) {
            query += " m.vrk_hinta >= " + alaraja + " AND m.vrk_hinta <= " + ylaraja;
        }

        if (id > 0 || !alue.equals("Valitse toiminta-alue") || !postinro.equals("") || henkilomaara > 0 || alaraja > 0 || ylaraja < 300) {
            mokkiHaku(query);
            lblHallintaNotification.setText("Haku onnistui!");
        } else {
            lblHallintaNotification.setText("Hakuehdot puuttuvat tai ne on syötetty väärin!");
        }
    }

    @FXML
    public void onEnterPressed(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ENTER)) {

            String sana = tfSanaHaku.getText().trim();
            String query = "SELECT m.mokki_id, ta.nimi, m.postinro, m.mokkinimi, m.katuosoite, m.kuvaus, " +
                    "m.henkilomaara, m.varustelu, m.vrk_hinta, m.omistaja_id FROM mokki m INNER JOIN toimintaalue ta" +
                    " ON m.toimintaalue_id=ta.toimintaalue_id WHERE mokkinimi LIKE '%" + sana + "%' OR katuosoite LIKE" +
                    " '%" + sana + "%' OR varustelu LIKE '%" + sana + "%' OR kuvaus LIKE '%" + sana + "%'";
            mokkiHaku(query);
            lblHallintaNotification.setText("Sanahaku tehty!");
        }
    }

    public void mokkiHaku(String query) {

        ObservableList<Mokki> mokki = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);

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

    public void switchToMokkienHallinta(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/mokkienHallinta.fxml")));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.getScene().getWindow().centerOnScreen();
        // this.stage.setMaximized(true);
        this.stage.show();
    }

    public void switchToMokkienVuokraus(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/mokkienVuokraus.fxml")));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void switchToPalveluidenHallinta(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/palveluidenHallinta.fxml")));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void switchToAsiakkaidenTietojenHallinta(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/asiakkaidenTietojenHallinta.fxml")));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void switchToLaskujenHallinta(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/laskutus.fxml")));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void switchToRaporttienHallinta(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("resources/raportit.fxml")));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    @FXML
    private void suljeSovellus() {
        Platform.exit();
    }
}