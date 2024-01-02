import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.max.lesson3.home.accuweather.weather.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;


public class TestMock extends AccuweatherMockAbstractTest{
    private static final Logger logger
            = LoggerFactory.getLogger(TestMock.class);

    public static final String code200 = "/forecasts/v1/daily/1day/257514";
    public static final String code400 = "/forecasts/v1/daily/10day/690127";

    @Test
    @DisplayName("Проверка при ответе 200")
    @Severity(SeverityLevel.NORMAL)
    @Owner("Понять и простить")
    @Epic(value = "TestMock")
    void testOneDay() throws JsonProcessingException {
        logger.info("test 1 is run");
        ObjectMapper mapper = new ObjectMapper();
        Weather weather = new Weather();

        stubFor(get(urlPathEqualTo(code200)).withHeader("Content-Type", containing("JSON"))
                .withQueryParam("i", containing("tring"))
                .willReturn(aResponse().withStatus(200).withBody(mapper.writeValueAsString(weather))));

        String responseBody = given().queryParam("i", "string123")
                .header("Content-Type", "JSONChecking")
                .when().request(Method.GET, getBaseUrl() + code200)
                .then().statusCode(200).extract().body().asString();

        Assertions.assertEquals(mapper.writeValueAsString(weather), responseBody);
    }

    @Test
    @DisplayName("Проверка при ответе 400")
    @Severity(SeverityLevel.NORMAL)
    @Owner("Понять и простить")
    @Epic(value = "TestMock")
    void testTenDays() {
        logger.info("test 2 is run");

        stubFor(get(urlPathEqualTo(code200)).withQueryParam("i", equalTo("string"))
                .withHeader("Accept", equalTo("text/xml"))
                .willReturn(aResponse().withStatus(400).withBody("Failed")));

        String responseBody = given().queryParam("i", "string")
                .header("Accept", "text/xml")
                .when().get(getBaseUrl() + code400)
                .then().statusCode(400).extract().body().asString();

        Assertions.assertEquals("Failed", responseBody);
    }
}