package com.example.findbackapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchingManager {

    private final DatabaseHelper dbHelper;

    public MatchingManager(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Finds matches for a target item against all candidate items in database.
     * Lost items are matched with Found items, and vice versa.
     */
    public List<MatchingResult> findMatches(Item targetItem) {
        List<MatchingResult> results = new ArrayList<>();
        if (targetItem == null) return results;

        String candidateType = Item.TYPE_LOST.equalsIgnoreCase(targetItem.getItemType())
                ? Item.TYPE_FOUND : Item.TYPE_LOST;

        List<Item> candidates = dbHelper.getItemsByType(candidateType);

        for (Item candidate : candidates) {
            // Exclude items by same user if desired, or allow all. Also skip resolved items.
            if (Item.STATUS_RESOLVED.equalsIgnoreCase(candidate.getStatus())) {
                continue;
            }

            int score = calculateMatchScore(targetItem, candidate);
            if (score >= 40) {
                results.add(new MatchingResult(targetItem, candidate, score));
            }
        }

        // Sort descending by score
        Collections.sort(results, new Comparator<MatchingResult>() {
            @Override
            public int compare(MatchingResult o1, MatchingResult o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });

        return results;
    }

    /**
     * Finds all matching pairs in database.
     */
    public List<MatchingResult> findAllMatches() {
        List<MatchingResult> allResults = new ArrayList<>();
        List<Item> lostItems = dbHelper.getItemsByType(Item.TYPE_LOST);

        for (Item lost : lostItems) {
            if (Item.STATUS_RESOLVED.equalsIgnoreCase(lost.getStatus())) continue;
            List<MatchingResult> matches = findMatches(lost);
            allResults.addAll(matches);
        }

        Collections.sort(allResults, new Comparator<MatchingResult>() {
            @Override
            public int compare(MatchingResult o1, MatchingResult o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });

        return allResults;
    }

    /**
     * Calculate score between two items based on prompt rules:
     * 1. Category match: 40 points
     * 2. Item name similarity: 25 points
     * 3. Color match: 15 points
     * 4. Location match: 10 points
     * 5. Date match/closeness: 10 points
     */
    public int calculateMatchScore(Item item1, Item item2) {
        int totalScore = 0;

        // 1. Category (40 pts)
        if (item1.getCategory() != null && item1.getCategory().equalsIgnoreCase(item2.getCategory())) {
            totalScore += 40;
        }

        // 2. Name similarity (25 pts)
        double nameSim = calculateSimilarity(item1.getItemName(), item2.getItemName());
        totalScore += (int) (nameSim * 25);

        // 3. Color (15 pts)
        if (item1.getColor() != null && item2.getColor() != null) {
            if (item1.getColor().equalsIgnoreCase(item2.getColor())) {
                totalScore += 15;
            } else if (item1.getColor().contains(item2.getColor()) || item2.getColor().contains(item1.getColor())) {
                totalScore += 10;
            }
        }

        // 4. Location (10 pts)
        if (item1.getLocation() != null && item2.getLocation() != null) {
            String loc1 = item1.getLocation().trim();
            String loc2 = item2.getLocation().trim();
            if (loc1.equalsIgnoreCase(loc2)) {
                totalScore += 10;
            } else if (loc1.contains(loc2) || loc2.contains(loc1)) {
                totalScore += 7;
            }
        }

        // 5. Date closeness (10 pts)
        int dateScore = calculateDateClosenessScore(item1.getItemDate(), item2.getItemDate());
        totalScore += dateScore;

        return Math.min(100, totalScore);
    }

    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        s1 = s1.trim().toLowerCase();
        s2 = s2.trim().toLowerCase();

        if (s1.equals(s2)) return 1.0;
        if (s1.contains(s2) || s2.contains(s1)) return 0.8;

        int distance = computeLevenshteinDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;

        return 1.0 - ((double) distance / maxLength);
    }

    private int computeLevenshteinDistance(String lhs, String rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) cost[i] = i;

        for (int j = 1; j < len1; j++) {
            newcost[0] = j;
            for (int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        return cost[len0 - 1];
    }

    private int calculateDateClosenessScore(String dateStr1, String dateStr2) {
        if (dateStr1 == null || dateStr2 == null) return 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date d1 = sdf.parse(dateStr1);
            Date d2 = sdf.parse(dateStr2);
            if (d1 != null && d2 != null) {
                long diffMillis = Math.abs(d1.getTime() - d2.getTime());
                long diffDays = diffMillis / (1000 * 60 * 60 * 24);

                if (diffDays <= 1) return 10;
                if (diffDays <= 3) return 7;
                if (diffDays <= 7) return 4;
                if (diffDays <= 14) return 2;
            }
        } catch (ParseException ignored) {}
        return 0;
    }
}
