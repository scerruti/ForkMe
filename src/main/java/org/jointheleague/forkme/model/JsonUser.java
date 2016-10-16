package org.jointheleague.forkme.model;/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class JsonUser {
    private static final String USERS = "USERS";
    private static final int CUTOFF_DAYS = -60;  // Remove accounts older than this many days (negative)

    private String login;
    private String name;
    private String avatar;
    private Calendar lastLogin;

    private JsonUser(PersistentUser user) {
        this.login = user.getLogin();
        this.name = user.getName();
        this.avatar = user.getAvatar().toString();
        this.lastLogin = user.getLastLogin();
    }

    private PersistentUser getPersistent() {
        return new PersistentUser(this);
    }

    static void saveAccounts() {
        Gson gson = new Gson();

        Collection<JsonUser> users = new HashSet<>(PersistentUser.getAccountListSize());
        users.addAll(PersistentUser.getAccounts().stream().map(JsonUser::new).collect(Collectors.toSet()));
        String json = gson.toJson(users);

        Preferences preferences = Preferences.userNodeForPackage(PersistentUser.class);
        preferences.put(USERS, json);
    }

    public static void loadAccounts() {
        Preferences preferences = Preferences.userNodeForPackage(PersistentUser.class);
        String json = preferences.get(USERS, "");

        Gson gson = new Gson();

        Type collectionType = new TypeToken<Collection<JsonUser>>() {
        }.getType();
        Collection<JsonUser> storedUsers = gson.fromJson(json, collectionType);

        if (storedUsers == null) return;

        HashSet<JsonUser> accountsToRemove = new HashSet<>();

        Calendar cutoff = Calendar.getInstance();
        cutoff.add(Calendar.DATE, CUTOFF_DAYS);

        for (JsonUser jsonUser : storedUsers) {
            if (jsonUser.lastLogin.before(cutoff)) {
                accountsToRemove.add(jsonUser);
            } else {
                PersistentUser.addAccount(jsonUser.getPersistent());
            }
        }

        // TODO remove old accounts in background
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public Path getAvatar() {
        return Paths.get(avatar);
    }

    public Calendar getLastLogin() {
        return lastLogin;
    }
}

