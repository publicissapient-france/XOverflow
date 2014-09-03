package com.xebia.xoverflow.server.service;

import spark.utils.IOUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by jpthiery on 03/09/2014.
 */
public class MailPollerService {

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

            store.connect("xoverflow@thiery.in", "");

            defaultFolder = store.getDefaultFolder();

            System.out.println("defaultFolder : " + defaultFolder.getName());


            for (Folder folder : defaultFolder.list()) {

                System.out.println(folder.getName());

            }

            inbox = defaultFolder.getFolder("INBOX");

            printMessages(inbox);

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

    private static void printMessages(Folder folder) {

        try {

            folder.open(Folder.READ_ONLY);

            int count = folder.getMessageCount();

            int unread = folder.getUnreadMessageCount();

            System.out.println("Il y a " + count + " messages, dont " + unread + " non lus.");

            for (int i = 1; i <= count; i++) {


                Message message = folder.getMessage(i);

                System.out.println("Message n° " + i);

                System.out.println("Sujet : " + message.getSubject());


                System.out.println("Expéditeur : ");

                Address[] addresses = message.getFrom();

                for (Address address : addresses) {

                    System.out.println("\t" + address);

                }


                System.out.println("Destinataires : ");

                addresses = message.getRecipients(Message.RecipientType.TO);

                if (addresses != null) {

                    for (Address address : addresses) {

                        System.out.println("\tTo : " + address);

                    }

                }

                addresses = message.getRecipients(MimeMessage.RecipientType.CC);

                if (addresses != null) {

                    for (Address address : addresses) {

                        System.out.println("\tCopie : " + address);

                    }

                }


                System.out.println("Content : -- ");

                Multipart mp = (Multipart) message.getContent();
                int mp_count = mp.getCount();
                System.out.println(IOUtils.toString(mp.getBodyPart(1).getInputStream()));



                //System.out.println(IOUtils.toString(message.getInputStream()));

                //System.out.println(message.getContent().toString());


            }


        } catch (Exception e) {

            e.printStackTrace();

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

    public static void main(String[] args) {
        MailPollerService mailPollerService = new MailPollerService();
        mailPollerService.poll();
    }

}
