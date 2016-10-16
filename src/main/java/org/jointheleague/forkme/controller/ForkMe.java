package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import org.eclipse.egit.github.core.client.RequestException;
import org.jointheleague.forkme.model.Account;
import org.jointheleague.forkme.model.PersistentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

public class ForkMe {
    private static final Logger logger = LoggerFactory.getLogger(ForkMe.class);
    private static final String LOGIN_ERROR = "login.error";

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
    }

    @FXML
    private void login(ActionEvent actionEvent) {
        loginButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style
        loginError.setText("");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Account account = new Account(username.getText(), password.getText());
                try {
                    account.login();
                } catch (RequestException e) {
                    if (e.getStatus() == 401) {
                        loginError.setText(org.jointheleague.forkme.ForkMe.getResources().getString(LOGIN_ERROR));
                    } else {
                        loginError.setText("Unrecognized error, see logs.");
                        logger.error("Unrecoginized login error", e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                org.jointheleague.forkme.ForkMe.currentAccount.setValue(PersistentUser.getAccount(username.getText()));
                loginButton.getScene().setCursor(Cursor.DEFAULT); //Change cursor to wait style
            }
        });
    }
}
