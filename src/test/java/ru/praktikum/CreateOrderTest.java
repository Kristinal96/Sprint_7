package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.api.OrderApi;
import ru.praktikum.models.Order;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private static OrderApi orderApi;
    private String[] colors;

    public CreateOrderTest(String[] colors) {
        this.colors = colors;
    }

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        orderApi = new OrderApi();
    }

    @Parameterized.Parameters(name = "Тест с цветами: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}, // Без указания цвета
        });
    }

    @Test
    @Step("Тест на создание заказа с указанными цветами")
    public void testCreateOrderWithColors() {
        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );

        Response response = orderApi.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}