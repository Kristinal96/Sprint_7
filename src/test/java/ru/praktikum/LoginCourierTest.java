package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

public class LoginCourierTest {

    private String courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Создание курьера")
    public Response createCourier(String login, String password, String firstName) {
        return given()
                .contentType("application/json")
                .body("{\n" +
                        "    \"login\": \"" + login + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"firstName\": \"" + firstName + "\"\n" +
                        "}")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Удаление курьера")
    public void deleteCourier(String courierId) {
        given()
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    @Step("Авторизация курьера")
    public Response loginCourier(String login, String password) {
        return given()
                .contentType("application/json")
                .body("{\n" +
                        "    \"login\": \"" + login + "\",\n" +
                        "    \"password\": \"" + password + "\"\n" +
                        "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Test
    @Step("Тест на авторизацию курьера")
    public void testLoginCourier() {
        String login = "ninjaZZ11111";
        String password = "1234";
        String firstName = "saske";

        // Сначала создаем курьера
        Response creationResponse = createCourier(login, password, firstName);
        creationResponse.then()
                .statusCode(201) // Ожидаем успешное создание
                .body("ok", equalTo(true));

        // Сохраняем ID созданного курьера
        courierId = creationResponse.jsonPath().getString("id");

        // Затем пытаемся авторизоваться
        Response authResponse = loginCourier(login, password);
        authResponse.then()
                .statusCode(200) // Ожидаем успешную авторизацию
                .body("id", notNullValue()); // Ожидаем наличие поля 'id'
    }

    @Test
    @Step("Тест на авторизацию курьера с неверными данными")
    public void testLoginCourierWithInvalidCredentials() {
        String login = "invalid_login";
        String password = "invalid_password";

        Response response = loginCourier(login, password);

        response.then()
                .statusCode(404) // Ожидаем статус 404 (Not Found)
                .body("message", equalTo("Учетная запись не найдена")); // Ожидаем соответствующее сообщение
    }

    @Test
    @Step("Тест на авторизацию курьера без обязательных полей")
    public void testLoginCourierWithoutRequiredFields() {
        String login = "ninja";
        String password = "";

        Response response = loginCourier(login, password);

        response.then()
                .statusCode(400) // Ожидаем статус 400 (Bad Request)
                .body("message", equalTo("Недостаточно данных для входа")); // Ожидаем сообщение об ошибке
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId); // Удаляем созданный аккаунт после теста
        }
    }
}