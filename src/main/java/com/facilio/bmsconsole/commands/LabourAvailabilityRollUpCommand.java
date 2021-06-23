package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;

public class LabourAvailabilityRollUpCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<? extends WorkOrderLabourContext> woLabourRecords = (List<WorkOrderLabourContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
		if (woLabourRecords != null && !woLabourRecords.isEmpty()) {
			for (WorkOrderLabourContext woLabour : woLabourRecords) {
                LabourContext labour = woLabour.getLabour();
                labour.setAvailability(!labour.getAvailability());
				UpdateRecordBuilder<LabourContext> updateBuilder = new UpdateRecordBuilder<LabourContext>()
						.module(module).fields(modBean.getAllFields(module.getName()))
						.andCondition(CriteriaAPI.getIdCondition(labour.getId(), module));
				
				updateBuilder.update(labour);
			}
		}
		
		return false;
	}
	}

