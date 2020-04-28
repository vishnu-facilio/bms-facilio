package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class ServiceCatalogApi  {
	
	public static final String SERVICE_COMPLAINT_CATEGORY = "complaint";
	
	public static ServiceCatalogGroupContext getComplaintCategory() throws Exception {
		return getCategoryDetails(-1);
	}
	
	public static ServiceCatalogGroupContext getCategoryDetails(long id) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getServiceCatalogGroupFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectRecord = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .select(fields)
                ;
                
		if (id == -1) {
			selectRecord.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), SERVICE_COMPLAINT_CATEGORY, StringOperators.IS));
		}
		else {
			selectRecord.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getServiceCatalogGroupModule()));
		}

        Map<String, Object> map = selectRecord.fetchFirst();
        return FieldUtil.getAsBeanFromMap(map, ServiceCatalogGroupContext.class);
	}

}
