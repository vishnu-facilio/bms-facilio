package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.handler.AlarmWorkFlowHandler;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateWoIdInNewAlarmCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		AlarmOccurrenceContext alarmOccurrence = (AlarmOccurrenceContext) context.get(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		if (alarmOccurrence != null) {
			alarmOccurrence.setWoId(wo.getId());
			Map<Long, List<UpdateChangeSet>> changes=new HashMap<>();
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			
			FacilioField woField = FieldFactory.getAsMap(modBean.getAllFields(module.getName())).get("woId");
			UpdateRecordBuilder<AlarmOccurrenceContext> builder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
					.module(module)
					.fields(Collections.singletonList(woField))
					.andCondition(CriteriaAPI.getIdCondition(alarmOccurrence.getId(), module));
			builder.update(alarmOccurrence);

			BaseAlarmContext baseAlarm = alarmOccurrence.getAlarm();
			if (baseAlarm.getLastOccurrenceId() == alarmOccurrence.getId()) {
				UpdateRecordBuilder<BaseAlarmContext> alarmUpdateBuilder = new UpdateRecordBuilder<BaseAlarmContext>()
						.module(modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM))
						.fields(Collections.singletonList(modBean.getField("lastWoId", FacilioConstants.ContextNames.BASE_ALARM)))
						.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM)));
				baseAlarm.setLastWoId(alarmOccurrence.getWoId());
				alarmUpdateBuilder.withChangeSet(V3RecordAPI.getRecordsList(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()),Collections.singletonList(baseAlarm.getId())));
				alarmUpdateBuilder.update(baseAlarm);

				Map<Long, List<UpdateChangeSet>> recordChanges = alarmUpdateBuilder.getChangeSet();
				changes.put(baseAlarm.getId(), recordChanges.get(baseAlarm.getId()));

				addMessageToAlarmWorkFlow(baseAlarm,changes);
			}
		}
		return false;
	}

	private void addMessageToAlarmWorkFlow(BaseAlarmContext baseAlarm, Map<Long, List<UpdateChangeSet>> changes) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		Map<String, Object> alarmContext = new HashMap<>();
		alarmContext.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.NEW_READING_ALARM );
		alarmContext.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.NEW_READING_ALARM, Collections.singletonList(baseAlarm)));
		alarmContext.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, changes);
		alarmContext.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, Collections.singletonList(EventType.EDIT));

		WmsBroadcaster.getBroadcaster().sendMessage(new Message()
				.setTopic(AlarmWorkFlowHandler.TOPIC + "/" + baseAlarm.getId())
				.setContent(FieldUtil.getAsJSON(alarmContext)));
	}
}
