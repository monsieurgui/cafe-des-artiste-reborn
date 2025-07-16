package com.cafedesartistes.botplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlayerApplication {
    private static final Logger logger = LoggerFactory.getLogger(BotPlayerApplication.class);

    public static void main(String[] args) {
        logger.info("Bot Player starting...");
        
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            logger.info("Bot Player interrupted, shutting down...");
            Thread.currentThread().interrupt();
        }
    }
}