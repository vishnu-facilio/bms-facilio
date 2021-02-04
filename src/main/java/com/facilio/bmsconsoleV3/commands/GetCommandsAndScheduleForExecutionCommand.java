package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetCommandsAndScheduleForExecutionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime =  (long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ControlActionCommandContext> select = new SelectRecordsBuilder<ControlActionCommandContext>()
				.moduleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)
				.beanClass(ControlActionCommandContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ControlActionCommandContext.Status.SCHEDULED.getIntVal()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("executedTime"), startTime +","+endTime, DateOperators.BETWEEN));
		
		List<ControlActionCommandContext> commands = select.get();
		
		for(ControlActionCommandContext command :commands) {
			BmsJobUtil.scheduleOneTimeJobWithProps(command.getId(), "controlCommandExecutionJob", command.getExecutedTime()/1000, "facilio", null);
		}
		return false;
	}

}
