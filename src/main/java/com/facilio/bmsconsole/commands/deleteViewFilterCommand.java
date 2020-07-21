package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ViewFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class deleteViewFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ViewFilterContext viewFilter = (ViewFilterContext) context.get(FacilioConstants.ContextNames.VIEW_FILTER);
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getViewFiltersModule().getTableName())
		.andCondition(CriteriaAPI.getIdCondition(viewFilter.getId(), ModuleFactory.getViewFiltersModule()));
		
		deleteRecordBuilder.delete();
		
		CriteriaAPI.deleteCriteria(viewFilter.getCriteriaId());
		return false;
	}

}
