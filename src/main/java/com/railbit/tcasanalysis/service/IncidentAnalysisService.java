//package com.railbit.tcasanalysis.service;
//
//
//import com.railbit.tcasanalysis.DTO.RequestDto;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class IncidentAnalysisService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String callTeammateApi(RequestDto requestDto) {
////        String url = "http://213.199.57.172:9089/analysis/query";
//        String url = "http://192.168.1.221:8081/analysis/query";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<RequestDto> entity = new HttpEntity<>(requestDto, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        return response.getBody();
//    }
//}

//package com.railbit.tcasanalysis.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.railbit.tcasanalysis.DTO.RequestDto;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class IncidentAnalysisService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Value("${auth.username}")
//    private String username;
//
//    @Value("${auth.password}")
//    private String password;
//
////    @Value("${auth.splcode}")
////    private String splCode;
//
//    public String callTeammateApi(RequestDto requestDto) {
//        // ✅ Your teammate API endpoint
//        String url = "http://192.168.1.221:8081/analysis/query";
//        // String url = "http://213.199.57.172:9089/analysis/query";
//
//        try {
//
//            String token = fetchAuthToken();
//            System.out.println("✅ Token fetched successfully: " + token);
//
//
//            requestDto.setToken(token);
//
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            String finalRequestBody = objectMapper.writeValueAsString(requestDto);
//            System.out.println(" Final request body for teammate API: " + finalRequestBody);
//
//            HttpEntity<String> entity = new HttpEntity<>(finalRequestBody, headers);
//
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            System.out.println("✅ Response Status: " + response.getStatusCode());
//            System.out.println("✅ Response Body: " + response.getBody());
//
//            return response.getBody();
//
//        } catch (HttpClientErrorException | HttpServerErrorException ex) {
//            System.err.println(" Error Response: " + ex.getResponseBodyAsString());
//            return "Error calling teammate API: " + ex.getStatusCode() + " : " + ex.getResponseBodyAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error calling teammate API: " + e.getMessage();
//        }
//    }
//
//    private String fetchAuthToken() throws Exception {
//        String loginUrl = "http://localhost:8081/tcasapi/login/";
//
//        ObjectNode loginPayload = objectMapper.createObjectNode();
//        loginPayload.put("username", username);
//        loginPayload.put("password", password);
////        loginPayload.put("splCode", splCode);
//
//
//        System.out.println(" Login request payload: " + objectMapper.writeValueAsString(loginPayload));
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> loginEntity = new HttpEntity<>(objectMapper.writeValueAsString(loginPayload), headers);
//
//
//        ResponseEntity<String> loginResponse = restTemplate.exchange(
//                loginUrl,
//                HttpMethod.POST,
//                loginEntity,
//                String.class
//        );
//
//        // ✅ Step 5: Log response
//        System.out.println(" Login API Response: " + loginResponse.getBody());
//
//
//        if (loginResponse.getStatusCode() == HttpStatus.OK) {
//            JsonNode jsonNode = objectMapper.readTree(loginResponse.getBody());
//            if (jsonNode.has("token")) {
//                return jsonNode.get("token").asText();
//            } else {
//                throw new RuntimeException("Token field not found in login response");
//            }
//        } else {
//            throw new RuntimeException("Failed to fetch token: " + loginResponse.getStatusCode());
//        }
//    }
//}




package com.railbit.tcasanalysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.railbit.tcasanalysis.DTO.RequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class IncidentAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Value("${api.login-url}")
    private String loginUrl;

    @Value("${api.analysis-url}")
    private String analysisUrl;



    public String callTeammateApi(RequestDto requestDto) {
        try {
            String token = fetchAuthToken();
            System.out.println(" Token fetched successfully: " + token);

            requestDto.setToken(token);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String finalRequestBody = objectMapper.writeValueAsString(requestDto);
            System.out.println("Final request body for teammate API: " + finalRequestBody);

            HttpEntity<String> entity = new HttpEntity<>(finalRequestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    analysisUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println(" Response Status: " + response.getStatusCode());
            System.out.println(" Response Body: " + response.getBody());

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.err.println(" Error Response: " + ex.getResponseBodyAsString());
            return "Error calling teammate API: " + ex.getStatusCode() + " : " + ex.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling teammate API: " + e.getMessage();
        }
    }

    private String fetchAuthToken() throws Exception {
        ObjectNode loginPayload = objectMapper.createObjectNode();
        loginPayload.put("username", username);
        loginPayload.put("password", password);


        System.out.println("Login request payload: " + objectMapper.writeValueAsString(loginPayload));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> loginEntity = new HttpEntity<>(objectMapper.writeValueAsString(loginPayload), headers);

        ResponseEntity<String> loginResponse = restTemplate.exchange(
                loginUrl,
                HttpMethod.POST,
                loginEntity,
                String.class
        );

        System.out.println("Login API Response: " + loginResponse.getBody());

        if (loginResponse.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonNode = objectMapper.readTree(loginResponse.getBody());
            if (jsonNode.has("token")) {
                return jsonNode.get("token").asText();
            } else {
                throw new RuntimeException("Token field not found in login response");
            }
        } else {
            throw new RuntimeException("Failed to fetch token: " + loginResponse.getStatusCode());
        }
    }
}

