package tests.resonanceDibimbing;

import body.sportActivity.CreateSportActivityBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class CreateTicketTest {
    private String token;
    private String sessionToken;

    @BeforeClass
    public void setup() throws Exception {
        // Set base URI
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Baca token dari file token.json
        // Baca sessionToken dari JSON
        JSONObject tokenJson = new JSONObject(new FileReader("src/resources/json/sessionToken.json"));
        sessionToken = tokenJson.getString("sessionToken");
        if (sessionToken == null || sessionToken.isEmpty()) {
            throw new RuntimeException("Session token kosong. Jalankan LoginTest dulu.");
        }
    }

    @Test
    public void createSportActivity() throws IOException {
        // Buat body dari class
        CreateSportActivityBody bodyObj = new CreateSportActivityBody();
        JSONObject requestBody = bodyObj.getBody();

        // Kirim request POST
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .cookie("__Secure-next-auth.session-token", sessionToken)
                .body(requestBody.toString())
                .when()
                .post("/api/rest/createTicket")
                .then()
                .extract().response();

        System.out.println("Response: " + response.asString());

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);

        // Validasi error = false
        Assert.assertFalse(response.jsonPath().getBoolean("error"));

//        // Validasi message (pake user id po o ya validate nya??)
//        Assert.assertEquals(response.jsonPath().getString("title"), ConfigReader.getProperty("title"));

        // Save activity id
        String new_ticket_id = response.jsonPath().getString("id");
        System.out.println("New Ticket ID: " + new_ticket_id);

        JSONObject tokenJson = new JSONObject();
        tokenJson.put("id", new_ticket_id);

        try (FileWriter file = new FileWriter("src/resources/json/new_ticket_id.json")) {
            file.write(tokenJson.toString(4)); // 4 = indentation
            file.flush();
        }
    }
}
