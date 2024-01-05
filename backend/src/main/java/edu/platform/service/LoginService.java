package edu.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static edu.platform.constants.GraphQLConstants.DATA;

@Getter
@Setter
@Service
public class LoginService {
    private static final String URL = "https://edu.21-school.ru/";
    private static final String LOGIN_XPATH = "/html/body/div/div/div/div[2]/div/div/form/div[1]/div/input";
    private static final String PASSWORD_XPATH = "/html/body/div/div/div/div[2]/div/div/form/div[2]/div/input";
    private static final String BUTTON_XPATH = "/html/body/div/div/div/div[2]/div/div/form/div[3]/button";
    private static final String AUTHORITY = "edu.21-school.ru";
    private static final String GRAPHQL_URL = "https://edu.21-school.ru/services/graphql";
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${selenium.url}")
    private String URL_SELENIUM;

    @Value("${school21.fullLogin}")
    private String fullLogin;

    @Value("${school21.login}")
    private String login;

    @Value("${school21.password}")
    private String password;

    @Value("${school21.school-id}")
    private String schoolId;

    private String cookies;

    public void Init() {
        setCookies();
    }

    public void setCookies() {
        System.out.println("[getCookies] start login " + login);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");

        URL url;
        try {
            url = new URL(URL_SELENIUM);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        WebDriver driver = new RemoteWebDriver(url, chromeOptions);
        driver.manage().window().maximize();

        String cookiesStr;
        try {
            driver.get(URL);
            TimeUnit.SECONDS.sleep(1);

            driver.findElement(By.xpath(LOGIN_XPATH)).sendKeys(fullLogin);
            driver.findElement(By.xpath(PASSWORD_XPATH)).sendKeys(password);
            driver.findElements(By.xpath(BUTTON_XPATH)).get(0).click();
            TimeUnit.SECONDS.sleep(1);

            Set<Cookie> cookies = driver.manage().getCookies();

            StringBuilder cookiesSB = new StringBuilder();
            for (Cookie cookie : cookies) {
                cookiesSB.append(cookie.getName())
                        .append("=")
                        .append(cookie.getValue())
                        .append(";");
            }
            cookiesStr = cookiesSB.toString();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
        System.out.println("[getCookies] done  " + cookiesStr);
        cookies = cookiesStr;
    }

    public JsonNode sendRequest(String requestBody) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookies);
        headers.set("schoolId", schoolId);
        headers.set("authority", AUTHORITY);
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
