package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.*;

public class DeleteTaskCommand extends FacilioCommand implements PostTransactionCommand {

	private Set<Long> idsToUpdateTaskCount;
	private String moduleName;

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
			
			idsToUpdateTaskCount = parentIds;
			this.moduleName = moduleName;
//			for (Long parentId : parentIds) {
//				FacilioChain.addPostTransactionListObject(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, parentId);
//			}
//			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		}
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		TicketAPI.updateTaskCount(idsToUpdateTaskCount);
		return false;
	}
}