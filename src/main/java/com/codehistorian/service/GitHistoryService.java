package com.codehistorian.service;

import com.codehistorian.git.CommitAnalyzer;
import com.codehistorian.git.DiffAnalyzer;
import com.codehistorian.model.CommitInfo;
import com.codehistorian.model.FileChange;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.List;

public class GitHistoryService {

    private final CommitAnalyzer commitAnalyzer = new CommitAnalyzer();
    private final DiffAnalyzer diffAnalyzer = new DiffAnalyzer();

    public List<CommitInfo> getRecentCommits(Git git, int maxCount) throws GitAPIException {
        return commitAnalyzer.listCommits(git, maxCount);
    }

    public List<FileChange> getFileChanges(Git git, String commitId) throws IOException {
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(commitId));
            return commitAnalyzer.listFileChanges(git, commit);
        }
    }

    public String getDiff(Git git, String commitId) throws IOException {
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(commitId));
            return diffAnalyzer.diffForCommit(git, commit);
        }
    }
}
