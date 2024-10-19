package com.example.check.Utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class BBTIUtil {
    private static final String TAG = "BBTIUtil";


    public static int convertBBTITypeToNumber(String bbtiType) {
        switch (bbtiType) {
            case "ACFR": return 137;
            case "ACNR": return 138;
            case "ACFL": return 147;
            case "ACNL": return 148;
            case "ADFR": return 157;
            case "ADNR": return 158;
            case "ADFL": return 167;
            case "ADNL": return 168;
            case "BCFR": return 237;
            case "BCNR": return 238;
            case "BCFL": return 247;
            case "BCNL": return 248;
            case "BDFR": return 257;
            case "BDNR": return 258;
            case "BDFL": return 267;
            case "BDNL": return 268;
            default:
                Log.e(TAG, "Invalid BBTI type: " + bbtiType);
                return -1; // 오류 상황을 나타내는 값
        }
    }


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
        public String number;

        public BBTIResult(String imageName, String title, String description, String number) {
            this.imageName = imageName;
            this.title = title;
            this.description = description;
            this.number = number;
        }
    }

    public static BBTIResult getBBTIResult(JSONArray bbtiResults, String identifier) {
        try {
            for (int i = 0; i < bbtiResults.length(); i++) {
                JSONObject result = bbtiResults.getJSONObject(i);
                if (result.getString("조합").equals(identifier) || result.getString("번호").equals(identifier)) {
                    String imageName = result.getString("이미지");
                    String title = result.getString("타이틀");
                    String description = result.getString("설명");
                    String number = result.getString("번호");
                    return new BBTIResult(imageName, title, description, number);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing BBTI result: " + e.getMessage());
        }
        return null; // 결과를 찾지 못한 경우
    }

    // MainActivity에서 사용할 메소드
    public static BBTIResult getBBTIResultByNumber(JSONObject bbtiData, String number) {
        try {
            JSONArray results = bbtiData.getJSONArray("results");
            return getBBTIResult(results, number);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing BBTI data: " + e.getMessage());
        }
        return null;
    }

    // BBTIFragment에서 사용할 메소드
    public static BBTIResult getBBTIResultByType(JSONArray bbtiResults, String bbtiType) {
        return getBBTIResult(bbtiResults, bbtiType);
    }
}