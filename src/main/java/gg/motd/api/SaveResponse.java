package gg.motd.api;

public class SaveResponse {
    protected boolean success;
    protected MOTD motd;

    public boolean isSuccess() {
        return success;
    }

    public MOTD getMotd() {
        return motd;
    }
}
