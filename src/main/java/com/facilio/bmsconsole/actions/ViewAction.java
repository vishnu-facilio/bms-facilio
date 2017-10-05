package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class ViewAction extends ActionSupport {
	
	public String addView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, parentView);
		if(getFilters() != null) {	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		
		context.put(FacilioConstants.ContextNames.NEW_CV, view);
		
		Chain addView = FacilioChainFactory.getAddViewChain();
		addView.execute(context);
		
		this.viewId = view.getId();
		
		return SUCCESS;
	}
	
	private long viewId;
	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	private FacilioView view;
	public FacilioView getView() {
		return view;
	}
	public void setView(FacilioView view) {
		this.view = view;
	}

	private String parentView;
	public String getParentView() {
		return parentView;
	}
	public void setParentView(String parentView) {
		this.parentView = parentView;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String filters;
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
}
