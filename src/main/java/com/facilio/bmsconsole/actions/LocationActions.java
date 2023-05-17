package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class LocationActions extends FacilioAction { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String execute() throws Exception {
		
		setLocations(LocationAPI.getAllLocations(AccountUtil.getCurrentOrg().getOrgId()));
		
	    return SUCCESS;
	}
	
//	public String newLocation() throws Exception {
//		setSetup(SetupLayout.getNewLocationLayout());
//		FacilioContext context = new FacilioContext();
//		FacilioChain newLocation = FacilioChainFactory.getNewLocationChain();
//		newLocation.execute(context);
//		
//		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
//		
//		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
//		
//		return SUCCESS;
//	}

//	public String editLocation() throws Exception {
//		
//		setSetup(SetupLayout.getEditLocationLayout());
//		FacilioContext context = new FacilioContext();
//		context.put(FacilioConstants.ContextNames.ID, locationId);
//		FacilioChain editLocation = FacilioChainFactory.getLocationChain();
//		editLocation.execute(context);
//		context.get(FacilioConstants.ContextNames.LOCATION);
//		setLocation((LocationContext) context.get(FacilioConstants.ContextNames.LOCATION));
//		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
//		return SUCCESS;
//	}
		
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
		
		FacilioContext context = new FacilioContext();
		Command getLocations = FacilioChainFactory.getAllLocationsCommand();
		getLocations.execute(context);
		setLocations((List<LocationContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
//		setSetup(SetupLayout.getLocationsListLayout());
//		setLocations(LocationAPI.getAllLocations(AccountUtil.getCurrentOrg().getOrgId()));
		
		return SUCCESS;
	}
	
	public String v2locationDetails() throws Exception  {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, locationId);
		Command getLocations = FacilioChainFactory.getLocationChain();
		getLocations.execute(context);
		setResult(FacilioConstants.ContextNames.LOCATION, context.get(FacilioConstants.ContextNames.LOCATION));
		return SUCCESS;
	}
	
	public String addLocation() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getLocation());
		Command addLocation = FacilioChainFactory.addLocationChain();
		addLocation.execute(context);
			
//		location.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
//		long locationId = LocationAPI.addLocation(location);	
		setLocationId((Long) context.get(FacilioConstants.ContextNames.RECORD_ID));
		
		return SUCCESS;
	}

	public String updateLocation() throws Exception {
	
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getLocation());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,getLocationIds());
		Command updateLocation = FacilioChainFactory.updateLocationChain();
		updateLocation.execute(context);
		
//		location.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
//		boolean isUpdated = LocationAPI.updateLocation(location);
		return SUCCESS;
	}
	
	public String deleteLocation() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,getLocationIds());
		Command deleteLocation = FacilioChainFactory.deleteLocationChain();
		deleteLocation.execute(context);
		
//		long id = getLocationId();
//		boolean isDeleted = LocationAPI.deleteLocation(id, AccountUtil.getCurrentOrg().getOrgId());
		return SUCCESS;
	}

	List<Long> locationIds;
	
	public List<Long> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(List<Long> locationIds) {
		this.locationIds = locationIds;
	}

	private List<LocationContext> locations = null;
	public List<LocationContext> getLocations() {
		return locations;
	}
	public List<LocationContext> getRecords() {
		return locations;
	}
	public void setLocations(List<LocationContext> locations) {
		this.locations = locations;
	}
	
}