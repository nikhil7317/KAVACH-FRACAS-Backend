package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.config.ReCaptchaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ReCaptchaService {

    private static final String VERIFY_URL = "https://recaptcha.net/recaptcha/api/siteverify";

    @Autowired
    private ReCaptchaConfig config;

    public boolean verify(String token) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", config.getSecret());
        params.add("response", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map body = response.getBody();
            return (Boolean) body.get("success");
        }
        return false;
    }

}
