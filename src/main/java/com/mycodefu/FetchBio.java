package com.mycodefu;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FetchBio {

    public static final String PROMPT_TEMPLATE = """
            {
                "model": "phi-4",
                "messages": [
                    { "role": "system", "content": "You are a bio writer. When given a name, age and profession write a bio in two short sentences for that person. Include a comment about their family. Avoid the words meticulous, dedicated, experienced and accomplished." },
                    { "role": "user", "content": "Name: %s, Age: %d, Profession: %s" }
                ],
                "temperature": 0.7,
                "max_tokens": 100,
                "stream": false
            }""";
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    record AIMessage(String role, String content) {
    }

    record AIChoice(AIMessage message) {
    }

    record AIResponse(List<AIChoice> choices) {
    }

    public static void main(String[] args) {
        String name = "Bob";
        int age = 44;
        String profession = "Programmer";
        System.out.println("Requesting bio for: " + name + ", " + age + ", " + profession);
        String bio = bioFor(name, age, profession);
        System.out.println("Bio result: " + bio);
    }

    public static String bioFor(String name, int age, String profession) {
        String endpoint = "http://localhost:1234/v1/chat/completions";

        try {
            URL url = URI.create(endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Setup connection properties
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Create JSON payload
            String jsonInputString = PROMPT_TEMPLATE.formatted(name, age, profession);
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);

            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

            // Read response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                String rawModelResponse = response.toString();
                AIResponse aiResponse = mapper.readValue(rawModelResponse, AIResponse.class);
                return aiResponse.choices.getFirst().message.content;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
