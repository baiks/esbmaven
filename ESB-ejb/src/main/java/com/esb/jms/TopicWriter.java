package com.esb.jms;

import java.util.*;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class TopicWriter {

    private final String queue;
    //private TextMessage msg;
    //private final String JMS_FACTORY = "java:jboss/exported/jms/RemoteConnectionFactory";
    //private static final String JMS_FACTORY = "java:/ConnectionFactory";
    private HashMap objmsg = new HashMap();

    public TopicWriter(String queue, HashMap objmsg) {
        this.queue = queue;
        this.objmsg = objmsg;
    }

    public boolean sendObjectToTopic(String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS, String JMS_FACTORY) throws NamingException, JMSException {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        Properties p = new Properties();
        //p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        //p.put(Context.PROVIDER_URL, "remote://52.167.209.202:4447");
        p.put(Context.PROVIDER_URL, PROVIDER_URL);
        p.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
        p.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        Context cont = new InitialContext(p);
        try {
            //Custom made ConnetionFactory
            ConnectionFactory conFact = (ConnectionFactory) cont.lookup(JMS_FACTORY);
            connection = conFact.createConnection();
            session = connection.createSession(false,
                    QueueSession.AUTO_ACKNOWLEDGE);
            Destination topic = (Topic) cont.lookup(this.queue);

            producer = session.createProducer(topic);
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(objmsg);
            msg.setStringProperty("messageDest", objmsg.get("messageDest").toString());
            msg.setJMSCorrelationID(objmsg.get("CorrelationID").toString());
            producer.send(msg);
            return true;
        } catch (NamingException | JMSException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (session != null) {
                session.close();
            }
            if (producer != null) {
                producer.close();
            }
        }

    }

    public boolean sendObjectToQueue(String PROVIDER_URL, String SECURITY_PRINCIPAL, String SECURITY_CREDENTIALS, String JMS_FACTORY) throws NamingException, JMSException {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        Properties p = new Properties();
//        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        p.put(Context.PROVIDER_URL, PROVIDER_URL);
        p.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
        p.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        Context cont = new InitialContext(p);
        try {
            ConnectionFactory conFact = (ConnectionFactory) cont.lookup(JMS_FACTORY);
            connection = conFact.createConnection();
            session = connection.createSession(false,
                    QueueSession.AUTO_ACKNOWLEDGE);
            Destination queue = (Queue) cont.lookup(this.queue);
            producer = session.createProducer(queue);
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(objmsg);
            msg.setJMSCorrelationID(objmsg.get("CorrelationID").toString());
            producer.send(msg);
            return true;
        } catch (NamingException | JMSException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (session != null) {
                session.close();
            }
            if (producer != null) {
                producer.close();
            }
            //producer.close();
        }

    }

}
