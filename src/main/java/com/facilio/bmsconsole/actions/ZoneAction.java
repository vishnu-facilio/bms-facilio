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
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ZoneAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String zoneList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain getAllZone = FacilioChainFactory.getAllZoneChain();
		getAllZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setZones((List<ZoneContext>) context.get(FacilioConstants.ContextNames.ZONE_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newZone() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain newZone = FacilioChainFactory.getNewZoneChain();
		newZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		customFieldNames = new ArrayList<>();
		List<String> defaultFields = Arrays.asList(ZoneContext.DEFAULT_ZONE_FIELDS);
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
	
	public String addZone() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ZONE, zone);
		
		Chain addZone = FacilioChainFactory.getAddZoneChain();
		addZone.execute(context);
		
		setZoneId(zone.getZoneId());
		
		return SUCCESS;
	}
	
	public String viewZone() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ZONE_ID, getZoneId());
		
		Chain getZoneChain = FacilioChainFactory.getZoneDetailsChain();
		getZoneChain.execute(context);
		
		setZone((ZoneContext) context.get(FacilioConstants.ContextNames.ZONE));
		
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewZoneLayout();
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
	
	private ZoneContext zone;
	public ZoneContext getZone() 
	{
		return zone;
	}
	public void setZone(ZoneContext zone) 
	{
		this.zone = zone;
	}
	
	private long zoneId;
	public long getZoneId() 
	{
		return zoneId;
	}
	public void setZoneId(long zoneId) 
	{
		this.zoneId = zoneId;
	}
	
	private List<ZoneContext> zones;
	public List<ZoneContext> getZones() 
	{
		return zones;
	}
	public void setZones(List<ZoneContext> zones) 
	{
		this.zones = zones;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.ZONE;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewZoneLayout();
	}
	
	public String getViewName()
	{
		return "All Zones";
	}
	
	public List<ZoneContext> getRecords() 
	{
		return zones;
	}
}