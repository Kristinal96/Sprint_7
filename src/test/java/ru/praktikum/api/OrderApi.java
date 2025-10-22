package ru.praktikum.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.Order;

public class OrderApi {
    public Response createOrder(Order order) {
        return RestAssured.given()
                .contentType("application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    public Response getOrdersByCourierId(String courierId) {
        return RestAssured.given()
                .queryParam("courierId", courierId)
                .when()
                .get("/api/v1/orders");
    }

    public Response getAllOrders() {
        return RestAssured.get("/api/v1/orders");
    }

    public Response getOrderByTrack(String trackNumber) {
        return RestAssured.get("/api/v1/orders/track?track=" + trackNumber);
    }

    public Response cancelOrder(String orderId) {
        return RestAssured.delete("/api/v1/orders/" + orderId);
    }
}