package fr.xebia.xoverflow.backend.test;

import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.UserLight;

import java.util.Collections;

public interface DataBuilder {

    default UserLight aUser() {
        UserLight.Builder builder = UserLight.builder()
                .setUsername("jpthiery");
        builder.setId("1234");
        return builder
                .build();
    }

    default Message aMessage() {
        Message.Builder builder = Message.builder();
        builder.setContent("Fake content")
                .setAuthor(aUser())
                .setPublishDate(System.currentTimeMillis());
        builder.setId("1234");
        return builder.build();
    }

    default MessageThread aMessageThread() {
        MessageThread.Builder builder = MessageThread.builder()
                .setMessages(Collections.singletonList(aMessage()))
                .setSubject("test thread")
                .setOrigin(MessageThread.Origin.UNKNOW);
        builder.setId("1234");
        return builder
                .build();
    }

}
