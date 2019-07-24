package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class SetReadingInputValuesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long rdmId = (long) context.get(FacilioConstants.ContextNames.READING_DATA_META_ID);
		List<Map<String, Object>> props = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		props.forEach(prop -> {
			prop.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
			prop.put("rdmId", rdmId);
		});
		
		List<FacilioField> fields = FieldFactory.getReadingInputValuesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getReadingInputValuesModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("rdmId"), String.valueOf(rdmId), NumberOperators.EQUALS));
		deleteBuilder.delete();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(fields)
				.table(module.getTableName())
				.addRecords(props);
		insertBuilder.save();
		
		
		return false;
	}

}
