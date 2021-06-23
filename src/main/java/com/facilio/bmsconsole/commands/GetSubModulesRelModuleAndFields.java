package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetSubModulesRelModuleAndFields extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule parentModule = null;
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(context.containsKey(FacilioConstants.ContextNames.MODULE_NAME) && context.get(FacilioConstants.ContextNames.MODULE_NAME) != null) {

			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			parentModule = modBean.getModule(moduleName);
		}
		else {
			Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
			parentModule = modBean.getModule(moduleId);
		}
		
		if(parentModule != null) {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getSubModulesRelModule().getTableName())
					.select(FieldFactory.getSubModuleRelFields())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentModuleId"), parentModule.getModuleId()+"", NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			List<FacilioModule> modules = new ArrayList<FacilioModule>();
			if(props != null && !props.isEmpty()) {
				for(Map<String, Object> prop :props) {
					Long subModuleId = (Long)prop.get("childModuleId");
					
					FacilioModule subModule = modBean.getModule(subModuleId);
					List<FacilioField> fields = modBean.getAllFields(subModule.getName());
					subModule.setFields(fields);
					
					modules.add(subModule);
				}
			}
			context.put(FacilioConstants.ContextNames.SUB_MODULES, modules);
		}
		return false;
	}

}
