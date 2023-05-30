package com.facilio.devtool;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GenerateApiFromXml {

    public static void main(String[] args) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        List<String> paths = Arrays.asList(
                "src/main/webapp/WEB-INF/classes/admin.xml",
                "src/main/webapp/WEB-INF/classes/agent.xml",
                "src/main/webapp/WEB-INF/classes/agentv3.xml",
                "src/main/webapp/WEB-INF/classes/api.xml",
                "src/main/webapp/WEB-INF/classes/apiv2.xml",
                "src/main/webapp/WEB-INF/classes/apiv3.xml",
                "src/main/webapp/WEB-INF/classes/apiv4.xml",
                "src/main/webapp/WEB-INF/classes/baseapiv1.xml",
                "src/main/webapp/WEB-INF/classes/basestruts.xml",
                "src/main/webapp/WEB-INF/classes/bmsconsole.xml",
                "src/main/webapp/WEB-INF/classes/eventconsole.xml",
                "src/main/webapp/WEB-INF/classes/global-struts.xml",
                "src/main/webapp/WEB-INF/classes/integration.xml",
                "src/main/webapp/WEB-INF/classes/integrationv2.xml",
                "src/main/webapp/WEB-INF/classes/internal.xml",
                "src/main/webapp/WEB-INF/classes/internalv3.xml",
                "src/main/webapp/WEB-INF/classes/leedconsole.xml",
                "src/main/webapp/WEB-INF/classes/public.xml",
                "src/main/webapp/WEB-INF/classes/serviceportal.xml",
                "src/main/webapp/WEB-INF/classes/serviceportalv2.xml",
                "src/main/webapp/WEB-INF/classes/setup.xml",
                "src/main/webapp/WEB-INF/classes/setupv2.xml",
                "src/main/webapp/WEB-INF/classes/setupwebtab.xml",
                "src/main/webapp/WEB-INF/classes/sso.xml",
                "src/main/webapp/WEB-INF/classes/struts.xml",
                "src/main/webapp/WEB-INF/classes/vendorportalv2.xml",
                "src/main/webapp/WEB-INF/classes/visitorportalv2.xml"
        );
        if(CollectionUtils.isNotEmpty(paths)) {
            for(String path : paths) {
                try {
                    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(new File(path));
                    NodeList list = doc.getElementsByTagName("struts");
                    for (int temp = 0; temp < list.getLength(); temp++) {
                        Node node = list.item(temp);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            NodeList elementPackage = element.getElementsByTagName("package");
                            for(int j=0;j<elementPackage.getLength();j++) {
                                NamedNodeMap attributeNodeMapPackage = elementPackage.item(j).getAttributes();
                                Node namespace = attributeNodeMapPackage.getNamedItem("namespace");
                                if(namespace != null) {
                                    String namespaceString = attributeNodeMapPackage.getNamedItem("namespace").getNodeValue();
                                    Element actionElement = (Element) elementPackage.item(j);
                                    NodeList actionElementNode = actionElement.getElementsByTagName("action");
                                    if (actionElementNode.getLength() > 0) {
                                        for (int k = 0; k < actionElementNode.getLength(); k++) {
                                            NamedNodeMap attributeNodeMap = actionElementNode.item(k).getAttributes();
                                            String finalString = namespaceString + "/" + attributeNodeMap.getNamedItem("name").getNodeValue();
                                            int i = 0;
                                            String name = ":attr";
                                            while(finalString.contains("*")) {
                                                i = i + 1;
                                                finalString = StringUtils.replaceOnce(finalString, "*", name + i);
                                            }
                                            System.out.println(finalString);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}