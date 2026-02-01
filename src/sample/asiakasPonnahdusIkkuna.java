package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;



public class asiakasPonnahdusIkkuna {

    public static void display(String title, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(250);

        Label label = new Label();
        label.setText(message);
        Button peruutaButton = new Button("Peruuta");
        Button hyvaksyButton = new Button("Hyväksy");
        peruutaButton.setOnAction(e -> {
            window.close();
        });

        hyvaksyButton.setOnAction(e->{
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,peruutaButton,hyvaksyButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
