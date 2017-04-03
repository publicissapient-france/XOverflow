package fr.xebia.xoverflow.backend.test.es;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.xebia.xoverflow.backend.es.EsRepository;
import fr.xebia.xoverflow.backend.test.DataBuilder;
import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import fr.xebia.xoverflow.model.UserLight;
import javaslang.control.Option;
import javaslang.control.Try;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Ignore
public class EsRepositoryTest implements DataBuilder {

    private OkHttpClient hpptclient;

    @Before
    public void setup() {
        hpptclient = mock(OkHttpClient.class);
    }

    @Test
    public void add_one_thread() {
        MessageThread messageThread = aMessageThread();
        EsRepository esRepository = new EsRepository(hpptclient, "http://localhost:9002") {
            @Override
            protected Try<EsRepository.HttpResponse> executeRequest(Request request) {
                if (request.method().equals("POST")) {
                    assertThat(request.url().encodedPath()).isEqualTo("/xoverflow/thread/");
                    return Try.success(new EsRepository.HttpResponse(200, "{\"_id\":\"1234\"}"));
                } else if (request.method().equals("GET")) {
                    Gson gson = new GsonBuilder().create();
                    String body = "{\n" +
                            "  \"_index\" :   \"xoverflow\",\n" +
                            "  \"_type\" :    \"thread\",\n" +
                            "  \"_id\" :      \"1234\",\n" +
                            "  \"_version\" : 1,\n" +
                            "  \"found\" :    true,\n" +
                            "  \"_source\" :" + gson.toJson(messageThread) + "\n" +
                            "}";
                    return Try.success(new EsRepository.HttpResponse(200, body));
                }
                return Try.success(null);
            }
        };


        esRepository.addMessageThread(messageThread);

        Option<MessageThread> result = esRepository.getMessageThread(messageThread.getId());

        assertThat(result).isNotNull();
        assertThat(result.isDefined()).isTrue();
        assertThat(result.get().getId()).isEqualTo(messageThread.getId());

    }

    public static void main(String[] args) {
        EsRepository esRepository = new EsRepository(new OkHttpClient(), "http://localhost:9200");
        EsRepositoryTest t = new EsRepositoryTest();
        MessageThread messageThread = t.aMessageThread();
        UserLight userLight = t.aUser();
        User userToAdd = new User(null, userLight.getUsername(), "toto@xoverflow.xebia.fr", null);
        Option<User> userOption = esRepository.addUser(userToAdd);
        if (userOption.isDefined()) {

            User user = userOption.get();
            List<Message> messages = messageThread.getMessages().stream().map(m -> {
                Message.Builder builder = Message.builder(m);
                builder.setAuthor(user);
                return builder.build();
            }).collect(Collectors.toList());
            MessageThread.Builder builder1 = MessageThread.builder(messageThread);
            builder1.setMessages(messages);

            Option<MessageThread> response = esRepository.addMessageThread(builder1.build());
            if (response.isDefined()) {
                String id = response.get().getId();
                MessageThread.Builder builder = MessageThread.builder(messageThread);
                builder.setId(id);
                MessageThread build = builder.build();
                System.out.println("Index MessageThread under id " + id);
                Option<MessageThread> messageThreadResult = esRepository.getMessageThread(id);
                System.out.println(messageThreadResult);

                Message.Builder messageBuilder = Message.builder(t.aMessage());
                messageBuilder.setContent("Second message content.");
                boolean messageAdded = esRepository.addMessageToThread(id, messageBuilder.build());
                System.out.println(messageAdded);

                messageThreadResult = esRepository.getMessageThread(id);
                System.out.println(messageThreadResult);
            }
        }

    }

}