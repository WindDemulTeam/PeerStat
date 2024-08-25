package edu.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.parser.RequestBody;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.platform.constants.GraphQLConstants.*;

@Getter
@Setter
@Service
public class LoginService {
    private static final String GRAPHQL_URL = "https://edu.21-school.ru/services/graphql";
    private final String baseUrl = "https://auth.sberclass.ru/auth/realms/EduPowerKeycloak";
    private final String cookieUrlTemplate = baseUrl + "/protocol/openid-connect/auth?client_id=school21&redirect_uri=https://edu.21-school.ru/&state=%s&response_mode=fragment&response_type=code&scope=openid&nonce=%s";
    private final String tokenUrl = baseUrl + "/protocol/openid-connect/token";
    private final ObjectMapper MAPPER = new ObjectMapper();
    @Value("${school21.fullLogin}")
    private String fullLogin;

    @Value("${school21.login}")
    private String login;

    @Value("${school21.password}")
    private String password;

    private String schoolId;
    private String token;

    public boolean login() {
        String url;
        Matcher responseMatcher;
        HttpEntity<String> response;
        String responseStr;
        List<String> responseList;
        HttpHeaders headers;
        MultiValueMap<String, String> param;

        System.out.println("[Login] start");

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        try (CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().disableRedirectHandling().build()) {
            factory.setHttpClient(closeableHttpClient);

            RestTemplate restTemplate = new RestTemplate(factory);

            UUID state = UUID.randomUUID();
            UUID nonce = UUID.randomUUID();

            response = restTemplate.exchange(String.format(cookieUrlTemplate, state, nonce), HttpMethod.GET, null, String.class);
            responseStr = response.getBody();
            if (responseStr == null) {
                System.out.println("[Login] cookieUrlTemplate error");
                return false;
            }
            responseMatcher = Pattern.compile("(?<loginAction>)https:.+(?=\")", Pattern.MULTILINE).matcher(responseStr);
            if (responseMatcher.find()) {
                url = responseStr.substring(responseMatcher.start(), responseMatcher.end()).replace("amp;", "");
            } else {
                System.out.println("[Login] loginAction error");
                return false;
            }

            headers = new HttpHeaders();
            headers.setContentType((MediaType.APPLICATION_FORM_URLENCODED));
            param = new LinkedMultiValueMap<>();
            param.add("username", fullLogin);
            param.add("password", password);
            response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    new HttpEntity<>(param, headers),
                    String.class
            );

            responseList = response.getHeaders().get(HttpHeaders.LOCATION);
            if (responseList != null) {
                response = restTemplate.exchange(responseList.get(0),
                        HttpMethod.POST,
                        null,
                        String.class
                );
            } else {
                System.out.println("[Login] location1 error");
                return false;
            }

            responseList = response.getHeaders().get(HttpHeaders.LOCATION);
            if (responseList == null) {
                System.out.println("[Login] location2 error");
                return false;
            }
            responseStr = responseList.get(0);
            responseMatcher = Pattern.compile("(?<=code=).+", Pattern.MULTILINE).matcher(responseStr);
            String oAuthCode;
            if (responseMatcher.find()) {
                oAuthCode = responseStr.substring(responseMatcher.start(), responseMatcher.end());
            } else {
                System.out.println("[Login] oAuthCode error");
                return false;
            }

            headers = new HttpHeaders();
            headers.setContentType((MediaType.APPLICATION_FORM_URLENCODED));
            param = new LinkedMultiValueMap<>();
            param.add("client_id", "school21");
            param.add("code", oAuthCode);
            param.add("grant_type", "authorization_code");
            param.add("redirect_uri", "https://edu.21-school.ru/");

            response = restTemplate.exchange(tokenUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(param, headers),
                    String.class
            );

            token = String.valueOf(MAPPER.readTree(response.getBody()).get("access_token")).replaceAll("\"", "");
            JsonNode jsonNode = sendRequest(RequestBody.userRoleLoaderGetRoles());
            schoolId = jsonNode.get(USER).get(USER_SCHOOL_ROLES).get(0).get(SCHOOL_ID).asText();
            System.out.println("[Login] token " + token + "\nschoolId " + schoolId);
            return true;
        } catch (HttpServerErrorException | IOException e) {
            System.out.println("[Login] token error " + e.getMessage());
            return false;
        }
    }

    public JsonNode sendRequest(String requestBody) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("schoolId", schoolId);
        headers.set("userrole", "STUDENT");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        String responseStr = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            responseStr = restTemplate.postForObject(GRAPHQL_URL, request, String.class);
        } catch (RestClientException e) {
            System.out.println("[PARSER] ERROR " + e.getMessage());
        }
        return MAPPER.readTree(responseStr).get(DATA);
    }
}
