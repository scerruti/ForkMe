package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jointheleague.forkme.model.PersistentUser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class AccountBox extends VBox {

    private final PersistentUser user;
    @FXML
    private Button actionButton;
    @FXML
    private Text errorMessage;
    @FXML
    private ImageView avatar;
    @FXML
    private Text name;
    @FXML
    private VBox actionPane;

    public AccountBox(PersistentUser user) {
        URL view = getClass().getResource("/AccountBox.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(view, org.jointheleague.forkme.ForkMe.getResources());
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.user = user;

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public AccountBox(AccountBox source) {
        this(source.getUser());
    }

    @FXML
    public void initialize() throws IOException {
        name.setText(user.getName());
        Image image = new Image(Files.newInputStream(user.getAvatar()));
        avatar.setImage(image);
    }

    private PersistentUser getUser() {
        return user;
    }

    public void setButtonsVisible(boolean buttonsVisible) {
        actionPane.setVisible(buttonsVisible);
    }
}
