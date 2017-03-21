package fr.xebia.xoverflow.service;

import fr.xebia.xoverflow.model.Message;
import fr.xebia.xoverflow.model.MessageThread;
import fr.xebia.xoverflow.model.User;
import fr.xebia.xoverflow.service.search.Criteria;
import javaslang.control.Option;

import java.util.List;
import java.util.Set;

public interface Repository {

    Option<MessageThread> addMessageThread(MessageThread messageThread);

    boolean addMessageToThread(String threadId, Message message);

    Option<MessageThread> getMessageThread(String id);

    Option<User> addUser(User user);

    Option<User> getUser(String id);

    List<MessageThread> searchMessageThread(Criteria... criterion);

}
