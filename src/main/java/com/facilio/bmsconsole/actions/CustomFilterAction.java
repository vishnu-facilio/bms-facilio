package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class CustomFilterAction extends FacilioAction{
	
	private static final long serialVersionUID = 1L;
	
	private CustomFilterContext customFilter;


	public CustomFilterContext getCustomFilter() {
		return customFilter;
	}

	public void setCustomFilter(CustomFilterContext customFilter) {
		this.customFilter = customFilter;
	}

	private Long viewId; 
	
	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	private Long filterId;
	
	
	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}

	private String moduleName;
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String addCustomFilter() throws Exception {
		FacilioChain chain = TransactionChainFactory.addCustomFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.CUSTOM_FILTER, customFilter);
		if (customFilter.getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(customFilter.getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT));
		return SUCCESS;
		
	}
	
	public String updateCustomFilter() throws Exception {
		FacilioChain chain = TransactionChainFactory.updateCustomFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.CUSTOM_FILTER, customFilter);
		context.put(FacilioConstants.ContextNames.FILTER_ID, filterId);
		if (customFilter.getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(customFilter.getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT));
		return SUCCESS;
		
	}
	
	public String deleteCustomFilter() throws Exception {
		FacilioChain chain = TransactionChainFactory.deleteCustomFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.CUSTOM_FILTER, customFilter);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT));
		return SUCCESS;
		
	}
	
	public String getCustomFilters() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getCustomFiltersList();
		
		Context context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		
		chain.execute();
		
		setResult(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST, context.get(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST));
		return SUCCESS;
	}

}
