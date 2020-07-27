package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.context.QuickFilterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class QuickFilterAction extends FacilioAction{
	
private static final long serialVersionUID = 1L;
	
	private QuickFilterContext quickFilter;
	
	private List<CustomFilterContext> customFilters;
	
	private Long viewId;
	
	private List<Long> fieldIds;
	
	private List<QuickFilterContext> quickFilters;
	
	public List<QuickFilterContext> getQuickFilters() {
		return quickFilters;
	}

	public void setQuickFilters(List<QuickFilterContext> quickFilters) {
		this.quickFilters = quickFilters;
	}
	
	
	public List<CustomFilterContext> getCustomFilters() {
		return customFilters;
	}

	public void setCustomFilters(List<CustomFilterContext> customFilters) {
		this.customFilters = customFilters;
	}
	

	public List<Long> getFieldIds() {
		return fieldIds;
	}

	public void setFieldIds(List<Long> fieldIds) {
		this.fieldIds = fieldIds;
	}

	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}

	public QuickFilterContext getQuickFilter() {
		return quickFilter;
	}

	public void setQuickFilter(QuickFilterContext quickFilter) {
		this.quickFilter = quickFilter;
	}

	public String addProperties() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addViewManagerPropertiesChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEWID, viewId);
//		context.put(FacilioConstants.ContextNames.FIELD_IDS, fieldIds);
		context.put(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT, quickFilters);
		context.put(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST, customFilters);
		chain.execute();
		setResult(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT));
		return SUCCESS;
		
	}

	public String deleteQuickFilter() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.deleteQuickFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		chain.execute();
		setResult(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT));
				
		return SUCCESS;
		
	}
	
	public String getQuickFiltersList() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.fetchQuickFilterChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		chain.execute();
		setResult(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT, context.get(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT));
		
		return SUCCESS;
		
	}

}
