package org.blatnik.o7testproj;

import static spark.Spark.port;
import static spark.Spark.threadPool;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

public class Main {
    public static void main(String[] args) {
        final int maxThreads = 50;
        final int minThreads = 10;
        final int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        port(8080);
        String campaignKind = Config.datastoreCampaignKind;
        String clickKind = Config.datastoreClickKind;
        String shardedClickKind = Config.datastoreShardedClickKind;
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("campaignKind=")) {
                    campaignKind = arg.substring("campaignKind=".length());
                }
            }
        }

        Datastore datastore = DatastoreOptions.defaultInstance().service();
        new MainController(
            new CampaignService(datastore, campaignKind),
            new ClickService(datastore, clickKind, new ShardedClickCounterService(datastore, shardedClickKind))
        );
    }
}
