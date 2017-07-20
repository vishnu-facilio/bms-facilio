package com.facilio.fw;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class BeanFactory {
	static HashMap<String,Class> beans = new HashMap();
	static
	
	{
		//TODO move it to context listener file

			
	}
	
	public static void initBeans()
	{
		ClassLoader classLoader = BeanFactory.class.getClassLoader();
		File beansxml = new File(classLoader.getResource("conf/fw/beans.xml").getFile());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	
			Document doc = dBuilder.parse(beansxml);
			NodeList nList = doc.getElementsByTagName("bean");
			
			

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				Element eElement = (Element) nNode;
				String name = eElement.getAttribute("name");
				String classname = eElement.getAttribute("class");
				beans.put(name,Class.forName(classname));
			}

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
System.out.println("beans initialised" +beans );
	
	}
	public Object lookup(String beanname,Long orgid) throws InstantiationException, IllegalAccessException
	{
		Class implclass = beans.get(beanname);
		Object implobj = implclass.newInstance();
		return Proxy.newProxyInstance(implclass.getClass().getClassLoader(),
				implclass.getInterfaces(), new BeanInvocationHandler(implobj,orgid));	
		
	}

}
