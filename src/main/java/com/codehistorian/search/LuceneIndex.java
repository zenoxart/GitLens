package com.codehistorian.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LuceneIndex {

    private final Directory directory;
    private final StandardAnalyzer analyzer = new StandardAnalyzer();

    public LuceneIndex(Path indexPath) throws IOException {
        Files.createDirectories(indexPath);
        this.directory = FSDirectory.open(indexPath);
    }

    public Directory getDirectory() {
        return directory;
    }

    public StandardAnalyzer getAnalyzer() {
        return analyzer;
    }

    public IndexWriter openWriter() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, config);
    }

    public void close() throws IOException {
        directory.close();
    }
}
