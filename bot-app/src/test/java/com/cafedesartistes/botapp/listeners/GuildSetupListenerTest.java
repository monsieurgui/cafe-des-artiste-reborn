package com.cafedesartistes.botapp.listeners;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for GuildSetupListener.
 */
public class GuildSetupListenerTest {
    
    @Test
    public void testListenerInstantiation() {
        String testDbUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        GuildSetupListener listener = new GuildSetupListener(testDbUrl);
        assertNotNull("Listener should be instantiated", listener);
    }
}