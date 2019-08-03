/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.common;

import com.esb.Main.ConfigSession;
import com.esb.jms.WriteToQueue;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Mac;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
//import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author adm
 */
public class ClassImportantValues {

    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */
    ResultSet rslt;

    /**
     *
     * @param mode
     * @param initialVectorString
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    private Cipher initCipher(final int mode, final String initialVectorString, final String secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        final SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        final IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
        final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, skeySpec, initialVector);
        return cipher;
    }

    /**
     *
     * @param format
     * @return
     */
    public String anyDate(String format) {
        try {
            if ("".equals(format)) {
                format = "yyyy-MM-dd HH:mm:ss"; // default
            }
            java.util.Date today;
            SimpleDateFormat formatter;

            formatter = new SimpleDateFormat(format);
            today = new java.util.Date();
            return (formatter.format(today));
        } catch (Exception ex) {
            System.out.println(" \n**** anyDate ****\n" + ex.getMessage());
        }
        return "";
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean empty(final String s) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    /**
     *
     * @param is
     * @return
     */
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();

    }

    /**
     *
     * @param xmlAlias
     * @param requestXML
     * @return
     * @throws XPathExpressionException
     * @throws Exception
     */
    public static Map<String, String> createMapFromXML(String xmlAlias, String requestXML)
            throws XPathExpressionException, Exception {
        final NodeList nodes = evaluateXML(createDOMDocument(requestXML), xmlAlias);
        Map<String, String> xmlDataMap = new HashMap<String, String>();
        int len = (nodes != null) ? nodes.getLength() : 0;
        for (int i = 0; i < len; i++) {
            NodeList children = nodes.item(i).getChildNodes();

            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (!(child.getTextContent().contains("\n") || child.getTextContent().contains("\t"))) { //If child node context has \n then there is no value in there
                        xmlDataMap.put(child.getNodeName(), child.getTextContent());
                    }
                }
            }
        }
        return xmlDataMap;
    }

    /**
     * *
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public static Document createDOMDocument(String xml) throws Exception {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(is);

        return document;
    }

    /**
     *
     * @param doc
     * @param pathStr
     * @return
     * @throws XPathExpressionException
     */
    public static NodeList evaluateXML(final Document doc, final String pathStr)
            throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final XPathExpression expr = xpath.compile(pathStr);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    /**
     *
     * @param methodName
     * @param payloadMap
     * @return
     */
    public String prepareXMLPayload(String methodName, Map<String, String> payloadMap) {
        String payload = "";

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element rootElement = document.createElement(methodName);
            document.appendChild(rootElement);

            for (String key : payloadMap.keySet()) {
                Element innerElement = document.createElement(key);
                if (!(payloadMap.get(key).equals(""))) {
                    innerElement.appendChild(document.createTextNode("" + payloadMap.get(key)));
                    rootElement.appendChild(innerElement);
                }
            }

            payload = toStringXMLMessage(document);
        } catch (Exception ex) {

        }
        return payload;
    }

    /**
     *
     * @param xmlPayload
     * @return
     * @throws Exception
     */
    public static String toStringXMLMessage(Document xmlPayload) throws Exception {
        DOMSource source = new DOMSource(xmlPayload);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(source, streamResult);

        return stringWriter.toString().replace("\n", "");
    }

    /**
     *
     * @param xmlAlias
     * @param dataXML
     * @return
     */
    public static Map<String, String> convertNodesToMap(String xmlAlias, String dataXML) {
        XStream xStream = new XStream();
        xStream.registerConverter(new MapEntryConverter());
        xStream.alias(xmlAlias, Map.class);

        return (Map) xStream.fromXML(dataXML);
    }

    private static class MapEntryConverter implements Converter {

        @Override
        public boolean canConvert(Class clazz) {
            return AbstractMap.class.isAssignableFrom(clazz);
        }

        /**
         *
         * @param value
         * @param writer
         * @param context
         */
        @Override
        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            AbstractMap<String, Object> map = (AbstractMap<String, Object>) value;
            map.entrySet().stream().map((entry) -> {
                writer.startNode(entry.getKey());
                return entry;
            }).map((entry) -> {
                writer.setValue(entry.getValue().toString());
                return entry;
            }).forEach((_item) -> {
                writer.endNode();
            });
        }

        /**
         *
         * @param reader
         * @param context
         * @return
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            Map<String, Object> map = new HashMap<>();

            while (reader.hasMoreChildren()) {
                reader.moveDown();
                map.put(reader.getNodeName(), reader.getValue());
                reader.moveUp();
            }
            return map;
        }
    }

    public static void LogErrors(String filename, String ErrorMessage) {
        Logs4jLogger log = new Logs4jLogger();
        log.LogEngine(ErrorMessage, ConfigSession.ESBLog+"/MDB", filename);
    }

    public HashMap ReturnMap(String value) {
        value = value.replace("{", "").replace("}", "");
        HashMap map = new HashMap();
        String[] keyValuePairs = value.split(",");              //split the string to creat key-value pairs

        for (String pair : keyValuePairs) //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value 
            if (entry.length > 1) {
                map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
            }
        }

        return map;
    }

    public static BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
        int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
        return d.setScale(scale, mode);
    }

    public String generateHash(String password, String enctype) throws NoSuchAlgorithmException {
        //SHA-256 - SHA-512 - MD5
        MessageDigest md = null;
        byte[] hash = null;
        md = MessageDigest.getInstance(enctype);
        hash = md.digest(password.getBytes());
        return convertToHex(hash);
    }

    public String convertToHex(byte[] raw) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < raw.length; i++) {
            stringBuilder.append(Integer.toString((raw[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuilder.toString();
    }

    public String HmacHash(String password, String key, String encodingMAC) {
        //Sample HmacSHA512,HmacSHA256
        Mac enctype = null;
        String result = null;
        try {
            enctype = Mac.getInstance(encodingMAC);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), encodingMAC);
            enctype.init(keySpec);
            byte[] mac_data = enctype.doFinal(password.getBytes("UTF-8"));
            result = convertToHex(mac_data);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public String GenerateRandomNo() {
        Random rand = new Random();
        int val = (rand.nextInt((10000000 - 9) + 1) + 9) * 9999999;
        if (val < 0) {
            val = val * -10;
        }
        return String.valueOf(val).replace("-", "1");
    }
}
