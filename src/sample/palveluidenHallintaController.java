package sample;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class palveluidenHallintaController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField txtnimi;

    @FXML
    private TableView<Palvelu> tvpalvelu;

    @FXML
    private TableColumn<Palvelu, Integer> tbcpalvelu_id;

    @FXML
    private TableColumn<Palvelu, Integer> tbctoimintaalue_id;

    @FXML
    private TableColumn<Palvelu, String> tbcnimi;

    @FXML
    private TableColumn<Palvelu, Integer> tbctyyppi;

    @FXML
    private TableColumn<Palvelu, String> tbckuvaus;

    @FXML
    private TableColumn<Palvelu, Double> tbchinta;

    @FXML
    private TableColumn<Palvelu, Double> tbcalv;

    @FXML
    private TextField txthinta;

    @FXML
    private TextField txtalv;

    @FXML
    private TextField tftyyppi;

    @FXML
    private TextField tfToimintaalue_id;

    @FXML
    private TextArea kuvausTextArea;

    @FXML
    private Label palveluIDLabel;

    ObservableList<Palvelu> oblist = FXCollections.observableArrayList();


    //Metodi, jolla haetaan sql tietokannasta taulukon tiedot
    public void initialize() {
        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla valitaan kaikki tiedot asiakas taulusta
            ResultSet rs = connectDB.createStatement().executeQuery("select * from palvelu");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                oblist.add(new Palvelu(rs.getInt("palvelu_id"),rs.getInt("toimintaalue_id"),rs.getString("nimi"),
                        rs.getInt("tyyppi"),rs.getString("kuvaus"),rs.getDouble("hinta"),rs.getDouble("alv")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        tbcpalvelu_id.setCellValueFactory(new PropertyValueFactory<Palvelu,Integer>("palvelu_id"));
        tbctoimintaalue_id.setCellValueFactory(new PropertyValueFactory<Palvelu,Integer>("toimintaalue_id"));
        tbcnimi.setCellValueFactory(new PropertyValueFactory<Palvelu,String>("nimi"));
        tbctyyppi.setCellValueFactory(new PropertyValueFactory<Palvelu,Integer>("tyyppi"));
        tbckuvaus.setCellValueFactory(new PropertyValueFactory<Palvelu,String>("kuvaus"));
        tbchinta.setCellValueFactory(new PropertyValueFactory<Palvelu,Double>("hinta"));
        tbcalv.setCellValueFactory(new PropertyValueFactory<Palvelu,Double>("alv"));

        //Asetetaan kaikki taulun tiedot listaan
        tvpalvelu.setItems(oblist);

        naytaTiedotPalvelu();
    }

    //palveluiden lisääminen tauluun
    public void btLisaa() {
        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //Sql lause, jolla luodaan asiakkaita
            String query = "insert into palvelu (toimintaalue_id,nimi,tyyppi,kuvaus,hinta,alv)values(?,?,?,?,?,?)";
            //Preparedstatement, joka kertoo mitä pitää tehdä sql:ssä
            PreparedStatement pst = connectDB.prepareStatement(query);
            //Tietojen hakeminen textfieldeistä
            pst.setString(1,tfToimintaalue_id.getText());
            pst.setString(2,txtnimi.getText());
            pst.setString(3,tftyyppi.getText());
            pst.setString(4,kuvausTextArea.getText());
            pst.setString(5,txthinta.getText());
            pst.setString(6,txtalv.getText());
            //Lause, jolla suoritetaan komennot sql:ssa
            pst.executeUpdate();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            lataaPalvelutaulu();

        } catch(Exception e) {

        }
    }

    public void lataaPalvelutaulu() {
        try {
            //Lause, jolla tyhjennetään kaikki listan nykyiset tiedot
            oblist.clear();
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //Lause, joka suoritetaan sql:ssä
            ResultSet rs = connectDB.createStatement().executeQuery("select * from palvelu");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                oblist.add(new Palvelu(rs.getInt("palvelu_id"),rs.getInt("toimintaalue_id"),rs.getString("nimi"),
                        rs.getInt("tyyppi"),rs.getString("kuvaus"),rs.getDouble("hinta"),rs.getDouble("alv")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void naytaTiedotPalvelu(){
        //Annetaan hiiren klikkaukselle tapahtuma
        tvpalvelu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Lause, jolla valitaan taulukosta kaikki rivit ja sarakkeet
                Palvelu pv = tvpalvelu.getItems().get(tvpalvelu.getSelectionModel().getSelectedIndex());

                //Näytetään taulukon tiedot omissa textfieldeissä
                palveluIDLabel.setText(String.valueOf(pv.getPalvelu_id()));
                tfToimintaalue_id.setText(String.valueOf(pv.getToimintaalue_id()));
                txtnimi.setText(pv.getNimi());
                tftyyppi.setText(String.valueOf(pv.getTyyppi()));
                kuvausTextArea.setText(pv.getKuvaus());
                txthinta.setText(String.valueOf(pv.getHinta()));
                txtalv.setText(String.valueOf(pv.getAlv()));

            }
        });
    }

    public void btPoista() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Metodi, jolla pystytään suorittamaan sql komennot
            PreparedStatement pst;

            //Sql lause, jolla poistetaan asiakkaan tiedot taulusta
            String query = "DELETE FROM palvelu WHERE palvelu_id=?";

            //Suoritetaan sql komento DELETE
            pst = connectDB.prepareStatement(query);
            pst.setString(1,palveluIDLabel.getText());
            pst.executeUpdate();


            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            lataaPalvelutaulu();

        } catch (Exception e) {
        }
    }

    public void btMuokkaa() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Metodi, jolla pystytään suorittamaan sql komennot
            PreparedStatement pst;

            //Tietojen näyttäminen textfieldeissä
            int rivi1 = Integer.parseInt(palveluIDLabel.getText());
            int rivi2 = Integer.parseInt(tfToimintaalue_id.getText());
            String rivi3 = txtnimi.getText();
            int rivi4 = Integer.parseInt(tftyyppi.getText());
            String rivi5 = kuvausTextArea.getText();
            double rivi6 = Double.parseDouble(txthinta.getText());
            double rivi7 = Double.parseDouble(txtalv.getText());

            //Sql lause, jolla päivitetään asiakkaan tietoja taulussa
            String query = "UPDATE palvelu set toimintaalue_id= "+rivi2+",nimi='"+rivi3+"',tyyppi="+rivi4+",kuvaus='"+rivi5+"',hinta="+rivi6+",alv="+rivi7
                    +" WHERE palvelu_id="+rivi1+"";

            //Lause, jolla suoritetaan komennot sql:ssa
            pst = connectDB.prepareStatement(query);
            pst.execute();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            lataaPalvelutaulu();

        } catch (Exception e) {
        }


    }


    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}