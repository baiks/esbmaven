/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.common.ClassImportantValues;
import com.esb.common.DatabaseConnections;
import com.esb.common.HTTPCalls;
import com.esb.jms.TopicWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adm
 */
public class DBProcessor {

    private final DatabaseConnections connect = new DatabaseConnections();

    /**
     * This function receives a json login request with necessary fields from channel does
     * the necessary DB processing by invoking the view VW_ALLCUSTOMERS and table TBAGENTS  
     * depending on field102->Agent ID and sends back a json response to channel.
     * @param inrequest
     * @return
     */
    public JSONObject ProcessLogin(JSONObject inrequest) {
        try {
            // All columns
            HashMap columns = new HashMap();
            // where condition
            HashMap condition = new HashMap();
            // value of the condition
            HashMap val = new HashMap();
            String table = "VW_ALLCUSTOMERS";
            if (inrequest.has("TYPE")) {
                columns.put("1", "*");
                condition.put("1", "AGENTID");
                val.put("1", inrequest.get("field101"));
                table = "TBAGENTS";
            } else {
                columns.put("1", "*");
                switch (inrequest.get("field2").toString().length()) {
                    case 12:
                        condition.put("1", "MWALLETACCOUNT");
                        break;
                    default:
                        condition.put("1", "CUSTOMERNO");
                        break;
                }
                val.put("1", inrequest.get("field2"));
            }
            String sql = DatabaseConnections.ExcuteSelect(table, columns, condition, val, "");
            String res = connect.ExecuteQueryReturnJson(sql);

            if (ClassImportantValues.empty(res)) {
                inrequest.put("field39", "57");
                inrequest.put("field48", "Customer details do not exist");
            } else if (res.contains("An error occurred!")) {
                inrequest.put("field39", "57");
                inrequest.put("field48", res);
            } else {
                inrequest = new JSONObject(res.replace("]", "").replace("[", ""));
                inrequest.put("field39", "00");
                inrequest.put("field48", "Success");
            }
        } catch (JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ProcessLogin", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function receives a json object registration request from channel with necessary fields
     * and performs DB CRUD->C by invoking the Stored procedures SP_REGISTRATION(APP & USSD) and 
     * SP_REGISTRATION_INSTITUTION(CPS) depending with the channel(field32) and returns a json object.
     * @param inrequest
     * @return
     */
    public JSONObject ProcessRegistration(JSONObject inrequest) {
        try {
            String IDNumber = inrequest.get("field70").toString();
            String SurName = inrequest.get("field61").toString();
            String FName = inrequest.get("field62").toString();
            String LName = "";
            if (inrequest.has("field63")) {
                LName = inrequest.get("field63").toString();
            }
            int fromRange = 1;
            int toRange = 5;

            String pin = ClassImportantValues.GenerateRandomNo().substring(fromRange, toRange);
            String encpin = ConfigSession.cl.HmacHash(
                    Base64.encodeBase64String((pin + inrequest.get("field2")).getBytes()), "secret", "HmacSHA256");
            int cntryfro = 0;
            int cntryto = 2;
            if (inrequest.has("field79")) {
                if (!"KE".equalsIgnoreCase(inrequest.get("field79").toString().substring(cntryfro, cntryto))) {
                    encpin = inrequest.get("field72").toString();
                }
            }

            inrequest.put("field72", encpin);
            inrequest.put("rawpin", pin);
            HashMap<String, String> iprsres = new HashMap<String, String>();
            if (!"CPS".equals(inrequest.get("field32"))) {
                if ("CPS".equals(inrequest.has("field79"))) {
                    if (!"KE".equalsIgnoreCase(inrequest.get("field79").toString().substring(cntryfro, cntryto))) {
                        iprsres.put("field39", "00");
                    }
                } else {
                    // iprsres = ProcessIPRSRest(IDNumber, SurName, FName, LName, inrequest);
                    iprsres = ProcessIPRSSoap(IDNumber, SurName, FName, SurName);
                }
            } else {
                iprsres.put("field39", "00");
            }
            String sql = "SP_REGISTRATION";

            if ("CPS".equals(inrequest.get("field32"))) {
                sql = "SP_REGISTRATION_INSTITUTION";
            }

            ArrayList params = new ArrayList();
            params.add("FirstName");
            params.add("SecondName");
            params.add("LastName");
            params.add("PHONENUMBER");
            params.add("IdentificationID");
            params.add("EmailAddress");
            params.add("PIN");
            params.add("LANG");
            params.add("IMSI");
            params.add("DATEOFBIRTH");
            params.add("GENDER");
            params.add("TOWN");
            params.add("POSTALCODE");
            params.add("RESIDENCECODE");
            params.add("NATIONALITYCODE");
            params.add("KENYAMOBILE");
            params.add("APPROVED");
            if (!"CPS".equals(inrequest.get("field32"))) {
                if (!"00".equals(iprsres.get("field39"))) {
                    sql = "SP_IPRSFAILED";
                    params.add("FNAMEFROMIPRSS");
                    params.add("IDFROMIPRSS");
                    params.add("MNAMEFROMIPRSS");
                    params.add("SNAMEFROMIPRSS");
                    params.add("IPRSRESPONSE");
                }
            }
            ArrayList paramvalues = new ArrayList();
            paramvalues.add(inrequest.get("field61"));
            paramvalues.add(inrequest.get("field62"));
            if (inrequest.has("field63")) {
                paramvalues.add(inrequest.get("field63"));
            } else {
                paramvalues.add("");
            }
            paramvalues.add(inrequest.get("field2"));
            paramvalues.add(inrequest.get("field70"));
            if (inrequest.has("field71")) {
                paramvalues.add(inrequest.get("field71"));
            } else {
                paramvalues.add("");
            }
            paramvalues.add(inrequest.get("field72"));
            paramvalues.add(inrequest.get("field73"));
            paramvalues.add(inrequest.get("field74"));
            paramvalues.add(inrequest.get("field75"));
            if (inrequest.has("field76")) {
                paramvalues.add(inrequest.get("field76"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field77")) {
                paramvalues.add(inrequest.get("field77"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field78")) {
                paramvalues.add(inrequest.get("field78"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field79")) {
                paramvalues.add(inrequest.get("field79"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field80")) {
                paramvalues.add(inrequest.get("field80"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field81")) {
                paramvalues.add(inrequest.get("field81"));
            } else {
                paramvalues.add("");
            }
            paramvalues.add("1");
            if (!"CPS".equals(inrequest.get("field32"))) {
                if (!"00".equals(iprsres.get("field39"))) {
                    if (iprsres.containsKey("First_Name")) {
                        paramvalues.add(iprsres.get("First_Name"));
                    } else {
                        paramvalues.add("");
                    }
                    paramvalues.add(IDNumber);
                    if (iprsres.containsKey("Surname")) {
                        paramvalues.add(iprsres.get("Surname"));
                    } else {
                        paramvalues.add("");
                    }
                    if (iprsres.containsKey("Other_Name")) {
                        paramvalues.add(iprsres.get("Other_Name"));
                    } else {
                        paramvalues.add("");
                    }

                    paramvalues.add(iprsres.get("field48"));
                }
            }
            Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = DatabaseConnections.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                while (rslt.next()) {
                    if ("Successful".equalsIgnoreCase(rslt.getString(1))) {
                        inrequest.put("field39", "00");
                        inrequest.put("field126", rslt.getString(2));
                    } else {
                        inrequest.put("field39", "57");
                    }
                    inrequest.put("field48", rslt.getString(1));
                }
                rslt.close();
                con.close();
                connect.initialContext.close();
                // Send SMS
            }

            // ..Remember parameters below are important
            if (!inrequest.get("field48").toString().contains("exist")) {
                SendSMS(inrequest);
            }

        } catch (JSONException | SQLException | NamingException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ProcessRegistration", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        LogTransaction((HashMap) inrequest.toMap());
        return inrequest;
    }

    /**
     * This function receives a json object Card Linking request from channel i.e USSD & APP
     * and invokes the stored procedure SP_LINKCARDS. The function returns a json object 
     * which is sent back to channel.
     * @param inrequest
     * @return
     */
    public JSONObject ProcessCardLinking(JSONObject inrequest) {
        try {
            ArrayList<String> params = new ArrayList<>();
            params.add("MWALLETACCOUNT");
            params.add("CARDNO");
            params.add("TYPE");
            params.add("AGENTID");
            params.add("CARD_EXPIRY");
            params.add("CARD_ALIAS");

            ArrayList<String> paramvalues = new ArrayList<>();
            paramvalues.add(inrequest.get("field102").toString());
            paramvalues.add(inrequest.get("field2").toString());

            if (inrequest.has("field128")) {
                paramvalues.add(inrequest.get("field128").toString());
            } else {
                paramvalues.add("");
            }

            if (inrequest.has("field101")) {
                paramvalues.add(inrequest.get("field101").toString());
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field119")) {
                paramvalues.add(inrequest.get("field119").toString());
            } else {
                paramvalues.add("");
            }
            if (inrequest.has("field120")) {
                paramvalues.add(inrequest.get("field120").toString());
            } else {
                paramvalues.add("");
            }

            String sql = "SP_LINKCARDS";
            inrequest = ProcessSPReturnrslt(inrequest, params, paramvalues, sql);
            // Send SMS
            if ("00".equals(inrequest.get("field39"))) {
                SendSMS(inrequest);
            }

        } catch (JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ProcessCardLinking", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        LogTransaction((HashMap) inrequest.toMap());
        return inrequest;
    }
/**
 *  This function invokes a stored procedure and returns result set which is then loaded to json object.
 *  The json object returned is sent as a response back to channel.
 * @param inrequest
 * @param params
 * @param paramvalues
 * @param sql
 * @return 
 */
    private JSONObject ProcessSPReturnrslt(JSONObject inrequest, ArrayList<String> params, ArrayList<String> paramvalues, String sql) {
        try {
            Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = DatabaseConnections.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                while (rslt.next()) {
                    if ("Successful".equalsIgnoreCase(rslt.getString(1))) {
                        inrequest.put("field39", "00");
                    } else {
                        inrequest.put("field39", "57");
                    }
                    inrequest.put("field48", rslt.getString(1));

                }
                rslt.close();
                con.close();
                connect.initialContext.close();

            }
        } catch (SQLException | JSONException | NamingException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ProcessSPReturnrslt", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function receives a json object from channel with the necessary fields and does a DB update
     * and returns a json object which is sent back to channel.
     * @param inrequest
     * @return
     */
    public JSONObject ProcessUpdateUserDetails(JSONObject inrequest) {
        try {
            String Condition = inrequest.get("field72").toString();
            String Values = inrequest.get("field73").toString();

            String mycondition = "WHERE ";
            String[] cond = Condition.split("\\|");

            for (int i = 0; i < cond.length; i++) {
                String[] thiscondition = cond[i].split("=");
                if (i == 0) {
                    mycondition = mycondition + thiscondition[0] + "='" + thiscondition[1] + "'";
                } else {
                    mycondition = mycondition + " AND " + thiscondition[0] + "='" + thiscondition[1] + "'";
                }

            }
            String myvalues = "";
            String[] val = Values.split("\\|");
            for (int j = 0; j < val.length; j++) {
                String[] thisval = val[j].split("=");
                if (j == 0) {
                    myvalues = myvalues + thisval[0] + "='" + thisval[1] + "'";
                } else {
                    myvalues = myvalues + "," + thisval[0] + "='" + thisval[1] + "'";
                }

            }

            String table = "VW_ALLCUSTOMERS";
            if (inrequest.has("TYPE")) {
                table = "TBAGENTS";
            }
            String sql = "SELECT TOP(1) COUNT(*) AS CN FROM " + table + " " + mycondition;
            String exists = connect.ExecuteQueryStringValue(sql, "", "CN");
            if (Integer.parseInt(exists) <= 0) {
                inrequest.put("field39", "57");
                inrequest.put("field48", "Customer Details do not exist");
            } else {
                sql = "UPDATE " + table + " SET " + myvalues + " " + mycondition;
                connect.ExecuteUpdate(sql);
                inrequest.put("field39", "00");
                inrequest.put("field48", "Successful");
            }

        } catch (JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ProcessUpdateUserDetails", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        LogTransaction((HashMap) inrequest.toMap());
        return inrequest;
    }

    /**
     * This function receives a json object from channel does Pin Reset and return
     * json object with either failure or success which in turn sent to channel.
     * @param inrequest
     * @return
     */
    public JSONObject ProcesspwdReset(JSONObject inrequest) {
        try {
            inrequest.put("field72", "MWALLETACCOUNT=" + inrequest.get("field2").toString());
            int fromRange = 0;
            int toRange = 4;
            String pin = ClassImportantValues.GenerateRandomNo().substring(fromRange, toRange);
            inrequest.put("field88", pin);
            String base64 = Base64.encodeBase64String((pin + inrequest.get("field2").toString()).getBytes());
            pin = ConfigSession.cl.HmacHash(base64, "secret", "HmacSHA256");
            inrequest.put("field73", "PIN=" + pin);
            inrequest = ProcessUpdateUserDetails(inrequest);
            if (inrequest.has("field39")) {
                if ("00".equals(inrequest.get("field39"))) {
                    SendSMS(inrequest);
                }
            }
        } catch (JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ProcesspwdReset", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function Sends a soap request to Payment Gateway IPRS adapter and returns a json object 
     * which is sent to channel.
     * @param Number
     * @param Surname
     * @param First_Name
     * @param Other_Name
     * @return
     */
    public static HashMap<String, String> ProcessIPRSSoap(String Number, String Surname, String First_Name,
            String Other_Name) {
        HashMap<String, String> response = new HashMap<>();
        String IPRS_URL = ConfigSession.IPRS_URL;
        try {
            String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:main=\"http://main.iprsadaptor.eclectics.com/\">"
                    + System.lineSeparator() + "    <soapenv:Header/>" + System.lineSeparator() + "    <soapenv:Body>"
                    + System.lineSeparator() + "        <main:iprsIncomingRequest>" + System.lineSeparator()
                    + "            <clientID>" + ConfigSession.IPRSclientID + "</clientID>" + System.lineSeparator()
                    + "            <username>" + ConfigSession.IPRSusr + "</username>" + System.lineSeparator()
                    + "            <password>" + ConfigSession.IPRSpwd + "</password>" + System.lineSeparator()
                    + "            <salt>" + ConfigSession.IPRSsalt + "</salt>" + System.lineSeparator()
                    + "            <IDNumber>" + Number + "</IDNumber>" + System.lineSeparator()
                    + "            <passport/>" + System.lineSeparator() + "        </main:iprsIncomingRequest>"
                    + System.lineSeparator() + "    </soapenv:Body>" + System.lineSeparator() + "</soapenv:Envelope>";
            ClassImportantValues.LogErrors("IPRSRequest", request);
            String res = HTTPCalls.POSTRequest(IPRS_URL, request);
            ClassImportantValues.LogErrors("IPRSResponse", res);
            res = res.replace(
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:iprsIncomingRequestResponse xmlns:ns2=\"http://main.iprsadaptor.eclectics.com/\">",
                    "");
            res = res.replace("</ns2:iprsIncomingRequestResponse></soap:Body></soap:Envelope>", "");
            // <soap:Envelope
            // xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ns2:iprsIncomingRequestResponse
            // xmlns:ns2="http://main.iprsadaptor.eclectics.com/"><return>{"Photo":"{}","Gender":"M","Place_of_Birth":"{}","ID_Number":"29914297","First_Name":"STEPHEN","RegOffice":"{}","Ethnic_Group":"{}","Clan":"{}","Serial_Number":"{}","Place_of_Death":"{}","Place_of_Live":"{}","Fingerprint":"{}","Date_of_Birth":"5/10/1993
            // 12:00:00
            // AM","Citizenship":"{}","Date_of_Issue":"{}","Occupation":"{}","Pin":"{}","Signature":"{}","Date_of_Death":"{}","Family":"{}","Other_Name":"OMONDI","ErrorOcurred":"false","ErrorCode":"{}","ErrorMessage":"{}","Surname":"ASINGO"}</return></ns2:iprsIncomingRequestResponse></soap:Body></soap:Envelope>
            HashMap irpres = (HashMap) ClassImportantValues.createMapFromXML("return", res);
            String jsonstring = irpres.get("#text").toString();
            JSONObject iprsjsonres = new JSONObject(jsonstring);

            if (iprsjsonres.has("ErrorOcurred") && (!"true".equals(iprsjsonres.get("ErrorOcurred")))) {
                String Fullname = iprsjsonres.get("Surname").toString() + " " + iprsjsonres.get("First_Name").toString()
                        + " " + iprsjsonres.get("Other_Name");
                Fullname = Fullname.replace("'", "");
                // Should match all names in whatever order
                if (!Fullname.contains(First_Name.toUpperCase()) || !Fullname.contains(Surname.toUpperCase())) {
                    response.put("field39", "01");
                    response.put("field48",
                            "Your names do not match you National ID names. Please confirm and try again");
                    response.put("Surname", iprsjsonres.get("Surname").toString());
                    response.put("First_Name", iprsjsonres.get("First_Name").toString());
                    response.put("Other_Name", iprsjsonres.get("Other_Name").toString());
                } else {
                    response.put("field39", "00");
                }

            } else {
                response.put("field39", "57");
                response.put("field48", iprsjsonres.get("ErrorMessage").toString());
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ProcessIPRS", sw.toString());
            response.put("field39", "01");
            response.put("field48", "Please try again. Contact Huduma Customer Care if error persists.");
            return response;
        }
        return response;
    }

    /**
     * This function though disabled, was meant to send requests to Payment Gateway IPRS node and return a json
     * object.
     * @param Number
     * @param Surname
     * @param First_Name
     * @param Other_Name
     * @param request
     * @return
     */
//    public HashMap ProcessIPRSRest(String Number, String Surname, String First_Name, String Other_Name,
//            JSONObject request) {
//        String IPRS_URL = ConfigSession.PGIPRSENDPOINT;
//        JSONObject mergedresponse = null;
//        try {
//            String password = ConfigSession.cl.HmacHash(Base64.encodeBase64String(ConfigSession.PGPWD.getBytes()),
//                    ConfigSession.PGKEY, "HmacSHA512");
//            request.put("amount", "0");
//            request.put("clientid", ConfigSession.PGCLIENTID);
//            request.put("accountno", request.get("field2"));
//            request.put("currencycode", ConfigSession.CURRENCYCODE);
//            request.put("serviceid", request.get("field91"));
//            request.put("transactionid", request.get("field11").toString() + request.get("field37"));
//            request.put("timestamp", ClassImportantValues.getUTCTimeStamp());
//            request.put("username", ConfigSession.PGUSR);
//            request.put("password", password.toLowerCase());
//            request.put("payload", "{ID:" + Number + "}");
//            ConfigSession.cl.LogErrors("IPRSRequest", request.toString());
//            String res = ConfigSession.httpcals.POSTRequest(IPRS_URL, request.toString());
//            ConfigSession.cl.LogErrors("IPRSResponse", res);
//            JSONObject iprsjsonres = new JSONObject(res);
//
//            mergedresponse = new JSONObject(request, JSONObject.getNames(request));
//            for (String key : JSONObject.getNames(iprsjsonres)) {
//                mergedresponse.put(key, iprsjsonres.get(key));
//            }
//
//            if (mergedresponse.has("ErrorOcurred") && (!"true".equals(mergedresponse.get("ErrorOcurred")))) {
//                String Fullname = mergedresponse.get("Surname").toString() + " "
//                        + mergedresponse.get("First_Name").toString() + " " + mergedresponse.get("Other_Name");
//                Fullname = Fullname.replace("'", "");
//                // Should match all names in whatever order
//                if (!Fullname.contains(First_Name.toUpperCase()) || !Fullname.contains(Surname.toUpperCase())) {
//                    mergedresponse.put("field39", "01");
//                    mergedresponse.put("field48",
//                            "Your names do not match you National ID names. Please confirm and try again");
//                    mergedresponse.put("Surname", mergedresponse.get("Surname"));
//                    mergedresponse.put("First_Name", mergedresponse.get("First_Name"));
//                    mergedresponse.put("Other_Name", mergedresponse.get("Other_Name"));
//                } else {
//                    mergedresponse.put("field39", "00");
//                }
//
//            } else {
//                mergedresponse.put("field39", "57");
//                mergedresponse.put("field48", mergedresponse.get("ErrorMessage"));
//            }
//
//        } catch (Exception ex) {
//            StringWriter sw = new StringWriter();
//            ex.printStackTrace(new PrintWriter(sw));
//            ConfigSession.cl.LogErrors("ProcessIPRS", sw.toString());
//            mergedresponse.put("field39", "01");
//            mergedresponse.put("field48", "Please try again. Contact Huduma Customer Care if error persists.");
//            return (HashMap) mergedresponse.toMap();
//        }
//        return (HashMap) mergedresponse.toMap();
//    }
    /**
     * This function receives a Hashmap with channel fields and invokes the stored procedure SP_SAVEMESSAGES.
     * @param inrequest
     */
    public void LogTransaction(HashMap inrequest) {
        ArrayList params = new ArrayList();
        ArrayList paramvalues = new ArrayList();
        Iterator it = inrequest.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry pair = (java.util.Map.Entry) it.next();
            // System.out.println(pair.getKey() + " = " + pair.getValue());
            if (pair.getKey().toString().contains("field")) {
                if (pair.getKey().toString().contains("field0")) {
                    params.add("field1");
                    paramvalues.add(pair.getValue());
                } else {
                    params.add(pair.getKey());
                    paramvalues.add(pair.getValue());
                }
            }
            // it.remove(); // avoids a ConcurrentModificationException
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
        connect.ExecuteUpdateSP(sql, params, paramvalues);
    }

    /**
     * This function retrieves the service id from TBSERVICES and loads it on field91 of the json object returned.
     * The service is important while sending the request to Payment Gateway.
     * @param serviceCode
     * @param selectcolumn
     * @param inrequest
     * @param clause
     * @return
     */
    public JSONObject getBillService(String serviceCode, String selectcolumn, JSONObject inrequest, String clause) {
        if (inrequest.has("field71")) {
            String serviceName = "";
            try {
                String sql = "SELECT " + selectcolumn + " FROM TBSERVICES WHERE " + clause + "='" + serviceCode + "'";

                serviceName = connect.ExecuteQueryStringValue(sql, "", selectcolumn);
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ClassImportantValues.LogErrors("getServiceID", sw.toString());
            }
            if (!ClassImportantValues.empty(serviceName)) {
                inrequest.put("field91", serviceName);
            }
        }
        return inrequest;
    }

    /**
     * This function retrieves the bank id and bank code from TBBANKS and 
     * loads the two as field91 and field92 respectively on the json object returned.
     * @param BankCode
     * @param inrequest
     * @return
     */
    public JSONObject getBankID(String BankCode, JSONObject inrequest) {
        ResultSet rslt = null;
        if (inrequest.has("field71")) {
            if ("BANK".equals(inrequest.get("field71"))) {
                try {
                    String sql = "SELECT * FROM TBBANKS WHERE BankCode='" + BankCode + "'";
                    try (Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE)) {
                        rslt = connect.ExecuteQueryReturnString(con, sql);
                        while (rslt.next()) {
                            inrequest.put("field91", rslt.getString("BankID"));
                            inrequest.put("field92", rslt.getString("BankName"));
                        }
                        rslt.close();
                        con.close();
                        connect.initialContext.close();
                    }
                } catch (SQLException | JSONException | NamingException ex) {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    ClassImportantValues.LogErrors("getBankID", sw.toString());
                }
            }
        }
        return inrequest;
    }

    /**
     * This function retrieves customer name from TBCUSTOMERS. This name is used while sending SMS.
     * It also retrieves DR account when the transaction has Wallet ID as field102.
     * @param inrequest
     * @param column
     * @return
     */
    public JSONObject getWalletAccount(JSONObject inrequest, String column) {
        String sql = "";
        String response = "";
        int fromRange = 1;
        int toRange = 10;
        try {
            if (inrequest.has("field102")) {
                if (inrequest.get("field102").toString().length() == 10) {
                    inrequest.put("field102",
                            "254" + inrequest.get("field102").toString().substring(fromRange, toRange));
                }
                if (inrequest.get("field102").toString().contains("HD")) {
                    sql = "SELECT " + column + " FROM TBACCOUNT WHERE CUSTOMERNO='" + inrequest.get("field102") + "'";
                    response = connect.ExecuteQueryStringValue(sql, "", column);
                    if (!ClassImportantValues.empty(response)) { // if empty then no need to replace
                        inrequest.put("field102", response);
                    }
                    sql = "SELECT ACCOUNTNAME FROM TBCUSTOMERS WHERE MWALLETACCOUNT='" + inrequest.get("field102")
                            + "'";
                    response = connect.ExecuteQueryStringValue(sql, "", "ACCOUNTNAME");
                    inrequest.put("name102", response);
                } else if (inrequest.get("field102").toString().length() == 12) {
                    sql = "SELECT ACCOUNTNAME FROM TBCUSTOMERS WHERE MWALLETACCOUNT='" + inrequest.get("field102")
                            + "'";
                    response = connect.ExecuteQueryStringValue(sql, "", "ACCOUNTNAME");
                    inrequest.put("name102", response);
                }
            }
            inrequest = getWalletAccount103(inrequest, column);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("getWalletAccount", sw.toString());
        }
        return inrequest;
    }

    /**
     * This function retrieves customer name from TBCUSTOMERS. This name is used while sending SMS.
     * It also retrieves CR account when the transaction has Wallet ID as field103.
     * @param inrequest
     * @param column
     * @return
     */
    public JSONObject getWalletAccount103(JSONObject inrequest, String column) {
        String sql = "";
        String response = "";
        int fromRange = 1;
        int toRange = 10;
        try {
            if (inrequest.has("field103")) {
                if (inrequest.get("field103").toString().length() == 10) {
                    inrequest.put("field103",
                            "254" + inrequest.get("field103").toString().substring(fromRange, toRange));
                }
                if (inrequest.get("field103").toString().contains("HD")) {
                    sql = "SELECT " + column + " FROM TBACCOUNT WHERE CUSTOMERNO='" + inrequest.get("field103") + "'";
                    response = connect.ExecuteQueryStringValue(sql, "", column);
                    if (!ClassImportantValues.empty(response)) { // if empty then no need to replace
                        inrequest.put("field103", response);
                    }
                    sql = "SELECT ACCOUNTNAME FROM TBCUSTOMERS WHERE MWALLETACCOUNT='" + inrequest.get("field103")
                            + "'";
                    response = connect.ExecuteQueryStringValue(sql, "", "ACCOUNTNAME");
                    inrequest.put("name103", response);
                } else if (inrequest.get("field103").toString().length() == 12) {
                    sql = "SELECT ACCOUNTNAME FROM TBCUSTOMERS WHERE MWALLETACCOUNT='" + inrequest.get("field103")
                            + "'";
                    response = connect.ExecuteQueryStringValue(sql, "", "ACCOUNTNAME");
                    inrequest.put("name103", response);
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("getWalletAccount", sw.toString());
        }
        return inrequest;
    }

    /**
     * This function receives a json object retrieves the charges by invoking the SQL table function FN_GET_CHARGES
     * and computes the VAT loading the charge and VAT on field28 and field26 on the json object returned.
     * @param inrequest
     * @return
     */
    public JSONObject getCharge(JSONObject inrequest) {
        String TTYPE = "WW";
        if ("400000".equals(inrequest.get("field126"))) {
            if (inrequest.has("CARDCR")) {
                TTYPE = "WC";
            }
            if (inrequest.has("CARDDR")) {
                TTYPE = "CW";
            }
        }
        String sql = "select * from FN_GET_CHARGES(" + "'0200'," + "'" + inrequest.get("field126") + "'," + "'"
                + inrequest.get("field4") + "'," + "'" + inrequest.get("field102") + "'," + "'"
                + inrequest.get("field32") + "'," + "'" + TTYPE + "')";
        String result = connect.ExecuteQueryStringValue(sql, "", "Fee");

        if (ClassImportantValues.empty(result)) {
            result = "0";
        }
        BigDecimal charge = new BigDecimal(result);
        int rnddcpl = 2;
        charge = ClassImportantValues.round(charge, rnddcpl, true);
        int dutyrate = 10;
        BigDecimal vatrate = new BigDecimal(dutyrate);
        BigDecimal VAT = charge.divide(vatrate);

        inrequest.put("field28", charge.toString());
        inrequest.put("field26", VAT.toString());
        inrequest.put("field39", "00");
        inrequest.put("field48", "Successful");
        return inrequest;
    }

    /**
     * This function receives a json object, does a bill presentment to Payment Gateway
     * and returns a json object with fields from PG.
     * @param inrequest
     * @return
     */
    public static JSONObject BillPresentment(JSONObject inrequest) {
        try {
            String res = HTTPCalls.POSTRequest(ConfigSession.BILL_PREURL, inrequest.toString());
            inrequest = new JSONObject(res);
        } catch (IOException | JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("BillPresentment", sw.toString());
        }
        return inrequest;
    }

    /**
     * This function receives a json object and process various requests i.e horoscope 
     * and Bible verse of the day from USSD channel.
     * @param inrequest
     * @return
     */
    public static JSONObject USSDOnlineRequests(JSONObject inrequest) {
        JSONObject mergedresponse = null;
        JSONObject response = null;

        switch (inrequest.get("field100").toString()) {
            case "horoscope":
                inrequest.put("type", inrequest.get("field100"));
                inrequest.put("request", inrequest.get("field70"));
                break;
            default:
                inrequest.put("type", inrequest.get("field100"));
                inrequest.put("request", inrequest.get("field70"));
                break;
        }
        try {
            String res = ConfigSession.httpcals.httpsPOST(ConfigSession.USSDOnlineRequest, inrequest.toString());
            response = new JSONObject(res);

            mergedresponse = new JSONObject(inrequest, JSONObject.getNames(inrequest));
            for (String key : JSONObject.getNames(response)) {
                mergedresponse.put(key, response.get(key));
            }
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException | JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("USSDOnlineRequests", sw.toString());
        }
        // \u2014
        mergedresponse.put("field70", mergedresponse.get("field48").toString().replace("’", "'")
                .replace("\u201c", "'").replace("\u201d", "'"));
        mergedresponse.put("field65", inrequest.get("field102").toString());
        mergedresponse.put("field39", "00");
        mergedresponse.put("field48", "Successful");
        return mergedresponse;
    }

    /**
     * This function retrieves the card profile by invoking SP_GETCARDPROFILE.
     * @param inrequest
     * @return
     */
    public JSONObject getCardProfile(JSONObject inrequest) {
        int f481 = 1;
        int f482 = 2;
        try {
            ArrayList<String> params = new ArrayList<>();
            params.add("CARDNO");

            ArrayList<String> paramvalues = new ArrayList<>();
            paramvalues.add(inrequest.get("field102").toString());

            String sql = "SP_GETCARDPROFILE";
            Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = DatabaseConnections.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                while (rslt.next()) {
                    if ("Successful".equalsIgnoreCase(rslt.getString(1))) {
                        inrequest.put("field39", "00");
                        inrequest.put("field48", rslt.getString(f482));
                    } else {
                        inrequest.put("field39", "57");
                        inrequest.put("field48", rslt.getString(f481));
                    }

                }
                rslt.close();
                con.close();
                connect.initialContext.close();
            }

        } catch (JSONException | SQLException | NamingException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("getCardProfile", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        LogTransaction((HashMap) inrequest.toMap());
        return inrequest;
    }

    /**
     * This function checks if a customer is already registered by invoking the stored procedure SP_CHECKCUSTOMER.
     * @param inrequest
     * @return
     */
    public JSONObject CheckCustomer(JSONObject inrequest) {
        try {
            ArrayList<String> params = new ArrayList<>();
            params.add("MWALLETACCOUNT");

            ArrayList<String> paramvalues = new ArrayList<>();
            paramvalues.add(inrequest.get("field2").toString());

            String sql = "SP_CHECKCUSTOMER";
            Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = DatabaseConnections.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                while (rslt.next()) {
                    if ("Successful".equalsIgnoreCase(rslt.getString(1))) {
                        inrequest.put("field99", "00");
                        inrequest.put("field48", rslt.getString(1));
                    } else {
                        inrequest.put("field99", "57");
                        inrequest.put("field48", rslt.getString(1));
                    }

                }
                rslt.close();
                con.close();
                connect.initialContext.close();
            }

        } catch (JSONException | SQLException | NamingException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("CheckCustomer", sw.toString());
            inrequest.put("field99", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    /**
     * This function send SMS by writing a request to topic with message selector as messageDest'SMS'.
     * SMS adaptor in turn picks the messages on the topic with that selector and sends it to PG SMS adaptor.
     * @param inrequest
     */
    public static void SendSMS(JSONObject inrequest) {
        try {
            inrequest.put("messageDest", "SMS");
            inrequest.put("TXN", "Yes");
            inrequest.put("CorrelationID", UUID.randomUUID().toString());
            TopicWriter topicwriter = new TopicWriter(ConfigSession.ESBTransaction_Topic, (HashMap) inrequest.toMap());
            topicwriter.sendObjectToTopic(ConfigSession.PROVIDER_URL, ConfigSession.SECURITY_PRINCIPAL,
                    ConfigSession.SECURITY_CREDENTIALS, ConfigSession.JMSConnectionFactory);
        } catch (JSONException | NamingException | JMSException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("SendSMS", sw.toString());
            inrequest.put("field99", "57");
            inrequest.put("field48", "An error occurred!");
        }
    }
}
