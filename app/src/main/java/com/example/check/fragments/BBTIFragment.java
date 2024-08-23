package com.example.check.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
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
    private ImageButton nextButton;
    private JSONObject questionsJson;
    private int currentQuestionIndex = 0;
    private Map<String, Integer> scores = new HashMap<>();
    private ApiService apiService;
    private JSONArray currentQuestionSet;
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bbti, container, false);

        startView = rootView.findViewById(R.id.startView);
        questionView = rootView.findViewById(R.id.questionView);
        resultView = rootView.findViewById(R.id.resultView);

        Button startButton = rootView.findViewById(R.id.startButton);
        if (startButton != null) {
            startButton.setOnClickListener(v -> startBBTITest());
        } else {
            Log.e(TAG, "Start button not found in layout");
        }

        questionTextView = rootView.findViewById(R.id.questionTextView);
        questionNumberTextView = rootView.findViewById(R.id.questionNumberTextView);
        option1Button = rootView.findViewById(R.id.option1Button);
        option2Button = rootView.findViewById(R.id.option2Button);
        nextButton = rootView.findViewById(R.id.nextButton);

        if (option1Button != null) option1Button.setOnClickListener(v -> selectOption(0));
        if (option2Button != null) option2Button.setOnClickListener(v -> selectOption(1));
        if (nextButton != null) nextButton.setOnClickListener(v -> onNextButtonClick());

        loadQuestions();
        apiService = ApiClient.getClient().create(ApiService.class);

        return rootView;
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
        try {
            InputStream is = getResources().openRawResource(R.raw.bbti_questions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            questionsJson = new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading questions: " + e.getMessage());
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
                if (questionTextView != null) questionTextView.setText(question.getString("text"));
                if (questionNumberTextView != null) questionNumberTextView.setText(String.format("%d", question.getInt("id")));
                JSONArray options = question.getJSONArray("options");
                if (option1Button != null) option1Button.setText(options.getJSONObject(0).getString("text"));
                if (option2Button != null) option2Button.setText(options.getJSONObject(1).getString("text"));
                if (nextButton != null) nextButton.setVisibility(View.INVISIBLE);
                currentCategory = question.getString("category");

                // Reset button states
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
        if (option1Button != null && option2Button != null) {
            option1Button.setSelected(optionIndex == 0);
            option2Button.setSelected(optionIndex == 1);
            if (nextButton != null) nextButton.setVisibility(View.VISIBLE);
        }
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
            String category = question.getString("category");
            String value = option1Button.isSelected() ?
                    question.getJSONArray("options").getJSONObject(0).getString("value") :
                    question.getJSONArray("options").getJSONObject(1).getString("value");
            scores.put(category, scores.getOrDefault(category, 0) + (value.equals(category.substring(0, 1)) ? 1 : 0));
        } catch (JSONException e) {
            Log.e(TAG, "Error updating score: " + e.getMessage());
        }
    }

    private void showResults() {
        if (questionView != null && resultView != null) {
            questionView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);
            String bbtiType = calculateBBTIType();
            sendResultsToServer(bbtiType);
            displayResult(bbtiType);
        } else {
            Log.e(TAG, "questionView or resultView is null");
        }
    }

    private String calculateBBTIType() {
        StringBuilder bbtiType = new StringBuilder();
        bbtiType.append(scores.getOrDefault("AB", 0) > 1 ? "A" : "B");
        bbtiType.append(scores.getOrDefault("CD", 0) > 1 ? "C" : "D");
        bbtiType.append(scores.getOrDefault("FN", 0) > 0 ? "F" : "N");
        bbtiType.append(scores.getOrDefault("RL", 0) > 1 ? "R" : "L");
        return bbtiType.toString();
    }

    private void displayResult(String bbtiType) {
        TextView resultTitleTextView = rootView.findViewById(R.id.resultTitleTextView);
        ImageView resultImageView = rootView.findViewById(R.id.resultImageView);
        TextView resultDescription1TextView = rootView.findViewById(R.id.resultDescription1TextView);
        TextView resultDescription2TextView = rootView.findViewById(R.id.resultDescription2TextView);

        if (resultTitleTextView != null && resultImageView != null &&
                resultDescription1TextView != null && resultDescription2TextView != null) {
            setResultDetails(bbtiType, resultTitleTextView, resultImageView, resultDescription1TextView, resultDescription2TextView);
        } else {
            Log.e(TAG, "One or more result views are null");
        }
    }

    private void setResultDetails(String bbtiType, TextView title, ImageView image, TextView desc1, TextView desc2) {
        switch (bbtiType) {
            case "ACFR":
                title.setText("귀찮아! 알아서 해줘! 패키지 여행객");
                image.setImageResource(R.drawable.img_bookbti_tmp);
                desc1.setText("이 유형은 어쩌구 저쩌구...");
                desc2.setText("이 유형은 어쩌구 저쩌구...");
                break;
            case "ACFL":
                title.setText("파도와 한몸! 파도 타는 서퍼");
                image.setImageResource(R.drawable.img_bookbti_tmp);
                desc1.setText("이 유형은 어쩌구 저쩌구...");
                desc2.setText("이 유형은 어쩌구 저쩌구...");
                break;
            // Add cases for other BBTI types
            default:
                title.setText("알 수 없는 유형");
                image.setImageResource(R.drawable.img_bookbti_tmp);
                desc1.setText("유형을 분석할 수 없습니다.");
                desc2.setText("다시 테스트를 진행해 주세요.");
        }
    }

    private void sendResultsToServer(String bbtiType) {
        // TODO: Implement the logic to send results to server
        // Example:
        // apiService.sendBBTIResult(MainActivity.userId, bbtiType).enqueue(new Callback<ResponseBody>() {
        //     @Override
        //     public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        //         if (response.isSuccessful()) {
        //             Log.d(TAG, "BBTI result sent successfully");
        //         } else {
        //             Log.e(TAG, "Failed to send BBTI result: " + response.code());
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(Call<ResponseBody> call, Throwable t) {
        //         Log.e(TAG, "Error sending BBTI result", t);
        //     }
        // });
    }
}