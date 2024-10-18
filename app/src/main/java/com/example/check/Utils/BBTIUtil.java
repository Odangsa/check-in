package com.example.check.Utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class BBTIUtil {
    private static final String TAG = "BBTIUtil";

    public static String calculateBBTIType(Map<String, Integer> scores) {
        StringBuilder bbtiType = new StringBuilder();
        bbtiType.append(scores.getOrDefault("AB", 0) > 1 ? "A" : "B");
        bbtiType.append(scores.getOrDefault("CD", 0) > 1 ? "C" : "D");
        bbtiType.append(scores.getOrDefault("FN", 0) > 0 ? "F" : "N");
        bbtiType.append(scores.getOrDefault("RL", 0) > 1 ? "R" : "L");
        return bbtiType.toString();
    }

    public static void updateScore(Map<String, Integer> scores, JSONObject question, boolean isFirstOptionSelected) {
        try {
            String category = question.getString("category");
            JSONArray options = question.getJSONArray("options");
            String value = isFirstOptionSelected ?
                    options.getJSONObject(0).getString("value") :
                    options.getJSONObject(1).getString("value");
            scores.put(category, scores.getOrDefault(category, 0) + (value.equals(category.substring(0, 1)) ? 1 : 0));
        } catch (JSONException e) {
            Log.e(TAG, "Error updating score: " + e.getMessage());
        }
    }

    public static class BBTIResult {
        public String imageName;
        public String title;
        public String description;

        public BBTIResult(String imageName, String title, String description) {
            this.imageName = imageName;
            this.title = title;
            this.description = description;
        }
    }

    public static BBTIResult getBBTIResult(JSONArray bbtiResults, String bbtiType) {
        try {
            for (int i = 0; i < bbtiResults.length(); i++) {
                JSONObject result = bbtiResults.getJSONObject(i);
                if (result.getString("조합").equals(bbtiType)) {
                    String imageName = result.getString("이미지");
                    String title = result.getString("타이틀");
                    String description = result.getString("설명");
                    return new BBTIResult(imageName, title, description);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing BBTI result: " + e.getMessage());
        }
        return null; // 결과를 찾지 못한 경우
    }
    public static class BBTINumberResult {
        public String imageName;
        public String title;

        public BBTINumberResult(String imageName, String title) {
            this.imageName = imageName;
            this.title = title;
        }
    }

    public static BBTINumberResult getBBTIResultByNumber(JSONObject bbtiData, String number) {
        try {
            JSONArray results = bbtiData.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                if (result.getString("번호").equals(number)) {
                    String imageName = result.getString("이미지");
                    String title = result.getString("타이틀");
                    return new BBTINumberResult(imageName, title);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing BBTI result by number: " + e.getMessage());
        }
        return null; // 결과를 찾지 못한 경우
    }
}