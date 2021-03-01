package ui.user;

import businesslogic.CatERing;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextInputDialog;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import ui.Main;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class UserLoginManagement {

    @FXML
    Main mainPaneController;

    public void initialize() {
        TextInputDialog dial = new TextInputDialog("");
        dial.setTitle("Accedi");
        dial.setHeaderText("Inserisci il tuo username");
        String user = dial.showAndWait().orElse(new String());

        CatERing.getInstance().getUserManager().login(user);
    }
}
