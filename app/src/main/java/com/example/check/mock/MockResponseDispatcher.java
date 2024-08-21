package com.example.check.mock;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class MockResponseDispatcher extends Dispatcher {
    private static final String TAG = "MockResponseDispatcher";
    private static Context context;
    private static final Map<String, String> mockResponses = new HashMap<>();

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }

    @NonNull
    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String path = request.getPath();
        Log.d(TAG, "Received request for path: " + path);

        if (path != null) {
            if (path.startsWith("/lib")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("lib"));
            } else if (path.startsWith("/recommendation_book_two")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("recommendation_book_two"));
            } else if (path.startsWith("/book_detail")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("book_detail"));
            } else if (path.startsWith("/recommendations_today_book")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("recommendations_today_book"));
            }
        }
        Log.w(TAG, "No matching path found for: " + path);
        return new MockResponse().setResponseCode(404);
    }

    public static String getMockResponse(String fileName) {
        if (!mockResponses.containsKey(fileName)) {
            mockResponses.put(fileName, readMockJsonFromRaw(fileName));
        }
        return mockResponses.get(fileName);
    }

    private static String readMockJsonFromRaw(String fileName) {
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier(fileName, "raw", context.getPackageName());
            InputStream inputStream = resources.openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            Log.d(TAG, "Successfully read mock JSON for: " + fileName);
            return content;
        } catch (Exception e) {
            Log.e(TAG, "Error reading mock JSON for: " + fileName, e);
            return "";
        }
    }
}