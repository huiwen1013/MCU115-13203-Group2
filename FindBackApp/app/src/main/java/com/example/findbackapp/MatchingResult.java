package com.example.findbackapp;

public class MatchingResult {
    private Item sourceItem; // The item belonging to user or selected for matching
    private Item matchedItem; // The item found that matches
    private int score; // Total score 0 - 100

    public MatchingResult(Item sourceItem, Item matchedItem, int score) {
        this.sourceItem = sourceItem;
        this.matchedItem = matchedItem;
        this.score = score;
    }

    public Item getSourceItem() {
        return sourceItem;
    }

    public Item getMatchedItem() {
        return matchedItem;
    }

    public int getScore() {
        return score;
    }

    public String getMatchLevel() {
        if (score >= 80) {
            return "高度符合";
        } else if (score >= 60) {
            return "中度符合";
        } else if (score >= 40) {
            return "低度符合";
        } else {
            return "不符";
        }
    }
}
