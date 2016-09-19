package org.blatnik.o7testproj;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.common.collect.Iterators;

public class CampaignServiceFunctionalTest {
    private static final LocalDatastoreHelper HELPER = LocalDatastoreHelper.create(1.0);
    private static final DatastoreOptions DATASTORE_OPTIONS = HELPER.options();
    private static final Datastore DATASTORE = DATASTORE_OPTIONS.service();
    private static final String KIND = "campaignFuncTest";
    private static final CampaignService CAMPAIGN_SERVICE = new CampaignService(DATASTORE, KIND);

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        HELPER.start();
    }

    @Before
    public void setUp() {
        Query<Entity> query = null;
        query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, "SELECT * FROM " + KIND).build();
        QueryResults<Entity> results = DATASTORE.run(query);

        KeyFactory keyFactory = DATASTORE.newKeyFactory().kind(KIND);
        while (results.hasNext()) {
            Entity result = results.next();
            DATASTORE.delete(keyFactory.newKey(result.getString("id")));
        }
    }
    @After
    public void tearDown() {
    }

    @AfterClass
    public static void afterClass() throws IOException, InterruptedException {
        HELPER.stop();
    }
    private void compareTwoCampaigns(Campaign first, Campaign second) {
        assertEquals(first.getId(), second.getId());
        assertEquals(first.getName(), second.getName());
        assertEquals(first.getRedirectUrl(), second.getRedirectUrl());

        assertEquals(first.getPlatforms().length, second.getPlatforms().length);
        
        String[] platforms = first.getPlatforms();
        String[] curPlatforms = second.getPlatforms();
        Arrays.sort(platforms);
        Arrays.sort(curPlatforms);
        for(int i=0; i<platforms.length; i++) {
            assertEquals(platforms[i], curPlatforms[i]);
        }
    }
    private void compareCampaigns(Campaign[] list, Campaign second) {
        Campaign first = list[matchCampaign(second.getName())];
        compareTwoCampaigns(first, second);
    }
    private int matchCampaign(String c) {
        switch (c) {
        case "campaign1":
            return 0;
        case "campaign2":
            return 1;
        case "campaign3":
            return 2;
        default:
            return -1;
        }
    }
    @Test
    public void testCreateCampaign() {
        Campaign[] c = new Campaign[]{
            CAMPAIGN_SERVICE.createCampaign("campaign1", "http://example.org/redir1", new String[]{"iphone", "android"}),
            CAMPAIGN_SERVICE.createCampaign("campaign2", "http://example.org/redir2", new String[]{"iphone"}),
            CAMPAIGN_SERVICE.createCampaign("campaign3", "http://example.org/redir3", new String[]{"android"})
        };
        
        List<Campaign> allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns(null);
        assertEquals(3, allCampaigns.size());
        compareCampaigns(c, allCampaigns.get(0));
        compareCampaigns(c, allCampaigns.get(1));
        compareCampaigns(c, allCampaigns.get(2));
    }
    @Test
    public void testGetAllCampaigns() {
        Campaign[] c = new Campaign[]{
            CAMPAIGN_SERVICE.createCampaign("campaign1", "http://example.org/redir1", new String[]{"iphone", "android"}),
            CAMPAIGN_SERVICE.createCampaign("campaign2", "http://example.org/redir2", new String[]{"iphone"}),
            CAMPAIGN_SERVICE.createCampaign("campaign3", "http://example.org/redir3", new String[]{"android"})
        };
        List<Campaign> allCampaigns;
        allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns("iphone");
        assertEquals(2, allCampaigns.size());
        compareCampaigns(c, allCampaigns.get(0));
        compareCampaigns(c, allCampaigns.get(1));
        allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns("android");
        assertEquals(2, allCampaigns.size());
        compareCampaigns(c, allCampaigns.get(0));
        compareCampaigns(c, allCampaigns.get(1));
        boolean executed = false;
        try {
            CAMPAIGN_SERVICE.getAllCampaigns("invalidplatform");
            executed = true;
        } catch(IllegalArgumentException e) {
            assertEquals("Invalid platform", e.getMessage());
        }
        assertEquals(false, executed);
    }
    @Test
    public void testDeleteCampaigns() throws Exception {
        Campaign[] c = new Campaign[]{
            CAMPAIGN_SERVICE.createCampaign("campaign1", "http://example.org/redir1", new String[]{"iphone", "android"}),
            CAMPAIGN_SERVICE.createCampaign("campaign2", "http://example.org/redir2", new String[]{"iphone"}),
            CAMPAIGN_SERVICE.createCampaign("campaign3", "http://example.org/redir3", new String[]{"android"})
        };
        List<Campaign> allCampaigns;
        allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns(null);
        assertEquals(3, allCampaigns.size());
        assertEquals("ok", CAMPAIGN_SERVICE.deleteCampaign(allCampaigns.get(0).getId()).getMessage());
        allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns(null);
        assertEquals(2, allCampaigns.size());
        assertEquals("ok", CAMPAIGN_SERVICE.deleteCampaign(allCampaigns.get(0).getId()).getMessage());
        allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns(null);
        assertEquals(1, allCampaigns.size());
        assertEquals("ok", CAMPAIGN_SERVICE.deleteCampaign(allCampaigns.get(0).getId()).getMessage());
        allCampaigns = CAMPAIGN_SERVICE.getAllCampaigns(null);
        assertEquals(0, allCampaigns.size());
        assertEquals("ok", CAMPAIGN_SERVICE.deleteCampaign("invalid").getMessage());
    }
    @Test
    public void testUpdateCampaign() {
        Campaign c = CAMPAIGN_SERVICE.createCampaign("campaign1", "http://example.org/redir1", new String[]{"iphone", "android"});
        Campaign received = CAMPAIGN_SERVICE.getCampaign(c.getId());
        assertEquals("campaign1", c.getName());
        assertEquals("campaign1", received.getName());
        Campaign upd = CAMPAIGN_SERVICE.updateCampaign(c.getId(), "campaignUpd", "http://example.org/redirUpd", new String[]{"iphone"});
        Campaign received2 = CAMPAIGN_SERVICE.getCampaign(c.getId());
        compareTwoCampaigns(received2, upd);
    }
}
