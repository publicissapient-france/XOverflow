package com.xebia.xoverflow.server;

import com.xebia.xoverflow.server.exception.BadRequestException;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.DaggerModule;
import com.xebia.xoverflow.server.service.PostRepositoryService;
import dagger.ObjectGraph;
import org.codehaus.jackson.map.ObjectMapper;
import spark.Request;

import javax.inject.Inject;
import java.io.IOException;

import static spark.Spark.*;

public class XoverflowServer {


    private final PostRepositoryService repositoryService;

    public static void main(String[] args) {
        ObjectGraph graph = ObjectGraph.create(DaggerModule.class);

        XoverflowServer xoverflowServer = graph.get(XoverflowServer.class);

        xoverflowServer.runServer();
    }


    private final ObjectMapper objectMapper;

    @Inject
    public XoverflowServer(ObjectMapper mapper, PostRepositoryService repositoryService ) {
        this.objectMapper = mapper;
        this.repositoryService = repositoryService;
    }


    public void runServer() {
        staticFileLocation("/");


        put("/post", (request, response) -> {
            Post post = parsePostFromRequest(request);

            post = repositoryService.create(post);

            return postToJson(post);
        });

        get("/hello", (request, response) -> "Hello World!");

    }


    private Post parsePostFromRequest(Request request) {
        Post post;
        try {
            post = objectMapper.readValue(request.body(), Post.class);
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
        return post;
    }

    private String postToJson(Post post) {
        try {
            return objectMapper.writeValueAsString(post);
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

}
