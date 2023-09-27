package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.opensymphony.xwork2.ActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class SpaceAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long stateTransitionId = -1;
	public long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	private Long approvalTransitionId;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}

	private int page;
	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return this.page;
	}

	private int perPage = -1;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getPerPage() {
		return this.perPage;
	}

	@SuppressWarnings("unchecked")
	public String spaceList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		if (getSiteId() > 0) {
			context.put(FacilioConstants.ContextNames.SITE_ID, getSiteId());
		}
		if (getBuildingId() > 0) {
			context.put(FacilioConstants.ContextNames.BUILDING_ID, getBuildingId());
		}
		if (getFloorId() > 0) {
			context.put(FacilioConstants.ContextNames.FLOOR_ID, getFloorId());
		}
		if (getCategoryId() > 0) {
			context.put(FacilioConstants.ContextNames.SPACE_CATEGORY, getCategoryId());
		}

		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "space.name");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}

		if (getPage() > 0) { // Added the check to maintain backward compatibility
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);

		FacilioChain getAllSpace = FacilioChainFactory.getAllSpaceChain();
		getAllSpace.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setSpaces((List<SpaceContext>) context.get(FacilioConstants.ContextNames.SPACE_LIST));
		
		return SUCCESS;
	}
	
	public String independentSpaceList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SITE_ID, getSiteId());
		
		FacilioChain getIndependentSpace = FacilioChainFactory.getIndependentSpaceChain();
		getIndependentSpace.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setSpaces((List<SpaceContext>) context.get(FacilioConstants.ContextNames.SPACE_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newSpace() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		FacilioChain newSpace = FacilioChainFactory.getNewSpaceChain();
		newSpace.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map mp = ActionContext.getContext().getParameters();
		String isajax = ((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		if(isajax!=null && isajax.equals("true"))
		{
			return "ajaxsuccess";
		}
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public String addSpace() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		if(tenantSpace != null) {
			space = tenantSpace;
		}
		context.put(FacilioConstants.ContextNames.SPACE, space);
		
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());
		SpaceCategoryContext spaceCategory= space.getSpaceCategory();
		long categoryId=-1;
		if(spaceCategory!=null) {
			categoryId=spaceCategory.getId();
		}
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
		
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SPACE_ACTIVITY);

		CommonCommandUtil.addEventType(EventType.CREATE, context);

		FacilioChain addSpace = FacilioChainFactory.getAddSpaceChain();
		addSpace.execute(context);
		
		setSpaceId((Long)context.get(FacilioConstants.ContextNames.RECORD_ID));
		
		return SUCCESS;
	}

	public String v2AddSpace() throws Exception {
		addSpace();
		setResult(FacilioConstants.ContextNames.SPACE_ID, spaceId);
		return SUCCESS;
	}
	
	public String updateSpace() throws Exception 
	{
		
		FacilioContext context = new FacilioContext();
		if(tenantSpace != null) {
			space = tenantSpace;
		}
		context.put(FacilioConstants.ContextNames.BASE_SPACE, space);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "space");

		CommonCommandUtil.addEventType(EventType.EDIT, context);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
		
		if(space.getLocation()!=null) {
			context.put(FacilioConstants.ContextNames.RECORD, space.getLocation());
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(space.getLocation().getId()));
		}
		
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SPACE_ACTIVITY);
		
		FacilioChain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
		setSpace(space);
		return SUCCESS;
	}

	public String v2UpdateSpace() throws Exception {
		updateSpace();
		setResult(FacilioConstants.ContextNames.SPACE, space);
		return SUCCESS;
	}
	
	public String deleteSpace() throws Exception {
		FacilioContext context = new FacilioContext();
		SetTableNamesCommand.getForSpace();
		FacilioChain deleteCampus = FacilioChainFactory.deleteSpaceChain();
		deleteCampus.execute(context);
		return SUCCESS;
	}
	
	public String viewSpace() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getSpaceId());
		
		FacilioChain getSpaceChain = FacilioChainFactory.getSpaceDetailsChain();
		getSpaceChain.execute(context);
		
		setSpace((SpaceContext) context.get(FacilioConstants.ContextNames.SPACE));
		
		return SUCCESS;
	}

	public String v2SpaceDetails() throws Exception {
		viewSpace();
		setResult(FacilioConstants.ContextNames.SPACE, space);
		return SUCCESS;
	}
	
	private JSONObject reports;
	public JSONObject getReports() {
		return reports;
	}
	public void setReports(JSONObject reports) {
		this.reports = reports;
	}
	
	private JSONArray reportcards;
	public JSONArray getReportcards() {
		return reportcards;
	}
	public void setReportcards(JSONArray reportcards) {
		this.reportcards = reportcards;
	}
	
	public String reportCards() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getSpaceId());
		
		FacilioChain getReportCardsChain = FacilioChainFactory.getSpaceReportCardsChain();
		getReportCardsChain.execute(context);
		
		JSONObject reports = (JSONObject) context.get(FacilioConstants.ContextNames.REPORTS);
		
		setReports(reports);
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		
		return SUCCESS;
	}

	public String getSpaceInsights() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getSpaceId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME,"space");
		FacilioChain spaceInsights = FacilioChainFactory.getSpaceInsights();
		spaceInsights.execute(context);
		setReports((JSONObject) context.get(FacilioConstants.ContextNames.COUNT));

		return SUCCESS;
	}
	
	public String getTenantForSpace() throws Exception
	{
		setTenant(TenantsAPI.getTenantForSpace(spaceId));
		return SUCCESS;
		
	}
	public List getFormlayout()
	{
		return FormLayout.getNewSpaceLayout(fields);
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
	
	String filters;
	public void setFilters(String filters){
		this.filters = filters;
	}
	public String getFilters(){
		return this.filters;
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
	
	private SpaceContext space;
	public SpaceContext getSpace() 
	{
		return space;
	}
	public void setSpace(SpaceContext space) 
	{
		this.space = space;
	}
	
	private long spaceId;
	public long getSpaceId() 
	{
		return spaceId;
	}
	public void setSpaceId(long spaceId) 
	{
		this.spaceId = spaceId;
	}
	
	private long siteId = -1;
	public long getSiteId() 
	{
		return siteId;
	}
	public void setSiteId(long siteId) 
	{
		this.siteId = siteId;
	}
	
	private long buildingId = -1;
	public long getBuildingId() 
	{
		return buildingId;
	}
	public void setBuildingId(long buildingId) 
	{
		this.buildingId = buildingId;
	}
	
	private long floorId = -1;
	public long getFloorId() 
	{
		return floorId;
	}
	public void setFloorId(long floorId) 
	{
		this.floorId = floorId;
	}
	
	private long categoryId = -1;
	public long getCategoryId() 
	{
		return categoryId;
	}
	public void setCategoryId(long categoryId) 
	{
		this.categoryId = categoryId;
	}
	
	private List<SpaceContext> spaces;
	public List<SpaceContext> getSpaces() 
	{
		return spaces;
	}
	public void setSpaces(List<SpaceContext> spaces) 
	{
		this.spaces = spaces;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.SPACE;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewSpaceLayout();
	}
	
	public String getViewDisplayName()
	{
		return "All Spaces";
	}
	
	public List<SpaceContext> getRecords() 
	{
		return spaces;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummarySpaceLayout();
	}
	
	public SpaceContext getRecord() 
	{
		return space;
	}
	private TenantContext tenant;
	public TenantContext getTenant() 
	{
		return tenant;
	}
	
	public void setTenant(TenantContext tenant) 
	{
		this.tenant = tenant;
	}
	
	private TenantUnitSpaceContext tenantSpace;
	public TenantUnitSpaceContext getTenantSpace() {
		return tenantSpace;
	}

	public void setTenantSpace(TenantUnitSpaceContext tenantSpace) {
		this.tenantSpace = tenantSpace;
	}
	
}