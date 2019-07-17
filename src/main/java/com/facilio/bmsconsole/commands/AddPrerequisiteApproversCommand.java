package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;


public class AddPrerequisiteApproversCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<SingleSharingContext> prerequisiteApproversList = (List<SingleSharingContext>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVERS_LIST);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(prerequisiteApproversList != null && !prerequisiteApproversList.isEmpty()) {
			FacilioModule module = ModuleFactory.getPrerequisiteApproversModule();
			List<FacilioField> fields = FieldFactory.getSharingFields(module);
			for (FacilioField field : fields) {
				if ("fieldId".equalsIgnoreCase(field.getName())) {
					fields.remove(field);
				}
			}
			List<Map<String, Object>> props = new ArrayList<>();
			prerequisiteApproversList.forEach(pre->{
				pre.setParentId(workOrder.getId());
				try{
					props.add(FieldUtil.getAsProperties(pre));
				}catch(Exception e){
					e.printStackTrace();
				}
			});
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(fields).addRecords(props);
			builder.save();
		}
		return false;
	}
}
