package com.capstone.pick.domain.constant;

public enum OrderCriteria {
    LATEST("최신순"), POPULAR("인기순"), RELEVANCE("관련도순");

    private final String displayValue;

    OrderCriteria(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
