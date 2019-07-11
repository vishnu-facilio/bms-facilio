package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PrerequisiteApproversContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;


public class AddPrerequisiteApproversCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PrerequisiteApproversContext> prerequisiteApproversList = (List<PrerequisiteApproversContext>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVERS_LIST);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(prerequisiteApproversList != null && !prerequisiteApproversList.isEmpty()) {
			String moduleName = FacilioConstants.ContextNames.PREREQUISITE_APPROVERS;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			prerequisiteApproversList.forEach(pre->pre.setParentId(workOrder.getId()));
			InsertRecordBuilder<PrerequisiteApproversContext> builder = new InsertRecordBuilder<PrerequisiteApproversContext>()
															.module(module).fields(fields)
															.addRecords(prerequisiteApproversList);
			builder.save();
		}
		return false;
	}
}
