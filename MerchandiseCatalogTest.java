package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MerchandiseCatalogTest {

    @Test
    public void verifyMerchandiseCatalog() {

        RestAssured.baseURI = "https://dev-cmnsvc-ingress.nortonmotorcycles.com/norton-ccp-catalog";

        // Request Body
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("productType", "MERCHANDISE");
        requestBody.put("market", "IB");
        requestBody.put("industry", "2Wheeler");
        requestBody.put("nameSpace", "NORTON");
        requestBody.put("regionId", "123");

        Map<String, Integer> pagination = new HashMap<>();
        pagination.put("page", 1);
        pagination.put("size", 20);

        requestBody.put("pagination", pagination);

        // API Call
        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/v2/catalogs/merchandise")
                .then()
                .statusCode(200)

                // Field Validation for Productytype &norton etc...
                .body("productType", equalToIgnoringCase("MERCHANDISE"))
                .body("nameSpace", equalToIgnoringCase("NORTON"))
                .body("market", equalToIgnoringCase("IB"))
                .body("industry", equalToIgnoringCase("2Wheeler"))

                // Structure Validation for categories array
                .body("categories", notNullValue())
                .body("categories[0].catalogSkuId", notNullValue())
                .body("categories[0].partId", notNullValue())

                // Currency Validation
                .body("categories[0].price.currency.currencyCode", equalTo("GBP"))
                .extract()
                .response();

        // Print the Request body
        System.out.println("Request Body: " + requestBody);
        System.out.println("Status Code: " + response.getStatusCode());

        // Print Full Response
        System.out.println("Response Body:");
        response.prettyPrint();

        // Extract Response Values
        String productName = response.jsonPath().getString("categories[0].name");
        String currency = response.jsonPath().getString("categories[0].price.currency.currencyCode");

        float rrp = response.jsonPath().getFloat("categories[0].price.rrp");
        float netPrice = response.jsonPath().getFloat("categories[0].price.stock.netPrice.value");
        float tax = response.jsonPath().getFloat("categories[0].price.stock.tax.value");
        float totalPrice = response.jsonPath().getFloat("categories[0].price.stock.totalPrice.value");

        // Print Values
        System.out.println("Product Name: " + productName);
        System.out.println("Currency: " + currency);
        System.out.println("RRP: " + rrp);
        System.out.println("Net Price: " + netPrice);
        System.out.println("Tax: " + tax);
        System.out.println("Total Price: " + totalPrice);


    }
}