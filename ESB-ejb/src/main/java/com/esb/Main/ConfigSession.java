/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.common.ClassImportantValues;
import com.esb.common.Configurations;
import com.esb.common.DatabaseConnections;
import com.esb.common.HTTPCalls;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;

/**
 *
 * @author adm
 */
@Singleton
@LocalBean
public class ConfigSession {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public static final HTTPCalls httpcals = new HTTPCalls();
    public static final ClassImportantValues cl = new ClassImportantValues();
    public static final DatabaseConnections connect = new DatabaseConnections();
    public static final Configurations config = new Configurations();
    public static final String PROVIDER_URL = config.getConfig("PROVIDER_URL");
    public static final String SECURITY_PRINCIPAL = config.getConfig("SECURITY_PRINCIPAL");
    public static final String SECURITY_CREDENTIALS = config.getConfig("SECURITY_CREDENTIALS");
    public static final String JMSConnectionFactory = config.getConfig("JMSConnectionFactory");
    public static final String ESBTransaction_Topic = config.getConfig("ESBTransaction_Topic");
    public static final String ESB_ADAPTER_RESPONSE_QUEUE = config.getConfig("ESB_ADAPTER_RESPONSE_QUEUE");
    public static final String NBKQueue = config.getConfig("NBKQueue");
    public static final String ESB_LOG_QUEUE = config.getConfig("ESB_LOG_QUEUE");
    public static final String ESB_DATA_SOURCE = config.getConfig("ESB_DATA_SOURCE");
    public static final String ESBLog = config.getConfig("ESBLOG");
    public static final String ESBLogQueue = config.getConfig("ESBLogQueue");
}
