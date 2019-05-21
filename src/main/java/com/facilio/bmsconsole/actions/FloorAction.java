package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStoreFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class FloorAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public String floorList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BUILDING_ID, getBuildingId());
		
		Chain getAllFloor = FacilioChainFactory.getAllFloorChain();
		getAllFloor.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setFloors((List<FloorContext>) context.get(FacilioConstants.ContextNames.FLOOR_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newFloor() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain newFloor = FacilioChainFactory.getNewFloorChain();
		newFloor.execute(context);
		
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
	
	public String addFloor() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FLOOR, floor);
		
		Chain addFloor = FacilioChainFactory.getAddFloorChain();
		addFloor.execute(context);
		
		setFloorId(floor.getId());
		
		return SUCCESS;
	}
	// updateFloor
	public String updateFloor() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BASE_SPACE, floor);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "floor");
		Chain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
		setFloor(floor);
		return SUCCESS;
	}
	public String deleteFloor() throws Exception {
		FacilioContext context = new FacilioContext();
		SetTableNamesCommand.getForFloor();
		Chain deleteCampus = FacilioChainFactory.deleteSpaceChain();
		deleteCampus.execute(context);
		return SUCCESS;
	}
	
	public String viewFloor() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getFloorId());
		
		Chain getFloorChain = FacilioChainFactory.getFloorDetailsChain();
		getFloorChain.execute(context);
		
		setFloor((FloorContext) context.get(FacilioConstants.ContextNames.FLOOR));
		
		return SUCCESS;
	}
	
	private File floorPlanImage;
	private String floorPlanImageFileName;
	private String floorPlanImageContentType;
	private String floorPlanInfo;

	public File getFloorPlanImage() {
		return floorPlanImage;
	}

	public void setFloorPlanImage(File floorPlanImage) {
		this.floorPlanImage = floorPlanImage;
	}

	public String getFloorPlanImageFileName() {
		return floorPlanImageFileName;
	}

	public void setFloorPlanImageFileName(String floorPlanImageFileName) {
		this.floorPlanImageFileName = floorPlanImageFileName;
	}

	public String getFloorPlanImageContentType() {
		return floorPlanImageContentType;
	}

	public void setFloorPlanImageContentType(String floorPlanImageContentType) {
		this.floorPlanImageContentType = floorPlanImageContentType;
	}
	
	public String getFloorPlanInfo() {
		return floorPlanInfo;
	}

	public void setFloorPlanInfo(String floorPlanInfo) {
		this.floorPlanInfo = floorPlanInfo;
	}
	
	public String uploadFloorPlan() throws Exception 
	{
		this.floor = new FloorContext();
		floor.setId(getFloorId());
		
		if (this.floorPlanImage != null) {
			long floorPlanId = FileStoreFactory.getInstance().getFileStore().addFile(floorPlanImageFileName, floorPlanImage, floorPlanImageContentType);
				
			floor.setFloorPlanId(floorPlanId);
		}
		
		floor.setFloorPlanInfo(getFloorPlanInfo());
			
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BASE_SPACE, floor);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "floor");
		Chain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
		setFloor(floor);
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
		context.put(FacilioConstants.ContextNames.ID, getFloorId());
		
		Chain getReportCardsChain = FacilioChainFactory.getFloorReportCardsChain();
		getReportCardsChain.execute(context);
		
		JSONObject reports = (JSONObject) context.get(FacilioConstants.ContextNames.REPORTS);
		
		setReports(reports);
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewFloorLayout(fields);
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
	
	private FloorContext floor;
	public FloorContext getFloor() 
	{
		return floor;
	}
	public void setFloor(FloorContext floor) 
	{
		this.floor = floor;
	}
	
	private long floorId;
	public long getFloorId() 
	{
		return floorId;
	}
	public void setFloorId(long floorId) 
	{
		this.floorId = floorId;
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
	
	private List<FloorContext> floors;
	public List<FloorContext> getFloors() 
	{
		return floors;
	}
	public void setFloors(List<FloorContext> floors) 
	{
		this.floors = floors;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.FLOOR;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewFloorLayout();
	}
	
	public String getViewDisplayName()
	{
		return "All Floors";
	}
	
	public List<FloorContext> getRecords() 
	{
		return floors;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryFloorLayout();
	}
	
	public FloorContext getRecord() 
	{
		return floor;
	}
}