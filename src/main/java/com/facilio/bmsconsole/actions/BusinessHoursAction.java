package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

import java.util.List;

public class BusinessHoursAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String addBussinessHours() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BUSINESS_HOUR, businessHour);
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceid);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		Chain addBusinessHourChain = TransactionChainFactory.addBusinessHourChain();
		addBusinessHourChain.execute(context);
		id = (long) context.get(FacilioConstants.ContextNames.ID);
//		setResult("id", id);
		businessHour.setId(id);
		setResult("businessHour", businessHour);
		return SUCCESS;
	}

	public String updateBusinessHourInResource() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceid);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		Chain updateBusinessHourInResourceChain = TransactionChainFactory.updateBusinessHourInResourceChain();
		updateBusinessHourInResourceChain.execute(context);
		return SUCCESS;
	}

	public String v2updateBusinessHours() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BUSINESS_HOUR, businessHour);
		Chain updateBusinessHoursChain = TransactionChainFactory.updateBusinessHoursChain();
		updateBusinessHoursChain.execute(context);
		setResult("businessHour", businessHour);
		return SUCCESS;
	}

	public String v2deleteBusinessHours() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		Chain deleteBusinessHoursChain = TransactionChainFactory.deleteBusinessHoursChain();
		deleteBusinessHoursChain.execute(context);
		return SUCCESS;
	}

	public String v2getBusinessHoursList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		Chain getBusinessHoursChain = ReadOnlyChainFactory.getBusinessHoursChain();
		getBusinessHoursChain.execute(context);
		businessHour = (BusinessHoursContext) context.get(FacilioConstants.ContextNames.BUSINESS_HOUR);
		businessHoursList = (List<BusinessHoursContext>) context.get(FacilioConstants.ContextNames.BUSINESS_HOUR_LIST);
		setResult("businessHour", businessHour);
		setResult("list", businessHoursList);
		return SUCCESS;
	}

	private long resourceid = -1;

	public long getResourceid() {
		return resourceid;
	}

	public void setResourceid(long resourceid) {
		this.resourceid = resourceid;
	}

	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	BusinessHoursContext businessHour;

	public BusinessHoursContext getBusinessHour() {
		return businessHour;
	}

	public void setBusinessHour(BusinessHoursContext businessHour) {
		this.businessHour = businessHour;
	}

	List<BusinessHoursContext> businessHoursList;

	public List<BusinessHoursContext> getBusinessHoursList() {
		return businessHoursList;
	}

	public void setBusinessHoursList(List<BusinessHoursContext> businessHoursList) {
		this.businessHoursList = businessHoursList;
	}

}
