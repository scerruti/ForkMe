package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.eclipse.egit.github.core.client.RequestException;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.LoginService;
import org.jointheleague.forkme.view.AccountBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ForkMeController {
    private static final Logger logger = LoggerFactory.getLogger(ForkMeController.class);
    private static final String LOGIN_ERROR = "error.login";
    private static final String UNRECOGNIZED_ERROR = "error.unrecognized";
    private static final String GITHUB_ERROR = "error.github";
    private static final String LOGIN_TEXT = "login.button.text";
    @FXML
    public AccountBox currentAccountBox;
    @FXML
    public VBox loginPrompt;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Text loginError;
    @FXML
    private Button loginButton;

   @FXML
    public void initialize() {
       currentAccountBox.setUser(ForkMe.currentAccount.getValue());
       currentAccountBox.setActionButtonText(ForkMe.getResources().getString(LOGIN_TEXT));
       currentAccountBox.setActionControlsVisible(true);
       currentAccountBox.setMainController(this);
       ForkMe.currentAccount.addListener((observable, oldValue, newValue) -> {
           currentAccountBox.setUser(newValue);
           loginPrompt.setVisible(false);
           currentAccountBox.setDisable(false);
           currentAccountBox.setActionControlsVisible(true);
       });
    }

    @FXML
    private void login(ActionEvent actionEvent) {
        loginButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style
        loginError.setText("");

        LoginService service = new LoginService(username.getText(), password.getText());
        service.setOnSucceeded(event -> {
            loginButton.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
        });
        service.setOnFailed(event -> {
            loginButton.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
            Exception e = (Exception) event.getSource().exceptionProperty().get();

            if (e instanceof RequestException) {
                RequestException re = (RequestException) e;
                if (re.getStatus() == 401) {
                    loginError.setText(ForkMe.getResources().getString(LOGIN_ERROR));
                } else {
                    loginError.setText(UNRECOGNIZED_ERROR);
                    logger.error("Unrecoginized login error", re);
                }
            } else if (e instanceof IOException) {
                loginError.setText(GITHUB_ERROR);
                logger.error("GitHub could not be reached.", e);
            }
        });
        service.start();
    }

    public void promptForLogin() {
        loginPrompt.setVisible(true);
        currentAccountBox.setDisable(true);
        currentAccountBox.hideActionControls();
        username.requestFocus();
    }
}
