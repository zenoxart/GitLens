package com.codehistorian.analysis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyAnalyzer {

    private final Map<String, Set<String>> dependencies = new ConcurrentHashMap<>();

    public Map<String, Set<String>> getDependencies() {
        return dependencies;
    }
}
