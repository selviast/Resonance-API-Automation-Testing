package tests.auth;

import body.auth.LoginBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileWriter;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class LoginTest {

    @Test
    public void login() throws IOException {
        // Set base URI dari ConfigReader
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Buat body login
        LoginBody loginBody = new LoginBody();

        // Kirim request POST ke endpoint login
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(loginBody.loginData().toString())
                .when()
                .post("/api/rest/login") // endpoint login REST
                .then()
                .statusCode(200)
                .extract().response();

        // Print response
        System.out.println("Response: " + response.asString());

        // Validasi token di response body
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
        System.out.println("Token: " + token);

        // Ambil session cookie jika ada (untuk NextAuth / TRPC)
        String sessionCookie = response.getCookie("__Secure-next-auth.session-token");
        if (sessionCookie != null && !sessionCookie.isEmpty()) {
            System.out.println("Session cookie: " + sessionCookie);
        }

        // Simpan token & session cookie ke file JSON
        JSONObject tokenJson = new JSONObject();
        tokenJson.put("token", token);
        tokenJson.put("sessionToken", sessionCookie != null ? sessionCookie : "");

        try (FileWriter file = new FileWriter("src/resources/json/token.json")) {
            file.write(tokenJson.toString(4)); // 4 = indentation
            file.flush();
        }

        System.out.println("Token & session cookie berhasil disimpan di resources/json/token.json");
    }
}
