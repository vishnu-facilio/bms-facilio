package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.constants.FacilioConstants;

public class GetRollUpFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> parentRollUpFieldsIds = (List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
		List<RollUpField> rollUpFields = new ArrayList<RollUpField>();
		for(Long parentRollUpFieldsId: parentRollUpFieldsIds) {
			List<RollUpField> rollUpField =  RollUpFieldUtil.getRollUpFieldsByParentRollUpFieldId(parentRollUpFieldsId, true);
			if(rollUpField != null && !rollUpField.isEmpty()) {
				rollUpFields.addAll(rollUpField);
			}
		}
		context.put(FacilioConstants.ContextNames.ROLL_UP_FIELDS, rollUpFields);
		return false;
	}

}
