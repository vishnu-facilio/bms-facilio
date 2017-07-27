package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class BuildingAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String buildingList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
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
		context.put(FacilioConstants.ContextNames.BUILDING, building);
		
		Chain addBuilding = FacilioChainFactory.getAddBuildingChain();
		addBuilding.execute(context);
		
		setBuildingId(building.getId());
		
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