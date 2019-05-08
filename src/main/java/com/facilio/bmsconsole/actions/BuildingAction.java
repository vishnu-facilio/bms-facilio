package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class BuildingAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public String buildingList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SITE_ID, getSiteId());
		
		Chain getAllBuilding = FacilioChainFactory.getAllBuildingChain();
		getAllBuilding.execute(context);
		
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setBuildings((List<BuildingContext>) context.get(FacilioConstants.ContextNames.BUILDING_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newBuilding() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain newBuilding = FacilioChainFactory.getNewBuildingChain();
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
			Chain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		context.put(FacilioConstants.ContextNames.BUILDING, building);
		Chain addBuilding = FacilioChainFactory.getAddBuildingChain();
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
				Chain addLocation = FacilioChainFactory.addLocationChain();
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
		Chain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
		setBuilding(building);
		return SUCCESS;
	}
	public String deleteBuilding() throws Exception {
		FacilioContext context = new FacilioContext();
		SetTableNamesCommand.getForBuilding();
		Chain deleteCampus = FacilioChainFactory.deleteSpaceChain();
		deleteCampus.execute(context);
		return SUCCESS;
	}
	
	public String viewBuilding() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		
		Chain getBuildingChain = FacilioChainFactory.getBuildingDetailsChain();
		getBuildingChain.execute(context);
		
		setBuilding((BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING));
		
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
		
		Chain getReportCardsChain = FacilioChainFactory.getBuildingReportCardsChain();
		getReportCardsChain.execute(context);
		
		JSONObject reports = (JSONObject) context.get(FacilioConstants.ContextNames.REPORTS);
		reports.put("categoryreport", getSpaceCategorySummary());
		
		setReports(reports);
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		
		List<EnergyMeterContext> energyMeters = DashboardUtil.getMainEnergyMeter(getBuildingId()+"");
		if(energyMeters != null && !energyMeters.isEmpty()) {
			setMainEnergyMeter(energyMeters.get(0));
		}
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
}