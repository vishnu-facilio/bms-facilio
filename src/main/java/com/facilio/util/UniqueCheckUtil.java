package com.facilio.util;

import java.util.Collections;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class UniqueCheckUtil {

	
	public static boolean checkIfUnique(String currentValue,String moduleName,String uniqueFieldName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioField uniqueField = modBean.getField(uniqueFieldName, moduleName);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.select(Collections.singletonList(uniqueField))
				.beanClass(ModuleBaseWithCustomFields.class)
				.andCondition(CriteriaAPI.getCondition(uniqueField, currentValue, StringOperators.IS));
		
		
		List<ModuleBaseWithCustomFields> res = select.get();
		
		if(res != null && !res.isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
}
