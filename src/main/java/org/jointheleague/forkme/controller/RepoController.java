package org.jointheleague.forkme.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.RepoManager;
import org.jointheleague.forkme.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */
public class RepoController extends AnchorPane {
    private static final Logger logger = LoggerFactory.getLogger(RepoController.class);

    private final RepoManager repoManager;
    public ListView repoList;
    public Tab infoTab;
    private List<Repository> myRepoList = new ArrayList<>();
    private ObservableList<Repository> myRepositoryList = FXCollections.observableList(myRepoList);

    public RepoController(RepoManager repoManager) {
        this.repoManager = repoManager;

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
        repoManager.getMyRepositories().addListener(new ListChangeListener<Repository>() {
            @Override
            public void onChanged(Change<? extends Repository> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        //permutate
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        for (Repository repository : change.getRemoved()) {
                            myRepositoryList.remove(repository);
                        }
                        for (Repository repository : change.getAddedSubList()) {
                            myRepositoryList.add(repository);
                        }
                    }
                }
            }
        });
        myRepoList.addAll(repoManager.getMyRepositories());
        repoList.setItems(myRepositoryList);
        repoList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Repository>() {
                    @Override
                    public void changed(ObservableValue<? extends Repository> observable, Repository oldValue, Repository newValue) {
                        infoTab.setContent(
                                new RepoDetailController(newValue));

                    }
                });
    }

    @FXML
    public void cloneRepo() {
        Object selectedItem = repoList.getSelectionModel().getSelectedItem();
        if (selectedItem instanceof Repository) {
            ((Repository) selectedItem).clone(repoManager);
        } else {
            logger.error("Unknown item returned from selection {}", selectedItem);
        }
    }
}
