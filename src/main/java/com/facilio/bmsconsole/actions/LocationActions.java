package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class LocationActions extends ActionSupport {
	
	public String execute() throws Exception {
		
		setLocations(LocationAPI.getAllLocations(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
	    return SUCCESS;
	}
	
	public String create() throws Exception {
	
		FacilioContext context = new FacilioContext();
		Chain newLocation = FacilioChainFactory.getNewLocationChain();
		newLocation.execute(context);
		
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		return "new_location";
	}
	
	public String add() throws Exception {
		
		location.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		long locationId = LocationAPI.addLocation(location);
		
		setLocationId(locationId);
		
		return "add_location_success";
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
	
	private List<LocationContext> locations = null;
	public List<LocationContext> getLocations() {
		return locations;
	}
	public void setLocations(List<LocationContext> locations) {
		this.locations = locations;
	}
}