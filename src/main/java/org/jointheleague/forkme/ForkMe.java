package org.jointheleague.forkme;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.eclipse.egit.github.core.client.RequestException;
import org.jointheleague.forkme.controller.UserListController;
import org.jointheleague.forkme.model.Account;
import org.jointheleague.forkme.model.JsonUser;
import org.jointheleague.forkme.model.PersistentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ForkMe extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ForkMe.class);
    public static final String APPLICATION_NAME = "forkMe";

    private static ForkMe instance;
    public static ObjectProperty<PersistentUser> currentAccount = new SimpleObjectProperty<>();

    private List<Account> accounts;
    private ResourceBundle resources;
    private Stage primaryStage;

    public static void main(String[] args) {
        JsonUser.loadAccounts();

        launch(args);
    }

    public static void setCurrentAccount(PersistentUser currentAccount) {
        ForkMe.currentAccount.setValue(currentAccount);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        resources = ResourceBundle.getBundle(APPLICATION_NAME);

        BorderPane loginPane = null;
        try {
            loginPane = (BorderPane) getPane(resources, "/ForkMe.fxml");
        } catch (IOException e) {
            logger.error("Can't load user interface.", e);
            System.exit(0);
        }

        loginPane.setCenter(new UserListController(PersistentUser.accounts));

        primaryStage.setOnCloseRequest(event -> Platform.exit());
        primaryStage.setTitle(resources.getString("title"));
        primaryStage.show();

    }

    private Pane getPane(ResourceBundle resources, String viewFile) throws IOException {
        URL location = getClass().getResource(viewFile);
        FXMLLoader loader = new FXMLLoader(location, resources);
        Pane pane = loader.load();

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);

        return pane;
    }

    public static ResourceBundle getResources() {
        return instance.getInstanceResources();
    }

    private ResourceBundle getInstanceResources() {
        return resources;
    }




}
