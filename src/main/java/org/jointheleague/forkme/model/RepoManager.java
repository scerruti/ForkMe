package org.jointheleague.forkme.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jointheleague.forkme.ForkMe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/*
 * Copyright 2016, The League of Amazing Programmers, All Rights Reserved
 */
public class RepoManager {
    private GitHubRepoService repoLoader;
    private String currentLogin;
    private RepositoryService repoService;
    private List<Repository> myRepositories = new ArrayList<>();
    private ObservableList<Repository> myRepositoriesObservable = FXCollections.observableArrayList(myRepositories);
    private Map<String, Map<String, org.eclipse.egit.github.core.Repository>> userRepositories;
    private Map<String, Map<String, org.eclipse.egit.github.core.Repository>> organizationRepositories;

    public RepoManager() {
        ForkMe.currentAccount.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentLogin = newValue.getLogin();
                System.out.println(currentLogin);

                repoLoader = new GitHubRepoService(ForkMe.getCurrentAccount().getClient());
                repoLoader.setOnFailed(event -> event.getSource().getException().printStackTrace());
                repoLoader.setOnSucceeded(event -> buildMaps((Collection<org.eclipse.egit.github.core.Repository>) event.getSource().getValue()));

                repoLoader.start();
            }
        });
    }

    private void buildMaps(Collection<org.eclipse.egit.github.core.Repository> repos) {
        myRepositoriesObservable.clear();

        for (org.eclipse.egit.github.core.Repository r : repos) {
            switch (r.getOwner().getType()) {
                case User.TYPE_USER:
                    if (r.getOwner().getLogin().equals(currentLogin)) {
                        myRepositoriesObservable.add(new Repository(r));
                    } else {
                        if (userRepositories == null) {
                            userRepositories = new HashMap<>();
                        }
                        if (!userRepositories.containsKey(r.getOwner().getLogin())) {
                            userRepositories.put(r.getOwner().getLogin(), new HashMap<>());
                        }
                        userRepositories.get(r.getOwner().getLogin()).put(r.getName(), r);
                    }
                    break;
                case User.TYPE_ORG:
                    if (organizationRepositories == null) {
                        organizationRepositories = new HashMap<>();
                    }
                    if (!organizationRepositories.containsKey(r.getOwner().getLogin())) {
                        organizationRepositories.put(r.getOwner().getLogin(), new HashMap<>());
                    }
                    organizationRepositories.get(r.getOwner().getLogin()).put(r.getName(), r);
                    break;
            }
        }

    }

    public void dumpRepositories() {
        for (Repository r : myRepositories) {
            System.out.println(r.getName());
        }

        System.out.println("\nUsers");
        for (String user : userRepositories.keySet()) {
            System.out.println(user);
            for (String name : userRepositories.get(user).keySet()) {
                System.out.println("\t" + name);
            }
        }

        System.out.println("\nOrganisations");
        for (String organization : organizationRepositories.keySet()) {
            System.out.println(organization);
            for (String name : organizationRepositories.get(organization).keySet()) {
                System.out.println("\t" + name);
            }
        }

    }

    public ObservableList<Repository> getMyRepositories() {
        return myRepositoriesObservable;
    }

    public org.eclipse.jgit.lib.Repository clone(Repository myRepo) {
        org.eclipse.jgit.lib.Repository repository = null;
        String url = myRepo.getCloneUrl();

        File repoDir = Paths.get(System.getProperty("user.home"), "Desktop", myRepo.getName()).toFile();
        boolean match = false;
        while (repoDir.exists() && !match) {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.setGitDir(repoDir);
            try {
                repository = repositoryBuilder.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Config storedConfig = repository.getConfig();
            Set<String> remotes = storedConfig.getSubsections("remote");

            for (String remoteName : remotes) {
                String remoteUrl = storedConfig.getString("remote", remoteName, "url");
                if (url.equals(remoteUrl)) {
                    match = true;
                    break;
                }
            }

            // Append owner name to repo
            // Append numbers
        }


        try {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(repoDir)
                    .call();

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return repository;
    }

    private class GitHubRepoService extends Service<Collection<org.eclipse.egit.github.core.Repository>> {

        GitHubRepoService(GitHubClient client) {
            repoService = new RepositoryService(client);
        }

        @Override
        protected Task<Collection<org.eclipse.egit.github.core.Repository>> createTask() {
            return new Task<Collection<org.eclipse.egit.github.core.Repository>>() {
                @Override
                protected Collection<org.eclipse.egit.github.core.Repository> call() throws Exception {
                    return repoService.getRepositories();
                }
            };
        }

    }
}
