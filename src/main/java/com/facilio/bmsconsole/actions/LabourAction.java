package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class LabourAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private LabourContext labour;

	public LabourContext getLabour() {
		return labour;
	}

	public void setLabour(LabourContext labour) {
		this.labour = labour;
	}

	private List<LabourContext> labours;

	public List<LabourContext> getLabours() {
		return labours;
	}

	public void setLabours(List<LabourContext> labours) {
		this.labours = labours;
	}

	private long labourId;

	public long getLabourId() {
		return labourId;
	}

	public void setLabourId(long labourId) {
		this.labourId = labourId;
	}

	public String addLabour() throws Exception {
		FacilioContext context = new FacilioContext();
		LocationContext location = labour.getLocation();
		if(location!=null)
		{	
			location.setName(labour.getName()+"_location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			Chain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		
		context.put(FacilioConstants.ContextNames.RECORD, labour);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.remove(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Chain addLabour = TransactionChainFactory.getAddLabourChain();
		addLabour.execute(context);
		setResult(FacilioConstants.ContextNames.LABOUR, labour);
		context.put(FacilioConstants.ContextNames.LABOUR_ID, labour.getId());
		return SUCCESS;
	}

	public String deleteLabour() throws Exception {
		FacilioContext context = new FacilioContext();
		List<Long> idList = new ArrayList<Long>();
		idList.add(labourId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, idList);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		Chain deleteLabour = TransactionChainFactory.getDeleteLabourChain();
		deleteLabour.execute(context);
		setResult(FacilioConstants.ContextNames.LABOUR_ID, labourId);
		return SUCCESS;
	}

	public String updateLabour() throws Exception {
		FacilioContext context = new FacilioContext();
		
		//update location
		LocationContext location = labour.getLocation();
		
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(labour.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				Chain editLocation = FacilioChainFactory.updateLocationChain();
				editLocation.execute(context);
				labour.setLocation(location);
			}
			else {
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			labour.setAddress(null);
		}
		if(context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST) != null) {
			context.remove(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		
		context.put(FacilioConstants.ContextNames.RECORD, labour);
		context.put(FacilioConstants.ContextNames.RECORD_ID, labour.getId());
		context.put(FacilioConstants.ContextNames.ID, labour.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, labour.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(labour.getId()));
		Chain updateLabourChain = TransactionChainFactory.getUpdateLabourChain();
		updateLabourChain.execute(context);
		setLabourId(labour.getId());
		setResult(FacilioConstants.ContextNames.LABOUR, labour);

		return SUCCESS;
	}

	public String labourDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getLabourId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchStockedToolsDetails();
		inventryDetailsChain.execute(context);

		setLabour((LabourContext) context.get(FacilioConstants.ContextNames.LABOUR));
		setResult(FacilioConstants.ContextNames.LABOUR, labour);
		return SUCCESS;
	}

	public String labourList() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain labourListChain = ReadOnlyChainFactory.getLabourList();
		labourListChain.execute(context);
		labours = (List<LabourContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (labours == null) {
				labours = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.LABOURS, labours);
		
		return SUCCESS;
	}

}
