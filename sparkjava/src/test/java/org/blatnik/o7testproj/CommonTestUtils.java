package org.blatnik.o7testproj;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import spark.utils.IOUtils;

public class CommonTestUtils {
    public static String[] mainArgs = new String[] {"campaignkind=testCampaign", "shardedclickkind=testShardedClickCounter"};
    public static Object[] executeRequest(int auth, String method, String path, String postDataStr) throws IOException {
        URL url = new URL("http://localhost:8080" + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (auth == 1) {
            connection.setRequestProperty("Authorization", "Basic c2FkbWluOnNhZG1pbnB3");
        } else if (auth == 0) {
            // no auth
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("charset", "utf-8");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setDoOutput(true);
        if (postDataStr != null) {
            byte[] postData = postDataStr.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }
        }
        connection.connect();
        int code = connection.getResponseCode();
        
        String resp = "";
        if (200 <= code && code <= 299) {
            resp = IOUtils.toString(connection.getInputStream());
        } else if (300 <= code && code <= 399) {
            resp = connection.getHeaderField("Location");
        } else {
            resp = IOUtils.toString(connection.getErrorStream());
        } 
        return new Object[]{code+"", resp};
    }

}
