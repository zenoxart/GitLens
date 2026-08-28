package com.codehistorian.analysis;

import com.codehistorian.model.CodeSymbol;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaSourceAnalyzer {

    public List<CodeSymbol> analyzeFile(Path javaFile) throws IOException {
        List<CodeSymbol> symbols = new ArrayList<>();
        CompilationUnit unit = StaticJavaParser.parse(javaFile);

        unit.findAll(ClassOrInterfaceDeclaration.class).forEach(type -> {
            CodeSymbol symbol = new CodeSymbol();
            symbol.setName(type.getNameAsString());
            symbol.setKind(type.isInterface() ? CodeSymbol.SymbolKind.INTERFACE : CodeSymbol.SymbolKind.CLASS);
            symbol.setFilePath(javaFile.toString());
            type.getRange().ifPresent(range -> {
                symbol.setStartLine(range.begin.line);
                symbol.setEndLine(range.end.line);
            });
            symbols.add(symbol);
        });

        unit.findAll(MethodDeclaration.class).forEach(method -> {
            CodeSymbol symbol = new CodeSymbol();
            symbol.setName(method.getNameAsString());
            symbol.setKind(CodeSymbol.SymbolKind.METHOD);
            symbol.setFilePath(javaFile.toString());
            method.findAncestor(ClassOrInterfaceDeclaration.class)
                    .ifPresent(type -> symbol.setEnclosingType(type.getNameAsString()));
            method.getRange().ifPresent(range -> {
                symbol.setStartLine(range.begin.line);
                symbol.setEndLine(range.end.line);
            });
            symbols.add(symbol);
        });

        return symbols;
    }
}
