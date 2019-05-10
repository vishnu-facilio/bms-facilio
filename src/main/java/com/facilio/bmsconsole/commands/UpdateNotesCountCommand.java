package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;

public class UpdateNotesCountCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(UpdateNotesCountCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		String ticketModule = (String) context.get("ticketmodule");
		String moduleString = (String) context.get("moduleName");
		Collection<Long> parentIds = (Collection<Long>) context.get(FacilioConstants.ContextNames.IDS_TO_UPDATE_COUNT);
		try {
		
		if (StringUtils.isNoneEmpty(ticketModule) && CollectionUtils.isNotEmpty(parentIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField noOfNotesField = modBean.getField("noOfNotes", ticketModule);
			FacilioModule tModule = modBean.getModule(ticketModule);
			FacilioModule module = modBean.getModule(moduleString);
			
			FacilioField parentIdField = modBean.getField("parentId", moduleString);
			if (parentIdField == null) {
				// log for testing, will remove it once its fixed
				LOGGER.error("moduleString inside UpdateNotesCountCommand: " + moduleString);
			}
	
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
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tModule))
						.andCondition(CriteriaAPI.getIdCondition(id, tModule))
						;
				
				updateRecordBuilder.updateViaMap(updateMap);
			}
		}
		}
		catch(Exception e) {
			LOGGER.error("Exception in UpdateNotesCountCommand: moduleString" + moduleString + ", ticketModule:" + ticketModule + ",parentIds: "+ parentIds, e);
			CommonCommandUtil.emailException("UpdateNotesCountCommand", "Exception in UpdateNotesCountCommand - " + AccountUtil.getCurrentOrg().getId(), e);
		}
		return false;
	}

}
