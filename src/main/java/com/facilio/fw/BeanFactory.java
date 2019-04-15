package com.facilio.fw;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.transaction.FacilioTransactionManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashMap;

public class BeanFactory {

	private static Logger log = LogManager.getLogger(BeanFactory.class.getName());

	static HashMap<String, Class> beans = new HashMap<>();

	static {
		//TODO move it to context listener file
	}

	public static void initBeans() {

		ClassLoader classLoader = BeanFactory.class.getClassLoader();
		URL url = classLoader.getResource("conf/fw/beans.xml");
		if (url != null) {
			File beansxml = new File(url.getFile());
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
					beans.put(name, Class.forName(classname));
				}

			} catch (SAXException | IOException | ParserConfigurationException | ClassNotFoundException e) {
				log.info("Exception occurred ", e);
			}
			log.info("beans initialised : " + beans);
		} else {
			log.error("Couldn't find beans.xml file");
		}
	}


	public static Object lookup(String beanname) throws InstantiationException, IllegalAccessException {
		return lookup(beanname, false);
	}
	
	public static Object lookup(String beanname, int transaction) throws InstantiationException, IllegalAccessException {
		if(transaction==FacilioTransactionManager.TRANSACTION_NotSupported)
		{
		return lookup(beanname, false);
		}
		else
		{
			return lookup(beanname, 0L, transaction);
		}
	}

	protected static Object lookup(String beanname, boolean transaction) throws InstantiationException, IllegalAccessException {
		return lookup(beanname, 0L, transaction);
	}

	public static Object lookup(String beanname, String domainname) throws InstantiationException, IllegalAccessException, Exception {
		return lookup(beanname, domainname, false);
	}

	protected static Object lookup(String beanname, String domainname, boolean transaction) throws InstantiationException, IllegalAccessException, Exception {
		Long orgid = AccountUtil.getOrgBean().getOrg(domainname).getOrgId();
		return lookup(beanname, orgid, transaction);
	}

	public static Object lookup(String beanname, Long orgid) throws InstantiationException, IllegalAccessException {
		return lookup(beanname, orgid, false);
	}

	protected static Object lookup(String beanname, Long orgid, boolean transaction) throws InstantiationException, IllegalAccessException {
		Class implclass = beans.get(beanname);
		Object implobj = implclass.newInstance();
		return Proxy.newProxyInstance(implclass.getClassLoader(),
				implclass.getInterfaces(), new BeanInvocationHandler(implobj, orgid, transaction));
	}
	
	protected static Object lookup(String beanname, Long orgid, int transaction) throws InstantiationException, IllegalAccessException {
		Class implclass = beans.get(beanname);
		Object implobj = implclass.newInstance();
		return Proxy.newProxyInstance(implclass.getClassLoader(),
				implclass.getInterfaces(), new BeanInvocationHandler(implobj, orgid, transaction));
	}
}
