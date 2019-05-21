package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants.EventModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class UpdateEventCountCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(UpdateEventCountCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		List alarmIds = (List) context.get(FacilioConstants.ContextNames.ALARM_ID);
		if (CollectionUtils.isNotEmpty(alarmIds)) {
			String moduleName = "alarm";
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			FacilioField noOfEventsField = modBean.getField("noOfEvents", moduleName);
			
			FacilioModule eventModule = EventModuleFactory.getEventModule();
			List<FacilioField> fields = new ArrayList<>();
			FacilioField alarmIdField = new FacilioField();
			alarmIdField.setName("alarmId");
			alarmIdField.setColumnName("ALARM_ID");
			alarmIdField.setDataType(FieldType.NUMBER);
			alarmIdField.setModule(eventModule);
			fields.add(alarmIdField);
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table("Event")
					.select(fields)
					.groupBy(alarmIdField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(alarmIdField, alarmIds, NumberOperators.EQUALS))
					.andCustomWhere("ORGID = ?", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
			List<Map<String, Object>> list = builder.get();
			
			Map<String, Object> updateMap = new HashMap<>();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("alarmId")).longValue();
				int numberOfEvents = ((Number) map.get("count")).intValue();
				
				updateMap.put("noOfEvents", numberOfEvents);
				
				UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(module)
						.fields(Collections.singletonList(noOfEventsField))
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
						.andCondition(CriteriaAPI.getIdCondition(id, module))
						;
				
				updateRecordBuilder.updateViaMap(updateMap);
			}
		}
		return false;
	}

}
