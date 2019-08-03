/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.jms.TopicWriter;
import com.esb.jms.WriteToQueue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author adm
 */
public class ESBRequestProcessor {

    TransactionProcessor Processor = new TransactionProcessor();
    TransactionsValidations validate = new TransactionsValidations();
    WriteToQueue writetoqueue = new WriteToQueue();
    //private HashMap request = null;

//    public ESBRequestProcessor(HashMap request) {
//        this.request = request;
//    }
    public void processRequest(HashMap request) {
        boolean sendsms = false;
        boolean sendemail = false;
        Random rand = new Random();
        String Token = "";
        try {
            request = validate.getCharges(request);
            int procode = Integer.parseInt(request.get("field3").toString());
            switch (procode) {
                case 310000:
                case 400000:
                case 430000:
                case 420000:
                case 500000:
                case 210000:
                case 10000:
                case 450000:
                case 600000: //LOAN REQUEST
                case 610000: //LOAN REPAYMENTS
                case 460000: //CARD TO WALLET
                case 480000: //CARD TO MPESA
                    Processor.POST_MINI_TRANSACTIONS(request);
                    sendsms = true;
                    break;
                case 620000:
                    Token = ConfigSession.cl.GenerateRandomNo().substring(0, 8);
                    request.put("field36", ConfigSession.cl.HmacHash(Base64.encodeBase64String(Token.getBytes()), "secret", "HmacSHA256"));
                    request.put("field66", Token);
                    Processor.POST_MINI_TRANSACTIONS(request);
                    sendsms = true;
                    break;
                case 630000:
                    request.put("field36", ConfigSession.cl.HmacHash(Base64.encodeBase64String(request.get("field36").toString().getBytes()), "secret", "HmacSHA256"));
                    Processor.POST_MINI_TRANSACTIONS(request);
                    sendsms = true;
                    break;
                case 380000:
                    Processor.ProcessMinistatement(request);
                    Processor.POST_MINI_TRANSACTIONS(request);
                    if (!request.get("field32").equals("APP")) {
                        sendsms = true;
                    }
                    break;
                case 370000:
                    Processor.POST_MINI_TRANSACTIONS(request);
                    Processor.ProcessFullstatement(request);
                    sendemail = true;
                    break;
                case 230000:
                    request = Processor.SalaryUploadFullReg(request);
                    if (request.get("field39").equals("00")) {
                        Processor.POST_MINI_TRANSACTIONS(request);
                        sendsms = true;
                    }
                    break;
                default:
                    request.put("field39", "57");
                    request.put("field48", "Invalid processing code");
                    break;
            }
            LogTransaction(request);
            SendSMSAndEmail(sendsms, sendemail, request);
            SendResponse(request);
        } catch (NumberFormatException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("processRequest", sw.toString());
            request.put("field39", "57");
            request.put("field48", "An error occurred!");
        }
    }

    private void SendSMSAndEmail(boolean sendsms, boolean sendemail, HashMap request) {
        try {
            //Send SMS and Email if and only if successful
            if (request.containsKey("field39") && request.containsKey("field0")) {
                if (request.get("field39").equals("00") && request.get("field0").equals("0200")) {
                    //Start Send SMS to SMS topic
                    if (sendsms) {
                        //..Remember parameters below are important
                        request.put("messageDest", "SMS");
                        request.put("TXN", "Yes");
                        request.put("CorrelationID", request.get("field37"));
                        TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, request);
                        topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL,
                                ConfigSession.SECURITY_PRINCIPAL,
                                ConfigSession.SECURITY_CREDENTIALS,
                                ConfigSession.JMSConnectionFactory);
                    }
                    //End Send SMS to SMS topic

                    //Start Send EMAIL to EMAIL topic
                    if (sendemail) {
                        //..Remember parameters below are important
                        request.put("messageDest", "EMAIL");
                        request.put("TXN", "Yes");
                        request.put("CorrelationID", request.get("field37"));
                        TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, request);
                        topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL,
                                ConfigSession.SECURITY_PRINCIPAL,
                                ConfigSession.SECURITY_CREDENTIALS,
                                ConfigSession.JMSConnectionFactory);
                    }
                    //End Send EMAIL to EMAIL topic
                }
            }
        } catch (NamingException | JMSException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("SendSMSAndEmail", sw.toString());
            request.put("field39", "57");
            request.put("field48", "An error occurred!");
        }
    }

    private void SendResponse(HashMap inrequest) {
        boolean topg = false; //Send Request to PG
        boolean tonbk = false; //Send Request to NBK
        try {
            if (inrequest.get("field39").equals("00")) {
                int procode = Integer.parseInt(inrequest.get("field3").toString());
                switch (procode) {
                    case 430000:
                        topg = true;
                        break;
                    case 500000:
                        topg = true;
                        break;
                    case 420000:
                        topg = true;
                        break;
                    case 450000:
                        topg = true;
                        break;
                    case 400000:
                        if (inrequest.containsKey("field71")) {
                            if (inrequest.get("field71").equals("BANK")) {  //Send to NBK if Wallet to Bank
                                tonbk = true;
                            }
                        }
                        break;
                    case 460000:
                        topg = true;
                        break;
                    case 480000: //Send to PG to DR card and the Send to NBK to CR MPESA
                        topg = true;
                        tonbk = true;
                        break;
                }
            }
            if (!(inrequest.get("field0").equals("0420"))) {
                if (topg) { //Transactions are processed by PG
                    inrequest.put("messageDest", "PG");
                    inrequest.put("CorrelationID", inrequest.get("field37"));
                    TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, inrequest);
                    topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL,
                            ConfigSession.SECURITY_PRINCIPAL,
                            ConfigSession.SECURITY_CREDENTIALS,
                            ConfigSession.JMSConnectionFactory);
                }
                if (tonbk) {  //Transactions are processed by NBK
                    inrequest.put("CorrelationID", inrequest.get("field37"));
                    writetoqueue.writeToRequestQueue(ConfigSession.NBKQueue,
                            ConfigSession.PROVIDER_URL,
                            ConfigSession.SECURITY_PRINCIPAL,
                            ConfigSession.SECURITY_CREDENTIALS,
                            ConfigSession.JMSConnectionFactory, inrequest.toString());
                }

                inrequest.put("CorrelationID", inrequest.get("field37"));
                writetoqueue.writeToRequestQueue(ConfigSession.ESB_ADAPTER_RESPONSE_QUEUE,
                        ConfigSession.PROVIDER_URL,
                        ConfigSession.SECURITY_PRINCIPAL,
                        ConfigSession.SECURITY_CREDENTIALS,
                        ConfigSession.JMSConnectionFactory, inrequest.toString());

            }

            UpdateTransaction(inrequest);
        } catch (NumberFormatException | NamingException | JMSException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ResponseToPG", sw.toString());
        }
    }

    public void LogTransaction(HashMap inrequest) {
        ArrayList params = new ArrayList();
        ArrayList paramvalues = new ArrayList();
        Iterator it = inrequest.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            if (pair.getKey().toString().contains("field")) {
                if (pair.getKey().toString().contains("field0")) {
                    params.add("field1");
                    paramvalues.add(pair.getValue());
                } else {
                    params.add(pair.getKey());
                    paramvalues.add(pair.getValue());
                }
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        for (int i = 0; i < 128; i++) {
            if (!params.contains("field" + i)) {
                if (i == 4) {
                    params.add("field" + i);
                    paramvalues.add("0");
                } else {
                    params.add("field" + i);
                    paramvalues.add("");
                }
            }
        }
        String sql = "SP_SAVEMESSAGES";
        Connection con = ConfigSession.connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
        ConfigSession.connect.ExecuteUpdateSP(con, sql, params, paramvalues);
    }

    public void UpdateTransaction(HashMap inrequest) {
        String sql = "UPDATE TBMESSAGES_EXTERNAL SET "
                + "FIELD39='" + inrequest.get("field39") + "',"
                + "FIELD48='" + inrequest.get("field48") + "' "
                + "WHERE "
                + "FIELD37='" + inrequest.get("field37") + "'"
                + " AND FIELD11='" + inrequest.get("field11") + "'";
        Connection con = ConfigSession.connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
        ConfigSession.connect.ExecuteUpdate(con, sql);
    }

//    @Override
//    public void release() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void run() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
