package com.facilio.xml.builder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XMLBuilder {
	
	Element element;
	Document doc;
	String xmlString;
	
	private XMLBuilder(Element element,Document doc) {
		this.element = element;
		this.doc = doc;
	}
	
	private XMLBuilder(String xmlString,Document doc) {
		this.element = element;
		this.doc = doc;
	}
	
	public static XMLBuilder create(String name) throws Exception {
		
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		 Document doc = dBuilder.newDocument();
		 
		 Element element = doc.createElement(name);
		 
		 doc.appendChild(element);
		 
		 return new XMLBuilder(element,doc);
	}
	
//	public static XMLBuilder parse(String xmlString) throws Exception {
//		
//		try(InputStream stream = new ByteArrayInputStream(xmlString.getBytes("UTF-16"));) {
//			parse(stream);
//		}
//	}
//	
//	public static XMLBuilder parse(InputStream stream) throws Exception {
//		
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//    	Document doc = dBuilder.parse(stream);
//        doc.getDocumentElement().normalize();
//		 
//		 
//		 return new XMLBuilder(element,doc);
//	}
	
	public XMLBuilder element(String name) throws Exception {
		
		Element element = doc.createElement(name);
		
		XMLBuilder xmlElement = new XMLBuilder(element,doc);
		
		 this.element.appendChild(element);
		 return xmlElement;
	}
	
	public XMLBuilder e(String text) throws Exception {
		return element(text);
	}
	
	public XMLBuilder text(String text) throws Exception {
		element.setTextContent(text); 
		return this;
	}
	
	public XMLBuilder t(String text) throws Exception {
		return text(text);
	}
	
	public XMLBuilder attribute(String name,String value) throws Exception {
		element.setAttribute(name, value); 
		return this;
	}
	
	public XMLBuilder a(String name,String value) throws Exception {
		return attribute(name, value);
	}
	
	public String getAsXMLString(){
		
		 DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		 LSSerializer lsSerializer = domImplementation.createLSSerializer();
		 
		 String result = lsSerializer.writeToString(doc);
		
		 return result;
	}
}
