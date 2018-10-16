package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.constants.FacilioConstants;

@SuppressWarnings("serial")
public class BaseSpaceAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	public String baseSpaceList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "basespace.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		if (getOrderBy() != null) {
 			JSONObject sorting = new JSONObject();
 			sorting.put("orderBy", getOrderBy());
 			sorting.put("orderType", getOrderType());
 			context.put(FacilioConstants.ContextNames.SORTING, sorting);
 		}
 		JSONObject pagination = new JSONObject();
 		pagination.put("page", getPage());
 		pagination.put("perPage", getPerPage());
 		if (getPerPage() < 0) {
 			pagination.put("perPage", 5000);
 		}
 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_SPACE);
		Chain getAllSpace = FacilioChainFactory.getAllAreaChain();
		getAllSpace.execute(context);
		
		setModuleName("Base Space");
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	private boolean isZone;
	public boolean getIsZone() {
		return isZone;
	}
	public void setIsZone(boolean isZone) {
		this.isZone = isZone;
	}
	public String childrenList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE_ID, getSpaceId());
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, getSpaceType());
		context.put(FacilioConstants.ContextNames.IS_ZONE, getIsZone());
		
		Chain getAllSpace = FacilioChainFactory.getBaseSpaceChildrenChain();
		getAllSpace.execute(context);
		
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() 
	{
		return moduleName;
	}
	public void setModuleName(String moduleName) 
	{
		this.moduleName = moduleName;
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() 
	{
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) 
	{
		this.actionForm = actionForm;
	}
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() 
	{
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) 
	{
		this.customFieldNames = customFieldNames;
	}
	
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() 
	{
		return basespaces;
	}
	public void setBasespaces(List<BaseSpaceContext> basespaces) 
	{
		this.basespaces = basespaces;
	}
	
	public String getViewDisplayName()
	{
		return "All Spaces";
	}
	
	public List<BaseSpaceContext> getRecords() 
	{
		return basespaces;
	}
	
	private long spaceId;
	
	public long getSpaceId() {
		return this.spaceId;
	}
	
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	private String spaceType;

	public String getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}
	
	String filters;
	public void setFilters(String filters){
		this.filters = filters;
	}
	public String getFilters(){
		return this.filters;
	}
	
	String orderBy;
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderBy() {
		return this.orderBy;
	}
	
	String orderType;
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderType() {
		return this.orderType;
	}
	
	String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return this.search;
	}
	
	
/******************      V2 Api    ******************/
	
	public String v2baseSpaceList() throws Exception {
		baseSpaceList();
		setResult(FacilioConstants.ContextNames.BASE_SPACE_LIST, basespaces);
		return SUCCESS;
	}
	
}