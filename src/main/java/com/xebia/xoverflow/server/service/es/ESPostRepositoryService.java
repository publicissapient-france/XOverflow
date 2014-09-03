package com.xebia.xoverflow.server.service.es;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.PostRepositoryService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Created by arnaud on 03/09/2014.
 */
public class ESPostRepositoryService implements PostRepositoryService {


    public static final String INDEX_PATH = "/posts/post/";

    public ESApiService esApiService;

    @Inject
    public ESPostRepositoryService(ESApiService esApiService) {
        this.esApiService = esApiService;
    }


    @Override
    public Post create(Post post) {
        JsonObject response = esApiService.createPost(post);
        post.setId(response.get("_id").getAsString());
        return post;
    }

    @Override
    public List<Post> listLast10Posts() {

        List<Post> res = new ArrayList<>();
        JsonObject response = esApiService.listPost();

        JsonArray jsonPosts = response.get("hits").getAsJsonObject().get("hits").getAsJsonArray();

        Function<JsonObject, Post> convertJsonToPost = convertJsonToPost();

        Iterator<JsonElement> iterator = jsonPosts.iterator();
        while (iterator.hasNext()) {
            JsonElement jsonPost = iterator.next();
            JsonObject jsonObject = jsonPost.getAsJsonObject();
            Post post = convertJsonToPost.apply(jsonObject.get("_source").getAsJsonObject());
            post.setId(jsonObject.get("_id").getAsString());
            res.add(post);
        }

        return res;
    }

    @Override
    public Post findPost(String id) {
        return null;
    }

    private static Function<JsonObject, Post> convertJsonToPost() {
        return new Function<JsonObject, Post>() {
            @Override
            public Post apply(JsonObject json) {
                Post res = new Post();
                res.setSubject(json.get("subject").getAsString());
                res.setBody(json.get("body").getAsString());
                res.setUserName(json.get("userName").getAsString());

                return res;
            }
        };

    }

}
