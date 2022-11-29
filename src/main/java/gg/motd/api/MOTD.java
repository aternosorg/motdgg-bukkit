package gg.motd.api;

import com.google.gson.Gson;

import java.io.IOException;

public class MOTD {
    protected String session = null;
    protected String name = null;
    protected String text = null;
    protected String id = null;
    protected String favicon = null;

    public String getSession() {
        return session;
    }

    public MOTD setSession(String session) {
        this.session = session;
        return this;
    }

    public String getName() {
        return name;
    }

    public MOTD setName(String name) {
        this.name = name;
        return this;
    }

    public String getText() {
        return text;
    }

    public MOTD setText(String text) {
        this.text = text;
        return this;
    }

    public String getId() {
        return id;
    }

    public MOTD setId(String id) {
        this.id = id;
        return this;
    }

    public String getFavicon() {
        return favicon;
    }

    public MOTD setFavicon(String favicon) {
        this.favicon = favicon;
        return this;
    }

    public SaveResponse save(APIClient client) throws IOException {
        String response = client.request("api/save.php", "POST", new Gson().toJson(this));
        return new Gson().fromJson(response, SaveResponse.class);
    }
}
