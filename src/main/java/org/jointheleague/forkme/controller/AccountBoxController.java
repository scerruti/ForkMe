package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.eclipse.egit.github.core.client.RequestException;
import org.jointheleague.forkme.ForkMe;
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
    private static final Image defaultImage = new Image("student-silhouette.png");
    private static final String LOGIN_TEXT = "login.button.text";
    private static final String LOGOFF_TEXT = "logoff.button.text";
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
    private UserListController userListController;
    private ChangeListener<Path> avatarListener;
    private ForkMeController mainController;

    public AccountBoxController() {
        this.user = null;
    }

    public PersistentUser getUser() {
        return user;
    }

    public void setUser(PersistentUser user) {
        if (user != null) {
            name.textProperty().bind(user.effectiveNameProperty());
            setUserImage(user.getAvatar());
            avatarListener = (observable, oldValue, newValue) -> setUserImage(newValue);
            user.avatarProperty().addListener(avatarListener);
            loginButton.setText(ForkMe.getResources().getString(LOGOFF_TEXT));
        } else {
            if (this.user != null) {
                name.textProperty().unbind();
                this.user.avatarProperty().removeListener(avatarListener);
            }
            name.textProperty().setValue("");
            avatar.setImage(defaultImage);
            loginButton.setText(ForkMe.getResources().getString(LOGIN_TEXT));
        }
        this.user = user;
    }

    public void setLoginButtonsVisible(boolean buttonsVisible) {
        loginPane.setVisible(buttonsVisible);
        loginPane.setManaged(buttonsVisible);
        passwordField.requestFocus();
    }

    @FXML
    private void loginAction(ActionEvent actionEvent) {
        final Scene scene = loginButton.getScene();
        scene.setCursor(Cursor.WAIT); //Change cursor to wait style
        errorMessage.setText("");

        LoginService service = new LoginService(user.getLogin(), passwordField.getText());
        service.setOnSucceeded(event -> {
            userListController.hideAccountLogin();
            scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
        });
        service.setOnFailed(event -> {
            scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
            Exception e = (Exception) event.getSource().exceptionProperty().get();

            if (e instanceof RequestException) {
                RequestException re = (RequestException) e;
                if (re.getStatus() == 401) {
                    errorMessage.setText(ForkMe.getResources().getString(LOGIN_ERROR));
                } else {
                    errorMessage.setText(UNRECOGNIZED_ERROR);
                    logger.error("Unrecoginized login error", re);
                }
            } else if (e instanceof IOException) {
                errorMessage.setText(GITHUB_ERROR);
                logger.error("GitHub could not be reached.", e);
            }
        });
        service.start();
    }

    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        userListController.hideAccountLogin();
    }

    @FXML
    private void doAction(ActionEvent actionEvent) {
        if (this.user != null) {
            ForkMe.logoff();
        } else {
            mainController.promptForLogin();
        }
    }

    private void setUserImage(Path avatarPath) {
        Image image;
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
        this.actionPane.setManaged(visible);
    }

    public void setUserList(UserListController userListController) {
        this.userListController = userListController;
    }

    public void setActionButtonText(String buttonText) {
        loginButton.setText(buttonText);
    }

    public void setMainController(ForkMeController forkMeController) {
        this.mainController = forkMeController;
    }

    public void hideActionControls() {
        this.actionPane.setVisible(false);
    }

    public VBox getButtonPane() {
        return loginPane;
    }
}
