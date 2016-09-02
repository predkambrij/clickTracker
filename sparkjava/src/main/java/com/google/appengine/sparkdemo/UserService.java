package com.google.appengine.sparkdemo;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.*;
import com.google.api.client.json.jackson2.*;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class UserService {

    private final Datastore datastore;
    private final KeyFactory keyFactory;
    private final String kind;

    /**
     * Constructor for UserService.
     *
     * @param datastore gcloud-java Datastore service object to execute requests
     * @param kind the kind for the Datastore entities in this demo
     */
    public UserService(Datastore datastore, String kind) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().kind(kind);
        this.kind = kind;
    }

    /**
     * Return a list of all users.
     */
    public List<User> getAllUsers() {
        Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, "SELECT * FROM " + kind).build();
        QueryResults<Entity> results = datastore.run(query);
        List<User> users = new ArrayList<>();
        while (results.hasNext()) {
            Entity result = results.next();
            users.add(new User(result.getString("id"), result.getString("name"), result.getString("email")));
        }
        return users;
    }

    /**
     * Return the user with the given id.
     */
    User getUser(String id) {
        Entity entity = datastore.get(keyFactory.newKey(id));
        return entity == null ? null : new User(entity.getString("id"), entity.getString("name"), entity.getString("email"));
    }

    /**
     * Create a new user and add it to Cloud Datastore.
     */
    public User createUser(String name, String email) {
        failIfInvalid(name, email);
        User user = new User(name, email);
        Key key = keyFactory.newKey(user.getId());
        Entity entity = Entity.builder(key)
            .set("id", user.getId())
            .set("name", name)
            .set("email", email)
            .build();
        datastore.add(entity);
        return user;
    }

    /**
     * Delete a user from Cloud Datastore.
     */
    public String deleteUser(String id) {
        Key key = keyFactory.newKey(id);
        datastore.delete(key);
        return "ok";
    }

    /**
     * Updates a user in Cloud Datastore.
     */
    public User updateUser(String id, String name, String email) {
        failIfInvalid(name, email);
        Key key = keyFactory.newKey(id);
        Entity entity = datastore.get(key);
        if (entity == null) {
            throw new IllegalArgumentException("No user with id '" + id + "' found");
        } else {
            entity = Entity.builder(entity)
                .set("id", id)
                .set("name", name)
                .set("email", email)
                .build();
            datastore.update(entity);
        }
        return new User(id, name, email);
    }

    private void failIfInvalid(String name, String email) {
        checkArgument(name != null && !name.isEmpty(), "Parameter 'name' cannot be empty");
        checkArgument(email != null && !email.isEmpty(), "Parameter 'email' cannot be empty");
    }

    public void exc() {
        String clientID = "553539452176-ogmj31nnv0sb0f3e18e286qcnm7tgb1m.apps.googleusercontent.com";
        String clientSecret = "DbdeZTZNMmjzdro9d0FcuKyv";
        String redirectURI = "https://o7testproj.appspot.com/oauth2callback";
        String authCode = "";

        GoogleTokenResponse tokenResponse = null;
        try {
            tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://www.googleapis.com/oauth2/v4/token",
                    clientID,
                    clientSecret,
                    authCode,
                    redirectURI
                ).execute();

        } catch (IOException e) {

        }

        String accessToken = tokenResponse.getAccessToken();

        // Use access token to call API
        // GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        // Drive drive =
        //     new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
        //         .setApplicationName("Auth Code Exchange Demo")
        //         .build();
        // File file = drive.files().get("appfolder").execute();

        // Get profile info from ID token
        try {
            GoogleIdToken idToken = tokenResponse.parseIdToken();
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();  // Use this value as a key to identify a user.
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
        } catch (IOException e) {

        }
    }
}
