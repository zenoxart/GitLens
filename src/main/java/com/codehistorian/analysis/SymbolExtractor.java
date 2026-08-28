package com.codehistorian.analysis;

import com.codehistorian.model.CodeSymbol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SymbolExtractor {

    private final JavaSourceAnalyzer analyzer = new JavaSourceAnalyzer();

    public List<CodeSymbol> extractFromDirectory(Path root) throws IOException {
        List<CodeSymbol> symbols = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(root)) {
            List<Path> javaFiles = paths.filter(p -> p.toString().endsWith(".java")).toList();
            for (Path javaFile : javaFiles) {
                try {
                    symbols.addAll(analyzer.analyzeFile(javaFile));
                } catch (Exception e) {
                    // skip files that fail to parse
                }
            }
        }
        return symbols;
    }
}
