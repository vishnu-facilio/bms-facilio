package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.opensymphony.xwork2.ActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class BuildingAction extends FacilioAction {
	
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

	@SuppressWarnings("unchecked")
	public String buildingList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SITE_ID, getSiteId());
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		FacilioChain getAllBuilding = FacilioChainFactory.getAllBuildingChain();
		getAllBuilding.execute(context);
		
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setBuildings((List<BuildingContext>) context.get(FacilioConstants.ContextNames.BUILDING_LIST));
		
		return SUCCESS;
	}

	public String v2buildingList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getSpaceModuleListChain();
		FacilioContext constructListContext = chain.getContext();
		constructListContext(constructListContext);
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "resource.name");
			searchObj.put("query", getSearch());
			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, "building");
		chain.execute();
		if (isFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,
					chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		} else {
			buildings = (List<BuildingContext>) chain.getContext()
					.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.BUILDING_LIST, buildings);
		}
		return SUCCESS;
	}


	
	public String v2thirdParty3dViewBuildingsList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		FacilioField thirdpartyidField = modBean.getField("thirdpartyid", module.getName());
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																.moduleName(module.getName())
																.beanClass(BuildingContext.class)
																.select(modBean.getAllFields(module.getName()))
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCondition(thirdpartyidField,"NULL",CommonOperators.IS_NOT_EMPTY));
		
		List<BuildingContext> buildings = selectBuilder.get();
		setResult(FacilioConstants.ContextNames.BUILDING_LIST, buildings);
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newBuilding() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		FacilioChain newBuilding = FacilioChainFactory.getNewBuildingChain();
		newBuilding.execute(context);
		
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
	
	public String addBuilding() throws Exception 
	{
		FacilioContext context = new FacilioContext();	
		LocationContext location = building.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(building.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			FacilioChain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		context.put(FacilioConstants.ContextNames.BUILDING, building);

		CommonCommandUtil.addEventType(EventType.CREATE, context);
		
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.BUILDING_ACTIVITY);

		FacilioChain addBuilding = FacilioChainFactory.getAddBuildingChain();
		addBuilding.execute(context);
		
		setBuildingId(building.getId());
		
		return SUCCESS;
	}
	
	public String updateBuilding() throws Exception 
	{
		
		
		FacilioContext context = new FacilioContext();
		LocationContext location = building.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(building.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
				building.setLocation(null);
			}
			else {
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			building.setLocation(null);
		}
		
		context.put(FacilioConstants.ContextNames.BASE_SPACE, building);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "building");

		CommonCommandUtil.addEventType(EventType.EDIT, context);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
		
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.BUILDING_ACTIVITY);

		FacilioChain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
		setBuilding(building);
		return SUCCESS;
	}
	public String deleteBuilding() throws Exception {
		FacilioContext context = new FacilioContext();
		SetTableNamesCommand.getForBuilding();
		FacilioChain deleteCampus = FacilioChainFactory.deleteSpaceChain();
		deleteCampus.execute(context);
		return SUCCESS;
	}
	
	public String viewBuilding() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		
		FacilioChain getBuildingChain = FacilioChainFactory.getBuildingDetailsChain();
		getBuildingChain.execute(context);
		
		setBuilding((BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING));
		
		return SUCCESS;
	}

	public String v2BuildingDetails() throws Exception {
		viewBuilding();
		setResult(FacilioConstants.ContextNames.BUILDING, building);
		return SUCCESS;
	}
	
	EnergyMeterContext mainEnergyMeter;
	
	public EnergyMeterContext getMainEnergyMeter() {
		return mainEnergyMeter;
	}

	public void setMainEnergyMeter(EnergyMeterContext mainEnergyMeter) {
		this.mainEnergyMeter = mainEnergyMeter;
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
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		context.put(FacilioConstants.ContextNames.REPORT_CARDS_META, fetchReportCardsMeta);
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		
		FacilioChain getReportCardsChain = FacilioChainFactory.getBuildingReportCardsChain();
		getReportCardsChain.execute(context);
		
		JSONObject reports = (JSONObject) context.get(FacilioConstants.ContextNames.REPORTS);
		reports.put("categoryreport", getSpaceCategorySummary());
		
		setReports(reports);
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));

		return SUCCESS;
	}

	public String getSpaceInsights() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME,"building");
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		FacilioChain buildingInsights = FacilioChainFactory.getBuildingInsights();
		buildingInsights.execute(context);
		setReports((JSONObject) context.get(FacilioConstants.ContextNames.COUNT));

		return SUCCESS;
	}
	
	private List<Map<String, Object>> getSpaceCategorySummary() throws Exception {

		FacilioField categoryIdFld = new FacilioField();
		categoryIdFld.setName("category_id");
		categoryIdFld.setColumnName("ID");
		categoryIdFld.setModule(ModuleFactory.getSpaceCategoryModule());
		categoryIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField categoryFld = new FacilioField();
		categoryFld.setName("category");
		categoryFld.setColumnName("NAME");
		categoryFld.setModule(ModuleFactory.getSpaceCategoryModule());
		categoryFld.setDataType(FieldType.STRING);
		
		FacilioField categoryDescFld = new FacilioField();
		categoryDescFld.setName("category_desc");
		categoryDescFld.setColumnName("DESCRIPTION");
		categoryDescFld.setModule(ModuleFactory.getSpaceCategoryModule());
		categoryDescFld.setDataType(FieldType.STRING);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(categoryIdFld);
		fields.add(categoryFld);
		fields.add(categoryDescFld);
		fields.add(countFld);
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("BaseSpace")
				.innerJoin("Space")
				.on("BaseSpace.ID = Space.ID")
				.leftJoin("Space_Category")
				.on("Space.SPACE_CATEGORY_ID = Space_Category.ID")
				.groupBy("Space_Category.ID, Space_Category.NAME, Space_Category.DESCRIPTION")
				.andCustomWhere("BaseSpace.ORGID=? AND Space.ORGID = ? AND BaseSpace.BUILDING_ID = ?", orgId, orgId, getBuildingId());

		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewBuildingLayout(fields);
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
	
	private BuildingContext building;
	public BuildingContext getBuilding() 
	{
		return building;
	}
	public void setBuilding(BuildingContext building) 
	{
		this.building = building;
	}
	
	private long buildingId;
	public long getBuildingId() 
	{
		return buildingId;
	}
	public void setBuildingId(long buildingId) 
	{
		this.buildingId = buildingId;
	}
	
	private long siteId;
	public long getSiteId() 
	{
		return siteId;
	}
	public void setSiteId(long siteId) 
	{
		this.siteId = siteId;
	}
	
	private List<BuildingContext> buildings;
	public List<BuildingContext> getBuildings() 
	{
		return buildings;
	}
	public void setBuildings(List<BuildingContext> buildings) 
	{
		this.buildings = buildings;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.BUILDING;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewBuildingLayout();
	}
	
	public String getViewDisplayName()
	{
		return "All Buildings";
	}
	
	public List<BuildingContext> getRecords() 
	{
		return buildings;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryBuildingLayout();
	}
	
	public BuildingContext getRecord() 
	{
		return building;
	}

	public List<String> getFetchReportCardsMeta() {
		return fetchReportCardsMeta;
	}

	public void setFetchReportCardsMeta(List<String> fetchReportCardsMeta) {
		this.fetchReportCardsMeta = fetchReportCardsMeta;
	}

	private List<String> fetchReportCardsMeta;
}