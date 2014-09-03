package com.xebia.xoverflow.server.service.es;

import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.PostRepositoryService;
import retrofit.RestAdapter;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by arnaud on 03/09/2014.
 */
public class ESPostRepositoryService implements PostRepositoryService {


    public static final String INDEX_PATH = "/posts/post/";

    @Inject
    protected RestAdapter restAdapter;


    @Override
    public Post create(Post post) {
        return null;
    }

    @Override public List<Post> listLast10Posts() {
        return null;
    }
}
