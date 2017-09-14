package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class LocationActions extends ActionSupport {
	
	private SetupLayout setup;
	
	private List<FacilioField> fields;
	
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	public String execute() throws Exception {
		
		setLocations(LocationAPI.getAllLocations(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
	    return SUCCESS;
	}
	
	public String newLocation() throws Exception {
		setSetup(SetupLayout.getNewLocationLayout());
		FacilioContext context = new FacilioContext();
		Chain newLocation = FacilioChainFactory.getNewLocationChain();
		newLocation.execute(context);
		
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		return SUCCESS;
	}

	public String editLocation() throws Exception {
		
		setSetup(SetupLayout.getEditLocationLayout());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, locationId);
		Chain editLocation = FacilioChainFactory.getLocationChain();
		editLocation.execute(context);
		context.get(FacilioConstants.ContextNames.LOCATION);
		setLocation((LocationContext) context.get(FacilioConstants.ContextNames.LOCATION));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		return SUCCESS;
	}
	
	public String updateLocation() throws Exception {
		
		location.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		boolean isUpdated = LocationAPI.updateLocation(location);

		return SUCCESS;
	}
	
	public String deleteLocation() throws Exception {
		
		boolean isDeleted = LocationAPI.deleteLocation(locationId,OrgInfo.getCurrentOrgInfo().getOrgid());
		
		return SUCCESS;
	}
	
	public String addLocation() throws Exception {
		
		location.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		long locationId = LocationAPI.addLocation(location);
		
		setLocationId(locationId);
		
		return SUCCESS;
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() {
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}
	
	private LocationContext location;
	public LocationContext getLocation() {
		return location;
	}
	public void setLocation(LocationContext location) {
		this.location = location;
	}
	
	private long locationId;
	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	
	public String locationsList() throws Exception  {
		
		setSetup(SetupLayout.getLocationsListLayout());
		setLocations(LocationAPI.getAllLocations(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	private List<LocationContext> locations = null;
	public List<LocationContext> getLocations() {
		return locations;
	}
	public void setLocations(List<LocationContext> locations) {
		this.locations = locations;
	}
	
	private List formlayout;
	
	public List getFormlayout() 
	{
		return FormLayout.getNewLocationLayout(fields);
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewLocationLayout();
	}
}