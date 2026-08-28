package com.codehistorian.model;

public class Developer {
    private String name;
    private String email;
    private int commitCount;
    private int linesChanged;
    private int filesTouched;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    public int getLinesChanged() {
        return linesChanged;
    }

    public void setLinesChanged(int linesChanged) {
        this.linesChanged = linesChanged;
    }

    public int getFilesTouched() {
        return filesTouched;
    }

    public void setFilesTouched(int filesTouched) {
        this.filesTouched = filesTouched;
    }
}
