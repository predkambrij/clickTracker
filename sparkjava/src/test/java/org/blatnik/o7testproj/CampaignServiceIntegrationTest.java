package org.blatnik.o7testproj;

import static org.junit.Assert.assertTrue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import spark.Spark;
import spark.utils.IOUtils;

public class CampaignServiceIntegrationTest {
    @BeforeClass
    public static void beforeClass() {
        Main.main(CommonTestUtils.mainArgs);
        Spark.awaitInitialization();
    }

    @Before
    public void setUp() throws IOException {
    }

    @After
    public void tearDown() throws IOException {
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    @Test
    public void testCreateUpdateDeleteCampaign() throws IOException {
        String postDataStr = "name=testn&redirectUrl="+URLEncoder.encode("http://example.org/redir1", "UTF-8")
                            +"&platforms=iphone+android";
        Object[] res = CommonTestUtils.executeRequest(1, "POST", "/api/campaign", postDataStr);
        assertEquals(200, Integer.parseInt((String)res[0]));

        Campaign created = new Gson().fromJson((String)res[1], Campaign.class);
        assertEquals("testn", created.getName());
        assertEquals("http://example.org/redir1", created.getRedirectUrl());
        String[] platforms = created.getPlatforms();
        Arrays.sort(platforms);
        assertEquals(2, platforms.length);
        assertEquals("android", platforms[0]);
        assertEquals("iphone", platforms[1]);

        String putDataStr = "name=testupd&redirectUrl="+URLEncoder.encode("http://example.org/redir2", "UTF-8")
            +"&platforms=iphone";
        Object[] resUpd = CommonTestUtils.executeRequest(1, "PUT", "/api/campaign/"+created.getId(), putDataStr);
        assertEquals(200, Integer.parseInt((String)resUpd[0]));
        Campaign updated = new Gson().fromJson((String)resUpd[1], Campaign.class);
        assertEquals("testupd", updated.getName());
        assertEquals("http://example.org/redir2", updated.getRedirectUrl());
        String[] platformsupdated = updated.getPlatforms();
        assertEquals(1, platformsupdated.length);
        assertEquals("iphone", platformsupdated[0]);

        Object[] resDel = CommonTestUtils.executeRequest(1, "DELETE", "/api/campaign/"+created.getId(), null);
        assertEquals(200, Integer.parseInt((String)resDel[0]));
        Response resp = new Gson().fromJson((String)resDel[1], Response.class);
        assertEquals("ok", resp.getMessage());
    }
    @Test
    public void testGetAllCampaigns() throws IOException {
        Object[] res;
        String postDataStr1 = "name=testn1&redirectUrl="+URLEncoder.encode("http://example.org/redir1", "UTF-8")
                +"&platforms=iphone+android";
        res = CommonTestUtils.executeRequest(1, "POST", "/api/campaign", postDataStr1);
        assertEquals(200, Integer.parseInt((String)res[0]));
        String postDataStr2 = "name=testn2&redirectUrl="+URLEncoder.encode("http://example.org/redir1", "UTF-8")
                +"&platforms=iphone";
        res = CommonTestUtils.executeRequest(1, "POST", "/api/campaign", postDataStr2);
        assertEquals(200, Integer.parseInt((String)res[0]));
        String postDataStr3 = "name=testn1&redirectUrl="+URLEncoder.encode("http://example.org/redir1", "UTF-8")
                +"&platforms=android";
        res = CommonTestUtils.executeRequest(1, "POST", "/api/campaign", postDataStr3);
        assertEquals(200, Integer.parseInt((String)res[0]));

        res = CommonTestUtils.executeRequest(1, "GET", "/api/campaign?platform=iphone", null);
        assertEquals(200, Integer.parseInt((String)res[0]));
        assertEquals(2, new Gson().fromJson((String)res[1], Campaign[].class).length);

        res = CommonTestUtils.executeRequest(1, "GET", "/api/campaign?platform=android", null);
        assertEquals(200, Integer.parseInt((String)res[0]));
        assertEquals(2, new Gson().fromJson((String)res[1], Campaign[].class).length);

        res = CommonTestUtils.executeRequest(1, "GET", "/api/campaign", null);
        assertEquals(200, Integer.parseInt((String)res[0]));
        Campaign campaigns[] = new Gson().fromJson((String)res[1], Campaign[].class);
        assertEquals(3, campaigns.length);

        for (int i=0; i<campaigns.length; i++) {
            CommonTestUtils.executeRequest(1, "DELETE", "/api/campaign/"+campaigns[i].getId(), null);
        }

    }

}
