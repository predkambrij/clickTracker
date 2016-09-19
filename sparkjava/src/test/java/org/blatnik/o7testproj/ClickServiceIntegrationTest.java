package org.blatnik.o7testproj;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import spark.Spark;

public class ClickServiceIntegrationTest {
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
    public void testClicks() throws IOException {
        String postDataStr = "name=testn&redirectUrl="+URLEncoder.encode("http://example.org/redir1", "UTF-8")
                            +"&platforms=iphone+android";
        Object[] res = CommonTestUtils.executeRequest(1, "POST", "/api/campaign", postDataStr);
        Campaign created = new Gson().fromJson((String)res[1], Campaign.class);
        assertEquals(200, Integer.parseInt((String)res[0]));

        Object[] resGet1 = CommonTestUtils.executeRequest(1, "GET", "/api/click/"+created.getId(), null);
        assertEquals(200, Integer.parseInt((String)resGet1[0]));
        Response resp1 = new Gson().fromJson((String)resGet1[1], Response.class);
        assertEquals(0+"", resp1.getMessage());

        for (int i=0; i<10; i++) {
            Object[] resDo1 = CommonTestUtils.executeRequest(1, "GET", "/click/"+created.getId(), null);
            assertEquals(302, Integer.parseInt((String)resDo1[0]));
            assertEquals("http://example.org/redir1", (String)resDo1[1]);
        }

        Object[] resGet2 = CommonTestUtils.executeRequest(1, "GET", "/api/click/"+created.getId(), null);
        assertEquals(200, Integer.parseInt((String)resGet2[0]));
        Response resp2 = new Gson().fromJson((String)resGet2[1], Response.class);
        System.out.println(resp2.getMessage());
        assertEquals(10+"", resp2.getMessage());

        Object[] resDel = CommonTestUtils.executeRequest(1, "DELETE", "/api/campaign/"+created.getId(), null);
        assertEquals(200, Integer.parseInt((String)resDel[0]));

        Object[] resDo2 = CommonTestUtils.executeRequest(1, "GET", "/click/invalid", null);
        assertEquals(302, Integer.parseInt((String)resDo2[0]));
        assertEquals("http://outfit7.com", (String)resDo2[1]);
    }
}
