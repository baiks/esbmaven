/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author adm
 */
public class ReadFromQueue {

    //private static final String JMS_FACTORY = "java:/ConnectionFactory";
    //private static final String JMS_FACTORY = "java:jboss/exported/jms/RemoteConnectionFactory";
    private Context initialContext;
    private QueueConnectionFactory queueConnectionFactory;
    private Queue queue;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    QueueBrowser queueBrowser;
    QueueReceiver queueReceiver;
/**
 * This function browses a queue and reads the message in it.
 * @param queueName
 * @param PROVIDER_URL
 * @param SECURITY_PRINCIPAL
 * @param SECURITY_CREDENTIALS
 * @param JMS_FACTORY
 * @param correlationID
 * @return
 */
    public HashMap readfromQueue(String queueName, String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS, String JMS_FACTORY, String correlationID) {
        String queueMessage = "";
        HashMap dataMap = new HashMap();
        try {
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY,
                    "org.jnp.interfaces.NamingContextFactory");
            properties.put(Context.URL_PKG_PREFIXES, " org.jboss.naming:org.jnp.interfaces");
            properties.put(Context.PROVIDER_URL, PROVIDER_URL);
            properties.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
            properties.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);

            initialContext = new InitialContext();
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(JMS_FACTORY);
            queue = (Queue) initialContext.lookup(queueName);
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queueBrowser = queueSession.createBrowser(queue, "JMSCorrelationID = '" + correlationID + "'");
            queueConnection.start();
            Enumeration enumeration = queueBrowser.getEnumeration();

            while (enumeration.hasMoreElements()) {
                Message message = (Message) enumeration.nextElement();

                queueReceiver = queueSession.createReceiver(queue, "JMSCorrelationID = '" + correlationID + "'");

                ObjectMessage objectMessage = (ObjectMessage) message;

                dataMap = (HashMap) objectMessage.getObject();
                queueMessage = dataMap.toString();

                queueReceiver.receiveNoWait();
            }

            queueBrowser.close();

            dataMap.remove("CorrelationID");

            return dataMap;
        } catch (JMSException | NamingException ex) {
            java.util.logging.Logger.getLogger(ReadFromQueue.class.getSimpleName()).log(Level.INFO, "ERROR:- " + ex.getMessage(), ex);
            return dataMap;
        } finally {
            releaseResources();
        }
    }
/**
 * This function releases queue Resources
 */
    public void releaseResources() {
        try {
            queueSession.close();

            queueConnection.close();

            queue = null;

            queueConnectionFactory = null;

            initialContext.close();

        } catch (JMSException | NamingException ex) {
            java.util.logging.Logger.getLogger(ReadFromQueue.class.getSimpleName()).log(Level.INFO, "ERROR:- " + ex.getMessage(), ex);
        }

    }
}
