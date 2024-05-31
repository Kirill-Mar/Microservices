package service;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class UserClient {

    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

    private final String url = "http://94.198.50.185:7081/api/users";
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private static StringBuilder result = new StringBuilder();

    public static void main(String[] args) {
        UserClient userClient = new UserClient();
        try {
            userClient.performOperations();
            if (result.length() != 18) {
                logger.error("Ошибка: итоговый код имеет неправильную длину");
            } else {
                logger.info("Итоговый код - {}", result);
            }
        } catch (Exception e) {
            logger.error("Ошибка выполнения операций: ", e);
        }
    }

    public void performOperations() {
        String sessionId = getAllUsers();
        headers.set("cookie", sessionId);
        createUser();
        updateUser();
        deleteUser(3L);
    }

    private String getAllUsers() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return String.join(";", Objects.requireNonNull(response.getHeaders().get("set-cookie")));
        } catch (Exception e) {
            logger.error("Ошибка получения всех пользователей: ", e);
            throw e;
        }
    }

    private void createUser() {
        try {
            User user = new User(3L, "James", "Brown", (byte) 30, "james.brown@example.com");
            HttpEntity<User> entity = new HttpEntity<>(user, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            result.append(response.getBody());
        } catch (Exception e) {
            logger.error("Ошибка создания пользователя: ", e);
            throw e;
        }
    }

    private void updateUser() {
        try {
            User user = new User(3L, "Thomas", "Shelby", (byte) 30, "thomas.shelby@example.com");
            HttpEntity<User> entity = new HttpEntity<>(user, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            result.append(response.getBody());
        } catch (Exception e) {
            logger.error("Ошибка обновления пользователя: ", e);
            throw e;
        }
    }

    private void deleteUser(Long id) {
        try {
            HttpEntity<User> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url + "/" + id, HttpMethod.DELETE, entity, String.class);
            result.append(response.getBody());
        } catch (Exception e) {
            logger.error("Ошибка удаления пользователя: ", e);
            throw e;
        }
    }
}
