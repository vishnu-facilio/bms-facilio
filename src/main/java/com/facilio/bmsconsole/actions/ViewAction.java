package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionSupport;

public class ViewAction extends ActionSupport {
	
	public String viewList() throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (moduleName == null) {
			moduleName = "workorder";
		}
		FacilioModule moduleObj = modBean.getModule(moduleName);
		
		setViews(ViewAPI.getAllViews(moduleObj.getModuleId(), AccountUtil.getCurrentOrg().getOrgId()));
		return SUCCESS;
	}
	
	public String getViewDetail() throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (moduleName == null) {
			moduleName = "workorder";
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		
		Chain getViewChain = FacilioChainFactory.getViewDetailsChain();
		getViewChain.execute(context);
		
		setView((FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW));
		
		return SUCCESS;
	}
	
	public String addView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, parentView);
		if(getFilters() != null) {	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, view.getFields());
		context.put(FacilioConstants.ContextNames.NEW_CV, view);
		
		Chain addView = FacilioChainFactory.getAddViewChain();
		addView.execute(context);
		
		this.viewId = view.getId();
		
		return SUCCESS;
	}
	
	public String customizeColumns() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		if(viewId == -1) {
			context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		}
		else {
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		}
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, fields);
		Chain customizeColumnChain = FacilioChainFactory.getViewCustomizeColumnChain();
		customizeColumnChain.execute(context);

		return SUCCESS;
	}
	
	private long viewId = -1;
	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	private List<FacilioView> views;
	public List<FacilioView> getViews() {
		return views;
	}
	public void setViews(List<FacilioView> views) {
		this.views = views;
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
	
	private String viewName;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private List<ViewField> fields;
	public List<ViewField> getFields() {
		return fields;
	}
	public void setFields(List<ViewField> fields) {
		this.fields = fields;
	}
	
	
}
