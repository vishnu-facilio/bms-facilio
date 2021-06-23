package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class UpdateAttachmentCountUpdateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Collection<Long> list = (List<Long>) context.get(FacilioConstants.ContextNames.IDS_TO_UPDATE_COUNT);
		String attachmentModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		String moduleName = null;
		switch (attachmentModuleName) {
			case "ticketattachments":
				moduleName = "ticket";
				break;
				
			case "taskattachments":
				moduleName = "task";
				break;
		}
		
		if (list != null && !list.isEmpty() && StringUtils.isNotBlank(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			List<FacilioField> fields = new ArrayList<>();
			FacilioField parentTicketId = modBean.getField("parentId", attachmentModuleName);
			fields.add(parentTicketId);
			
			FacilioModule module = parentTicketId.getModule();
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentTicketId.getCompleteColumnName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(parentTicketId, list, NumberOperators.EQUALS));
			List<Map<String, Object>> rs = recordBuilder.get();
			
			if (!rs.isEmpty()) {
				FacilioModule ticketModule = modBean.getModule(moduleName);
				
				FacilioField attachmentField = new FacilioField();
				attachmentField.setName("noOfAttachments");
				attachmentField.setColumnName("NO_OF_ATTACHMENTS");
				attachmentField.setDataType(FieldType.NUMBER);
				
				List<FacilioField> updateFields = new ArrayList<>();
				updateFields.add(attachmentField);
				
				Condition idFieldCondition = CriteriaAPI.getCondition(FieldFactory.getIdField(ticketModule), NumberOperators.EQUALS);
				for (Map<String, Object> map : rs) {
					Long id = ((Number) map.get("parentId")).longValue();
					idFieldCondition.setValue(String.valueOf(id));
					
					GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
							.table(ticketModule.getTableName())
							.fields(updateFields)
//							.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ticketModule))
							.andCondition(idFieldCondition);
					
					Map<String, Object> updateMap = new HashMap<String, Object>();
					updateMap.put("noOfAttachments", ((Number) map.get("count")).intValue());
					
					updateRecordBuilder.update(updateMap);
				}
			}
		}
		return false;
	}

}
