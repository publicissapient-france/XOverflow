package fr.xebia.xoverflow.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import fr.xebia.xoverflow.api.internal.JsonTransformer;
import fr.xebia.xoverflow.backend.es.EsRepository;
import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import fr.xebia.xoverflow.backend.service.Repository;
import fr.xebia.xoverflow.model.service.Criteria;
import javaslang.control.Option;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.inject.Inject;

import java.util.Optional;

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

        before((Filter) (request, response) -> response.type(JSON_CONTENT_TYPE));   // Always response a Json content.

        //  User

        get("/user/:id", JSON_CONTENT_TYPE, (request, response) -> getObjectById(request, repository::getUser), new JsonTransformer());

        //  Message

        put("/message", JSON_CONTENT_TYPE, (request, response) -> {
            String body = request.body();
            JsonParser parser = new JsonParser();
            try {
                JsonObject json = (JsonObject) parser.parse(body);
                Optional<String> messageThreadIdOpt = readStringFromJson(json, "messageThreadId");
                if (messageThreadIdOpt.isPresent()) {
                    String messageThreadId = messageThreadIdOpt.get();
                    long publishDate = json.getAsJsonPrimitive("publishDate").getAsLong();
                    String content = json.getAsJsonPrimitive("content").getAsString();
                    String authorId = json.getAsJsonPrimitive("authorId").getAsString();
                    repository.getUser(authorId);

                } else {
                    halt(400, "Can not find 'messageThreadId' property.");
                }
            } catch (JsonParseException e) {
                halt(400, "Body request isn't a valid Json.");
            }
            return "";
        }, new JsonTransformer());

        //  Thread

        post("/thread/search", JSON_CONTENT_TYPE, (request, response) -> searchThread(response), new JsonTransformer());

        get("/thread/search", JSON_CONTENT_TYPE, this::searchMessageThread, new JsonTransformer());

        get("/thread/:id", JSON_CONTENT_TYPE, (request, response) -> getObjectById(request, repository::getMessageThread), new JsonTransformer());
    }

    private Object searchMessageThread(Request request, Response response) {
        response.type(JSON_CONTENT_TYPE);
        String criteria = request.queryParams("c");
        if (isBlank(criteria)) {
            halt(400, "'c' query param must be defined.");
            return "";
        }
        LOGGER.debug("Criteria {}", criteria);
        return repository.searchMessageThread(
                new Criteria("subject", Criteria.CriteriaOperator.COULD_BE, criteria),
                new Criteria("author", Criteria.CriteriaOperator.COULD_BE, criteria)
        );
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


    private interface JsonGetter<T> {
        T extract(JsonPrimitive jsonPrimitive);
    }

    private static <T> Optional<T> readFromJson(JsonObject json, String name, JsonGetter<T> mapper) {
        return Optional.ofNullable(json.getAsJsonPrimitive(name)).flatMap(jsonPrimitive -> Optional.of(mapper.extract(jsonPrimitive)));

    }

    protected static Optional<String> readStringFromJson(JsonObject json, String name) {
        requireNonNull(json, "json must be defined.");
        if (isBlank(name)) {
            throw new IllegalArgumentException("name must be defined.");
        }
        return readFromJson(json, name, JsonPrimitive::getAsString);
    }

    protected static Optional<Integer> readIntFromJson(JsonObject json, String name) {
        requireNonNull(json, "json must be defined.");
        if (isBlank(name)) {
            throw new IllegalArgumentException("name must be defined.");
        }
        return readFromJson(json, name, JsonPrimitive::getAsInt);
    }

    protected static Optional<Long> readLongFromJson(JsonObject json, String name) {
        requireNonNull(json, "json must be defined.");
        if (isBlank(name)) {
            throw new IllegalArgumentException("name must be defined.");
        }
        return readFromJson(json, name, JsonPrimitive::getAsLong);
    }

    protected static Optional<Boolean> readBooleanFromJson(JsonObject json, String name) {
        requireNonNull(json, "json must be defined.");
        if (isBlank(name)) {
            throw new IllegalArgumentException("name must be defined.");
        }
        return readFromJson(json, name, JsonPrimitive::getAsBoolean);
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
