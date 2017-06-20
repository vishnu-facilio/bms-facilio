package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;

public class AddCustomFieldCommand implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> fieldIds = new ArrayList<>();
		
		List<FacilioCustomField> fields = (List<FacilioCustomField>) context.get("CustomFields");
		for(FacilioCustomField field : fields) {
			fieldIds.add(CFUtil.addCustomField(field));
		}
		
		context.put("FieldIDs", fieldIds);
		
		return false;
	}
}
