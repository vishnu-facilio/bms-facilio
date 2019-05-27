package com.facilio.internal;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.Map;

public class InternalApi {
	
	public static boolean checkMinClientVersion(String type, double version) throws Exception {
		FacilioModule module = ModuleFactory.getMobileDetailsModule();
		List<FacilioField> fields = FieldFactory.getMobileDetailFields();
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), type, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("minVersion"), String.valueOf(version), NumberOperators.LESS_THAN_EQUAL));
		
		Map<String, Object> details = builder.fetchFirst();
		return details != null && !details.isEmpty();
	}

}
