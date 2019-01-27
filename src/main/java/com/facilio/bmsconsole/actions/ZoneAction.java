package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ZoneAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	
	@SuppressWarnings("unchecked")
	public String zoneChildrenList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ZONE_ID, getZoneId());
		
		Chain getAllZone = FacilioChainFactory.getAllZoneChildrenChain();
		getAllZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setChildren((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
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
	
	public String addZone() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ZONE, zone);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, spaceId);
		Chain addZone = FacilioChainFactory.getAddZoneChain();
		addZone.execute(context);
		
		setZoneId(zone.getId());
		
		return SUCCESS;
	}
	
	public String updateZone() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ZONE, zone);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, spaceId);
		Chain updateZone = FacilioChainFactory.getUpdateZoneChain();
		updateZone.execute(context);
		
		setZoneId(zone.getId());
		
		return SUCCESS;
	}
	public String deleteZone() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, zoneId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "zone");
		Chain deleteZone = FacilioChainFactory.deleteSpaceChain();
		deleteZone.execute(context);
		setZoneId(zoneId);
		return SUCCESS;
	}
	private List<Long> spaceId;
	public List<Long> getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(List<Long> spaceId) {
		this.spaceId = spaceId;
	}

	public String viewZone() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getZoneId());
		
		Chain getZoneChain = FacilioChainFactory.getZoneDetailsChain();
		getZoneChain.execute(context);
		
		setZone((ZoneContext) context.get(FacilioConstants.ContextNames.ZONE));
		
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewZoneLayout(fields);
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
	
	public String getViewDisplayName()
	{
		return "All Zones";
	}
	
	public List<ZoneContext> getRecords() 
	{
		return zones;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryZoneLayout();
	}
	
	public ZoneContext getRecord() 
	{
		return zone;
	}
	
	private List<BaseSpaceContext> children;
	public List<BaseSpaceContext> getChildren() 
	{
		return children;
	}
	
	public void setChildren(List<BaseSpaceContext> children) 
	{
		this.children = children;
	}
}