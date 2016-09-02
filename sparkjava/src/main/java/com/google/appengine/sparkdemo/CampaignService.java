package com.google.appengine.sparkdemo;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

public class CampaignService {
    private final Datastore datastore;
    private final KeyFactory keyFactory;
    private final String kind;

    /**
     * Constructor for CampaignService.
     *
     * @param datastore gcloud-java Datastore service object to execute requests
     * @param kind the kind for the Datastore entities
     */
    public CampaignService(Datastore datastore, String kind) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().kind(kind);
        this.kind = kind;
    }

    private String platformsToDb(String[] platforms) {
        String res = platforms[0];
        for (int i=1; i<platforms.length; i++) {
            res += " "+platforms[i];
        }
        return res;
    }
    public Campaign createCampaign(String name, String redirectUrl, String[] platforms) {
        checkArguments(name, redirectUrl, platforms);
        Campaign campaign = new Campaign(name, redirectUrl, platforms);

        Key key = keyFactory.newKey(campaign.getId());
        Entity entity = Entity.builder(key)
            .set("id", campaign.getId())
            .set("name", name)
            .set("redirectUrl", redirectUrl)
            .set("platforms", platformsToDb(platforms))
            .build();

        datastore.add(entity);

        return campaign;
    }
    public List<Campaign> getAllCampaigns() {
        Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, "SELECT * FROM " + kind).build();
        QueryResults<Entity> results = datastore.run(query);
        List<Campaign> campaigns = new ArrayList<>();
        while (results.hasNext()) {
            Entity result = results.next();
            String[] platforms = result.getString("platforms").split(" ");
            campaigns.add(new Campaign(result.getString("id"), result.getString("name"), result.getString("redirectUrl"), platforms));
        }
        return campaigns;
    }
    Campaign getCampaign(String id) {
        Entity entity = datastore.get(keyFactory.newKey(id));
        if (entity == null) {
            return null;
        } else {
            return new Campaign(
                entity.getString("id"),
                entity.getString("name"),
                entity.getString("redirectUrl"),
                entity.getString("platforms").split(" ")
            );
        }
    }
    public Campaign updateCampaign(String id, String name, String redirectUrl, String[] platforms) {
        checkArguments(name, redirectUrl, platforms);
        Key key = keyFactory.newKey(id);
        Entity entity = datastore.get(key);
        if (entity == null) {
            throw new IllegalArgumentException("No campaign with id '" + id + "' found");
        } else {
            entity = Entity.builder(entity)
                .set("id", id)
                .set("name", name)
                .set("redirectUrl", redirectUrl)
                .set("platforms", platformsToDb(platforms))
                .build();
            datastore.update(entity);
        }
        return new Campaign(id, name, redirectUrl, platforms);
    }

    public String deleteCampaign(String id) {
        Key key = keyFactory.newKey(id);
        datastore.delete(key);
        return "ok";
    }

    private void checkArguments(String name, String redirectUrl, String[] platforms) {
        checkArgument(name != null && !name.isEmpty(), "Parameter 'name' cannot be empty");

        boolean urlIsValid = true;
        try {
            new URL(redirectUrl);
        } catch (MalformedURLException e) {
            urlIsValid = false;
        }
        checkArgument(redirectUrl != null && urlIsValid, "Parameter 'redirectUrl' is invalid");

        if (platforms == null || platforms.length == 0) {
            checkArgument(false, "Value of parameter 'platforms' cannot be empty");
        } else {
            Set<String> counter = new HashSet<String>();
            for (int i=0; i<platforms.length; i++) {
                if (platforms[i] == null) {
                    checkArgument(false, "Value of parameter 'platforms' cannot be null");
                } else {
                    if (counter.contains(platforms[i])) {
                        checkArgument(false, "Values of parameter 'platforms' cannot be repeated");
                    } else if (!Config.allowedPlatforms.contains(platforms[i])){
                        checkArgument(false, "Invalid value of parameter 'platforms'");
                    } else {
                        counter.add(platforms[i]);
                    }
                }
            }
        }
    }

}