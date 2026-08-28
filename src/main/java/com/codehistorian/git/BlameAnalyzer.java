package com.codehistorian.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;

public class BlameAnalyzer {

    public BlameResult blame(Git git, String filePath) throws GitAPIException {
        return git.blame().setFilePath(filePath).call();
    }
}
