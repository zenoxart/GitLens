package com.codehistorian.service;

import com.codehistorian.git.JGitRepositoryReader;
import com.codehistorian.model.RepositoryInfo;
import com.codehistorian.storage.ProjectIndexManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;

public class RepositoryService {

    private final JGitRepositoryReader repositoryReader = new JGitRepositoryReader();

    private Git currentGit;
    private RepositoryInfo currentRepositoryInfo;
    private ProjectIndexManager indexManager;

    public RepositoryInfo openRepository(Path path) throws IOException, GitAPIException {
        close();
        currentGit = repositoryReader.open(path);
        currentRepositoryInfo = repositoryReader.buildRepositoryInfo(currentGit);
        indexManager = new ProjectIndexManager(currentRepositoryInfo.getLocalPath());
        indexManager.ensureDirectories();
        return currentRepositoryInfo;
    }

    public RepositoryInfo cloneRepository(String remoteUrl, Path targetDirectory) throws GitAPIException, IOException {
        close();
        currentGit = repositoryReader.clone(remoteUrl, targetDirectory);
        currentRepositoryInfo = repositoryReader.buildRepositoryInfo(currentGit);
        indexManager = new ProjectIndexManager(currentRepositoryInfo.getLocalPath());
        indexManager.ensureDirectories();
        return currentRepositoryInfo;
    }

    public Git getCurrentGit() {
        return currentGit;
    }

    public RepositoryInfo getCurrentRepositoryInfo() {
        return currentRepositoryInfo;
    }

    public ProjectIndexManager getIndexManager() {
        return indexManager;
    }

    public void close() {
        if (currentGit != null) {
            currentGit.close();
            currentGit = null;
        }
    }
}
