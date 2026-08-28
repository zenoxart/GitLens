package com.codehistorian.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectIndexManager {

    private static final String INDEX_DIR_NAME = ".code-history";

    private final Path repositoryRoot;

    public ProjectIndexManager(Path repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    }

    public Path getIndexRoot() {
        return repositoryRoot.resolve(INDEX_DIR_NAME);
    }

    public Path getRepositoryCacheFile() {
        return getIndexRoot().resolve("repository.json");
    }

    public Path getLuceneIndexPath() {
        return getIndexRoot().resolve("index").resolve("lucene");
    }

    public Path getGitHubCacheDir() {
        return getIndexRoot().resolve("github");
    }

    public Path getSymbolsCacheFile() {
        return getIndexRoot().resolve("symbols").resolve("symbols.json");
    }

    public Path getDependenciesCacheFile() {
        return getIndexRoot().resolve("dependencies").resolve("graph.json");
    }

    public void ensureDirectories() throws IOException {
        Files.createDirectories(getLuceneIndexPath());
        Files.createDirectories(getGitHubCacheDir().resolve("pull-requests"));
        Files.createDirectories(getGitHubCacheDir().resolve("issues"));
        Files.createDirectories(getSymbolsCacheFile().getParent());
        Files.createDirectories(getDependenciesCacheFile().getParent());
        Files.createDirectories(getIndexRoot().resolve("cache"));
    }
}
