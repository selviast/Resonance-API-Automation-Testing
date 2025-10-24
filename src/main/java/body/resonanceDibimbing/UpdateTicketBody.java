package body.resonanceDibimbing;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;

public class UpdateTicketBody {
    public JSONObject getBodyFromFile(String filePath) throws Exception {
        FileInputStream file = new FileInputStream(filePath);
        JSONObject body = new JSONObject(new JSONTokener(file));
        file.close();
        return body;
    }
}
