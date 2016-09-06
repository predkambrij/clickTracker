package com.google.appengine.sparkdemo;

import java.util.UUID;

public class Click {
    private String id;
    private String campaignId;

    public Click(String campaignId) {
        this.id = UUID.randomUUID().toString();
        this.campaignId = campaignId;
    }

    // getters setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }
}
