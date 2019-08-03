package Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author adm
 */
import java.util.HashMap;
import java.util.Map;
import redis.clients.jedis.Jedis;

public class RedisConfiguring {

    public static void main(String[] args) {
        // TODO code application logic here

        Jedis jedis = new Jedis("127.0.0.1");
        jedis.auth("baiks@123");

        jedis.select(1);

        //IPRS
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("clientID", "15");
        hash.put("salt", "CirLHBfgU3W2B8Yd2VoZDw==");
        hash.put("IPRS_URL", "http://10.20.2.28:8080/IPRSAdaptor/IprsIncomingRequest");
        hash.put("usr", "ngari.joshua@ekenya.co.ke");
        hash.put("pwd", "40aef43877");

        jedis.hmset("IPRS", hash);

        //PG_ADAPTOR
        Map<String, String> PG_hash = new HashMap<String, String>();
        PG_hash.put("CHOUTPAYURL", "https://10.20.2.6:8085/online-checkout/online-checkout-request.php");
        PG_hash.put("CHOUTUSERNAME", "paulkabaiku023@gmail.com");
        PG_hash.put("PGCLIENTID", "3044");
        PG_hash.put("PGPWD", "d0062bf4993f");
        PG_hash.put("IPRSURL", "http://10.20.2.28:8080/IPRSAdaptor/IPRSServlet");
        PG_hash.put("CHOUTCONFIRMURL", "https://10.20.2.6:8085/online-checkout/online-checkout-confirmation.php");
        PG_hash.put("PGlog", "/var/log/applications/SMEP/LOGS/PG");
        PG_hash.put("PGKEY", "/otrSY5CrQ+SCOitIy4/pA==");
        PG_hash.put("PGPREURL", "https://10.20.2.6:8443/ServiceLayer/presentment/ePay");
        PG_hash.put("WHITELISTMPESA", "254721301890,254721258866,254726741807,254721287179,254728077266,254732793531,254723317624,254721429631,254711986678,254722633080,254720996984,254716005575,254721867198,254723521441,254731878393,254726147600,254708042980,254718617006,254716755266,254718559338,254721392978,254725138505,254722274869,254727215191,254726729220,254717631104,254729311571,254725936174,254717266711,254723468089,254724017787,56565656");
        PG_hash.put("PGUSR", "hudumatest01");
        PG_hash.put("CURRENCYCODE", "KES");
        PG_hash.put("CHOUTSHORTCODE", "808600");
        PG_hash.put("ESBURL", "http://10.20.2.30:8080/ESB_SMEPDTM-war/SMEPDTMServlet");
        PG_hash.put("PGURL", "https://10.20.2.6:8443/ServiceLayer/request/postRequest");
        PG_hash.put("CHOUTPASSWORD", "877ff83f527c4c60961beaae6f00006b1a446a7cf34e06a8b583540435d9b277d77b3ab34e597d26f4554ecb677ed7340d2a4fce6da59eff0bea45c1a4c43b00");

        jedis.hmset("PG_ADAPTOR", PG_hash);

        //SMSADAPTOR
        Map<String, String> SMS_Adaptor_hash = new HashMap<String, String>();
        SMS_Adaptor_hash.put("SMSAPI", "https://10.20.2.148:8085/SafcomSMS/SendSMS.php");
        SMS_Adaptor_hash.put("EMAILAPI", "https://10.20.2.6:8085/HudumaEmailService/EmailService.php");
        SMS_Adaptor_hash.put("SMSLOGS", "/var/log/applications/SMEP/LOGS/ALERTS");
        SMS_Adaptor_hash.put("SENDER", "ECLECTICS");
        SMS_Adaptor_hash.put("EMAILLOGS", "/var/log/applications/SMEP/LOGS/ALERTS");

        jedis.hmset("SMSADAPTOR", SMS_Adaptor_hash);

        //OTHERS
        jedis.set("BILL_PREURL", "http://10.20.2.23:8080/PG_Adaptor/PresentMent");
        jedis.set("SECURITY_PRINCIPAL", "admin");
        jedis.set("SECURITY_CREDENTIALS", "admin@123");
        jedis.set("PROVIDER_URL", "jnp://127.0.0.1:9990");
        jedis.set("ESB_DATA_SOURCE", "java:jboss/datasources/ESB");
        jedis.set("ESB_ADAPTER_RESPONSE_QUEUE", "java:jboss/exported/jms/queue/ESBADAPTERRESPONSE_QUEUE");
        jedis.set("ESBTransaction_Topic", "java:/exported/jms/topic/ESBTransaction_Topic");
        jedis.set("ESBLOG", "C:/Program Files/logs/ESB");
        jedis.set("CHANNELS", "USSD,APP,ADMIN,CPS");
        jedis.set("JMSConnectionFactory","java:/ConnectionFactory");

        //Let them last forever
        jedis.persist("IPRS");
        jedis.persist("PG_ADAPTOR");
        jedis.persist("SMSADAPTOR");
        jedis.persist("BILL_PREURL");
        jedis.persist("SECURITY_PRINCIPAL");
        jedis.persist("SECURITY_CREDENTIALS");
        jedis.persist("PROVIDER_URL");
        jedis.persist("ESB_DATA_SOURCE");
        jedis.persist("ESB_ADAPTER_RESPONSE_QUEUE");
        jedis.persist("ESBTransaction_Topic");
        jedis.persist("ESBLOG");
        jedis.persist("CHANNELS");

        //getting the key value
        System.out.println(jedis.get("PROVIDER_URL"));
    }

}
