package com.codehistorian.model;

public class Evidence {

    public enum EvidenceType { COMMIT, PULL_REQUEST, ISSUE, FILE }

    private EvidenceType type;
    private String referenceId;
    private String label;
    private String detail;

    public EvidenceType getType() {
        return type;
    }

    public void setType(EvidenceType type) {
        this.type = type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
