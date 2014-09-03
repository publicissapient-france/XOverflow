package com.xebia.xoverflow.server;

import com.xebia.xoverflow.server.exception.BadRequestException;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.PostRepositoryService;
import org.codehaus.jackson.map.ObjectMapper;
import spark.Request;

import java.io.IOException;

import static spark.Spark.*;

public class XoverflowServer {


    private final PostRepositoryService repositoryService;

    public static void main(String[] args) {
            new XoverflowServer().runServer();
    }


    private final ObjectMapper objectMapper;

    public XoverflowServer(){
        objectMapper = new ObjectMapper();
        repositoryService = new PostRepositoryService() {};

    }


    public void runServer (){
        staticFileLocation("/");


        put("/posts", (request, response) ->  {
            Post post = parsePostFromRequest(request);

            return "";
        });

        get("/hello",(request, response) -> "Hello World!");

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

}
