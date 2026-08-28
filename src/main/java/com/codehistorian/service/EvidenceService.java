package com.codehistorian.service;

import com.codehistorian.model.CommitInfo;
import com.codehistorian.model.Evidence;
import com.codehistorian.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class EvidenceService {

    public List<Evidence> fromSearchResults(List<SearchResult> results) {
        List<Evidence> evidence = new ArrayList<>();
        for (SearchResult result : results) {
            Evidence item = new Evidence();
            item.setType(switch (result.getType()) {
                case COMMIT -> Evidence.EvidenceType.COMMIT;
                case PULL_REQUEST -> Evidence.EvidenceType.PULL_REQUEST;
                case ISSUE -> Evidence.EvidenceType.ISSUE;
                case FILE -> Evidence.EvidenceType.FILE;
            });
            item.setReferenceId(result.getId());
            item.setLabel(result.getTitle());
            item.setDetail(result.getSnippet());
            evidence.add(item);
        }
        return evidence;
    }

    public Evidence fromCommit(CommitInfo commit) {
        Evidence item = new Evidence();
        item.setType(Evidence.EvidenceType.COMMIT);
        item.setReferenceId(commit.getId());
        item.setLabel(commit.getShortId() + " " + commit.getShortMessage());
        item.setDetail(commit.getFullMessage());
        return item;
    }
}
