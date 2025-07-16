package com.cafedesartistes.botcore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotCoreApplication {
    private static final Logger logger = LoggerFactory.getLogger(BotCoreApplication.class);

    public static void main(String[] args) {
        logger.info("Bot Core starting...");
        
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            logger.info("Bot Core interrupted, shutting down...");
            Thread.currentThread().interrupt();
        }
    }
}