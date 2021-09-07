package com.facilio.bundle.context;

import com.facilio.bundle.utils.BundleConstants;
import com.facilio.xml.builder.XMLBuilder;

import lombok.Getter;
import lombok.Setter;

 @Getter @Setter
 public class BundleFileContext {
	
	String name;
	String extension;
	Long parentComponentId;
	XMLBuilder xmlContent;
	String fileContent;
	
	public boolean isXMLFile() {
		return extension.equals(BundleConstants.XML_FILE_EXTN);
	}
	
	public BundleFileContext(String name,String extension,String xmlComponentName,String fileContent) throws Exception {
		this.name = name;
		this.extension = extension;
		if(extension.equals(BundleConstants.XML_FILE_EXTN)) {
			xmlContent = XMLBuilder.create(xmlComponentName);
		}
		else {
			this.fileContent = fileContent;
		}
	}
	
}
