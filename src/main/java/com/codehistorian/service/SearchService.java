package com.codehistorian.service;

import com.codehistorian.model.CommitInfo;
import com.codehistorian.model.SearchResult;
import com.codehistorian.search.LuceneIndex;
import com.codehistorian.search.LuceneIndexer;
import com.codehistorian.search.LuceneSearchEngine;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SearchService {

    private LuceneIndex index;
    private LuceneIndexer indexer;
    private LuceneSearchEngine searchEngine;

    public void open(Path indexPath) throws IOException {
        index = new LuceneIndex(indexPath);
        indexer = new LuceneIndexer(index);
        searchEngine = new LuceneSearchEngine(index);
    }

    public void indexCommits(List<CommitInfo> commits) throws IOException {
        indexer.indexCommits(commits);
    }

    public void indexFilePaths(List<String> paths) throws IOException {
        indexer.indexFilePaths(paths);
    }

    public List<SearchResult> search(String query, int maxResults) throws IOException, ParseException {
        return searchEngine.search(query, maxResults);
    }

    public void close() throws IOException {
        if (index != null) {
            index.close();
        }
    }
}
