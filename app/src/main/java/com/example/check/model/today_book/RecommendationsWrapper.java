package com.example.check.model.today_book;

import java.util.List;

public class RecommendationsWrapper {
    private List<RecommendationCategory> recommendations;

    public List<RecommendationCategory> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationCategory> recommendations) {
        this.recommendations = recommendations;
    }
}