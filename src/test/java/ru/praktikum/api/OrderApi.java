package ru.praktikum.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.Order;
import io.qameta.allure.Step;

public class OrderApi {
    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return RestAssured.given()
                .contentType("application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Получение заказов по курьеру")
    public Response getOrdersByCourierId(String courierId) {
        return RestAssured.given()
                .queryParam("courierId", courierId)
                .when()
                .get("/api/v1/orders");
    }

    @Step("Получение всех заказов")
    public Response getAllOrders() {
        return RestAssured.get("/api/v1/orders");
    }

    @Step("Получение заказа по трек-номеру")
    public Response getOrderByTrack(String trackNumber) {
        return RestAssured.get("/api/v1/orders/track?track=" + trackNumber);
    }

    @Step("Отмена заказа")
    public Response cancelOrder(String orderId) {
        return RestAssured.delete("/api/v1/orders/" + orderId);
    }
}