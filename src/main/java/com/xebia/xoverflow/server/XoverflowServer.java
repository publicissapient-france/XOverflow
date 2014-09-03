package com.xebia.xoverflow.server;

import com.xebia.xoverflow.server.exception.BadRequestException;
import com.xebia.xoverflow.server.model.Answer;
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

    private final ObjectMapper objectMapper;

    public static void main(String[] args) {
        ObjectGraph graph = ObjectGraph.create(DaggerModule.class);

        Node node = graph.get(Node.class);
        node.start();

        graph.get(XoverflowServer.class).runServer();
    }

    @Inject
    public XoverflowServer(ObjectMapper mapper, PostRepositoryService repositoryService) {
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
        });

        // Create a post
        put("/post", (request, response) -> {
            Post post = parsePostFromRequest(request);
            post = repositoryService.create(post);

            return postToJson(repositoryService.create(post));
        });

        put("/post/:id/answer", (request, response) -> {
            final Post post = repositoryService.findPost(request.params("id"));
            post.getAnswers().add(parseAnswerFromRequest(request));

            return postToJson(post);
        });

        get("/post/:id", (request, response) -> {
            Post res;
            String id = request.params("id");
            res = repositoryService.findPost(id);
            return postToJson(res);
        });

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

    private Answer parseAnswerFromRequest(Request request) {
        Answer answer;
        try {
            answer = objectMapper.readValue(request.body(), Answer.class);
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
        return answer;
    }

    private String postToJson(Object objectToSerializeInJson) {
        try {
            return objectMapper.writeValueAsString(objectToSerializeInJson);
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

}
