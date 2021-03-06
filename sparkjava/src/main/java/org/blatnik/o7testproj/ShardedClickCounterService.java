package org.blatnik.o7testproj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;

public class ShardedClickCounterService {

    private final Datastore datastore;
    private final KeyFactory keyFactory;
    private final String kind;
    private static final Logger logger = LoggerFactory.getILoggerFactory().getLogger(ShardedClickCounterService.class.getName());

    /**
     * Default number of shards.
     */
    private static final int NUM_SHARDS = 200;

    /**
     * Default number of retries.
     */
    private static final int NUM_RETRYIES = 5;

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
        for (int retry = 0; retry < NUM_RETRYIES; retry++) {
            int shardNum = new Random().nextInt(NUM_SHARDS)+1;
            String keyStr = campaignId+"_"+shardNum;
            Key key = keyFactory.newKey(keyStr);
            Transaction transaction = datastore.newTransaction();
            try {
                Entity entity = transaction.get(key);
                if (entity == null) {
                    // add shared counter entry
                    ShardedClickCounter scc = new ShardedClickCounter(keyStr, campaignId, 1);
                    Entity newEntity = Entity.builder(key)
                        .set("id", scc.getId())
                        .set("campaignId", campaignId)
                        .set("count", scc.getCount())
                        .build();
                    transaction.add(newEntity);
                    try {
                        transaction.commit();
                        return;
                    } catch (DatastoreException e) {
                        if (e.code() == 3) {
                            // concurrency problem (transaction closed)
                            logger.info(Config.strings.get("createFailed"), retry, NUM_RETRYIES);
                        } else {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // increment shared counter entry
                    Entity updated = Entity.builder(entity)
                            .set("count", entity.getLong("count")+1)
                            .build();
                    transaction.update(updated);
                    try {
                        transaction.commit();
                        return;
                    } catch (DatastoreException e) {
                        if (e.code() == 3) {
                            // concurrency problem (transaction closed)
                            logger.info(Config.strings.get("retryFailed"), retry, NUM_RETRYIES);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                if (transaction.active()) {
                    transaction.rollback();
                }
            }
        }
    }

    public final long getCount(String campaignId) {
        String sql = "SELECT * FROM " + kind+ " where campaignId=@cid";
        Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, sql)
            .setBinding("cid", campaignId).build();

        QueryResults<Entity> results = datastore.run(query);
        long sum = 0;
        while (results.hasNext()) {
            Entity result = results.next();
            sum += result.getLong("count");
        }

        return sum;
    }
}
