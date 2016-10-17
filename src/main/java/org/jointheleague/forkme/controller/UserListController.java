package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.animation.*;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jointheleague.forkme.model.PersistentUser;
import org.jointheleague.forkme.view.AccountBox;

import java.io.IOException;

public class UserListController extends StackPane {

    @FXML
    private FlowPane userPane;
    @FXML
    private Pane loginAnchorPane;
    @FXML
    private Pane loginAccountPane;

    private ObservableMap<String, PersistentUser> list; // TODO add change listener
    private double sourceX;

    public UserListController(ObservableMap<String, PersistentUser> list) {
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

                    AccountBox accountBox = new AccountBox(source.getUser());
                    loginAccountPane.getChildren().add(accountBox);
                    accountBox.setLayoutX(source.getLayoutX());
                    accountBox.setLayoutY(source.getLayoutY());

                    FadeTransition fadeTransition =
                            new FadeTransition(Duration.millis(500), userPane);
                    fadeTransition.setFromValue(1.0);
                    fadeTransition.setToValue(.3);

                    sourceX = source.getLayoutX();

                    TranslateTransition translateTransition =
                        new TranslateTransition(Duration.millis(500), accountBox);
                    translateTransition.setToX(350-source.getLayoutX());

                    ScaleTransition scaleTransition =
                            new ScaleTransition(Duration.millis(500), accountBox);
                    scaleTransition.setToX(2f);
                    scaleTransition.setToY(2f);


                    ParallelTransition parallelTransition = new ParallelTransition();
                    parallelTransition.getChildren().addAll(
                            fadeTransition,
                            translateTransition,
                            scaleTransition
                    );
                    parallelTransition.play();

                    accountBox.showLoginControls();
                    accountBox.hideActionControls();
                }
            });
            userPane.getChildren().add(accountBox);
        }
    }

    public void hideAccountLogin(AccountBox box) {
        Pane pane = (Pane) box.getScene().lookup("#loginAccountPane");
        AnchorPane anchorPane = (AnchorPane) box.getScene().lookup("#loginAnchorPane");
        FlowPane userPane = (FlowPane) box.getScene().lookup("#userPane");

        FadeTransition fadeTransition =
                new FadeTransition(Duration.millis(500), userPane);
        fadeTransition.setFromValue(0.3);
        fadeTransition.setToValue(1.0);

        TranslateTransition translateTransition =
                new TranslateTransition(Duration.millis(250), box);
        translateTransition.setFromX(350f - sourceX);
        translateTransition.setToX(0);

        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(250), box);
        scaleTransition.setToX(1f);
        scaleTransition.setToY(1f);

        ScaleTransition scaleTransition1 =
                new ScaleTransition(Duration.millis(250), pane);
        scaleTransition1.setFromY(0);


        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
                fadeTransition,
                translateTransition,
                scaleTransition
        );

        parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                pane.getChildren().remove(box);
                anchorPane.setVisible(false);
            }
        });

        parallelTransition.play();

    }

}
