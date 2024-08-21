package com.example.check.model.stampboard;

import java.util.List;

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
}
