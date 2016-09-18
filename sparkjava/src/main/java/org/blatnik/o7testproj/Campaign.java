package org.blatnik.o7testproj;

import java.util.UUID;

public class Campaign {
    private String id;
    private String name;
    private String redirectUrl;
    private String[] platforms;
    
    public Campaign(String name, String redirectUrl, String[] platforms) {
        this(UUID.randomUUID().toString(), name, redirectUrl, platforms);
    }

    public Campaign(String id, String name, String redirectUrl, String[] platforms) {
        this.id = id;
        this.name = name;
        this.redirectUrl = redirectUrl;
        this.platforms = platforms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }
}
