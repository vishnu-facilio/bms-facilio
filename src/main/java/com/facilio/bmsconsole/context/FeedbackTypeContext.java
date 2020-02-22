package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class FeedbackTypeContext extends ModuleBaseWithCustomFields  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<ServiceCatalogContext>  catalogs;
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ServiceCatalogContext> getCatalogs() {
		return catalogs;
	}

	public void setCatalogs(List<ServiceCatalogContext> catalogs) {
		this.catalogs = catalogs;
	}
}
