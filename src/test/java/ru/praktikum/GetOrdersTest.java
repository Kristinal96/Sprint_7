package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import ru.praktikum.api.OrderApi;
import ru.praktikum.api.CourierApi;
import ru.praktikum.models.Courier;
import static org.hamcrest.Matchers.*;;

public class GetOrdersTest {

    private static OrderApi orderApi;
    private static CourierApi courierApi;
    private String courierId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        orderApi = new OrderApi();
        courierApi = new CourierApi();
    }

    @Before
    public void prepareTestData() {
        Courier courier = new Courier("uniqueLoginForThisTest1111111", "somePassword", "Test User");
        Response res = courierApi.createCourier(courier);
        courierId = res.jsonPath().getString("id");
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }

    @Test
    @Step("Тест на получение списка заказов с заданным courierId")
    public void testGetOrdersWithValidCourierId() {
        Response ordersResponse = orderApi.getOrdersByCourierId(courierId);

        ordersResponse.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @Step("Тест на получение списка заказов с invalid courierId")
    public void testGetOrdersWithInvalidCourierId() {
        String invalidCourierId = "9999";

        Response ordersResponse = orderApi.getOrdersByCourierId(invalidCourierId);

        ordersResponse.then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором 9999 не найден"));
    }

    @Test
    @Step("Тест на получение списка заказов без указания courierId")
    public void testGetOrdersWithoutCourierId() {
        Response response = orderApi.getAllOrders();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}