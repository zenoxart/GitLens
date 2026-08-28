package com.codehistorian.ai;

import com.codehistorian.model.Evidence;

import java.util.List;

public class PromptBuilder {

    public String buildSystemPrompt() {
        return "You are GitLens, an AI codebase historian. Answer the developer's question using ONLY the "
                + "evidence provided below (commits, pull requests, issues, files). Cite the evidence "
                + "items you relied on. If the evidence does not answer the question, say so.";
    }

    public String buildUserPrompt(String question, List<Evidence> evidence) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Question: ").append(question).append("\n\nEvidence:\n");
        for (Evidence item : evidence) {
            prompt.append("- [").append(item.getType()).append("] ")
                    .append(item.getLabel()).append(": ")
                    .append(item.getDetail()).append("\n");
        }
        return prompt.toString();
    }
}
