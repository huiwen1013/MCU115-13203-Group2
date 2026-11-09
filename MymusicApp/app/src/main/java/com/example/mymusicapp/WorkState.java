package com.example.mymusicapp;

public enum WorkState {
    IDLE("Idle"),
    PREPARING("Preparing..."),
    WORKING("Working..."),
    COMPLETED("背景工作結束！"),
    CANCELED("Canceled");

    private final String displayText;

    WorkState(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
