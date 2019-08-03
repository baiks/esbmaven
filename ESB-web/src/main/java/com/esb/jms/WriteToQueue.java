/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.jms;

import com.esb.Main.ConfigSession;
import java.util.HashMap;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author adm
 */
public class WriteToQueue {

    // private static final String JMS_FACTORY = "java:/ConnectionFactory";
    // private static final String JMS_FACTORY =
    // "java:jboss/exported/jms/RemoteConnectionFactory";
    private Context context;
    private InitialContext cont;
    private ConnectionFactory connectionFactory;
    private QueueConnectionFactory qconnectionFactory;
    private Queue queue;
    private Connection connection;
    private QueueConnection qconnection;
    private Session session;
    private QueueSession qsession;
    private MessageProducer messageProducer;
    QueueSender queueSender;
    TextMessage textMessage;

    /**
     * This function writes message to queue.
     *
     * @param queueName
     * @param PROVIDER_URL
     * @param SECURITY_PRINCIPAL
     * @param SECURITY_CREDENTIALS
     * @param JMS_FACTORY
     * @param dataMap
     * @return
     */
    public boolean writeToRequestQueue(String queueName, String PROVIDER_URL, String SECURITY_PRINCIPAL,
            String SECURITY_CREDENTIALS, String JMS_FACTORY, String dataMap) {
        boolean writeToQueue = false;
        String messageID = "";
        HashMap data = null;
        // If txn
        if (dataMap.contains("CorrelationID")) {
            data = ConfigSession.cl.ReturnMap(dataMap);
            messageID = data.get("CorrelationID").toString();
        }
        try {
            Properties properties = new Properties();
            properties.put(Context.PROVIDER_URL, PROVIDER_URL);
            properties.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
            properties.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);

            if (dataMap.contains("CorrelationID")) { // ESB expect ObjectMessage with a correlation ID
                context = new InitialContext(properties);
                connectionFactory = (ConnectionFactory) context.lookup(JMS_FACTORY);
                queue = (Queue) context.lookup(queueName);
                connection = connectionFactory.createConnection();
                connection.start();
                session = connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                messageProducer = session.createProducer(queue);
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(data);
                objectMessage.setJMSCorrelationID(messageID);
                messageProducer.send(objectMessage);
            } else { // Logs expect TextMessage
                context = new InitialContext(properties);
                qconnectionFactory = (QueueConnectionFactory) context.lookup(JMS_FACTORY);
                queue = (Queue) context.lookup(queueName);
                qconnection = qconnectionFactory.createQueueConnection();
                qconnection.start();
                qsession = qconnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                queueSender = qsession.createSender(queue);
                textMessage = qsession.createTextMessage();
                textMessage.setText(dataMap);
                queueSender.send(textMessage);
            }

            writeToQueue = true;
            releaseConnections();

        } catch (JMSException | NamingException ex) {
            ex.printStackTrace();
        }

        return writeToQueue;
    }

    /**
     * This function release queue Connections
     */
    public void releaseConnections() {
        try {
            if (messageProducer != null) {
                messageProducer.close();
            }

            if (session != null) {
                session.close();
            }
            if (qsession != null) {
                qsession.close();
            }
            closeOthers();

        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }

    /**
     * This function release queue Connections
     */
    public void closeOthers() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (qconnection != null) {
                qconnection.close();
            }
            if (queue != null) {
                queue = null;
            }
            if (connectionFactory != null) {
                connectionFactory = null;
            }

            if (qconnectionFactory != null) {
                qconnectionFactory = null;
            }
            if (context != null) {
                context = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
