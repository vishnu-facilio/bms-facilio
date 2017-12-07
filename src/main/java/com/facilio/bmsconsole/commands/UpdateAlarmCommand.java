package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(alarm != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			if(alarm.isAcknowledged()) {
				alarm.setAcknowledgedTime(System.currentTimeMillis());
				
				alarm.setAcknowledgedBy(AccountUtil.getCurrentUser());
			}
			
			if(alarm.getSeverity() != null && alarm.getSeverity().equalsIgnoreCase(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
				alarm.setClearedTime(System.currentTimeMillis());
				alarm.setClearedBy(AccountUtil.getCurrentUser());
			}
			
			alarm.setModifiedTime(System.currentTimeMillis());
			
			if(recordIds.size() == 1) {
				SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
																	.table(dataTableName)
																	.moduleName(moduleName)
																	.beanClass(AlarmContext.class)
																	.select(fields)
																	.andCondition(idCondition);

				List<AlarmContext> alarms = builder.get();
				if(alarms != null && !alarms.isEmpty()) {
					AlarmContext alarmObj = alarms.get(0);
					CommonCommandUtil.updateAlarmDetailsInTicket(alarmObj, alarm);
				}
				context.put(FacilioConstants.ContextNames.RECORD, alarms.get(0));
			}
			
			UpdateRecordBuilder<AlarmContext> updateBuilder = new UpdateRecordBuilder<AlarmContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(alarm));
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		}
		return false;
	}

}
