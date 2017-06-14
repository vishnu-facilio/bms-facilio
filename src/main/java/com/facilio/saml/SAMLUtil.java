package com.facilio.saml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SAMLUtil {

	private static final Pattern pemPattern = Pattern.compile(
            "-----BEGIN PRIVATE KEY-----" + // File header
            "(.*\\n)" +                     // Key data
            "-----END PRIVATE KEY-----" +   // File footer
            "\\n?",                         // Optional trailing line break
            Pattern.MULTILINE | Pattern.DOTALL);
	
	public static PrivateKey getPemPrivateKey(File f, String algorithm) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] privateKey = new byte[(int) f.length()];
		dis.readFully(privateKey);
		dis.close();

//		String temp = new String(keyBytes);
//		String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----", "");
//		privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
//		System.out.println("Private key\n"+privKeyPEM);

		byte[] decodedPrivateKey = privateKey;
        if (privateKey[0] == '-') {
            decodedPrivateKey = decodePrivateKey(privateKey);
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey key = kf.generatePrivate(spec);
        System.out.println("##### PRIVATE KEY OBJECT...");
        System.out.println(key);
        return key;
	}
	
	protected static byte[] decodePrivateKey(byte[] data) throws InvalidKeyException {
        try {
            String s = new String(data, "UTF-8");
            Matcher extracter = pemPattern.matcher(s);
            if (extracter.matches()) {
                String pemBody = extracter.group(1);
                return DatatypeConverter.parseBase64Binary(pemBody);
            } else {
                throw new InvalidKeyException("Private key should be provided in PEM format!");
            }
        } catch (UnsupportedEncodingException exc) {
            // This should never happen.
            exc.printStackTrace();
        }
        return null;
    }
	
	public static PrivateKey getPrivateKey() {
		
		try {
			ClassLoader classLoader = SAMLUtil.class.getClassLoader();
			File privateKeyFile = new File(classLoader.getResource("conf/saml/saml.pem").getFile());
			
			return getPemPrivateKey(privateKeyFile, "RSA");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
//		InputStream in=null;
//		PrivateKey privatekey=null;
//		byte[] array=null;
//		try{
//			ClassLoader classLoader = SAMLUtil.class.getClassLoader();
//			File privateKeyFile = new File(classLoader.getResource("conf/saml/saml.der").getFile());
//
//			in = new FileInputStream(privateKeyFile);
//			array=new byte[ 1024*4 ];
//			while(true)
//			{
//				int bytesRead=in.read(array);
//				if(bytesRead==-1)
//				{
//					break;
//				}
//			}
//
//			PKCS8EncodedKeySpec encodedPrivateKey = new PKCS8EncodedKeySpec(array);
//			KeyFactory keyFactory = null;
//			try {
//				keyFactory = KeyFactory.getInstance("RSA");
//			} catch (NoSuchAlgorithmException e) {
//				e.printStackTrace();
//			}
//			try {
//				privatekey = keyFactory.generatePrivate(encodedPrivateKey);
//			} catch (InvalidKeySpecException e) {
//				e.printStackTrace();
//			}
//			return privatekey; 
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//		finally {
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
	
	public static X509Certificate getX509Certificate() {
		X509Certificate x509Cert = null;
		try{
			CertificateFactory fty = CertificateFactory.getInstance("X.509");
			
			File certFile = new File(SAMLUtil.class.getClassLoader().getResource("conf/saml/saml.crt").getFile());
			String certContent = getFileAsString(certFile);
			
//			ByteArrayInputStream bais = new ByteArrayInputStream(certContent.getBytes());
//			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlEa0RDQ0F2bWdBd0lCQWdJSkFPUllLUHlud3JnNE1BMEdDU3FHU0liM0RRRUJDd1VBTUlHTk1Rc3dDUVlEDQpWUVFHRXdKSlRqRVRNQkVHQTFVRUNCTUtWR0Z0YVd3Z1RtRmtkVEVRTUE0R0ExVUVCeE1IUTJobGJtNWhhVEVRDQpNQTRHQTFVRUNoTUhSbUZqYVd4cGJ6RVFNQTRHQTFVRUN4TUhSbUZqYVd4cGJ6RU1NQW9HQTFVRUF4TURTVVJRDQpNU1V3SXdZSktvWklodmNOQVFrQkZoWnRZV2RsYzJoQWRHaHBibWR6WTJsbGJuUXVZMjl0TUI0WERURTNNRFl4DQpNakE1TVRRMU5Wb1hEVEU0TURZeE1qQTVNVFExTlZvd2dZMHhDekFKQmdOVkJBWVRBa2xPTVJNd0VRWURWUVFJDQpFd3BVWVcxcGJDQk9ZV1IxTVJBd0RnWURWUVFIRXdkRGFHVnVibUZwTVJBd0RnWURWUVFLRXdkR1lXTnBiR2x2DQpNUkF3RGdZRFZRUUxFd2RHWVdOcGJHbHZNUXd3Q2dZRFZRUURFd05KUkZBeEpUQWpCZ2txaGtpRzl3MEJDUUVXDQpGbTFoWjJWemFFQjBhR2x1WjNOamFXVnVkQzVqYjIwd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKDQpBb0dCQUxUbmpxcWxZZnB3bktrQkJWM3lSNHpDRHVOVmtxWkE0Y0EzWjdnZWQrMWV2QVlLQVFSd0UySjJrOFVhDQp0dDg2a2J2dWNnUzFUN1NnQ0pET0F1WG9teFBPNWJVY2dkTnYwZlA2Y0w2bUZDTUFWZ2M0TjlXQlJ1RkhDV1BaDQpjcS9KSmpDVWY1OEVOd2pwNFR2SkF6VDJBbDlPZ053N1dNbk9aT3JGM0Y1N01LeFhBZ01CQUFHamdmVXdnZkl3DQpIUVlEVlIwT0JCWUVGSlBDZUlKRURLYzdOM1ZRN3RZaHhXMXBKaEJvTUlIQ0JnTlZIU01FZ2Jvd2diZUFGSlBDDQplSUpFREtjN04zVlE3dFloeFcxcEpoQm9vWUdUcElHUU1JR05NUXN3Q1FZRFZRUUdFd0pKVGpFVE1CRUdBMVVFDQpDQk1LVkdGdGFXd2dUbUZrZFRFUU1BNEdBMVVFQnhNSFEyaGxibTVoYVRFUU1BNEdBMVVFQ2hNSFJtRmphV3hwDQpiekVRTUE0R0ExVUVDeE1IUm1GamFXeHBiekVNTUFvR0ExVUVBeE1EU1VSUU1TVXdJd1lKS29aSWh2Y05BUWtCDQpGaFp0WVdkbGMyaEFkR2hwYm1kelkybGxiblF1WTI5dGdna0E1RmdvL0tmQ3VEZ3dEQVlEVlIwVEJBVXdBd0VCDQovekFOQmdrcWhraUc5dzBCQVFzRkFBT0JnUUFwT2JTN0VxTDBheTFheTI2REdwU3RsdnpQZ0QwUXVvRXYwRkdzDQpoVDhFa2xSVzFTVGlzVXZ6VHBvNWJDeDNUM205dm52N1VtQnNlbkRDcnpOTyt3NkZiWitjZllVUlZEZjFvdnF6DQplbGV2dDRacFpxV2w1NWpySEFOTlVVN2NXL3BwZG1xSWZtYnoyT2JhQ0ozaENlOXJLRUNzUTZzWmdEUFVVMWYwDQpqN1k4dXc9PQ0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ=="));
			FileInputStream fis = new FileInputStream(certFile);
			
			x509Cert = (java.security.cert.X509Certificate) fty.generateCertificate(fis);
//			byte[] bt = x509Cert.getPublicKey().getEncoded();
//			System.out.println(new String(bt));
			
			return x509Cert; 
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getFileAsString(File file) throws FileNotFoundException, IOException {
		StringWriter content = new StringWriter();
		int readCount = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			char[] buf = new char[8192];
			while ((readCount = reader.read(buf)) > 0) {
				content.write(buf, 0, readCount);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (content != null) {
				content.close();
			}
		}
		return content.toString();
	}
	
	public static String convertDomToString(Document doc) throws Exception {

		TransformerFactory tf1 = TransformerFactory.newInstance();
		Transformer trans1 = tf1.newTransformer();
		StringWriter writer1 = new StringWriter();
		trans1.transform(new DOMSource(doc), new StreamResult(writer1));
		String docString = writer1.toString();
		return docString;
	}

	public static Document convertStringToDocument(String subject) throws Exception {

		Properties builderProperties = new Properties();
		builderProperties.setProperty("http://xml.org/sax/features/namespaces", "true");

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.parse(new InputSource(new StringReader(subject)));
	}
}