package com.cafedesartistes.botapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotAppApplication {
    private static final Logger logger = LoggerFactory.getLogger(BotAppApplication.class);

    public static void main(String[] args) {
        logger.info("Bot App starting...");
        
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            logger.info("Bot App interrupted, shutting down...");
            Thread.currentThread().interrupt();
        }
    }
}