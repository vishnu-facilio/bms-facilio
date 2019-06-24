package com.facilio.mv.command;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class DeleteMVAjustmentVsBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER_OLD);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getMVAjuststmentVsBaselineFields());
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getMVAjuststmentVsBaselineModule().getTableName())
		.andCondition(CriteriaAPI.getCondition(fieldMap.get("projectId"), mvProjectWrapper.getMvProject().getId()+"", NumberOperators.EQUALS));
		
		deleteRecordBuilder.delete();
		
		return false;
	}

}
