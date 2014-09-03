package com.xebia.xoverflow.server.service;

import com.xebia.xoverflow.server.model.Post;
import spark.utils.IOUtils;

import javax.mail.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by jpthiery on 03/09/2014.
 */
public class MailPollerService {


    private final PostRepositoryService postRepositoryService;

    public MailPollerService(PostRepositoryService postRepositoryService) {
        this.postRepositoryService = postRepositoryService;
    }


    public void poll() {

        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", "thiery.in");
        properties.setProperty("mail.smtp.user", "xoverflox@thiery.in");

        Session session = Session.getInstance(properties);


        Store store = null;

        Folder defaultFolder = null;

        Folder inbox = null;

        try {

            store = session.getStore(new URLName("pop3://thiery.in"));

            String password = System.getProperty("mail.password");

            store.connect("xoverflow@thiery.in", password);

            defaultFolder = store.getDefaultFolder();

            inbox = defaultFolder.getFolder("INBOX");

            try {

                inbox.open(Folder.READ_WRITE);
                int count = inbox.getMessageCount();
                int unread = inbox.getUnreadMessageCount();

                for (int i = 1; i <= count; i++) {

                    Message message = inbox.getMessage(i);
                    Post post = new Post();

                    post.setSubject(message.getSubject());
                    post.setUserName(message.getFrom()[0].toString());

                    Address[] addresses = message.getFrom();
                    for (Address address : addresses) {
                        System.out.println("\t" + address);
                    }

                    Multipart mp = (Multipart) message.getContent();
                    String content = IOUtils.toString(mp.getBodyPart(0).getInputStream());
                    post.setBody(content);

                    postRepositoryService.create(post);

                    message.setFlag(Flags.Flag.DELETED,true);

                }


            } catch (Exception e) {

                e.printStackTrace();

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally { // Ne pas oublier de fermer tout ça !

            close(inbox);

            close(defaultFolder);

            try {

                if (store != null && store.isConnected()) {

                    store.close();

                }

            } catch (MessagingException e) {

                e.printStackTrace();

            }

        }

    }

    private static void close(Folder folder) {

        if (folder != null && folder.isOpen()) {

            try {

                folder.close(false); // false -> On n'efface pas les messages marqués DELETED

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }


}
