package fr.xebia.xoverflow.backend.es;

import com.google.gson.*;
import fr.xebia.xoverflow.model.IdBuilder;
import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import fr.xebia.xoverflow.backend.service.Repository;

import fr.xebia.xoverflow.model.service.Criteria;
import javaslang.control.Option;
import javaslang.control.Try;

import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

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
        createIndex();
    }

    private void createIndex() {
        String mapping = "{\n" +
                "  \"mappings\": {\n" +
                "    \"thread\": {\n" +
                "      \"properties\": {\n" +
                "        \"messages\": {\n" +
                "          \"type\": \"nested\" \n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = RequestBody.create( JSON_MINETYPE, mapping);
        Request request = builder.url(esUrl + INDEX_NAME )
                .put(requestBody)
                .build();
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


    @Override
    public List<MessageThread> searchMessageThread(Criteria... criterion) {
        requireNonNull(criterion, "criterion must be defined.");
        return search(MessageThread.class, THREAD, criterion).getOrElse(new ArrayList<>());
    }

    protected String generateQuery(List<Criteria> criterion) {
        assert criterion != null : "criterion must be defined";

        StringBuilder res = new StringBuilder("{\"query\": { \"bool\": { ");
        StringBuilder must = new StringBuilder("\"must\": [");
        StringBuilder should = new StringBuilder("\"should\": [");
        StringBuilder mustNot = new StringBuilder("\"must_not\": [");
        boolean isFirstMustCriteria = true;
        boolean isFirstShouldCriteria = true;
        boolean isFirstMustNotCriteria = true;
        for (Criteria criteria : criterion) {
            StringBuilder sb = should;
            if (isFirstMustCriteria) {
                isFirstMustCriteria = false;
            } else {
                must.append(",");
            }
            if (isFirstShouldCriteria) {
                isFirstShouldCriteria = false;
            } else {
                should.append(",");
            }
            if (isFirstMustNotCriteria) {
                isFirstMustNotCriteria = false;
            } else {
                mustNot.append(",");
            }
            switch (criteria.getOperator()) {
                case MATCH:
                case COULD_BE:
                    sb = should;
                    break;
                case MUST_BE:
                    sb = must;
                    break;
                case EXCLUDE:
                    sb = mustNot;
                    break;
            }
            sb.append("{ \"match\": { \"").append(criteria.getField()).append("\": \"").append(criteria.getValue()).append("\" }}");
        }
        must.append("]");
        mustNot.append("]");
        should.append("]");
        res.append(must.toString()).append(",");
        res.append(mustNot.toString()).append(",");
        res.append(should.toString());
        res.append("}}}");

        return res.toString();
    }

    protected String computeUrl(String type, String action) {
        String url = endWithSeparator(esUrl);
        StringBuilder sb = new StringBuilder(url)
                .append(INDEX_NAME.substring(1))
                .append(endWithSeparator(type));

        if (isNotBlank(action)) {
            sb.append(action);
        }
        //return sb.toString();

        return esUrl + INDEX_NAME + type + action;
    }

    protected String computeUrl(String type) {
        return computeUrl(type, null);
    }

    private String endWithSeparator(String str) {
        if (!str.endsWith("/")) {
            str = str + SPERATOR;
        }
        return str;
    }

    protected <T> Option<List<T>> search(Class<T> classe, String esType, Criteria... criterionArray) {
        requireNonNull(criterionArray, "criterion must be defined.");
        List<Criteria> criterion = Arrays.asList(criterionArray);
        JsonParser parser = new JsonParser();

        Request.Builder builder = new Request.Builder();
        String url = computeUrl(esType, SEARCH_ACTION);
        String query = generateQuery(criterion);
        if (LOGGER.isDebugEnabled()) {
            JsonElement jsonEl = parser.parse(query);
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String json = prettyGson.toJson(jsonEl);
            LOGGER.debug("Sending following query on url '{}':\n{}", url, json);
        }
        Request request = builder.url(url)
                .post(RequestBody.create(JSON_MINETYPE, query))
                .build();
        Try<HttpResponse> httpResponses = executeRequest(request);
        Try<List<T>> res = httpResponses.map(httpResponse -> {
            String body = httpResponse.getBody();

            JsonObject json = (JsonObject) parser.parse(body);

            JsonArray hits = json.getAsJsonObject(HITS_ATTRIBUTES).getAsJsonArray(HITS_ATTRIBUTES);
            Gson gson = this.gson.get();
            List<T> dtos = new ArrayList<>();
            for (JsonElement el : hits) {
                JsonObject element = (JsonObject) el;
                T dto = gson.fromJson(element.getAsJsonObject(SOURCE_ATTRIBUTE), classe);
                dtos.add(dto);
            }
            return dtos;
        });
        return res.getOption();

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

        public HttpResponse(int code, String body) {
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

    protected static final String SOURCE_ATTRIBUTE = "_source";

    protected static final String HITS_ATTRIBUTES = "hits";

    protected static final String SEARCH_ACTION = "_search";

    protected static final char SPERATOR = '/';

}
