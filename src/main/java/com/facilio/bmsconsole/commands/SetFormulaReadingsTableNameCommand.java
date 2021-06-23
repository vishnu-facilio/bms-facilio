package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;

public class SetFormulaReadingsTableNameCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, ReadingsAPI.FORMULA_FIELD_TABLE_NAME);
		return false;
	}

}
