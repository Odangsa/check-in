package com.example.check.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.check.R;
import com.example.check.Utils.BBTIUtil;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BBTIFragment extends Fragment {

    private static final String TAG = "BBTIFragment";
    private View rootView;
    private View startView;
    private View questionView;
    private View resultView;
    private TextView questionTextView;
    private TextView questionNumberTextView;
    private Button option1Button, option2Button;
    private JSONObject questionsJson;
    private int currentQuestionIndex = 0;
    private Map<String, Integer> scores = new HashMap<>();
    private ApiService apiService;
    private JSONArray currentQuestionSet;
    private JSONArray bbtiResults;
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bbti, container, false);
        if (rootView == null) {
            Log.e(TAG, "Failed to inflate layout");
            return null;
        }

        initializeViews();
        setListeners();
        loadQuestions();
        loadBBTIResults();
        apiService = ApiClient.getClient().create(ApiService.class);

        return rootView;
    }

    private void initializeViews() {
        startView = rootView.findViewById(R.id.startView);
        questionView = rootView.findViewById(R.id.questionView);
        resultView = rootView.findViewById(R.id.resultView);
        questionTextView = rootView.findViewById(R.id.questionTextView);
        questionNumberTextView = rootView.findViewById(R.id.questionNumberTextView);
        option1Button = rootView.findViewById(R.id.option1Button);
        option2Button = rootView.findViewById(R.id.option2Button);

    }

    private void setListeners() {
        Button startButton = rootView.findViewById(R.id.startButton);
        if (startButton != null) {
            startButton.setOnClickListener(v -> startBBTITest());
        } else {
            Log.e(TAG, "Start button not found in layout");
        }

        if (option1Button != null) option1Button.setOnClickListener(v -> {
            selectOption(0);
            onNextButtonClick();
        });
        if (option2Button != null) option2Button.setOnClickListener(v -> {
            selectOption(1);
            onNextButtonClick();
        });
    }

    private void startBBTITest() {
        if (startView != null && questionView != null) {
            startView.setVisibility(View.GONE);
            questionView.setVisibility(View.VISIBLE);
            currentQuestionSet = getMainQuestions();
            currentQuestionIndex = 0;
            displayQuestion();
        } else {
            Log.e(TAG, "startView or questionView is null");
        }
    }

    private void loadQuestions() {
        questionsJson = loadJSONFromResource(R.raw.bbti_questions);
    }

    private void loadBBTIResults() {
        JSONObject bbtiResultsObj = loadJSONFromResource(R.raw.bbti);
        try {
            bbtiResults = bbtiResultsObj.getJSONArray("results");
            Log.d(TAG, "BBTI Results loaded: " + bbtiResults.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error loading BBTI results: " + e.getMessage());
            bbtiResults = new JSONArray();
        }
    }

    private JSONObject loadJSONFromResource(int resourceId) {
        try {
            InputStream is = getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading JSON from resource: " + e.getMessage());
            return new JSONObject();
        }
    }

    private JSONArray getMainQuestions() {
        try {
            return questionsJson.getJSONArray("questions");
        } catch (JSONException e) {
            Log.e(TAG, "Error getting main questions: " + e.getMessage());
            return new JSONArray();
        }
    }

    private void displayQuestion() {
        try {
            if (currentQuestionIndex < currentQuestionSet.length()) {
                JSONObject question = currentQuestionSet.getJSONObject(currentQuestionIndex);
                questionTextView.setText(question.getString("text"));
                questionNumberTextView.setText(String.format("%d", question.getInt("id")));
                JSONArray options = question.getJSONArray("options");
                option1Button.setText(options.getJSONObject(0).getString("text"));
                option2Button.setText(options.getJSONObject(1).getString("text"));

                currentCategory = question.getString("category");

                option1Button.setSelected(false);
                option2Button.setSelected(false);
            } else {
                showResults();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error displaying question: " + e.getMessage());
        }
    }

    private void selectOption(int optionIndex) {
        option1Button.setSelected(optionIndex == 0);
        option2Button.setSelected(optionIndex == 1);

    }

    private void onNextButtonClick() {
        try {
            updateScore();
            currentQuestionIndex++;
            if (currentQuestionIndex < currentQuestionSet.length()) {
                displayQuestion();
            } else if (currentCategory.equals("FN")) {
                String fnResult = scores.getOrDefault("FN", 0) > 0 ? "F" : "N";
                currentQuestionSet = getBranchedQuestions(fnResult);
                currentQuestionIndex = 0;
                displayQuestion();
            } else {
                showResults();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error in onNextButtonClick: " + e.getMessage());
        }
    }

    private JSONArray getBranchedQuestions(String branch) throws JSONException {
        return questionsJson.getJSONObject("branchedQuestions").getJSONArray(branch);
    }

    private void updateScore() {
        try {
            JSONObject question = currentQuestionSet.getJSONObject(currentQuestionIndex);
            BBTIUtil.updateScore(scores, question, option1Button.isSelected());
        } catch (JSONException e) {
            Log.e(TAG, "Error updating score: " + e.getMessage());
        }
    }

    private void showResults() {
        if (questionView != null && resultView != null) {
            questionView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);
            String bbtiType = BBTIUtil.calculateBBTIType(scores);
            Log.d(TAG, "BBTI Type: " + bbtiType);
            sendResultsToServer(bbtiType);
            displayResult(bbtiType);
        } else {
            Log.e(TAG, "questionView or resultView is null");
        }
    }

//    private void displayResult(String bbtiType) {
//        View resultView = rootView.findViewById(R.id.resultView);
//        if (resultView == null) {
//            Log.e(TAG, "resultView is null");
//            return;
//        }
//
//        ImageView resultImageView = resultView.findViewById(R.id.resultImageView);
//        TextView resultTitleTextView = resultView.findViewById(R.id.resultTitleTextView);
//        TextView resultDescription1TextView = resultView.findViewById(R.id.resultDescription1TextView);
//        TextView resultDescription2TextView = resultView.findViewById(R.id.resultDescription2TextView);
//
//        // Check each view individually
//        if (resultImageView == null) Log.e(TAG, "resultImageView is null");
//        if (resultTitleTextView == null) Log.e(TAG, "resultTitleTextView is null");
//        if (resultDescription1TextView == null) Log.e(TAG, "resultDescription1TextView is null");
//        if (resultDescription2TextView == null) Log.e(TAG, "resultDescription2TextView is null");
//
//        // Proceed only if all views are available
//        if (resultImageView != null && resultTitleTextView != null &&
//                resultDescription1TextView != null && resultDescription2TextView != null) {
//
//            // Make the result view visible
//            resultView.setVisibility(View.VISIBLE);
//
//            JSONObject result = BBTIUtil.findBBTIResult(bbtiResults, bbtiType);
//            if (result != null) {
//                try {
//                    // Set the image
//                    resultImageView.setImageResource(R.drawable.img_bookbti_tmp);
//
//                    // Set the title
//                    String title = "■ " + result.getString("타이틀");
//                    resultTitleTextView.setText(title);
//                    resultTitleTextView.setVisibility(View.VISIBLE);
//
//                    // Set the descriptions
//                    String description = result.getString("설명");
//                    String[] splitDescription = splitDescription(description);
//                    resultDescription1TextView.setText(splitDescription[0]);
//                    resultDescription2TextView.setText(splitDescription[1]);
//
//                    resultDescription1TextView.setVisibility(View.VISIBLE);
//                    resultDescription2TextView.setVisibility(View.VISIBLE);
//
//                    // Log the results for debugging
//                    Log.d(TAG, "BBTI Type: " + bbtiType);
//                    Log.d(TAG, "Title: " + title);
//                    Log.d(TAG, "Description 1: " + splitDescription[0]);
//                    Log.d(TAG, "Description 2: " + splitDescription[1]);
//
//                } catch (JSONException e) {
//                    Log.e(TAG, "Error setting BBTI result: " + e.getMessage());
//                    setErrorResult(resultTitleTextView, resultDescription1TextView, resultDescription2TextView);
//                }
//            } else {
//                Log.e(TAG, "BBTI result not found for type: " + bbtiType);
//                setErrorResult(resultTitleTextView, resultDescription1TextView, resultDescription2TextView);
//            }
//        } else {
//            Log.e(TAG, "One or more result views are null");
//        }
//    }
    private void displayResult(String bbtiType) {
        View resultView = rootView.findViewById(R.id.resultView);
        if (resultView == null) {
            Log.e(TAG, "resultView is null");
            return;
        }

        ImageView resultImageView = resultView.findViewById(R.id.resultImageView);
        TextView resultTitleTextView = resultView.findViewById(R.id.resultTitleTextView);
        TextView resultDescription1TextView = resultView.findViewById(R.id.resultDescription1TextView);
        TextView resultDescription2TextView = resultView.findViewById(R.id.resultDescription2TextView);

        // Proceed only if all views are available
        if (resultImageView != null && resultTitleTextView != null &&
                resultDescription1TextView != null && resultDescription2TextView != null) {

            // Make the result view visible
            resultView.setVisibility(View.VISIBLE);

            BBTIUtil.BBTIResult result = BBTIUtil.getBBTIResult(bbtiResults, bbtiType);
            if (result != null) {
                // Set the image
                int imageResId = getResources().getIdentifier(result.imageName.replace(".png", ""), "drawable", getContext().getPackageName());
                if (imageResId != 0) {
                    resultImageView.setImageResource(imageResId);
                } else {
                    Log.e(TAG, "Image resource not found: " + result.imageName);
                    resultImageView.setImageResource(R.drawable.img_bookbti_tmp);
                }

                // Set the title
                String title = "■ " + result.title;
                resultTitleTextView.setText(title);
                resultTitleTextView.setVisibility(View.VISIBLE);

                // Set the descriptions
                String[] splitDescription = splitDescription(result.description);
                resultDescription1TextView.setText(splitDescription[0]);
                resultDescription2TextView.setText(splitDescription[1]);

                resultDescription1TextView.setVisibility(View.VISIBLE);
                resultDescription2TextView.setVisibility(View.VISIBLE);

                // Log the results for debugging
                Log.d(TAG, "BBTI Type: " + bbtiType);
                Log.d(TAG, "Title: " + title);
                Log.d(TAG, "Description 1: " + splitDescription[0]);
                Log.d(TAG, "Description 2: " + splitDescription[1]);
            } else {
                Log.e(TAG, "BBTI result not found for type: " + bbtiType);
            }
        } else {
            Log.e(TAG, "One or more result views are null");
        }
    }

    private String[] splitDescription(String description) {
        String[] result = new String[2];
        if (description.length() > 100) {
            int splitIndex = description.lastIndexOf(" ", 100);
            result[0] = description.substring(0, splitIndex);
            result[1] = description.substring(splitIndex + 1);
        } else {
            result[0] = description;
            result[1] = "";
        }
        return result;
    }

    // If you have specific images for each BBTI type, you can use this method
//    private int getImageResourceForType(String bbtiType) {
//        switch (bbtiType) {
//            case "ACFR":
//                return R.drawable.img_acfr;
//            case "ACFL":
//                return R.drawable.img_acfl;
//            // Add cases for other BBTI types
//            default:
//                return R.drawable.img_bookbti_tmp;
//        }
//    }

    private void sendResultsToServer(String bbtiType) {
        // TODO: Implement the logic to send results to server
    }
}