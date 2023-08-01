package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TicketAPI {
	
	private static Logger LOGGER = LogManager.getLogger(TicketAPI.class.getName());


	public static List<AttachmentContext> getRelatedAttachments(long ticketId) throws Exception 
	{
		return AttachmentsAPI.getAttachments(FacilioConstants.ContextNames.TICKET_ATTACHMENTS, ticketId);
	}
	
	public static int deleteTickets(FacilioModule module, Collection<Long> recordIds) throws Exception {
		return deleteTickets(module, recordIds, -1);
	}
	
	public static Map<Long, TaskContext> getTaskMap(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
														.module(module)
														.beanClass(TaskContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		Map<Long, TaskContext> tasks = builder.getAsMap();
		if(tasks != null && !tasks.isEmpty()) {
			for (Entry<Long, TaskContext> entry : tasks.entrySet()) {
				TaskContext task = entry.getValue();
				if (task.getReadingFieldId() != -1) {
					task.setReadingField(modBean.getField(task.getReadingFieldId()));
				}
			}
			return tasks;
		}
		return null;
	}
	
	public static int deleteTickets(FacilioModule module, Collection<Long> recordIds, int level) throws Exception {
		DeleteRecordBuilder<TicketContext> builder = new DeleteRecordBuilder<TicketContext>()
															.module(module)
															.recordsPerBatch(500)
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
		int deletedRows;
		if (module.isTrashEnabled()) {
			deletedRows = builder.markAsDelete();
		} else {
			deletedRows = builder.delete();
		}
	
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
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(rdmModule))
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
	
	@Deprecated
	public static FacilioStatus getStatus(String status) throws Exception {
		// Default method to get workorder states
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		return getStatus(workorderModule, status);
	}

	public static List<FacilioStatus> getStatuses(FacilioModule module, Criteria criteria) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS))
				.beanClass(FacilioStatus.class)
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		if (criteria != null && !criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}
		return builder.get();
	}

	public static FacilioStatus getApprovalStatus(String status) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
				.beanClass(FacilioStatus.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS))
				.andCustomWhere("STATUS = ?", status)
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", "", CommonOperators.IS_EMPTY))
				.orderBy("ID");
		return builder.fetchFirst();
	}

	public static FacilioStatus getStatus(FacilioModule module, String status) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
				.beanClass(FacilioStatus.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS))
				.andCustomWhere("STATUS = ?", status)
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.orderBy("ID");
				
		List<FacilioStatus> statuses = builder.get();
		if (CollectionUtils.isNotEmpty(statuses)) {
			return statuses.get(0);
		}
		return null;
	}
	
	@Deprecated
	public static List<FacilioStatus> getStatusOfStatusType(StatusType statusType) throws Exception {
		// Default method to get workorder states
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		return getStatusOfStatusType(workorderModule, statusType);
	}
	
	public static List<FacilioStatus> getStatusOfStatusType(FacilioModule module, StatusType statusType) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
				.beanClass(FacilioStatus.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS))
				.andCustomWhere("STATUS_TYPE = ?", statusType.getIntVal())
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.orderBy("ID");
		List<FacilioStatus> statuses = builder.get();
		return statuses;
	}
	
	public static List<TicketPriorityContext> getPriorties(long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
															.moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
															.beanClass(TicketPriorityContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY))
															.orderBy("TicketPriority.SEQUENCE_NUMBER");
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

	public static TicketTypeContext getType(long orgId, String name) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketTypeContext> builder = new SelectRecordsBuilder<TicketTypeContext>()
				.table("TicketType")
				.moduleName(FacilioConstants.ContextNames.TICKET_TYPE)
				.beanClass(TicketTypeContext.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_TYPE))
				.andCustomWhere("ORGID = ? AND NAME=?", orgId,name)
				.orderBy("ID");
		return builder.get().get(0);
	}

	public static TicketTypeContext getType(long orgId, long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketTypeContext> builder = new SelectRecordsBuilder<TicketTypeContext>()
				.table("TicketType")
				.moduleName(FacilioConstants.ContextNames.TICKET_TYPE)
				.beanClass(TicketTypeContext.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_TYPE))
				.andCustomWhere("ORGID = ? AND ID=?", orgId, id)
				.orderBy("ID");
		return builder.get().get(0);
	}

		public static List<TicketTypeContext> getPlannedTypes(long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketTypeContext> builder = new SelectRecordsBuilder<TicketTypeContext>()
															.table("TicketType")
															.moduleName(FacilioConstants.ContextNames.TICKET_TYPE)
															.beanClass(TicketTypeContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_TYPE))
															.andCondition(CriteriaAPI.getCondition("NAME","name", "Preventive, Rounds, Compliance", StringOperators.IS))
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
															.orderBy("DISPLAY_NAME");
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

	public static TicketPriorityContext getPriority(long orgId, long id) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
				.table("TicketPriority")
				.moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
				.beanClass(TicketPriorityContext.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY))
				.andCustomWhere("ORGID = ? AND ID = ?", orgId, id)
				.orderBy("ID");
		List<TicketPriorityContext> categories = builder.get();
		return categories.get(0);
	}

	public static FacilioStatus getStatus(long stateId) throws Exception {
		return getStatus(AccountUtil.getCurrentOrg().getId(), stateId);
	}
	
	public static FacilioStatus getStatus(long orgId, long id) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.table("TicketStatus")
				.moduleName("ticketstatus")
				.beanClass(FacilioStatus.class)
				.select(modBean.getAllFields("ticketstatus"))
				.andCustomWhere("ORGID = ? AND ID = ?", orgId, id)
				.orderBy("ID");
		List<FacilioStatus> statuses = builder.get();
		return statuses.get(0);
	}
	
	@Deprecated
	public static List<FacilioStatus> getAllStatus(boolean ignorePreOpen) throws Exception {
		// Default method to get workorder states
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		return getAllStatus(workorderModule, ignorePreOpen);
	}

	public static List<FacilioStatus> getAllApprovalStatus() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);

		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
				.beanClass(FacilioStatus.class)
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", "", CommonOperators.IS_EMPTY))
				.select(fields);

		return builder.get();
	}
	
	public static List<FacilioStatus> getAllStatus(FacilioModule module, boolean ignorePreOpen) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);
		
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
															.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
															.beanClass(FacilioStatus.class)
															.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
															.select(fields);
		
		if (modBean.getField("moduleState", ContextNames.WORK_ORDER) != null) {
			ignorePreOpen = false;
		}
		if (ignorePreOpen) {
			FacilioField typeField = FieldFactory.getAsMap(fields).get("typeCode");
			builder.andCondition(CriteriaAPI.getCondition(typeField, String.valueOf(FacilioStatus.StatusType.PRE_OPEN.getIntVal()), NumberOperators.NOT_EQUALS));
		}
		
		 return builder.get();
	}
	
	public static void updateStatus(FacilioStatus status, FacilioModule parentModule) throws Exception {
		status.setParentModuleId(-1);
		status.setStatus(null);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		checkTicketStatus(status, ticketStatusModule, fieldMap, parentModule);
		
		UpdateRecordBuilder<FacilioStatus> builder = new UpdateRecordBuilder<FacilioStatus>()
				.module(ticketStatusModule)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(status.getId(), ticketStatusModule));
		builder.update(status);
	}
	
	public static void addStatus(FacilioStatus status, FacilioModule parentModule) throws Exception {
		if (parentModule == null) {
			throw new IllegalArgumentException("Module cannot be empty");
		}
		
		if (StringUtils.isEmpty(status.getDisplayName())) {
			throw new IllegalArgumentException("Display name cannot be empty");
		}
		if (status.getType() == null) {
			throw new IllegalArgumentException("typecode should not be empty");
		}
		
		String statusName = status.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+","");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		checkTicketStatus(status, ticketStatusModule, fieldMap, parentModule);
		
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.module(ticketStatusModule)
				.beanClass(FacilioStatus.class)
				.orderBy(ticketStatusModule.getTableName() + ".ID desc")
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentModuleId"), String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("status"), statusName, StringOperators.IS));
		criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("status"), statusName + "\\_%", StringOperators.CONTAINS));
		builder.andCriteria(criteria);
		List<FacilioStatus> list = builder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			String name = list.get(0).getStatus();
			int count = 0;
			if (name.contains("_")) {
				count = Integer.parseInt(name.substring(name.lastIndexOf('_') + 1));
			}
			statusName = statusName + "_" + (++count);
		}
		status.setStatus(statusName);
		status.setParentModuleId(parentModule.getModuleId());
		if (status.getRecordLocked() == null) {
			status.setRecordLocked(false);
		}
		if (status.getRequestedState() == null) {
			status.setRequestedState(false);
		}
		if (status.getTimerEnabled() == null) {
			status.setTimerEnabled(false);
		}
		
		InsertRecordBuilder<FacilioStatus> insertBuilder = new InsertRecordBuilder<FacilioStatus>()
				.module(ticketStatusModule)
				.fields(fields);
		insertBuilder.insert(status);
	}
	
	private static void checkTicketStatus(FacilioStatus status, FacilioModule ticketStatusModule, Map<String, FacilioField> fieldMap, FacilioModule parentModule) throws Exception {
		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
				.module(ticketStatusModule)
				.beanClass(FacilioStatus.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("displayName"), status.getDisplayName(), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentModuleId"), String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS))
				.select(Collections.singletonList(fieldMap.get("displayName")));
		if (status.getId() > 0) {
			builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(status.getId()), NumberOperators.NOT_EQUALS));
		}
		if (builder.fetchFirst() != null) {
			throw new IllegalArgumentException("Status already found");
		}		
	}

	public static List<TaskContext> getRelatedTasks(long ticketId) throws Exception {
		return getRelatedTasks(Collections.singletonList(ticketId));
	}
	
	public static List<TaskContext> getRelatedTasks(List<Long> ticketIds) throws Exception {
		return getRelatedTasks(ticketIds, true, false);
	}
	
	public static List<TaskContext> getRelatedTasks(List<Long> ticketIds, boolean fetchChildren, boolean fetchResources) throws Exception 
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
		if (CollectionUtils.isEmpty(tasks)) {
			return tasks;
		}
		
		if (fetchChildren) {
			for(TaskContext task: tasks) {
				if (task.getLastReading() == null && task.getInputTypeEnum() == InputType.READING && task.getResource() != null) {
					FacilioField readingField = modBean.getField(task.getReadingFieldId());
					ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);
					task.setLastReading(meta.getValue());
				}
			}
		}
		
		if (fetchResources) {
			List<Long> resourceIds = tasks.stream().filter(task -> task.getResource() != null && task.getResource().getId() > 0)
					.map(task -> task.getResource().getId()).collect(Collectors.toList());
			if (!resourceIds.isEmpty()) {
				 Map<Long, ResourceContext> resources = ResourceAPI.getResourceAsMapFromIds(resourceIds);
				 for(TaskContext task: tasks) {
					 if (task.getResource() != null && task.getResource().getId() > 0 && resources.containsKey(task.getResource().getId())) {
						 task.setResource(resources.get(task.getResource().getId()));
					 }
				 }
			}
		}
		return tasks;
	}

	public static List<V3TaskContext> getRelatedTasksV3(List<Long> ticketIds, boolean fetchChildren, boolean fetchResources) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields("task");
		FacilioField parentId = FieldFactory.getAsMap(fields).get("parentTicketId");

		SelectRecordsBuilder<V3TaskContext> builder = new SelectRecordsBuilder<V3TaskContext>()
				.table("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(V3TaskContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(parentId, ticketIds, NumberOperators.EQUALS))
				.orderBy("ID");

		List<V3TaskContext> tasks = builder.get();
		if (CollectionUtils.isEmpty(tasks)) {
			return tasks;
		}

		if (fetchChildren) {
			for(V3TaskContext task: tasks) {
				if (task.getLastReading() == null && task.getInputTypeEnum().equals(InputType.READING) && task.getResource() != null) {
					FacilioField readingField = modBean.getField(task.getReadingFieldId());
					ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);
					task.setLastReading(meta.getValue());
				}
			}
		}

		if (fetchResources) {
			List<Long> resourceIds = tasks.stream().filter(task -> task.getResource() != null && task.getResource().getId() > 0)
					.map(task -> task.getResource().getId()).collect(Collectors.toList());
			if (!resourceIds.isEmpty()) {
				Map<Long, ResourceContext> resources = ResourceAPI.getResourceAsMapFromIds(resourceIds);
				for(V3TaskContext task: tasks) {
					if (task.getResource() != null && task.getResource().getId() > 0 && resources.containsKey(task.getResource().getId())) {
						task.setResource(resources.get(task.getResource().getId()));
					}
				}
			}
		}
		return tasks;
	}
	public static int deleteTasks(List<Long> taskIds) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("task");
		 
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(taskIds, module));
		
		return delete.delete();
		
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

	public static Map<Long, TaskSectionContext> getTaskSectionFromParentTicketID(long parentTicketId) throws Exception {
		if(parentTicketId != -1) {
			FacilioModule module = ModuleFactory.getTaskSectionModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getTaskSectionFields())
					.table(module.getTableName())
					.andCustomWhere("(IS_PRE_REQUEST IS NULL OR NOT IS_PRE_REQUEST) AND PARENT_TICKET_ID = ?", parentTicketId);

			return getTasksSectionsFromMapList(selectBuilder.get());

		}
		return null;
	}
	
	public static List<TaskSectionContext> getTaskSections(Criteria criteria) throws Exception {
		if(criteria != null) {
			FacilioModule module = ModuleFactory.getTaskSectionModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getTaskSectionFields())
															.table(module.getTableName())
															.andCriteria(criteria);
			List<Map<String, Object>> sectionProps = selectBuilder.get();
			if(sectionProps != null && !sectionProps.isEmpty()) {
				return FieldUtil.getAsBeanListFromMapList(sectionProps, TaskSectionContext.class);
			}
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
	public static Map<Long, List<TaskContext>> groupPreRequestBySection(List<TaskContext> tasks) throws Exception {
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
	
	public static Map<Long, List<TaskContext>> sortPrerequisiteBySequence(Map<Long, List<TaskContext>> prerequisitesMap) throws Exception {
		final Map<Long, List<TaskContext>> prerequisitesMap1 = prerequisitesMap;
		if(prerequisitesMap != null && !prerequisitesMap.isEmpty()) {
			Map<Long,Integer> secSeqMap = new HashMap<>();
			secSeqMap = prerequisitesMap.entrySet().stream().filter(en -> !en.getValue().isEmpty()).collect(Collectors.toMap(en -> en.getKey() ,en -> (int)en.getValue().get(0).getSequence()));
			prerequisitesMap = secSeqMap.entrySet().stream().collect(Collectors.toMap(en -> en.getKey() ,en -> prerequisitesMap1.get(en.getKey()) , (e1, e2) -> e1,HashMap::new));
		    return prerequisitesMap;
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
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
		return 0;
	}
	
	public static void updateTicketAssignedBy(TicketContext ticket) {
		if ((ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != -1) || (ticket.getAssignmentGroup() != null && ticket.getAssignmentGroup().getId() != -1)) {
			ticket.setAssignedBy(AccountUtil.getCurrentUser());
		}
	}

	public static void updateTicketAssignedBy(List<? extends TicketContext> ticketContexts) {
		if (ticketContexts == null || ticketContexts.isEmpty()) {
			return;
		}

		for (int i = 0; i < ticketContexts.size(); i++) {
			TicketContext ticketContext = ticketContexts.get(i);
			if ((ticketContext.getAssignedTo() == null || ticketContext.getAssignedTo().getId() == -1)
					&& (ticketContext.getAssignmentGroup() == null || ticketContext.getAssignmentGroup().getId() != -1)) {
				continue;
			}

			ticketContext.setAssignedBy(AccountUtil.getCurrentUser());
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
					LOGGER.error("Exception occurred ", e);
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
		loadTicketTenants(tickets);
	}
	public static void loadWorkOrderLookups(Collection<? extends WorkOrderContext> workOrders) throws Exception {
		loadTicketStatus(workOrders);
		loadTicketPriority(workOrders);
		loadTicketCategory(workOrders);
		loadWorkOrdersUsers(workOrders);
		loadTicketGroups(workOrders);
		loadTicketResources(workOrders);
		loadTicketTenants(workOrders);

	}
	
	private static void loadTicketStatus(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			try {
				List<FacilioStatus> allStatus = getAllStatus(false);
				Map<Long, FacilioStatus> statuses = allStatus.stream().collect(Collectors.toMap(FacilioStatus::getId, Function.identity()));
				
				for(TicketContext ticket : tickets) {
					if (ticket != null) {
						FacilioStatus status = ticket.getStatus();
						if(status != null) {
							ticket.setStatus(statuses.get(status.getId()));
						}
					}
				}
			}
			catch(Exception e) {
				LOGGER.error("Exception occurred ", e);
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
				LOGGER.error("Exception occurred ", e);
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
				LOGGER.error("Exception occurred ", e);
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
	
	private static void loadWorkOrdersUsers(Collection<? extends WorkOrderContext> workOrders) throws Exception {
		if(workOrders != null && !workOrders.isEmpty()) {
			Set<Long> userId = new HashSet<>();
			for(WorkOrderContext workOrder : workOrders) {
				if (workOrder != null) {
					if (workOrder.getAssignedTo() != null) {
						userId.add(workOrder.getAssignedTo().getId());
					}
					if (workOrder.getRequestedBy() != null) {
						userId.add(workOrder.getRequestedBy().getId());
					}
					if (workOrder.getRequester() != null) {
						userId.add(workOrder.getRequester().getId());
					}
					
				}
			}
			if (!userId.isEmpty()) {
				List<Long> userIdList = new ArrayList<Long>();
				userIdList.addAll(userId);

				Map<Long, User> userMap = AccountUtil.getUserBean().getUsersAsMap(null, userIdList);

				for(WorkOrderContext workOrder : workOrders) {
					if (workOrder != null) {
						User assignTo = workOrder.getAssignedTo();
						if(assignTo != null) {
							workOrder.setAssignedTo(userMap.get(assignTo.getId()));
						}
						User requesterBy = workOrder.getRequestedBy();
						if(requesterBy != null) {
							workOrder.setRequestedBy(userMap.get(requesterBy.getId()));
						}
						User requester = workOrder.getRequester();
						if(requester != null) {
							workOrder.setRequester(userMap.get(requester.getId()));
						}
					}	
				}
			}
			
		}
	}
	
	private static void loadTicketGroups(Collection<? extends TicketContext> tickets) throws Exception {
		if(tickets != null && !tickets.isEmpty()) {
			List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getOrgId(), true, false);
			
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
			Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true, true);
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
	
	private static void loadTicketTenants(Collection<? extends TicketContext> tickets) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		
		if(tickets != null && !tickets.isEmpty()) {
			
			List<Long> tenantIds = tickets.stream()
					.filter(ticket -> ticket != null && ticket.getTenant() != null)
					.map(ticket -> ticket.getTenant().getId())
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(tenantIds)) {
				SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
						.module(module)
						.beanClass(TenantContext.class)
						.select(modBean.getAllFields(module.getName()))
						.andCondition(CriteriaAPI.getIdCondition(tenantIds, module))
						;
				Map<Long, TenantContext> tenants = builder.getAsMap();
				if(MapUtils.isNotEmpty(tenants)) {
					for(TicketContext ticket : tickets) {
						if (ticket != null) {
							TenantContext tenant = ticket.getTenant();
							if(tenant != null) {
								TenantContext tenantDetail = tenants.get(tenant.getId());
								ticket.setTenant(tenantDetail);
							}
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
														.table(module.getTableName());
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
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
					.fields(FieldFactory.getCalendarColorFields());
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));

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
		FacilioModule module = modBean.getModule(parentModuleName);
		SelectRecordsBuilder<? extends TicketContext> ticketBuilder = new SelectRecordsBuilder<TicketContext>()
																.select(modBean.getAllFields(parentModuleName))
																.moduleName(parentModuleName)
																.beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
																.andCustomWhere("Tickets.ID = ?", parentTicketId)
																;
		List<? extends TicketContext> tickets = ticketBuilder.get();
		if(tickets != null && !tickets.isEmpty()) {
			return tickets.get(0);
		}
		
		return null;
	}
	
	public static EventType getActivityTypeForTicketStatus(long statusId) throws Exception {
		FacilioStatus status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), statusId);
		Map<String, EventType> statusVsActivityType = new HashMap<>();
		statusVsActivityType.put("Resolved", EventType.SOLVE_WORK_ORDER);
		statusVsActivityType.put("Closed", EventType.CLOSE_WORK_ORDER);
		statusVsActivityType.put("Assigned", EventType.ASSIGN_TICKET);
		statusVsActivityType.put("On Hold", EventType.HOLD_WORK_ORDER);
		
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
				break;
			case TEXT:
			case READING:
				task.setReadingField(modBean.getField(task.getReadingFieldId()));
				Unit readingFieldUnit = null;		
				if(task.getReadingFieldUnit() == -1 && task.getReadingField() != null && task.getReadingField() instanceof NumberField)
				{
					NumberField readingNumberField = (NumberField) task.getReadingField();
						
					if(readingNumberField.getMetricEnum() != null && task.getResource() != null) {
						
						ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), task.getReadingField());
						if(rdm != null && rdm.getUnitEnum() != null)
						{
							readingFieldUnit = rdm.getUnitEnum();
						}
					
						else if(readingNumberField.getUnitEnum() != null)
						{
							readingFieldUnit = readingNumberField.getUnitEnum();							
						}
						else
						{
							Metric metric = readingNumberField.getMetricEnum();
							readingFieldUnit = AccountUtil.getOrgBean().getOrgDisplayUnit(metric.getMetricId());
						}

						if (readingFieldUnit != null) {
							task.setReadingFieldUnit(readingFieldUnit.getUnitId());
						}
					}
				}
					
				break;
			case BOOLEAN:
				BooleanField field = (BooleanField) modBean.getField(task.getReadingFieldId());
				task.setReadingField(field);
				task.setTruevalue(field.getTrueVal());
				task.setFalsevalue(field.getFalseVal());
				List<String> options = new ArrayList<>();
				options.add(field.getTrueVal());
				options.add(field.getFalseVal());
				task.setOptions(options);
				break;
			case RADIO:
				if (task.getReadingFieldId() != -1) {
					task.setReadingField(modBean.getField(task.getReadingFieldId()));
					task.setOptions(((EnumField)task.getReadingField()).getVisibleOptions());
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
		if (ticket.getResource() != null && ticket.getResource().getId() > 0) {
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
								throw new IllegalArgumentException("The Space - "+resource.getName()+" does not belong in the Workorder's Site.");
							} else {
								throw new IllegalArgumentException("The Space - "+resource.getName()+" does not belong in the Workorder request's Site.");
							}
							
						} else {
							if (isWorkOrder) {
								throw new IllegalArgumentException("The Asset - "+resource.getName()+" does not belong in the Workorder's Site.");
							} else {
								throw new IllegalArgumentException("The Asset - "+resource.getName()+" does not belong in the Workorder request's Site.");
							}
						}
					}
				}
			}
		} else if((ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() > 0) || (ticket.getAssignmentGroup() != null && ticket.getAssignmentGroup().getId() > 0)) {
			User assignedTo = ticket.getAssignedTo();
			Group assignmentGroup = ticket.getAssignmentGroup();
			long groupSiteId = -1;
			Set<Long> userSiteIds = new HashSet<>();
			if (ticket.getAssignedTo() != null && assignedTo.getOuid() > 0) {
				assignedTo = AccountUtil.getUserBean().getUser(assignedTo.getOuid(), true);
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
			if (assignmentGroup != null && assignmentGroup.getId() > 0) {
				assignmentGroup = AccountUtil.getGroupBean().getGroup(assignmentGroup.getId());
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
			case ML_ALARM:
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
					BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId(), true);
					try {
						if (baseSpace.getSpaceTypeEnum() == SpaceType.SITE) {
							resourceSiteId = baseSpace.getId();
						} else {
							resourceSiteId = baseSpace.getSiteId();
						}
					} catch (NullPointerException ex) {
						LOGGER.error("Base space id is " + asset.getSpaceId(), ex);
						throw ex;
					}

				}
			}
			
			if (resourceSiteId > 0) {
				if (resourceSiteId != siteId) {
					if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
						if (isWorkOrder) {
							throw new IllegalArgumentException("The Space - "+resource.getName()+" does not belong in the Workorder's Site.");
						} else {
							throw new IllegalArgumentException("The Space - "+resource.getName()+" does not belong in the Workorder request's Site.");
						}
						
					} else {
						if (isWorkOrder) {
							throw new IllegalArgumentException("The Asset - "+resource.getName()+" does not belong in the Workorder's Site.");
						} else {
							throw new IllegalArgumentException("The Asset - "+resource.getName()+" does not belong in the Workorder request's Site.");
						}
					}
				}
			}
		}
		
		User assignedTo = ticket.getAssignedTo();
		Group assignmentGroup = ticket.getAssignmentGroup();
	
		Set<Long> userSiteIds = new HashSet<>();
		if (assignedTo != null && assignedTo.getOuid() > 0) {
			assignedTo = AccountUtil.getUserBean().getUser(assignedTo.getOuid(), true);
			if (assignedTo != null) {
				List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
				Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
				if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
					for (long id : accessibleSpace) {
						BaseSpaceContext space = idVsBaseSpace.get(id);
						if (space.getSpaceTypeEnum() == SpaceType.SITE) {
							userSiteIds.add(space.getId());
						} else {
							userSiteIds.add(space.getSiteId());
						}
					}
				}
			}
			else {
				ticket.setAssignedTo(null); //Setting assigned to as null if there's no such user
			}
		}
		
		long groupSiteId = -1;
		if (assignmentGroup != null && assignmentGroup.getId() != -1) {
			assignmentGroup = AccountUtil.getGroupBean().getGroup(assignmentGroup.getId());
			groupSiteId = assignmentGroup.getSiteId();
		}
	
		if (groupSiteId > 0 && groupSiteId != siteId) {
			throw new IllegalArgumentException("The Team does not belong to current site.");
		}
		
		if (!userSiteIds.isEmpty() && !userSiteIds.contains(siteId)) {
			throw new IllegalArgumentException("The User does not belong to current site.");
		}
		
	}
	public static void associateTenant (TicketContext ticket) throws Exception {
		if (ticket.getResource() != null && ticket.getResource().getId() != -1) {
			if (ticket.getTenant() != null && ticket.getTenant().getId() > 0 ) {
				if (AccountUtil.getCurrentOrg().getOrgId() != 320l) {
					List<TenantContext> tenants = TenantsAPI.getAllTenantsForResource(ticket.getResource().getId());
					if (tenants == null || tenants.stream().noneMatch(tenant -> tenant.getId() == ticket.getTenant().getId())) {
						throw new IllegalArgumentException("The tenant associated doesn't belong to the workorder space/asset");
					}
				}
			}
			else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
				TenantContext tenant = TenantsAPI.getTenantForResource(ticket.getResource().getId());
				ticket.setTenant(tenant);
			}
		}
	}

	// For PM
	public static void associateTenant(List<? extends TicketContext> tickets) throws Exception {
		if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
			return;
		}

		List<Long> resourceIds = tickets.stream().filter(i -> i.getResource() != null && i.getResource().getId() != -1).map(i -> i.getResource().getId()).collect(Collectors.toList());

		if (resourceIds != null && CollectionUtils.isEmpty(resourceIds)) {
			return;
		}
		
		Map<Long, TenantContext> tenants = TenantsAPI.getTenantForResources(resourceIds);

		for (int i = 0; i < tickets.size(); i++) {
			TicketContext ticketContext = tickets.get(i);
			ResourceContext resource = ticketContext.getResource();
			if (resource == null || resource.getId() == -1) {
				continue;
			}
			ticketContext.setTenant(tenants.get(resource.getId()));
		}
	}

	public static void updateTaskCount(Collection parentIds) throws Exception {
		if (CollectionUtils.isNotEmpty(parentIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = "task";

			FacilioField parentIdField = modBean.getField("parentTicketId", moduleName);
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(parentIdField);
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			FacilioField preRequestField = new FacilioField();
			preRequestField.setName("IS_PRE_REQUEST");
			preRequestField.setColumnName("IS_PRE_REQUEST");
			preRequestField.setDataType(FieldType.BOOLEAN);
			
			Condition condition = CriteriaAPI.getCondition(parentIdField, parentIds, NumberOperators.EQUALS);
			Condition notPreRequestCondition = CriteriaAPI.getCondition(preRequestField, "1" , NumberOperators.NOT_EQUALS);
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentIdField.getCompleteColumnName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(condition).andCondition(notPreRequestCondition);
			
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
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(completedStatusCondition).andCondition(notPreRequestCondition);
			
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
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ticketModule))
						.andCondition(idFieldCondition);
				recordBuilder.update(updateMap);
			}
		}		
	}
}
