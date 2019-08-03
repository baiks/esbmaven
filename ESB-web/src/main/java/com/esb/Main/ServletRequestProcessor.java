/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.common.ClassImportantValues;
import com.esb.jms.ReadFromQueue;
import com.esb.jms.TopicWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adm
 */
public class ServletRequestProcessor {

    DBProcessor dbProcessor = new DBProcessor();
    ReadFromQueue readfromqueue = new ReadFromQueue();

    /**
     * This is the core function thus the decision maker that receives requests
     * from channel routing them to various sub-functions depending on the
     * processing code.
     *
     * @param jsonrequest
     * @return
     */
    public JSONObject processRequest(JSONObject jsonrequest) {
        try {
            int procode = Integer.parseInt(jsonrequest.get("field3").toString());
            jsonrequest = dbProcessor.getWalletAccount(jsonrequest, "ACCOUNTNO"); // Get Wallet accout
            switch (procode) {
                case 110000:
                    jsonrequest = dbProcessor.ProcessLogin(jsonrequest);
                    break;
                case 120000:
                    jsonrequest = Registration(jsonrequest);
                    break;
                case 310000:
                case 380000:
                case 370000:
                case 420000:
                case 210000:
                case 10000:
                case 230000:
                case 620000:
                case 630000:
                case 450000:
                case 600000:
                case 610000:
                    jsonrequest = ProcessOtherTransactions(jsonrequest);
                    break;
                case 500000:
                case 400000:
                case 430000:
                case 460000:
                case 480000:
                    jsonrequest = EnrichFT(jsonrequest);
                    jsonrequest = ProcessOtherTransactions(jsonrequest);
                    break;
                case 130000:
                    jsonrequest = dbProcessor.ProcessCardLinking(jsonrequest);
                    break;
                case 140000:
                    jsonrequest = dbProcessor.ProcessUpdateUserDetails(jsonrequest);
                    break;
                case 150000:
                    jsonrequest = dbProcessor.getCharge(jsonrequest);
                    break;
                case 160000:
                    jsonrequest = DBProcessor.BillPresentment(jsonrequest);
                    break;
                case 170000:
                    jsonrequest = dbProcessor.ProcesspwdReset(jsonrequest);
                    break;
                case 180000:
                    jsonrequest = dbProcessor.getCardProfile(jsonrequest);
                    break;
                case 190000:
                    jsonrequest = SendSMS(jsonrequest);
                    break;
                case 410000:
                    jsonrequest = DBProcessor.USSDOnlineRequests(jsonrequest);
                    SendSMS(jsonrequest);
                    break;
                default:
                    jsonrequest.put("field39", "57");
                    jsonrequest.put("field48", "Invalid processing code");
                    break;
            }

        } catch (JSONException | NumberFormatException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ServletError", sw.toString());
            jsonrequest.put("field39", "57");
            jsonrequest.put("field48", "An error occurred!");
            // System.out.println("Servlet Processor" + sw.toString());
        }

        return jsonrequest;
    }

    /**
     * This function retrieves IPRS service id from TBSERVICES by invoking the
     * method getBillService() and process the registration by invoking the
     * method ProcessRegistration().
     *
     * @param inrequest
     * @return
     */
    private JSONObject Registration(JSONObject inrequest) {
        dbProcessor.getBillService("IPRS", "SERVICEID", inrequest, "SERVICECODE"); // Now we go thru PG
        // so load field // service id
        if ("APP".equals(inrequest.get("field32"))) {
            if (!"".equals(inrequest.get("field128"))) {
                inrequest.put("field126", "250000");
                inrequest = SendOTP(inrequest);
            } else {
                inrequest = dbProcessor.ProcessRegistration(inrequest);
            }
        } else {
            inrequest = dbProcessor.ProcessRegistration(inrequest);
        }
        return inrequest;
    }

    /**
     * This function sends requests to ESB MDB via the Transaction topic
     * (ESBTransaction_Topic) with message selector as messageDest='TXN' and
     * browse response from the queue (ESB_ADAPTER_RESPONSE_QUEUE)
     *
     * @param inrequest
     * @return
     */
    private JSONObject ProcessOtherTransactions(JSONObject inrequest) {
        inrequest.put("messageDest", "TXN");
        inrequest.put("CorrelationID", inrequest.get("field37"));
        try {
            TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, (HashMap) inrequest.toMap());
            topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL, ConfigSession.SECURITY_PRINCIPAL,
                    ConfigSession.SECURITY_CREDENTIALS, ConfigSession.JMSConnectionFactory);

            // Thread.sleep(5000);
            HashMap response = new HashMap();
            while (response.isEmpty()) { // Check if empty and has looped max 5
                response = readfromqueue.readfromQueue(ConfigSession.ESB_ADAPTER_RESPONSE_QUEUE,
                        ConfigSession.PROVIDER_URL, ConfigSession.SECURITY_PRINCIPAL,
                        ConfigSession.SECURITY_CREDENTIALS, ConfigSession.JMSConnectionFactory,
                        inrequest.get("field37").toString());
            }
            inrequest = new JSONObject(response);

        } catch (NamingException | JMSException | JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ProcessOtherTransactions", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function sends SMS to the topic with message selector as
     * messageDest='SMS'.
     *
     * @param inrequest
     * @return
     */
    private JSONObject SendSMS(JSONObject inrequest) {
        inrequest.put("messageDest", "SMS");
        inrequest.put("CorrelationID", inrequest.get("field37"));
        if (!"410000".equals(inrequest.get("field3"))) { // Send SMS for USSD online
            inrequest.put("TXN", "Yes");
            inrequest.put("field3", inrequest.get("field126"));
        }

        try {
            TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, (HashMap) inrequest.toMap());
            topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL, ConfigSession.SECURITY_PRINCIPAL,
                    ConfigSession.SECURITY_CREDENTIALS, ConfigSession.JMSConnectionFactory);
            inrequest.put("field39", "00");
            inrequest.put("field48", "Successful");

        } catch (NamingException | JMSException | JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("SendSMS", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function receives an OTP request from APP and send an SMS with the
     * OTP.
     *
     * @param jsonrequest
     * @return
     */
    public JSONObject SendOTP(JSONObject jsonrequest) {
        try {
            // Check if customer exist
            jsonrequest = dbProcessor.CheckCustomer(jsonrequest);

            // If customer not exist send OTP sms
            if ("00".equals(jsonrequest.get("field99"))) {
                SendSMS(jsonrequest);
            }
            jsonrequest.put("field39", jsonrequest.get("field99"));
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("SendOTP", sw.toString());
            jsonrequest.put("field39", "57");
            jsonrequest.put("field48", "An error occurred!");
        }
        return jsonrequest;
    }

    /**
     * This function retrieves the Bank Id and Bank Name by invoking the
     * function getBankID()
     *
     * @param inrequest
     * @return
     */
    private JSONObject EnrichFT(JSONObject inrequest) {
        try {
            if (inrequest.has("field100") && "400000".equals(inrequest.get("field3"))) {
                if (inrequest.has("field71")) {
                    if ("BANK".equals(inrequest.get("field71"))) {
                        inrequest = dbProcessor.getBankID(inrequest.get("field35").toString(), inrequest);
                    }
                }
            } else {
                EnrichOthers(inrequest);
            }
        } catch (JSONException | NumberFormatException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("EnrichFT", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function retrieves the Biller service id by invoking the function
     * getBillService().
     *
     * @param inrequest
     * @return
     */
    private JSONObject EnrichOthers(JSONObject inrequest) {
        int procode = Integer.parseInt(inrequest.get("field3").toString());
        switch (procode) {
            case 500000:
            case 460000:
            case 480000:
            case 430000:
                if (inrequest.has("field71")) {
                    inrequest = dbProcessor.getBillService(inrequest.get("field71").toString(), "SERVICENAME", inrequest,
                            "SERVICECODE");
                }
                break;
            default:
                break;
        }
        return inrequest;
    }

    /**
     * This function checks if the request has field102 or field103 as card and
     * does the loads CARDDR or CARDCR on the json object request.
     *
     * @param channelRequest
     * @return
     */
    public static JSONObject CardTransaction(JSONObject channelRequest) {
        //Determine if Card for 102..can be done better just a POC 
        if (channelRequest.has("field102")) {
            if (channelRequest.get("field102").toString().length() > 12) {
                if (channelRequest.get("field3").equals("310000") || channelRequest.get("field3").equals("380000")) {
                    channelRequest.put("CARDDR", "Yes");
                    channelRequest.put("CARDCR", "Yes");
                } else {
                    channelRequest.put("CARDDR", "Yes");
                }

            }
        }
        //Determine if Card for 103..can be done better just a POC
        if (channelRequest.has("field103")) {
            if (channelRequest.get("field103").toString().length() > 12) {
                channelRequest.put("CARDCR", "Yes");
            }
        }
        return channelRequest;
    }
}
