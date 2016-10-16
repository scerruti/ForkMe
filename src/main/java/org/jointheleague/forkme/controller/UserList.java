package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.animation.*;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jointheleague.forkme.model.PersistentUser;

import java.io.IOException;

public class UserList extends StackPane {

    @FXML
    private FlowPane userPane;
    @FXML
    private Pane loginAnchorPane;
    @FXML
    private Pane loginAccountPane;

    private ObservableMap<String, PersistentUser> list; // TODO add change listener

    public UserList(ObservableMap<String, PersistentUser> list) {
        this.list = list;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/UserList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void initialize() {
        createAccountBoxes();
    }

    private void createAccountBoxes() {
        for (PersistentUser user : list.values()) {
            AccountBox accountBox = new AccountBox(user);
            accountBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    AccountBox source = (AccountBox) event.getSource();
                    loginAnchorPane.setVisible(true);

                    AccountBox accountBox = new AccountBox(source);
                    loginAccountPane.getChildren().add(accountBox);
                    accountBox.setLayoutX(source.getLayoutX());
                    accountBox.setLayoutY(source.getLayoutY());

                    TranslateTransition translateTransition =
                        new TranslateTransition(Duration.millis(1000), accountBox);
                    translateTransition.setToX(350-source.getLayoutX());

                    ScaleTransition scaleTransition =
                            new ScaleTransition(Duration.millis(1000), accountBox);
                    scaleTransition.setToX(2f);
                    scaleTransition.setToY(2f);


                    ParallelTransition parallelTransition = new ParallelTransition();
                    parallelTransition.getChildren().addAll(
                            translateTransition,
                            scaleTransition
                    );
                    parallelTransition.play();

                    accountBox.setButtonsVisible(true);
                }
            });
            userPane.getChildren().add(accountBox);
        }
    }

}
