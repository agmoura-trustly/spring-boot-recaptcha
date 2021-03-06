package com.test.recaptcha.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RecaptchaController {

    private static final String GOOGLE_RECAPTCHA_API = "https://www.google.com/recaptcha/api/";
    private static final String GOOGLE_RECAPTCHA_API2 = "https://www.google.com/recaptcha/api2/";
    private static final String GOOGLE_RECAPTCHA_JS = "https://www.gstatic.com/recaptcha/releases/OOKISvkNnVD_m_9dreR_1S0n/recaptcha__en.js";
    private static final String INTERNAL_RECAPTCHA_JS = "http://localhost:8080/scripts/recaptcha/api2/recaptcha__en.js";

    @Value("${google.recaptcha.client}")
    private String recaptchaClient;

    @GetMapping("recaptcha/api2/anchor")
    public String anchor(HttpServletRequest request) {
        // TODO: Tratar permissão de dominios

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForEntity(GOOGLE_RECAPTCHA_API2 + "anchor?" + request.getQueryString(), String.class).getBody();
        response = response.replace(GOOGLE_RECAPTCHA_JS, INTERNAL_RECAPTCHA_JS);
        return response;
    }

    @GetMapping(value = "recaptcha/api2/webworker.js", produces = "text/javascript")
    public String webworker(HttpServletRequest request) {
        return "importScripts('" + INTERNAL_RECAPTCHA_JS + "')";
    }

    @GetMapping("recaptcha/api2/bframe")
    public String bframe(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForEntity(GOOGLE_RECAPTCHA_API2 + "bframe?" + request.getQueryString(), String.class).getBody();
        response = response.replace(GOOGLE_RECAPTCHA_JS, INTERNAL_RECAPTCHA_JS);
        return response;
    }

    @GetMapping("recaptcha/api/fallback")
    public ResponseEntity<String> fallback(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(GOOGLE_RECAPTCHA_API + "fallback?" + request.getQueryString(), String.class);
        return response;
    }

    @PostMapping("recaptcha/api2/reload")
    public ResponseEntity<String> reload(@RequestBody byte[] data) {
        RestTemplate restTemplate = new RestTemplate();
        // https://www.google.com/recaptcha/api2/reload?k=6LdUw7kUAAAAAOGXrQg3o3PK2M9Rl4wCYh7rKO8_
        // String response = restTemplate.postForObject(GOOGLE_RECAPTCHA_RELOAD + "?k=" + recaptchaClient, body, String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        // map.add("email", "first.last@example.com");

        HttpEntity<byte[]> request = new HttpEntity<>(data, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_RECAPTCHA_API2 + "reload?k=" + recaptchaClient, request, String.class);
        return response;
    }

    @GetMapping("recaptcha/api2/payload")
    //"image/jpeg"
    public ResponseEntity<byte[]> payload(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(GOOGLE_RECAPTCHA_API2 + "payload?" + request.getQueryString(), byte[].class);
        return response;
    }

    @PostMapping("recaptcha/api2/replaceimage")
    public ResponseEntity<String> replaceImage(@RequestParam MultiValueMap<String, String> body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_RECAPTCHA_API2 + "replaceimage?k=" + recaptchaClient, request, String.class);
        return response;
    }

    @PostMapping("recaptcha/api2/userverify")
    public ResponseEntity<String> userVerify(@RequestParam MultiValueMap<String, String> body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_RECAPTCHA_API2 + "userverify?k=" + recaptchaClient, request, String.class);
        return response;
    }

}
