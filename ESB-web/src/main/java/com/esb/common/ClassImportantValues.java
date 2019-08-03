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
import java.text.SimpleDateFormat;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
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
import org.xml.sax.SAXException;

/**
 *
 * @author adm
 */
public class ClassImportantValues {

	/*
	 * To change this template, choose Tools | Templates and open the template in
	 * the editor.
	 */

	/**
	 * 
	 * @param mode
	 * @param initialVectorString
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public static Cipher initCipher(final int mode, final String initialVectorString, final String secretKey)
			throws Exception {
		final SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
		final IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
		final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(mode, skeySpec, initialVector);
		return cipher;
	}

	/**
	 * The function validates if a string is empty or null.
	 * @param s
	 * @return
	 */
	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		// HashMap map = new HashMap();
		return s == null || s.trim().isEmpty();
	}

	/**
	 * This function retrieves the string in InputStream
	 * @param is
	 * @return
	 */
	public static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		int size = 20000;
		StringBuilder sb = new StringBuilder(size);

		String line = "";
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
	 * This function creates a map from XML.
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
			int childlen = children.getLength();
			for (int j = 0; j < childlen; j++) {
				Node child = children.item(j);
				xmlDataMap.put(child.getNodeName(), child.getTextContent());
			}
		}
		return xmlDataMap;
	}

	/**
	 * *
	 *
	 * @param xml
	 * @return
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public static Document createDOMDocument(String xml)
			throws SAXException, IOException, ParserConfigurationException {
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
	public static NodeList evaluateXML(final Document doc, final String pathStr) throws XPathExpressionException {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		final XPathExpression expr = xpath.compile(pathStr);
		return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	}

	/**
	 * This function creates an XML from Map.
	 * @param methodName
	 * @param payloadMap
	 * @return
	 */
	public static String prepareXMLPayload(String methodName, Map<String, String> payloadMap) {
		String payload = "";

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element rootElement = document.createElement(methodName);
			document.appendChild(rootElement);

			for (String key : payloadMap.keySet()) {
				Element innerElement = document.createElement(key);
				if (!ConfigSession.cl.empty(payloadMap.get(key))) {
					innerElement.appendChild(document.createTextNode("" + payloadMap.get(key)));
					rootElement.appendChild(innerElement);
				}
			}

			payload = toStringXMLMessage(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payload;
	}

	/**
	 *
	 * @param xmlPayload
	 * @return
	 * @throws javax.xml.transform.TransformerException
	 */
	public static String toStringXMLMessage(Document xmlPayload) throws TransformerException {
		DOMSource source = new DOMSource(xmlPayload);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		transformer.transform(source, streamResult);

		return stringWriter.toString().replace(System.lineSeparator(), "");
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

	/**
	 * Convert xml to hashmap
	 */
	public static class MapEntryConverter implements Converter {

		@Override
                /**
                 * 
                 */
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
		public Map unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			Map<String, Object> map = new HashMap<>();

			while (reader.hasMoreChildren()) {
				reader.moveDown();
				map.put(reader.getNodeName(), reader.getValue());
				reader.moveUp();
			}
			return map;
		}
	}

 public static void LogErrors(String filename, String ErrorMessage)
  {
    Logs4jLogger log = new Logs4jLogger();
    log.LogEngine(ErrorMessage, ConfigSession.ESBLog+"/API", filename);
  }
/**
 * This function creates hash map from string hash map.
 * @param value
 * @return 
 */
	public static HashMap ReturnMap(String value) {
		value = value.replace("{", "").replace("}", "");
		HashMap map = new HashMap();
		String[] keyValuePairs = value.split(","); // split the string to creat key-value pairs

		for (String pair : keyValuePairs) // iterate over the pairs
		{
			String[] entry = pair.split("="); // split the pairs to get key and value
			if (entry.length > 1) {
				map.put(entry[0].trim(), entry[1].trim()); // add them to the hashmap and trim whitespaces
			}
		}

		return map;
	}

	/**
	 * This function pads string left if the string does not satisify the required length.
	 * @param s
	 * @param len
	 * @param c
	 * @return
	 */
	public static String padleft(String s, int len, char c) {
		s = s.trim();
		// if (s.length() > len) {
		// }
		StringBuilder d = new StringBuilder(len);
		int fill = len - s.length();
		while (fill-- > 0) {
			d.append(c);
		}
		d.append(s);
		return d.toString();
	}

	/**
	 * This function returns a hash of the string depending on the hash type parsed.
	 * @param password
	 * @param enctype
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String generateHash(String password, String enctype) throws NoSuchAlgorithmException {
		// SHA-256 - SHA-512 - MD5
		MessageDigest md = null;
		byte[] hash = null;
		md = MessageDigest.getInstance(enctype);
		hash = md.digest(password.getBytes());
		return convertToHex(hash);
	}

	/**
	 * This function converts string or byte to HEX.
	 * @param raw
	 * @return
	 */
	public static String convertToHex(byte[] raw) {
		int substring = 1;
		int length = 16;
		int size = 200;
		StringBuilder stringBuilder = new StringBuilder(size);
		for (int i = 0; i < raw.length; i++) {

			stringBuilder.append(Integer.toString((raw[i] & 0xff) + 0x100, length).substring(substring));
		}
		return stringBuilder.toString();
	}

	/**
	 * This function does a Hmac hashing of the string parsed depending on the hash parsed.
	 * @param password
	 * @param key
	 * @param encodingMAC
	 * @return
	 */
	public String HmacHash(String password, String key, String encodingMAC) {
		// Sample HmacSHA512,HmacSHA256
		Mac enctype = null;
		String result = null;
		try {
			enctype = Mac.getInstance(encodingMAC);
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), encodingMAC);
			enctype.init(keySpec);
			byte[] mac_data = enctype.doFinal(password.getBytes("UTF-8"));
			result = convertToHex(mac_data);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException
				| IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This function rounds off the B decimal parsed to the the parsed scale. 
	 * @param d
	 * @param scale
	 * @param roundUp
	 * @return
	 */
	public static BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
		int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
		return d.setScale(scale, mode);
	}

	/**
	 *  This function generates a random number between 1 and 10000000.
	 * @return
	 */
	public static String GenerateRandomNo() {
		Random rand = new Random();
		int val = (rand.nextInt((10000000 - 9) + 1) + 9) * 9999999;
		if (val < 0) {
			val = val * -10;
		}
		return String.valueOf(val).replace("-", "1");
	}

	/**
	 * This function returns the time stamp Quoted "Z" to indicate UTC, no timezone offset
	 * @return
	 */
	public static String getUTCTimeStamp() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(timeZone);
		String currentTimestamp = df.format(new Date());

		return currentTimestamp;
	}
}
