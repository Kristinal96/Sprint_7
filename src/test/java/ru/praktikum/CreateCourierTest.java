package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

public class CreateCourierTest {

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

    @Test
    @Step("Тест на создание курьера")
    public void testCreateCourier() {
        String login = "ninjaZ11111"; // Используем уникальное имя для теста
        String password = "1234";
        String firstName = "saske";

        Response response = createCourier(login, password, firstName);

        response.then()
                .statusCode(201) // Ожидаем статус 201 (Created)
                .body("ok", equalTo(true)); // Ожидаем, что поле "ok" равно true

        courierId = response.jsonPath().getString("id"); // Сохраняем ID созданного курьера
    }

    @Test
    @Step("Тест на создание курьера с существующим логином")
    public void testCreateCourierWithExistingLogin() {
        String login = "existing_login";
        String password = "1234";
        String firstName = "saske";

        Response response = createCourier(login, password, firstName);

        response.then()
                .statusCode(409) // Ожидаем статус 409 (Conflict)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой.")); // Ожидаем сообщение об ошибке
    }

    @Test
    @Step("Тест на создание курьера без обязательных полей")
    public void testCreateCourierWithoutRequiredFields() {
        String login = "ninja";
        String password = ""; // Пароль пуст, что недопустимо
        String firstName = "saske";

        Response response = createCourier(login, password, firstName);

        response.then()
                .statusCode(400) // Ожидаем статус 400 (Bad Request)
                .body("message", equalTo("Недостаточно данных для создания учетной записи")); // Ожидаем сообщение об ошибке
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId); // Удаляем созданный аккаунт после теста
        }
    }
}
