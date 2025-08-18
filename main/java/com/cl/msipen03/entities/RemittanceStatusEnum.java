package com.cl.msipen03.entities;

public enum RemittanceStatusEnum {

    ACTC("ACCEPTÉE"),
    ACCP("ACCEPTÉE"),
    RJCT("REJETÉE"),
    PART("PARTIELLEMENT ACCEPTÉE");

    private final String label;

    RemittanceStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static String getStatusLabel(String code) {
        try {
            return valueOf(code).getLabel();
        } catch (IllegalArgumentException | NullPointerException e) {
            return code;
        }
    }
}