package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class FetchResourcesListForBulkActionCreateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String filterModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getIdField(Constants.getModBean().getModule(filterModuleName)));
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<>()
				.module(Constants.getModBean().getModule(filterModuleName))
				.select(fields)
				.beanClass(ModuleBaseWithCustomFields.class)
				.andCriteria(criteria);
		
		List<ModuleBaseWithCustomFields> resources = select.get();
		
		context.put(FacilioConstants.ContextNames.RESOURCE_LIST, resources);
		
		return false;
	}

}
