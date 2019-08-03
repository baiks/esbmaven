/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author adm
 */
@MessageDriven(mappedName = "java:/exported/jms/topic/ESBTransaction_Topic", activationConfig = {
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "java:/exported/jms/topic/ESBTransaction_Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "durable"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "java:/exported/jms/topic/ESBTransaction_Topic"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/exported/jms/topic/ESBTransaction_Topic"),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "messageDest= 'TXN'")
})
public class ESBMDB implements MessageListener {

    public ESBMDB() {
    }

    @Override
    public void onMessage(Message message) {

        Object messageObject = new Object();

        if (message instanceof ObjectMessage) {
            try {
                messageObject = ((ObjectMessage) message).getObject();
                HashMap messageMap = (HashMap) messageObject;
                ESBRequestProcessor process = new ESBRequestProcessor();

//                WorkerManager wm = new WorkerManager();
//                WorkManager mwm = wm.JbossWorkManager();
//                mwm.scheduleWork(process);
                process.processRequest(messageMap);
            } catch (JMSException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("onMessage", sw.toString());

            }
        }

    }

}
