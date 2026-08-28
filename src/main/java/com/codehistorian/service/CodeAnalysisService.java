package com.codehistorian.service;

import com.codehistorian.analysis.SymbolExtractor;
import com.codehistorian.model.CodeSymbol;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CodeAnalysisService {

    private final SymbolExtractor symbolExtractor = new SymbolExtractor();

    public List<CodeSymbol> analyzeRepository(Path root) throws IOException {
        return symbolExtractor.extractFromDirectory(root);
    }
}
