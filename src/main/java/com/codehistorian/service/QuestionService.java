package com.codehistorian.service;

import com.codehistorian.ai.AnswerGenerator;
import com.codehistorian.model.AiAnswer;
import com.codehistorian.model.Evidence;
import com.codehistorian.model.SearchResult;

import java.util.List;

public class QuestionService {

    private final SearchService searchService;
    private final EvidenceService evidenceService;
    private final AnswerGenerator answerGenerator;

    public QuestionService(SearchService searchService, EvidenceService evidenceService, AnswerGenerator answerGenerator) {
        this.searchService = searchService;
        this.evidenceService = evidenceService;
        this.answerGenerator = answerGenerator;
    }

    public AiAnswer ask(String question) throws Exception {
        List<SearchResult> results = searchService.search(question, 10);
        List<Evidence> evidence = evidenceService.fromSearchResults(results);
        return answerGenerator.generate(question, evidence);
    }
}
