package com.facilio.xml.builder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XMLBuilder {
	
	XMLBuilder parent;
	Element element;
	Document doc;
	NodeList nodes;
	
	private XMLBuilder(Document doc,Element element,XMLBuilder parent) {
		this.element = element;
		this.doc = doc;
		this.parent = parent;
	}
	
	private XMLBuilder(NodeList nodes,Document doc) {
		this.nodes = nodes;
		this.doc = doc;
	}
	
	public static XMLBuilder create(String name) throws Exception {
		
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		 Document doc = dBuilder.newDocument();
		 
		 Element element = doc.createElement(name);
		 
		 doc.appendChild(element);
		 
		 return new XMLBuilder(doc,element,null);
	}
	
	public XMLBuilder element(String name) throws Exception {
		
		Element element = doc.createElement(name);
		
		XMLBuilder xmlElement = new XMLBuilder(doc,element,this);
		
		 this.element.appendChild(element);
		 return xmlElement;
	}
	
	public XMLBuilder constructElement(String name) throws Exception {
		
		Element element = doc.createElement(name);
		
		XMLBuilder xmlElement = new XMLBuilder(doc,element,null);
		
		return xmlElement;
	}
	
	public XMLBuilder addElement(XMLBuilder xmlElement) throws Exception {
		
		xmlElement.parent = this;
		this.element.appendChild(xmlElement.element);
		return this;
	}
	
	public XMLBuilder parent() {
		return parent;
	}
	
	public XMLBuilder p() {
		return parent();
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
		 result = result.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
		 return result;
	}
	
	// parser methods
	
	public static XMLBuilder parse(String xmlString) throws Exception {
		
		xmlString = xmlString.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
		
		try(InputStream stream = new ByteArrayInputStream(xmlString.getBytes("UTF-16"));) {
			return parse(stream);
		}
	}
	
	public static XMLBuilder parse(InputStream stream) throws Exception {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        
        Node node = doc.getFirstChild();
        String nodeName = node.getNodeName();
		
        NodeList nodeList = doc.getElementsByTagName(nodeName);
		 
		return new XMLBuilder(nodeList,doc);
	}
	
	public String text() {
		
		Element result = null;
		if(element != null) {
			result = element;
		}
		else if(nodes != null) {
			Node resultNode = nodes.item(0);
        	if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        		result = (Element) resultNode;
        	}
		}
		if(result != null) {
			String resultString = result.getTextContent();
    		return resultString;
		}
		return null;
	}
	
	public String t() {
		return text();
	}
	public String getText() {
		return text();
	}
	
	public String a(String name) {
		return attribute(name);
	}
	public String getAttribute(String name) {
		return attribute(name);
	}
	
	public String attribute(String name) {
		
		Element result = null;
		if(element != null) {
			result = element;
		}
		else if(nodes != null) {
			Node resultNode = nodes.item(0);
        	if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        		result = (Element) resultNode;
        	}
		}
		if(result != null) {
			String resultString = result.getAttribute(name);
    		return resultString;
		}
		return null;
	}
	
	public XMLBuilder get(int index) {
		
		if(nodes != null) {
			Node resultNode = nodes.item(index);
        	if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element result  = (Element) resultNode;
        		this.element = result;
        	}
		}
		return this;
	}
	
	public XMLBuilder getElement(String name) {
		
		Element result = null;
		if(element != null) {
			result = element;
		}
		else if(nodes != null) {
			Node resultNode = nodes.item(0);
        	if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        		result = (Element) resultNode;
        	}
		}
		if(result != null) {
			NodeList nodeList = result.getElementsByTagName(name);
			return new XMLBuilder(nodeList,doc);
		}
		return null;
	}
}
