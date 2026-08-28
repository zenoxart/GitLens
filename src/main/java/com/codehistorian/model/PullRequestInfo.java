package com.codehistorian.model;

import java.time.Instant;
import java.util.List;

public class PullRequestInfo {
    private int number;
    private String title;
    private String body;
    private String state;
    private String author;
    private Instant createdAt;
    private Instant mergedAt;
    private String mergeCommitSha;
    private List<Integer> linkedIssueNumbers;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(Instant mergedAt) {
        this.mergedAt = mergedAt;
    }

    public String getMergeCommitSha() {
        return mergeCommitSha;
    }

    public void setMergeCommitSha(String mergeCommitSha) {
        this.mergeCommitSha = mergeCommitSha;
    }

    public List<Integer> getLinkedIssueNumbers() {
        return linkedIssueNumbers;
    }

    public void setLinkedIssueNumbers(List<Integer> linkedIssueNumbers) {
        this.linkedIssueNumbers = linkedIssueNumbers;
    }
}
