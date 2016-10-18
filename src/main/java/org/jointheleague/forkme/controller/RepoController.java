package org.jointheleague.forkme.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import org.eclipse.egit.github.core.Repository;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.PersistentUser;
import org.jointheleague.forkme.model.Repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by league on 10/17/16.
 */
public class RepoController extends AnchorPane {
    private final Repo repo;
    public ListView repoList;
    private List<String> myRepoList = new ArrayList<>();
    private ObservableList<String> myRepositoryList = FXCollections.observableList(myRepoList);

    public RepoController(Repo repo) {
        this.repo = repo;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/RepoView.fxml"));
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
                if (newValue != null) {
                    Platform.runLater(() -> ForkMe.show(RepoController.this));
                }
            }
        });
    }

    @FXML
    public void initialize() {
        repo.getMyRepositories().addListener(new MapChangeListener<String, Repository>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Repository> change) {
                System.out.println("-- change called --");
                if (change.wasAdded()) {
                    myRepositoryList.add(change.getKey());
                } else if (change.wasRemoved()) {
                    myRepositoryList.remove(change.getKey());
                }
            }
        });
        myRepoList.addAll(repo.getMyRepositories().keySet());
        repoList.setItems(myRepositoryList);
    }

    @FXML
    public void printRepos() {

    }
}
