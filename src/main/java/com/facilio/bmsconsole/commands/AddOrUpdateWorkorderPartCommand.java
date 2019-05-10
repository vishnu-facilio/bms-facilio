package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddOrUpdateWorkorderPartCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderPartsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_PARTS);
		List<FacilioField> workorderPartsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_PARTS);
		List<WorkorderPartsContext> workorderParts = (List<WorkorderPartsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderPartsContext> partsToBeAdded = new ArrayList<>();
		if (workorderParts != null) {
			for (WorkorderPartsContext workorderpart : workorderParts) {
				InventoryContext inventory = InventoryApi.getInventory(workorderpart.getPartId());
				double costOccured = 0;
				if (inventory.getUnitcost() >= 0) {
					costOccured = inventory.getUnitcost() * workorderpart.getQuantity();
				}
				workorderpart.setCost(costOccured);
				workorderpart.setModifiedTime(System.currentTimeMillis());
				if (workorderpart.getId() <= 0) {
					//Insert
					workorderpart.setTtime(System.currentTimeMillis());
					partsToBeAdded.add(workorderpart);
				}
				else {
					updateWorkorderParts(workorderPartsModule, workorderPartsFields, workorderpart);
				}
			}
			if(partsToBeAdded!=null && !partsToBeAdded.isEmpty()) {
				addWorkorderParts(workorderPartsModule, workorderPartsFields, partsToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderParts.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 1);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderParts);
		}
		return false;
	}

	private void addWorkorderParts(FacilioModule module, List<FacilioField> fields, List<WorkorderPartsContext> parts) throws Exception {
		InsertRecordBuilder<WorkorderPartsContext> readingBuilder = new InsertRecordBuilder<WorkorderPartsContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(parts);
		readingBuilder.save();
	}
	
	private void updateWorkorderParts(FacilioModule module, List<FacilioField> fields, WorkorderPartsContext part) throws Exception {

		UpdateRecordBuilder<WorkorderPartsContext> updateBuilder = new UpdateRecordBuilder<WorkorderPartsContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);

		System.err.println( Thread.currentThread().getName()+"Exiting updateReadings in  AddorUpdateCommand#######  ");

	}
	
}
