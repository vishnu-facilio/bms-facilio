package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class FloorAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String floorList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
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
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		customFieldNames = new ArrayList<>();
		List<String> defaultFields = Arrays.asList(FloorContext.DEFAULT_FLOOR_FIELDS);
		for(FacilioField field : fields) 
		{
			if(!defaultFields.contains(field.getName())) 
			{
				customFieldNames.add(field.getName());
			}
		}
		Map mp = ActionContext.getContext().getParameters();
		String isajax = ((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		if(isajax!=null && isajax.equals("true"))
		{
			return "ajaxsuccess";
		}
		return SUCCESS;
	}
	
	public String addFloor() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FLOOR, floor);
		
		Chain addFloor = FacilioChainFactory.getAddFloorChain();
		addFloor.execute(context);
		
		setFloorId(floor.getFloorId());
		
		return SUCCESS;
	}
	
	public String viewFloor() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FLOOR_ID, getFloorId());
		
		Chain getFloorChain = FacilioChainFactory.getFloorDetailsChain();
		getFloorChain.execute(context);
		
		setFloor((FloorContext) context.get(FacilioConstants.ContextNames.FLOOR));
		
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewFloorLayout();
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
}