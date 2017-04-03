package fr.xebia.xoverflow.model;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

public class Message implements Serializable {

    private final String id;

    private final UserLight author;

    private final long publishDate;

    private final String content;

    public Message(String id, UserLight author, long publishDate, String content) {
        requireNonNull(author, "author must be defined.");
        this.id = id;
        this.author = author;
        this.publishDate = publishDate;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public UserLight getAuthor() {
        return author;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (publishDate != message.publishDate) return false;
        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (!author.equals(message.author)) return false;
        return content.equals(message.content);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + author.hashCode();
        result = 31 * result + (int) (publishDate ^ (publishDate >>> 32));
        result = 31 * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", author=" + author +
                ", publishDate=" + publishDate +
                ", content='" + content + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Message messageToCopy) {
        return new Builder(messageToCopy);
    }

    public static class Builder implements IdBuilder<Message> {

        private String id;

        private UserLight author;

        private long publishDate;

        private String content;

        private Builder() {
            publishDate = System.currentTimeMillis();
        }

        private Builder(Message messageToCopy) {
            requireNonNull(messageToCopy, "messageToCopy must be defined.");
            this.author = messageToCopy.author;
            this.publishDate = messageToCopy.publishDate;
            this.content = messageToCopy.content;
        }

        @Override
        public Message build() {
            return new Message(id, author, publishDate, content);
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }

        public Builder setAuthor(UserLight author) {
            this.author = author;
            return this;
        }

        public Builder setPublishDate(long publishDate) {
            this.publishDate = publishDate;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }
    }
}
