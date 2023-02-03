package com.facilio.beans;

import java.io.File;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.jobs.DataPendingAlertJob;
import com.facilio.bmsconsole.jobs.DataProcessingAlertJob;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.modules.*;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.plannedmaintenance.ExecutorBase;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.plannedmaintenance.ScheduleExecutor;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.handler.AuditLogHandler;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.SingletonMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.metrics.AgentMetrics;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.actions.ReadingAction;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.workflows.context.WorkflowContext;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;

public class ModuleCRUDBeanImpl implements ModuleCRUDBean {

	private static final Logger LOGGER = LogManager.getLogger(ModuleCRUDBeanImpl.class.getName());

	@Override
	public AlarmContext processAlarm(JSONObject alarmInfo) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarmInfo);

		FacilioChain addAlarmChain = TransactionChainFactory.getAddAlarmFromEventChain();
		addAlarmChain.execute(context);
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		return alarm;
	}

	@Override
	public int deleteAlarm(List<Long> id) throws Exception {
		// TODO Auto-generated method stub
		if (id != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

			FacilioChain deleteAlarmChain = FacilioChainFactory.getDeleteAlarmChain();
			deleteAlarmChain.execute(context);

			int rowsDeleted = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			return rowsDeleted;
		}
		return 0;
	}

	public ControllerContext getController(String deviceName, String deviceId,Long agentId) throws Exception{
		if(deviceId != null && deviceName != null && agentId != null) {
			return ControllerAPI.getController(deviceName, deviceId,agentId);
		}
		return null;
	}

    public ControllerContext addController(ControllerContext controller) throws Exception{
        if(controller != null) {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.CONTROLLER_SETTINGS, controller);
            FacilioChain addcontrollerSettings = FacilioChainFactory.getAddControllerChain();
            addcontrollerSettings.execute(context);
        }
        return controller;
    }


	@Override
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest, List<File> attachedFiles, List<String> attachedFileNames, List<String> attachedFilesContentType) throws Exception {
		// TODO Auto-generated method stub
		if (workOrderRequest != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REQUESTER, workOrderRequest.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workOrderRequest);

			if (attachedFiles != null && !attachedFiles.isEmpty() && attachedFileNames != null && !attachedFileNames.isEmpty() && attachedFilesContentType != null && !attachedFilesContentType.isEmpty()) {
				context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, attachedFiles);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, attachedFileNames);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, attachedFilesContentType);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			}

			Command addWorkOrderRequest = FacilioChainFactory.getAddWorkOrderRequestChain();
			addWorkOrderRequest.execute(context);
			return workOrderRequest.getId();
		}
		return -1;
	}

	@Override
	public long addWorkOrderFromEmail(WorkOrderContext workOrder, List<File> attachedFiles, List<String> attachedFileNames,
			List<String> attachedFilesContentType) throws Exception {
		if (workOrder != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REQUESTER, workOrder.getRequester());
			context.put(FacilioConstants.ApprovalRule.APPROVAL_REQUESTER, workOrder.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
			context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);

			FacilioStatus preOpenStatus = TicketAPI.getStatus("preopen");
			workOrder.setStatus(preOpenStatus);

			if (attachedFiles != null && !attachedFiles.isEmpty() && attachedFileNames != null && !attachedFileNames.isEmpty() && attachedFilesContentType != null && !attachedFilesContentType.isEmpty()) {
				context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, attachedFiles);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, attachedFileNames);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, attachedFilesContentType);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			}

			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			FacilioContext ctx = V3Util.createRecord(woModule, FieldUtil.getAsJSON(workOrder));
			return ((V3WorkOrderContext) ctx.get("workorder")).getId();

		}
		return -1;
	}


	@Override
	public int updateAlarmFromJson(JSONObject alarmInfo, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarmInfo);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);

		FacilioChain updateAlarm = TransactionChainFactory.updateAlarmFromJsonChain();
		updateAlarm.execute(context);

		return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
	}

	@Override
	public List<WorkOrderContext> addWorkOrderFromPM(Context context, PreventiveMaintenance pm) throws Exception {
		return addWorkOrderFromPM(context, pm, -1);
	}

	@Override
	public List<WorkOrderContext> addWorkOrderFromPM(Context context, PreventiveMaintenance pm, long templateId) throws Exception {
		// TODO Auto-generated method stub
		if (pm != null) {

			List<WorkOrderContext> workOrderContexts = new ArrayList<>();

			if(templateId == -1) {
				templateId = pm.getTemplateId();
			}
			Map<Long, PMResourcePlannerContext> resourcePlanners = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			//PreventiveMaintenanceAPI.addJobPlanSectionsToWorkorderTemplate(pm, workorderTemplate);							 un command here to start using job plans 
			WorkOrderContext wo = null;
			List<Long> resourceIds = null;

			if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
				Long resourceId = pm.getBaseSpaceId();
				if (resourceId == null || resourceId < 0) {
					resourceId = pm.getSiteId();
				}
				resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),resourceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), false);
			} else if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
				Long baseSpaceId = pm.getBaseSpaceId();
				List<Long> scope;
				if (baseSpaceId == null || baseSpaceId < 0) {
					scope = PreventiveMaintenanceAPI.getPMSites(pm.getId());
				} else {
					scope = Collections.singletonList(baseSpaceId);
				}
				resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),scope,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), true);
			}
			else {
				resourceIds = Collections.singletonList(workorderTemplate.getResourceIdVal());
			}

			for (Long resourceId : resourceIds) {

				Map<String, List<TaskContext>> taskMap = null;

				if (resourcePlanners != null && resourcePlanners.containsKey(resourceId)) {
					PMResourcePlannerContext resourcePlanner = resourcePlanners.get(resourceId);
					if (resourcePlanner.getAssignedToId() != null && resourcePlanner.getAssignedToId() > 0) {
						workorderTemplate.setAssignedToId(resourcePlanner.getAssignedToId());
					}
				}

				wo = workorderTemplate.getWorkorder();

				wo.setJobStatus(WorkOrderContext.JobsStatus.COMPLETED);

				int preRequisiteCount = workorderTemplate.getPreRequestSectionTemplates().size();
				wo.setPrerequisiteEnabled(preRequisiteCount != 0);
				if (wo.getPrerequisiteEnabled()) {
					wo.setPreRequestStatus(PreRequisiteStatus.NOT_STARTED.getValue());
				} else {
					wo.setPreRequestStatus(PreRequisiteStatus.COMPLETED.getValue());
				}

				wo.setResource(ResourceAPI.getResource(resourceId));
				taskMap = workorderTemplate.getTasks();

				Map<String, List<TaskContext>> taskMapForNewPmExecution = null;    // should be handled in above if too

				boolean isNewPmType = false;

				if (workorderTemplate.getSectionTemplates() != null) {
					for (TaskSectionTemplate sectiontemplate : workorderTemplate.getSectionTemplates()) {// for new pm_Type section should be present and every section should have a AssignmentType
						if (sectiontemplate.getAssignmentType() < 0) {
							isNewPmType = false;
							break;
						} else {
							isNewPmType = true;
						}
					}
				}

				PMTriggerContext pmTrigger = (PMTriggerContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER);
				wo.setTrigger(pmTrigger);

				if (isNewPmType) {
					Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
					if (woTemplateResourceId > 0) {
						Long currentTriggerId = null;
						if (pmTrigger != null) {
							currentTriggerId = pmTrigger.getId();
						}
						taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(workorderTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId, pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE);
					}
				} else {
					taskMapForNewPmExecution = workorderTemplate.getTasks();
				}
				if (taskMapForNewPmExecution != null) {
					taskMap = taskMapForNewPmExecution;
				}
				Map<String, List<TaskContext>> preRequestMap = workorderTemplate.getPreRequests();

				Map<String, List<TaskContext>> preRequestMapForNewPmExecution = null;
				isNewPmType = false;

				if (workorderTemplate.getPreRequestSectionTemplates() != null) {
					for (TaskSectionTemplate sectiontemplate : workorderTemplate.getPreRequestSectionTemplates()) {
						if (sectiontemplate.getAssignmentType() < 0) {
							isNewPmType = false;
							break;
						} else {
							isNewPmType = true;
						}
					}
				}

				if (isNewPmType) {
					Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
					if (woTemplateResourceId > 0) {
						Long currentTriggerId = null;
						if (pmTrigger != null) {
							currentTriggerId = pmTrigger.getId();
						}
						preRequestMapForNewPmExecution = PreventiveMaintenanceAPI.getPreRequestMapForNewPMExecution(
								workorderTemplate.getPreRequestSectionTemplates(), woTemplateResourceId,
								currentTriggerId, pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE);
					}
				} else {
					preRequestMapForNewPmExecution = workorderTemplate.getPreRequests();
				}
				if (preRequestMapForNewPmExecution != null) {
					preRequestMap = preRequestMapForNewPmExecution;
				}
				wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
				wo.setPm(pm);
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
				context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
				context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, preRequestMap);
				context.put(FacilioConstants.ContextNames.PREREQUISITE_APPROVERS_LIST, workorderTemplate.getPrerequisiteApprovers());
				context.put(FacilioConstants.ContextNames.IS_PM_EXECUTION, true);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, wo.getAttachments());

				if (AccountUtil.getCurrentOrg().getOrgId() == 218L) {
					if (taskMapForNewPmExecution == null || taskMapForNewPmExecution.isEmpty()) {
						LOGGER.log(Level.ERROR, "PMID : " + pm.getId() + " No taskmap");
					}
				}

				//Temp fix. Have to be removed eventually
				PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);
				FacilioChain addWOChain = TransactionChainFactory.getAddWorkOrderChain();
				addWOChain.execute(context);

				// if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.SINGLE) { //Need to be handled for multiple resources, it causes deadlock
				// incrementPMCount(pm);
				// }
				workOrderContexts.add(wo);
			}
			return workOrderContexts;
		}
		return null;
	}

	private void incrementPMCount(PreventiveMaintenance pm) throws Exception {
//		if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
//			TransactionManager tm = FTransactionManager.getTransactionManager();
//			if (tm != null) {
//				Transaction t = tm.getTransaction();
//				LOGGER.info("Connection & free connection size before incrementing PM count : "+((FacilioTransaction) t).getConnectionSize()+"::"+((FacilioTransaction) t).getFreeConnectionSize());
//			}
//		}

		pm.setCurrentExecutionCount(pm.getCurrentExecutionCount() + 1);
		Map<String, Object> props = new HashMap<>();
		props.put("currentExecutionCount", pm.getCurrentExecutionCount());

		if (pm.getCurrentExecutionCount() == pm.getMaxCount()) {
			PreventiveMaintenanceAPI.setPMInActive(pm.getId());
			props.put("status", false);
		}

		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getPreventiveMaintenanceFields())
				.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(pm.getId(), module));

		updateBuilder.update(props);
	}

	public List<Map<String, Object>> CopyPlannedMaintenance() throws Exception {
		System.out.println("___________>>>>>>>>>>OrgID: " + AccountUtil.getCurrentOrg().getId());

		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();

		FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getId());

		// selectBuilder.limit(1);
		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			System.out.println("Number of Pm's in the Org:" + props.size());
			for (Map<String, Object> prop : props) {
				long templateId = (Long) prop.get("templateId");
				WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
				WorkorderTemplate woNewTemplate = new WorkorderTemplate();
				Long categoryId = (Long) prop.get("categoryId");
				if (categoryId != null) {
					prop.put("categoryName", TicketAPI.getCategory(AccountUtil.getCurrentOrg().getId(), categoryId).getName());
				} else {
					prop.put("categoryName", null);
				}
				woNewTemplate.setName(woTemplate.getName());
				woNewTemplate.setTasks(woTemplate.getTasks());
				prop.put("template", woNewTemplate);
		
		
		
		/*	Map<String, List<TaskContext>> taskMap = woTemplate.getTasks();
		
		Iterator itr = taskMap.keySet().iterator();
		while (itr.hasNext()) {
			String section = (String) itr.next();
			for (TaskContext tc : woTemplate.getTasks().get(section)) {
				System.out.println("Number of Pm in the Org: ["+section+"] "+ FieldUtil.getAsJSON(tc));
			}
		}*/

//		for(String key : prop.keySet()){
//			System.out.println(key + " : "+prop.get(key));
//			/*long templateId = (Long)prop.get("templateId");
//			WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
//			woTemplate.setOrgId(newOrgId);
//			TemplateAPI.addPMWorkOrderTemplate(woTemplate);
//		*/
//		}
			}

			return props;
		}
		return null;
	}

	@Override
	public PreventiveMaintenance CopyWritePlannedMaintenance(List<Map<String, Object>> props) throws Exception {

		System.out.println("___________>>>>>>>>>>New OrgID: " + AccountUtil.getCurrentOrg().getId());
		for (Map<String, Object> prop : props) {
			// System.out.println("$$$$$$$$$$woTemplate:"+prop.get("template"));

			WorkorderTemplate woTemplate = (WorkorderTemplate) prop.get("template");
			long orgId = AccountUtil.getCurrentOrg().getId();
			String category = (String) prop.get("categoryName");
			if (category != null) {

				TicketCategoryContext categoryId = TicketAPI.getCategory(orgId, category);

				if (categoryId != null) {
					// System.out.println("+++++++++++++++++" +FieldUtil.getAsJSON(woTemplate));
					woTemplate.setCategoryId(categoryId.getId());
				} else {
					TicketCategoryContext record = new TicketCategoryContext();
					record.setName(category);
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule("ticketcategory");

					InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
							.module(module)
							.fields(modBean.getAllFields("ticketcategory"));

					long id = insertRecordBuilder.insert(record);

					woTemplate.setCategoryId(id);
				}
			}
			woTemplate.setOrgId(orgId);
			long tempId = TemplateAPI.addPMWorkOrderTemplate(woTemplate);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<" + tempId);

			PreventiveMaintenance newPm = new PreventiveMaintenance();

			newPm.setTitle((String) prop.get("title"));
			newPm.setTemplateId(tempId);
			newPm.setCreatedById(AccountUtil.getOrgBean().getSuperAdmin(orgId).getId());
			newPm.setOrgId(orgId);
			newPm.setCreatedTime(System.currentTimeMillis());
			newPm.setStatus(PMStatus.INACTIVE);
			newPm.setTriggerType(4);

			Map<String, Object> pmProps = FieldUtil.getAsProperties(newPm);

			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getPreventiveMaintenanceModule().getTableName())
					.fields(FieldFactory.getPreventiveMaintenanceFields()).addRecord(pmProps);
			builder.save();
			long id = (long) pmProps.get("id");

			System.out.println(">>>>>>>>>>>>>>>>>>>> PM ID: " + id);
//			for(String key : prop.keySet()){
////		long templateId = (Long)prop.get("template");
////		WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
//		
//			}
		}

		return null;
	}

	@Override
	public WorkOrderContext CloseAllWorkOrder() throws Exception {
		List<WorkOrderContext> workOrders = new ArrayList<WorkOrderContext>();
		List<FacilioStatus> ticketStatus = new ArrayList<FacilioStatus>();
		List<TaskContext> tasks = new ArrayList<TaskContext>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getId());
		FacilioModule module = modBean.getModule("workorder");
		 
		
		/*SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.table(module.getTableName())
				.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(WorkOrderContext.class)
				.andCriteria(ViewFactory.getAllOverdueWorkOrdersCriteria());
		
		workOrders = builder.get();*/

		System.out.println("___________>>>>>>>>>>Number of workorders OverDue: " + workOrders.size());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(ModuleFactory.getTicketStatusIdField());

//		SelectRecordsBuilder<FacilioStatus> builder1 = new SelectRecordsBuilder<FacilioStatus>()
//				.table("TicketStatus")
//				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
//				.select(fields)
//				.beanClass(FacilioStatus.class)
//				.andCustomWhere(ModuleFactory.getTicketStatusModule().getTableName()+".ORGID = ? AND STATUS = ?", AccountUtil.getCurrentOrg().getId(), "closed");

		ticketStatus = Collections.singletonList(TicketAPI.getStatus("closed"));

		ticketStatus.get(0).getId();


		System.out.println("@@@@@@@@@@@@@@@@@@@@@ Ticket Status Id" + ticketStatus.get(0).getId());

		for (WorkOrderContext workOrder : workOrders) {
			SelectRecordsBuilder<TaskContext> taskbuilder = new SelectRecordsBuilder<TaskContext>()
					.table("Tasks")
					.moduleName(FacilioConstants.ContextNames.TASK)
					.beanClass(TaskContext.class)
					.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
					.andCustomWhere("Tasks.PARENT_TICKET_ID = ?", workOrder.getId());


			tasks = taskbuilder.get();

			// System.out.println("===============LLLLLLLLL FL "+modBean.getAllFields(FacilioConstants.ContextNames.TICKET));


			if (tasks != null && !tasks.isEmpty()) {
				new StringJoiner(",");
				List<Long> taskIdList = new ArrayList<>();
				System.out.println("@@@@@@@@@@@@@@@@@@@@@Number of Tasks in the Workorder: " + tasks.size() + "for Workorder" + workOrder.getId());
				String questMark = "";
				int idx = 0;
				for (TaskContext task : tasks) {
					taskIdList.add(task.getId());
					if (idx == 0) {
						questMark += "?";
					} else {
						questMark += ", ?";
					}
					idx++;
				}
				System.out.println("===============LLLLLLLLLTask ID's" + taskIdList);
				System.out.println("===============LLLLLLLLL" + questMark);

				UpdateRecordBuilder<TaskContext> updateTaskBuilder = new UpdateRecordBuilder<TaskContext>()
						.moduleName(FacilioConstants.ContextNames.TASK)
						.fields(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
						.andCustomWhere(" Tickets.ID in ( " + questMark + " )", taskIdList.toArray());


				TaskContext ts = new TaskContext();
				ts.setStatusNew(TaskStatus.CLOSED);

				updateTaskBuilder.update(ts);


			}
			System.out.println("===============LLLLLLLLL WO" + workOrder.getId());


			UpdateRecordBuilder<WorkOrderContext> updateWOBuilder = new UpdateRecordBuilder<WorkOrderContext>()
					.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
					.fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
					.andCustomWhere("Tickets.ID = ?", workOrder.getId());

			WorkOrderContext wo = new WorkOrderContext();
			wo.setStatus(ticketStatus.get(0));

			updateWOBuilder.update(wo);

		}

		return null;

	}

	@Override
	public void deleteAllData(String moduleName) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule extendedModule = module.getExtendModule();
		while (extendedModule != null) {
			module = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}

		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		deleteBuilder.delete();
	}

	@Override
	public void processTimeSeries(long timeStamp, JSONObject payLoad, FacilioRecord record, boolean adjustTime) throws Exception {
		TimeSeriesAPI.processPayLoad(timeStamp, payLoad, record, null, adjustTime);
	}

	@Override
	public void processTimeSeries(FacilioRecord record) throws Exception {
		try {
			TimeSeriesAPI.processFacilioRecord(record);
		} catch (AmazonS3Exception e) {
			LOGGER.info("s3 exception " + e.getLocalizedMessage());
		}
	}

  /*  @Override
    public void processTimeSeries(long timeStamp, JSONObject payLoad, Record record, IRecordProcessorCheckpointer checkpointer, boolean adjustTime) throws Exception {
        TimeSeriesAPI.processPayLoadOld(timeStamp, payLoad, record, checkpointer, null, adjustTime);
    }

    @Override
    public void processTimeSeries(FacilioConsumer consumer, FacilioRecord record) throws Exception {
        try {
            TimeSeriesAPI.processFacilioRecordOld(consumer, record);
        } catch (AmazonS3Exception e) {
            LOGGER.info("s3 exception " + e.getLocalizedMessage());
        }
    }*/

    @Override
	public void processEvents(long timeStamp, JSONArray payLoad, List<EventRuleContext> eventRules) throws Exception {
		EventAPI.processEvents(timeStamp, payLoad, eventRules);
	}

	@Override
	public List<EventRuleContext> getActiveEventRules() throws Exception {
		return EventRulesAPI.getActiveEventRules();
	}

	@Override
	public Boolean isFeatureEnabled(FeatureLicense license) throws Exception {
		return AccountUtil.isFeatureEnabled(license);
	}

	@Override
	public void acknowledgePublishedMessage(long id, String message, JSONObject payLoad) throws Exception {

		FacilioChain chain = TransactionChainFactory.getAcknowledgeMessageChain();
		FacilioContext context = chain.getContext();

		context.put(ContextNames.ID, id);
		context.put(ContextNames.MESSAGE, message);
		context.put(ContextNames.PAY_LOAD, payLoad);

		chain.execute();
	}

	public void acknowledgeNewPublishedMessage(long id, Status status) throws Exception {
		IotMessageApiV2.acknowdledgeMessage(id, status);
	}


	@Override
	public long addDeviceId(String deviceId) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(deviceDetailsModule.getTableName()).fields(FieldFactory.getDeviceDetailsFields());
		HashMap<String, Object> device = new HashMap<>();
		device.put("deviceId", deviceId);
		device.put("inUse", true);
		device.put("lastUpdatedTime", System.currentTimeMillis());
		device.put("lastAlertedTime", 0L);
		device.put("alertFrequency", 2400000L);
		long id = builder.insert(device);
		return id;
	}

	@Override
	public Map<String, Long> getDeviceMap() throws Exception {
		// TODO Auto-generated method stub
		FacilioModule deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(deviceDetailsModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(deviceDetailsModule))
				.select(FieldFactory.getDeviceDetailsFields());
		HashMap<String, Long> deviceData = new HashMap<>();
		List<Map<String, Object>> data = builder.get();
		for (Map<String, Object> obj : data) {
			String deviceId = (String) obj.get("deviceId");
			Long id = (Long) obj.get("id");
			deviceData.put(deviceId, id);
		}
		return deviceData;
	}

	public List<Map<String, Object>> getAgentDataMap(String agentName, AgentType type) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentDataFields());
		FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table(AgentKeys.AGENT_TABLE).select(FieldFactory.getAgentDataFields());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(agentDataModule))
		if (agentName != null) {
			genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(agentDataModule), agentName, StringOperators.IS)).get();
		}
		if (type != null) {
			genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.AGENT_TYPE), type.getLabel(), StringOperators.IS));
		}
		return genericSelectRecordBuilder.get();

	}

	@Override
	public Long addLog(Map<String, Object> logData) throws Exception { // done transaction
		FacilioModule logModule = ModuleFactory.getAgentLogModule();
		GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
				.table(AgentKeys.AGENT_LOG_TABLE)
				.fields(FieldFactory.getAgentLogFields());
		return genericInsertRecordBuilder.insert(logData);
	}

	@Override
	public int updateAgentMetrics(Map<String, Object> metrics, Criteria criteria) throws Exception { // done transaction
		FacilioModule metricsmodule = ModuleFactory.getAgentMetricsModule();
		GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(AgentKeys.METRICS_TABLE)
				.fields(FieldFactory.getAgentMetricsFields())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(metricsmodule))
				.andCriteria(criteria);
																/*.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsmodule), criteria.get(AgentKeys.AGENT_ID).toString(),NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishTypeField(metricsmodule), criteria.get(EventUtil.DATA_TYPE).toString(),NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(metricsmodule), String.valueOf(criteria.get(AgentKeys.ID)),NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getAgentMetricsFields()).get(AgentKeys.CREATED_TIME), String.valueOf(criteria.get(AgentKeys.CREATED_TIME)),NumberOperators.EQUALS));
*/
		return genericUpdateRecordBuilder.update(metrics);


	}

	public long insertAgentMetrics(Map<String, Object> metrics) throws Exception { // done transaction
		GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
				.table(AgentKeys.METRICS_TABLE)
				.fields(FieldFactory.getAgentMetricsFields());
		try {
			return genericInsertRecordBuilder.insert(metrics);
		} catch (Exception e) {
			if (e instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
				LOGGER.info("Duplicate Metrics, insertion failed " + e.getMessage());
			} else {
				LOGGER.info("Exception occurred ", e);
			}
		}
		return -1L;

	}

	@Override
	public List<Map<String, Object>> getMetrics(Long agentId, Integer publishType, Long createdTime) throws Exception {
		FacilioModule metricsModule = ModuleFactory.getAgentMetricsModule();
		if (agentId != null && publishType != null) {
			GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
					.table(AgentKeys.METRICS_TABLE)
					.select(FieldFactory.getAgentMetricsFields())
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsModule), agentId.toString(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishTypeField(metricsModule), publishType.toString(), NumberOperators.EQUALS))
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(metricsModule))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getCreatedTime(metricsModule), createdTime.toString(), NumberOperators.EQUALS));
			List<Map<String, Object>> rows = genericSelectRecordBuilder.get();
			return rows;
		}
		return new ArrayList<>();
	}

	public Long addAgentMessage(Map<String, Object> map) throws Exception { // transaction done
		FacilioModule messageModule = ModuleFactory.getAgentMessageModule();
		try {
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(messageModule.getTableName())
					.fields(FieldFactory.getAgentMessageFields());
			return insertRecordBuilder.insert(map);
		} catch (Exception e) {
			if (e instanceof MySQLIntegrityConstraintViolationException || e instanceof BatchUpdateException) {
				LOGGER.info("Duplicate Message,insertion failed " + e.getMessage());
			} else {
				LOGGER.info("Exception Occurred " + e.getMessage());
				throw e;
			}
		}
		return -1L;
	}

	public Long updateAgentMessage(Map<String, Object> map) throws Exception { // transaction done
		FacilioModule messageModule = ModuleFactory.getAgentMessageModule();
		try {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentMessageFields());
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
					.table(messageModule.getTableName())
					.fields(new ArrayList<>(fieldMap.values()))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.MSG_STATUS), "0", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.RECORD_ID), String.valueOf(map.get(AgentKeys.RECORD_ID)), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.PARTITION_ID), String.valueOf(map.get(AgentConstants.PARTITION_ID)), NumberOperators.EQUALS));

			Integer rowsAffected = updateRecordBuilder.update(map);
			return Long.valueOf(rowsAffected);
		} catch (Exception e) {
			if (e instanceof MySQLIntegrityConstraintViolationException) {
				LOGGER.info("Duplicate Message,updation failed " + e.getMessage());
			} else {
				LOGGER.info("Exception Occurred " + e.getMessage());
				throw e;
			}
		}
		return 0L;
	}

	public List<Map<String, Object>> getRows(FacilioContext context) throws Exception {
		// always create an Empty List<Map<String,Object>> and return it instead of null;
		List<Map<String, Object>> rows = new ArrayList<>();
		try {
			if ((context.containsKey(ContextNames.TABLE_NAME) && (context.get(ContextNames.TABLE_NAME) != null))
					&& (context.containsKey(ContextNames.CRITERIA) && (context.get(ContextNames.CRITERIA) != null))
					&& (context.containsKey(ContextNames.FIELDS) && (context.get(ContextNames.FIELDS) != null))) {

				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
						.table(context.get(FacilioConstants.ContextNames.TABLE_NAME).toString())
						.select((Collection<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS))
//                    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
						.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
				if (context.containsKey(FacilioConstants.ContextNames.SORT_FIELDS)) {
					selectRecordBuilder.orderBy(context.get(FacilioConstants.ContextNames.SORT_FIELDS).toString());
				}
				if (context.containsKey(FacilioConstants.ContextNames.INNER_JOIN) && context.containsKey(FacilioConstants.ContextNames.ON_CONDITION)) {
					LOGGER.info("adding inner join ");
					selectRecordBuilder.innerJoin(String.valueOf(context.get(ContextNames.INNER_JOIN))).on(String.valueOf(context.get(ContextNames.ON_CONDITION)));
				}
				JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
				if (pagination != null) {
					int page = (int) pagination.get("page");
					int perPage = (int) pagination.get("perPage");

					int offset = ((page - 1) * perPage);
					if (offset < 0) {
						offset = 0;
					}

					selectRecordBuilder.offset(offset);
					selectRecordBuilder.limit(perPage);
				} else {
					if (context.containsKey(FacilioConstants.ContextNames.OFFSET)) {
						selectRecordBuilder.offset(Integer.parseInt(context.get(FacilioConstants.ContextNames.OFFSET).toString()));
					}
					if (context.containsKey(FacilioConstants.ContextNames.LIMIT_VALUE)) {
						LOGGER.info(" limit in get rows  is " + context.get(FacilioConstants.ContextNames.LIMIT_VALUE));
						selectRecordBuilder.limit(Integer.parseInt((context.get(FacilioConstants.ContextNames.LIMIT_VALUE).toString())));
					} else {
						selectRecordBuilder.limit(1000);
					}
				}

				if ((context.containsKey(FacilioConstants.ContextNames.AGGREGATOR) && (((Boolean) context.get(ContextNames.AGGREGATOR)))) && context.containsKey(ContextNames.MODULE)) {
					selectRecordBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField((FacilioModule) context.get(ContextNames.MODULE)));
				}

				rows.addAll(selectRecordBuilder.get());
				context.put("query", selectRecordBuilder.toString());
				//LOGGER.info(" rows obtained "+rows.size());
			} else {
				LOGGER.info("Exception occurred table name or criteria are mandatory and can't be null ");
			}
		} catch (Exception e) {
			LOGGER.info("Exception Occurred ", e);
			throw e;
		}
		return rows;
	}

	public Integer updateTable(FacilioContext context) throws Exception {
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();

		if (context.containsKey(FacilioConstants.ContextNames.TABLE_NAME)) {
			updateRecordBuilder.table((String) context.get(FacilioConstants.ContextNames.TABLE_NAME));

			if (context.containsKey(FacilioConstants.ContextNames.FIELDS)) {
				updateRecordBuilder.fields((List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS));

				if (context.containsKey(FacilioConstants.ContextNames.CRITERIA)) {
					updateRecordBuilder.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
				}
				if (context.containsKey(FacilioConstants.ContextNames.TO_UPDATE_MAP)) {
					Integer rowsAffected = updateRecordBuilder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
					return rowsAffected;
				} else {
					return 0;
				}
			}
		}
		return 0;
	}

	@Override
	public Integer deleteFromDb(FacilioContext context) throws Exception {
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		if (context.containsKey(FacilioConstants.ContextNames.TABLE_NAME)) {
			deleteRecordBuilder.table(String.valueOf(context.get(FacilioConstants.ContextNames.TABLE_NAME)));

			if (context.containsKey(FacilioConstants.ContextNames.CRITERIA)) {
				deleteRecordBuilder.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
				return deleteRecordBuilder.delete();
			}
			LOGGER.info("No criteria in delete-builder");
			return 0;
		}
		return 0;
	}

	@Override
	public void readingTools(long orgId, long fieldId, long assetId, long startTtime, long endTtime, String email, long fieldsOptionType)
			throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.ADMIN_DELTA_ORG, orgId);
		context.put(ContextNames.FIELD_ID, fieldId);
		context.put(ContextNames.ASSET_ID, assetId);
		context.put(ContextNames.START_TTIME, startTtime);
		context.put(ContextNames.END_TTIME, endTtime);
		context.put(ContextNames.ADMIN_USER_EMAIL, email);
		context.put(ContextNames.FIELD_OPTION_TYPE, fieldsOptionType);

		if (fieldsOptionType == 1) {
			FacilioChain readingToolsChain = TransactionChainFactory.readingToolsDeltaCalculationChain();
			readingToolsChain.execute(context);
		} else {
			FacilioChain readingToolsChain = TransactionChainFactory.readingToolsDuplicateRemoveChain();
			readingToolsChain.execute(context);
		}

	}
	@Override
	public void deleteReadings(long orgId, long fieldId, long assetId, long startTtime, long endTtime, long assetCatorgryId, long moduleId)
			throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.ADMIN_DELTA_ORG, orgId);
		context.put(ContextNames.FIELD_ID, fieldId);
		context.put(ContextNames.ASSET_ID, assetId);
		context.put(ContextNames.START_TIME, startTtime);
		context.put(ContextNames.END_TIME, endTtime);
		context.put(ContextNames.CATEGORY_ID, assetCatorgryId);
		context.put(ContextNames.MODULE_ID, moduleId);
		FacilioChain deleteReading = TransactionChainFactory.deleteAssetReadings();
		deleteReading.execute(context);

	}

	@Override
	public void moveReadings(long orgId, long fieldId, long assetId, long startTtime, long endTtime, long assetCatorgryId, long duration, long type)
			throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.ADMIN_DELTA_ORG, orgId);
		context.put(ContextNames.FIELD_ID, fieldId);
		context.put(ContextNames.ASSET_ID, assetId);
		context.put(ContextNames.START_TIME, startTtime);
		context.put(ContextNames.END_TIME, endTtime);
		context.put(ContextNames.CATEGORY_ID, assetCatorgryId);
		context.put(ContextNames.DURATION, duration);
		context.put(ContextNames.TYPE, type);
		FacilioChain deleteReading = TransactionChainFactory.shiftAssetReadings();
		deleteReading.execute(context);

	}

	@Override
	public void readingFieldsMigration(long orgId, long sourceFieldId, long assetId, long assetCategoryId, long targetFieldId)
			throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.ADMIN_DELTA_ORG, orgId);
		context.put(ContextNames.ASSET_ID, assetId);
		context.put(ContextNames.CATEGORY_ID, assetCategoryId);
		context.put(FacilioConstants.ContextNames.SOURCE_ID, sourceFieldId);
		context.put(FacilioConstants.ContextNames.TARGET_ID, targetFieldId);
		FacilioChain chain = TransactionChainFactory.addFieldMigrationJob();
		chain.execute(context);
	}


	@Override
	public List<AssetCategoryContext> getCategoryList() throws Exception {
		// TODO Auto-generated method stub

		List<AssetCategoryContext> assetcategory = AssetsAPI.getCategoryList();
		if (assetcategory != null) {
			return assetcategory;
		}
		return null;
	}

	@Override
	public List<AssetContext> getAssetListOfCategory(long category) throws Exception {
		// TODO Auto-generated method stub

		List<AssetContext> assetList = AssetsAPI.getAssetListOfCategory(category);
		if (assetList != null) {
			return assetList;
		}
		return null;
	}


	public List<FacilioModule> getAssetReadings(long parentCategoryId) throws Exception {
		ReadingAction reading = new ReadingAction();
		reading.setParentCategoryId(parentCategoryId);
		reading.getAssetReadings();
		List<FacilioModule> newReading = reading.getReadings();
		if (newReading != null) {
			return newReading;
		}
		return null;
	}


	@Override
	public void updatePMJob(List<WorkOrderContext> workorders) throws Exception {
		Map<Long, WorkOrderContext> oldWorkorders = WorkOrderAPI.getWorkOrdersAsMap(workorders.stream().map(WorkOrderContext::getId).collect(Collectors.toList()));
		List<WorkOrderContext> preopenWorkorders = new ArrayList<>();
		List<WorkOrderContext> executedWorkorders = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioStatus preopenStatus = TicketAPI.getStatus(modBean.getModule(ContextNames.WORK_ORDER), "preopen");

		for (WorkOrderContext wo : workorders) {
			WorkOrderContext oldWo = oldWorkorders.get(wo.getId());
			if (oldWo.getModuleState() == null || oldWo.getModuleState().getId() == preopenStatus.getId()) {    // Pre open state
				preopenWorkorders.add(wo);
			} else {
				WorkOrderContext newWo = new WorkOrderContext();
				if (wo.getDueDate() > 0) {
					newWo.setDueDate(wo.getDueDate());
				}
				if (wo.getAssignedTo() != null && wo.getAssignedTo().getId() != -1) {
					newWo.setAssignedTo(wo.getAssignedTo());
					newWo.setAssignedBy(AccountUtil.getCurrentUser());
				}
				if (wo.getAssignmentGroup() != null && wo.getAssignmentGroup().getId() != -1) {
					newWo.setAssignmentGroup(wo.getAssignmentGroup());
					newWo.setAssignedBy(AccountUtil.getCurrentUser());
				}
				executedWorkorders.add(newWo);
			}
		}
		FacilioContext context = new FacilioContext();
		if (!preopenWorkorders.isEmpty()) {
			for (WorkOrderContext wo : preopenWorkorders) {
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				context.put(FacilioConstants.ContextNames.IS_NEW_EVENT, true);    // temp

				FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceJobChain();
				updatePM.execute(context);

				context.clear();
			}
		}
		if (!executedWorkorders.isEmpty()) {
			for (WorkOrderContext wo : preopenWorkorders) {

				EventType activityType = EventType.EDIT;
				// Temp
				if ((wo.getAssignedTo() != null && wo.getAssignedTo().getId() != -1) || (wo.getAssignmentGroup() != null && wo.getAssignmentGroup().getId() != -1)) {
					activityType = EventType.ASSIGN_TICKET;
				}

				context.put(FacilioConstants.ContextNames.EVENT_TYPE, activityType);

				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(wo.getId()));
				context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

				FacilioChain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
				updateWorkOrder.execute(context);

				context.clear();
			}
		}
	}

	public Long addAgentController(Controller controller) {
		try {
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(AgentConstants.AGENT_CONTROLLER_TABLE)
					.fields(FieldFactory.getAgentControllerFields());
			LOGGER.info(" inserting value " + controller.getParentJSON());
			long controllerId = builder.insert(controller.getParentJSON());
			if (controllerId > 0) {
				controller.setId(controllerId);
				LOGGER.info(" adding child controller " + controller.getId());
				try {
					GenericInsertRecordBuilder builder1 = new GenericInsertRecordBuilder();
					switch (FacilioControllerType.valueOf(controller.getControllerType())) {
						case MODBUS_RTU:
							builder1.table(AgentConstants.MODBUS_CONTROLLER_TABLE);
							break;
						case MODBUS_IP:
							builder1.table(AgentConstants.MODBUS_CONTROLLER_TABLE).fields(FieldFactory.getModbusControllerFields());
							break;
						case BACNET_MSTP:
						case OPC_XML_DA:
						case BACNET_IP:
						case NIAGARA:
						case MISC:
						case OPC_UA:
						case KNX:
						case LON_WORKS:
					}
					LOGGER.info(" child controller value " + controller.getChildJSON());
					long childId = builder1.insert(controller.getChildJSON());
					if (childId > 0) {
						return controller.getId();
					}
				} catch (Exception e) {
					LOGGER.info("Exception occurred ", e);
				}
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
		return -1L;
	}


	public Long addChildController(Controller controller) {
		LOGGER.info(" adding controller child " + controller.getId());
		try {
			GenericInsertRecordBuilder builder1 = new GenericInsertRecordBuilder();
			switch (FacilioControllerType.valueOf(controller.getControllerType())) {
				case MODBUS_RTU:
					builder1.table(AgentConstants.MODBUS_CONTROLLER_TABLE);
					break;
				case MODBUS_IP:
					builder1.table(AgentConstants.MODBUS_CONTROLLER_TABLE);
					break;
				case BACNET_MSTP:
				case OPC_XML_DA:
				case BACNET_IP:
				case NIAGARA:
				case MISC:
				case OPC_UA:
				case KNX:
				case LON_WORKS:
			}
			long childId = builder1.insert(controller.getChildJSON());
			if (childId > 0) {
				return controller.getId();
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
		return -1L;
	}

	public Long genericInsert(Context context) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder();
		if (context.containsKey(ContextNames.MODULE)) {
			FacilioModule module = (FacilioModule) context.get(ContextNames.MODULE);
			builder.table(module.getTableName());
			if (context.containsKey(ContextNames.FIELDS)) {
				builder.fields((List<FacilioField>) context.get(ContextNames.FIELDS));
			}
		} else {
			if (context.containsKey(ContextNames.TABLE_NAME)) {
				String tableName = String.valueOf(context.get(ContextNames.TABLE_NAME));
				if ((tableName != null) && (!tableName.isEmpty())) {
					builder.table(tableName);
					if (context.containsKey(ContextNames.FIELDS)) {
						List<FacilioField> fields = (List<FacilioField>) context.get(ContextNames.FIELDS);
						if ((fields != null) && (!fields.isEmpty())) {
							builder.fields(fields);
						} else {
							LOGGER.info(" Exception occurred Fields null or empty ");
							throw new Exception("Fields cant be null or empty -> " + fields);
						}
					} else {
						LOGGER.info(" Exception occurred Fields missing ");
						throw new Exception(" fields are missing ");
					}
				} else {
					LOGGER.info(" Exception occurred tableName is null or empty ");
					throw new Exception("Table name is null or empty -> " + tableName);
				}
			} else {
				LOGGER.info(" Exception occurred tableName missing from context ");
				throw new Exception(" Table name is missing from context -> " + context.values());
			}
		}
		if (context.containsKey(ContextNames.TO_INSERT_MAP)) {
			Map<String, Object> map = (Map<String, Object>) context.get(ContextNames.TO_INSERT_MAP);
			if (map != null) {
				try {
					Long id = builder.insert(map);
					LOGGER.info(" insertion ID " + id);
					LOGGER.info("to insert map is " + map);
					context.put(ContextNames.ID, id);
					return id;
				} catch (Exception e) {
					LOGGER.info("Exception occurred ", e);
					return -1L;
				}
			} else {
				LOGGER.info(" Exception occurred to insert map is null ");
				throw new Exception(" toInsertMap can't be null or empty ->" + map);
			}
		} else {
			LOGGER.info(" Exception occurred to insert map  missing from context ");
			throw new Exception(" toInsertMap missing from context ->" + context.keySet());
		}
	}

	@Override
	public void processNewTimeSeries(JSONObject payload, Controller controller) throws Exception {
		FacilioChain chain = TransactionChainFactory.getTimeSeriesProcessChainV2();
		FacilioContext context = chain.getContext();
		context.put(AgentConstants.DATA, payload);
		context.put(AgentConstants.CONTROLLER, controller);
		chain.execute();
	}

	public boolean addPoint(Point point) throws Exception {
		if (point != null) {
			Map<String, Object> toInsertMap = point.getPointJSON();
			LOGGER.info(" point as map " + toInsertMap);
			FacilioModule pointModule = ModuleFactory.getPointModule();
			FacilioContext context = new FacilioContext();
			context.put(ContextNames.MODULE, pointModule);
		}
		return false;
	}

	@Override
	public FileInfo getFile(long fileId) throws Exception {

		FileStore fs = FacilioFactory.getFileStore();
		FileInfo fileInfo = fs.getFileInfo(fileId);
		return fileInfo;
	}

	@Override
	public void demoOneTimeJob(long orgId, ZonedDateTime currentZdt) throws Exception {
		// TODO Auto-generated method stub
		try {
			FacilioChain chain = TransactionChainFactory.demoRollUpOneTimeJobChain();
			chain.getContext().put("ORGID",orgId);
			chain.getContext().put("START_TIME", currentZdt);
			chain.execute();
		}catch(Exception e) {
			throw e;
		}

	}


	public boolean addMetrics(AgentMetrics metrics) throws Exception {
		return MetricsApi.insertMetrics(metrics);
	}

	@Override
	public boolean updateMetrics(Map<String, Object> toUpdate, long metricsId) throws Exception {
		MetricsApi.updateMetrics(toUpdate, metricsId);
		return false;
	}

	@Override

	public List<Map<String, Object>> getOrgSpecificAgentList() throws Exception {
		return new GenericSelectRecordBuilder().select(FieldFactory.getNewAgentFields()).table(ModuleFactory.getNewAgentModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getNewAgentFields()).get("deletedTime"), CommonOperators.IS_EMPTY))
				.get();
	}

	@Override
	public void disableOrEnableAgent(long agentId, boolean disable) throws Exception {
		FacilioModule module = ModuleFactory.getNewAgentModule();
		Map<String, Object> prop = new HashMap<>();
		prop.put("isDisable", disable);
		if (disable) {
			prop.put("lastDisabledTime", System.currentTimeMillis());
		} else {
			prop.put("lastEnabledTime", System.currentTimeMillis());
		}
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getNewAgentFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(agentId, module));
		builder.update(prop);
	}

	public void addOrUpdateWeatherData(Map<Long, List<Long>> siteAndStationMap, Map<Long, Map<String, Object>> dataMap) throws Exception {
		if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WEATHER_INTEGRATION)) {
			return;
		}
		FacilioChain context = TransactionChainFactory.addOrUpdateWeatherDataChain();
		context.getContext().put("WeatherStationIdVsSiteId", siteAndStationMap);
		context.getContext().put("dataMap", dataMap);
		context.execute();
	}

	@Override
	public void addOrUpdateDailyWeatherData(Map<Long, List<Long>> siteAndStationMap, Map<Long, Map<String, Object>> dataMap) throws Exception {
		if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WEATHER_INTEGRATION)) {
			return;
		}
		FacilioChain context = TransactionChainFactory.addOrUpdateDailyWeatherDataChain();
		context.getContext().put("WeatherStationIdVsSiteId", siteAndStationMap);
		context.getContext().put("dataMap", dataMap);
		context.execute();
	}

	@Override
	public long getPendingDataCount() throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(new ArrayList<>()).table(DataPendingAlertJob.AGENT_MESSAGE_MODULE.getTableName())
				.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, DataPendingAlertJob.FIELD_MAP.get(AgentConstants.ID))
				.andCondition(CriteriaAPI.getCondition(DataPendingAlertJob.FIELD_MAP.get(AgentKeys.MSG_STATUS), String.valueOf(0), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(DataPendingAlertJob.FIELD_MAP.get(AgentKeys.START_TIME), String.valueOf(getLastTwohours()), NumberOperators.LESS_THAN));
		return (long) builder.fetchFirst().get(AgentConstants.ID);
	}

	private long getLastTwohours() {
		return DateTimeUtil.addHours(DateTimeUtil.getCurrenTime(), -2);
	}

	@Override
	public List<Map<String, Object>> getMissingData() throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getAgentNameField(DataProcessingAlertJob.AGENT_MODULE));
		fields.add(FieldFactory.getField(AgentConstants.LAST_DATA_RECEIVED_TIME, "LAST_DATA_RECEIVED_TIME", DataProcessingAlertJob.AGENT_MODULE, FieldType.NUMBER));
		fields.add(FieldFactory.getIdField(DataProcessingAlertJob.AGENT_MODULE));
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields).table(DataProcessingAlertJob.AGENT_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(DataProcessingAlertJob.FIELD_MAP.get(AgentConstants.LAST_DATA_RECEIVED_TIME), String.valueOf(getLastTwohours()), NumberOperators.LESS_THAN))
				.orderBy("ID DESC");
		List<Map<String, Object>> props = builder.get();
		return CollectionUtils.isNotEmpty(props) ? props : Collections.emptyList();
	}

	@Override
	public long addRequestFromEmail(MimeMessage emailMsg, MimeMessageParser parser, SupportEmailContext supportEmail, Long workOrderRequestEmailId) throws Exception {
		long requestId = -1;
		OrgBean orgBean = AccountUtil.getOrgBean();
		if (orgBean.isFeatureEnabled(FeatureLicense.CUSTOM_MAIL)) {
			requestId = MailMessageUtil.createRecordToMailModule(supportEmail, emailMsg, workOrderRequestEmailId);
			LOGGER.info("Added Email from Email Parser : " + requestId);
		}
		return requestId;
	}

	@Override
	public InputStream getDownloadStream(String namespace, long fileId) throws Exception {
		FileStore fs = FacilioFactory.getFileStore();
		FileInfo fileInfo = namespace == null ? fs.getFileInfo(fileId) : fs.getFileInfo(namespace, fileId);
		InputStream downloadStream = null;
		if (fileInfo != null) {
			downloadStream = fs.readFile(fileInfo);
		}
		return downloadStream;
	}

	@Override
	public FileInfo getFileInfo(String namespace, long fileId) throws Exception {
		FileStore fs = FacilioFactory.getFileStore();
		return namespace == null ? fs.getFileInfo(fileId) : fs.getFileInfo(namespace, fileId);
	}

	@Override
	public Object executeWorkflow(WorkflowContext workflowContext) throws Exception {
		return workflowContext.executeWorkflowScoped();
	}

	@Override
	public void addAuditLog(AuditLogHandler.AuditLogContext auditLog) throws Exception {
		AuditLogUtil.insertAuditLog(auditLog);
	}

	@Override
	public void initMLService(Map<String , Object> mlServiceData) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(MLServiceUtil.ML_SERVICE_MODULE);
		V3Util.createRecord(module, mlServiceData);
//		MLServiceAction.initMLServiceV3FromAdmin(mlServiceData);
	}

	@Override
	public ApplicationContext getApplicationForLinkName(String appName) throws Exception {
		return ApplicationApi.getApplicationForLinkName(appName);
	}
	
	@Override
	public void schedulePM(long plannerId,PlannedMaintenanceAPI.ScheduleOperation operation) throws Exception {
		// TODO(2):
		PMPlanner pmPlanner = getPmPlanners(plannerId);

		PlannedMaintenance plannedmaintenance = V3RecordAPI.getRecord("plannedmaintenance", pmPlanner.getPmId());

		List<PMResourcePlanner> pmResourcePlanners = getPMResourcePlanner(plannerId);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule("workorder");

		FacilioContext context = new FacilioContext();
		context.put("trigger", pmPlanner.getTrigger());
		context.put("cutOffTime", System.currentTimeMillis());

		Map<FacilioStatus.StatusType, FacilioStatus> statusMap = new HashMap<>();
		getPreOpenStatus(statusMap);
		context.put(FacilioConstants.ContextNames.STATUS_MAP, statusMap);

		ExecutorBase scheduleExecutor = operation.getExecutorClass();
		
		scheduleExecutor.deletePreOpenworkOrder(plannerId);
		
		pmPlanner.setResourcePlanners(pmResourcePlanners);
		
		context.put(FacilioConstants.PM_V2.PM_V2_MODULE_NAME, plannedmaintenance);
		context.put(FacilioConstants.PM_V2.PM_V2_PLANNER, pmPlanner);
		
		List<V3WorkOrderContext> generatedWorkOrders = scheduleExecutor.execute(context); //  scheduleExecutor.execute returns no WOs
		List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = generatedWorkOrders.stream().map(i-> (ModuleBaseWithCustomFields)i).collect(Collectors.toList());

		for(ModuleBaseWithCustomFields object: moduleBaseWithCustomFields){
			Map<String, Object> objectMap = FieldUtil.getAsProperties(object);
			V3Util.preCreateRecord(workOrderModule.getName(), Collections.singletonList(objectMap), null,null);
		}

		updateLastGeneratedTimeInPlanner(plannerId, (long) context.getOrDefault(FacilioConstants.ContextNames.LAST_EXECUTION_TIME, -1));
	}

//	@Override
//	public void extendPlanner(long plannerId, Duration duration) throws Exception {
//		PMPlanner pmPlanner = getPmPlanners(plannerId);
//		PlannedMaintenance plannedmaintenance = V3RecordAPI.getRecord("plannedmaintenance", pmPlanner.getPmId());
//
//		List<PMResourcePlanner> pmResourcePlanners = getPMResourcePlanner(plannerId);
//
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		FacilioModule workOrderModule = modBean.getModule("workorder");
//
//
//		FacilioContext context = new FacilioContext();
//		context.put("cutOffTime", pmPlanner.getGeneratedUpto());
//
//		ZonedDateTime dateTime = DateTimeUtil.getDateTime(pmPlanner.getGeneratedUpto(), false);
//		ZonedDateTime plannedEndTime = dateTime.plus(duration);
//		pmPlanner.getTrigger().setPlanEndTime(plannedEndTime.toEpochSecond() * 1000);
//
//		context.put("trigger", pmPlanner.getTrigger());
//
//		Map<FacilioStatus.StatusType, FacilioStatus> statusMap = new HashMap<>();
//		getPreOpenStatus(statusMap);
//		context.put(FacilioConstants.ContextNames.STATUS_MAP, statusMap);
//
//		ScheduleExecutor scheduleExecutor = new ScheduleExecutor();
//		pmPlanner.setResourcePlanners(pmResourcePlanners);
//		
//		context.put(FacilioConstants.PM_V2.PM_V2_MODULE_NAME, plannedmaintenance);
//		context.put(FacilioConstants.PM_V2.PM_V2_PLANNER, pmPlanner);
//
//		List<V3WorkOrderContext> generatedWorkOrders = scheduleExecutor.execute(context);
//		List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = generatedWorkOrders.stream().map(i-> (ModuleBaseWithCustomFields)i).collect(Collectors.toList());
//
//		V3Util.createRecord(workOrderModule, moduleBaseWithCustomFields);
//		updateLastGeneratedTimeInPlanner(plannerId, pmPlanner.getTrigger().getEndTime() * 1000);
//	}

	private void updateLastGeneratedTimeInPlanner(long plannerId, long generatedUpto) throws Exception {
		if(generatedUpto > 0) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
			List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("generatedUpto", generatedUpto);

			UpdateRecordBuilder<PMPlanner> updateRecordBuilder = new UpdateRecordBuilder<>();
			updateRecordBuilder.fields(Collections.singletonList(fieldMap.get("generatedUpto")));
			updateRecordBuilder.module(pmPlannerModule);
			updateRecordBuilder.andCondition(CriteriaAPI.getIdCondition(plannerId, pmPlannerModule));
			updateRecordBuilder.updateViaMap(updateMap);
		}
	}

	private void getPreOpenStatus(Map<FacilioStatus.StatusType, FacilioStatus> statusMap) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioStatus> statusOfStatusType = TicketAPI.getStatusOfStatusType(workorderModule, FacilioStatus.StatusType.PRE_OPEN);
		statusMap.put(FacilioStatus.StatusType.PRE_OPEN, statusOfStatusType.get(0));
	}

	private PMPlanner getPmPlanners(long plannerId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
		List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

		SelectRecordsBuilder<PMPlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder.select(pmPlannerFields);
		selectRecordsBuilder.beanClass(PMPlanner.class);
		selectRecordsBuilder.module(pmPlannerModule);

		// add supplement to be fetched
		List<SupplementRecord> supplementList = new ArrayList<>();
		supplementList.add((LookupField) fieldMap.get("trigger"));

		selectRecordsBuilder.fetchSupplements(supplementList);
		selectRecordsBuilder.andCondition(CriteriaAPI.getIdCondition(plannerId, pmPlannerModule));
		List<PMPlanner> pmPlanners = selectRecordsBuilder.get();

		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(pmPlanners)) {
			return pmPlanners.get(0);
		}

		return null;
	}

	private List<PMResourcePlanner> getPMResourcePlanner(Long pmPlannerId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
		List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

		SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder.select(pmPlannerFields)
				.beanClass(PMResourcePlanner.class)
				.module(pmPlannerModule)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("planner"), pmPlannerId+"", NumberOperators.EQUALS));

		// add supplements to be fetched
		List<SupplementRecord> supplementList = new ArrayList<>();
		supplementList.add((LookupField) fieldMap.get("resource"));
		selectRecordsBuilder.fetchSupplements(supplementList);

		return selectRecordsBuilder.get();
	}
}