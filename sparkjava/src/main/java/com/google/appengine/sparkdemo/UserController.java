package com.google.appengine.sparkdemo;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.halt;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.google.gson.Gson;

import spark.Spark;

public class UserController {
    Logger slf4jLogger;
    ILoggerFactory iLoggerFactory;

    /**
     * Creates a controller that maps requests to gcloud-java functions.
     */
    public UserController(ILoggerFactory iLoggerFactory, final UserService userService, final CampaignService campaignService) {
        this.slf4jLogger = iLoggerFactory.getLogger(UserController.class.getName());
        this.iLoggerFactory = iLoggerFactory;

        Spark.staticFileLocation("/public");

        get( // TODO
            "/oauth2callback",
            (req, res) -> {
                System.out.println(req.queryParams());
                System.out.println(req.queryParams("abc"));
                System.out.println(req.queryParams("ghi"));
                //userService.exc();
                return "ok";
            },
            UserController::toJson
        );

        post(
            "/api/campaign",
            (req, res) ->  {
                if (!Common.authenticate(this.iLoggerFactory, req.headers("Authorization"), req.ip())) {
                    halt(401);
                    return "";
                }

                Campaign campaign = campaignService.createCampaign(
                    req.queryParams("name"),
                    req.queryParams("redirectUrl"),
                    req.queryParams("platforms").split(" ")
                );
                return campaign;
            },
            UserController::toJson
        );
        get(
            "/api/campaign",
            (req, res) -> {
                if (!Common.authenticate(this.iLoggerFactory, req.headers("Authorization"), req.ip())) {
                    halt(401);
                    return "";
                }
                return campaignService.getAllCampaigns();
            },
            UserController::toJson
        );
        get(
            "/api/campaign/:id",
            (req, res) -> {
                if (!Common.authenticate(this.iLoggerFactory, req.headers("Authorization"), req.ip())) {
                    halt(401);
                    return "";
                }
                return campaignService.getCampaign(req.params(":id"));
            },
            UserController::toJson
        );

        put(
            "/api/campaign/:id",
            (req, res) -> {
                if (!Common.authenticate(this.iLoggerFactory, req.headers("Authorization"), req.ip())) {
                    halt(401);
                    return "";
                }
                return campaignService.updateCampaign(
                    req.params(":id"),
                    req.queryParams("name"),
                    req.queryParams("redirectUrl"),
                    req.queryParams("platforms").split(" ")
                );
            },
            UserController::toJson
        );

        delete(
            "/api/campaign/:id",
            (req, res) -> {
                if (!Common.authenticate(this.iLoggerFactory, req.headers("Authorization"), req.ip())) {
                    halt(401);
                    return "";
                }
                return campaignService.deleteCampaign(req.params(":id"));
            },
            UserController::toJson
        );

        get(
            "/api/users",
            (req, res) -> userService.getAllUsers(),
            UserController::toJson
        );

        get(
            "/api/users/:id",
            (req, res) -> userService.getUser(req.params(":id")),
            UserController::toJson
        );

        post(
            "/api/users",
            (req, res) -> userService.createUser(req.queryParams("name"), req.queryParams("email")),
            UserController::toJson
        );

        put(
            "/api/users/:id",
            (req, res) -> userService.updateUser(req.params(":id"), req.queryParams("name"), req.queryParams("email")),
            UserController::toJson
        );

        delete(
            "/api/users/:id",
            (req, res) -> userService.deleteUser(req.params(":id")),
            UserController::toJson
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
    }

    private static String toJson(Object object) {
        return new Gson().toJson(object);
    }
}
