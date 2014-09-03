package com.xebia.xoverflow.server;

import com.xebia.xoverflow.server.exception.BadRequestException;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.DaggerModule;
import com.xebia.xoverflow.server.service.PostRepositoryService;
import dagger.ObjectGraph;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.node.Node;
import spark.Request;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static spark.Spark.*;

public class XoverflowServer {


    private final PostRepositoryService repositoryService;

    public static void main(String[] args) {
        ObjectGraph graph = ObjectGraph.create(DaggerModule.class);

        Node node = graph.get(Node.class);
        node.start();


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

        // Get All posts
        get("/posts", (request, response) -> {

            List<Post> posts = repositoryService.listLast10Posts();

             //posts = stubdedList();
            return postToJson(posts);
        } );

        // Create a post
        put("/post", (request, response) -> {
            Post post = parsePostFromRequest(request);

            post = repositoryService.create(post);

            post = new Post();
            post.setSubject("Qui a gagné le Hackathon ?");
            post.setBody("Je voudrais connaitre l'identité du gagnant du Hackathon :)");
            post.setDate(new Date());
            post.setUserName("rbung");
            post.setAnswers(new ArrayList<>());
            post.setId("azertyuytrez");
            return post;
          //  return postToJson(post);
        });

        get("/hello", (request, response) -> "Hello World!");

    }

    private List<Post> stubdedList() {
        List<Post> res = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Post post = new Post();
            post.setSubject("Qui a gagné le Hackathon ?");
            post.setBody("Je voudrais connaitre l'identité du gagnant du Hackathon :)");
            post.setDate(new Date());
            post.setUserName("rbung");
            post.setAnswers(new ArrayList<>());
            post.setId("azertyuytrez"+i);
            res.add(post);
        }
        return res;
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

    private String postToJson(Object objectToSerializeInJson) {
        try {
            return objectMapper.writeValueAsString(objectToSerializeInJson);
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

}
