package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Создание заказа")
    public Response createOrder(
            String firstName,
            String lastName,
            String address,
            int metroStation,
            String phone,
            int rentTime,
            String deliveryDate,
            String comment,
            List<String> color
    ) {
        return given()
                .contentType("application/json")
                .body("{" +
                        "\"firstName\":\"" + firstName + "\"," +
                        "\"lastName\":\"" + lastName + "\"," +
                        "\"address\":\"" + address + "\"," +
                        "\"metroStation\":" + metroStation + "," +
                        "\"phone\":\"" + phone + "\"," +
                        "\"rentTime\":" + rentTime + "," +
                        "\"deliveryDate\":\"" + deliveryDate + "\"," +
                        "\"comment\":\"" + comment + "\"," +
                        "\"color\":[" + formatColors(color) + "]" +
                        "}")
                .when()
                .post("/api/v1/orders");
    }

    private String formatColors(List<String> colors) {
        return String.join(",", colors.stream().map(c -> "\"" + c + "\"").toArray(String[]::new));
    }

    @Test
    @Step("Тест на создание заказа с черным цветом")
    public void testCreateOrderWithBlackColor() {
        String firstName = "Naruto";
        String lastName = "Uchiha";
        String address = "Konoha, 142 apt.";
        int metroStation = 4;
        String phone = "+7 800 355 35 35";
        int rentTime = 5;
        String deliveryDate = "2020-06-06";
        String comment = "Saske, come back to Konoha";
        List<String> color = new ArrayList<>(List.of("BLACK")); // Использование черного цвета

        Response response = createOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);

        response.then()
                .statusCode(201) // Ожидаемый статус 201 (заказ создан)
                .body("track", notNullValue()); // Ожидание наличия номера трека
    }

    @Test
    @Step("Тест на создание заказа с обоими цветами")
    public void testCreateOrderWithBothColors() {
        String firstName = "Naruto";
        String lastName = "Uchiha";
        String address = "Konoha, 142 apt.";
        int metroStation = 4;
        String phone = "+7 800 355 35 35";
        int rentTime = 5;
        String deliveryDate = "2020-06-06";
        String comment = "Saske, come back to Konoha";
        List<String> color = new ArrayList<>(List.of("BLACK", "GREY")); // Два цвета

        Response response = createOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);

        response.then()
                .statusCode(201) // Ожидаемый статус 201 (заказ создан)
                .body("track", notNullValue()); // Ожидание наличия номера трека
    }

    @Test
    @Step("Тест на создание заказа без указания цвета")
    public void testCreateOrderWithoutColor() {
        String firstName = "Naruto";
        String lastName = "Uchiha";
        String address = "Konoha, 142 apt.";
        int metroStation = 4;
        String phone = "+7 800 355 35 35";
        int rentTime = 5;
        String deliveryDate = "2020-06-06";
        String comment = "Saske, come back to Konoha";
        List<String> color = new ArrayList<>(); // Без выбора цвета

        Response response = createOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);

        response.then()
                .statusCode(201) // Ожидаемый статус 201 (заказ создан)
                .body("track", notNullValue()); // Ожидание наличия номера трека
    }
}
