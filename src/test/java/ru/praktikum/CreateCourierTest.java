package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import ru.praktikum.api.CourierApi;
import ru.praktikum.models.Courier;
import static org.hamcrest.Matchers.*;

public class CreateCourierTest {

    private static CourierApi courierApi;
    private String courierId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierApi = new CourierApi();
    }

    @Before
    public void prepareTestData() {

    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }

    @Test
    @Step("Тест на успешное создание курьера")
    public void testCreateCourier() {
        Courier validCourier = new Courier("ninjaZ11111111", "1234", "saske");
        Response response = courierApi.createCourier(validCourier);

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = response.jsonPath().getString("id");
    }

    @Test
    @Step("Тест на запрет создания одинакового курьера дважды")
    public void testCantCreateSameCourierTwice() {
        Courier sameCourier = new Courier("existing_login111", "1234", "saske");

        // первая регистрация успешна
        Response firstResponse = courierApi.createCourier(sameCourier);
        firstResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // вторая регистрация должна завершиться ошибкой
        Response secondResponse = courierApi.createCourier(sameCourier);
        secondResponse.then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Step("Тест на отсутствие обязательных полей")
    public void testMissingRequiredFields() {
        Courier incompleteCourier = new Courier("ninja", "", "saske");
        Response response = courierApi.createCourier(incompleteCourier);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}