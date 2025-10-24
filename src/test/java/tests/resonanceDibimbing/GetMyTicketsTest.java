package tests.resonanceDibimbing;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static io.restassured.RestAssured.given;

public class GetMyTicketsTest {

    private String cookie;

    @BeforeClass
    public void setup() throws Exception {
        // Set base URI dari config.properties
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
    public void getMyTickets() throws IOException {
        // Baca file JSON
//        FileReader reader = new FileReader("src/resources/json/myTickets_id.json");
//        JSONObject json = new JSONObject(new JSONTokener(reader));
//        reader.close();
//
//        // Ambil activity_id dari JSON
//        String myTicket_id = json.getString("id");
//
//        System.out.println("Activity ID: " + myTicket_id);

        Response response = given()
                .header("Cookie", cookie)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .when()
                .get("/api/rest/myTickets")
                .then()
                .extract().response();

        // Print response
        System.out.println("Response: " + response.asString());

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);

//        // Validasi error = false
//        Boolean error = response.jsonPath().getBoolean("error");
//        Assert.assertFalse(error, "Expected error = false");

        // Validasi result.id sesuai
//        Integer id = response.jsonPath().getInt("result.id");
//        Assert.assertEquals(id.intValue(), activityId, "Activity ID tidak sesuai");
//
//        String description = response.jsonPath().getString("result.description");
//        Assert.assertNotNull(description, "Description should not be null");
        String myTickets_id = response.jsonPath().getString("id");

        System.out.println("myTicket ID: " + myTickets_id);

        JSONObject tokenJson = new JSONObject();
        tokenJson.put("myTickets_id", myTickets_id);


        try (FileWriter file = new FileWriter("src/resources/json/myTickets_id.json")) {
            file.write(tokenJson.toString(4)); // 4 = indentation
            file.flush();
        }

    }
}
