package com.xebia.xoverflow.server.service.es;

import com.google.gson.JsonObject;
import com.xebia.xoverflow.server.model.Post;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.List;

/**
 * Created by arnaud on 03/09/2014.
 */
public interface ESApiService {

    @POST(ESPostRepositoryService.INDEX_PATH)
    public JsonObject createPost(@Body Post post);

    @GET(ESPostRepositoryService.INDEX_PATH + "_search?q=id:{id}")
    public JsonObject getPost(@Path("id") String id);

    @GET(ESPostRepositoryService.INDEX_PATH + "_search")
    public JsonObject listPost();

    
}
