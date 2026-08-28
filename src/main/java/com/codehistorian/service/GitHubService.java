package com.codehistorian.service;

import com.codehistorian.github.GitHubClient;
import com.codehistorian.github.IssueFetcher;
import com.codehistorian.github.PullRequestFetcher;
import com.codehistorian.model.IssueInfo;
import com.codehistorian.model.PullRequestInfo;

import java.io.IOException;
import java.util.List;

public class GitHubService {

    private GitHubClient client;
    private PullRequestFetcher pullRequestFetcher;
    private IssueFetcher issueFetcher;

    public void connect(String token) {
        this.client = new GitHubClient(token);
        this.pullRequestFetcher = new PullRequestFetcher(client);
        this.issueFetcher = new IssueFetcher(client);
    }

    public boolean isConnected() {
        return client != null;
    }

    public List<PullRequestInfo> fetchPullRequests(String owner, String repo) throws IOException, InterruptedException {
        return pullRequestFetcher.fetchPullRequests(owner, repo);
    }

    public List<IssueInfo> fetchIssues(String owner, String repo) throws IOException, InterruptedException {
        return issueFetcher.fetchIssues(owner, repo);
    }
}
