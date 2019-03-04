package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateTaskCountCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(UpdateTaskCountCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		Collection<Long> parentIds = (Collection<Long>) context.get(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT);
		if (CollectionUtils.isNotEmpty(parentIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = "task";

			LOGGER.log(Level.SEVERE, "module name: " + moduleName + " parentids: " + StringUtils.join(parentIds.toArray(), ", "));
			
			FacilioField parentIdField = modBean.getField("parentTicketId", moduleName);
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(parentIdField);
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			Condition condition = CriteriaAPI.getCondition(parentIdField, parentIds, NumberOperators.EQUALS);
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentIdField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(condition);
			
			List<Map<String, Object>> totalCountList = select.get();
			
			Map<Long, MutablePair<Integer, Integer>> updatedValues = new HashMap<>();
			for (Map<String, Object> map : totalCountList) {
				long id = ((Number) map.get("parentTicketId")).longValue();
				MutablePair<Integer, Integer> pair = new MutablePair<>();
				pair.setLeft(((Number) map.get("count")).intValue());
				updatedValues.put(id, pair);
			}
			
			FacilioField statusField = modBean.getField("statusNew", moduleName);
			Condition completedStatusCondition = CriteriaAPI.getCondition(statusField, NumberOperators.EQUALS);
			completedStatusCondition.setValue(String.valueOf(2));
			select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentIdField.getCompleteColumnName())
					.andCondition(condition)
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(completedStatusCondition);
			
			List<Map<String, Object>> completedCountList = select.get();
			
			for (Map<String, Object> map : completedCountList) {
				long id = ((Number) map.get("parentTicketId")).longValue();
				MutablePair<Integer, Integer> pair = updatedValues.get(id);
				if (pair == null) {
					pair = new MutablePair<>();
				}
				pair.setRight(((Number) map.get("count")).intValue());
			}

			String ticketModuleName = "ticket";
			FacilioModule ticketModule = modBean.getModule(ticketModuleName);
			
			FacilioField noOfTasksField = new FacilioField();
			noOfTasksField.setName("noOfTasks");
			noOfTasksField.setColumnName("NO_OF_TASKS");
			noOfTasksField.setDataType(FieldType.NUMBER);
			
			FacilioField noOfClosedTasksField = new FacilioField();
			noOfClosedTasksField.setName("noOfClosedTasks");
			noOfClosedTasksField.setColumnName("NO_OF_CLOSED_TASKS");
			noOfClosedTasksField.setDataType(FieldType.NUMBER);
			
			for (Long id: updatedValues.keySet()) {
				Map<String, Object> updateMap = new HashMap<>();
				MutablePair<Integer,Integer> pair = updatedValues.get(id);
				
				updateMap.put("noOfTasks", pair.getLeft() == null ? 0 : pair.getLeft());
				updateMap.put("noOfClosedTasks", pair.getRight() == null ? 0 : pair.getRight());
				
				FacilioField idField = FieldFactory.getIdField(ticketModule);
				Condition idFieldCondition = CriteriaAPI.getCondition(idField, NumberOperators.EQUALS);
				idFieldCondition.setValue(String.valueOf(id));
				
				GenericUpdateRecordBuilder recordBuilder = new GenericUpdateRecordBuilder()
						.table(ticketModule.getTableName())
						.fields(Arrays.asList(noOfTasksField, noOfClosedTasksField))
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ticketModule))
						.andCondition(idFieldCondition);
				recordBuilder.update(updateMap);
			}
		}
		return false;
	}

}
