import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainAppTest {

    @Test
    public void connectionTest() throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(Config.APP_URL + "?q=" + "Warszawa" + "&appid=" + Config.APP_ID).openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String respone = stringBuilder.toString();

        JSONObject rootObject = new JSONObject(respone);


        Assert.assertTrue(rootObject.getInt("cod") == 200);
    }
}
