package com.google.appengine.sparkdemo;

import static spark.Spark.port;
import static spark.Spark.threadPool;

import org.slf4j.LoggerFactory;

import com.google.cloud.datastore.DatastoreOptions;

public class Main {
    public static void main(String[] args) {
        final int maxThreads = 8;
        final int minThreads = 2;
        final int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        port(8080);
        String kind = Config.datastoreKind;
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("kind=")) {
                    kind = arg.substring("kind=".length());
                }
            }
        }

        UserController userController = new UserController(
                LoggerFactory.getILoggerFactory(),
            new UserService(DatastoreOptions.defaultInstance().service(), kind)
        );
    }
}
