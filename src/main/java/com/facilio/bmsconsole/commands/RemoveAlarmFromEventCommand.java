package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class RemoveAlarmFromEventCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
			List<FacilioField> fields = EventConstants.EventFieldFactory.getEventFields();
			FacilioField alarmId = FieldFactory.getAsMap(fields).get("alarmId");
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.fields(fields)
															.table(module.getTableName())
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getCondition(alarmId, recordIds, PickListOperators.IS))
															;
			Map<String, Object> prop = new HashMap<>();
			prop.put("alarmId", -1);
			updateBuilder.update(prop);
		}
		return false;
	}

}
