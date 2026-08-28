package com.codehistorian.ai;

public interface LlmClient {
    String complete(String systemPrompt, String userPrompt) throws Exception;
}
