package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

public class GetOrdersTest {

    private String courierId; // Переменная для хранения ID курьера

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

    @Step("Получение списка заказов")
    public Response getOrders(String courierId, String nearestStation, String limit, String page) {
        return given()
                .queryParam("courierId", courierId)
                .queryParam("nearestStation", nearestStation)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get("/api/v1/orders");
    }

    @Test
    @Step("Тест на получение списка заказов с заданным courierId")
    public void testGetOrdersWithValidCourierId() {
        // Создаем курьера
        String login = "uniqueLoginForThisTest11111";
        String password = "somePassword";
        String firstName = "Some First Name";

        Response creationResponse = createCourier(login, password, firstName);
        creationResponse.then()
                .statusCode(201) // Убеждаемся, что курьер успешно создан
                .body("ok", equalTo(true));

        // Получаем ID созданного курьера
        courierId = creationResponse.jsonPath().getString("id");

        // Отправляем запрос на получение списка заказов с правильным courierId
        Response ordersResponse = getOrders(courierId, "1", "10", "0");

        ordersResponse.then()
                .statusCode(200) // Ожидаем успешный ответ
                .body("orders", notNullValue()); // Проверяем, что поле "orders" присутствует
    }

    @Test
    @Step("Тест на получение списка заказов с invalid courierId")
    public void testGetOrdersWithInvalidCourierId() {
        String invalidCourierId = "999";

        Response ordersResponse = getOrders(invalidCourierId, "1", "10", "0");

        ordersResponse.then()
                .statusCode(404) // Ожидаем статус 404 (Not Found)
                .body("message", equalTo("Курьер с идентификатором 999 не найден")); // Ожидаем сообщение об ошибке
    }

    @Test
    @Step("Тест на получение списка заказов без указания courierId")
    public void testGetOrdersWithoutCourierId() {
        String courierId = null;
        String nearestStation = "1";
        String limit = "10";
        String page = "0";

        Response response = getOrders(courierId, nearestStation, limit, page);

        response.then()
                .statusCode(200) // Ожидаем статус 200 (OK)
                .body("orders", notNullValue()); // Ожидаем, что поле "orders" не null
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId); // Удаляем созданного курьера
        }
    }
}