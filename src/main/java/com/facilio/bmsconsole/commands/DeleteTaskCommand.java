package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;

public class DeleteTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		if (recordIds != null && !recordIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule("task");
			FacilioField parentTicketId = modBean.getField("parentTicketId", "task");
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(parentTicketId);
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
					.table(dataTableName)
					.select(fields)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), recordIds, NumberOperators.EQUALS));
			List<Map<String, Object>> list = selectRecordBuilder.get();
			Set<Long> parentIds = new HashSet<>();
			for (Map<String, Object> map : list) {
				parentIds.add((Long) map.get("parentTicketId"));
			}
			
			String sql = "ID IN (";
			for (int i=0; i < recordIds.size(); i++) {
				if (i != 0) {
					sql += ",";
				}
				sql += "?";
			}
			sql += ")";
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(dataTableName)
					.andCustomWhere(sql, recordIds.toArray());
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
			for (Long parentId : parentIds) {
				FacilioChain.addPostTransactionListObject(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, parentId);
			}
			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		}
		return false;
	}
}