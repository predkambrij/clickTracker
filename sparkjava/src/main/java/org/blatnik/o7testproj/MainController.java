package org.blatnik.o7testproj;

import static spark.Spark.before;
import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.halt;

import java.util.HashMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.google.gson.Gson;

import spark.Spark;

public class MainController {

    /**
     * Creates a controller that maps requests to gcloud-java functions.
     */
    public MainController(final CampaignService campaignService, final ClickService clickService) {
        post(
            "/api/campaign",
            (req, res) ->  {
                Campaign campaign = campaignService.createCampaign(
                    req.queryParams("name"),
                    req.queryParams("redirectUrl"),
                    req.queryParams("platforms").split(" ")
                );
                return campaign;
            },
            MainController::toJson
        );

        get(
            "/api/campaign",
            (req, res) -> {
                return campaignService.getAllCampaigns(req.queryParams("platform"));
            },
            MainController::toJson
        );

        get(
            "/api/campaign/:id",
            (req, res) -> {
                return campaignService.getCampaign(req.params(":id"));
            },
            MainController::toJson
        );

        put(
            "/api/campaign/:id",
            (req, res) -> {
                String[] platforms = null;
                Campaign result = campaignService.updateCampaign(
                    req.params(":id"),
                    req.queryParams("name"),
                    req.queryParams("redirectUrl"),
                    (req.queryParams("platforms") == null)?null:req.queryParams("platforms").split(" ")
                );
                return result;
            },
            MainController::toJson
        );

        delete(
            "/api/campaign/:id",
            (req, res) -> {
                Response result = campaignService.deleteCampaign(req.params(":id"));
                return result;
                
            },
            MainController::toJson
        );

        get(
            "/api/click/:id",
            (req, res) -> {
                return clickService.clickAnalytics(req.params(":id"));
            }
        );

        get(
            "/click/:id",
            (req, res) -> {
                res.redirect(clickService.addClick(req.params(":id"), campaignService));
                return res;
            }
        );

        before(
            (req, res) -> {
                if (req.pathInfo().equals("/api/campaign")
                        || req.pathInfo().startsWith("/api/campaign/")
                        || req.pathInfo().startsWith("/api/click/")) {
                    if (!Common.authenticate(req.headers("Authorization"), req.ip())) {
                        halt(401);
                    }
                }
            }
        );

        after(
            (req, res) -> {
                res.type("application/json");
            }
        );

        exception(
            IllegalArgumentException.class,
            (error, req, res) -> {
                res.status(400);
                res.body(toJson(new ResponseError(error)));
            }
        );

        exception(
            Exception.class,
            (error, req, res) -> {
                error.printStackTrace();
                res.status(500);
                res.body(toJson(new ResponseError("Internal error")));
            }
        );
    }

    private static String toJson(Object object) {
        return new Gson().toJson(object);
    }
}
