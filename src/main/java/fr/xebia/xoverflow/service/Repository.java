package fr.xebia.xoverflow.service;

import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import javaslang.control.Option;

public interface Repository {

    Option<MessageThread> addMessageThread(MessageThread messageThread);

    boolean addMessageToThread(String threadId, Message message);

    Option<MessageThread> getMessageThread(String id);

    Option<User> addUser(User user);

    Option<User> getUser(String id);

}
