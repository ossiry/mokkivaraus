package sample;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;


public class loginController {

    //Scene builderin osien linkittäminen
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private Scene scene;
    private Parent root;

    //Sisäänkirjautumisen metodi
    public void sisaanPaasyHyvaksytty(ActionEvent event) throws IOException{

        //Alustetaan textfieldit ja resultset
        ResultSet resultSet = null;
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        try{
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla tarkistetaan käyttäjänimi ja salasana
            String query = "SELECT kayttaja_nimi,salasana from kayttaja WHERE kayttaja_nimi=? and salasana=?";
            PreparedStatement pst = connectDB.prepareStatement(query);
            pst.setString(1,username);
            pst.setString(2,password);
            resultSet = pst.executeQuery();

            //Ponnahdusikkuna, jos salasana menee väärin
            if(!resultSet.next()) {
                rekisterointiPonnahdusIkkuna.display("ERROR","Virheellinen käyttäjätunnus tai salasana. Kokeile uudelleen.");

                //Ponnahdusikkuna, jos salasana menee oikein
            } else{
                rekisterointiPonnahdusIkkuna.display("Nice","Kirjautuminen onnistui, tervetuloa!");
                this.root = (Parent)FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
                this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                this.scene = new Scene(this.root);
                this.stage.setScene(this.scene);
                this.stage.centerOnScreen();
                this.stage.show();
            }
        } catch (Exception e) {

        }
    }

    public void kirjautuu() {
        usernameTextField.setText("Masa99");
        passwordField.setText("Masa99Masa99");
    }

    //Rekisteröinti ikkunan vaihto metodi
    public void switchToRekisterointi(ActionEvent event) throws IOException {

        this.root = (Parent)FXMLLoader.load(this.getClass().getResource("resources/rekisterointi.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.centerOnScreen();
        this.stage.show();

    }

}
