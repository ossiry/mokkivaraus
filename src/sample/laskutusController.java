package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class laskutusController {

    @FXML
    private Label etunimiLabel;

    @FXML
    private Label sukunimiLabel;

    @FXML
    private Label lahiosoiteLabel;

    @FXML
    private Label postinumeroLabel;

    @FXML
    private Label mokinNimiLabel;

    @FXML
    private Label mokinPostiNumeroLabel;

    @FXML
    private Label katuosoiteLabel;

    @FXML
    private Label palveluLabel;

    @FXML
    private Label sahkopostiLabel;

    @FXML
    private Label puhelinNumeroLabel;

    @FXML
    private TableView<lasku> tableView;

    @FXML
    private TableColumn<lasku, Integer> laskuIDColumn;

    @FXML
    private TableColumn<lasku, Integer> varausidColumn;

    @FXML
    private TableColumn<lasku, Integer> summaColumn;

    @FXML
    private TableColumn<lasku, Integer> alvColumn;

    @FXML
    private TextField hakuTextField;

    ObservableList<lasku> oblist = FXCollections.observableArrayList();

    public void initialize() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla valitaan kaikki tiedot asiakas taulusta
            ResultSet rs = connectDB.createStatement().executeQuery("SELECT * FROM lasku");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                oblist.add(new lasku(rs.getInt("lasku_id"),rs.getInt("varaus_id"),rs.getInt("summa"),
                        rs.getInt("alv")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        laskuIDColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("lasku_id"));
        varausidColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("varaus_id"));
        summaColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("summa"));
        alvColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("alv"));

        //Asetetaan kaikki taulun tiedot listaan
        tableView.setItems(oblist);

        naytaTiedot();

    }

    public void haku() {

        //Tyhjennetään lista
        oblist.clear();

        try {

            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla haetaan tietoja annetulla sanalla tai kirjaimella
            String sana = hakuTextField.getText();
            String query = "select * from asiakas \n" +
                    "inner join varaus on asiakas.asiakas_id = varaus.asiakas_id\n" +
                    "inner join lasku on varaus.varaus_id = lasku.varaus_id\n" +
                    "where etunimi like '%" + sana + "%' or sukunimi like '%" + sana +
                    "%' or lahiosoite like '%" + sana + "%'";

            ResultSet rs = connectDB.createStatement().executeQuery(query);

            //While loop, joka lisää kaikki löydetyt asiakkaat listaan
            while(rs.next()) {
                oblist.add(new lasku(rs.getInt("lasku_id"),rs.getInt("varaus_id"),rs.getInt("summa"),
                        rs.getInt("alv")));
            }

        } catch(SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        laskuIDColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("lasku_id"));
        varausidColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("varaus_id"));
        summaColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("summa"));
        alvColumn.setCellValueFactory(new PropertyValueFactory<lasku,Integer>("alv"));

        //Asetetaan kaikki taulun tiedot listaan
        tableView.setItems(oblist);
    }

    //Metodi, jolla näkee taulkon tiedot textfieldeissä
    public void naytaTiedot() {

        //Annetaan hiiren klikkaukselle tapahtuma
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Lause, jolla valitaan taulukosta kaikki rivit ja sarakkeet
                lasku lk = tableView.getItems().get(tableView.getSelectionModel().getSelectedIndex());
                connectionClass connectNow = new connectionClass();
                Connection connectDB = connectNow.getConnection();

                String query = ("select * from asiakas \n" +
                        "inner join varaus on asiakas.asiakas_id = varaus.asiakas_id\n" +
                        "inner join lasku on varaus.varaus_id = lasku.varaus_id\n" +
                        "where varaus.varaus_id = '" + lk.getVaraus_id() + "'");

                String query2 = ("select * from mokki\n" +
                        "inner join asiakas on mokki.omistaja_id = asiakas.asiakas_id\n" +
                        "inner join varaus on mokki.mokki_id = varaus.mokki_mokki_id\n" +
                        "where varaus.varaus_id = '" + lk.getVaraus_id() + "'");

                try {

                    ResultSet rs = connectDB.createStatement().executeQuery(query);
                    ResultSet rs2 = connectDB.createStatement().executeQuery(query2);


                    while(rs.next()) {
                        etunimiLabel.setText(rs.getString(3));
                        sukunimiLabel.setText(rs.getString(4));
                        lahiosoiteLabel.setText(rs.getString(5));
                        postinumeroLabel.setText(rs.getString(2));
                        sahkopostiLabel.setText(rs.getString(6));
                        puhelinNumeroLabel.setText(rs.getString(7));

                    }

                    while(rs2.next()) {
                        mokinNimiLabel.setText(rs2.getString(4));
                        mokinPostiNumeroLabel.setText(rs2.getString(3));
                        katuosoiteLabel.setText(rs2.getString(5));

                    }

                } catch (Exception e) {

                }
            }
        });
    }

    public void resetti() {
        postinumeroLabel.setText("Postinumero");
        etunimiLabel.setText("Etunimi");
        sukunimiLabel.setText("Sukunimi");
        lahiosoiteLabel.setText("Lähiosoite");
        hakuTextField.setText("");
        mokinNimiLabel.setText("Mökin nimi");
        mokinPostiNumeroLabel.setText("Postinumero");
        katuosoiteLabel.setText("Katuosoite");
        palveluLabel.setText("Palvelu");
        haku();


    }

    //Metodi, jolla pääsee takaisin päänäyttöön
    private Stage stage;
    private Scene scene;
    private Parent root;

    //Luodaan uusi ikkuna, joka korvataan vanhalla
    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    //Luodaan uusi ikkuna, joka korvataan vanhalla
    public void switchToAktiiviset(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/aktiivisetLaskut.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

}
