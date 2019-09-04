package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetImportHistoryListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		int count = (int) context.get(FacilioConstants.ContextNames.COUNT);
		Integer importMode = (Integer) context.get(FacilioConstants.ContextNames.IMPORT_MODE);
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
