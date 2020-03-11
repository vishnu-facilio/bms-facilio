package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.mysql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;

public class SetDeleteBaseSpaceRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
		BaseSpaceContext toBeDeletedBaseSpaceContext = SpaceAPI.getBaseSpace(spaceId, true);
		if(toBeDeletedBaseSpaceContext != null) {
			context.put(FacilioConstants.ContextNames.RECORD, toBeDeletedBaseSpaceContext);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_SPACE);
		}
		
		return false;
	}

}
