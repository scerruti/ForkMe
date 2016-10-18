package org.jointheleague.forkme.controller;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
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

        ForkMe.currentUser.addListener(new ChangeListener<PersistentUser>() {
            @Override
            public void changed(ObservableValue<? extends PersistentUser> observable, PersistentUser oldValue, PersistentUser newValue) {
                if (newValue == null) {
                    Platform.runLater(() -> ForkMe.show(UserListController.this));
                }
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
        Pane pane = (Pane) displayedAccountBox.getScene().lookup("#loginAccountPane");
        AnchorPane anchorPane = (AnchorPane) displayedAccountBox.getScene().lookup("#loginAnchorPane");
        FlowPane userPane = (FlowPane) displayedAccountBox.getScene().lookup("#userPane");

        FadeTransition fadeTransition =
                new FadeTransition(Duration.millis(500), userPane);
        fadeTransition.setFromValue(0.3);
        fadeTransition.setToValue(1.0);

        TranslateTransition translateTransition =
                new TranslateTransition(Duration.millis(250), displayedAccountBox);
        translateTransition.setFromX(350f - sourceX);
        translateTransition.setToX(0);

        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(250), displayedAccountBox);
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

        parallelTransition.setOnFinished(event -> {
            pane.getChildren().remove(displayedAccountBox);
            anchorPane.setVisible(false);
        });

        parallelTransition.play();

    }

}
