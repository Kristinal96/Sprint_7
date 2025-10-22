package ru.praktikum.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.Courier;

public class CourierApi {
    public Response createCourier(Courier courier) {
        return RestAssured.given()
                .contentType("application/json")
                .body(courier) //
                .when()
                .post("/api/v1/courier");
    }

    public Response deleteCourier(String courierId) {
        return RestAssured.delete("/api/v1/courier/" + courierId);
    }

    public Response loginCourier(Courier courier) {
        return RestAssured.given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }
}
