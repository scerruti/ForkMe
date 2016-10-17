package org.jointheleague.forkme.view;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.jointheleague.forkme.controller.AccountBoxController;
import org.jointheleague.forkme.model.PersistentUser;

import java.io.IOException;
import java.net.URL;

public class AccountBox extends VBox {
    private final AccountBoxController controller;

    public AccountBox() {
        this(null);
    }

    public AccountBox(PersistentUser user) {
        URL location = getClass().getResource("/AccountBox.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location, org.jointheleague.forkme.ForkMe.getResources());
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
            this.controller = fxmlLoader.getController();
            controller.setUser(user);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public PersistentUser getUser() {
        return controller.getUser();
    }

    public void showLoginControls() {
        controller.setLoginButtonsVisible(true);
    }

    public void hideActionControls() {
        controller.setActionButtonsVisible(false);
    }
}
