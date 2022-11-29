package gg.motd.api;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class APIClient {
    protected String userAgent = "motdgg-bukkit/unknown/unknown";

    public APIClient() {

    }

    public APIClient(String userAgent) {
        this.userAgent = userAgent;
    }

    public String request(String endpoint) throws IOException {
        return this.request(endpoint, "GET", null);
    }

    public String request(String endpoint, String method, String body) throws IOException {
        URL url = new URL("https://motd.gg/" + endpoint);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);

        //send log to api
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", this.userAgent);

        if (body != null) {
            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(out.length);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }
        }
        connection.connect();

        if (connection.getResponseCode() == 404) {
            return null;
        }

        if (connection.getResponseCode() != 200) {
            throw new IOException("Received non-200 response code: " + connection.getResponseCode());
        }

        InputStreamReader isReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
        return new BufferedReader(isReader)
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public MOTD getMotd(String id) throws IOException {
        String response = this.request(id + ".json");
        return new Gson().fromJson(response, MOTD.class);
    }
}
