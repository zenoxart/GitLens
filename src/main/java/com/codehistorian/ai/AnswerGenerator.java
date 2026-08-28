package com.codehistorian.ai;

import com.codehistorian.model.AiAnswer;
import com.codehistorian.model.Evidence;

import java.util.List;

public class AnswerGenerator {

    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder = new PromptBuilder();

    public AnswerGenerator(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    public AiAnswer generate(String question, List<Evidence> evidence) throws Exception {
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(question, evidence);
        String answerText = llmClient.complete(systemPrompt, userPrompt);

        AiAnswer answer = new AiAnswer();
        answer.setQuestion(question);
        answer.setAnswerText(answerText);
        answer.setEvidence(evidence);
        return answer;
    }
}
