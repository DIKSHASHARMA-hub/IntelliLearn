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
    			Generate exactly 10 multiple-choice questions from the following study material.

    			IMPORTANT RULES:

    			1. Each question must have exactly four options:
    			   - optionA
    			   - optionB
    			   - optionC
    			   - optionD

    			2. The "correctAnswer" field MUST contain ONLY the option LETTER:
    			   "A", "B", "C", or "D".

    			3. NEVER return the answer text.

    			Correct example:

    			{
    			  "question": "Which servlet is the heart of Spring MVC?",
    			  "optionA": "LoginServlet",
    			  "optionB": "DispatcherServlet",
    			  "optionC": "ContextServlet",
    			  "optionD": "HttpServlet",
    			  "correctAnswer": "B"
    			}

    			Wrong example:

    			{
    			  "question": "Which servlet is the heart of Spring MVC?",
    			  "optionA": "LoginServlet",
    			  "optionB": "DispatcherServlet",
    			  "optionC": "ContextServlet",
    			  "optionD": "HttpServlet",
    			  "correctAnswer": "DispatcherServlet"
    			}

    			4. Return ONLY a valid JSON array.
    			5. Do NOT use markdown.
    			6. Do NOT add explanations.
    			7. Do NOT wrap the JSON in ```json blocks.

    			Study Material:

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
        
        String response = restTemplate.postForObject(
                apiUrl + "?key=" + apiKey,
                request,
                String.class);

        return response;
    }

}