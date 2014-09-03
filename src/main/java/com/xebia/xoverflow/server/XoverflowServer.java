package com.xebia.xoverflow.server;

import com.xebia.xoverflow.server.exception.BadRequestException;
import com.xebia.xoverflow.server.model.Answer;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.DaggerModule;
import com.xebia.xoverflow.server.service.MailPollerService;
import com.xebia.xoverflow.server.service.PollerTask;
import com.xebia.xoverflow.server.service.PostRepositoryService;
import dagger.ObjectGraph;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.node.Node;
import spark.Request;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static spark.Spark.get;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

public class XoverflowServer {

    private final PostRepositoryService repositoryService;

    private final ObjectMapper objectMapper;

    private final MailPollerService mailPollerService;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ObjectGraph graph = ObjectGraph.create(DaggerModule.class);

        Node node = graph.get(Node.class);
        node.start();

        graph.get(XoverflowServer.class).runServer();
    }

    @Inject
    public XoverflowServer(ObjectMapper mapper, PostRepositoryService repositoryService, MailPollerService mailPollerService) {
        this.objectMapper = mapper;
        this.repositoryService = repositoryService;
        this.mailPollerService = mailPollerService;
    }

    public void runServer() throws ExecutionException, InterruptedException {
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
            return postToJson(repositoryService.create(post));
        });

        put("/post/:id/answer", (request, response) -> {
            final String questionId = request.params("id");
            final Post post = repositoryService.findPost(questionId);
            final Answer newAnswer = parseAnswerFromRequest(request);
            newAnswer.setQuestionId(questionId);
            post.getAnswers().add(newAnswer);
            final Post postAnswer = repositoryService.updatePost(post);
            return postToJson(postAnswer);
        });

        get("/post/:id", (request, response) -> {
            Post res;
            String id = request.params("id");
            res = repositoryService.findPost(id);
            return postToJson(res);
        });

        ExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.submit(new PollerTask(mailPollerService));


        get("/search/:query", (request, response) -> {
            List<Post> res;
            String queryString = request.params("query");
            res = repositoryService.searchPosts(queryString);
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
