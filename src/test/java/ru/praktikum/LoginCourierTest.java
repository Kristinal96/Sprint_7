package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import ru.praktikum.api.CourierApi;
import ru.praktikum.models.Courier;
import static org.hamcrest.Matchers.*;

public class LoginCourierTest {

    private static CourierApi courierApi;
    private String courierId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierApi = new CourierApi();
    }

    @Before
    public void prepareTestData() {
        Courier courier = new Courier("ninjaZZ111111", "1234", "saske");
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
    @Step("Тест на успешную авторизацию")
    public void testValidLogin() {
        Courier validCourier = new Courier("ninjaZZ111111", "1234", "saske");
        Response response = courierApi.loginCourier(validCourier);

        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Step("Тест на неудачную авторизацию")
    public void testInvalidLogin() {
        Courier invalidCourier = new Courier("invalid_login", "invalid_password", "saske");
        Response response = courierApi.loginCourier(invalidCourier);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Тест на авторизацию без обязательных полей")
    public void testLoginWithoutRequiredFields() {
        Courier incompleteCourier = new Courier("ninja", "", "saske");
        Response response = courierApi.loginCourier(incompleteCourier);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}