package org.jointheleague.forkme.model;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jointheleague.forkme.ForkMe;

import java.io.IOException;

public class LoginService extends Service<Void> {
    private String login;
    private String text;

    public LoginService(String login, String text) {
        this.login = login;
        this.text = text;
    }

    protected Task<Void> createTask() {

        return new Task<Void>() {
            protected Void call() throws IOException {

                Account account = new Account(login, text);
                account.login();
                ForkMe.setCurrentUser(PersistentUser.getAccount(login));
                ForkMe.setCurrentUser(account);
                return null;
            }
        };
    }
}
