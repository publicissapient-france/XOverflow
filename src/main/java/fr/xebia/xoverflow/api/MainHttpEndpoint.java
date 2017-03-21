package fr.xebia.xoverflow.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import fr.xebia.xoverflow.es.EsRepository;
import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import fr.xebia.xoverflow.service.Repository;
import fr.xebia.xoverflow.service.search.Criteria;
import javaslang.control.Option;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang.StringUtils.isBlank;
import static spark.Spark.*;

public class MainHttpEndpoint implements SparkApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainHttpEndpoint.class);

    protected static final String JSON_CONTENT_TYPE = "application/json";

    private final int port;

    private final Repository repository;

    @Inject
    public MainHttpEndpoint(int port, Repository repository) {
        requireNonNull(repository, "repository must be defined.");
        this.port = port;
        this.repository = repository;
    }

    public void start() {
        LOGGER.info("Starting Http server.");
        Spark.port(port);

        init();

        Spark.awaitInitialization();
        LOGGER.info("Http server started on port {}.", port);
    }

    @Override
    public void init() {

        Spark.staticFileLocation("webapp");

        //  User

        get("/user/:id", JSON_CONTENT_TYPE, (request, response) -> getObjectById(request, repository::getUser), new JsonTransformer());

        //  Message

        put("/message", JSON_CONTENT_TYPE, (request, response) -> {
            String body = request.body();
            JsonParser parser = new JsonParser();
            try {
                JsonObject json = (JsonObject) parser.parse(body);

            } catch (JsonParseException e) {
                halt(400, "Body request isn't a valid Json.");
            }
            return "";
        }, new JsonTransformer());

        //  Thread

        post("/thread/search", JSON_CONTENT_TYPE, (request, response) -> searchThread(response), new JsonTransformer());

        get("/thread/search", JSON_CONTENT_TYPE, (request, response) -> {
            response.type(JSON_CONTENT_TYPE);
            String criteria = request.queryParams("c");
            if (isBlank(criteria)) {
                halt(400, "'c' query param must be defined.");
                return "";
            }
            LOGGER.debug("Criteria {}", criteria);
            return repository.searchMessageThread(new Criteria("subject", Criteria.CriteriaOperator.COULD_BE, criteria));
        }, new JsonTransformer());

        get("/thread/:id", JSON_CONTENT_TYPE, (request, response) -> getObjectById(request, repository::getMessageThread), new JsonTransformer());
    }

    private Object searchThread(Response response) {
        String body = response.body();
        JsonParser parser = new JsonParser();
        try {
            JsonObject json = (JsonObject) parser.parse(body);
            if (json.has("criteria")) {
                String criteria = json.getAsJsonPrimitive("criteria").getAsString();
                return repository.searchMessageThread(new Criteria("subject", criteria));
            } else {
                halt(400, "Json body doesn't contain 'criteria' property.");
            }
        } catch (JsonParseException e) {
            halt(400, "Body request isn't a valid Json.");
        }
        return "";
    }

    private Object getObjectById(Request request, GetterCallback callback) {
        String identifier = request.params(":id");
        if (isBlank(identifier)) {
            halt(404);
            return "";
        }
        Option user = callback.getById(identifier);
        if (user.isDefined()) {
            return user.get();
        }
        halt(404);
        return "";
    }

    interface GetterCallback {
        Option getById(String identifier);
    }

    public static void main(String[] args) {
        EsRepository repository = new EsRepository(new OkHttpClient(), "http://localhost:9200");

        MessageThread thread = new MessageThread("react/redux");
        Option<MessageThread> messageThreadAdded = repository.addMessageThread(thread);
        if (messageThreadAdded.isDefined()) {
            User.Builder userBuilder = User.builderUser();
            Message.Builder builder = Message.builder();
            User jpascal = userBuilder
                    .setUsername("jpascal")
                    .setEmail("jpthiery@xebia.fr")
                    .build();
            Message message = builder.setAuthor(jpascal).setContent("Youpi").build() ;
            repository.addMessageToThread(messageThreadAdded.get().getId(), message);
        }

        MainHttpEndpoint endpoint = new MainHttpEndpoint(8080, repository);
        endpoint.start();
    }

}
