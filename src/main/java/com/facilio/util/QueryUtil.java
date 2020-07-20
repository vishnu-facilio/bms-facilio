package com.facilio.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class QueryUtil {

	public static List<Map<String, Object>> fetchRecord(String moduleName,Long id) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		Condition idCondn = CriteriaAPI.getIdCondition(id, module);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.select(fields)
				.andCondition(idCondn)
				.beanClass(ModuleBaseWithCustomFields.class);
		
		return select.getAsProps();
	}
	
	public static List<Map<String, Object>> genericFetchRecord(FacilioModule module,List<FacilioField> fields,Criteria fetchCriteria,Condition condition) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				;
		
		if(fetchCriteria != null) {
			selectBuilder.andCriteria(fetchCriteria);
		}
		if(condition != null) {
			selectBuilder.andCondition(condition);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		return props;
	}
}
