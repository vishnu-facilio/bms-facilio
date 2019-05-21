package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeleteWorkorderLabourCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<WorkOrderLabourContext> deletedWorkorderLabour = (List<WorkOrderLabourContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);

		if (deletedWorkorderLabour != null && !deletedWorkorderLabour.isEmpty()) {
			for (WorkOrderLabourContext workorderLabour : deletedWorkorderLabour) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				FacilioModule labourModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
				List<FacilioField> labourFields = modBean.getAllFields(FacilioConstants.ContextNames.LABOUR);

//				Map<String, FacilioField> labourFieldsMap = FieldFactory.getAsMap(labourFields);
//				List<LookupField>lookUpfields = new ArrayList<>();
//				lookUpfields.add((LookupField) labourFieldsMap.get("toolType"));

				SelectRecordsBuilder<LabourContext> selectBuilder = new SelectRecordsBuilder<LabourContext>()
								.select(labourFields).table(labourModule.getTableName()).moduleName(labourModule.getName())
								.beanClass(LabourContext.class).andCustomWhere(labourModule.getTableName() + ".ID = ?",
										workorderLabour.getLabour().getId());

				List<LabourContext> labourRecords = selectBuilder.get();

				if (labourRecords != null && !labourRecords.isEmpty()) {
							LabourContext labourRecForId = labourRecords.get(0);
							labourRecForId.setAvailability(true);
							updateLabour(labourRecForId, labourModule, labourFields);
				}
			}
		}
		return false;
	}

	private void updateLabour(LabourContext labour,FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<LabourContext> updateBuilder = new UpdateRecordBuilder<LabourContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(labour.getId(), module));
		updateBuilder.update(labour);

	}

}
