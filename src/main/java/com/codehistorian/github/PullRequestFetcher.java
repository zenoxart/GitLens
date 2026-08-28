package com.codehistorian.github;

import com.codehistorian.model.PullRequestInfo;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PullRequestFetcher {

    private final GitHubClient client;

    public PullRequestFetcher(GitHubClient client) {
        this.client = client;
    }

    public List<PullRequestInfo> fetchPullRequests(String owner, String repo) throws IOException, InterruptedException {
        List<PullRequestInfo> result = new ArrayList<>();
        JsonNode array = client.get("/repos/" + owner + "/" + repo + "/pulls?state=all&per_page=100");
        for (JsonNode node : array) {
            PullRequestInfo pr = new PullRequestInfo();
            pr.setNumber(node.path("number").asInt());
            pr.setTitle(node.path("title").asText());
            pr.setBody(node.path("body").asText(""));
            pr.setState(node.path("state").asText());
            pr.setAuthor(node.path("user").path("login").asText());

            String createdAt = node.path("created_at").asText(null);
            if (createdAt != null) {
                pr.setCreatedAt(Instant.parse(createdAt));
            }
            String mergedAt = node.path("merged_at").asText(null);
            if (mergedAt != null && !mergedAt.isBlank()) {
                pr.setMergedAt(Instant.parse(mergedAt));
            }
            pr.setMergeCommitSha(node.path("merge_commit_sha").asText(null));
            result.add(pr);
        }
        return result;
    }
}
