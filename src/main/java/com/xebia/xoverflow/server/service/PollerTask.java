package com.xebia.xoverflow.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.Callable;

public class PollerTask implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PollerTask.class);

    private final MailPollerService mailPollerService;

    @Inject
    public PollerTask(MailPollerService mailPollerService) {
        this.mailPollerService = mailPollerService;
    }

    @Override
    public Void call() throws Exception {
        while(!Thread.interrupted()) {
            LOGGER.info("Calling mailPollerService");
            mailPollerService.poll();
            Thread.sleep(5000);
        }
        return null;
    }
}

