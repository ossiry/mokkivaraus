package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class rekisterointiController {

    @FXML
    private TextField kayttajaTunnusTextField;

    @FXML
    private TextField salasanaTextField;

    @FXML
    private TextField salasanaUudelleenTextField;

    ObservableList<asiakas> oblist = FXCollections.observableArrayList();

    //Salasanan tarkistus metodi
    public boolean kelpaa() {
        boolean kelvollinenSalasana = true;
        int i = 0;
        int numerot = 0;
        int isotKirjaimet = 0;
        int pienetKirjaimet = 0;

        if (salasanaTextField.getText().length() < 10)
            kelvollinenSalasana = false;

        while (kelvollinenSalasana && i < salasanaTextField.getText().length()) {
            char ch = salasanaTextField.getText().charAt(i);

            if (Character.isLetter(ch) && Character.isLowerCase(ch)) {
                pienetKirjaimet++;
            } else if (Character.isLetter(ch) && Character.isUpperCase(ch)) {
                isotKirjaimet++;
            } else if (Character.isDigit(ch)) {
                numerot++;
            }
            i++;

        }

        if (numerot < 1)
            kelvollinenSalasana = false;

        if (pienetKirjaimet < 1)
            kelvollinenSalasana = false;

        if (isotKirjaimet < 1)
            kelvollinenSalasana = false;

        return kelvollinenSalasana;

    }

    public void lisaaKayttaja(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root;

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //SQL lause
            String query = "INSERT INTO kayttaja(kayttaja_nimi,salasana) VALUES(?,?)";
            PreparedStatement pst = connectDB.prepareStatement(query);
            pst.setString(1,kayttajaTunnusTextField.getText());
            pst.setString(2,salasanaTextField.getText());
            if (kelpaa() && salasanaTextField.getText().equals(salasanaUudelleenTextField.getText())) {
                rekisterointiPonnahdusIkkuna.display("Nice","Käyttäjä luotu!");
                pst.executeUpdate();
                root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/login.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                rekisterointiPonnahdusIkkuna.display("ERROR","Salasana ei kelpaa, kokeile uudelleen.");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }





    public void switchToLogin(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root;

        root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

}
