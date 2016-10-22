package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jointheleague.forkme.ForkMe;
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
    private AccountBox displayedAccountBox;

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

        ForkMe.currentUser.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                Platform.runLater(() -> ForkMe.show(UserListController.this));
            }
        });
    }

    @FXML
    public void initialize() {
        createAccountBoxes();
    }

    private void createAccountBoxes() {
        for (PersistentUser user : list.values()) {
            AccountBox accountBox = new AccountBox(user);
            accountBox.setOnMouseClicked(event -> {
                AccountBox source = (AccountBox) event.getSource();
                loginAnchorPane.setVisible(true);

                AccountBox accountBox1 = new AccountBox(source.getUser());
                accountBox1.setUserList(UserListController.this);
                UserListController.this.displayedAccountBox = accountBox1;
                loginAccountPane.getChildren().add(accountBox1);
                accountBox1.setLayoutX(source.getLayoutX());
                accountBox1.setLayoutY(source.getLayoutY());

                FadeTransition fadeTransition =
                        new FadeTransition(Duration.millis(500), userPane);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(.3);

                FadeTransition fadeInAccountBox =
                        new FadeTransition(Duration.millis(500), accountBox1);
                fadeInAccountBox.setFromValue(0);
                fadeInAccountBox.setToValue(1);

                sourceX = source.getLayoutX();

                TranslateTransition translateTransition =
                        new TranslateTransition(Duration.millis(500), accountBox1);
                translateTransition.setToX(350 - source.getLayoutX());

                ScaleTransition scaleTransition =
                        new ScaleTransition(Duration.millis(500), accountBox1);
                scaleTransition.setToX(2f);
                scaleTransition.setToY(2f);

                ParallelTransition parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().addAll(
                        fadeTransition,
                        fadeInAccountBox,
                        translateTransition,
                        scaleTransition
                );
                parallelTransition.play();

                accountBox1.showLoginControls();
                accountBox1.hideActionControls();
            });
            userPane.getChildren().add(accountBox);
        }
    }

    void hideAccountLogin() {
        FadeTransition fadeInUserPane =
                new FadeTransition(Duration.millis(500), userPane);
        fadeInUserPane.setFromValue(0.3);
        fadeInUserPane.setToValue(1.0);

        FadeTransition fadeOutAccountBox =
                new FadeTransition(Duration.millis(500), displayedAccountBox);
        fadeOutAccountBox.setFromValue(1);
        fadeOutAccountBox.setToValue(0);

        TranslateTransition translateTransition =
                new TranslateTransition(Duration.millis(250), displayedAccountBox);
        translateTransition.setFromX(350f - sourceX);
        translateTransition.setToX(0);

        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(250), displayedAccountBox);
        scaleTransition.setToX(1f);
        scaleTransition.setToY(1f);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
                fadeInUserPane,
                fadeOutAccountBox,
                translateTransition,
                scaleTransition
        );

        parallelTransition.setOnFinished(event -> {
            loginAccountPane.getChildren().remove(displayedAccountBox);
            loginAnchorPane.setVisible(false);
        });

        parallelTransition.play();

    }

}
