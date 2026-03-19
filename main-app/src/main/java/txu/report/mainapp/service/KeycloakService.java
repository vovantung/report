package txu.report.mainapp.service;

import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import txu.report.mainapp.dto.KeycloakCreateUserRequest;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.token-url}")
    private String tokenUrl;


    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;


    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return (String) response.getBody().get("access_token");
    }
    public Map<String, Object> getRoleByName(String roleName) {

        String token = getAccessToken();

        String url = "https://keycloak.txuyen.com/admin/realms/master/roles/" + roleName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    public void assignRealmRolesToUser(String userId, List<String> roleNames) {

        String token = getAccessToken();

        List<Map<String, Object>> roles = new ArrayList<>();

        for (String roleName : roleNames) {
            Map<String, Object> role = getRoleByName(roleName);

            Map<String, Object> roleRepresentation = new HashMap<>();
            roleRepresentation.put("id", role.get("id"));
            roleRepresentation.put("name", role.get("name"));

            roles.add(roleRepresentation);
        }

        String url = "https://keycloak.txuyen.com/admin/realms/master/users" + userId + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(roles, headers);

        restTemplate.postForEntity(url, request, Void.class);
    }


    public String createKeycloakUser(String username, String email, String lastName, String firstName) {

        // ----- Header -----
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());


        KeycloakCreateUserRequest body = new KeycloakCreateUserRequest();
        body.setUsername(username);
        body.setEnabled(true);
        body.setEmail(email);
        body.setFirstName(firstName);
        body.setLastName(lastName);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

//        try {
            ResponseEntity<Void> response = restTemplate.exchange("https://keycloak.txuyen.com/admin/realms/master/users", HttpMethod.POST, request, Void.class);
            // Lấy userId từ header Location
            String location = response.getHeaders().getFirst("Location");
            return location.substring(location.lastIndexOf("/") + 1);

//        } catch (HttpStatusCodeException ex) {
////            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
//            return null;
//        }
    }

//    public String createKeycloakUser(String username, String email, String lastName, String firstName) {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        String basicAuth = Base64.getEncoder().encodeToString(("txuyen.com" + ":" + "nD7tSw1pDeHMpXq1Rn0p5cW29tlaB3gb").getBytes(StandardCharsets.UTF_8));
//        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);
//
//        // ----- Body -----
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "password");
//        body.add("username", "admin");
//        body.add("password", "Phan@123");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//        String token = "";
//
//        try {
//            ResponseEntity<Map> response = restTemplate.exchange("https://keycloak.txuyen.com/realms/master/protocol/openid-connect/token", HttpMethod.POST, request, Map.class);
//            token = (String) response.getBody().get("access_token");
//        } catch (HttpStatusCodeException ex) {
//
//        }
//
//        HttpHeaders headers_ = new HttpHeaders();
//        headers_.setContentType(MediaType.APPLICATION_JSON);
//        headers_.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
//
//        KeycloakCreateUserRequest body_ = new KeycloakCreateUserRequest();
//        body_.setUsername(username);
//        body_.setEnabled(true);
//        body_.setEmail(email);
//        body_.setFirstName(firstName);
//        body_.setLastName(lastName);
//
//        HttpEntity<KeycloakCreateUserRequest> entity = new HttpEntity<>(body_, headers_);
//
//        try {
//            ResponseEntity<Void> response_ = restTemplate.exchange("https://keycloak.txuyen.com/admin/realms/master/users", HttpMethod.POST, entity, Void.class);
////            return ResponseEntity.status(response_.getStatusCode()).build();
//            return username;
//
//        } catch (HttpStatusCodeException ex) {
////            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
//            return null;
//        }
//    }

}
