package com.codehistorian.search;

import com.codehistorian.model.CommitInfo;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.List;

public class LuceneIndexer {

    private final LuceneIndex index;

    public LuceneIndexer(LuceneIndex index) {
        this.index = index;
    }

    public void indexCommits(List<CommitInfo> commits) throws IOException {
        try (IndexWriter writer = index.openWriter()) {
            for (CommitInfo commit : commits) {
                Document doc = new Document();
                doc.add(new StringField("type", "commit", Field.Store.YES));
                doc.add(new StringField("id", commit.getId(), Field.Store.YES));
                doc.add(new TextField("title", commit.getShortMessage(), Field.Store.YES));
                doc.add(new TextField("content", commit.getFullMessage(), Field.Store.YES));
                writer.addDocument(doc);
            }
        }
    }

    public void indexFilePaths(List<String> filePaths) throws IOException {
        try (IndexWriter writer = index.openWriter()) {
            for (String path : filePaths) {
                Document doc = new Document();
                doc.add(new StringField("type", "file", Field.Store.YES));
                doc.add(new StringField("id", path, Field.Store.YES));
                doc.add(new TextField("title", path, Field.Store.YES));
                doc.add(new TextField("content", path.replace('/', ' ').replace('.', ' '), Field.Store.YES));
                writer.addDocument(doc);
            }
        }
    }
}
