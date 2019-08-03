/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.common.ClassImportantValues;
import com.esb.common.Configurations;
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
    /**
     * For testing
     */
    public static final HTTPCalls httpcals = new HTTPCalls();
    public static final ClassImportantValues cl = new ClassImportantValues();
    public static final Configurations config = new Configurations();
    public static final String PROVIDER_URL = config.getConfig("PROVIDER_URL");
    public static final String SECURITY_PRINCIPAL = config.getConfig("SECURITY_PRINCIPAL");
    public static final String SECURITY_CREDENTIALS = config.getConfig("SECURITY_CREDENTIALS");
    public static final String JMSConnectionFactory = config.getConfig("JMSConnectionFactory");
    public static final String ESB_ADAPTER_RESPONSE_QUEUE = config.getConfig("ESB_ADAPTER_RESPONSE_QUEUE");
    public static final String ESBLogQueue = config.getConfig("ESBLogQueue");
    public static final String ESBTransaction_Topic = config.getConfig("ESBTransaction_Topic");
    public static final String ESB_LOG_QUEUE = config.getConfig("ESB_LOG_QUEUE");
    public static final String ESB_DATA_SOURCE = config.getConfig("ESB_DATA_SOURCE");
    public static final String ESBLog = config.getConfig("ESBLOG");
    public static final String QUEUE_WAIT = config.getConfig("QUEUE_WAIT");
    public static final String IPRS_URL = config.getConfigFromObject("IPRS", "IPRS_URL");
    public static final String IPRSclientID = config.getConfigFromObject("IPRS", "clientID");
    public static final String IPRSusr = config.getConfigFromObject("IPRS", "usr");
    public static final String IPRSpwd = config.getConfigFromObject("IPRS", "pwd");
    public static final String IPRSsalt = config.getConfigFromObject("IPRS", "salt");
    public static final String CHANNELS = config.getConfig("CHANNELS");
    public static final String BILL_PREURL = config.getConfig("BILL_PREURL");
    public static final String USSDOnlineRequest = config.getConfig("USSDOnlineRequest");
    public static final String PGIPRSENDPOINT = config.getConfigFromObject("PG_ADAPTOR", "IPRSURL");
    public static final String PGCLIENTID = config.getConfigFromObject("PG_ADAPTOR", "PGCLIENTID");
    public static final String PGUSR = config.getConfigFromObject("PG_ADAPTOR", "PGUSR");
    public static final String PGPWD = config.getConfigFromObject("PG_ADAPTOR", "PGPWD");
    public static final String PGKEY = config.getConfigFromObject("PG_ADAPTOR", "PGKEY");
    public static final String CURRENCYCODE = config.getConfigFromObject("PG_ADAPTOR", "CURRENCYCODE");
}
