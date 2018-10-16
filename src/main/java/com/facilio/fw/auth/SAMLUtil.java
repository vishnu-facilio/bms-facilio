package com.facilio.fw.auth;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.LogManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SAMLUtil {

	private static org.apache.log4j.Logger log = LogManager.getLogger(SAMLUtil.class.getName());

	public static String getFileAsString(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			copyBytes(new BufferedInputStream(fis), bytes);

			return bytes.toString("utf-8");
		} finally {
			fis.close();
		}
	}

	private static void copyBytes(InputStream is, OutputStream bytes) throws IOException {
		int res = is.read();
		while (res != -1) {
			bytes.write(res);
			res = is.read();
		}
	}
	
	public static String convertDomToString(Document doc) throws Exception {

		TransformerFactory tf1 = TransformerFactory.newInstance();
		Transformer trans1 = tf1.newTransformer();
		StringWriter writer1 = new StringWriter();
		trans1.transform(new DOMSource(doc), new StreamResult(writer1));
		String docString = writer1.toString();
		return docString;
	}
	
	public static Document convertStringToDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
		docfactory.setNamespaceAware(true);

		// do not expand entity reference nodes 
		docfactory.setExpandEntityReferences(false);

		docfactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// Add various options explicitly to prevent XXE attacks.
		// (adding try/catch around every setAttribute just in case a specific parser does not support it.
		try {
			// do not include external general entities
			docfactory.setAttribute("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
		} catch (Throwable e) {}
		try {
			// do not include external parameter entities or the external DTD subset
			docfactory.setAttribute("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);
		} catch (Throwable e) {}
		try {
			docfactory.setAttribute("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
		} catch (Throwable e) {}
		try {
			docfactory.setAttribute("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
		} catch (Throwable e) {}
		try {
			// ignore the external DTD completely
			docfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
		} catch (Throwable e) {}
		try {
			// build the grammar but do not use the default attributes and attribute types information it contains
			docfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", Boolean.FALSE);
		} catch (Throwable e) {}
		try {
			docfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (Throwable e) {}

		DocumentBuilder builder = docfactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr;
		try {
			expr = xpath.compile("//*[@ID]");

			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element elem = (Element) nodeList.item(i);
				Attr attr = (Attr) elem.getAttributes().getNamedItem("ID");
				elem.setIdAttributeNode(attr, true);
			}
		} catch (XPathExpressionException e) {
			return null;
		}
		return doc;
	}
	
	public static String formatCert(String cert, Boolean heads) {
		String x509cert = "";

		if (cert != null) {		
			x509cert = cert.replace("\\x0D", "").replace("\r", "").replace("\n", "").replace(" ", "");

			if (x509cert != null && !"".equals(x509cert)) {
				x509cert = x509cert.replace("-----BEGINCERTIFICATE-----", "").replace("-----ENDCERTIFICATE-----", "");
			
				if (heads) {
					x509cert = "-----BEGIN CERTIFICATE-----\n" + chunkString(x509cert, 64) + "-----END CERTIFICATE-----";
				}
			}
		}
		return x509cert;
	}
	
	public static String formatPrivateKey(String key, boolean heads) {
		String xKey = "";

		if (key != null) {
			xKey = key.replace("\\x0D", "").replace("\r", "").replace("\n", "").replace(" ", "");
	
			if (xKey != null && !"".equals(xKey)) {
				if (xKey.startsWith("-----BEGINPRIVATEKEY-----")) {
					xKey = xKey.replace("-----BEGINPRIVATEKEY-----", "").replace("-----ENDPRIVATEKEY-----", "");
	
					if (heads) {
						xKey = "-----BEGIN PRIVATE KEY-----\n" + chunkString(xKey, 64) + "-----END PRIVATE KEY-----";
					}
				} else {
	
					xKey = xKey.replace("-----BEGINRSAPRIVATEKEY-----", "").replace("-----ENDRSAPRIVATEKEY-----", "");
	
					if (heads) {
						xKey = "-----BEGIN RSA PRIVATE KEY-----\n" + chunkString(xKey, 64) + "-----END RSA PRIVATE KEY-----";
					}
				}
			}
		}
			
		return xKey;
	}	

	private static String chunkString(String str, int chunkSize) {
		String newStr = "";
		int stringLength = str.length();
		for (int i = 0; i < stringLength; i += chunkSize) {
			if (i + chunkSize > stringLength) {
				chunkSize = stringLength - i;
			}
			newStr += str.substring(i, chunkSize + i) + '\n';
		}
		return newStr;
	}

	public static X509Certificate loadCert() throws CertificateException, IOException {
		ClassLoader classLoader = SAMLUtil.class.getClassLoader();
		File privateKeyFile = new File(classLoader.getResource("conf/saml/saml.crt").getFile());
		String certString = getFileAsString(privateKeyFile);
		
		certString = formatCert(certString, true);
		X509Certificate cert;
		
		try {
			cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(
				new ByteArrayInputStream(certString.getBytes("utf-8")));
		} catch (IllegalArgumentException e){
			cert = null;
		}
		return cert;
	}

	public static PrivateKey loadPrivateKey() throws GeneralSecurityException, IOException {

		ClassLoader classLoader = SAMLUtil.class.getClassLoader();
		File privateKeyFile = new File(classLoader.getResource("conf/saml/saml.pem").getFile());
		String keyString = getFileAsString(privateKeyFile);
		
		keyString = formatPrivateKey(keyString, false);
		keyString = chunkString(keyString, 64);		
		KeyFactory kf = KeyFactory.getInstance("RSA");
		
		PrivateKey privKey;
		try {
			byte[] encoded = org.apache.commons.codec.binary.Base64.decodeBase64(keyString);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
			privKey = kf.generatePrivate(keySpec);
		}
		catch(IllegalArgumentException e) {
			privKey = null;
			log.info("Exception occurred ", e);
		}

		return privKey;
	}
}