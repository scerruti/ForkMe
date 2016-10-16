package org.jointheleague.forkme.model;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PersistentUser {
    private static final Logger logger = LoggerFactory.getLogger(PersistentUser.class);

    private static final Map<String, PersistentUser> accountList = new HashMap<>();
    public static final ObservableMap<String, PersistentUser> accounts = FXCollections.observableMap(accountList);
    private static int accountListSize;
    private StringProperty login;
    private StringProperty name;
    private ObjectProperty<Path> avatar;
    private Calendar lastLogin;

    private PersistentUser(Account account) {
        this.login = new SimpleStringProperty(account.getLogin());
        this.name = new SimpleStringProperty(account.getName());
        Path imageFile = null;
        try {
            imageFile = account.getImageFile(account.getAvatarUrl());
        } catch (IOException e) {
            logger.warn("Unable to get image file for avatar.", e);
        }
        this.avatar = new SimpleObjectProperty<>(imageFile);
        this.lastLogin = Calendar.getInstance();
    }

    public PersistentUser(JsonUser user) {
        this.login = new SimpleStringProperty(user.getLogin());
        this.name = new SimpleStringProperty(user.getName());
        this.avatar = new SimpleObjectProperty<>(user.getAvatar());
        this.lastLogin = user.getLastLogin();
    }

    public static int getAccountListSize() {
        return accountList.size();
    }

    public static Collection<PersistentUser> getAccounts() {
        return accountList.values();
    }

    public static void updateAccount(Account account) {
        PersistentUser existing = accountList.get(account.getLogin());
        if (existing == null) {
            PersistentUser newAccount = new PersistentUser(account);
            accountList.put(account.getLogin(), newAccount);
        } else {
            existing.update(account);
        }
        JsonUser.saveAccounts();
    }

    public Account toAccount() {
        return new Account(login.toString(), "");
    }

    private void update(Account account) {
        if (!this.name.getValue().equals(account.getName())) {
            this.name.setValue(account.getName());
        }
        if (!this.login.getValue().equals(account.getLogin())) {
            this.login.setValue(account.getLogin());
        }
        Path avatar = null;
        try {
            avatar = account.getImageFile(account.getAvatarUrl());
        } catch (IOException e) {
            logger.error("Unable to get path for image url", e);
        }
        if (!this.avatar.getValue().equals(avatar)) {
            this.avatar.setValue(avatar);
        }
        this.lastLogin.equals(Calendar.getInstance());
    }


    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Path getAvatar() {
        return avatar.get();
    }

    public ObjectProperty<Path> avatarProperty() {
        return avatar;
    }

    public String getLogin() {
        return login.get();
    }

    public Calendar getLastLogin() {
        return lastLogin;
    }

    public static void addAccount(PersistentUser persistent) {
        accountList.put(persistent.getLogin(), persistent);
    }

    public static PersistentUser getAccount(String login) {
        return accountList.get(login);
    }
}