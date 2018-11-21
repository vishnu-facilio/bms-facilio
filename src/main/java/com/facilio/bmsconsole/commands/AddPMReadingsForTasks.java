package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.BooleanField;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddPMReadingsForTasks implements Command {

	private static final int MAX_FIELDS_PER_TYPE_PER_MODULE = 5;
	private static final int MAX_LENGTH_OF_FIELD_NAME = 97;
	private static final int MAX_LENGTH_OF_MODULE_NAME = 95;
	private static final Logger LOGGER = LogManager.getLogger(AddPMReadingsForTasks.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if (tasks != null && !tasks.isEmpty()) {
			Map<FieldType, List<FacilioField>> fieldMap = createAndSplitFields(tasks);
			context.put(FacilioConstants.ContextNames.MODULE_LIST, createAndAddReadings(pm.getTitle(), fieldMap));	// db addition
			context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE, ReadingDataMeta.ReadingInputType.TASK);
			context.put(FacilioConstants.ContextNames.READING_RULES_LIST, getReadingRules(tasks));
			context.put(FacilioConstants.ContextNames.ACTIONS_LIST, getActionsList(tasks));
			updateTaskFieldId(tasks);
		}
		return false;
	}
	
	private List<List<List<ActionContext>>> getActionsList(List<TaskContext> tasks) {
		List<List<List<ActionContext>>> actionsList = tasks.stream().map(TaskContext::getActionsList).collect(Collectors.toList());
		tasks.stream().forEach(t -> t.setActionsList(null));
		return actionsList;
	}
	
	private List<List<ReadingRuleContext>> getReadingRules(List<TaskContext> tasks) {
		List<List<ReadingRuleContext>> readingRules = tasks.stream().map(t -> {
			List<ReadingRuleContext> r = t.getReadingRules();
			if (r != null && !r.isEmpty()) {
				r.stream().forEach(rule -> {
					long id = t.getReadingField().getFieldId();
					rule.setReadingFieldId(id);
					FacilioField f = t.getReadingField();
					if (f != null) {
						rule.getEvent().setModuleId(f.getModuleId());
					}
				});
			}
			return r;
		}).collect(Collectors.toList());
		
		tasks.stream().forEach(t -> t.setReadingRules(null));
		
		return readingRules;
	}
	
	private Map<FieldType, List<FacilioField>> createAndSplitFields(List<TaskContext> tasks) throws Exception {
		Map<FieldType, List<FacilioField>> fieldMap = new HashMap<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(TaskContext task : tasks) {
			switch(task.getInputTypeEnum()) {
				case TEXT:
					if (task.getReadingFieldId() == -1) {
						FacilioField stringField = checkAndAddField(task.getSubject(), FieldType.STRING, task.getUniqueId(), fieldMap);
						task.setReadingField(stringField);
					}
					else {
						checkAndUpdateField(task, modBean);	// update pm case
					}
					break;
				case NUMBER:
					if (task.getReadingFieldId() == -1) {
						NumberField numberField = (NumberField) checkAndAddField(task.getSubject(), FieldType.DECIMAL, task.getUniqueId(), fieldMap);
						task.setReadingField(numberField);
					}
					else {
						checkAndUpdateField(task, modBean);
					}
					break;
				case RADIO:
					if (task.getReadingFieldId() == -1) {
						EnumField enumField = (EnumField) checkAndAddField(task.getSubject(), FieldType.ENUM, task.getUniqueId(), fieldMap);
						enumField.setValues(task.getOptions());
						task.setReadingField(enumField);
					}
					else {
						checkAndUpdateField(task, modBean);
					}
					break;
				case BOOLEAN:
					if (task.getReadingFieldId() == -1) {
						BooleanField booleanField = (BooleanField) checkAndAddField(task.getSubject(), FieldType.BOOLEAN, task.getUniqueId(), fieldMap);
						booleanField.setTrueVal(task.getOptions().get(0));
						booleanField.setFalseVal(task.getOptions().get(1));
						task.setReadingField(booleanField);
					}
					else {
						checkAndUpdateField(task, modBean);
					}
					break;
				default:
					break;
			}
		}
		return fieldMap;
	}
	
	private FacilioField checkAndAddField(String fieldName, FieldType type, long uniqueId, Map<FieldType, List<FacilioField>> fieldMap) {
		String displayName = StringUtils.abbreviate(fieldName, MAX_LENGTH_OF_FIELD_NAME);
		String name = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+","") + "_" +uniqueId;
		FacilioField field = FieldFactory.getField(name, displayName, null, null, type);
		List<FacilioField> fields = fieldMap.get(type);
		if (fields == null) {
			fields = new ArrayList<>();
			fieldMap.put(type, fields);
		}
		fields.add(field);
		return field;
	}
	
	private void checkAndUpdateField (TaskContext task, ModuleBean modBean) throws Exception {
		String displayName = StringUtils.abbreviate(task.getSubject(), MAX_LENGTH_OF_FIELD_NAME);
		FacilioField field = modBean.getField(task.getReadingFieldId());
		task.setReadingField(field);
		if (!field.getDisplayName().equals(displayName)) {
			FacilioField updateField = new FacilioField();
			updateField.setFieldId(task.getReadingFieldId());
			updateField.setDisplayName(displayName);
			modBean.updateField(updateField);
			field.setDisplayName(displayName);
		}
	}
	
	private List<FacilioModule> createAndAddReadings(String pmTitle, Map<FieldType, List<FacilioField>> fieldMap) throws Exception {
		if (!fieldMap.isEmpty()) {
			List<FacilioField> fieldList = new ArrayList<>();
			String moduleName = StringUtils.abbreviate(pmTitle, MAX_LENGTH_OF_MODULE_NAME);
			List<FacilioModule> modules = new ArrayList<>();
			while (!fieldMap.isEmpty()) {
				Iterator<List<FacilioField>> fieldsItr = fieldMap.values().iterator();
				while (fieldsItr.hasNext()) {
					List<FacilioField> fields = fieldsItr.next();
					Iterator<FacilioField> itr = fields.iterator();
					int count = 0;
					while (itr.hasNext() && count < MAX_FIELDS_PER_TYPE_PER_MODULE) {
						fieldList.add(itr.next());
						count++;
						itr.remove();
					}
					if (fields.isEmpty()) {
						fieldsItr.remove();
					}
				}
				
				if (!fieldList.isEmpty()) {
					modules.add(addReading(moduleName, fieldList));		// module addition done here
					fieldList = new ArrayList<>();
				}
				else {
					break;
				}
			}
			return modules;
		}
		return null;
	}

	private FacilioModule addReading(String readingName, List<FacilioField> fields) throws Exception {
		addDefaultPMReadingFields(fields);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READING_NAME, readingName);
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "PM_Readings");
		
		Chain addReadingChain = TransactionChainFactory.getAddReadingsChain();
		addReadingChain.execute(context);
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		module.setFields(fields);
		return module;
	}
	
	private void addDefaultPMReadingFields(List<FacilioField> fields) {
		fields.add(FieldFactory.getField("woId", "Workorder ID", "WO_ID", null, FieldType.NUMBER));
		fields.add(FieldFactory.getField("taskId", "Task ID", "TASK_ID", null, FieldType.NUMBER));
		fields.add(FieldFactory.getField("taskUniqueId", "Task Unique ID", "TASK_UNIQUE_ID", null, FieldType.NUMBER));
	}
	
	private void updateTaskFieldId(List<TaskContext> tasks) {
		// TODO Auto-generated method stub
		for(TaskContext task : tasks) {
			if (task.getReadingField() != null) {
				if (task.getReadingField().getFieldId() == -1) {
					throw new IllegalArgumentException("PM Readings have not been added properly");
				}
				task.setReadingFieldId(task.getReadingField().getFieldId());
				task.setReadingField(null);
				task.setOptions(null);
			}
		}
	}
}
