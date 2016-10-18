package org.jointheleague.forkme;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jointheleague.forkme.controller.RepoController;
import org.jointheleague.forkme.controller.UserListController;
import org.jointheleague.forkme.model.Account;
import org.jointheleague.forkme.model.JsonUser;
import org.jointheleague.forkme.model.PersistentUser;
import org.jointheleague.forkme.model.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ForkMe extends Application {
    public static final String APPLICATION_NAME = "forkMe";
    private static final Logger logger = LoggerFactory.getLogger(ForkMe.class);
    public static ObjectProperty<PersistentUser> currentUser = new SimpleObjectProperty<>();
    public static ObjectProperty<Account> currentAccount = new SimpleObjectProperty<>();
    private static ForkMe instance;
    private List<Account> accounts;
    private ResourceBundle resources;
    private Stage primaryStage;
    private Repo repo = new Repo();
    private StackPane centerPane;
    private RepoController repoPane;
    private UserListController userPane;
    private BorderPane mainWindow;

    public static void main(String[] args) {
        JsonUser.loadAccounts();

        launch(args);
    }

    public static void setCurrentUser(PersistentUser currentUser) {
        ForkMe.currentUser.setValue(currentUser);
    }

    public static ResourceBundle getResources() {
        return instance.getInstanceResources();
    }

    public static void logoff() {
        currentUser.setValue(null);
        currentAccount.setValue(null);
    }

    public static void setCurrentUser(Account account) {
        ForkMe.currentAccount.setValue(account);
    }

    public static Account getCurrentAccount() {
        return currentAccount.get();
    }

    public static void show(Pane pane) {
        instance.mainWindow.setCenter(pane);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        resources = ResourceBundle.getBundle(APPLICATION_NAME);

        mainWindow = null;
        try {
            mainWindow = (BorderPane) getPane(resources, "/ForkMe.fxml");
        } catch (IOException e) {
            logger.error("Can't load user interface.", e);
            System.exit(0);
        }

        userPane = new UserListController(PersistentUser.accounts);
        repoPane = new RepoController(repo);
        mainWindow.setCenter(userPane);

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

    private ResourceBundle getInstanceResources() {
        return resources;
    }
}
