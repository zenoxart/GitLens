package com.codehistorian.search;

import com.codehistorian.model.SearchResult;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneSearchEngine {

    private final LuceneIndex index;

    public LuceneSearchEngine(LuceneIndex index) {
        this.index = index;
    }

    public List<SearchResult> search(String queryText, int maxResults) throws IOException, ParseException {
        List<SearchResult> results = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(index.getDirectory())) {
            IndexSearcher searcher = new IndexSearcher(reader);
            StoredFields storedFields = searcher.storedFields();

            MultiFieldQueryParser parser = new MultiFieldQueryParser(
                    new String[]{"title", "content"}, index.getAnalyzer());
            Query query = parser.parse(queryText);
            TopDocs topDocs = searcher.search(query, maxResults);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = storedFields.document(scoreDoc.doc);
                SearchResult result = new SearchResult();
                result.setType("commit".equals(doc.get("type"))
                        ? SearchResult.ResultType.COMMIT
                        : SearchResult.ResultType.FILE);
                result.setId(doc.get("id"));
                result.setTitle(doc.get("title"));
                result.setSnippet(doc.get("content"));
                result.setScore(scoreDoc.score);
                results.add(result);
            }
        }
        return results;
    }
}
