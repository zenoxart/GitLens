package com.codehistorian.git;

import com.codehistorian.model.RepositoryInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class JGitRepositoryReader {

    public Git open(Path repoPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.findGitDir(repoPath.toFile())
                .readEnvironment()
                .build();
        return new Git(repository);
    }

    public Git clone(String remoteUrl, Path targetDirectory) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(targetDirectory.toFile())
                .call();
    }

    public RepositoryInfo buildRepositoryInfo(Git git) throws IOException, GitAPIException {
        Repository repository = git.getRepository();
        File workTree = repository.getWorkTree();

        RepositoryInfo info = new RepositoryInfo();
        info.setName(workTree.getName());
        info.setLocalPath(workTree.toPath());
        info.setCurrentBranch(repository.getBranch());

        int count = 0;
        Set<String> contributors = new LinkedHashSet<>();
        String lastCommitId = null;
        for (RevCommit commit : git.log().call()) {
            if (lastCommitId == null) {
                lastCommitId = commit.getName();
            }
            contributors.add(commit.getAuthorIdent().getName());
            count++;
        }
        info.setCommitCount(count);
        info.setContributors(new ArrayList<>(contributors));
        info.setLastCommitId(lastCommitId);
        return info;
    }
}
