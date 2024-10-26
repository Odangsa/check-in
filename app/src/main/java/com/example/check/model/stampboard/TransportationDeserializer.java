package com.example.check.model.stampboard;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TransportationDeserializer implements JsonDeserializer<Transportation> {
    @Override
    public Transportation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Transportation transportation = new Transportation();

        JsonObject jsonObject = json.getAsJsonObject();
        transportation.setType(jsonObject.get("type").getAsString());

        JsonElement visitedLibrariesElement = jsonObject.get("visited_libraries");
        if (visitedLibrariesElement.isJsonArray()) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> visitedLibraries = context.deserialize(visitedLibrariesElement, listType);
            transportation.setVisited_libraries(visitedLibraries);
        } else if (visitedLibrariesElement.isJsonPrimitive()) {
            String value = visitedLibrariesElement.getAsString();
            if ("None".equalsIgnoreCase(value)) {
                transportation.setVisited_libraries((List<String>) null); // 또는 new ArrayList<>()
            } else {
                throw new JsonParseException("Unexpected value for visited_libraries: " + value);
            }
        } else {
            throw new JsonParseException("Unexpected JSON element type for visited_libraries");
        }

        return transportation;
    }
}