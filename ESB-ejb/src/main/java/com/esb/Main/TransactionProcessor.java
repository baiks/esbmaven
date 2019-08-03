/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.naming.NamingException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author adm
 */
public class TransactionProcessor {

    public HashMap POST_MINI_TRANSACTIONS(HashMap inrequest) {
        try {
            ArrayList params = new ArrayList();
            params.add("Field37");
            params.add("iv_MsgType");
            params.add("iv_Field3");
            params.add("iv_Field4");
            params.add("iv_Field7");
            params.add("iv_Field11");
            params.add("iv_Field24");
            params.add("iv_Field32");
            params.add("iv_Field35");
            params.add("iv_Field36");
            params.add("iv_Field90");
            params.add("iv_Field100");
            params.add("iv_Field101");
            params.add("iv_Field102");
            params.add("iv_Field103");
            params.add("iv_Commission");
            params.add("iv_VAT");
            params.add("iv_Field68");
            params.add("iv_CustCurrency");
            params.add("iv_Channel");
            params.add("iv_TerminalID");
            params.add("iv_UserID");
            params.add("iv_TrnCode");
            params.add("iv_Field65");

            ArrayList paramvalues = new ArrayList();

            paramvalues.add(inrequest.get("field37"));  //RRN
            paramvalues.add(inrequest.get("field0"));   //MTI
            paramvalues.add(inrequest.get("field3"));   //Processing code
            paramvalues.add(inrequest.get("field4"));   //Amount
            paramvalues.add(inrequest.get("field7"));   //Date time
            paramvalues.add(inrequest.get("field11"));  //System audit No/Stan
            paramvalues.add(inrequest.get("field24"));  //TXN type i.e MM
            paramvalues.add(inrequest.get("field32"));  //Channel
            if (inrequest.containsKey("field35")) {     //Track 2 data
                paramvalues.add(inrequest.get("field35"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.containsKey("field36")) {     //Remit Token 
                paramvalues.add(inrequest.get("field36"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.containsKey("field90")) {  //Reversal ref
                paramvalues.add(inrequest.get("field90"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.containsKey("field100")) {  //TXN Type
                paramvalues.add(inrequest.get("field100"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.containsKey("field101")) {  //Agent ID
                paramvalues.add(inrequest.get("field101"));
            } else {
                paramvalues.add("");
            }

            if (inrequest.containsKey("field102")) {  //DR account
                paramvalues.add(inrequest.get("field102"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.containsKey("field103")) {  //CR account
                paramvalues.add(inrequest.get("field103"));
            } else {
                paramvalues.add("");
            }
            paramvalues.add(inrequest.get("field28")); //charge
            paramvalues.add(inrequest.get("field26")); //VAT
            paramvalues.add(inrequest.get("field68"));  //Narration
            paramvalues.add(inrequest.get("field49"));  //Currency
            paramvalues.add(inrequest.get("field32"));  //Channel 
            if (inrequest.containsKey("field41")) {     //Terminal ID
                paramvalues.add(inrequest.get("field41"));
            } else {
                paramvalues.add("");
            }
            if (inrequest.containsKey("CARDDR")) {
                paramvalues.add(inrequest.get("CARDDR")); //Not used
            } else {
                paramvalues.add(""); //Not used
            }
            if (inrequest.containsKey("CARDCR")) {
                paramvalues.add(inrequest.get("CARDCR")); //Not used
            } else {
                paramvalues.add(""); //Not used
            }
            if (inrequest.containsKey("field65")) {     //Mobile Recipient
                paramvalues.add(inrequest.get("field65"));
            } else {
                paramvalues.add("");
            }
            String sql = "SP_POST_MINI_TRANSACTIONS";
            Connection con = ConfigSession.connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = ConfigSession.connect.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                while (rslt.next()) {
                    if (rslt.getString("Narration").equalsIgnoreCase("Successful")) {
                        inrequest.put("field39", "00");
                        inrequest.put("field54", rslt.getString("AvailableBal") + "|" + rslt.getString("ActualBal"));
                    } else {
                        inrequest.put("field39", "57");
                    }
                    inrequest.put("field48", rslt.getString("Narration"));

                }
                rslt.close();
                con.close();
                ConfigSession.connect.initialContext.close();
            }
        } catch (SQLException | NamingException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("POST_MINI_TRANSACTIONS", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    public HashMap ProcessMinistatement(HashMap inrequest) {
        try {
            ArrayList params = new ArrayList();
            params.add("MWALLETACCOUNT");

            ArrayList paramvalues = new ArrayList();
            paramvalues.add(inrequest.get("field102"));

            String sql = "SP_GETMINISTATEMENT";
            String MiniHeader;
            String MiniStatement;
            Connection con = ConfigSession.connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = ConfigSession.connect.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                MiniHeader = "DATE_TIME|REF_NUMBER|NARRATION|DRCR|TRAN_AMOUNT";
                MiniStatement = "";
                while (rslt.next()) {
                    if (ConfigSession.cl.empty(MiniStatement)) {
                        MiniStatement = MiniStatement + rslt.getString("ValueDate") + "|"
                                + rslt.getString("TrxRefNo") + "|"
                                + rslt.getString("Narration").split("-")[0] + "|"
                                + rslt.getString("DRCR") + "|"
                                + rslt.getString("Amount");
                    } else {
                        MiniStatement = MiniStatement + "~" + rslt.getString("ValueDate") + "|"
                                + rslt.getString("TrxRefNo") + "|"
                                + rslt.getString("Narration").split("-")[0] + "|"
                                + rslt.getString("DRCR") + "|"
                                + rslt.getString("Amount");
                    }
                }
                rslt.close();
                con.close();
                ConfigSession.connect.initialContext.close();
            }
            if (!ConfigSession.cl.empty(MiniStatement)) {
                MiniStatement = MiniHeader + "~" + MiniStatement;
                inrequest.put("field127", MiniStatement);
            } else {
                inrequest.put("field127", "No mini Data");
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ProcessMinistatement", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    public HashMap ProcessFullstatement(HashMap inrequest) {
        try {
            ArrayList params = new ArrayList();
            params.add("MWALLETACCOUNT");
            if (inrequest.get("field32").equals("APP")) {
                params.add("FROMDATE");
                params.add("TODATE");
                params.add("CHANNEL");
                params.add("USSDOPTION");
            } else {
                params.add("FROMDATE");
                params.add("TODATE");
                params.add("CHANNEL");
                params.add("USSDOPTION");
            }

            ArrayList paramvalues = new ArrayList();
            paramvalues.add(inrequest.get("field102"));
            if (inrequest.get("field32").equals("APP")) {
                paramvalues.add(inrequest.get("field71"));
                paramvalues.add(inrequest.get("field72"));
                paramvalues.add(inrequest.get("field32"));
                paramvalues.add("0");
            } else {
                paramvalues.add("0");
                paramvalues.add("0");
                paramvalues.add(inrequest.get("field32"));
                paramvalues.add(inrequest.get("field71"));
            }

            String sql = "SP_GETFULLSTATEMENT";
            String MiniHeader;
            String MiniStatement;
            Connection con = ConfigSession.connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = ConfigSession.connect.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                MiniHeader = "DATE|REF NUMBER|NARRATION|DRCR|AMOUNT";
                MiniStatement = "";
                while (rslt.next()) {
                    if (ConfigSession.cl.empty(MiniStatement)) {
                        MiniStatement = MiniStatement + rslt.getString("ValueDate") + "|"
                                + rslt.getString("TrxRefNo") + "|"
                                + rslt.getString("Narration") + "|"
                                + rslt.getString("DRCR") + "|"
                                + rslt.getString("Amount");
                    } else {
                        MiniStatement = MiniStatement + "~" + rslt.getString("ValueDate") + "|"
                                + rslt.getString("TrxRefNo") + "|"
                                + rslt.getString("Narration") + "|"
                                + rslt.getString("DRCR") + "|"
                                + rslt.getString("Amount");
                    }
                }
                rslt.close();
                con.close();
                ConfigSession.connect.initialContext.close();
            }
            if (!ConfigSession.cl.empty(MiniStatement)) {
                MiniStatement = MiniHeader + "~" + MiniStatement;
                inrequest.put("field127", MiniStatement);
            } else {
                inrequest.put("field127", "No FulStatement");
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ProcessFullstatement", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }

    public HashMap SalaryUploadFullReg(HashMap inrequest) {
        try {
            Random rand = new Random();
            String pin = String.valueOf(rand.nextInt(1000000)).substring(0, 4);
            inrequest.put("field72", pin);
            ArrayList params = new ArrayList();
            params.add("FirstName");
            params.add("SecondName");
            params.add("LastName");
            params.add("PHONENUMBER");
            params.add("IdentificationID");
            params.add("EmailAddress");
            params.add("PIN");
            params.add("DATEOFBIRTH");
            params.add("GENDER");
            params.add("INSTITUTIONID");

            ArrayList paramvalues = new ArrayList();

            paramvalues.add(inrequest.get("field61"));
            paramvalues.add(inrequest.get("field62"));
            paramvalues.add(inrequest.get("field63"));
            paramvalues.add(inrequest.get("field2"));
            paramvalues.add(inrequest.get("field70"));
            paramvalues.add(inrequest.get("field71"));
            paramvalues.add(ConfigSession.cl.HmacHash(Base64.encodeBase64String((pin + inrequest.get("field2")).getBytes()), "secret", "HmacSHA256"));
            paramvalues.add(inrequest.get("field75"));
            paramvalues.add(inrequest.get("field76"));
            paramvalues.add(inrequest.get("field77"));

            String sql = "SP_SALARYUPLOADS";
            Connection con = ConfigSession.connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            try (ResultSet rslt = ConfigSession.connect.ExecuteSPReturnString(con, sql, params, paramvalues)) {
                while (rslt.next()) {
                    if (rslt.getString("Narration").equalsIgnoreCase("Successful")) {
                        inrequest.put("field39", "00");
                        inrequest.put("CUSTOMEREXIST", rslt.getString("CUSTOMEREXIST"));
                    } else {
                        inrequest.put("field39", "57");
                    }
                    inrequest.put("field48", rslt.getString("Narration"));

                }
                rslt.close();
                con.close();
                ConfigSession.connect.initialContext.close();
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("SalaryUploadFullReg", sw.toString());
            inrequest.put("field39", "57");
            inrequest.put("field48", "An error occurred!");
        }
        return inrequest;
    }
}
