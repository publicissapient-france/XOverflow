package com.xebia.xoverflow.server.service.es;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.PostRepositoryService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by arnaud on 03/09/2014.
 */
public class ESPostRepositoryService implements PostRepositoryService {

    public static final String INDEX_PATH = "/posts/post/";

    private ESApiService esApiService;
    private Gson gson;

    @Inject
    public ESPostRepositoryService(ESApiService esApiService, Gson gson) {
        this.esApiService = esApiService;
        this.gson = gson;
    }


    @Override
    public Post create(Post post) {
        post.setDate(new Date());
        JsonObject response = esApiService.createPost(post);
        post.setId(response.get("_id").getAsString());
        return post;
    }

    @Override
    public List<Post> listLast10Posts() {

        List<Post> res = new ArrayList<>();
        JsonObject response = esApiService.listPost(10, "date:desc");

        JsonArray jsonPosts = extractPostsJson(response);

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

    private static JsonArray extractPostsJson(JsonObject response) {
        return response.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
    }

    @Override
    public Post findPost(String id) {
        JsonObject response = esApiService.getPost("_id:" + id);

        JsonArray jsonPosts = extractPostsJson(response);

        Function<JsonObject, Post> convertJsonToPost = convertJsonToPost();
        Post res = null;
        Iterator<JsonElement> iterator = jsonPosts.iterator();
        while (iterator.hasNext()) {
            JsonElement jsonPost = iterator.next();
            JsonObject jsonObject = jsonPost.getAsJsonObject();
            res = convertJsonToPost.apply(jsonObject.get("_source").getAsJsonObject());
            res.setId(jsonObject.get("_id").getAsString());

        }
        return res;
    }

    @Override
    public Post updatePost(Post post) {
        Iterables.getLast(post.getAnswers()).setDate(new Date());
        JsonObject response = esApiService.updatePost(post, post.getId());
        post.setId(response.get("_id").getAsString());
        return post;
    }

    private Function<JsonObject, Post> convertJsonToPost() {
        return new Function<JsonObject, Post>() {
            @Override
            public Post apply(JsonObject json) {
                return gson.fromJson(json, Post.class);
            }
        };

    }

}
