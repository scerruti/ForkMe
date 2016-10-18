package org.jointheleague.forkme.model;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.jointheleague.forkme.ForkMe;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by league on 10/17/16.
 */
public class Repo {
    private String currentUserLogin;
    private RepositoryService repoService;
    private Map<String, Repository> myRepositories;
    private Map<String, Map<String, Repository>> userRepositories;
    private Map<String, Map<String, Repository>> organizationRepositories;

    public Repo() {
        if (ForkMe.getCurrentAccount() != null) {
            currentUserLogin = ForkMe.getCurrentAccount().getLogin();
            repoService = new RepositoryService(ForkMe.getCurrentAccount().getClient());
            loadRepositories();
//            loadOrganizations();
//            loadRepositoriesForOrganizations();
        }
    }

    private void loadRepositories() {
        try {
            for (Repository r : repoService.getRepositories()) {
                switch (r.getOwner().getType()) {
                    case User.TYPE_USER:
                        if (r.getOwner().getLogin().equals(currentUserLogin)) {
                            if (myRepositories == null) {
                                myRepositories = new HashMap<>();
                            }
                            myRepositories.put(r.getName(), r);
                        } else {
                            if (userRepositories == null) {
                                userRepositories = new HashMap<>();
                            }
                            if (!userRepositories.containsKey(r.getOwner().getLogin())) {
                                userRepositories.put(r.getOwner().getLogin(), new HashMap<>());
                            }
                            userRepositories.get(r.getOwner().getLogin()).put(r.getName(),r);
                        }
                        break;
                    case User.TYPE_ORG:
                        if (organizationRepositories == null) {
                            organizationRepositories = new HashMap<>();
                        }
                        if (!organizationRepositories.containsKey(r.getOwner().getLogin())) {
                            organizationRepositories.put(r.getOwner().getLogin(), new HashMap<>());
                        }
                        organizationRepositories.get(r.getOwner().getLogin()).put(r.getName(),r);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String name : myRepositories.keySet()) {
            System.out.println(name);
        }

        System.out.println("\nUsers");
        for (String user : userRepositories.keySet()) {
            System.out.println(user);
            for (String name : userRepositories.get(user).keySet()) {
                System.out.println("\t"+name);
            }
        }

        System.out.println("\nOrganisations");
        for (String organization : organizationRepositories.keySet()) {
            System.out.println(organization);
            for (String name : organizationRepositories.get(organization).keySet()) {
                System.out.println("\t"+name);
            }
        }

    }


}
