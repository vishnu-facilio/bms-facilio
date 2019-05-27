package com.facilio.fw.auth;

import com.facilio.bmsconsole.actions.LoginAction;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.Inflater;

public class SAMLUtil {

	private static org.apache.log4j.Logger log = LogManager.getLogger(SAMLUtil.class.getName());
	
	private static final SimpleDateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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
	
	public static String decodeSAMLRequest(String encodedStr) {
		try {
			byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encodedStr);
			Inflater inf = new Inflater(true);

			inf.setInput(decoded);
			byte[] message = new byte[5000];
			int resultLength = inf.inflate(message);
			inf.end();
			return new String(message, 0, resultLength, "UTF-8");
		} catch (Exception e) {
			return null;
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
	
	private static String getNotBeforeDateAndTime() {
		Calendar beforeCal = Calendar.getInstance();
		beforeCal.add(Calendar.MINUTE, -5);
		SAML_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
		return SAML_DATE_FORMAT.format(beforeCal.getTime());
	}
	
	private static String getNotOnOrAfterDateAndTime() {
		Calendar afterCal = Calendar.getInstance();
		afterCal.add(Calendar.MINUTE, 15);
		SAML_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
		return SAML_DATE_FORMAT.format(afterCal.getTime());
	}
	
	public static String generateSignedSAMLResponse(SAMLAttribute samlAttr) throws Exception {
		ClassLoader classLoader = LoginAction.class.getClassLoader();
		File samlXML = new File(classLoader.getResource("conf/saml/saml-response.xml").getFile());
		String samlTemplate = SAMLUtil.getFileAsString(samlXML);

		Date dt = new Date();
		String samlDT = SAML_DATE_FORMAT.format(dt);
		String randomUUID_1 = UUID.randomUUID().toString();
		String randomUUID_2 = UUID.randomUUID().toString();
		String samlResponse = samlTemplate.replaceAll("--IssueInstant--|--AuthnInstant--", samlDT);
		samlResponse = samlResponse.replaceAll("--Issuer--", samlAttr.getIssuer());
		samlResponse = samlResponse.replaceAll("--ResponseID--", "_" + randomUUID_1);
		samlResponse = samlResponse.replaceAll("--AssertionID--", "SAML_" + randomUUID_2);
		samlResponse = samlResponse.replaceAll("--SessionIndex--", "SAML_" + randomUUID_2);
		samlResponse = samlResponse.replaceAll("--EmailID--", samlAttr.getEmail());
		samlResponse = samlResponse.replaceAll("--InResponseTo--", samlAttr.getInResponseTo());
		samlResponse = samlResponse.replaceAll("--Recipient--|--Destination--", samlAttr.getRecipient());
		samlResponse = samlResponse.replaceAll("--AudienceRestriction--", samlAttr.getIntendedAudience());
		samlResponse = samlResponse.replaceAll("--ConditionsNotBefore--", getNotBeforeDateAndTime());
		samlResponse = samlResponse.replaceAll("--ConditionsNotOnOrAfter--|--SubjectNotOnOrAfter--",
				getNotOnOrAfterDateAndTime());

		Document document = SAMLUtil.convertStringToDocument(samlResponse);

		if (samlAttr.getCustomAttr() != null) {

			NodeList assertions = document.getElementsByTagName("Assertion");
			if (assertions != null && assertions.getLength() > 0) {

				Element assertion = (Element) assertions.item(0);
				JSONObject customAttr = samlAttr.getCustomAttr();

				Iterator<String> keys = customAttr.keySet().iterator();
				Element attributeStatement = document.createElement("AttributeStatement");
				while (keys.hasNext()) {
					String attr = keys.next();
					String value = customAttr.get(attr).toString();
					Element cuatomAttrElement = newAttrTag(document, attr, value);
					attributeStatement.appendChild(cuatomAttrElement);
				}
				assertion.appendChild(attributeStatement);
			}
		}

		String Temp = SAMLUtil.convertDomToString(document);
		document = SAMLUtil.convertStringToDocument(Temp);

		document = signSAMLDocument(document, samlAttr.getPrivateKey(), samlAttr.getX509Certificate());

		samlResponse = SAMLUtil.convertDomToString(document);

		byte[] enc = org.apache.commons.codec.binary.Base64.encodeBase64(samlResponse.getBytes("UTF-8"));
		return new String(enc, "UTF-8");
	}
	
	private static Element newAttrTag(Document doc, String attribute, String value) {
		Element attributeTag = doc.createElement("Attribute");
		attributeTag.setAttribute("Name", attribute);
		Element attributeValue = doc.createElement("AttributeValue");
		attributeValue.setAttribute("xsi:type", "xs:string");
		attributeValue.setTextContent(value);
		attributeTag.appendChild(attributeValue);
		return attributeTag;
	}
	
	public static Document signSAMLDocument(Document root, PrivateKey privKey, X509Certificate x509Cert)
			throws KeyException, CertificateException {

		XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");

		DOMSignContext domSignCtx = new DOMSignContext(privKey, root.getElementsByTagName("Assertion").item(0));
		domSignCtx.setDefaultNamespacePrefix("ds");

		domSignCtx.setNextSibling(root.getElementsByTagName("Subject").item(0));
		Element assertion = (Element) root.getElementsByTagName("Assertion").item(0);
		assertion.setIdAttributeNode(assertion.getAttributeNode("ID"), true);
		String reference_URI = assertion.getAttribute("ID");
		Reference ref = null;
		SignedInfo signedInfo = null;
		try {
			Transform transform1 = xmlSigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
			Transform transform2 = xmlSigFactory.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#",
					(TransformParameterSpec) null);
			ArrayList<Transform> listt = new ArrayList<Transform>();
			listt.add(transform1);
			listt.add(transform2);
			ref = xmlSigFactory.newReference("#" + reference_URI,
					xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null), listt, null, null);
			signedInfo = xmlSigFactory.newSignedInfo(
					xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
							(C14NMethodParameterSpec) null),
					xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			ex.printStackTrace();
		}

		KeyInfoFactory keyInfoFactory = xmlSigFactory.getKeyInfoFactory();

		ArrayList x509Content = new ArrayList();
		x509Content.add(x509Cert);
		X509Data xd = keyInfoFactory.newX509Data(x509Content);
		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xd));

		XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
		try {
			// Sign the document
			xmlSignature.sign(domSignCtx);
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return root;
	}
}