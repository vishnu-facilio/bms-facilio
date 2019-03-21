package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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

				Map<String, FacilioField> labourFieldsMap = FieldFactory.getAsMap(labourFields);
				List<LookupFieldMeta> lookUpfields = new ArrayList<>();
				lookUpfields.add(new LookupFieldMeta((LookupField) labourFieldsMap.get("toolType")));

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
