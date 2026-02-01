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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

public class asiakkaidenTietojenHallintaController {

    //Scene builderin osien yhdistäminen intellij:hin
    @FXML
    private TableView<asiakas> tableView;
    @FXML
    private TableColumn<asiakas, Integer> asiakas_idColumn;
    @FXML
    private TableColumn<asiakas, Integer> postinroColumn;
    @FXML
    private TableColumn<asiakas, String> etunimiColumn;
    @FXML
    private TableColumn<asiakas, String> sukunimiColumn;
    @FXML
    private TableColumn<asiakas, String> lahiosoiteColumn;
    @FXML
    private TableColumn<asiakas, String> emailColumn;
    @FXML
    private TableColumn<asiakas, String> puhelinnroColumn;

    //Textfieldien yhdistäminen intellij:hin
    @FXML
    private TextField etunimiField;

    @FXML
    private TextField sukunimiField;

    @FXML
    private TextField postinumeroField;

    @FXML
    private TextField sahkopostiField;

    @FXML
    private TextField puhelinnumeroField;

    @FXML
    private TextField lahiosoiteField;

    @FXML
    private Label asiakasIDLabel;

    @FXML
    private TextField hakuTextField;

    //Asiakas olion tietojen listaaminen
    ObservableList<asiakas> oblist = FXCollections.observableArrayList();

    //Asiakkaiden lisääminen sql asiakas tauluun
    public void addUsers() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //Sql lause, jolla luodaan asiakkaita
            String query = "insert into asiakas (postinro,etunimi,sukunimi,lahiosoite,email,puhelinnro)values(?,?,?,?,?,?)";
            //Preparedstatement, joka kertoo mitä pitää tehdä sql:ssä
            PreparedStatement pst = connectDB.prepareStatement(query);
            //Tietojen hakeminen textfieldeistä
            pst.setString(1,postinumeroField.getText());
            pst.setString(2,etunimiField.getText());
            pst.setString(3,sukunimiField.getText());
            pst.setString(4,lahiosoiteField.getText());
            pst.setString(5,sahkopostiField.getText());
            pst.setString(6,puhelinnumeroField.getText());
            //Lause, jolla suoritetaan komennot sql:ssa
            pst.executeUpdate();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            paivitaTiedot();

        } catch(Exception e) {

        }
    }

    //Metodi, jolla päivitetään tauluntiedot
    public void paivitaTiedot() {

        try {
            //Lause, jolla tyhjennetään kaikki listan nykyiset tiedot
            oblist.clear();
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //Lause, joka suoritetaan sql:ssä
            ResultSet rs = connectDB.createStatement().executeQuery("select * from asiakas");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                oblist.add(new asiakas(rs.getInt("asiakas_id"), rs.getInt("postinro"), rs.getString("etunimi"),
                        rs.getString("sukunimi"), rs.getString("lahiosoite"), rs.getString("email"), rs.getString("puhelinnro")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    //Metodi, jolla päivitetään asiakkaiden tiedot taulussa
    public void paivitaAsiakas() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Metodi, jolla pystytään suorittamaan sql komennot
            PreparedStatement pst;

            //Tietojen näyttäminen textfieldeissä
            String rivi1 = asiakasIDLabel.getText();
            String rivi2 = postinumeroField.getText();
            String rivi3 = etunimiField.getText();
            String rivi4 = sukunimiField.getText();
            String rivi5 = lahiosoiteField.getText();
            String rivi6 = sahkopostiField.getText();
            String rivi7 = puhelinnumeroField.getText();

            //Sql lause, jolla päivitetään asiakkaan tietoja taulussa
            String query = "UPDATE asiakas set postinro= '"+rivi2+"',etunimi='"+rivi3+"',sukunimi='"+rivi4+"',lahiosoite='"+rivi5+"',email='"+rivi6+"',puhelinnro='"+rivi7
                    +"' where asiakas_id='"+rivi1+"'";

            //Lause, jolla suoritetaan komennot sql:ssa
            pst = connectDB.prepareStatement(query);
            pst.execute();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            paivitaTiedot();


        } catch (Exception e) {
        }
    }

    //Metodi, jolla poistetaan asiakkaiden tiedot taulusta
    public void poistaAsiakas() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Metodi, jolla pystytään suorittamaan sql komennot
            PreparedStatement pst;

            //Sql lause, jolla poistetaan asiakkaan tiedot taulusta
            String query = "DELETE FROM asiakas WHERE asiakas_id=?";

            //Suoritetaan sql komento DELETE
            pst = connectDB.prepareStatement(query);
            pst.setString(1,asiakasIDLabel.getText());
            pst.executeUpdate();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            paivitaTiedot();

        } catch (Exception e) {
            }
        }

        //Ponnahdus ikkuna, mikä tulee kun painetaan poista nappia
    public void poistaAsiakasPonnahdusIkkuna() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Varoitus!");
        window.setMinWidth(250);
        window.setMinHeight(250);

        Label label = new Label();
        label.setText("Haluatko varmasti poistaa tiedot?");
        Button peruutaButton = new Button("Peruuta");
        Button hyvaksyButton = new Button("Hyväksy");
        peruutaButton.setOnAction(e -> {
            window.close();
        });

        hyvaksyButton.setOnAction(e->{
                poistaAsiakas();
                resetti();
                window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,peruutaButton,hyvaksyButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    //Ponnahdusikkuna, mikä tulee kun painetaan lisää nappia
    public void lisaaAsiakasPonnahdusIkkuna() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Varoitus!");
        window.setMinWidth(250);
        window.setMinHeight(250);

        Label label = new Label();
        label.setText("Haluatko varmasti lisätä tiedot?");
        Button peruutaButton = new Button("Peruuta");
        Button hyvaksyButton = new Button("Hyväksy");
        peruutaButton.setOnAction(e -> {
            window.close();
        });

        hyvaksyButton.setOnAction(e->{
            addUsers();
            resetti();
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,peruutaButton,hyvaksyButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    //Ponnahdusikkuna, mikä tulee kun painetaan muokkaa nappia
    public void muokkaaAsiakastaPonnahdusIkkuna() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Varoitus!");
        window.setMinWidth(250);
        window.setMinHeight(250);

        Label label = new Label();
        label.setText("Haluatko varmasti päivittää muokatut tiedot?");
        Button peruutaButton = new Button("Peruuta");
        Button hyvaksyButton = new Button("Hyväksy");
        peruutaButton.setOnAction(e -> {
            window.close();
        });

        hyvaksyButton.setOnAction(e->{
            paivitaAsiakas();
            resetti();
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,peruutaButton,hyvaksyButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    //Metodi, jolla näkee taulkon tiedot textfieldeissä
    public void naytaTiedot() {

        //Annetaan hiiren klikkaukselle tapahtuma
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Lause, jolla valitaan taulukosta kaikki rivit ja sarakkeet
                asiakas as = tableView.getItems().get(tableView.getSelectionModel().getSelectedIndex());

                //Näytetään taulukon tiedot omissa textfieldeissä
                asiakasIDLabel.setText(as.getAsiakas_id().toString());
                postinumeroField.setText(as.getPostinro().toString());
                etunimiField.setText(as.getEtunimi());
                sukunimiField.setText(as.getSukunimi());
                lahiosoiteField.setText(as.getLahiosoite());
                sahkopostiField.setText(as.getEmail());
                puhelinnumeroField.setText(as.getPuhelinnro());
            }
        });
    }

    //Metodi, jolla haetaan sql tietokannasta taulukon tiedot
    public void initialize() {
        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla valitaan kaikki tiedot asiakas taulusta
            ResultSet rs = connectDB.createStatement().executeQuery("select * from asiakas");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                oblist.add(new asiakas(rs.getInt("asiakas_id"),rs.getInt("postinro"),rs.getString("etunimi"),
                        rs.getString("sukunimi"),rs.getString("lahiosoite"),rs.getString("email"),rs.getString("puhelinnro")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        asiakas_idColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("asiakas_id"));
        postinroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("postinro"));
        etunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("etunimi"));
        sukunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("sukunimi"));
        lahiosoiteColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("lahiosoite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("email"));
        puhelinnroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("puhelinnro"));

        //Asetetaan kaikki taulun tiedot listaan
        tableView.setItems(oblist);

        //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
        naytaTiedot();

    }

    //Metodi, jolla pystytään hakemaan asiakasta hakukenttään annetulla inffolla
    public void haku() {

        //Tyhjennetään lista
        oblist.clear();

        try {

            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla haetaan tietoja annetulla sanalla tai kirjaimella
            String sana = hakuTextField.getText();
            String query = "SELECT asiakas_id,postinro,etunimi,sukunimi,lahiosoite,email,puhelinnro FROM asiakas WHERE postinro LIKE '%"+ sana + "%' OR\n" +
                    "etunimi LIKE '%" + sana + "%' OR sukunimi LIKE '%" + sana + "%' OR lahiosoite LIKE '%" + sana + "%' OR email LIKE '%" + sana + "%' OR\n" +
                    "puhelinnro LIKE '%" + sana + "%'";

            ResultSet rs = connectDB.createStatement().executeQuery(query);

            //While loop, joka lisää kaikki löydetyt asiakkaat listaan
            while(rs.next()) {
                oblist.add(new asiakas(rs.getInt("asiakas_id"), rs.getInt("postinro"), rs.getString("etunimi"),
                        rs.getString("sukunimi"), rs.getString("lahiosoite"), rs.getString("email"), rs.getString("puhelinnro")));
            }

        } catch(SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        asiakas_idColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("asiakas_id"));
        postinroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("postinro"));
        etunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("etunimi"));
        sukunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("sukunimi"));
        lahiosoiteColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("lahiosoite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("email"));
        puhelinnroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("puhelinnro"));

        //Asetetaan kaikki taulun tiedot listaan
        tableView.setItems(oblist);
    }

    public void resetti() {
        asiakasIDLabel.setText("");
        postinumeroField.setText("");
        etunimiField.setText("");
        sukunimiField.setText("");
        lahiosoiteField.setText("");
        sahkopostiField.setText("");
        puhelinnumeroField.setText("");

    }

    //Metodi, jolla pääsee takaisin päänäyttöön
    private Stage stage;
    private Scene scene;
    private Parent root;

    //Luodaan uusi ikkuna, joka korvataan vanhalla
    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent)FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}
