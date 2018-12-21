package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class UpdateNotesCountCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String ticketModule = (String) context.get("ticketmodule");
		String moduleString = (String) context.get("moduleName");
		Set<Long> parentIds = (Set<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		
		if (StringUtils.isNoneEmpty(ticketModule) && CollectionUtils.isNotEmpty(parentIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField noOfNotesField = modBean.getField("noOfNotes", ticketModule);
			FacilioModule tModule = modBean.getModule(ticketModule);
			FacilioModule module = modBean.getModule(moduleString);
			
			FacilioField parentIdField = modBean.getField("parentId", moduleString);
	
			List<FacilioField> fields = new ArrayList<>();
			fields.add(parentIdField);
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentIdField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(parentIdField, parentIds, NumberOperators.EQUALS));
			
			List<Map<String, Object>> list = select.get();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("parentId")).longValue();
				int noOfNotes = ((Number) map.get("count")).intValue();
				
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("noOfNotes", noOfNotes);
				
				UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(tModule)
						.fields(Collections.singletonList(noOfNotesField))
						.andCondition(CriteriaAPI.getIdCondition(id, tModule))
						;
				
				updateRecordBuilder.update(updateMap);
			}
		}
		return false;
	}

}
