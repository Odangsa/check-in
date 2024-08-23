package com.example.check.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.check.R;
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

    private View startView;
    private View questionView;
    private View resultView;
    private TextView questionTextView;
    private Button option1Button, option2Button;
    private ImageButton nextButton;
    private JSONObject questionsJson;
    private int currentQuestionIndex = 0;
    private Map<String, Integer> scores = new HashMap<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        startView = inflater.inflate(R.layout.fragment_bbti, container, false);
        questionView = inflater.inflate(R.layout.fragment_bookbti_question, container, false);
        resultView = inflater.inflate(R.layout.fragment_bookbti_result, container, false);

        Button startButton = startView.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startBBTITest());

        questionTextView = questionView.findViewById(R.id.questionTextView);
        option1Button = questionView.findViewById(R.id.option1Button);
        option2Button = questionView.findViewById(R.id.option2Button);
        nextButton = questionView.findViewById(R.id.nextButton);

        option1Button.setOnClickListener(v -> selectOption(0));
        option2Button.setOnClickListener(v -> selectOption(1));
        nextButton.setOnClickListener(v -> onNextButtonClick());

        loadQuestions();
        apiService = ApiClient.getClient().create(ApiService.class);

        return startView;
    }

    private void startBBTITest() {
        ViewGroup parent = (ViewGroup) startView.getParent();
        parent.removeView(startView);
        parent.addView(questionView);
        displayQuestion();
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

    private void onNextButtonClick() {
        try {
            updateScore();
            currentQuestionIndex++;
            JSONArray questions = questionsJson.getJSONArray("questions");
            if (currentQuestionIndex < questions.length()) {
                displayQuestion();
            } else {
                showResults();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error in onNextButtonClick: " + e.getMessage());
        }
    }

    private void displayQuestion() {
        try {
            JSONArray questions = questionsJson.getJSONArray("questions");
            if (currentQuestionIndex < questions.length()) {
                JSONObject question = questions.getJSONObject(currentQuestionIndex);
                questionTextView.setText(question.getString("text"));
                JSONArray options = question.getJSONArray("options");
                option1Button.setText(options.getJSONObject(0).getString("text"));
                option2Button.setText(options.getJSONObject(1).getString("text"));
                nextButton.setVisibility(View.INVISIBLE);
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
        nextButton.setVisibility(View.VISIBLE);
    }

    private void updateScore() {
        try {
            JSONArray questions = questionsJson.getJSONArray("questions");
            JSONObject question = questions.getJSONObject(currentQuestionIndex);
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
        ViewGroup parent = (ViewGroup) questionView.getParent();
        parent.removeView(questionView);
        parent.addView(resultView);

        String bbtiType = calculateBBTIType();
        sendResultsToServer(bbtiType);

        TextView resultTitleTextView = resultView.findViewById(R.id.resultTitleTextView);
        ImageView resultImageView = resultView.findViewById(R.id.resultImageView);
        TextView resultDescription1TextView = resultView.findViewById(R.id.resultDescription1TextView);
        TextView resultDescription2TextView = resultView.findViewById(R.id.resultDescription2TextView);

        setResultDetails(bbtiType, resultTitleTextView, resultImageView, resultDescription1TextView, resultDescription2TextView);
    }

    private String calculateBBTIType() {
        StringBuilder bbtiType = new StringBuilder();
        bbtiType.append(scores.getOrDefault("AB", 0) > 1 ? "A" : "B");
        bbtiType.append(scores.getOrDefault("CD", 0) > 1 ? "C" : "D");
        bbtiType.append(scores.getOrDefault("FN", 0) > 0 ? "F" : "N");
        bbtiType.append(scores.getOrDefault("RL", 0) > 1 ? "R" : "L");
        return bbtiType.toString();
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
                // Set default values
        }
    }

    private void sendResultsToServer(String bbtiType) {
        // Implement the logic to send results to server
        // Example:
        // apiService.sendBBTIResult(MainActivity.userId, bbtiType).enqueue(new Callback<ResponseBody>() {
        //     @Override
        //     public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        //         if (response.isSuccessful()) {
        //             // Handle successful response
        //         } else {
        //             // Handle error
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(Call<ResponseBody> call, Throwable t) {
        //         // Handle failure
        //     }
        // });
    }
}