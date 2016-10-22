package org.jointheleague.forkme.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.eclipse.egit.github.core.Repository;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.Repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */
public class RepoController extends AnchorPane {
    private final Repo repo;
    public ListView repoList;
    public Tab infoTab;
    private List<String> myRepoList = new ArrayList<>();
    private ObservableList<String> myRepositoryList = FXCollections.observableList(myRepoList);

    public RepoController(Repo repo) {
        this.repo = repo;

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/RepoView.fxml"),
                ForkMe.getResources());
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        ForkMe.currentUser.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> ForkMe.show(RepoController.this));
            }
        });
    }

    @FXML
    public void initialize() {
        repo.getMyRepositories().addListener(new MapChangeListener<String, Repository>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Repository> change) {
                if (change.wasAdded()) {
                    myRepositoryList.add(change.getKey());
                } else if (change.wasRemoved()) {
                    myRepositoryList.remove(change.getKey());
                }
            }
        });
        myRepoList.addAll(repo.getMyRepositories().keySet());
        repoList.setItems(myRepositoryList);
        repoList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> infoTab.setContent(new RepoDetailController(repo.getRepository(newValue.toString()))));
    }

    @FXML
    public void cloneRepo() {
        String repoName = (String) repoList.getSelectionModel().getSelectedItem();
        repo.clone(repoName);
    }
}
