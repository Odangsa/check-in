package com.example.check.mock;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class MockResponseDispatcher extends Dispatcher {

    private static Context context;
    private static final Map<String, String> mockResponses = new HashMap<>();

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String path = request.getPath();
        if (path != null) {
            if (path.startsWith("/lib")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("lib.json"));
            } else if (path.startsWith("/recommendation_book")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("recommendation_book.json"));
            } else if (path.startsWith("/book_detail")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("book_detail.json"));
            } else if (path.startsWith("/recommendations")) {
                return new MockResponse().setResponseCode(200).setBody(getMockResponse("recommendations.json"));
            }
        }
        return new MockResponse().setResponseCode(404);
    }

    public static String getMockResponse(String fileName) {
        if (!mockResponses.containsKey(fileName)) {
            mockResponses.put(fileName, readMockJsonFromAssets(fileName));
        }
        return mockResponses.get(fileName);
    }

    private static String readMockJsonFromAssets(String fileName) {
        try {
            InputStream inputStream = context.getAssets().open("mock_responses/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}