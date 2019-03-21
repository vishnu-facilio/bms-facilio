package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.LabourContext;
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
		context.put(FacilioConstants.ContextNames.RECORD, labour);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		Chain addLabour = TransactionChainFactory.getAddLabourChain();
		addLabour.execute(context);
		setResult(FacilioConstants.ContextNames.LABOUR, labour);
		context.put(FacilioConstants.ContextNames.LABOUR_ID, labour.getId());
		return SUCCESS;
	}

	public String updateLabour() throws Exception {
		FacilioContext context = new FacilioContext();
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
