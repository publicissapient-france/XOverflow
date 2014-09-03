package com.xebia.xoverflow.server.service.es;

import com.google.gson.JsonObject;
import com.xebia.xoverflow.server.model.Post;
import retrofit.http.*;

import java.util.List;

/**
 * Created by arnaud on 03/09/2014.
 */
public interface ESApiService {

    @POST(ESPostRepositoryService.INDEX_PATH)
    public JsonObject createPost(@Body Post post);

    @GET(ESPostRepositoryService.INDEX_PATH + "_search")
    public JsonObject getPost(@Query("q") String query);

    @GET(ESPostRepositoryService.INDEX_PATH + "_search")
    public JsonObject listPost(@Query("size") int size, @Query("sort") String sort);

    
}
