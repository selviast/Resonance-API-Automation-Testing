package tests.resonanceDibimbing;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import static io.restassured.RestAssured.given;

public class DeleteTicketByIdTest {
    private String cookie;

    @BeforeClass
    public void setup() throws Exception {
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
    public void deleteticketyById() throws IOException {
        // Baca file JSON
        FileReader reader = new FileReader("src/resources/json/new_ticket_id.json");
        JSONObject json = new JSONObject(new JSONTokener(reader));
        reader.close();

        // Ambil activity_id dari JSON
        String newCreatedTicketId = json.getString("id");

        System.out.println("New Created Ticket ID: " + newCreatedTicketId);

        Response response = given()
                .header("Cookie", cookie)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .queryParam("ticketId", newCreatedTicketId)
                .when()
                .delete("/api/rest/deleteTicket")
                .then()
                .extract().response();


        // Print response
        System.out.println("Response: " + response.asString());
        System.out.println("Status Code: " + response.getStatusCode());

        // Validasi error = false
        // Ambil message dari response
        String message = response.jsonPath().getString("message");
        // Assert false jika message = "Ticket not found"
        Assert.assertFalse("Ticket not found".equals(message), "Ticket not found di response!");


        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);

        // Validasi message
        Assert.assertEquals(response.jsonPath().getString("message"), "Ticket deleted successfully");
    }
}
