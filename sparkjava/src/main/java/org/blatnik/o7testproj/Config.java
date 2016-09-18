package org.blatnik.o7testproj;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Config {
    public static Set<String> allowedPlatforms = new HashSet<String>(Arrays.asList("android", "iphone"));
    public static String datastoreCampaignKind = "campaign";
    public static String datastoreClickKind = "click";
    public static String datastoreShardedClickKind = "shardedClickCounter";

    // sudo apt-get install python-bcrypt; python -c "import bcrypt; print bcrypt.hashpw('sadminpw', bcrypt.gensalt())"
    public static String sadminUsername = "sadmin";
    public static String sadminPw = "$2a$12$nJWGNGXgOFEp1F/hkeV75OZyHcjFUYHQKZ1TaYAeZbkn6hXwrL9qG"; // sadminpw
}