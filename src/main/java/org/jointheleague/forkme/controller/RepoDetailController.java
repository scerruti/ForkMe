package org.jointheleague.forkme.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.Repository;

import java.io.IOException;

/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */
public class RepoDetailController extends GridPane {
    private final Repository repo;
    public TextField repoName;
    public TextField repoOwner;
    public TextField repoDescription;
    public TextArea readMe;

    RepoDetailController(Repository repo) {
        this.repo = repo;

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/RepoDetail.fxml"),
                ForkMe.getResources());
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
        repoName.setText(repo.getName());
        String ownerName = repo.getOwner().getName();
        if (ownerName == null || ownerName.isEmpty()) {
            ownerName = repo.getOwner().getLogin();
        }
        repoOwner.setText(ownerName);
        repoDescription.setText(repo.getDescription());
        readMe.setText("Loading the readme file.");
    }

}
