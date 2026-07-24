package com.intellilearn.util;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateQuiz(String pdfText) {

        String prompt = """
                Generate exactly 10 multiple-choice questions from the following text.

                Return ONLY valid JSON.

                Format:

                [
                  {
                    "question":"",
                    "optionA":"",
                    "optionB":"",
                    "optionC":"",
                    "optionD":"",
                    "correctAnswer":""
                  }
                ]

                Text:
                """ + pdfText;

        Map<String, Object> body = Map.of(
                "contents",
                new Object[]{
                        Map.of(
                                "parts",
                                new Object[]{
                                        Map.of("text", prompt)
                                })
                });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);
        
        System.out.println("API URL: " + apiUrl + "?key=" + apiKey);
        String response = restTemplate.postForObject(
                apiUrl + "?key=" + apiKey,
                request,
                String.class);

        return response;
    }

}
