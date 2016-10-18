package org.jointheleague.forkme.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import org.jointheleague.forkme.ForkMe;
import org.jointheleague.forkme.model.PersistentUser;

/**
 * Created by league on 10/17/16.
 */
public class RepoController extends SplitPane {
    @FXML
    public void initialize() {
        ForkMe.currentUser.addListener(new ChangeListener<PersistentUser>() {
            @Override
            public void changed(ObservableValue<? extends PersistentUser> observable, PersistentUser oldValue, PersistentUser newValue) {
                if (newValue == null) {
                    Platform.runLater(() -> RepoController.this.setVisible(false));
                } else {

                }
            }
        });
    }
}
