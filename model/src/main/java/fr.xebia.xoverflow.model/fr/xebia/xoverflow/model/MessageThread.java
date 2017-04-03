package fr.xebia.xoverflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang.StringUtils.isBlank;

public class MessageThread implements Serializable {

    private final String id;

    private final String subject;

    private final Origin origin;

    private final List<Message> messages;

    public MessageThread(String id, String subject, Origin origin) {
        if (isBlank(subject)) {
            throw new IllegalArgumentException("subject must be defined.");
        }
        if (origin == null) {
            this.origin = Origin.UNKNOW;
        } else {
            this.origin = origin;
        }
        this.id = id;
        this.subject = subject;
        this.messages = new ArrayList<>();
    }

    public MessageThread(String id, String subject) {
        this(id, subject, null);
    }

    public MessageThread(String subject) {
        this(null, subject, null);
    }

    public void addMessage(Message message) {
        requireNonNull(message, "message must be defined.");
        this.messages.add(message);
    }

    public void addMessage(List<Message> messages) {
        requireNonNull(messages, "messages must be defined.");
        List<Message> validateMessages = messages.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.messages.addAll(validateMessages);
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageThread that = (MessageThread) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (!subject.equals(that.subject)) return false;
        if (origin != that.origin) return false;
        return messages.equals(that.messages);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + subject.hashCode();
        result = 31 * result + origin.hashCode();
        result = 31 * result + messages.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MessageThread{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", origin=" + origin +
                ", messages=" + messages +
                '}';
    }

    public static Builder builder(MessageThread messageThreadToCopy) {
        return new Builder(messageThreadToCopy);
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum Origin {
        EMAIL,
        SLACK,
        UNKNOW
    }

    public static class Builder implements IdBuilder<MessageThread> {

        private String id;

        private String subject;

        private Origin origin;

        private List<Message> messages;

        public Builder() {
            this.messages = new ArrayList<>();
        }

        private Builder(MessageThread messageThreadToCopy) {
            requireNonNull(messageThreadToCopy, "messageThreadToCopy must be defined.");
            this.id = messageThreadToCopy.id;
            this.subject = messageThreadToCopy.subject;
            this.origin = messageThreadToCopy.origin;
            this.messages = new ArrayList<>(messageThreadToCopy.messages);
        }

        @Override
        public MessageThread build() {
            MessageThread messageThread = new MessageThread(id, subject, origin);
            messageThread.addMessage(messages);
            return messageThread;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setOrigin(Origin origin) {
            this.origin = origin;
            return this;
        }

        public Builder setMessages(List<Message> messages) {
            this.messages = messages;
            return this;
        }
    }

}
