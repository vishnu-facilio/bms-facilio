package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class LoadTaskCustomFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		List<FacilioCustomField> cfs = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
		context.put(FacilioConstants.ContextNames.CUSTOM_FIELDS, cfs);
		
		return false;
	}

}
