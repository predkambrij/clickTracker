package org.blatnik.o7testproj;

import java.util.UUID;

public class ShardedClickCounter {
    private String id;
    private String campaignId;
    private long count;

    public ShardedClickCounter(String id, String campaignId, long count) {
        this.id = id;
        this.campaignId = campaignId;
        this.count = count;
    }

    // getters setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }
}
