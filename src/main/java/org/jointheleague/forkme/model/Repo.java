package org.jointheleague.forkme.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.jointheleague.forkme.ForkMe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by league on 10/17/16.
 */
public class Repo {
    private RepoService repoLoader;
    private String currentLogin;
    private RepositoryService repoService;
    private Map<String, Repository> myRepositories = new TreeMap<>();
    private ObservableMap<String, Repository> myRepositoriesObservable = FXCollections.observableMap(myRepositories);
    private Map<String, Map<String, Repository>> userRepositories;
    private Map<String, Map<String, Repository>> organizationRepositories;

    public Repo() {
        ForkMe.currentAccount.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentLogin = newValue.getLogin();

                repoLoader = new RepoService(ForkMe.getCurrentAccount().getClient());
                repoLoader.setOnFailed(event -> {
                    event.getSource().getException().printStackTrace();
                });
                repoLoader.setOnSucceeded(event -> {
                    buildMaps((Collection<Repository>) event.getSource().getValue());
                });

                repoLoader.start();
            }
        });
    }

    private void buildMaps(Collection<Repository> repos) {
        myRepositoriesObservable.clear();

        for (Repository r : repos) {
            switch (r.getOwner().getType()) {
                case User.TYPE_USER:
                    if (r.getOwner().getLogin().equals(currentLogin)) {
                        myRepositoriesObservable.put(r.getName(), r);
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

    private void dumpRepositories() {
        for (String name : getMyRepositories().keySet()) {
            System.out.println(name);
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

    public ObservableMap<String, Repository> getMyRepositories() {
        return myRepositoriesObservable;
    }

    private class RepoService extends Service<Collection<Repository>> {

        public RepoService(GitHubClient client) {
            repoService = new RepositoryService(client);
        }

        @Override
        protected Task<Collection<Repository>> createTask() {
            return new Task<Collection<Repository>>() {
                @Override
                protected Collection<Repository> call() throws Exception {
                    return repoService.getRepositories();
                }
            };
        }

    }
}
