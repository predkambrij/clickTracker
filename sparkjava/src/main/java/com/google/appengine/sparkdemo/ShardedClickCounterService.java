package com.google.appengine.sparkdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

public class ShardedClickCounterService {

    private final Datastore datastore;
    private final KeyFactory keyFactory;
    private final String kind;

    /**
     * Default number of shards.
     */
    private static final int NUM_SHARDS = 20;

    /**
     * A random number generator, for distributing writes across shards.
     */
    private final Random generator = new Random();

    /**
     * Constructor for UserService.
     *
     * @param datastore gcloud-java Datastore service object to execute requests
     * @param kind the kind for the Datastore entities in this demo
     */
    public ShardedClickCounterService(Datastore datastore, String kind) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().kind(kind);
        this.kind = kind;
    }

    public void incrementCountCampaign(String campaignId) {
        ShardedClickCounter scc = new ShardedClickCounter(campaignId+"_1", campaignId, 0);

        Key key = keyFactory.newKey(scc.getId());

        Entity entity = Entity.builder(key)
            .set("id", scc.getId())
            .set("campaignId", campaignId)
            .set("count", 0)
            .build();

        try {
            datastore.update(entity);
        } catch (DatastoreException e) {
            if (!e.getMessage().contains("no entity to update")) {
                e.printStackTrace();
            } else {
                datastore.update(entity);
            }
        }
        
    }

    public final long getCount(String campaignId) {
        long sum = 0;

        Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, "SELECT * FROM " + kind+" where campaignId=@cid")
        //Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, "SELECT * FROM " + kind+"")
                        .setBinding("cid", campaignId)
                .build();

        QueryResults<Entity> results = datastore.run(query);
        while (results.hasNext()) {
            Entity result = results.next();
            sum += result.getLong("count");
        }
        
        return sum;
    }


}
