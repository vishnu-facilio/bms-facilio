package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetImportHistoryListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		int count = (int) context.get(FacilioConstants.ContextNames.COUNT);
		Integer importMode = (Integer) context.get(FacilioConstants.ContextNames.IMPORT_MODE);
		String moduleName = (String) context.get(ImportAPI.ImportProcessConstants.CHOOSEN_MODULE);
		Long moduleId = (long) -1;
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		if (moduleName != null) {
			module = bean.getModule(moduleName);
			moduleId = module.getModuleId();
		}
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.select(FieldFactory.getImportProcessFields())
				.orderBy("IMPORT_TIME desc");
		if (count > 0) {
			selectRecordBuilder.limit(count);
		}
		if (importMode > 0) {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition("IMPORT_MODE", "importMode", importMode.toString(), NumberOperators.EQUALS));
		}
		if (moduleId > 0) {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", moduleId.toString(), NumberOperators.EQUALS));
		} else if (module != null && StringUtils.isNotEmpty(module.getName())) {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", module.getName(), StringOperators.IS));
		}
		List<Map<String, Object>> props = selectRecordBuilder.get();
		
		List<ImportProcessContext> importContexts = new ArrayList<>();
		
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				ImportProcessContext importContext = FieldUtil.getAsBeanFromMap(prop, ImportProcessContext.class);
				importContexts.add(importContext);
			}
		}
		
		context.put(FacilioConstants.ContextNames.RECORD_LIST, importContexts);
		
		return false;
	}

}
