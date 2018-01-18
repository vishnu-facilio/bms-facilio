package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class TicketAPI {
	
	private static Logger logger = Logger.getLogger(TicketAPI.class.getName());
	
	public static List<AttachmentContext> getRelatedAttachments(long ticketId) throws Exception 
	{
		return AttachmentsAPI.getAttachments(FacilioConstants.ContextNames.TICKET_ATTACHMENTS, ticketId);
	}
	
	public static TicketStatusContext getStatus(String status) throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
																.table("TicketStatus")
																.moduleName("ticketstatus")
																.beanClass(TicketStatusContext.class)
																.select(modBean.getAllFields("ticketstatus"))
																.andCustomWhere("STATUS = ?", status)
																.orderBy("ID");
			List<TicketStatusContext> statuses = builder.get();
			return statuses.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static TicketCategoryContext getCategory(long orgId, String category) throws Exception
	{
		try
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
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static TicketPriorityContext getPriority(long orgId, String priority) throws Exception
	{
		try
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
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static TicketStatusContext getStatus(long orgId, long id) throws Exception
	{
		try
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
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static List<TicketStatusContext> getAllStatus() throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
																.table("TicketStatus")
																.moduleName("ticketstatus")
																.beanClass(TicketStatusContext.class)
																.select(modBean.getAllFields("ticketstatus"));
			 return builder.get();
		
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static List<TaskContext> getRelatedTasks(long ticketId) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());
		List<FacilioField> fields = modBean.getAllFields("task");
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
				.table("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(TaskContext.class)
				.select(fields)
				.andCustomWhere("PARENT_TICKET_ID = ?", ticketId)
				.orderBy("ID");

		List<TaskContext> tasks = builder.get();	
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
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
	
	public static void updateTicketStatus(TicketContext ticket) throws Exception {
		TicketStatusContext status = ticket.getStatus();
		
		if(status != null) {
			status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), status.getId());
		}
		else {
			ticket.setStatus(TicketAPI.getStatus("Submitted"));
		}
		
		if(ticket.getAssignedTo() != null && (status == null || status.getStatus().equals("Submitted"))) {
			ticket.setStatus(TicketAPI.getStatus("Assigned"));
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
		loadTicketSpaces(tickets.values());
		
		return tickets;
	}
	
	
	public static void loadTicketLookups(Collection<? extends TicketContext> tickets) throws Exception {
		loadTicketStatus(tickets);
		loadTicketPriority(tickets);
		loadTicketCategory(tickets);
		loadTicketUsers(tickets);
		loadTicketGroups(tickets);
		loadTicketSpaces(tickets);
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
					TicketStatusContext status = ticket.getStatus();
					if(status != null) {
						ticket.setStatus(statuses.get(status.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
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
					TicketPriorityContext priority = ticket.getPriority();
					if(priority != null) {
						ticket.setPriority(priorities.get(priority.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
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
					TicketCategoryContext category = ticket.getCategory();
					if(category != null) {
						ticket.setCategory(categories.get(category.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
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
				User assignTo = ticket.getAssignedTo();
				if(assignTo != null) {
					ticket.setAssignedTo(userMap.get(assignTo.getId()));
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
				Group assignGroup = ticket.getAssignmentGroup();
				if(assignGroup != null) {
					ticket.setAssignmentGroup(groupMap.get(assignGroup.getId()));
				}
			}
		}
	}
	
	private static void loadTicketSpaces(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			try {
				List<BaseSpaceContext> spaces = SpaceAPI.getAllBaseSpaces(null,null);
				
				Map<Long, BaseSpaceContext> spaceMap = new HashMap<>();
				for(BaseSpaceContext space : spaces) {
					spaceMap.put(space.getId(), space);
				}
				
				for(TicketContext ticket : tickets) {
					BaseSpaceContext space = ticket.getSpace();
					if(space != null) {
						ticket.setSpace(spaceMap.get(space.getId()));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
}
