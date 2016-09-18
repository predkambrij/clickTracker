package com.google.appengine.sparkdemo;

import java.nio.charset.Charset;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Common {
    public static Marker notifyAdmin = MarkerFactory.getMarker("NOTIFY_ADMIN");
    public static String hashPassword(String password_plaintext) {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(password_plaintext, salt);
    }
 
    public static boolean checkPassword(String password_plaintext, String stored_hash) {
        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        return BCrypt.checkpw(password_plaintext, stored_hash);
    }

    public static boolean authenticate(ILoggerFactory iLoggerFactory, String authorization, String ip) {
        Logger logger = iLoggerFactory.getLogger(Common.class.getName());
        String extraLogInfo = String.format("%s", ip);
        if (authorization != null && authorization.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);

            if (values.length == 2) {
                if (Config.sadminUsername.equals(values[0]) && Common.checkPassword(values[1], Config.sadminPw)) {
                    logger.info("sadmin auth succeed {}", extraLogInfo);
                    return true;
                }
                logger.info("sadmin auth failed ({}:{}) {}", values[0], values[1], extraLogInfo);
            } else {
                logger.info(notifyAdmin, "sadmin auth failed: invalid format ({}) {}", authorization, extraLogInfo);
            }
        } else {
            logger.info(notifyAdmin, "sadmin auth failed: invalid basic format ({}) {}", authorization, extraLogInfo);
        }
        try {
            // slow down possible brute force
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        return false;
    }

}