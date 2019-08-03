/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.jms.TopicWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

/**
 *
 * @author adm
 */
@MessageDriven(mappedName = "java:jboss/exported/jms/queue/NBKQueueReversal", activationConfig = {
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "durable"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/queue/NBKQueueReversal"),
    @ActivationConfigProperty(propertyName = "connectionFactoryName", propertyValue = "ConnectionFactory")
})
public class ESBMDBReversal implements MessageListener {

    public ESBMDBReversal() {
    }

    @Override
    public void onMessage(Message message) {

        Object messageObject = new Object();

        if (message instanceof ObjectMessage) {
            try {
                messageObject = ((ObjectMessage) message).getObject();
                HashMap messageMap = (HashMap) messageObject;
                if (!messageMap.get("field0").equals("0420")) { //If message comes in as not 0420 convert to 0420
                    messageMap.put("field0", "0420");
                }

                //Send a Reversal
                messageMap.put("messageDest", "TXN");
                messageMap.put("CorrelationID", messageMap.get("field37"));
                TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, messageMap);
                topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL,
                        ConfigSession.SECURITY_PRINCIPAL,
                        ConfigSession.SECURITY_CREDENTIALS,
                        ConfigSession.JMSConnectionFactory);

                //Send Reversal SMS
                messageMap.put("messageDest", "SMS");
                messageMap.put("CorrelationID", messageMap.get("field37"));
                topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL,
                        ConfigSession.SECURITY_PRINCIPAL,
                        ConfigSession.SECURITY_CREDENTIALS,
                        ConfigSession.JMSConnectionFactory);

            } catch (JMSException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("onMessageReversal", sw.toString());

            }
        }

    }

}
