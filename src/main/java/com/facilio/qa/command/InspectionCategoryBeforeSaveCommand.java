package com.facilio.qa.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;

public class InspectionCategoryBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		// TODO Auto-generated method stub
		
		List<InspectionCategoryContext> categories = Constants.getRecordList((FacilioContext) context);
		
		
		for(InspectionCategoryContext category : categories) {
			
			String linkName = DisplayNameToLinkNameUtil.getLinkName(category.getDisplayName(), FacilioConstants.Inspection.INSPECTION_CATEGORY, "categoryName");
			
			category.setCategoryName(linkName);
		}
		return false;
	}

}
