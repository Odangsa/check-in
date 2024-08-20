package com.example.check.mock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class MockResponseDispatcher extends Dispatcher {

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String path = request.getPath();
        if (path != null) {
            if (path.startsWith("/lib")) {
                return new MockResponse().setResponseCode(200).setBody(readMockJson("lib.json"));
            } else if (path.startsWith("/recommendation_book")) {
                return new MockResponse().setResponseCode(200).setBody(readMockJson("recommendation_book.json"));
            } else if (path.startsWith("/book_detail")) {
                return new MockResponse().setResponseCode(200).setBody(readMockJson("book_detail.json"));
            } else if (path.startsWith("/recommendations")) {
                return new MockResponse().setResponseCode(200).setBody(readMockJson("recommendations.json"));
            }
        }
        return new MockResponse().setResponseCode(404);
    }

    private String readMockJson(String fileName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mock_responses/" + fileName);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}