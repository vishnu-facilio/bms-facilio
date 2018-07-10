package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class GenericDeleteForSpaces implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BaseSpaceContext building = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.MODULE);
		String moduleNameSpace = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		DeleteRecordBuilder<BaseSpaceContext> deleteAs = new DeleteRecordBuilder<BaseSpaceContext>();
		deleteAs.markAsDelete();
		return false;
	}

}
