package com.codehistorian.model;

public class CodeSymbol {

    public enum SymbolKind { CLASS, INTERFACE, METHOD, FIELD, CONSTRUCTOR }

    private String name;
    private SymbolKind kind;
    private String filePath;
    private int startLine;
    private int endLine;
    private String enclosingType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolKind getKind() {
        return kind;
    }

    public void setKind(SymbolKind kind) {
        this.kind = kind;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getEnclosingType() {
        return enclosingType;
    }

    public void setEnclosingType(String enclosingType) {
        this.enclosingType = enclosingType;
    }
}
