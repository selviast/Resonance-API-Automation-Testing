package tests.resonanceDibimbing;

import body.resonanceDibimbing.UpdateTicketBody;
import body.sportActivity.UpdateSportActivityBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import static io.restassured.RestAssured.given;

public class UpdateStatusTicketById {
    @Test
    public void updateStatusTicketById() throws Exception {
        // Set Base URI dari ConfigReader
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Ambil cookie dari file cookie.json
        String cookieFile = "src/resources/json/cookie.json";
        String cookieContent = new String(Files.readAllBytes(Paths.get(cookieFile)));
        JSONObject cookieJson = new JSONObject(cookieContent);
        String cookie = cookieJson.getString("cookie");

        // Ambil ticket id dari file new_ticket_id.json
        String newCreatedTicketId = "src/resources/json/new_ticket_id.json";
        String ticketContent = new String(Files.readAllBytes(Paths.get(newCreatedTicketId)));
        JSONObject ticketJson = new JSONObject(ticketContent);
        String ticketId = ticketJson.getString("id");

        // Baca body dari file JSON
        UpdateTicketBody bodyHelper = new UpdateTicketBody();
        JSONObject requestBody = bodyHelper.getBodyFromFile("src/resources/json/update_ticket.json");

        // Update field ticketId dan createdAt
        requestBody.put("ticketId", ticketId);
        System.out.println("Tiket ID Update: " + ticketId);

        requestBody.put("solvedAt", Instant.now().toString());
        System.out.println(Instant.now().toString());

        requestBody.put("sendEmail", false);


        // Kirim request PUT
        Response response = given()
                .header("Cookie", cookie)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toString())
                .when()
                .put("/api/rest/updateStatusTicket" )
                .then()
                .extract().response();

        // Print response
        System.out.println("Response: " + response.asString());
        System.out.println("Ticket ID: " + ticketId);

        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Headers: " + response.getHeaders());


        // Validasi
        Assert.assertEquals(response.getStatusCode(), 200);
//        Assert.assertEquals(response.jsonPath().getString("message"), "data saved");
//        Assert.assertFalse(response.jsonPath().getBoolean("error"));
    }
}
