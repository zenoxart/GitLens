package com.codehistorian.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DiffAnalyzer {

    public String diffForCommit(Git git, RevCommit commit) throws IOException {
        Repository repository = git.getRepository();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (ObjectReader reader = repository.newObjectReader();
             DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(repository);

            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, commit.getTree());

            if (commit.getParentCount() > 0) {
                CanonicalTreeParser oldTree = new CanonicalTreeParser();
                oldTree.reset(reader, commit.getParent(0).getTree());
                formatter.format(oldTree, newTree);
            } else {
                formatter.format(null, commit.getTree());
            }
        }
        return out.toString(StandardCharsets.UTF_8);
    }
}
