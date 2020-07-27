package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class DeleteQuickFiltersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
		
		if (viewId > 0) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getQuickFilterFields());
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getQuickFilterModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("viewId"), String.valueOf(viewId), NumberOperators.EQUALS));
			
			deleteRecordBuilder.delete();
			
		}
		
		return false;
	}

	

}
