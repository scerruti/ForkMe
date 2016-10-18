package org.jointheleague.forkme.model;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.jointheleague.forkme.ForkMe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;

public class Account {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private static final String IMAGE_CACHE = System.getProperty("user.home") + File.separator + "." + ForkMe.APPLICATION_NAME;

    private String login;
    private String password;
    private GitHubClient client;
    private User user;

    public Account(String username, String password) {
        this.login = username;
        this.password = password;
    }

    Account(String login, String name, Path avatar) {
        this.login= login;
    }

    void login(String password) throws IOException {
        setPassword(password);
        login();
    }

    public void login() throws IOException {
        //Basic authentication
        client = new GitHubClient();
        client.setCredentials(this.login, password);

        UserService userService = new UserService(client);
        user = userService.getUser();

        try {
            downloadAvatar(new URL(user.getAvatarUrl()));
        } catch (IOException e) {
            logger.warn("Unable to download avatar", e);
        }

        PersistentUser.updateAccount(this);
    }

    Path getImageFile(URL avatarUrl) throws IOException {
        Path accountCache = Paths.get(IMAGE_CACHE, user.getLogin());

        if (!Files.exists(accountCache)) {
            Files.createDirectories(accountCache);
        }

        Path fileName = Paths.get(avatarUrl.getPath()).getFileName();
        return accountCache.resolve(fileName);
    }

    private void downloadAvatar(URL avatarUrl) throws IOException {

        Path imageFile = getImageFile(avatarUrl);

        if (Files.exists(imageFile)) {
            HttpURLConnection httpCon = (HttpURLConnection) avatarUrl.openConnection();
            long date = httpCon.getLastModified();

            if (Files.getLastModifiedTime(imageFile).compareTo(FileTime.fromMillis(date)) > 0) {
                return;
            }
        }
        try (InputStream in = avatarUrl.openStream()) {
            Files.copy(in, imageFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    void logout() {
        password = null;
        client = null;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getLogin() {
        return login;
    }

    public String getName() {
        return (user != null?user.getName():null);
    }

    public URL getAvatarUrl() {
        try {
            return (user != null?new URL(user.getAvatarUrl()):null);
        } catch (MalformedURLException e) {
            logger.warn("Unable to get avatar for user {}", login, e);
            return null;
        }
    }

    public GitHubClient getClient() {
        return client;
    }
}
