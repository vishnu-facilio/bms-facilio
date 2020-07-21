package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewFilterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ViewFilterAction extends FacilioAction{
	
	private static final long serialVersionUID = 1L;
	
	private ViewFilterContext viewFilter;

	public ViewFilterContext getViewFilter() {
		return viewFilter;
	}

	public void setViewFilter(ViewFilterContext viewFilter) {
		this.viewFilter = viewFilter;
	}
	
	private Long viewId;  
	
	
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

	public String addViewFilter() throws Exception {
		FacilioChain chain = TransactionChainFactory.addViewFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEW_FILTER, viewFilter);
		if (viewFilter.getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(viewFilter.getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT));
		return SUCCESS;
		
	}
	
	public String updateViewFilter() throws Exception {
		FacilioChain chain = TransactionChainFactory.updateViewFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEW_FILTER, viewFilter);
		if (viewFilter.getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(viewFilter.getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT));
		return SUCCESS;
		
	}
	
	public String deleteViewFilter() throws Exception {
		FacilioChain chain = TransactionChainFactory.deleteViewFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEW_FILTER, viewFilter);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT));
		return SUCCESS;
		
	}
	
	public String getViewFilters() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getViewFiltersList();
		
		Context context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		
		chain.execute();
		
		setResult(FacilioConstants.ContextNames.VIEW_FILTERS_LIST, context.get(FacilioConstants.ContextNames.VIEW_FILTERS_LIST));
		return SUCCESS;
	}

}
