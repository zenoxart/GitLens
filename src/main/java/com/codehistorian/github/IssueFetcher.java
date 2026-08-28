package com.codehistorian.github;

import com.codehistorian.model.IssueInfo;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class IssueFetcher {

    private final GitHubClient client;

    public IssueFetcher(GitHubClient client) {
        this.client = client;
    }

    public List<IssueInfo> fetchIssues(String owner, String repo) throws IOException, InterruptedException {
        List<IssueInfo> result = new ArrayList<>();
        JsonNode array = client.get("/repos/" + owner + "/" + repo + "/issues?state=all&per_page=100");
        for (JsonNode node : array) {
            if (node.has("pull_request")) {
                continue;
            }
            IssueInfo issue = new IssueInfo();
            issue.setNumber(node.path("number").asInt());
            issue.setTitle(node.path("title").asText());
            issue.setBody(node.path("body").asText(""));
            issue.setState(node.path("state").asText());
            issue.setAuthor(node.path("user").path("login").asText());

            String createdAt = node.path("created_at").asText(null);
            if (createdAt != null) {
                issue.setCreatedAt(Instant.parse(createdAt));
            }
            List<String> labels = new ArrayList<>();
            node.path("labels").forEach(label -> labels.add(label.path("name").asText()));
            issue.setLabels(labels);
            result.add(issue);
        }
        return result;
    }
}
