/* NB:

Code in this will be enabled only in debug mode.

Note and note carefully! I am not responsible for any defects araising from this file.

 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import com.esb.Main.ConfigSession;
import com.esb.common.ClassImportantValues;
import com.esb.common.HTTPCalls;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adm
 */
public class Test {

    ClassImportantValues cl = new ClassImportantValues();
    private static final Logger LOGGER = Logger.getLogger(Test.class.getName());

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Test tests = new Test();
            String request = tests.BalanceInquiry().toString();
            System.err.println("Outgoing:: " + request);
            HTTPCalls httpcalls = new HTTPCalls();
            String incoming = httpcalls.POSTRequest("http://localhost:8080/ESB-web/ESBApi", Base64.encodeBase64String(request.getBytes("UTF-8")));
            //String incoming = httpcalls.POSTRequest("http://localhost:8080/ESB_Huduma-war/HudumaServlet", Base64.encodeBase64String(request.getBytes("UTF-8")));
            System.err.println("Incoming:: " + new String(Base64.decodeBase64(incoming)));
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void FullStatementEmail() {
        try {
            HTTPCalls httpcalls = new HTTPCalls();
            String full = "DATE|REF NUMBER|NARRATION|DRCR|AMOUNT~2017-09-15|000000804618|MPESA C2B|C|10.00~2017-09-15|000000076181|EXCISE DUTY|D|1.00~2017-09-15|000000076181|CHARGE|D|10.00~2017-09-14|000848877|EXCISE DUTY|D|1.00~2017-09-14|000848877|CHARGE|D|10.00~2017-09-14|000000030476|MPESA C2B|C|10.00~2017-09-14|000798508|EXCISE DUTY|D|1.00~2017-09-14|000798508|CHARGE|D|10.00~2017-09-14|000679507|EXCISE DUTY|D|1.00~2017-09-14|000679507|CHARGE|D|10.00~2017-09-13|000587962|REVERSAL|C|2.50~2017-09-13|000587962|REVERSAL|C|25.00~2017-09-13|000587962|REVERSAL|C|10.00~2017-09-13|000587962|EXCISE DUTY|D|2.50~2017-09-13|000587962|CHARGE|D|25.00~2017-09-13|000587962|AIRTEL B2C|D|10.00~2017-09-13|000343124|FUNDS TRANSFER|C|45.00~2017-09-13|000299429|REVERSAL|C|2.50~2017-09-13|000299429|REVERSAL|C|25.00~2017-09-13|000299429|REVERSAL|C|10.00~2017-09-13|000299429|EXCISE DUTY|D|2.50~2017-09-13|000299429|CHARGE|D|25.00~2017-09-13|000299429|MPESA B2C|D|10.00~2017-09-13|000235651|REVERSAL|C|2.50~2017-09-13|000235651|REVERSAL|C|25.00~2017-09-13|000235651|REVERSAL|C|10.00~2017-09-13|000235651|EXCISE DUTY|D|2.50~2017-09-13|000235651|CHARGE|D|25.00~2017-09-13|000235651|MPESA B2C|D|10.00~2017-09-13|000658169|EXCISE DUTY|D|1.00~2017-09-13|000658169|CHARGE|D|10.00~2017-09-13|000000623328|MPESA C2B|C|10.00~2017-09-13|000535376|EXCISE DUTY|D|1.00~2017-09-13|000535376|CHARGE|D|10.00~2017-09-13|000535376|REMMITTANCE INITIATION|D|100.00~2017-09-13|000403566|EXCISE DUTY|D|1.00~2017-09-13|000403566|CHARGE|D|10.00~2017-09-12|000874871|EXCISE DUTY|D|1.00~2017-09-12|000874871|CHARGE|D|10.00~2017-09-12|000161566|EXCISE DUTY|D|0.50~2017-09-12|000161566|CHARGE|D|5.00~2017-09-12|000161566|KPLC           |D|100.00~2017-09-12|000206917|REVERSAL|C|2.50~2017-09-12|000206917|REVERSAL|C|25.00~2017-09-12|000206917|REVERSAL|C|10.00~2017-09-12|000206917|EXCISE DUTY|D|2.50~2017-09-12|000206917|CHARGE|D|25.00~2017-09-12|000206917|MPESA B2C|D|10.00~2017-09-12|000356205|EXCISE DUTY|D|1.00~2017-09-12|000356205|CHARGE|D|10.00~2017-09-12|000646407|EXCISE DUTY|D|1.00~2017-09-12|000646407|CHARGE|D|10.00~2017-09-12|000967350|EXCISE DUTY|D|1.00~2017-09-12|000967350|CHARGE|D|10.00~2017-09-12|000000531287|EXCISE DUTY|D|0.50~2017-09-12|000000531287|CHARGE|D|5.00~2017-09-12|000000531287|KPLC           |D|300.00~2017-09-12|000000337760|REVERSAL|C|2.50~2017-09-12|000000337760|REVERSAL|C|25.00~2017-09-12|000000337760|REVERSAL|C|10.00~2017-09-12|000000337760|EXCISE DUTY|D|2.50~2017-09-12|000000337760|CHARGE|D|25.00~2017-09-12|000000337760|MPESA B2C|D|10.00~2017-09-12|000898896|EXCISE DUTY|D|1.00~2017-09-12|000898896|CHARGE|D|10.00~2017-09-12|000568888|EXCISE DUTY|D|1.00~2017-09-12|000568888|CHARGE|D|10.00~2017-09-12|000000138959|EXCISE DUTY|D|1.00~2017-09-12|000000138959|CHARGE|D|10.00~2017-09-12|000000113582|EXCISE DUTY|D|1.00~2017-09-12|000000113582|CHARGE|D|10.00~2017-09-12|000815081|EXCISE DUTY|D|1.00~2017-09-12|000815081|CHARGE|D|10.00~2017-09-12|000794640|EXCISE DUTY|D|1.00~2017-09-12|000794640|CHARGE|D|10.00~2017-09-09|000255123|REVERSAL|C|2.50~2017-09-09|000255123|REVERSAL|C|25.00~2017-09-09|000255123|REVERSAL|C|10.00~2017-09-09|000255123|EXCISE DUTY|D|2.50~2017-09-09|000255123|CHARGE|D|25.00~2017-09-09|000255123|MPESA B2C|D|10.00~2017-09-08|000370762|EXCISE DUTY|D|1.00~2017-09-08|000370762|CHARGE|D|10.00~2017-09-08|000584985|EXCISE DUTY|D|1.00~2017-09-08|000584985|CHARGE|D|10.00~2017-09-08|000165617|EXCISE DUTY|D|1.00~2017-09-08|000165617|CHARGE|D|10.00~2017-09-08|000165830|EXCISE DUTY|D|1.00~2017-09-08|000165830|CHARGE|D|10.00~2017-09-07|000966586|EXCISE DUTY|D|1.00~2017-09-07|000966586|CHARGE|D|10.00~2017-09-07|000217816|REVERSAL|C|2.50~2017-09-07|000217816|REVERSAL|C|25.00~2017-09-07|000217816|REVERSAL|C|10.00~2017-09-07|000217816|EXCISE DUTY|D|2.50~2017-09-07|000217816|CHARGE|D|25.00~2017-09-07|000217816|MPESA B2C|D|10.00~2017-09-07|000709816|EXCISE DUTY|D|1.00~2017-09-07|000709816|CHARGE|D|10.00~2017-09-07|000275936|EXCISE DUTY|D|1.00~2017-09-07|000275936|CHARGE|D|10.00";
            String rqst = "{\"trntype\":\"transact\",\"field0\":\"0200\",\"field2\":\"254724017787\",\"field3\":\"120000\",\"field7\":\"0307165232\",\"field11\":\"717478\",\"field32\":\"APP\",\"field37\":\"000377196\",\"field49\":\"KE\",\"field61\":\"paul\",\"field62\":\"kabaiku\",\"field63\":\"kimani\",\"field70\":\"29298284\",\"field71\":\"paulkabaiku023@gmail.com\",\"field72\":\"070425d8e2e2c4a44cf9e8325bdfab6ef9b1b22b5d582f78fe542ddc71ff7904\",\"field73\":\"en\",\"field74\":\"89254021014023308854\",\"field75\":\"1988-01-01\",\"field76\":\"Male\",\"field128\":\"89254021014023308854\"}";
            JSONObject request = new JSONObject(rqst);
            request.put("is_statement", true);
            request.put("to_email", "SOndere@hudumakenya.go.ke");
            request.put("account", "254724017787");
            request.put("data", full);
            request.put("message", "2017-08-23 to 2017-08-28");
            request.put("subject", "Huduma Wallet Full Statement");
            System.out.println("Outgoing: " + request.toString());
            String url = "http://localhost:809/HudumaEmailService/EmailService.php";
            String incoming = "";
            //incoming = httpcalls.POSTRequest(url, request.toString());

            url = "https://10.20.2.6:8085/HudumaEmailService/EmailService.php";
            incoming = httpcalls.httpsPOST(url, request.toString());
            System.out.println("Incoming: " + incoming);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    public void USSDOnlineRequests() {
        try {
            HTTPCalls httpcalls = new HTTPCalls();
            JSONObject request = new JSONObject();
            request.put("type", "quote");
            request.put("request", "quote");

            System.out.println("Outgoing: " + request.toString());
            String url = "http://localhost:809/HudumaEmailService/USSDRequests.php";
            String incoming = "";
            incoming = httpcalls.POSTRequest(url, request.toString());

            url = "https://10.20.2.6:8085/HudumaEmailService/USSDRequestsOnline.php";
            incoming = httpcalls.httpsPOST(url, request.toString());
            System.out.println("Incoming: " + incoming);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    public void MainSend() {
        try {
            HTTPCalls httpcalls = new HTTPCalls();
            String url = "http://localhost:8686/ESB_Huduma-war/HudumaServlet";
            url = "http://10.20.2.23:8080/ESB_Huduma-war/HudumaServlet";
            //url = "https://testgateway.ekenya.co.ke:8443/wrappers/hudumaUssdWrapper.php";
            //url = "https://portal.ekenya.co.ke:8085/hudumaApp/hudumaAppWrapper.php";
//            String base64 = Base64.encodeBase64String("4545254724017787".getBytes());
//            System.out.println("PIN:" + cl.HmacHash(base64, "secret", "HmacSHA256"));
            Test test = new Test();
            System.out.println("Outgoing: " + test.WalletToMobile());
            String rqst = Base64.encodeBase64String(test.WalletToMobile().toString().getBytes());
            System.out.println("Base64 Econded: " + rqst);
            String incoming = httpcalls.POSTRequest(url, rqst);
            //String incoming = httpcalls.httpsPOST(url, rqst);
            incoming = new String(Base64.decodeBase64(incoming), "UTF-8");
            System.out.println("Incoming: " + incoming);
            JSONObject Response = new JSONObject(incoming);
            System.out.println("Rescode: " + Response.get("field48"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public JSONObject FTFROMBANKS() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "400000");
        jsonobject.put("field4", "100");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "FUNDS TRANSFER");
        //jsonobject.put("field71", "BANK");
        jsonobject.put("field100", "BANK");
        jsonobject.put("field103", "254724017787"); //WALLET ACCOUNT/POOL Account
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject CustomerLogin() {
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("field0", "0200");
        jsonobject.put("field3", "110000");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field37", f37);
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject AgentLogin() {
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("field0", "0200");
        jsonobject.put("field3", "110000");
        jsonobject.put("field101", "1000014");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("TYPE", "AGENT");
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field37", f37);
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject UpdateUserDetails() {
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("field0", "0200");
        jsonobject.put("field3", "140000");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field72", "MWALLETACCOUNT=254724017787");
        jsonobject.put("field73", "FIRSTLOGIN=0");
        jsonobject.put("field32", "USSD");
        //jsonobject.put("TYPE", "AGENT");
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field37", f37);
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject Registration() {
        JSONObject jsonobject = new JSONObject();
        try {
            Random rand = new Random();
            String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
            jsonobject.put("field37", f37);
            jsonobject.put("field0", "0200");
            jsonobject.put("field3", "120000");
            jsonobject.put("field2", "254724017787");
            jsonobject.put("field7", LocalDateTime.now());
            jsonobject.put("field11", LocalDateTime.now());
            jsonobject.put("field32", "CPS");
            jsonobject.put("field61", "Kimani");
            jsonobject.put("field62", "Paul");
            jsonobject.put("field63", "Kabaiku");
            jsonobject.put("field70", "29298284");
            jsonobject.put("field71", "paulkabaiku023@gmail.com");
            jsonobject.put("field72", "b10d34e22e5040a9565e9ed5f971159022656805b87961894cbde538071e73be");
            jsonobject.put("field73", "en");
            jsonobject.put("field74", "1232938209382");
            jsonobject.put("field75", "03-03-1993");
            jsonobject.put("field76", "Male");
            jsonobject.put("field77", "Nairobi");
            jsonobject.put("field78", "254 Nairobi");
            jsonobject.put("field79", "Kenya");
            jsonobject.put("field80", "Kenya");
            jsonobject.put("field81", "Kenya");
            jsonobject.put("field128", "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject BalanceInquiry() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "310000");
        jsonobject.put("field4", "0");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "BALANCE INQUIRY");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject CardLinking() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "S000000000002");
        jsonobject.put("field3", "130000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "CARD LINKING");
        jsonobject.put("field101", "1000072");
        jsonobject.put("field102", "254724017787");
        jsonobject.put("field128", "WALLET");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject MiniStatement() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "380000");
        jsonobject.put("field4", "0");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "MINI-STATEMENT");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject FT() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "400000");
        jsonobject.put("field4", "50");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "APP");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "FUNDS TRANSFER");
        jsonobject.put("field102", "254724017787");
        jsonobject.put("field103", "0716630770");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject FTTOBANKS() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "400000");
        jsonobject.put("field4", "100");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field65", "080002406"); //Bank Account
        jsonobject.put("field35", "BANK76");      //Bank ID
        jsonobject.put("field85", "Karue Kennedy");      //Beneficiary Name
        jsonobject.put("field86", "254724017787");      //Beneficiary Number
        jsonobject.put("field68", "FUNDS TRANSFER");
        jsonobject.put("field71", "BANK");
        jsonobject.put("field100", "BANK");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject WalletToMobile() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "430000");
        jsonobject.put("field4", "2300");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field65", "254724017787");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field71", "MPB2C");
        jsonobject.put("field100", "MPB2C");
        jsonobject.put("field68", "Wallet To Mobile " + jsonobject.get("field100"));
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject MobileToWallet() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "450000");
        jsonobject.put("field4", "100");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field65", "254724017787");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field71", "MPC2B");
        jsonobject.put("field100", "MPESA");
        jsonobject.put("field68", "Wallet To Mobile " + jsonobject.get("field100"));
        jsonobject.put("field103", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject BillPayment() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "500000");
        jsonobject.put("field4", "300");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field65", "323232232");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field71", "KPLC");
        jsonobject.put("field100", "KPLC");
        jsonobject.put("field68", "Bill Payment " + jsonobject.get("field100"));
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject AirtimePurchase() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "420000");
        jsonobject.put("field4", "300");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field65", "254724017787");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field71", "SAFARICOM");
        jsonobject.put("field68", "Airtime purchase " + jsonobject.get("field71"));
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject SalaryUpload() {
        JSONObject jsonobject = new JSONObject();
        try {
            Random rand = new Random();
            String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
            jsonobject.put("field0", "0200");
            jsonobject.put("field3", "230000");
            jsonobject.put("field2", "254708730788");
            jsonobject.put("field4", "200");
            jsonobject.put("field7", LocalDateTime.now());
            jsonobject.put("field11", LocalDateTime.now());
            jsonobject.put("field24", "MM");
            jsonobject.put("field32", "CPS");
            jsonobject.put("field37", f37);
            jsonobject.put("field49", "KES");
            jsonobject.put("field61", "Karimi");
            jsonobject.put("field62", "Joseph");
            jsonobject.put("field63", "Wamatangi");
            jsonobject.put("field68", "Salary payment for  " + jsonobject.get("field2"));
            jsonobject.put("field70", "28798288");
            jsonobject.put("field71", "wamatangi@gmail.com");
            jsonobject.put("field75", "25-07-1994");
            jsonobject.put("field76", "Male");
            jsonobject.put("field77", "0938092832"); //InstituionID
            jsonobject.put("field102", "254708730788");
            jsonobject.put("field103", "254724017787");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject CDP() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "210000");
        jsonobject.put("field4", "200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "CDP");
        jsonobject.put("field101", "1000014");
        jsonobject.put("field102", "500001000029");
        jsonobject.put("field103", "1000039");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject CWD() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "010000");
        jsonobject.put("field4", "2000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "CWD");
        jsonobject.put("field102", "5500000000000001");
        jsonobject.put("field103", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject getCharges() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "150000");
        jsonobject.put("field4", "2000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "CWD");
        jsonobject.put("field126", "210000");
        jsonobject.put("field102", "5500000000000001");
        jsonobject.put("field103", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject RemitInit() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "620000");
        jsonobject.put("field4", "200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field65", "254708003472");
        jsonobject.put("field68", "Remit Initiation");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject RemitFullFill() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "630000");
        jsonobject.put("field4", "200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field36", "51599298");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field65", "254708003472");
        jsonobject.put("field68", "Remit Fulfillment");
        jsonobject.put("field101", "1000014");
        jsonobject.put("field103", "500001000029");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject BillPresentment() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "160000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field36", "51599298");
        jsonobject.put("field37", f37);
        jsonobject.put("field100", "LANDRATES");
        jsonobject.put("field65", "254708003472");
        jsonobject.put("field68", "Bill Presentment");
        jsonobject.put("field101", "1000014");
        //1. NHIF
        //jsonobject.put("payload", "Stage:presentment,AccountNumber:2011088,Amount:100,IsCorporate:false,PhoneNumber:0724972416,PaymentTypeID:1");
        //2. BIZPERMIT
        //jsonobject.put("payload", "PhoneNumber:25412232323,BusinessID:1316793,PaymentTypeID:1,Stage:presentment");
        //3. LANDRATES
        jsonobject.put("payload", "Stage:presentment,PlotNumber:8285/829,PhoneNumber:0722111222,PaymentTypeID:1");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject PinReset() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "170000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field68", "Pin Reset");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject getCardProfile() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254708730788");
        jsonobject.put("field3", "180000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field68", "Get Card Profile");
        jsonobject.put("field102", "5500000000000012");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject SendSMS() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "190000");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field68", "Send SMS");
        jsonobject.put("field126", "200000");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject FullStatement() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "370000");
        jsonobject.put("field4", "200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "Full Statement");
        jsonobject.put("field71", "12");
        jsonobject.put("field73", "familianoma@gmail.com");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject USSDOnlineRequest() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "410000");
        jsonobject.put("field4", "0");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "Quran Quote of the day");
        jsonobject.put("field70", "Leo");
        jsonobject.put("field100", "horoscope");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject LoanRequest() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "600000");
        jsonobject.put("field4", "1200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "LOAN REQUEST");
        jsonobject.put("field102", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject LoanRepayment() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "254724017787");
        jsonobject.put("field3", "610000");
        jsonobject.put("field4", "1200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "USSD");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("field68", "LOAN REPAYMENT");
        jsonobject.put("field103", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject DiasporaRemitWallet() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "4242424242424242");
        jsonobject.put("field3", "460000");
        jsonobject.put("field4", "200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "APP");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("payload", "{\"txnCurrency\": \"KES\",\"ExpiryDate\": \"042019\",\"CardSecurityCode\": \"706\",\"PAN\": \"4242424242424242\"}");
        jsonobject.put("field65", "254724017787");
        jsonobject.put("field68", "Dispora Remmitance");
        jsonobject.put("field71", "DISPORAREMIT");
        jsonobject.put("field100", "DISPORAREMIT");
        jsonobject.put("field103", "254724017787");
        return jsonobject;
    }

    /**
     *
     * @return
     */
    public JSONObject DiasporaRemitMpesa() {
        JSONObject jsonobject = new JSONObject();
        Random rand = new Random();
        String f37 = ClassImportantValues.padleft(String.valueOf(rand.nextInt(1000000)), 12, '0');
        jsonobject.put("field0", "0200");
        jsonobject.put("field2", "4242424242424242");
        jsonobject.put("field3", "480000");
        jsonobject.put("field4", "200");
        jsonobject.put("field7", LocalDateTime.now());
        jsonobject.put("field11", LocalDateTime.now());
        jsonobject.put("field24", "MM");
        jsonobject.put("field32", "APP");
        jsonobject.put("field37", f37);
        jsonobject.put("field49", "KES");
        jsonobject.put("payload", "{\"txnCurrency\": \"KES\",\"ExpiryDate\": \"042019\",\"CardSecurityCode\": \"706\",\"PAN\": \"4242424242424242\"}");
        jsonobject.put("field65", "254724017787");
        jsonobject.put("field68", "Dispora Remmitance");
        jsonobject.put("field71", "DISPORAMPESA");
        jsonobject.put("field100", "DISPORAMPESA");
        jsonobject.put("field103", "254724017787");
        return jsonobject;
    }

    /**
     *
     */
    public void ProcessIPRS() {
        try {
            HTTPCalls httpcalls = new HTTPCalls();
            String url = "http://10.20.2.28:8080/IPRSAdaptor/IPRSServlet";
            String incoming = "";
            JSONObject request = new JSONObject();

//            {"amount":"0", "password":"",
//"clientid":"1023","accountno":"254726128484", "msisdn":"254724816269", "currencycode":"404", "serviceid":"1016",
// "transactionid":"7256200MM4", "timestamp":"2017-02-17T07:59Z", "username":"","payload":{"ID":"1234"}}
            String password = ConfigSession.cl.HmacHash(Base64.encodeBase64String("d0062bf4993f".getBytes()), "/otrSY5CrQ+SCOitIy4/pA==", "HmacSHA512");
            request.put("amount", "0");
            request.put("clientid", "3044");
            request.put("accountno", "245724017787");
            request.put("currencycode", "404");
            request.put("serviceid", "1016");
            request.put("transactionid", "7256200MM4554");
            request.put("timestamp", "2017-02-17T07:59Z");
            request.put("username", "hudumatest01");
            request.put("password", password.toLowerCase());
            request.put("payload", "{ID:292984}");
            System.err.println("OUTGOING: " + request.toString());
            incoming = httpcalls.POSTRequest(url, request.toString());
            System.err.println("INCOMING: " + incoming);
        } catch (JSONException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
