package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		List<Long> idList = new ArrayList<Long>();
		idList.add(getZoneId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, idList);
		
		Chain getAllZone = FacilioChainFactory.getAllZoneChildrenChain();
		getAllZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setChildren((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String getAllZoneChildrenList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		List<Long> idList = new ArrayList<Long>();
		idList.add(getZoneId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, idList);
		
		Chain getAllZone = FacilioChainFactory.getAllZoneChildrenChain();
		getAllZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setChildren((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	public String getTenantForZone() throws Exception
	{
		setTenant(TenantsAPI.fetchTenantForZone(zoneId));
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
	public String deleteZone() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, zoneId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "zone");
			Chain deleteZone = FacilioChainFactory.deleteSpaceChain();
			deleteZone.execute(context);
			setZoneId(zoneId);
			return SUCCESS;
			}
		catch (Exception e) {
			setError("error",e.getMessage());
			return ERROR;
		}
	
	}
	public String getAllZoneChildren() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getZoneIds());
		
		Chain getAllZone = FacilioChainFactory.getAllZoneChildrenChain();
		getAllZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setChildren((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
    public String getAllTenantZoneChildren() throws Exception {
		
		FacilioContext context = new FacilioContext();
		List<ZoneContext> tenantZones = SpaceAPI.getTenantZones();
		List<Long> zoneIds = new ArrayList<Long>();
		for(int j=0;j<tenantZones.size();j++) {
			zoneIds.add(tenantZones.get(j).getId());
		}
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,zoneIds);
		
		Chain getAllZone = FacilioChainFactory.getAllZoneChildrenChain();
		getAllZone.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setChildren((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
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
	
	private List<Long> zoneIds;
	public List<Long> getZoneIds() 
	{
		return zoneIds;
	}
	public void setZoneIds(List<Long> zoneIds) 
	{
		this.zoneIds = zoneIds;
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
	
	private TenantContext tenant;
	public TenantContext getTenant() 
	{
		return tenant;
	}
	
	public void setTenant(TenantContext tenant) 
	{
		this.tenant = tenant;
	}
	private JSONObject error;
	public JSONObject getError() {
		return error;
	}
	
	@SuppressWarnings("unchecked")
	public void setError(String key, Object error) {
		if (this.error == null) {
			this.error = new JSONObject();
		}
		this.error.put(key, error);			
	}
}