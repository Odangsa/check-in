package com.example.check.model.stampboard;

import java.util.List;
import java.util.ArrayList;

public class Transportation {
    private String type;
    private List<String> visited_libraries;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getVisited_libraries() {
        return visited_libraries;
    }

    public void setVisited_libraries(List<String> visited_libraries) {
        this.visited_libraries = visited_libraries;
    }

    // "None" 문자열을 처리하기 위한 새로운 메서드
    public void setVisited_libraries(String visited_libraries) {
        if ("None".equalsIgnoreCase(visited_libraries)) {
            this.visited_libraries = new ArrayList<>(); // 빈 리스트로 초기화
        } else {
            throw new IllegalArgumentException("Unexpected string value for visited_libraries: " + visited_libraries);
        }
    }

    @Override
    public String toString() {
        return "Transportation{" +
                "type='" + type + '\'' +
                ", visited_libraries=" + visited_libraries +
                '}';
    }
}