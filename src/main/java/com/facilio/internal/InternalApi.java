package com.facilio.internal;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;

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
