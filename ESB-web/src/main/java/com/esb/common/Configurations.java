package com.esb.common;

import java.util.logging.Level;
import redis.clients.jedis.Jedis;

/**
 *
 * @author sure
 */
public class Configurations {

    private Jedis jedis = null;
    String address = "127.0.0.1";
    int port = 6379;
    int dbno = 1;

    /**
     * This function does redis connection.
     */
    public Configurations() {
        try {
            if (jedis == null) {
                jedis = new Jedis(address, port);
            } else if (!jedis.isConnected()) {
                jedis = new Jedis(address, port);
            }

            jedis.auth("baiks@123");
            jedis.select(dbno);

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Configurations.class.getSimpleName()).log(Level.INFO, "ERROR:- " + ex.getMessage(), ex);
        }
    }

    /**
     * This function retrieves a string value from redis by key parsed.
     * @param keyString
     * @return
     */
    public String getConfig(String keyString) {
        String valString = null;
        try {
            valString = jedis.get(keyString);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Configurations.class.getSimpleName()).log(Level.INFO, "ERROR:- " + ex.getMessage(), ex);
        }
        return valString;
    }

    /**
     * This function retrieves a hash param from redis by use of object key and key string.
     * @param objKey
     * @param keyString
     * @return
     */
    public String getConfigFromObject(String objKey, String keyString) {
        String valString = null;
        try {
            valString = jedis.hget(objKey, keyString);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Configurations.class.getSimpleName()).log(Level.INFO, "ERROR:- " + ex.getMessage(), ex);
        }
        return valString;
    }
}
