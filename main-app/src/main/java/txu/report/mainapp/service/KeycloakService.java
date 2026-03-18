package txu.report.mainapp.service;

import lombok.AllArgsConstructor;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import txu.report.mainapp.dto.KeycloakCreateUserRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@AllArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate;

    public String createKeycloakUser(String username, String email, String lastName, String firstName) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String basicAuth = Base64.getEncoder().encodeToString(("txuyen.com" + ":" + "nD7tSw1pDeHMpXq1Rn0p5cW29tlaB3gb").getBytes(StandardCharsets.UTF_8));
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);

        // ----- Body -----
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", "admin");
        body.add("password", "Phan@123");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        String token = "";

        try {
            ResponseEntity<Map> response = restTemplate.exchange("https://keycloak.txuyen.com/realms/master/protocol/openid-connect/token", HttpMethod.POST, request, Map.class);
            token = (String) response.getBody().get("access_token");
        } catch (HttpStatusCodeException ex) {

        }

        HttpHeaders headers_ = new HttpHeaders();
        headers_.setContentType(MediaType.APPLICATION_JSON);
        headers_.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        KeycloakCreateUserRequest body_ = new KeycloakCreateUserRequest();
        body_.setUsername(username);
        body_.setEnabled(true);
        body_.setEmail(email);
        body_.setFirstName(firstName);
        body_.setLastName(lastName);

        HttpEntity<KeycloakCreateUserRequest> entity = new HttpEntity<>(body_, headers_);

        try {
            ResponseEntity<Void> response_ = restTemplate.exchange("https://keycloak.txuyen.com/admin/realms/master/users", HttpMethod.POST, entity, Void.class);
//            return ResponseEntity.status(response_.getStatusCode()).build();
            return username;

        } catch (HttpStatusCodeException ex) {
//            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
            return null;
        }


    }
}
