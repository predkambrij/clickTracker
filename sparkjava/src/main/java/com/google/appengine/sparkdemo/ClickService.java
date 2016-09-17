package com.google.appengine.sparkdemo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;


public class ClickService {
    private final Datastore datastore;
    private final KeyFactory keyFactory;
    private final String kind;
    private final ShardedClickCounterService shardedClickCounter;

    /**
     * Constructor for CampaignService.
     *
     * @param datastore gcloud-java Datastore service object to execute requests
     * @param kind the kind for the Datastore entities
     */
    public ClickService(Datastore datastore, String kind, ShardedClickCounterService shardedClickCounter) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().kind(kind);
        this.kind = kind;
        this.shardedClickCounter = shardedClickCounter;
    }

    public String addClick(String campaignId, CampaignService campaignService, HashMap<String, Campaign> campaignsCache
            //, ShardedCounterService shardedCounterService
            ) {
        // persistence, to avoid excessive db access
        Campaign campaign = null;
        if (campaignsCache.containsKey(campaignId)) {
            campaign = campaignsCache.get(campaignId);
        } else {
            // get the campaign from db
            campaign = campaignService.getCampaign(campaignId);
            campaignsCache.put(campaignId, campaign);
        }

        // if campaign id is invalid, redirect to outfit7
        if (campaign == null) {
            return "http://outfit7.com";
        }
        /*
        Click click = new Click(campaignId);

        Key key = keyFactory.newKey(click.getId());
        Entity entity = Entity.builder(key)
            .set("id", click.getId())
            .set("campaignId", campaignId)
            .build();

        datastore.add(entity);
*/

        return campaign.getRedirectUrl();
    }

    public String clickAnalytics(String campaignId) {
        shardedClickCounter.incrementCountCampaign(campaignId);
        //long count = shardedClickCounter.getCount(campaignId); System.out.println(count);
/*
        Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, "SELECT count() FROM " + kind+ " where campaignId=@cid")
                .setBinding("cid", campaignId).build();
        QueryResults<Entity> results = datastore.run(query);
        long clickNum = 0;
        
        while (results.hasNext()) {
            Entity result = results.next();
            clickNum++;
            String t = result.getString("id");
            System.out.println(t);
        }
*/
        return "";
    }
}
