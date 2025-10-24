package tests.resonanceDibimbing;

import body.resonanceDibimbing.CreateTicketBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class CreateTicketTest {
    private String cookie;

    @BeforeClass
    public void setup() throws Exception {
        // Set base URI
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");
        File cookieFile = new File("src/resources/json/cookie.json");
        String rawContent = Files.readString(cookieFile.toPath()).trim();
        JSONObject tokenJson = new JSONObject(rawContent);
        cookie = tokenJson.optString("cookie", null);

        if (cookie == null || cookie.isEmpty()) {
            throw new RuntimeException("Token kosong. Jalankan LoginTest dulu.");
        }
    }

    @Test
    public void createTicketTest() throws IOException {
        // Buat body dari class
        CreateTicketBody bodyObj = new CreateTicketBody();
        JSONObject requestBody = bodyObj.getBody();

        // Kirim request POST dengan Cookie
        Response response = given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .header("Cookie", cookie)
                .body(requestBody.toString())
                .when()
                .post("/api/rest/createTicket")
                .then()
                .extract().response();

        // Print response untuk debug
        System.out.println("Response: " + response.asString());

        // Validasi status code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200, "Status code bukan 200! Response: " + response.asString());

        // Ambil ticket id, cek null dulu
        String newTicketId = response.jsonPath().getString("id");
        if (newTicketId != null && !newTicketId.isEmpty()) {
            System.out.println("New Ticket ID: " + newTicketId);

            // Simpan ke file JSON
            JSONObject saveJson = new JSONObject();
            saveJson.put("id", newTicketId);

            try (FileWriter file = new FileWriter("src/resources/json/new_ticket_id.json")) {
                file.write(saveJson.toString(4));
            }
        } else {
            System.out.println("Ticket ID tidak ditemukan di response!");
        }
    }
}
