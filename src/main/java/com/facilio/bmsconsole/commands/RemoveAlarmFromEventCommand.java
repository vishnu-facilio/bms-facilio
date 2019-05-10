package com.facilio.bmsconsole.commands;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveAlarmFromEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
			List<FacilioField> fields = EventConstants.EventFieldFactory.getEventFields();
			FacilioField alarmId = FieldFactory.getAsMap(fields).get("alarmId");
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.fields(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getCondition(alarmId, recordIds, PickListOperators.IS))
															;
			Map<String, Object> prop = new HashMap<>();
			prop.put("alarmId", -1);
			updateBuilder.update(prop);
		}
		return false;
	}

}
