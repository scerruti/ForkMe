package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.eclipse.egit.github.core.client.RequestException;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.Account;
import org.jointheleague.forkme.model.LoginService;
import org.jointheleague.forkme.model.PersistentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AccountBoxController {
    private static final Logger logger = LoggerFactory.getLogger(AccountBoxController.class);
    private static final String LOGIN_ERROR = "error.login";
    private static final String UNRECOGNIZED_ERROR = "error.unrecognized";
    private static final String GITHUB_ERROR = "error.github";

    private PersistentUser user;
    @FXML
    private Button loginButton;
    @FXML
    private Text errorMessage;
    @FXML
    private ImageView avatar;
    @FXML
    private Text name;
    @FXML
    private VBox loginPane;
    @FXML
    private PasswordField passwordField;
    @FXML
    private VBox actionPane;
    private static final Image defaultImage = new Image("student-silhouette.png");

    /*
     * Controller constructor
     */
    public AccountBoxController() {
        this.user = null;
    }

    /*
     * View constructors
     */


    @FXML
    public void initialize() {
    }

    public PersistentUser getUser() {
        return user;
    }

    public void setLoginButtonsVisible(boolean buttonsVisible) {
        loginPane.setVisible(buttonsVisible);
    }

    @FXML
    private void loginAction(ActionEvent actionEvent) {
        loginButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style
        errorMessage.setText("");

        LoginService service = new LoginService(user.getLogin(), passwordField.getText());
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                //loginButton.getScene().hideAccountLogin(AccountBoxController.this);
                logger.debug("hide");
                loginButton.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
            }
        });
        service.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                loginButton.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
                Exception e = (Exception) event.getSource().exceptionProperty().get();

                handleError(e);

            }

            private void handleError(Exception e) {
                if (e instanceof RequestException) {
                    RequestException re = (RequestException) e;
                    if (re.getStatus() == 401) {
                        errorMessage.setText(org.jointheleague.forkme.ForkMe.getResources().getString(LOGIN_ERROR));
                    } else {
                        errorMessage.setText(UNRECOGNIZED_ERROR);
                        logger.error("Unrecoginized login error", re);
                    }
                } else if (e instanceof IOException){
                    errorMessage.setText(GITHUB_ERROR);
                    logger.error("GitHub could not be reached.", (IOException) e);
                }

        }
    });


}

    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        //UserListController.hideAccountLogin(this);
        logger.debug("hide");
    }

    @FXML
    private void doAction(ActionEvent actionEvent) {
        // TODO
    }

    public void setUser(PersistentUser user) {
        this.user = user;
        if (user != null) {
            name.textProperty().bind(user.effectiveNameProperty());
            setUserImage(user.getAvatar());
            user.avatarProperty().addListener(new ChangeListener<Path>() {
                @Override
                public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
                    setUserImage(newValue);
                }
            });
            actionPane.setVisible(false);
        }
    }

    private void setUserImage(Path avatarPath) {
        Image image = null;
        try {
            image = new Image(Files.newInputStream(avatarPath));
        } catch (IOException e) {
            image = defaultImage;
            logger.error("Unable to create user image from path", e);
        }
        avatar.setImage(image);
    }

    public void setActionButtonsVisible(boolean visible) {
        this.actionPane.setVisible(visible);
    }
}
