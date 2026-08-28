package com.codehistorian.git;

import com.codehistorian.model.CommitInfo;
import com.codehistorian.model.FileChange;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CommitAnalyzer {

    public List<CommitInfo> listCommits(Git git, int maxCount) throws GitAPIException {
        List<CommitInfo> result = new ArrayList<>();
        Iterable<RevCommit> commits = maxCount > 0
                ? git.log().setMaxCount(maxCount).call()
                : git.log().call();
        for (RevCommit commit : commits) {
            result.add(toCommitInfo(commit));
        }
        return result;
    }

    public CommitInfo toCommitInfo(RevCommit commit) {
        CommitInfo info = new CommitInfo();
        info.setId(commit.getName());
        info.setShortId(commit.getName().substring(0, 7));
        info.setAuthorName(commit.getAuthorIdent().getName());
        info.setAuthorEmail(commit.getAuthorIdent().getEmailAddress());
        info.setCommitTime(Instant.ofEpochSecond(commit.getCommitTime()));
        info.setShortMessage(commit.getShortMessage());
        info.setFullMessage(commit.getFullMessage());

        List<String> parents = new ArrayList<>();
        for (RevCommit parent : commit.getParents()) {
            parents.add(parent.getName());
        }
        info.setParentIds(parents);
        return info;
    }

    public List<FileChange> listFileChanges(Git git, RevCommit commit) throws IOException {
        List<FileChange> changes = new ArrayList<>();
        if (commit.getParentCount() == 0) {
            return changes;
        }
        Repository repository = git.getRepository();
        RevCommit parent = commit.getParent(0);

        try (ObjectReader reader = repository.newObjectReader();
             DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {

            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, parent.getTree());
            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, commit.getTree());

            formatter.setRepository(repository);
            List<DiffEntry> entries = formatter.scan(oldTree, newTree);
            for (DiffEntry entry : entries) {
                FileChange change = new FileChange();
                change.setChangeType(mapChangeType(entry.getChangeType()));
                change.setPath(entry.getNewPath());
                change.setOldPath(entry.getOldPath());
                for (Edit edit : formatter.toFileHeader(entry).toEditList()) {
                    change.setAdditions(change.getAdditions() + (edit.getEndB() - edit.getBeginB()));
                    change.setDeletions(change.getDeletions() + (edit.getEndA() - edit.getBeginA()));
                }
                changes.add(change);
            }
        }
        return changes;
    }

    private FileChange.ChangeType mapChangeType(DiffEntry.ChangeType type) {
        return switch (type) {
            case ADD -> FileChange.ChangeType.ADD;
            case MODIFY -> FileChange.ChangeType.MODIFY;
            case DELETE -> FileChange.ChangeType.DELETE;
            case RENAME -> FileChange.ChangeType.RENAME;
            case COPY -> FileChange.ChangeType.COPY;
        };
    }
}
