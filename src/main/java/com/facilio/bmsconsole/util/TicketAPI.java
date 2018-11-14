package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.CalendarColorContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class TicketAPI {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(TicketAPI.class.getName());


	public static List<AttachmentContext> getRelatedAttachments(long ticketId) throws Exception 
	{
		return AttachmentsAPI.getAttachments(FacilioConstants.ContextNames.TICKET_ATTACHMENTS, ticketId);
	}
	
	public static int deleteTickets(FacilioModule module, Collection<Long> recordIds) throws Exception {
		return deleteTickets(module, recordIds, -1);
	}
	
	public static int deleteTickets(FacilioModule module, Collection<Long> recordIds, int level) throws Exception {
		DeleteRecordBuilder<TicketContext> builder = new DeleteRecordBuilder<TicketContext>()
															.module(module)
															.andCondition(CriteriaAPI.getIdCondition(recordIds, module));

		if (level != -1) {
			builder.level(level);
		}
		
		List<Map<String, Object>> props = null;
		if (module.getName().equals("workorder")) {
			FacilioModule ticketModule = ModuleFactory.getTicketsModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.table(ticketModule.getTableName())
					.select(FieldFactory.getTicketFields(ticketModule))
					.andCondition(CriteriaAPI.getIdCondition(recordIds, ticketModule));
			props = selectBuilder.get();
		}
		
		int deletedRows = builder.delete();
	
		if (props != null && !props.isEmpty()) {
			Set<Long> userIds = props.stream().map(e -> (Long) e.get("assignedTo")).filter(e -> e != null).collect(Collectors.toSet());
			if (userIds != null && !userIds.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> uwhFields = modBean.getAllFields("userworkhoursreading");
				FacilioModule uwhModule = modBean.getModule("userworkhoursreading");
				String tableName = uwhModule.getTableName();
				
				Map<String, FacilioField> uwhFieldMap = FieldFactory.getAsMap(uwhFields); 
				
				uwhFields = new ArrayList<>(uwhFields); //mutable
				uwhFields.add(FieldFactory.getField("second_id", "second_table.ID", FieldType.NUMBER));
				uwhFields.add(FieldFactory.getIdField(uwhModule));
				
				
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
						.table(tableName)
						.select(uwhFields)
						.leftJoin(tableName + " second_table")
						.on("second_table.ORGID = ? AND second_table.PARENT_ID="+tableName+".PARENT_ID AND "+tableName+".TTIME < second_table.TTIME")
						.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), uwhModule))
						.andCondition(CriteriaAPI.getCondition("User_Workhour_Readings.PARENT_ID", "parentId", StringUtils.join(userIds, ","), NumberOperators.EQUALS))
						.andCustomWhere("second_table.ID IS NULL", AccountUtil.getCurrentOrg().getOrgId());
				
				List<Map<String, Object>> readingProps = selectBuilder.get();
				Map<Long, Map<String, Long>> userIDvsReadings = new HashMap<>();
				if (readingProps != null && !readingProps.isEmpty()) {
					readingProps.stream().forEach(e -> {
						Map<String, Long> map = new HashMap<>();
						map.put("ttime", (Long) e.get("ttime"));
						map.put("woId", (Long) e.get("woId"));
						map.put("workHoursEntry", new Long((Integer) e.get("workHoursEntry")));
						map.put("id", (Long) e.get("id"));
						if (e.get("sourceActivity") != null) {
							map.put("sourceActivity", (Long) e.get("sourceActivity"));
						} else {
							map.put("sourceActivity", -1l);
						}
						userIDvsReadings.put((Long) e.get("parentId"), map);
					});
				}
				
				List<FacilioField> rdmFields = FieldFactory.getReadingDataMetaFields();
				Map<String, FacilioField> fieldMap =  FieldFactory.getAsMap(rdmFields);
				
				FacilioField rdmresourceId = fieldMap.get("resourceId");
				FacilioField rdmfieldId = fieldMap.get("fieldId");
				
				List<Long> readingFields = Arrays.asList(uwhFieldMap.get("woId").getFieldId(), uwhFieldMap.get("workHoursEntry").getFieldId(), uwhFieldMap.get("sourceActivity").getFieldId());
				
				FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
						.table(rdmModule.getTableName())
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(rdmModule))
						.andCondition(CriteriaAPI.getCondition(rdmresourceId, userIds, NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(rdmfieldId, readingFields, NumberOperators.EQUALS));
				deleteBuilder.delete();
				
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReadingDataMetaModule().getTableName())
						.fields(FieldFactory.getReadingDataMetaFields());
				
				long orgId = AccountUtil.getCurrentOrg().getId();
				ReadingInputType type = ReadingsAPI.getRDMInputTypeFromModuleType(uwhModule.getTypeEnum());
				for (long userId: userIds) {
					Map<String, Long> map = userIDvsReadings.get(userId);
					if (map == null) {
						map = new HashMap<>();
						map.put("ttime", System.currentTimeMillis());
						map.put("woId", -1l);
						map.put("workHoursEntry", -1l);
						map.put("id", -1l);
						map.put("sourceActivity", -1l);
					}
					
					ReadingDataMeta woId = new ReadingDataMeta();
					woId.setOrgId(orgId);
					woId.setTtime(map.get("ttime"));
					woId.setValue(map.get("woId"));
					woId.setFieldId(uwhFieldMap.get("woId").getId());
					woId.setResourceId(userId);
					woId.setInputType(type);
					woId.setReadingDataId(map.get("id"));
					insertBuilder.addRecord(FieldUtil.getAsProperties(woId));
					
					ReadingDataMeta whe = new ReadingDataMeta();
					whe.setOrgId(orgId);
					whe.setTtime(map.get("ttime"));
					whe.setValue(map.get("workHoursEntry"));
					whe.setFieldId(uwhFieldMap.get("workHoursEntry").getId());
					whe.setResourceId(userId);
					whe.setInputType(type);
					whe.setReadingDataId(map.get("id"));
					insertBuilder.addRecord(FieldUtil.getAsProperties(whe));
					
					ReadingDataMeta sourceActivity = new ReadingDataMeta();
					sourceActivity.setOrgId(orgId);
					sourceActivity.setTtime(map.get("ttime"));
					sourceActivity.setValue(map.get("sourceActivity"));
					sourceActivity.setFieldId(uwhFieldMap.get("sourceActivity").getId());
					sourceActivity.setResourceId(userId);
					sourceActivity.setInputType(type);
					sourceActivity.setReadingDataId(map.get("id"));
					insertBuilder.addRecord(FieldUtil.getAsProperties(sourceActivity));
				}
				insertBuilder.save();
			}
		}
		
		return deletedRows;
	}
	
	public static TicketStatusContext getStatus(String status) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
															.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
															.beanClass(TicketStatusContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS))
															.andCustomWhere("STATUS = ?", status)
															.orderBy("ID");
		List<TicketStatusContext> statuses = builder.get();
		return statuses.get(0);
	}
	
	public static List<TicketPriorityContext> getPriorties(long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
															.moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
															.beanClass(TicketPriorityContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY))
															.orderBy("ID");
		return builder.get();
	}
	
	public static List<TicketTypeContext> getTypes(long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketTypeContext> builder = new SelectRecordsBuilder<TicketTypeContext>()
															.table("TicketType")
															.moduleName(FacilioConstants.ContextNames.TICKET_TYPE)
															.beanClass(TicketTypeContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_TYPE))
															.andCustomWhere("ORGID = ?", orgId)
															.orderBy("ID");
		return builder.get();
	}
	
	public static List<TicketCategoryContext> getCategories(long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketCategoryContext> builder = new SelectRecordsBuilder<TicketCategoryContext>()
															.table("TicketCategory")
															.moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
															.beanClass(TicketCategoryContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY))
															.andCustomWhere("ORGID = ?", orgId)
															.orderBy("ID");
		return builder.get();
	}
	
	public static TicketCategoryContext getCategory(long orgId, String category) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketCategoryContext> builder = new SelectRecordsBuilder<TicketCategoryContext>()
															.table("TicketCategory")
															.moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
															.beanClass(TicketCategoryContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY))
															.andCustomWhere("ORGID = ? AND NAME = ?", orgId, category)
															.orderBy("ID");
		List<TicketCategoryContext> categories = builder.get();
		return categories.get(0);
	}
	
	public static TicketCategoryContext getCategory(long orgId, long id) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketCategoryContext> builder = new SelectRecordsBuilder<TicketCategoryContext>()
															.table("TicketCategory")
															.moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
															.beanClass(TicketCategoryContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY))
															.andCustomWhere("ORGID = ? AND ID = ?", orgId, id);
															
		List<TicketCategoryContext> categories = builder.get();
		return categories.get(0);
	}
	
	public static TicketPriorityContext getPriority(long orgId, String priority) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
															.table("TicketPriority")
															.moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
															.beanClass(TicketPriorityContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY))
															.andCustomWhere("ORGID = ? AND PRIORITY = ?", orgId, priority)
															.orderBy("ID");
		List<TicketPriorityContext> categories = builder.get();
		return categories.get(0);
	}
	
	public static TicketStatusContext getStatus(long orgId, long id) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
															.table("TicketStatus")
															.moduleName("ticketstatus")
															.beanClass(TicketStatusContext.class)
															.select(modBean.getAllFields("ticketstatus"))
															.andCustomWhere("ORGID = ? AND ID = ?", orgId, id)
															.orderBy("ID");
		List<TicketStatusContext> statuses = builder.get();
		return statuses.get(0);
	}
	
	public static List<TicketStatusContext> getAllStatus() throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
															.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
															.beanClass(TicketStatusContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS));
		 return builder.get();
	}
	
	public static List<TaskContext> getRelatedTasks(long ticketId) throws Exception {
		return getRelatedTasks(Collections.singletonList(ticketId));
	}
	
	public static List<TaskContext> getRelatedTasks(List<Long> ticketIds) throws Exception {
		return getRelatedTasks(ticketIds, true);
	}
	
	public static List<TaskContext> getRelatedTasks(List<Long> ticketIds, boolean fetchChildren) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields("task");
		FacilioField parentId = FieldFactory.getAsMap(fields).get("parentTicketId");
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
				.table("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(TaskContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(parentId, ticketIds, NumberOperators.EQUALS))
				.orderBy("ID");

		List<TaskContext> tasks = builder.get();
		
		if (fetchChildren) {
			for(TaskContext task: tasks) {
				if (task.getLastReading() == null && task.getInputTypeEnum() == InputType.READING && task.getResource() != null) {
					FacilioField readingField = modBean.getField(task.getReadingFieldId());
					ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);
					task.setLastReading(meta.getValue());
				}
			}
		}
		return tasks;
	}
	
	private static Map<Long, TaskSectionContext> getTasksSectionsFromMapList(List<Map<String, Object>> sectionProps) {
		Map<Long, TaskSectionContext> sections = new HashMap<>();
		if(sectionProps != null && !sectionProps.isEmpty()) {
			for(Map<String, Object> sectionProp : sectionProps) {
				TaskSectionContext section = FieldUtil.getAsBeanFromMap(sectionProp, TaskSectionContext.class);
				sections.put(section.getId(), section);
			}
		}
		return sections;
	}
	
	public static Map<Long, TaskSectionContext> getRelatedTaskSections(long parentTicketId) throws Exception {
		if(parentTicketId != -1) {
			FacilioModule module = ModuleFactory.getTaskSectionModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getTaskSectionFields())
															.table(module.getTableName())
															.andCustomWhere("PARENT_TICKET_ID = ?", parentTicketId);
			
			return getTasksSectionsFromMapList(selectBuilder.get());
			
		}
		return null;
	}
	
	public static Map<Long, TaskSectionContext> getTaskSections(List<Long> sectionIds) throws Exception {
		if (sectionIds != null && !sectionIds.isEmpty()) {
			FacilioModule module = ModuleFactory.getTaskSectionModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getTaskSectionFields())
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getIdCondition(sectionIds, module));
			return getTasksSectionsFromMapList(selectBuilder.get());
		}
		return null;
	}
	public static TaskSectionContext getTaskSection(Long sectionId) throws Exception {
		if (sectionId != null) {
			FacilioModule module = ModuleFactory.getTaskSectionModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getTaskSectionFields())
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getIdCondition(sectionId, module));
			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props !=  null && !props.isEmpty()) {
				TaskSectionContext section = FieldUtil.getAsBeanFromMap(props.get(0), TaskSectionContext.class);
				
				return section;
			}
		}
		return null;
	}
	
	public static Map<Long, List<TaskContext>> groupTaskBySection(List<TaskContext> tasks) throws Exception {
		if(tasks != null && !tasks.isEmpty()) {
			Map<Long, List<TaskContext>> taskMap = new HashMap<>();
			for(TaskContext task : tasks) {
				List<TaskContext> taskList = taskMap.get(task.getSectionId());
				if (taskList == null) {
					taskList = new ArrayList<>();
					taskMap.put(task.getSectionId(), taskList);
				}
				taskList.add(task);
			}
			return taskMap;
		}
		return null;
	}
	
	public static List<NoteContext> getRelatedNotes(long ticketId) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TICKET_NOTES);
		
		SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
																.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_NOTES))
																.module(module)
																.beanClass(NoteContext.class)
																.andCustomWhere("PARENT_ID = ?", ticketId);
		
		return selectBuilder.get();
	}
	
	public static void loadRelatedModules(TicketContext ticket) throws Exception {
		if(ticket != null) {
			ticket.setTaskSections(getRelatedTaskSections(ticket.getId()));
			ticket.setTasks(groupTaskBySection(getRelatedTasks(ticket.getId())));
			ticket.setNotes(getRelatedNotes(ticket.getId()));
			ticket.setAttachments(getRelatedAttachments(ticket.getId()));
			if(ticket.getResource() != null)
			{
				ticket.setResource(ResourceAPI.getResource(ticket.getResource().getId()));
			}
		}
	}
	
	public static List<String> getTaskInputOptions(long taskId) throws Exception {
		FacilioModule module = ModuleFactory.getTaskInputOptionModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTaskInputOptionsFields())
														.table(module.getTableName())
														.andCustomWhere("TASK_ID = ?", taskId)
														;
		
		List<Map<String, Object>> optionList = selectBuilder.get();
		List<String> options = new ArrayList<>();
		for(Map<String, Object> option : optionList) {
			options.add((String) option.get("option"));
		}
		return options;
	}
	
	private static List<FacilioField> maxSerialNumberField = null;
	public static long getMaxSerialNumberOfOrg(long orgId) throws Exception {
		
		if(maxSerialNumberField == null) {
			FacilioField field = new FacilioField();
			field.setColumnName("MAX(SERIAL_NUMBER)");
			field.setName("maxSerialNumber");
			field.setDataType(FieldType.NUMBER);
			
			maxSerialNumberField = new ArrayList<>();
			maxSerialNumberField.add(field);
		}
		
		try{
			GenericSelectRecordBuilder builder =  new GenericSelectRecordBuilder()
														.select(maxSerialNumberField)
														.table("Tickets")
														.andCustomWhere("ORGID = ?", orgId);
			
			List<Map<String, Object>> maxValue = builder.get();
			if(maxValue != null && !maxValue.isEmpty() && maxValue.get(0).get("maxSerialNumber") != null) {
				return (long) maxValue.get(0).get("maxSerialNumber");
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return 0;
	}
	
	public static void updateTicketAssignedBy(TicketContext ticket) {
		if ((ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != -1) || (ticket.getAssignmentGroup() != null && ticket.getAssignmentGroup().getId() != -1)) {
			ticket.setAssignedBy(AccountUtil.getCurrentUser());
		}
	}
	
	public static void updateTicketStatus(TicketContext ticket) throws Exception {
		updateTicketStatus(null, ticket, null, false);
	}
	
	public static void updateTicketStatus(ActivityType activityType, TicketContext ticket, TicketContext oldTicket, boolean isWorkDurationChangeAllowed) throws Exception {
		TicketStatusContext statusObj = ticket.getStatus();
		
		if(statusObj != null) {
			statusObj = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), statusObj.getId());
		}
		else {
			ticket.setStatus(TicketAPI.getStatus("Submitted"));
		}
		
		if((ticket.getAssignedTo() != null || ticket.getAssignmentGroup() != null) && (statusObj == null || statusObj.getStatus().equals("Submitted"))) {
			ticket.setStatus(TicketAPI.getStatus("Assigned"));
		}
		statusObj = ticket.getStatus();
		
		if (oldTicket != null && statusObj != null) {
			if ("Work in Progress".equalsIgnoreCase(statusObj.getStatus())) {
				if (oldTicket.getActualWorkStart() != -1) {
					ticket.setResumedWorkStart(System.currentTimeMillis());
				}
				else {
					assignTicketToCurrentUser(ticket, oldTicket);
					ticket.setActualWorkStart(ticket.getOfflineWorkStart() != -1 ? ticket.getOfflineWorkStart() : System.currentTimeMillis());
				}
			}
			else if ("On Hold".equalsIgnoreCase(statusObj.getStatus()) || "Resolved".equalsIgnoreCase(statusObj.getStatus()) 
					|| ("Closed".equalsIgnoreCase(statusObj.getStatus()) && oldTicket.getStatus().getId() != TicketAPI.getStatus("Resolved").getId()) ) {

				long estimatedDuration;
				if ("Resolved".equalsIgnoreCase(statusObj.getStatus()) || "Closed".equalsIgnoreCase(statusObj.getStatus())) {
					ticket.setActualWorkEnd(ticket.getOfflineWorkEnd() != -1 ? ticket.getOfflineWorkEnd() : System.currentTimeMillis());
					estimatedDuration = TicketAPI.getEstimatedWorkDuration(oldTicket, ticket.getActualWorkEnd());
					if(isWorkDurationChangeAllowed) {
						long actualDuration = ticket.getActualWorkDuration() != -1 ? ticket.getActualWorkDuration() : ticket.getEstimatedWorkDuration();
						ticket.setActualWorkDuration(actualDuration);
						if (estimatedDuration == -1 && actualDuration != -1){
							estimatedDuration = ticket.getActualWorkDuration();
						}
					}
					else {
						ticket.setActualWorkDuration(estimatedDuration);
					}
				}
				else {
					estimatedDuration = TicketAPI.getEstimatedWorkDuration(oldTicket, System.currentTimeMillis());
				}
				
				if (estimatedDuration != -1) {
					ticket.setEstimatedWorkDuration(estimatedDuration);
				}
			}
		}
		
	}
	
	private static void assignTicketToCurrentUser(TicketContext ticket, TicketContext oldTicket) {
		if (oldTicket.getAssignedTo() == null) {
			User currentUser = AccountUtil.getCurrentUser();
			ticket.setAssignedTo(currentUser);
			ticket.setAssignedBy(currentUser);
			if (oldTicket.getAssignmentGroup() != null) {
				List<Long> ids = new ArrayList<Long>();
				try {
					List<Group> myGroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
					if (myGroups != null && !myGroups.isEmpty()) {
						ids = myGroups.stream().map(Group::getId).collect(Collectors.toList());
					}
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				
				if (!ids.contains(oldTicket.getAssignmentGroup().getId())) {
					ticket.setAssignmentGroup(new Group());
				}
			}
		}
	}
	
public static Map<Long, TicketContext> getTickets(String ids) throws Exception {
		
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(ModuleFactory.getTicketsModule()));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET);
		
		SelectRecordsBuilder<TicketContext> selectBuilder = new SelectRecordsBuilder<TicketContext>()
																	.select(fields)
																	.table("Tickets")
																	.moduleName(FacilioConstants.ContextNames.TICKET)
																	.beanClass(TicketContext.class)
																	.andCondition(idCondition)
																	.maxLevel(0);
		Map<Long, TicketContext> tickets = selectBuilder.getAsMap();
		
		loadTicketStatus(tickets.values());
		loadTicketPriority(tickets.values());
		loadTicketCategory(tickets.values());
		loadTicketUsers(tickets.values());
		loadTicketGroups(tickets.values());
		loadTicketResources(tickets.values());
		
		return tickets;
	}
	
	
	public static void loadTicketLookups(Collection<? extends TicketContext> tickets) throws Exception {
		loadTicketStatus(tickets);
		loadTicketPriority(tickets);
		loadTicketCategory(tickets);
		loadTicketUsers(tickets);
		loadTicketGroups(tickets);
		loadTicketResources(tickets);
	}
	
	private static void loadTicketStatus(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);
			
			try {
				SelectRecordsBuilder<TicketStatusContext> selectBuilder = new SelectRecordsBuilder<TicketStatusContext>()
																				.select(fields)
																				.table("TicketStatus")
																				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
																				.beanClass(TicketStatusContext.class);
				Map<Long, TicketStatusContext> statuses = selectBuilder.getAsMap();
				
				for(TicketContext ticket : tickets) {
					if (ticket != null) {
						TicketStatusContext status = ticket.getStatus();
						if(status != null) {
							ticket.setStatus(statuses.get(status.getId()));
						}
					}
				}
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
				throw e;
			}
		}
	}
	
	private static void loadTicketPriority(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY);
			
			try{
				SelectRecordsBuilder<TicketPriorityContext> selectBuilder = new SelectRecordsBuilder<TicketPriorityContext>()
																				.select(fields)
																				.table("TicketPriority")
																				.moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
																				.beanClass(TicketPriorityContext.class);
				Map<Long, TicketPriorityContext> priorities = selectBuilder.getAsMap();
				
				for(TicketContext ticket : tickets) {
					if (ticket != null) {
						TicketPriorityContext priority = ticket.getPriority();
						if(priority != null) {
							ticket.setPriority(priorities.get(priority.getId()));
						}
					}
				}
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
				throw e;
			}
		}
	}
	
	private static void loadTicketCategory(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY);
			
			try {
				SelectRecordsBuilder<TicketCategoryContext> selectBuilder = new SelectRecordsBuilder<TicketCategoryContext>()
																				.select(fields)
																				.table("TicketCategory")
																				.moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
																				.beanClass(TicketCategoryContext.class);
				Map<Long, TicketCategoryContext> categories = selectBuilder.getAsMap();
				
				for(TicketContext ticket : tickets) {
					if (ticket != null) {
						TicketCategoryContext category = ticket.getCategory();
						if(category != null) {
							ticket.setCategory(categories.get(category.getId()));
						}
					}
				}
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
				throw e;
			}
		}
	}
	
	private static void loadTicketUsers(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			List<User> users = AccountUtil.getOrgBean().getOrgUsers(AccountUtil.getCurrentOrg().getOrgId(), true);
			
			Map<Long, User> userMap = new HashMap<>();
			for(User user : users) {
				userMap.put(user.getId(), user);
			}
			
			for(TicketContext ticket : tickets) {
				if (ticket != null) {
					User assignTo = ticket.getAssignedTo();
					if(assignTo != null) {
						ticket.setAssignedTo(userMap.get(assignTo.getId()));
					}
				}
			}
		}
	}
	
	private static void loadTicketGroups(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getOrgId(), true);
			
			Map<Long, Group> groupMap = new HashMap<>();
			for(Group group : groups) {
				groupMap.put(group.getId(), group);
			}
			
			for(TicketContext ticket : tickets) {
				if (ticket != null) {
					Group assignGroup = ticket.getAssignmentGroup();
					if(assignGroup != null) {
						ticket.setAssignmentGroup(groupMap.get(assignGroup.getId()));
					}
				}
			}
		}
	}
	
	private static void loadTicketResources(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			List<Long> resourceIds = tickets.stream()
											.filter(ticket -> ticket != null && ticket.getResource() != null)
											.map(ticket -> ticket.getResource().getId())
											.collect(Collectors.toList());
			Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
			if(resources != null && !resources.isEmpty()) {
				for(TicketContext ticket : tickets) {
					if (ticket != null) {
						ResourceContext resource = ticket.getResource();
						if(resource != null) {
							ResourceContext resourceDetail = resources.get(resource.getId());
							ticket.setResource(resourceDetail);
						}
					}
				}
			}
		}
	}
	
	public static CalendarColorContext getCalendarColor() throws Exception {
		FacilioModule module = ModuleFactory.getCalendarColorModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getCalendarColorFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		List<Map<String, Object>> results = selectBuilder.get();
		if(results != null && !results.isEmpty())
		{
			Map<String, Object> prop = results.get(0);
			return FieldUtil.getAsBeanFromMap(prop, CalendarColorContext.class);
		}
		return new CalendarColorContext();
	}
	
	public static void setCalendarColor(CalendarColorContext calendarColor) throws Exception {
		FacilioModule module = ModuleFactory.getCalendarColorModule();
		calendarColor.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		Map<String, Object> colorProp = FieldUtil.getAsProperties(calendarColor);
		CalendarColorContext currentColor = getCalendarColor();
		if (currentColor != null && currentColor.getId() != -1) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getCalendarColorFields())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));

			updateBuilder.update(colorProp);
		}
		else {
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getCalendarColorFields())
					.addRecord(colorProp);

			insertBuilder.save();
		}
	}
	
	public static long getEstimatedWorkDuration (TicketContext ticket, long actualWorkEnd) {
		long duration = -1;
		long workEndInSec = actualWorkEnd/1000;
		if (ticket.getResumedWorkStart() != -1 && ticket.getEstimatedWorkDuration() != -1) {
			duration = ticket.getEstimatedWorkDuration();
			duration += (workEndInSec - (ticket.getResumedWorkStart()/1000));
		}
		else if (ticket.getActualWorkStart() != -1) {
			duration = workEndInSec - (ticket.getActualWorkStart()/1000);
		}
		
		return duration;
	}
	
	@SuppressWarnings("unchecked")
	public static TicketContext getParentTicket (long parentTicketId, String parentModuleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<? extends TicketContext> ticketBuilder = new SelectRecordsBuilder<TicketContext>()
																.select(modBean.getAllFields(parentModuleName))
																.moduleName(parentModuleName)
																.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(parentModuleName))
																.andCustomWhere("Tickets.ID = ?", parentTicketId)
																;
		List<? extends TicketContext> tickets = ticketBuilder.get();
		if(tickets != null && !tickets.isEmpty()) {
			return tickets.get(0);
		}
		
		return null;
	}
	
	public static ActivityType getActivityTypeForTicketStatus(long statusId) throws Exception {
		TicketStatusContext status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), statusId);
		Map<String, ActivityType> statusVsActivityType = new HashMap<>();
		statusVsActivityType.put("Resolved", ActivityType.SOLVE_WORK_ORDER);
		statusVsActivityType.put("Closed", ActivityType.CLOSE_WORK_ORDER);
		statusVsActivityType.put("Assigned", ActivityType.ASSIGN_TICKET);
		statusVsActivityType.put("On Hold", ActivityType.HOLD_WORK_ORDER);
		
		if (statusVsActivityType.containsKey(status.getStatus())) {
			return statusVsActivityType.get(status.getStatus());
		}
		return null;
	}
	
	public static void setTasksInputData (List<TaskContext> tasks) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(TaskContext task : tasks) {
			switch(task.getInputTypeEnum()) {
			case NUMBER:
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", Long.toString(task.getReadingFieldId()), NumberOperators.EQUALS));
				criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
				List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
				if (readingRules != null && !readingRules.isEmpty()) {
					for (ReadingRuleContext r:  readingRules) {
						long workflowId = r.getWorkflowId();
						if (workflowId != -1) {
							r.setWorkflow(WorkflowUtil.getWorkflowContext(workflowId, true));
						}
					}
				}
				task.setReadingRules(readingRules);
			case TEXT:
			case READING:
			case BOOLEAN:
				task.setReadingField(modBean.getField(task.getReadingFieldId()));
				break;
			case RADIO:
				if (task.getReadingFieldId() != -1) {
					task.setReadingField(modBean.getField(task.getReadingFieldId()));
					task.setOptions(((EnumField)task.getReadingField()).getValues());
				}
				else {
					task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
				}
				break;
//			case CHECKBOX:
//				task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
//				if(task.getInputValue() != null && !task.getInputValue().isEmpty()) {
//					task.setInputValues(Arrays.asList(task.getInputValue().split("\\s*,\\s*")));
//				}
//				else {
//					task.setInputValues(Collections.emptyList());
//				}
//				break;
			default:
				break;
		}
		}
	}

	public static <T extends TicketContext> void validateSiteSpecificData(T ticket, List<T> oldTickets) throws Exception {
		boolean isWorkOrder = ticket instanceof WorkOrderContext;
		if (ticket.getResource() != null) {
			ResourceContext resource = ResourceAPI.getResource(ticket.getResource().getId());
			long resourceSiteId = -1;
			if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
				BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getId());
				if (baseSpace.getSpaceTypeEnum() == SpaceType.SITE) {
					resourceSiteId = baseSpace.getId();
				} else {
					resourceSiteId = baseSpace.getSiteId();
				}
			} else {
				AssetContext asset = AssetsAPI.getAssetInfo(resource.getId(), false); //check deleted ?
				if (asset.getSpaceId() > 0) {
					BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId());
					if (baseSpace.getSpaceTypeEnum() == SpaceType.SITE) {
						resourceSiteId = baseSpace.getId();
					} else {
						resourceSiteId = baseSpace.getSiteId();
					}
				}
			}
			
			if (resourceSiteId > 0) {
				for(T oldWo: oldTickets) {
					long siteId = oldWo.getSiteId();
					if (resourceSiteId != siteId) {
						if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
							if (isWorkOrder) {
								throw new IllegalArgumentException("The Space does not belong in the Workorder's Site.");
							} else {
								throw new IllegalArgumentException("The Space does not belong in the Workorder request's Site.");
							}
							
						} else {
							if (isWorkOrder) {
								throw new IllegalArgumentException("The Asset does not belong in the Workorder's Site.");
							} else {
								throw new IllegalArgumentException("The Asset does not belong in the Workorder request's Site.");
							}
						}
					}
				}
			}
		} else if(ticket.getAssignedTo() != null || ticket.getAssignmentGroup() != null) {
			User assignedTo = ticket.getAssignedTo();
			Group assignmentGroup = ticket.getAssignmentGroup();
			long groupSiteId = -1;
			Set<Long> userSiteIds = new HashSet<>();
			if (ticket.getAssignedTo() != null && assignedTo.getOuid() != -1) {
				assignedTo = AccountUtil.getUserBean().getUser(assignedTo.getOuid());
				List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
				Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
				if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
					for (long id: accessibleSpace) {
						BaseSpaceContext space = idVsBaseSpace.get(id);
						if (space.getSpaceTypeEnum() == SpaceType.SITE) {
							userSiteIds.add(space.getId());
						} else {
							userSiteIds.add(space.getSiteId());
						}
					}
				}
			} 
			if (assignmentGroup != null && assignmentGroup.getGroupId() != -1) {
				assignmentGroup = AccountUtil.getGroupBean().getGroup(assignmentGroup.getGroupId());
				groupSiteId = assignmentGroup.getSiteId();
			}
			
			for (TicketContext oldWo: oldTickets) {
				long siteId = oldWo.getSiteId();
				if (groupSiteId > 0 && groupSiteId != siteId) {
					throw new IllegalArgumentException("The Team does not belong to current site.");
				}
				
				if (!userSiteIds.isEmpty() && !userSiteIds.contains(siteId)) {
					throw new IllegalArgumentException("The User does not belong to current site.");
				}
			}
		}
	}
	
	private static <T extends TicketContext> void skipSiteValidation(T ticket) {
		SourceType sourceType = ticket.getSourceTypeEnum();
		if (sourceType == null) {
			if (AccountUtil.getCurrentSiteId() != -1) {
				ticket.setSiteId(AccountUtil.getCurrentSiteId());
			}
			return;
		}
		
		switch (sourceType) { 
		case THRESHOLD_ALARM:
		case ANOMALY_ALARM:
		case ALARM:
			return;
		default:
			if (AccountUtil.getCurrentSiteId() != -1) {
				ticket.setSiteId(AccountUtil.getCurrentSiteId());
			}
			break;
		}
	}

	public static <T extends TicketContext> void validateSiteSpecificData(T ticket) throws Exception {
		long siteId = ticket.getSiteId();
		skipSiteValidation(ticket);
		
		if (siteId == -1) {
			return;
		}
		
		boolean isWorkOrder = ticket instanceof WorkOrderContext;
		if (ticket.getResource() != null && ticket.getResource().getId() != -1) {
			ResourceContext resource = ResourceAPI.getResource(ticket.getResource().getId());
			long resourceSiteId = -1;
			if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
				BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getId());
				if (baseSpace.getSpaceTypeEnum() == SpaceType.SITE) {
					resourceSiteId = baseSpace.getId();
				} else {
					resourceSiteId = baseSpace.getSiteId();
				}
			} else {
				AssetContext asset = AssetsAPI.getAssetInfo(resource.getId(), false); //check deleted ?
				if (asset.getSpaceId() > 0) {
					BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId());
					if (baseSpace.getSpaceTypeEnum() == SpaceType.SITE) {
						resourceSiteId = baseSpace.getId();
					} else {
						resourceSiteId = baseSpace.getSiteId();
					}
				}
			}
			
			if (resourceSiteId > 0) {
				if (resourceSiteId != siteId) {
					if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
						if (isWorkOrder) {
							throw new IllegalArgumentException("The Space does not belong in the Workorder's Site.");
						} else {
							throw new IllegalArgumentException("The Space does not belong in the Workorder request's Site.");
						}
						
					} else {
						if (isWorkOrder) {
							throw new IllegalArgumentException("The Asset does not belong in the Workorder's Site.");
						} else {
							throw new IllegalArgumentException("The Asset does not belong in the Workorder request's Site.");
						}
					}
				}
			}
		}
		
		User assignedTo = ticket.getAssignedTo();
		Group assignmentGroup = ticket.getAssignmentGroup();
	
		Set<Long> userSiteIds = new HashSet<>();
		if (assignedTo != null && assignedTo.getOuid() != -1) {
			assignedTo = AccountUtil.getUserBean().getUser(assignedTo.getOuid());
			List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
			Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
			if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
				for (long id: accessibleSpace) {
					BaseSpaceContext space = idVsBaseSpace.get(id);
					if (space.getSpaceTypeEnum() == SpaceType.SITE) {
						userSiteIds.add(space.getId());
					} else {
						userSiteIds.add(space.getSiteId());
					}
				}
			}
		}
		
		long groupSiteId = -1;
		if (assignmentGroup != null && assignmentGroup.getGroupId() != -1) {
			assignmentGroup = AccountUtil.getGroupBean().getGroup(assignmentGroup.getGroupId());
			groupSiteId = assignmentGroup.getSiteId();
		}
	
		if (groupSiteId > 0 && groupSiteId != siteId) {
			throw new IllegalArgumentException("The Team does not belong to current site.");
		}
		
		if (!userSiteIds.isEmpty() && !userSiteIds.contains(siteId)) {
			throw new IllegalArgumentException("The User does not belong to current site.");
		}
		
	}
}
