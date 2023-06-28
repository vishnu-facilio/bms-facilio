package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.WorkOrderContext.AllowNegativePreRequisite;
import com.facilio.bmsconsole.templates.*;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.*;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.Constants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkOrderAction extends FacilioAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(WorkOrderAction.class.getName());

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    private String currentView;
    
	private List<FacilioField> fields;

	public List getFormlayout() {
		return FormLayout.getNewTicketLayout(fields);
	}

	private Boolean vendorPortal;
	public Boolean getVendorPortal() {
		if (vendorPortal == null) {
			return false;
		}
		return vendorPortal;
	}
	public void setVendorPortal(Boolean vendorPortal) {
		this.vendorPortal = vendorPortal;
	}
	
	private PreventiveMaintenance pm = new PreventiveMaintenance();

	public PreventiveMaintenance getPm() {
		return pm;
	}

	public void setPm(PreventiveMaintenance pm) {
		this.pm = pm;
	}

	private ActionForm actionForm;

	public ActionForm getActionForm() {
		return actionForm;
	}

	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}

	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private List<Long> nextExecutionTimes = new ArrayList<>();

	public void setNextExecutionTimes(List<Long> val) {
		this.nextExecutionTimes = val;
	}

	public List<Long> getNextExecutionTimes() {
		return this.nextExecutionTimes;
	}

	private String scheduleInfo;

	public void setScheduleInfo(String info) throws JsonParseException, JsonMappingException, IOException, ParseException {
		this.scheduleInfo = info;
	}

	public String getScheduleInfo() {
		return this.scheduleInfo;
	}

	public String addWorkOrder() throws Exception {
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
		}
		else if(workorder != null) {
			workorder.parseFormData();
		}
		if(tasksString != null) {
			setTaskcontex(tasksString);
		}
		return addWorkOrder(workorder);
	}

	public String addWorkOrder(WorkOrderContext workorder) throws Exception {
		if (workorder.getSourceTypeEnum() == null) {
			workorder.setSourceType(TicketContext.SourceType.WEB_ORDER);
		}
		FacilioContext context = new FacilioContext();
		//not to send email while creating wo directly from portal 
//		if(workorder.getRequester() != null && workorder.getRequester().getUid() <= 0) {
//			workorder.getRequester().setInviteAcceptStatus(true);
//		}
		context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
		context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, getPublicRequest());

		
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, this.ticketattachments);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

 		
 		if (this.getFormName() != null && !this.getFormName().isEmpty()) {
			context.put(FacilioConstants.ContextNames.FORM_NAME, this.getFormName());
			context.put(FacilioConstants.ContextNames.FORM_OBJECT, workorder);
		}
		
		Command addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		
		setWorkOrderId(workorder.getId());
		return SUCCESS;
	}

	private Long templateId;

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String addPreventiveMaintenance() throws Exception {
		FacilioContext context = new FacilioContext();
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
		}
		if(preventiveMaintenanceString != null) {
			setPreventiveMaintenancecontex(preventiveMaintenanceString);
		}
		if(tasksString != null) {
			setTaskcontex(tasksString);
		}
		if(reminderString != null) {
			setRemindercontex(reminderString);
		}
		addPreventiveMaintenance(context);
		return SUCCESS;
	}

	public String calculateNextExecutionTimes() throws Exception {
		JSONParser parser = new JSONParser();
		ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(this.scheduleInfo), ScheduleInfo.class);
		Pair<Long, Integer> result = schedule.nextExecutionTime(Pair.of(startTime - 300, 0));
		long currentTime = System.currentTimeMillis()/1000;
		while (result.getLeft() < currentTime) {
			result = schedule.nextExecutionTime(result);
		}
		long endDate = schedule.getEndDate();
		this.nextExecutionTimes.add(result.getLeft());
		for (int i = 0; i < 9; i++) {
			result = schedule.nextExecutionTime(result);
			long nextExecutionTime = result.getLeft();
			if(endDate > 0 && nextExecutionTime * 1000 > endDate){
				break;
			}
			this.nextExecutionTimes.add(nextExecutionTime);
		}
		return SUCCESS;
	}

	private List<Long> workOrderIds;

	private Boolean doNotExecute;

	public String openUnOpenedWOs() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
		context.put(FacilioConstants.ContextNames.DO_NOT_EXECUTE, getDoNotExecute());
		context.put(ContextNames.LIMIT_VALUE, getLimit());
		FacilioChain chain = TransactionChainFactory.openUnOpenedPMs();
		chain.execute(context);
		List<Long> workOrders = (List<Long>) context.get(ContextNames.WORK_ORDER_LIST);
		setWorkOrderIds(workOrders);
		return SUCCESS;
	}
	
	public String addNewPreventiveMaintenance() throws Exception {
		FacilioContext context = new FacilioContext();
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
		}
		if(preventiveMaintenanceString != null) {
			setPreventiveMaintenancecontex(preventiveMaintenanceString);
		}
		if(tasksString != null) {
			setTaskSectioncontext(tasksString);
		}
		if(preRequestsString != null) {
			setPreRequestSectioncontext(preRequestsString);
		}
		if(prerequisiteApproverString != null && workorder.getAllowNegativePreRequisiteEnum().equals(AllowNegativePreRequisite.YES_WITH_APPROVAL)) {
			setPreRequisiteApproverContext(prerequisiteApproverString);
		}
		if(reminderString != null) {
			setRemindercontex(reminderString);
		}
		addPreventiveMaintenance(context);
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Preventive Maintenance with id #{%d} has been added", preventivemaintenance.getId()),
				preventivemaintenance.getTitle(),
				AuditLogHandler.RecordType.SETTING,
				"PreventiveMaintenance", preventivemaintenance.getId())
				.setActionType(AuditLogHandler.ActionType.ADD)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", preventivemaintenance.getId());
					json.put("moduleName", moduleName);
					json.put("Created At", preventivemaintenance.getCreatedTime());
					array.add(json);
					return array.toJSONString();
				}).apply(null))

		);
		return SUCCESS;
	}

	private long fromOrgId;
	private long toOrgId;

	public String importPM() throws Exception {
		PreventiveMaintenanceAPI.importPM(pmId, fromOrgId, toOrgId);
		return SUCCESS;
	}

	public String addPreventiveMaintenance(FacilioContext context) throws Exception {

		workorder.setRequester(null);
		if(reminders != null) {
			preventivemaintenance.setReminders(reminders);
		}
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES, prerequisiteApproverTemplates);
		context.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, sectionTemplates);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);

 		FacilioChain addTemplate = FacilioChainFactory.getAddNewPreventiveMaintenanceChain();
 		addTemplate.execute(context);

		return SUCCESS;
	}

	List<Long> assetids;

	public List<Long> getAssetids() {
		return assetids;
	}

	public void setAssetids(List<Long> assetids) {
		this.assetids = assetids;
	}
	private Long categoryId;
	

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String addBulkPreventiveMaintenance() throws Exception {
		PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPMsDetails(Collections.singletonList(pmId)).get(0);
		List<Long> assetLists = new ArrayList<>();
		if (type.equals("excludedResources")) {
			assetLists = getAssetExcludeList(assetids, categoryId);
		} else if (type.equals("all")) {
			assetLists = getAssetListCatergory(categoryId);
		} else {
			assetLists = assetids;	
		}

		Map<String, List<TaskContext>> taskPm1 = pm.getWoTemplate().getTasks();
		List<PMTriggerContext> pmTriggers = pm.getTriggers();
		
		for (Long assetId : assetLists) {
			
			pm.setId(-1);
			
			WorkOrderContext wo = pm.getWoTemplate().getWorkorder();
			ResourceContext resourceContext = wo.getResource();
			long oldAssetId = resourceContext.getId();
			
			wo.setId(-1);
			
			resourceContext.setId(assetId);
			pm.getWoTemplate().setResourceId(assetId);
			wo.setResource(resourceContext);
			
			
			if (taskPm1 != null) {
				for (String key : taskPm1.keySet()) {
					List<TaskContext> taskContexts =  taskPm1.get(key);
					for(TaskContext taskContext :taskContexts) {
						ResourceContext taskResourceCOntext = taskContext.getResource();
						if (taskResourceCOntext != null) {
							if (oldAssetId == taskResourceCOntext.getId()) {
								taskResourceCOntext.setId(assetId);
							}
							else if (taskContext.getInputTypeEnum() == InputType.READING) {
								taskContext.setReadingFieldId(-1);
								taskContext.setInputType(InputType.NONE);
							}
						}
						
						TicketAPI.setTasksInputData(Collections.singletonList(taskContext));
						if (taskContext.getInputTypeEnum() != InputType.NONE && taskContext.getInputTypeEnum() != InputType.READING) {
							taskContext.setReadingFieldId(-1);
						}
						
						taskContext.setId(-1);
					    taskContext.setSectionId(-1);
					    
					    taskContext.setReadingRules(null);	// Temporary
					}
				}
			}
			
			if (pmTriggers != null ) {
				Iterator<PMTriggerContext> iterator = pmTriggers.iterator();
				while (iterator.hasNext()) {
					PMTriggerContext pmTContext = iterator.next();
					pmTContext.setId(-1);
					if (pmTContext.getRuleId() != -1) {
						iterator.remove();
					}
				}
			}

			if (pm.getReminders() != null)
			{
				List<PMReminder> newReminder = new ArrayList<PMReminder>();
				List<PMReminder> pmReminder = pm.getReminders();
				for (PMReminder reminder: pmReminder) {
					if (reminder != null) {
						PMReminder constructReminder = new PMReminder() ;
						constructReminder.setDuration(reminder.getDuration());
						constructReminder.setType(reminder.getType());
						newReminder.add(constructReminder);
					}
				}
				pm.setReminders(newReminder);
			}

			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			context.put(FacilioConstants.ContextNames.TASK_MAP, taskPm1);
			context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
			FacilioChain addTemplate = FacilioChainFactory.getAddNewPreventiveMaintenanceChain();
			addTemplate.execute(context);
		}
		return SUCCESS;
	}

	private List<PMReminder> reminders;
	public List<PMReminder> getReminders() {
		return reminders;
	}
	public void setReminders(List<PMReminder> reminders) {
		this.reminders = reminders;
	}

	private long pmId = -1;
	public long getPmId() {
		return pmId;
	}
	public void setPmId(long pmId) {
		this.pmId = pmId;
	}
	
	private String type = null;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String executePM() throws Exception {
		FacilioContext context = executePMs(Collections.singletonList(pmId));
		if (context != null) {
			workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);

			// Adding Audit Log to show up when PM is Executed manually. It shows PM ID and number of resources that's executed.
			List<PreventiveMaintenance> preventiveMaintenanceList = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
			if(preventiveMaintenanceList != null && preventiveMaintenanceList.size() > 0 && preventiveMaintenanceList.get(0) !=null) {

				int numberOfResourcesExecuted = (preventiveMaintenanceList.get(0).getPmIncludeExcludeResourceContexts()!= null
						&& preventiveMaintenanceList.get(0).getPmIncludeExcludeResourceContexts().size()>0) ?
						preventiveMaintenanceList.get(0).getPmIncludeExcludeResourceContexts().size() : 0;

				sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Preventive Maintenance with id #{%d} has been executed with #{%d} number of resources", preventiveMaintenanceList.get(0).getId(), numberOfResourcesExecuted),
						preventiveMaintenanceList.get(0).getTitle(),
						AuditLogHandler.RecordType.SETTING,
						"PreventiveMaintenance", preventiveMaintenanceList.get(0).getId())
						.setActionType(AuditLogHandler.ActionType.MISCELLANEOUS)
						.setLinkConfig(((Function<Void, String>) o -> {
							JSONArray array = new JSONArray();
							JSONObject json = new JSONObject();
							json.put("id", preventiveMaintenanceList.get(0).getId());
							json.put("moduleName", moduleName);
							json.put("Created At", preventiveMaintenanceList.get(0).getCreatedTime());
							array.add(json);
							return array.toJSONString();
						}).apply(null))

				);
			}
		}
		return SUCCESS;
	}

	public String executePMs() throws Exception {
		FacilioContext context = executePMs(id);
		if (context != null) {
			id = (List<Long>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
		}
		return SUCCESS;
	}
	
	private FacilioContext executePMs (List<Long> ids) throws Exception {
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getActivePMs(ids, null, null);
		if (pms != null && !pms.isEmpty()) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
			context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
			context.put(FacilioConstants.ContextNames.PM_INCLUDE_EXCLUDE_LIST, pmIncludeExcludeResourceContexts);
			context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

			
			FacilioChain executePm = TransactionChainFactory.getExecutePMsChain();
			executePm.execute(context);
			
			return context;
		}
		return null;
	}
	
	private List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;
	public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
		return pmIncludeExcludeResourceContexts;
	}
	public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
		this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
	}

	public String updatePreventiveMaintenance() throws Exception {
		FacilioContext context = new FacilioContext();
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
		}
		if(preventiveMaintenanceString != null) {
			setPreventiveMaintenancecontex(preventiveMaintenanceString);
		}
		if(tasksString != null) {
			setTaskcontex(tasksString);
		}
		if(reminderString != null) {
			setRemindercontex(reminderString);
		}
		if(deleteReadingRulesListString != null) {
			this.deleteReadingRulesList = convertDeleteReadingRulesListString(deleteReadingRulesListString);
		}
		updatePreventiveMaintenance(context);

		return SUCCESS;
	}
	
	
	public String updateNewPreventiveMaintenance() throws Exception {
		FacilioContext context = new FacilioContext();
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
		}
		if(preventiveMaintenanceString != null) {
			setPreventiveMaintenancecontex(preventiveMaintenanceString);
		}
		if(tasksString != null) {
			setTaskSectioncontext(tasksString);
		}
		if (preRequestsString != null) {
			setPreRequestSectioncontext(preRequestsString);
		}
		if( prerequisiteApproverString != null && workorder.getAllowNegativePreRequisiteEnum().equals(AllowNegativePreRequisite.YES_WITH_APPROVAL)) {
			setPreRequisiteApproverContext(prerequisiteApproverString);
		}
		if(reminderString != null) {
			setRemindercontex(reminderString);
		}
		if(deleteReadingRulesListString != null) {
			this.deleteReadingRulesList = convertDeleteReadingRulesListString(deleteReadingRulesListString);
		}

		updatePreventiveMaintenance(context);
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Preventive Maintenance with id #{%d} has been updated", preventivemaintenance.getId()),
				preventivemaintenance.getTitle(),
				AuditLogHandler.RecordType.SETTING,
				"PreventiveMaintenance", preventivemaintenance.getId())
				.setActionType(AuditLogHandler.ActionType.ADD)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", preventivemaintenance.getId());
					json.put("moduleName", moduleName);
					json.put("Created At", preventivemaintenance.getCreatedTime());
					array.add(json);
					return array.toJSONString();
				}).apply(null))

		);
		return SUCCESS;
	}
	
	public Long getSpaceCategoryId() {
		return spaceCategoryId;
	}

	public void setSpaceCategoryId(Long spaceCategoryId) {
		this.spaceCategoryId = spaceCategoryId;
	}

	public Long getAssetCategoryId() {
		return assetCategoryId;
	}

	public void setAssetCategoryId(Long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}

	public List<Long> getIncludeIds() {
		return includeIds;
	}

	public void setIncludeIds(List<Long> includeIds) {
		this.includeIds = includeIds;
	}

	public List<Long> getExcludeIds() {
		return excludeIds;
	}

	public void setExcludeIds(List<Long> excludeIds) {
		this.excludeIds = excludeIds;
	}

	public List<Long> getParentResourceIds() {
		return parentResourceIds;
	}

	public void setParentResourceIds(List<Long> parentResourceIds) {
		this.parentResourceIds = parentResourceIds;
	}

	public Long getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}

	public PMAssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(int assignmentType) {
		this.assignmentType = PMAssignmentType.valueOf(assignmentType);
	}

	public PMAssignmentType getParentAssignmentType() {
		return parentAssignmentType;
	}

	public void setParentAssignmentType(int assignmentType) {
		this.parentAssignmentType = PMAssignmentType.valueOf(assignmentType);
	}

	public List<Long> getAssetCategoryIds() {
		return assetCategoryIds;
	}

	public void setAssetCategoryIds(List<Long> assetCategoryIds) {
		this.assetCategoryIds = assetCategoryIds;
	}

	public List<Long> getSpaceCategoryIds() {
		return spaceCategoryIds;
	}

	public void setSpaceCategoryIds(List<Long> spaceCategoryIds) {
		this.spaceCategoryIds = spaceCategoryIds;
	}

	public boolean isHasFloor() {
		return hasFloor;
	}

	public void setHasFloor(boolean hasFloor) {
		this.hasFloor = hasFloor;
	}
	
	Long spaceCategoryId;
	Long assetCategoryId;
	List<Long> includeIds;
	List<Long> excludeIds;
	List<Long> parentResourceIds;
	Long buildingId;
	PMAssignmentType assignmentType;
	PMAssignmentType parentAssignmentType;

	public List<BaseSpaceContext> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<BaseSpaceContext> buildings) {
		this.buildings = buildings;
	}

	private List<BaseSpaceContext> buildings;

	List<Long> assetCategoryIds;
	List<Long> spaceCategoryIds;
	boolean hasFloor;
	
	private Long siteId;
	
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getSiteId() {
		return this.siteId;
	}

	private List<Long> siteIds;
	public List<Long> getSiteIds() {
		return this.siteIds;
	}

	private List<Long> buildingIds;
	public List<Long> getBuildingIds() {
		return this.buildingIds;
	}

	public void setBuildingIds(List<Long> buildingIds) {
		this.buildingIds = buildingIds;
	}

	public void setSiteIds(List<Long> siteIds) {
		this.siteIds = siteIds;
	}

	public String getScopeFilteredValuesForPM() throws Exception {
		buildings = Collections.emptyList();

		if (assignmentType != null) {

			if (assignmentType.equals(PMAssignmentType.ALL_FLOORS)) {
				List<Long> floorsIds = new ArrayList<>();

				if (includeIds != null) {
					floorsIds = includeIds;
				} else {
					floorsIds = getIdsFromContextList(SpaceAPI.getBuildingFloors(buildingId));
					if (excludeIds != null) {
						floorsIds.removeAll(excludeIds);
					}
				}

				assetCategoryIds = AssetsAPI.getAssetCategoryIds(floorsIds, buildingId, true);
				spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(floorsIds, buildingId);
			} else if(assignmentType.equals(PMAssignmentType.SPACE_CATEGORY) || assignmentType.equals(PMAssignmentType.ALL_SITES)) {
				if(parentAssignmentType == null) {
					List<Long> spaceIds = new ArrayList<>();
					
					if(includeIds != null) {
						spaceIds = includeIds;
					}
					else {
						Long baseSpaceId = null;
						if (buildingId != null && buildingId > 0) {
							baseSpaceId = buildingId;
							spaceIds = getIdsFromSpaceContextList(SpaceAPI.getSpaceListOfCategory(baseSpaceId, spaceCategoryId));
						} else if (this.siteIds  != null && this.siteIds.size() > 0 && spaceCategoryId != null) {
							spaceIds = new ArrayList<>();
							for (long s: this.siteIds) {
								spaceIds.addAll(getIdsFromSpaceContextList(SpaceAPI.getSpaceListOfCategory(s, spaceCategoryId)));
							}
						}
						if(excludeIds != null) {
							spaceIds.removeAll(excludeIds);
						}
					}
					
					assetCategoryIds = AssetsAPI.getAssetCategoryIds(spaceIds, buildingId, true);
					spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(spaceIds, buildingId);

					if(assignmentType.equals(PMAssignmentType.ALL_SITES) && this.siteIds  != null && this.siteIds.size() > 0){
							assetCategoryIds = AssetsAPI.getAssetCategoryIds(siteIds, null, true);
							spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(siteIds, null);
					}
				}
				else {
					List<Long> spaceIds = new ArrayList<>();
					
					if(includeIds != null) {
						spaceIds = includeIds;
					}
					else {
						spaceIds = getIdsFromSpaceContextList(SpaceAPI.getSpaceListOfCategory(parentResourceIds, spaceCategoryId));
						if(excludeIds != null) {
							spaceIds.removeAll(excludeIds);
						}
					}
					
					assetCategoryIds = AssetsAPI.getAssetCategoryIds(spaceIds, buildingId, true);
					spaceCategoryIds = Collections.emptyList();
					spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(spaceIds, buildingId);
				}
			} else if (assignmentType.equals(PMAssignmentType.ALL_BUILDINGS)) {
				if (siteIds != null && siteIds.size() > 0) {
					Set<Long> assetCategSet = new HashSet<>();
					Set<Long> spaceCategSet = new HashSet<>();
					if (this.buildingIds != null && this.buildingIds.size() > 0) {
						for (int i = 0; i < this.siteIds.size(); i++) {
							for (int j = 0; j < this.buildingIds.size(); j++) {
								assetCategSet.addAll(AssetsAPI.getAssetCategoryIds(this.siteIds.get(i), this.buildingIds.get(j), true));
								spaceCategSet.addAll(SpaceAPI.getSpaceCategoryIds(this.siteIds.get(i), this.buildingIds.get(j)));
							}
						}
					} else {
						for (int i = 0; i < siteIds.size(); i++) {
							List<BaseSpaceContext> buildings = SpaceAPI.getSiteBuildingsWithFloors(siteIds.get(i));
							if (buildings != null) {
								for (int j = 0; j < buildings.size(); j++) {
									assetCategSet.addAll(AssetsAPI.getAssetCategoryIds(this.siteIds.get(i), buildings.get(j).getId(), true));
									spaceCategSet.addAll(SpaceAPI.getSpaceCategoryIds(this.siteIds.get(i), buildings.get(j).getId()));
								}
							}
						}
					}
					assetCategoryIds = assetCategSet.stream().collect(Collectors.toList());
					spaceCategoryIds = spaceCategSet.stream().collect(Collectors.toList());
				}
			} else if (assignmentType.equals(PMAssignmentType.ASSET_CATEGORY)) {
				assetCategoryIds = AssetsAPI.getSubCategoryIds(assetCategoryId); //doubt
			}
		} else if (siteIds != null && siteIds.size() > 0) {
			getScopeValuesForMultiSitePM();
		} else if (siteId != null && siteId > -1) {
			getScopeValuesForRegularPM();
		}
		return SUCCESS;
	}

	private void getScopeValuesForMultiSitePM() throws Exception {
		this.assetCategoryIds = new ArrayList<>();
		this.spaceCategoryIds = new ArrayList<>();
		this.buildings = new ArrayList<>();
		Set<Long> assetCategorySet = new HashSet<>();
		Set<Long> spaceCategorySet = new HashSet<>();

		for (int i = 0; i < siteIds.size(); i++) {
			List<BaseSpaceContext> buildings = SpaceAPI.getSiteBuildingsWithFloors(siteIds.get(i));
			if (buildings != null && !buildings.isEmpty()) {
				this.buildings.addAll(buildings);
			}
			assetCategorySet.addAll(AssetsAPI.getAssetCategoryIds(siteIds.get(i), buildingId, true));
			spaceCategorySet.addAll(SpaceAPI.getSpaceCategoryIds(siteIds.get(i), buildingId));
		}
		if (this.siteIds.size() == 1) {
			if (buildingId == null || buildingId < -1) {
				List<BaseSpaceContext> buildings = SpaceAPI.getSiteBuildingsWithFloors(this.siteIds.get(0));
				if (buildings != null && !buildings.isEmpty()) {
					setBuildings(buildings);
					hasFloor = true;
				}
			} else {
				List<BaseSpaceContext> buildings = SpaceAPI.getSiteBuildingsWithFloors(this.siteIds.get(0));
				if (buildings != null && !buildings.isEmpty()) {
					setBuildings(buildings);
				}
				List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(buildingId);
				if (floors != null && !floors.isEmpty()) {
					hasFloor = true;
				}
			}
		}
		this.assetCategoryIds.addAll(assetCategorySet);
		this.spaceCategoryIds.addAll(spaceCategorySet);
	}

	private void getScopeValuesForRegularPM() throws Exception {

		List<BaseSpaceContext> buildings = SpaceAPI.getSiteBuildingsWithFloors(siteId);
		if (buildingId == null || buildingId < -1) {
			if (CollectionUtils.isNotEmpty(buildings)) {
				setBuildings(buildings);
				hasFloor = true;
			}
		} else {
			if (CollectionUtils.isNotEmpty(buildings)) {
				setBuildings(buildings);
			}
			List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(buildingId);
			if (floors != null && !floors.isEmpty()) {
				hasFloor = true;
			}
		}
		this.assetCategoryIds = AssetsAPI.getAssetCategoryIds(siteId, buildingId, true);
		this.spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(siteId, buildingId);
	}

	private List<Long> getIdsFromSpaceContextList(List<SpaceContext> spaceListOfCategory) {
		List<Long> ids = null;
		if (spaceListOfCategory != null && !spaceListOfCategory.isEmpty()) {
			ids = new ArrayList<>();
			for (ModuleBaseWithCustomFields context : spaceListOfCategory) {
				ids.add(context.getId());
			}
		}
		return ids;
	}

	public List<Long> getIdsFromContextList(List<BaseSpaceContext> contexts) {

		List<Long> ids = null;
		if (contexts != null && !contexts.isEmpty()) {
			ids = new ArrayList<>();
			for (ModuleBaseWithCustomFields context : contexts) {
				ids.add(context.getId());
			}
		}
		return ids;
	}
	
	public String updatePreventiveMaintenance(FacilioContext context) throws Exception {

		workorder.setRequester(null);
		preventivemaintenance.setReminders(reminders);

		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
//		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, sectionTemplates);
		context.put(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES, prerequisiteApproverTemplates);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
		context.put(FacilioConstants.ContextNames.IS_UPDATE_PM, true);
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE_IDS, this.deleteReadingRulesList);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
 		
 		List<AttachmentContext> oldAttachments = workorder.getAttachments();
 		if(oldAttachments != null && !oldAttachments.isEmpty()) {
 			context.put(FacilioConstants.ContextNames.EXISTING_ATTACHMENT_LIST, oldAttachments); 			
 		}
 		for(TaskSectionTemplate sec:sectionTemplates){
 			if(sec.isPreRequestSection()){
 				for(TaskTemplate task:sec.getTaskTemplates()){
 					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
 					if(task.getReadingFieldId() > 0){
	 					BooleanField field =(BooleanField) modBean.getField(task.getReadingFieldId());
	 					
	 					String truevalue = (String)task.getAdditionInfo().get("truevalue");
	 					String falsevalue = (String)task.getAdditionInfo().get("falsevalue");
	 					
	 					if((!field.getTrueVal().equals(truevalue)) || (!field.getFalseVal().equals(falsevalue))){
	 						task.setReadingFieldId(-1);
		 					List<String> options = new ArrayList<>();
		 					options.add(truevalue);
		 					options.add(falsevalue);
		 					task.setOptions(options);
	 					}
 					}
 				}
 			}
 		}

 		FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceChain();
 		updatePM.execute(context);

		return SUCCESS;
	}

	private String qrVAL;

	public String getQrVAL() {
		return qrVAL;
	}

	public void setQrVAL(String qrVAL) {
		this.qrVAL = qrVAL;
	}

	private String qrVal;

	public String getQrVal() {
		return qrVal;
	}

	public void setQrVal(String qrVal) {
		this.qrVal = qrVal;
	}

	private long resourceId = -1;

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private PMJobsContext pmJob;

	public PMJobsContext getPmJob() {
		return pmJob;
	}

	public void setPmJob(PMJobsContext pmJob) {
		this.pmJob = pmJob;
	}

	public String updatePreventiveMaintenanceJob() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PM_JOB, pmJob);
		context.put(FacilioConstants.ContextNames.PM_RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.PM_ID, pmId);

		FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceJobChain();
		updatePM.execute(context);

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String preventiveMaintenanceSummary() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id.get(0));

		FacilioChain pmSummary = FacilioChainFactory.getNewPreventiveMaintenanceSummaryChain();
		pmSummary.execute(context);

		setPreventivemaintenance((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setTaskList((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setPreRequestList((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP));
		setListOfTasks((List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST));
		setListOfPreRequests((List<TaskContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_LIST));
		setPrerequisiteApproverTemplates((List<PrerequisiteApproversTemplate>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES));
		setSectionTemplates((List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));
		setPreRequestSectionTemplates((List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS));
		setReminders((List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS));
		return SUCCESS;
	}

	@Getter
	@Setter
	private Collection<PMResourcePlannerContext> resourcePlanners;

	public String pmResourcePlanner() throws Exception {
		FacilioContext context = new FacilioContext();
		Constants.setRecordId(context, id.get(0));

		FacilioChain resourcePlannerChain = FacilioChainFactory.getResourcePlannerChain();
		resourcePlannerChain.execute(context);

		Collection<PMResourcePlannerContext> result = (Collection<PMResourcePlannerContext>) Constants.getResult(context);
		this.setResourcePlanners(result);
		return SUCCESS;
	}

	private boolean hidePMResourcePlanner;

	public void setHidePMResourcePlanner(boolean hidePMResourcePlanner) {
		this.hidePMResourcePlanner = hidePMResourcePlanner;
	}

	public boolean getHidePMResourcePlanner() {
		return this.hidePMResourcePlanner;
	}

	@SuppressWarnings("unchecked")
	public String fetchPreventiveMaintenanceDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id.get(0));
		context.put(ContextNames.HIDE_PM_RESOURCE_PLANNER, getHidePMResourcePlanner());

		FacilioChain pmSummary = FacilioChainFactory.fetchPreventiveMaintenanceDetailsChain();
		pmSummary.execute(context);

		setPreventivemaintenance((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setTaskList((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setPreRequestList((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP));
		setListOfTasks((List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST));
		setListOfPreRequests((List<TaskContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_LIST));
		setPrerequisiteApproverTemplates((List<PrerequisiteApproversTemplate>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES));
		setSectionTemplates((List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));
		setPreRequestSectionTemplates((List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS));
		setReminders((List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS));
		return SUCCESS;
	}
	
	public String fetchRelatedWorkorderList() throws Exception {
		FacilioContext context = new FacilioContext();
		if (pmId > 0) {
			pm.setId(pmId);
		}
		
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
		FacilioChain workorders = FacilioChainFactory.fetchRelatedWorkordersChain();
		workorders.execute(context);
		pm =  (PreventiveMaintenance) (context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		setResult("workorders", pm.getWorkorders());
		return SUCCESS;
	}
	
	public String getPreventiveMaintenanceReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);

		FacilioChain pmReadings = FacilioChainFactory.getPreventiveMaintenanceReadingsChain();
		pmReadings.execute(context);

		Collection<WorkOrderContext> workOrderContexts = (Collection<WorkOrderContext>) context.get(ContextNames.RESULT);

		setResult("workorders", workOrderContexts);

		return SUCCESS;
	}
	
	public static List<Long> getAssetExcludeList(List<Long> assetList, Long categoryId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = 
				new SelectRecordsBuilder<AssetContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(AssetContext.class)
				.andCustomWhere("Assets.ID NOT IN ("+StringUtils.join(assetList, ",")+")")
				.andCustomWhere("CATEGORY = ?", categoryId);
		List<AssetContext> assstContextList = new ArrayList<AssetContext>();
		List<Long> assestLongId = new ArrayList<Long>();
		assstContextList = selectBuilder.get();
		for (AssetContext assetContext : assstContextList) {
			assestLongId.add(assetContext.getId());
		}
		return assestLongId;
	}
	
	public static List<Long> getAssetListCatergory(Long categoryId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = 
				new SelectRecordsBuilder<AssetContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(AssetContext.class)
				.andCustomWhere("CATEGORY = ?", categoryId);
		List<AssetContext> assstContextList = new ArrayList<AssetContext>();
		List<Long> assestLongId = new ArrayList<Long>();
		assstContextList = selectBuilder.get();
		for (AssetContext assetContext : assstContextList) {
			assestLongId.add(assetContext.getId());
		}
		return assestLongId;
	}

	public String deletePreventiveMaintenance() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

		FacilioChain deletePM = FacilioChainFactory.getDeletePreventiveMaintenanceChain();
		deletePM.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		if(rowsUpdated>0){
		for(long pmId : id){
			sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Preventive Maintenance with id #{%d} has been Deleted",pmId),
					null,
					AuditLogHandler.RecordType.SETTING,
					"PreventiveMaintenance", pmId)
					.setActionType(AuditLogHandler.ActionType.ADD)
			);}}
		return SUCCESS;
	}

	public String changePreventiveMaintenanceStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		FacilioChain addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChain();
		addTemplate.execute(context);

		for(long pmId : id){
			sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Preventive Maintenance with id #{%d} has been DeActivated",pmId),
					null,
					AuditLogHandler.RecordType.SETTING,
					"PreventiveMaintenance", pmId)
					.setActionType(AuditLogHandler.ActionType.ADD)
			);}
		return SUCCESS;
	}

	private long assetId = -1;

	public long getAssetId() {
		return assetId;
	}

	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}

	private long spaceId = -1;

	public long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}

	private long startTime = -1;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	private long endTime = -1;

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private List<Map<String, Object>> pmJobList;

	public List<Map<String, Object>> getPmJobList() {
		return pmJobList;
	}

	public void setPmJobList(List<Map<String, Object>> pmJobList) {
		this.pmJobList = pmJobList;
	}

	private boolean userTrigger = false;
	public void setUserTrigger(boolean userTrigger) {
		this.userTrigger = userTrigger;
	}

	public boolean getUserTrigger() {
		return this.userTrigger;
	}

	public String getPMJobs() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, getEndTime());
		context.put(FacilioConstants.ContextNames.CURRENT_CALENDAR_VIEW, getCurrentView());

		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}

		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);

		FacilioChain getPmchain = FacilioChainFactory.getGetNewPMJobListChain();
		getPmchain.execute(context);

		setPmMap((Map<Long, PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		setPmJobList((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_JOBS_LIST));
		setPmTriggerMap((Map<Long, PMTriggerContext>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST));
		setPmResourcesMap((Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES));
		setPmTriggerTimeBasedGroupedMap((Map<Long,Map<Long,List<Map<String, Object>>>>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGER_VS_PMJOB_MAP));
		return SUCCESS;
	}

	public String pmCount () throws Exception {
		return plannedMaintenanceList();
	}
	public String plannedMaintenanceList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		if (getCount() != null) {
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT, getCount());
		}
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}

		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		
		if (getPage() != 0) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

		if (AccountUtil.getCurrentAccount().isFromMobile() && AccountUtil.getCurrentOrg().getOrgId() == 263L) {
			context.put(FacilioConstants.ContextNames.IS_USER_TRIGGER, true);
		}
		context.put(FacilioConstants.ContextNames.IS_FROM_VIEW, Boolean.TRUE);
		FacilioChain getPmchain = FacilioChainFactory.getGetPreventiveMaintenanceListChain();
		getPmchain.execute(context);
		if (getCount() != null) {
			setWoCount((long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT));
		}
		else {
			setPms((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		}
		return SUCCESS;
	}

	private PreventiveMaintenance preventivemaintenance;

	public PreventiveMaintenance getPreventivemaintenance() {
		return preventivemaintenance;
	}

	public void setPreventivemaintenance(PreventiveMaintenance preventivemaintenance) {
		this.preventivemaintenance = preventivemaintenance;
	}

	private List<PreventiveMaintenance> pms;

	public List<PreventiveMaintenance> getPms() {
		return pms;
	}

	public void setPms(List<PreventiveMaintenance> pms) {
		this.pms = pms;
	}

	private Map<Long, PreventiveMaintenance> pmMap;

	public Map<Long, PreventiveMaintenance> getPmMap() {
		return pmMap;
	}

	public void setPmMap(Map<Long, PreventiveMaintenance> pmMap) {
		this.pmMap = pmMap;
	}

	private List<PMJobsContext> pmJobs;

	public List<PMJobsContext> getPmJobs() {
		return pmJobs;
	}

	public void setPmJobs(List<PMJobsContext> pmJobs) {
		this.pmJobs = pmJobs;
	}

	private Map<Long, PMTriggerContext> pmTriggerMap;

	public Map<Long, PMTriggerContext> getPmTriggerMap() {
		return pmTriggerMap;
	}

	public void setPmTriggerMap(Map<Long, PMTriggerContext> pmTriggerMap) {
		this.pmTriggerMap = pmTriggerMap;
	}

	private Map<Long, ResourceContext> pmResourcesMap;

	public Map<Long, ResourceContext> getPmResourcesMap() {
		return pmResourcesMap;
	}

	public void setPmResourcesMap(Map<Long, ResourceContext> pmResourcesMap) {
		this.pmResourcesMap = pmResourcesMap;
	}
	
	Map<Long,Map<Long,List<Map<String, Object>>>> pmTriggerTimeBasedGroupedMap;
	

	public Map<Long, Map<Long, List<Map<String, Object>>>> getPmTriggerTimeBasedGroupedMap() {
		return pmTriggerTimeBasedGroupedMap;
	}

	public void setPmTriggerTimeBasedGroupedMap(
			Map<Long, Map<Long, List<Map<String, Object>>>> pmTriggerTimeBasedGroupedMap) {
		this.pmTriggerTimeBasedGroupedMap = pmTriggerTimeBasedGroupedMap;
	}

	private List<List<Long>> actualTimings;
	
	public List<List<Long>> getActualTimings() {
		return this.actualTimings;
	}
	
	public void setActualTimings(List<List<Long>> actualTimings) {
		this.actualTimings = actualTimings;
	}


	private Map<String, Object> woStatusPercentage;

	public Map<String, Object> getWoStatusPercentage() {
		return woStatusPercentage;
	}

	public void setWoStatusPercentage(Map<String, Object> woStatusPercentage) {
		this.woStatusPercentage = woStatusPercentage;
	}


	private List<Map<String,Object>> avgResponseResolution;

	public List<Map<String,Object>> getAvgResponseResolution() {
		return avgResponseResolution;
	}

	public void setAvgResponseResolution(List<Map<String,Object>> avgResponseResolution) {
		this.avgResponseResolution = avgResponseResolution;
	}


    private Map<String, Object> avgResolutionTimeByCategory;

    public Map<String, Object> getAvgResolutionTimeByCategory() {
        return avgResolutionTimeByCategory;
    }

    public void setAvgResolutionTimeByCategory(Map<String, Object> avgResolutionTimeByCategory) {
        this.avgResolutionTimeByCategory = avgResolutionTimeByCategory;
    }



    public String assignWorkOrder() throws Exception {


		LOGGER.info("assigning with v2");
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSIGN_TICKET);
		if (workorder != null && workorder.getAssignedTo() != null && workorder.getAssignedTo().getId() == -1l) {
			workorder.getAssignedTo().setId(-99l);
		}
		if (workorder != null && workorder.getAssignmentGroup() != null && workorder.getAssignmentGroup().getId() == -1l) {
			workorder.getAssignmentGroup().setId(-99l);
		}
		updateWorkOrder(context);

		return SUCCESS;
	}
	
	private File signature;
	public File getSignature() {
		return signature;
	}
	public void setSignature(File signature) {
		this.signature = signature;
	}
	
	private String signatureFileName;
	public String getSignatureFileName() {
		return signatureFileName;
	}
	public void setSignatureFileName(String signatureFileName) {
		this.signatureFileName = signatureFileName;
	}
	
	private  String signatureContentType;
	public String getSignatureContentType() {
		return signatureContentType;
	}
	public void setSignatureContentType(String signatureContentType) {
		this.signatureContentType = signatureContentType;
	}

	public String closeWorkOrder() throws Exception {

		FacilioContext context = new FacilioContext();
		if (StringUtils.isNotEmpty(workOrderString) && workorder == null) {
			setWorkordercontex(workOrderString);
		}
		if (workorder == null) {
			workorder = new WorkOrderContext();
		}
		// For handling signature if workOrderString is send
		if (signature != null) {
			workorder.setSignature(signature);
			workorder.setSignatureFileName(signatureFileName);
			workorder.setSignatureContentType(signatureContentType);
		}
		context.put(FacilioConstants.ContextNames.ACTUAL_TIMINGS, actualTimings);


		if (stateTransitionId == null || stateTransitionId < 0) {
			workorder.setStatus(TicketAPI.getStatus("Closed")); // We shouldn't
			// allow close to be
			// edited
		}
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		setUpdateWorkorderContext(context);
		CommonCommandUtil.addEventType(EventType.CLOSE_WORK_ORDER, context);

		if (actualWorkDuration != -1) {
			workorder.setActualWorkDuration(actualWorkDuration);
		}
		return updateWorkOrder(context);
	}

	public String resolveWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.SOLVE_WORK_ORDER);
		context.put(FacilioConstants.ContextNames.ACTUAL_TIMINGS, actualTimings);

		workorder = new WorkOrderContext();
		workorder.setStatus(TicketAPI.getStatus("Resolved")); // We shouldn't
		// allow resolve
		// to be edited
		if (actualWorkDuration != -1) {
			workorder.setActualWorkDuration(actualWorkDuration);
		}
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		return updateWorkOrder(context);
	}

	public String deleteWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

		FacilioChain deleteWorkOrder = FacilioChainFactory.getDeleteWorkOrderChain();
		deleteWorkOrder.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		for(long workOrderId : id){
			sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Work Order with id #{%d} has been Deleted",workOrderId),
					null,
					AuditLogHandler.RecordType.MODULE,
					"WorkOrder", workOrderId)
					.setActionType(AuditLogHandler.ActionType.DELETE)
			);
		}

		return SUCCESS;
	}

	public String updateWorkOrder() throws Exception {
		if (AccountUtil.getCurrentOrg().getId() == 274) {
			LOGGER.info("Update WO called");
		}
		FacilioContext context = new FacilioContext();
		if (StringUtils.isNotEmpty(workOrderString) && workorder == null) {
			setWorkordercontex(workOrderString);
		}
		setUpdateWorkorderContext(context);
		return updateWorkOrder(context);
	}
	
	private String comment;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	private Boolean notifyRequester;
	public Boolean getNotifyRequester() {
		if (notifyRequester == null) {
			return false;
		}
		return notifyRequester;
	}
	public void setNotifyRequester(boolean notifyRequester) {
		this.notifyRequester = notifyRequester;
	}
	
	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	private Long approvalTransitionId;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}

	private Long customButtonId;
	public Long getCustomButtonId() {
		return customButtonId;
	}
	public void setCustomButtonId(Long customButtonId) {
		this.customButtonId = customButtonId;
	}

	private void setUpdateWorkorderContext(FacilioContext context) throws Exception {
		EventType activityType = EventType.EDIT;
		if (workorder == null) {
			if (stateTransitionId != null && stateTransitionId > 0) {
				workorder = new WorkOrderContext();
			} else if (approvalTransitionId != null && approvalTransitionId > 0) {
				workorder = new WorkOrderContext();
			} else if (customButtonId != null && customButtonId > 0) {
				workorder = new WorkOrderContext();
			}
		} else if (workOrderString == null && workorder != null) {
			workorder.parseFormData();
		}
		if (workorder.getStatus() != null) {
			EventType type = TicketAPI.getActivityTypeForTicketStatus(workorder.getStatus().getId());
			if (type != null) {
				activityType = type;
			}
		} else if (!context.containsKey(FacilioConstants.ContextNames.EVENT_TYPE) && ((workorder.getAssignedTo() != null && workorder.getAssignedTo().getId() > 0) || (workorder.getAssignmentGroup() != null && workorder.getAssignmentGroup().getId() > 0))) {
			activityType = EventType.ASSIGN_TICKET;
		}

		// cannot update module state directly
		if (workorder.getModuleState() != null) {
			workorder.setModuleState(null);
		}
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, this.ticketattachments);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		if (qrVal != null) {
			context.put(FacilioConstants.ContextNames.QR_VALUE, qrVal);
		} else {
			context.put(FacilioConstants.ContextNames.QR_VALUE, qrVAL);
		}
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
		if (customButtonId != null && customButtonId > 0) {
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST, Collections.singletonList(customButtonId));
			CommonCommandUtil.addEventType(EventType.CUSTOM_BUTTON, context);
		}
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, activityType);
		context.put(FacilioConstants.ContextNames.COMMENT, comment);
		context.put(FacilioConstants.ContextNames.NOTIFY_REQUESTER, getNotifyRequester());
	}

	private boolean closingWorkOrder(Long transitionID) throws Exception {
		if (transitionID == null) {
			return false;
		}
		StateflowTransitionContext transition = (StateflowTransitionContext)
				StateFlowRulesAPI.getStateTransition(transitionID);
		FacilioStatus toState = StateFlowRulesAPI.getStateContext(transition.getToStateId());
		return toState != null &&
				toState.getType().equals(FacilioStatus.StatusType.CLOSED);
	}

	private String qrValue;
	public void setQrValue(String qrValue) {
		this.qrValue = qrValue;
	}
	public String getQrValue() {
		return qrValue;
	}

	private String updateWorkOrder(FacilioContext context) throws Exception {
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
		context.put(FacilioConstants.ContextNames.IS_FROM_V2,true);
		context.put(ContextNames.QR_VALUE,qrValue);

		if (closingWorkOrder(stateTransitionId)) {
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CLOSE_WORK_ORDER);
		}

		FacilioChain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
		updateWorkOrder.execute(context);
		rowsUpdated = (Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setWorkOrders((List<WorkOrderContext>) context.get(ContextNames.WORK_ORDER_LIST));
		return SUCCESS;
	}

	public String viewWorkOrder() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getWorkOrderId());

		FacilioChain getWorkOrderChain = ReadOnlyChainFactory.getWorkOrderDetailsChain();
		getWorkOrderChain.execute(context);

		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setAvailableStates((List<WorkflowRuleContext>) context.get("availableStates"));
		setCurrentState((FacilioStatus) context.get("currentState"));
		return SUCCESS;
	}
	

	private FacilioStatus currentState;
	public FacilioStatus getCurrentState() {
		return currentState;
	}
	public void setCurrentState(FacilioStatus currentState) {
		this.currentState = currentState;
	}

	List<WorkflowRuleContext> availableStates;
	public List<WorkflowRuleContext> getAvailableStates() {
		return availableStates;
	}
	public void setAvailableStates(List<WorkflowRuleContext> availableStates) {
		this.availableStates = availableStates;
	}

	private WorkorderTemplate workorderTemplate;

    public WorkorderTemplate getWorkorderTemplate() {
        return workorderTemplate;
    }

    public void setWorkorderTemplate(WorkorderTemplate workorderTemplate) {
        this.workorderTemplate = workorderTemplate;
    }

    private WorkOrderContext workorder;

    public WorkOrderContext getWorkorder() {
        return workorder;
    }

    public void setWorkorder(WorkOrderContext workorder) {
        this.workorder = workorder;
    }

    private long workOrderId = -1;

	public long getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(long workOrderId) {
		this.workOrderId = workOrderId;
	}
	
	private List<Long> id;

	public List<Long> getId() {
		return id;
	}

	public void setId(List<Long> id) {
		this.id = id;
	}

	private int rowsUpdated = -1;

	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private String count;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	private long woCount;

	public long getWoCount() {
		return woCount;
	}

	public void setWoCount(long woCount) {
		this.woCount = woCount;
	}

	private String recordCount;

	public String getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(long count) {
		this.recordCount = ""+count;
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	private Boolean showViewsCount;
	public Boolean getShowViewsCount() {
		if (showViewsCount == null) {
			return false;
		}
		return showViewsCount;
	}
	public void setShowViewsCount(Boolean showViewsCount) {
		this.showViewsCount = showViewsCount;
	}
	
	List<Map<String, Object>> subViewsCount;
	public List<Map<String, Object>> getSubViewsCount() {
		return subViewsCount;
	}
	public void setSubViewsCount(List<Map<String, Object>> subViewsCount) {
		this.subViewsCount = subViewsCount;
	}
	
	private Boolean fetchTriggers;
	public boolean isFetchTriggers() {
		if (fetchTriggers == null) {
			return false;
		}
		return fetchTriggers;
	}
	public void setFetchTriggers(boolean fetchTriggers) {
		this.fetchTriggers = fetchTriggers;
	}

	private List<File> attachedFiles;
	private List<String> attachedFilesFileName;
	private List<String> attachedFilesContentType;
	private List<AttachmentContext> ticketattachments;
	private AttachmentType attachmentType;
	
	public int getAttachmentType() {
		if(attachmentType != null) {
			return attachmentType.getIntVal();
		}
		return -1;
	}
	public void setAttachmentType(int attachmentType) {
		this.attachmentType = AttachmentType.getType(attachmentType);
	}
	
	public List<File> getAttachedFiles() {
		return attachedFiles;
	}
	public void setAttachedFiles(List<File> attachedFiles) {
		this.attachedFiles = attachedFiles;
	}
	public List<String> getAttachedFilesFileName() {
		return attachedFilesFileName;
	}
	public void setAttachedFilesFileName(List<String> attachedFilesFileName) {
		this.attachedFilesFileName = attachedFilesFileName;
	}
	public List<String> getAttachedFilesContentType() {
		return attachedFilesContentType;
	}
	public void setAttachedFilesContentType(List<String> attachedFilesContentType) {
		this.attachedFilesContentType = attachedFilesContentType;
	}
	public List<AttachmentContext> getTicketattachments() {
		return ticketattachments;
	}
	public void setTicketattachments(List<AttachmentContext> ticketattachments) {
		this.ticketattachments = ticketattachments;
	}

	private String taskSectionString;
	
	public String getTaskSectionString() {
		return taskSectionString;
	}

	public void setTaskSectionString(String taskSectionString) {
		this.taskSectionString = taskSectionString;
	}

	private String preRequestsString;

	public String getPreRequestsString() {
		return preRequestsString;
	}

	public void setPreRequestsString(String preRequestsString) {
		this.preRequestsString = preRequestsString;
	}

	private String prerequisiteApproverString;

	public String getPrerequisiteApproverString() {
		return prerequisiteApproverString;
	}

	public void setPrerequisiteApproverString(String prerequisiteApproverString) {
		this.prerequisiteApproverString = prerequisiteApproverString;
	}

	private String tasksString;
	public String getTasksString() {
		return tasksString;
	}
	public void setTasksString(String tasksString) {
		this.tasksString = tasksString;
	}
	
	public void setTaskcontex(String task_string) throws Exception {
		this.tasks = convertTask(task_string);
		if (this.workorder != null) {
			for (Entry<String, List<TaskContext>> e : this.tasks.entrySet()) {
				for (TaskContext t : e.getValue()) {
					t.setSiteId(this.workorder.getSiteId());
				}
			}
		}
	}

	public List<TaskSectionTemplate> setPreRequestSectioncontext(String preRequestSectionstring) throws Exception {

		JSONParser parser = new JSONParser();
		JSONArray obj = (JSONArray) parser.parse(preRequestSectionstring);

		List<TaskSectionTemplate> taskSectionContextList = FieldUtil.getAsBeanListFromJsonArray(obj,
				TaskSectionTemplate.class);
		taskSectionContextList.stream().forEach(pr -> pr.setPreRequestSection(true));
		getSectionTemplates().addAll(taskSectionContextList);
		return taskSectionContextList;
	}
	
	public List<PrerequisiteApproversTemplate> setPreRequisiteApproverContext(String preRequisiteApproverString) throws Exception {
        JSONParser parser = new JSONParser();
		JSONArray obj = (JSONArray) parser.parse(preRequisiteApproverString);
		List<PrerequisiteApproversTemplate> prerequisiteApproverList = FieldUtil.getAsBeanListFromJsonArray(obj,PrerequisiteApproversTemplate.class);
		setPrerequisiteApproverTemplates(prerequisiteApproverList);
		return prerequisiteApproverList;
	}
	
	public List<TaskSectionTemplate> setTaskSectioncontext(String taskSectionstring) throws Exception {
		
		JSONParser parser = new JSONParser();
		JSONArray obj = (JSONArray) parser.parse(taskSectionstring);
		
		List<TaskSectionTemplate> taskSectionContextList = FieldUtil.getAsBeanListFromJsonArray(obj, TaskSectionTemplate.class);
		setSectionTemplates(taskSectionContextList);
		
//		tasks = new HashMap<>();
//		for(TaskSectionTemplate taskSectionContext :taskSectionContextList) {
//			tasks.put(taskSectionContext.getName(), taskSectionContext.getTasks());
//		}
		return taskSectionContextList;
	}
	
	public Map<String, List<TaskContext>> convertTask(String task_string) throws Exception
	{
		Map<String, List<TaskContext>> taskObj = new HashMap<>();
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(task_string);
		Iterator itr = obj.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			JSONArray taskList = (JSONArray) obj.get(key);
			List<TaskContext> taskContextList = FieldUtil.getAsBeanListFromJsonArray(taskList, TaskContext.class);
			taskObj.put(key, taskContextList);
		}
		return taskObj;
	}
	
	public String getMultiplePMResources() throws Exception {
		
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);

		FacilioChain getResourcesListForMultiplePM = ReadOnlyChainFactory.getResourcesListForMultiplePM();
		getResourcesListForMultiplePM.execute(context);
		
		setMultiPmResourceIds((List<Long>)context.get(FacilioConstants.ContextNames.MULTI_PM_RESOURCE_IDS));
		setMultiPmResources((List<ResourceContext>)context.get(FacilioConstants.ContextNames.MULTI_PM_RESOURCES));
		setPreventivemaintenance((PreventiveMaintenance)context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		
		return SUCCESS;
	}

	List<Long> multiPmResourceIds;
	List<ResourceContext> multiPmResources;
	
	
	public List<Long> getMultiPmResourceIds() {
		return multiPmResourceIds;
	}

	public void setMultiPmResourceIds(List<Long> multiPmResourceIds) {
		this.multiPmResourceIds = multiPmResourceIds;
	}

	public List<ResourceContext> getMultiPmResources() {
		return multiPmResources;
	}

	public void setMultiPmResources(List<ResourceContext> multiPmResources) {
		this.multiPmResources = multiPmResources;
	}

	private String preventiveMaintenanceString;
	
	public String getPreventiveMaintenanceString() {
		return preventiveMaintenanceString;
	}
	public void setPreventiveMaintenanceString(String preventiveMaintenanceString) {
		this.preventiveMaintenanceString = preventiveMaintenanceString;
	}
	
	public void setPreventiveMaintenancecontex(String preventiveMaintenanceString) throws Exception {
		
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(preventiveMaintenanceString);
		this.preventivemaintenance = FieldUtil.getAsBeanFromJson(obj, PreventiveMaintenance.class);
	}
	
	private String deleteReadingRulesListString;	
	public String getDeleteReadingRulesListString() {
		return deleteReadingRulesListString;
	}
	public void setDeleteReadingRulesListString(String deleteReadingRulesListString) {
		this.deleteReadingRulesListString = deleteReadingRulesListString;
	}
	
	public List<Long> convertDeleteReadingRulesListString(String convertDeleteReadingRulesListString)
	{
		List<Long> re = new ArrayList<>();
		JSONParser parser = new JSONParser();
		try {
			re = (JSONArray) parser.parse(convertDeleteReadingRulesListString);

		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
		return re;
	}
	
	private String reminderString;	
	public String getReminderString() {
		return reminderString;
	}
	public void setReminderString(String reminderString) {
		this.reminderString = reminderString;
	}
	
	public void setRemindercontex(String reminder_string) throws ParseException {
		JSONParser parser = new JSONParser();
		try {
			JSONArray obj = (JSONArray) parser.parse(reminder_string);
			this.reminders = new ArrayList<>();
			for(Object reminderObj: obj){
				if ( reminderObj instanceof JSONObject ) {
					JSONObject reminderJson = ((JSONObject) reminderObj);
					JSONArray reminderActions = (JSONArray) reminderJson.get("reminderActions");
					List<PMReminderAction> reminderActionList= new ArrayList<>();
					if (reminderActions != null) {
						for (Object remAction: reminderActions) {
							JSONObject remActionJson = ((JSONObject) remAction);
							JSONObject action = (JSONObject) remActionJson.get("action");
							JSONObject template = (JSONObject) action.get("template");
							Template t = null;
							if (template != null) {
								Object type = template.get("type");
								Type templateType = Type.getType(Integer.valueOf(((Long) type).toString()));
								if (templateType == Type.PUSH_NOTIFICATION) {
									t = FieldUtil.getAsBeanFromMap(template, PushNotificationTemplate.class);
								} else if (templateType == Type.DEFAULT) {
									template.remove("originalTemplate");
									t = FieldUtil.getAsBeanFromMap(template, DefaultTemplate.class);
								} else if (templateType == Type.EMAIL) {
									t = FieldUtil.getAsBeanFromMap(template, EMailTemplate.class);
								} else if (templateType == Type.SMS) {
									t = FieldUtil.getAsBeanFromMap(template, SMSTemplate.class);
								}
								action.remove("template");
							}

							ActionContext actionContext = FieldUtil.getAsBeanFromMap(action, ActionContext.class);
							actionContext.setTemplate(t);
							remActionJson.remove("action");
							PMReminderAction asBeanFromMap = FieldUtil.getAsBeanFromMap(remActionJson, PMReminderAction.class);
							asBeanFromMap.setAction(actionContext);
							reminderActionList.add(asBeanFromMap);
						}
					}
					reminderJson.remove("reminderActions");
					PMReminder asBeanFromMap = FieldUtil.getAsBeanFromMap(reminderJson, PMReminder.class);
					asBeanFromMap.setReminderActions(reminderActionList);
					reminders.add(asBeanFromMap);
				}
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
	}
	private String woIds;
	public String getWoIds() {
		return woIds;
	}

	public void setWoIds(String woIds) {
		this.woIds = woIds;
	}

	private String workOrderString;
	public String getWorkOrdertString() {
		return workOrderString;
	}
	public void setWorkOrderString(String workOrderString) {
		this.workOrderString = workOrderString;
	}
	
	public void setWorkordercontex(String workorder_string) throws Exception {

		LOGGER.info("A :: before parsing wo-string: " + workorder_string);

		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(workorder_string);
		this.workorder = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
		if (this.workorder != null && MapUtils.isNotEmpty(this.workorder.getData())) {
			Map<String, Object> data = this.workorder.getData();
			// temp fix to convert int to long for id
			for (Object value : data.values()) {
				if (value instanceof Map) {
					Object id = ((Map) value).get("id");
					if (id instanceof Number) {
						((Map) value).put("id", ((Number) id).longValue());
					}
				}
			}
		}

		LOGGER.info("B :: after parsing wo-string" + this.workorder.getData());
	}

	@SuppressWarnings("unchecked")
	public String workOrderList() throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		if (woIds != null) {
		String strNew = woIds.substring(1, woIds.length() - 1);
		List<Long> ids = Arrays.asList(strNew.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());		
		context.put(FacilioConstants.ContextNames.WO_IDS, ids);
		}
		if (isApproval()) {
			setViewName("approval_" + getViewName());
		}
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
		context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, criteria);
		
		// Currently setting tenant portal based on view name. Will be removed once scoping is handled
		boolean isTenantPortal = getViewName() != null && getViewName().equals("tenantWorkorder");
		context.put(FacilioConstants.ContextNames.IS_TENANT_PORTAL, isTenantPortal);
		
		context.put(FacilioConstants.ContextNames.WO_DUE_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WO_DUE_ENDTIME, getEndTime());
		context.put(FacilioConstants.ContextNames.IS_APPROVAL, isApproval());
		if (getCount() != null) {	// only count
			context.put(FacilioConstants.ContextNames.WO_LIST_COUNT, getCount());
		}
		context.put(FacilioConstants.ContextNames.FETCH_TRIGGERS, isFetchTriggers());
		if(getShowViewsCount()) {
			context.put(FacilioConstants.ContextNames.WO_VIEW_COUNT, true);
		}
		if( getSubView() != null) {
			context.put(FacilioConstants.ContextNames.SUB_VIEW, getSubView());
		}
		
		if (getClientCriteria() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getClientCriteria());
			Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
			context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
		}
		
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		
		context.put(ContextNames.WO_FETCH_ALL, getFetchAllType());
		context.put(FacilioConstants.ContextNames.CRITERIA_IDS, getCriteriaIds());

		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "workorder.subject,workorder.description");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}

		if (getOverrideViewOrderBy() && getOrderBy() != null) {
			JSONObject sorting = new JSONObject();
			sorting.put("orderBy", getOrderBy());
			sorting.put("orderType", getOrderType());
			context.put(FacilioConstants.ContextNames.SORTING, sorting);
			context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
		}
		
		if (isCalendarApi()) {
			context.put(FacilioConstants.ContextNames.FETCH_SELECTED_FIELDS, getCalendarSelectFields());
			context.put(FacilioConstants.ContextNames.FETCH_CUSTOM_FIELDS, true);
			context.put(FacilioConstants.ContextNames.FETCH_AS_MAP, true);
			context.put(FacilioConstants.ContextNames.DONT_FETCH_WO_WITH_DELETED_RESOURCES, true);
		}
		else if (getSelectFields() != null) {
 			context.put(FacilioConstants.ContextNames.FETCH_SELECTED_FIELDS, getSelectFields());			
 		}

		JSONObject pagination = new JSONObject();
		pagination.put("page", getPage());
		pagination.put("perPage", getPerPage());

		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		
		FacilioChain workOrderListChain = ReadOnlyChainFactory.getWorkOrderListChain();
		workOrderListChain.execute(context);

		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		if (getCount() != null) {
//			setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
			setWoCount((long) context.get(FacilioConstants.ContextNames.WORK_ORDER_COUNT));
			System.out.println("data" + getWoCount() + getViewName());
		}
		else if (isCalendarApi()) {
			setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, context.get("props"));
		}
		else {
			if(getShowViewsCount()) {
				setSubViewsCount((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.SUB_VIEW_COUNT));
				setSubView((String) context.get(FacilioConstants.ContextNames.SUB_VIEW));
			}
			setWorkOrders((List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
			setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		}
		setStateFlows((Map<String, List<WorkflowRuleContext>>) context.get("stateFlows"));
		setCustomButtons((List<CustomButtonRuleContext>) context.get(ContextNames.CUSTOM_BUTTONS));
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if (cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}
		return SUCCESS;
	}
	
	private Boolean calendarApi;
	public boolean isCalendarApi() {
		if (calendarApi == null) {
			return false;
		}
		return calendarApi;
	}

	public void setCalendarApi(Boolean calendarApi) {
		this.calendarApi = calendarApi;
	}

	private Map<Long, List<TaskContext>> preRequestList;

	public Map<Long, List<TaskContext>> getPreRequestList() {
		return preRequestList;
	}

	public void setPreRequestList(Map<Long, List<TaskContext>> preRequestList) {
		this.preRequestList = preRequestList;
	}
	private Map<Long, List<TaskContext>> taskList;

	public Map<Long, List<TaskContext>> getTaskList() {
		return taskList;
	}

	public void setTaskList(Map<Long, List<TaskContext>> taskList) {
		this.taskList = taskList;
	}

	private List<TaskContext> listOfPreRequests;

	public List<TaskContext> getListOfPreRequests() {
		return listOfPreRequests;
	}

	public void setListOfPreRequests(List<TaskContext> listOfPreRequests) {
		this.listOfPreRequests = listOfPreRequests;
	}
	private List<TaskContext> listOfTasks;
	public List<TaskContext> getListOfTasks() {
		return listOfTasks;
	}
	public void setListOfTasks(List<TaskContext> taskTemplates) {
		this.listOfTasks = taskTemplates;
	}

	private List<TaskSectionTemplate> preRequestSectionTemplates;

	public List<TaskSectionTemplate> getPreRequestSectionTemplates() {
		return preRequestSectionTemplates;
	}

	public void setPreRequestSectionTemplates(List<TaskSectionTemplate> preRequestSectionTemplates) {
		this.preRequestSectionTemplates = preRequestSectionTemplates;
	}

	List<PrerequisiteApproversTemplate> prerequisiteApproverTemplates;

	public List<PrerequisiteApproversTemplate> getPrerequisiteApproverTemplates() {
		return prerequisiteApproverTemplates;
	}

	public void setPrerequisiteApproverTemplates(List<PrerequisiteApproversTemplate> prerequisiteApproverTemplates) {
		this.prerequisiteApproverTemplates = prerequisiteApproverTemplates;
	}

	private List<TaskSectionTemplate> sectionTemplates;

	public List<TaskSectionTemplate> getSectionTemplates() {
		return sectionTemplates;
	}

	public void setSectionTemplates(List<TaskSectionTemplate> sectionTemplates) {
		this.sectionTemplates = sectionTemplates;
	}

	private Map<Long, TaskSectionContext> sections;

	public Map<Long, TaskSectionContext> getSections() {
		return sections;
	}

	public void setSections(Map<Long, TaskSectionContext> sections) {
		this.sections = sections;
	}

	private Map<String, List<TaskContext>> tasks;
	
	public Map<String, List<TaskContext>> getTasks() {
		return tasks;
	}

	public void setTasks(Map<String, List<TaskContext>> tasks) {
		this.tasks = tasks;
	}

	private List<WorkOrderContext> workOrders;

	public List<WorkOrderContext> getWorkOrders() {
		return workOrders;
	}

	public void setWorkOrders(List<WorkOrderContext> workOrders) {
		this.workOrders = workOrders;
	}
	
	private Boolean approval;
	public Boolean getApproval() {
		return approval;
	}
	public void setApproval(Boolean approval) {
		this.approval = approval;
	}
	public boolean isApproval() {
		if (approval != null) {
			return approval.booleanValue();
		}
		return false;
	}
	
	private String formName;
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public String getFormName() {
		return this.formName;
	}

	public String getModuleLinkName() {
		return FacilioConstants.ContextNames.WORK_ORDER;
	}

	public ViewLayout getViewlayout() {
		return ViewLayout.getViewWorkOrderLayout();
	}

	public List<WorkOrderContext> getRecords() {
		return workOrders;
	}

	private String viewName = null;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private String subView;
	public String getSubView() {
		return subView;
	}
	public void setSubView(String subView) {
		this.subView = subView;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private String displayName = "All Work Orders";

	public String getViewDisplayName() {
		return displayName;
	}

	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private Boolean fetchAllType;
	public Boolean getFetchAllType() {
		if (fetchAllType == null) {
			return false;
		}
		return fetchAllType;
	}
	public void setFetchAllType(Boolean fetchAllType) {
		this.fetchAllType = fetchAllType;
	}

	public RecordSummaryLayout getRecordSummaryLayout() {
		return RecordSummaryLayout.getRecordSummaryTicketLayout();
	}

	public WorkOrderContext getRecord() {
		return workorder;
	}

	private String filters;

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getFilters() {
		return this.filters;
	}

	String orderBy;

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	String orderType;

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderType() {
		return this.orderType;
	}
	
	private Boolean overrideViewOrderBy;
	public boolean getOverrideViewOrderBy() {
		if (overrideViewOrderBy == null) {
			return false;
		}
		return overrideViewOrderBy;
	}
	public void setOverrideViewOrderBy(boolean overrideViewOrderBy) {
		this.overrideViewOrderBy = overrideViewOrderBy;
	}

	String search;

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearch() {
		return this.search;
	}

	private int page;

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return this.page;
	}

	public int perPage = 40;

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getPerPage() {
		return this.perPage;
	}

	private String criteriaIds;

	public void setCriteriaIds(String criteriaIds) {
		this.criteriaIds = criteriaIds;
	}

	public String getCriteriaIds() {
		return this.criteriaIds;
	}
	
	private String clientCriteria;
	
	
	public String getClientCriteria() {
		return clientCriteria;
	}

	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}

	Criteria criteria;

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public String getActivitiesList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET_ID, workOrderId);

		FacilioChain activitiesChain = FacilioChainFactory.getTicketActivitiesChain();
		activitiesChain.execute(context);

		setActivities((List<TicketActivity>) context.get(FacilioConstants.TicketActivity.TICKET_ACTIVITIES));

		return SUCCESS;
	}
	public String fetchActivity() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, workOrderId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		FacilioChain workOrderActivity = ReadOnlyChainFactory.getActivitiesChain();
		workOrderActivity.execute(context);
		List<ActivityContext> activity =  (List<ActivityContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult("activity", activity);
		return SUCCESS;
	}

	private List<TicketActivity> activities;

	public List<TicketActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<TicketActivity> activities) {
		this.activities = activities;
	}

	private long actualWorkDuration = -1;

	public long getActualWorkDuration() {
		return actualWorkDuration;
	}

	public void setActualWorkDuration(long actualWorkDuration) {
		this.actualWorkDuration = actualWorkDuration;
	}

	private long estimatedDuration = -1;

	public long getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(long estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}

	public String getEstimatedWorkDuration() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getWorkOrderId());

		FacilioChain getWorkOrderChain = FacilioChainFactory.getWorkOrderDataChain();
		getWorkOrderChain.execute(context);

		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);

		if (workorder != null) {
			long duration = TicketAPI.getEstimatedWorkDuration(workorder, System.currentTimeMillis());
			setEstimatedDuration(duration);
		}

		return SUCCESS;
	}
	
	public String getWorkOrderStatusPercentage() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME, getEndTime());


		FacilioChain workOrderStatusPercentageListChain = ReadOnlyChainFactory.getWorkOrderStatusPercentageChain();
		workOrderStatusPercentageListChain.execute(context);


		setWoStatusPercentage((Map<String, Object>) context.get(FacilioConstants.ContextNames.WORK_ORDER_STATUS_PERCENTAGE_RESPONSE));
		

		return SUCCESS;
	}
	
	
	public String getAvgResolutionResponseTimeBySite() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME, getEndTime());


		FacilioChain avgResponseResolutionTimeChain = ReadOnlyChainFactory.getAvgResponseResolutionTimeBySiteChain();
		avgResponseResolutionTimeChain.execute(context);



		setAvgResponseResolution((List<Map<String,Object>>) context.get(FacilioConstants.ContextNames.WORKORDER_INFO_BY_SITE));

		
		
		return SUCCESS;
	}
	public String getTopNTechnicians() throws Exception
	{
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_TECHNICIAN_COUNT, getRecordCount());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME, getEndTime());
		
		FacilioChain woTechCountBySite = ReadOnlyChainFactory.getTopNTechBySiteChain();
		woTechCountBySite.execute(context);


		setTopTechnicians((Map<String, Object>) context.get(FacilioConstants.ContextNames.TOP_N_TECHNICIAN));


		return SUCCESS;

	}

	public String getWoCountBySite() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME, getEndTime());


		FacilioChain woCountBySite = ReadOnlyChainFactory.getWorkOrderCountBySiteChain();
		woCountBySite.execute(context);


		setWorkOrderBySite((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.SITE_ROLE_WO_COUNT));


		return SUCCESS;
	}

	

	public String getAvgWorkCompletionByCategory() throws Exception {

		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME, getEndTime());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID, getSiteId());

		FacilioChain avgCompletionTimeByCategoryChain = ReadOnlyChainFactory.getAvgCompletionTimeByCategoryChain();
		avgCompletionTimeByCategoryChain.execute(context);
		Map<String, Object> resp = (Map<String,Object>) context.get(FacilioConstants.ContextNames.WORK_ORDER_AVG_RESOLUTION_TIME);

		setAvgResolutionTimeByCategory(resp) ;

		return SUCCESS;
	}


	private List<Long> deleteReadingRulesList;
	public void setDeleteReadingRulesList(List<Long> deleteReadingRulesList) {
		this.deleteReadingRulesList = deleteReadingRulesList;
	}
	
	public List<Long> getDeleteReadingRulesList() {
		return this.deleteReadingRulesList;
	}
	
	/******************      V2 Api    ******************/
	
	public String v2viewWorkOrder() throws Exception {
		viewWorkOrder();
		if (workorder == null) {
			workorder = new WorkOrderContext();
		}
		setResult(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		setResult(FacilioConstants.ContextNames.AVAILABLE_STATES, getAvailableStates());
		setResult(FacilioConstants.ContextNames.CURRENT_STATE, getCurrentState());
		
		return SUCCESS;
	}

	public String v2BulkAddWorkOrder() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddBulkWorkOrderChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.WORK_ORDER_LIST, getWorkOrders());
		chain.execute();

		setResult(ContextNames.WORK_ORDER_LIST, context.get(ContextNames.WORK_ORDER_LIST));

		return SUCCESS;
	}

	public String v2addWorkOrder() throws Exception {

		// Temp: Added this for Mercatus account
		try {
			if(AccountUtil.getCurrentOrg().getOrgId() == 10) {
				HttpServletRequest request = ServletActionContext.getRequest();
				if (request != null) {
					if (request.getReader() != null) {
						String requestBody = IOUtils.toString(request.getReader());
						LOGGER.info("v2 WO create requestBody: " + requestBody);
					}
				}
			}
		}catch (Exception e){
			LOGGER.error("Error occurred while trying to get v2 WO create requestBody.", e);
		}


		addWorkOrder();
		v2viewWorkOrder();
		if (workorder != null) {
			setResult(FacilioConstants.ContextNames.MODIFIED_TIME, workorder.getModifiedTime());
		}
		return SUCCESS;
	}
	
	public String v2updateWorkOrder() throws Exception {
		updateWorkOrder();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		if (workOrders.size() == 1) {
			setWorkOrderId(workOrders.get(0).getId());
			v2viewWorkOrder();
			setResult(FacilioConstants.ContextNames.MODIFIED_TIME, workorder.getModifiedTime());
		}
		else {
			setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		}
		for(long workOrderId : id) {
			sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Work Order with id #{%d} has been Updated", workOrderId),
					null,
					AuditLogHandler.RecordType.MODULE,
					"WorkOrder", workOrderId)
					.setActionType(AuditLogHandler.ActionType.UPDATE)
			);
		}
		return SUCCESS;
	}
	
	public String v2assignWorkOrder() throws Exception {
		assignWorkOrder();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		if (workOrders.size() == 1) {
			setWorkOrderId(workOrders.get(0).getId());
			v2viewWorkOrder();
			setResult(FacilioConstants.ContextNames.MODIFIED_TIME, workorder.getModifiedTime());
		}
		else {
			setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		}

		return SUCCESS;
	}
	
	public String v2workOrderList() throws Exception {
		
		workOrderList();
		if (getCount() != null) {
			setResult(FacilioConstants.ContextNames.WORK_ORDER_COUNT, woCount);	
		}
		if (getSubView() != null) {
			setResult(FacilioConstants.ContextNames.SUB_VIEW, subView);
		}
		if (getShowViewsCount()) {
			setResult(FacilioConstants.ContextNames.SUB_VIEW_COUNT, subViewsCount);
		}
		setResult("stateFlows", stateFlows);
		setResult(FacilioConstants.ContextNames.CUSTOM_BUTTONS, customButtons);
		return SUCCESS;
	}
	
	public String v2workOrderCount () throws Exception {
		workOrderList();	
		setResult(FacilioConstants.ContextNames.WORK_ORDER_COUNT, woCount);		
		return SUCCESS;
	}
	
	private Map<String, List<WorkflowRuleContext>> stateFlows;
	public Map<String, List<WorkflowRuleContext>> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(Map<String, List<WorkflowRuleContext>> stateFlows) {
		this.stateFlows = stateFlows;
	}

	private List<CustomButtonRuleContext> customButtons;
	public List<CustomButtonRuleContext> getCustomButtons() {
		return customButtons;
	}
	public void setCustomButtons(List<CustomButtonRuleContext> customButtons) {
		this.customButtons = customButtons;
	}

	private Boolean publicRequest;
	public Boolean getPublicRequest() {
		if (publicRequest == null) {
			return false;
		}
		return publicRequest;
	}
	public void setPublicRequest(Boolean publicRequest) {
		this.publicRequest = publicRequest;
	}

	public String addPortalOrders() throws Exception {
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
		}
		else if(workorder != null) {
			workorder.parseFormData();
		}

		//The following has to be moved to chain
		workorder.setSourceType(SourceType.SERVICE_PORTAL_REQUEST);
		workorder.setSendForApproval(true);
		FacilioStatus preOpenStatus = TicketAPI.getStatus("preopen");
		workorder.setStatus(preOpenStatus);
		if (workorder.getRequester() == null && AccountUtil.getCurrentUser() != null && !getPublicRequest()) {
			workorder.setRequester(AccountUtil.getCurrentUser());
		}

		addWorkOrder(workorder);
		v2viewWorkOrder();
		return SUCCESS;
	}
	
	public String syncOfflineWorkOrders() throws Exception {
		if (lastSyncTime == null || lastSyncTime <= 0 ) {
			throw new IllegalArgumentException("Workorder last synced time is mandatory");
		}
		Map<Long, Map<String, Object>> errors = new HashMap<>();
		int rowsUpdated = 0; 
		for(WorkOrderContext wo: workOrders) {
			try {
				setWorkorder(wo);
				setId(Collections.singletonList(wo.getId()));
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.LAST_SYNC_TIME, lastSyncTime);
				setUpdateWorkorderContext(context);
				
				updateWorkOrder(context);
				rowsUpdated += this.rowsUpdated;
			}
			catch(Exception e) {
				Map<String, Object> obj = new HashMap<>();
				obj.put("data", FieldUtil.getAsJSON(wo).toJSONString());
				obj.put("error", e.getMessage());
				errors.put(wo.getId(), obj);
			}
		}
		if (!errors.isEmpty()) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.LAST_SYNC_TIME, lastSyncTime);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
			context.put(FacilioConstants.ContextNames.CUSTOM_OBJECT, errors);
			
			FacilioChain offlineSync = FacilioChainFactory.addOfflineSyncErrorChain();
			offlineSync.execute(context);
			
			setResult("error", errors.size() + " workorder(s) sync failed");
		}
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	private Long lastSyncTime;
	public Long getLastSyncTime() {
		return lastSyncTime;
	}
	public void setLastSyncTime(Long lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
	
	private List<Map<String,Object>> workOrderBySite;
	public List<Map<String,Object>> getWorkOrderBySite() {
		return workOrderBySite;
	}
	public void setWorkOrderBySite(List<Map<String,Object>> workOrderBySite) {
		this.workOrderBySite = workOrderBySite;
	}
	private Map<String,Object> topTechnicians;
	public Map<String,Object> getTopTechnicians() {
		return topTechnicians;
	}
	public void setTopTechnicians(Map<String,Object> topTechnicians) {
		this.topTechnicians = topTechnicians;
	}
	
	private String dateField;
	public String getDateField() {
		return dateField;
	}
	public void setDateField(String dateField) {
		this.dateField = dateField;
	}
	
	public String calendarWOs() throws Exception {
		return calendarApi(false);
	}
	
	public String calendarWOCount() throws Exception {
		return calendarApi(true);
	}
	
	private String calendarApi (boolean isCount) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DATE_FIELD, dateField);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		context.put(FacilioConstants.ContextNames.COUNT, isCount);
		
		FacilioChain woChain = ReadOnlyChainFactory.getCalendarWorkOrdersChain();
		woChain.execute(context);
		
		if (isCount) {
			setResult("count", context.get(FacilioConstants.ContextNames.WORK_ORDER_COUNT));
		}
		else {
			setResult("workorders", context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
		}
		
		return SUCCESS;
	}
	
	public String v2resolveWorkorder() throws Exception {
		resolveWorkOrder();
		setResult(FacilioConstants.ContextNames.RESULT, "success");
		setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		if (workOrders.size() == 1) {
			setWorkOrderId(workOrders.get(0).getId());
			v2viewWorkOrder();
			setResult(FacilioConstants.ContextNames.MODIFIED_TIME, workorder.getModifiedTime());
		}
		else {
			setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		}
		return SUCCESS;
	}
	
	public String v2closeWorkorder() throws Exception {
			LOGGER.info("closing with v2");
			closeWorkOrder();
			setResult(FacilioConstants.ContextNames.RESULT, "success");
			if (workOrders.size() == 1) {
				setWorkOrderId(workOrders.get(0).getId());
				v2viewWorkOrder();
				setResult(FacilioConstants.ContextNames.MODIFIED_TIME, workorder.getModifiedTime());
			} else {
				setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
			}
		return SUCCESS;
	}
	
	private List<String> getCalendarSelectFields() {
		return Arrays.asList("createdTime", "subject", "assignedTo", "assignmentGroup", "trigger", "resource", "category", "priority", "type", "moduleState", "scheduledStart");
	}


	private int preRequestStatus;

	public int getPreRequestStatus() {
		return preRequestStatus;
	}

	public void setPreRequestStatus(int preRequestStatus) {
		this.preRequestStatus = preRequestStatus;
	}

	public String v2GetPrerequisiteStatus() throws Exception {
		preRequestStatus = WorkOrderAPI.getWorkOrder(workOrderId).getPreRequestStatus();
		setResult("preRequestStatus", preRequestStatus);
		return SUCCESS;
	}

	public List<Long> getWorkOrderIds() {
		return workOrderIds;
	}

	public void setWorkOrderIds(List<Long> workOrderIds) {
		this.workOrderIds = workOrderIds;
	}

	public Boolean getDoNotExecute() {
		return doNotExecute;
	}

	private int limit = -1;



	public void setDoNotExecute(Boolean doNotExecute) {
		this.doNotExecute = doNotExecute;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getFromOrgId() {
		return fromOrgId;
	}

	public void setFromOrgId(long fromOrgId) {
		this.fromOrgId = fromOrgId;
	}

	public long getToOrgId() {
		return toOrgId;
	}

	public void setToOrgId(long toOrgId) {
		this.toOrgId = toOrgId;
	}
	
	public String getRequesterWoCount() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getRequesterWoCount();
		FacilioContext context = chain.getContext();
		chain.execute();
		
		setResult("all", context.get("all"));
		setResult("open", context.get("open"));
		setResult("closed", context.get("closed"));
		
		return SUCCESS;
	}
}
