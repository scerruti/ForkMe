package org.jointheleague.forkme.model;


import org.eclipse.egit.github.core.User;

/**
 * Created by league on 10/23/16.
 */
public class Repository {
    org.eclipse.egit.github.core.Repository gitHubRepository;
    org.eclipse.jgit.lib.Repository localRepository;

    public Repository(org.eclipse.egit.github.core.Repository gitHubRepository, org.eclipse.jgit.lib.Repository localRepository) {
        this.gitHubRepository = gitHubRepository;
        this.localRepository = localRepository;
    }

    public Repository(org.eclipse.egit.github.core.Repository r) {
        this(r, null);
    }

    public String getName() {
        return gitHubRepository.getName();
    }

    public String getCloneUrl() {
        return gitHubRepository.getCloneUrl();
    }

    public User getOwner() {
        return gitHubRepository.getOwner();
    }

    public String getDescription() {
        return gitHubRepository.getDescription();
    }

    @Override
    public String toString() {
        return getName();
    }

    public void clone(RepoManager repoManager) {
        if (localRepository == null) {
            localRepository = repoManager.clone(this);
        }
    }
}
