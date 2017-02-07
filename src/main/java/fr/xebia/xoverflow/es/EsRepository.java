package fr.xebia.xoverflow.es;

import com.google.gson.*;
import fr.xebia.xoverflow.model.IdBuilder;
import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import fr.xebia.xoverflow.service.Repository;
import javaslang.control.Option;
import javaslang.control.Try;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang.StringUtils.isBlank;

public class EsRepository implements Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsRepository.class);

    private final OkHttpClient httpClient;

    private final String esUrl;

    private final ThreadLocal<Gson> gson = ThreadLocal.withInitial(() -> new GsonBuilder().create());

    public EsRepository(OkHttpClient httpClient, String esUrl) {
        requireNonNull(httpClient, "httpClient must be defined.");
        if (isBlank(esUrl)) {
            throw new IllegalArgumentException("esUrl must be defined.");
        }
        this.httpClient = httpClient;
        this.esUrl = esUrl;
    }

    @Override
    public Option<MessageThread> addMessageThread(MessageThread messageThread) {
        requireNonNull(messageThread, "messageThread must be defined.");
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(JSON_MINETYPE, gson.get().toJson(messageThread));
        Request request = builder.url(esUrl + INDEX_NAME + THREAD)
                .post(requestBody)
                .build();
        Try<HttpResponse> responseTry = executeRequest(request);

        return checkDocInserted(responseTry, MessageThread.builder(messageThread));
    }

    private static RequestBody constructPartialUpdateRequestBody(JsonElement jsonElement) {
        Gson gson = new GsonBuilder().create();
        return RequestBody.create(JSON_MINETYPE, "{ \"doc\" : " + gson.toJson(jsonElement) + " }");
    }

    private static RequestBody constructPartialUpdateRequestBody(String json) {
        return RequestBody.create(JSON_MINETYPE, "{ \"doc\" : " + json + " }");
    }


    @Override
    public boolean addMessageToThread(String threadId, Message message) {
        if (isBlank(threadId)) {
            throw new IllegalArgumentException("threadId must be defined.");
        }
        requireNonNull(message, "message must be defined.");
        Option<MessageThread> messageThreadOption = getMessageThread(threadId);
        if (messageThreadOption.isDefined()) {
            MessageThread messageThread = messageThreadOption.get();
            messageThread.addMessage(message);
            Gson gson = new GsonBuilder().create();
            String messages = gson.toJson(messageThread.getMessages());

            Request.Builder builder = new Request.Builder();
            RequestBody requestBody = constructPartialUpdateRequestBody("{ \"messages\" : " + messages + "}");
            Request request = builder.url(esUrl + INDEX_NAME + THREAD + threadId + UPDATE)
                    .post(requestBody)
                    .build();

            Try<HttpResponse> httpResponseTry = executeRequest(request);

            return httpResponseTry.isSuccess();
        }
        return false;
    }

    @Override
    public Option<MessageThread> getMessageThread(String id) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("id must be defined.");
        }
        return get(id, THREAD, MessageThread.class);
    }

    @Override
    public Option<User> addUser(User user) {
        requireNonNull(user, "user must be defined.");
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(JSON_MINETYPE, gson.get().toJson(user));
        Request request = builder.url(esUrl + INDEX_NAME + USER)
                .post(requestBody)
                .build();
        Try<HttpResponse> responseTry = executeRequest(request);
        return checkDocInserted(responseTry, User.builder(user));
    }

    @Override
    public Option<User> getUser(String id) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("id must be defined.");
        }
        return get(id, USER, User.class);
    }

    protected Try<HttpResponse> executeRequest(Request request) {
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            return Try.success(new HttpResponse(response.code(), response.body().string()));
        } catch (IOException e) {
            LOGGER.error("An error occur while trying to {} on url {}.", request.method(), request.url().toString());
            return Try.failure(e);
        } finally {
            if (response != null) {
                IOUtils.closeQuietly(response.body());
            }
        }
    }

    private <T> Option<T> checkDocInserted(Try<HttpResponse> httpResponse, IdBuilder<T> idBuilder) {
        if (httpResponse.isSuccess()) {
            HttpResponse response = httpResponse.get();
            JsonParser jsonParser = new JsonParser();
            String body = response.getBody();
            JsonObject rootJson = (JsonObject) jsonParser.parse(body);
            if (rootJson.has(ID)) {
                String id = rootJson.getAsJsonPrimitive(ID).getAsString();
                idBuilder.setId(id);

                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = constructPartialUpdateRequestBody("{ \"id\" : \"" + id + "\" }");
                builder.url(esUrl + INDEX_NAME + THREAD + id + UPDATE).post(requestBody);
                Try<HttpResponse> updateResponse = executeRequest(builder.build());
                if (updateResponse.isSuccess() && LOGGER.isTraceEnabled()) {
                    response = updateResponse.get();
                    traceEsResponse("update", "id :" + id, response.getBody());
                }
                return Option.when(updateResponse.isSuccess(), idBuilder.build());
            }
        }
        return Option.none();
    }

    private <T> Option<T> get(String id, String type, Class<T> classe) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("id must be defined.");
        }
        Request.Builder builder = new Request.Builder();
        builder.url(esUrl + INDEX_NAME + type + id).get();
        Try<HttpResponse> httpResponses = executeRequest(builder.build());
        if (httpResponses.isSuccess()) {
            HttpResponse response = httpResponses.get();
            if (response.code == 200) {
                JsonParser parser = new JsonParser();
                JsonObject root = (JsonObject) parser.parse(response.getBody());
                if (root.getAsJsonPrimitive(FOUND).getAsBoolean()) {
                    Gson gson = new GsonBuilder().create();
                    T messageThread = gson.fromJson(root.getAsJsonObject(SOURCE), classe);
                    return Option.of(messageThread);
                }
            }
        }
        return Option.none();

    }

    private static void traceEsResponse(String action, String input, String body) {
        if (LOGGER.isTraceEnabled()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(body);
            LOGGER.debug(action + " a " + input + " with following response from ES:\n{}", gson.toJson(jsonElement));
        }
    }

    protected static class HttpResponse {

        private final int code;

        private final String body;

        protected HttpResponse(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }
    }

    private static final String INDEX_NAME = "/xoverflow";

    private static final String THREAD = "/thread/";

    private static final String USER = "/user/";

    private static final String ID = "_id";

    private static final String SOURCE = "_source";

    private static final String FOUND = "found";

    private static final MediaType JSON_MINETYPE = MediaType.parse("application/json");

    private static final String UPDATE = "/_update";

}
