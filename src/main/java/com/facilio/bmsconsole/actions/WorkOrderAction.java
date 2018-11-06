package com.facilio.bmsconsole.actions;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class WorkOrderAction extends FacilioAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(WorkOrderAction.class.getName());

    public String newWorkOrder() throws Exception {

		FacilioContext context = new FacilioContext();
		Chain newTicket = FacilioChainFactory.getNewWorkOrderChain();
		newTicket.execute(context);

		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));

		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

		return SUCCESS;
	}

	private List<FacilioField> fields;

	public List getFormlayout() {
		return FormLayout.getNewTicketLayout(fields);
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

	public String addWorkOrder() throws Exception {
		if(workOrderString != null) {
			setWorkordercontex(workOrderString);
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
		
		context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
		if (AccountUtil.getCurrentUser() == null && workorder.getRequester() != null && workorder.getRequester().getEmail() != null) {
			context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);
		}
		
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
 		
 		if (this.getFormName() != null && !this.getFormName().isEmpty()) {
			context.put(FacilioConstants.ContextNames.FORM_NAMES, new String[]{this.getFormName()});
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

	public String addWorkOrderFromTemplate() throws Exception {

		JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(getTemplateId());
		JSONParser parser = new JSONParser();
		JSONObject content = (JSONObject) parser
				.parse((String) template.getTemplate(new HashMap<String, Object>()).get("content"));

		WorkOrderContext workorder = FieldUtil.getAsBeanFromJson(content, WorkOrderContext.class);

		return addWorkOrder(workorder);
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
		if(reminderString != null) {
			setRemindercontex(reminderString);
		}
		addPreventiveMaintenance(context);
		return SUCCESS;
	}

	public String addPreventiveMaintenance(FacilioContext context) throws Exception {

		workorder.setRequester(null);
		preventivemaintenance.setReminders(reminders);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, sectionTemplates);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);

		Chain addTemplate = FacilioChainFactory.getAddPreventiveMaintenanceChain();
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
		System.out.printf("addBulkPreventiveMaintenance" + assetLists.size());
		
		
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
					if (pmTContext.getReadingRuleId() != -1) {
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
			Chain addTemplate = FacilioChainFactory.getAddPreventiveMaintenanceChain();
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

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);
		context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
		context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);

		Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
		executePm.execute(context);

		workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);

		return SUCCESS;
	}

	public String executePMs() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
		context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);

		Chain executePm = FacilioChainFactory.getExecutePMsChain();
		executePm.execute(context);

		id = (List<Long>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);

		return SUCCESS;
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
		if(reminderString != null) {
			setRemindercontex(reminderString);
		}
		if(deleteReadingRulesListString != null) {
			this.deleteReadingRulesList = convertDeleteReadingRulesListString(deleteReadingRulesListString);
		}
		updatePreventiveMaintenance(context);
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

	List<Long> assetCategoryIds;
	List<Long> spaceCategoryIds;
	boolean hasFloor;
	
	public String getScopeFilteredValuesForPM() throws Exception {
		
		if(assignmentType != null) {
			
			if(assignmentType.equals(PMAssignmentType.ALL_FLOORS)) {
				List<Long> floorsIds = new ArrayList<>();
				
				if(includeIds != null) {
					floorsIds = includeIds;
				}
				else {
					floorsIds = getIdsFromContextList(SpaceAPI.getBuildingFloors(buildingId));
					if(excludeIds != null) {
						floorsIds.removeAll(excludeIds);
					}
				}
				
				assetCategoryIds = AssetsAPI.getAssetCategoryIds(floorsIds);
				spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(floorsIds);
			}
			else if(assignmentType.equals(PMAssignmentType.SPACE_CATEGORY)) {
				
				if(parentAssignmentType == null) {
					List<Long> spaceIds = new ArrayList<>();
					
					if(includeIds != null) {
						spaceIds = includeIds;
					}
					else {
						spaceIds = getIdsFromSpaceContextList(SpaceAPI.getSpaceListOfCategory(buildingId, spaceCategoryId));
						if(excludeIds != null) {
							spaceIds.removeAll(excludeIds);
						}
					}
					
					assetCategoryIds = AssetsAPI.getAssetCategoryIds(spaceIds);
					spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(spaceIds);
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
					
					assetCategoryIds = AssetsAPI.getAssetCategoryIds(spaceIds);
					spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(spaceIds);
				}
			}
			else if(assignmentType.equals(PMAssignmentType.ASSET_CATEGORY)) {
				
				assetCategoryIds = AssetsAPI.getSubCategoryIds(assetCategoryId);
			}
		}
		else if(buildingId > 0) {															// only building selected case
			List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(buildingId);
			if(floors != null && !floors.isEmpty()) {
				hasFloor = true;
			}
			assetCategoryIds = AssetsAPI.getAssetCategoryIds(buildingId);
			spaceCategoryIds = SpaceAPI.getSpaceCategoryIds(buildingId);
		}
		return SUCCESS;
	}
	
	 private List<Long> getIdsFromSpaceContextList(List<SpaceContext> spaceListOfCategory) {
		 List<Long> ids = null;
	    	if(spaceListOfCategory != null && !spaceListOfCategory.isEmpty()) {
	    		ids = new ArrayList<>();
	    		for(ModuleBaseWithCustomFields context :spaceListOfCategory) {
	    			ids.add(context.getId());
	    		}
	    	}
	    	return ids;
	}

	public List<Long> getIdsFromContextList(List<BaseSpaceContext> contexts) {
	    	
	    	List<Long> ids = null;
	    	if(contexts != null && !contexts.isEmpty()) {
	    		ids = new ArrayList<>();
	    		for(ModuleBaseWithCustomFields context :contexts) {
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
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, sectionTemplates);
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
 		
		Chain updatePM = FacilioChainFactory.getUpdatePreventiveMaintenanceChain();
		updatePM.execute(context);

		return SUCCESS;
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

		Chain updatePM = FacilioChainFactory.getUpdatePreventiveMaintenanceJobChain();
		updatePM.execute(context);

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String preventiveMaintenanceSummary() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id.get(0));

		Chain pmSummary = FacilioChainFactory.getPreventiveMaintenanceSummaryChain();
		pmSummary.execute(context);

		setPreventivemaintenance(
				(PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setTaskList((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setListOfTasks((List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST));
		setSectionTemplates((List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));
		setReminders((List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS));

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

		Chain deletePM = FacilioChainFactory.getDeletePreventiveMaintenanceChain();
		deletePM.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);

		return SUCCESS;
	}

	public String changePreventiveMaintenanceStatus() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);

		Chain addTemplate = FacilioChainFactory.getChangePreventiveMaintenanceStatusChain();
		addTemplate.execute(context);

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

	public String getUpcomingPreventiveMaintenance() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME, getStartTime());
		// context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME,
		// startTime + (7*24*60*60));
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, getEndTime());

		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

		Chain getPmchain = FacilioChainFactory.getGetUpcomingPreventiveMaintenanceListChain();
		getPmchain.execute(context);

		setPmMap((Map<Long, PreventiveMaintenance>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		setPmJobs((List<PMJobsContext>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_JOBS_LIST));
		setPmTriggerMap((Map<Long, PMTriggerContext>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST));
		setPmResourcesMap((Map<Long, ResourceContext>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES));

		return SUCCESS;
	}

	private List<Map<String, Object>> pmJobList;

	public List<Map<String, Object>> getPmJobList() {
		return pmJobList;
	}

	public void setPmJobList(List<Map<String, Object>> pmJobList) {
		this.pmJobList = pmJobList;
	}

	public String getPMJobs() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME, getStartTime());
		// context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME,
		// startTime + (7*24*60*60));
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, getEndTime());

		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

		Chain getPmchain = FacilioChainFactory.getGetPMJobListChain();
		getPmchain.execute(context);

		setPmMap((Map<Long, PreventiveMaintenance>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		setPmJobList((List<Map<String, Object>>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_JOBS_LIST));
		setPmTriggerMap((Map<Long, PMTriggerContext>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST));
		setPmResourcesMap((Map<Long, ResourceContext>) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES));

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

		Chain getPmchain = FacilioChainFactory.getGetPreventiveMaintenanceListChain();
		getPmchain.execute(context);
		if (getCount() != null) {
//			setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
			setWoCount((long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT));
			System.out.println("data" + getWoCount());
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
	
	private List<List<Long>> actualTimings;
	
	public List<List<Long>> getActualTimings() {
		return this.actualTimings;
	}
	
	public void setActualTimings(List<List<Long>> actualTimings) {
		this.actualTimings = actualTimings;
	}

	public String assignWorkOrder() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ASSIGN_TICKET);
		return updateWorkOrder(context);
	}

	public String closeWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CLOSE_WORK_ORDER);
		context.put(FacilioConstants.ContextNames.ACTUAL_TIMINGS, actualTimings);

		workorder = new WorkOrderContext();
		workorder.setStatus(TicketAPI.getStatus("Closed")); // We shouldn't
															// allow close to be
															// edited
		if (actualWorkDuration != -1) {
			workorder.setActualWorkDuration(actualWorkDuration);
		}

		return updateWorkOrder(context);
	}

	public String resolveWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.SOLVE_WORK_ORDER);
		context.put(FacilioConstants.ContextNames.ACTUAL_TIMINGS, actualTimings);

		workorder = new WorkOrderContext();
		workorder.setStatus(TicketAPI.getStatus("Resolved")); // We shouldn't
																// allow resolve
																// to be edited
		if (actualWorkDuration != -1) {
			workorder.setActualWorkDuration(actualWorkDuration);
		}

		return updateWorkOrder(context);
	}

	public String deleteWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

		Chain deleteWorkOrder = FacilioChainFactory.getDeleteWorkOrderChain();
		deleteWorkOrder.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);

		return SUCCESS;
	}

	public String updateWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
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

	private void setUpdateWorkorderContext(FacilioContext context) throws Exception {
		ActivityType activityType = ActivityType.EDIT;
		if (workorder.getStatus() != null) {
			ActivityType type = TicketAPI.getActivityTypeForTicketStatus(workorder.getStatus().getId());
			if (type != null) {
				activityType = type;
			}
		}
		else if(!context.containsKey(FacilioConstants.ContextNames.ACTIVITY_TYPE) && ((workorder.getAssignedTo() != null && workorder.getAssignedTo().getId() > 0) || (workorder.getAssignmentGroup() != null && workorder.getAssignmentGroup().getId() > 0)) ) {
			activityType = ActivityType.ASSIGN_TICKET;
		}
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, activityType);
		context.put(FacilioConstants.ContextNames.COMMENT, comment);
	}

	private String updateWorkOrder(FacilioContext context) throws Exception {

		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

		Chain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
		updateWorkOrder.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);

		return SUCCESS;
	}

	public String viewWorkOrder() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getWorkOrderId());

		Chain getWorkOrderChain = ReadOnlyChainFactory.getWorkOrderDetailsChain();
		getWorkOrderChain.execute(context);

		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));

		return SUCCESS;
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
	private long woCount;

	public long getWoCount() {
		return woCount;
	}

	public void setWoCount(long woCount) {
		this.woCount = woCount;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String workOrderCount () throws Exception {
		System.out.println("View Name :  clount " + getViewName());
		return workOrderList();	
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

	private List<File> attachedFiles;
	private List<String> attachedFilesFileName;
	private List<String> attachedFilesContentType;
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
	
	private String taskSectionString;
	
	public String getTaskSectionString() {
		return taskSectionString;
	}

	public void setTaskSectionString(String taskSectionString) {
		this.taskSectionString = taskSectionString;
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
	
	public List<TaskSectionTemplate> setTaskSectioncontext(String taskSectionstring) throws Exception {
		
		JSONParser parser = new JSONParser();
		JSONArray obj = (JSONArray) parser.parse(taskSectionstring);
		
		List<TaskSectionTemplate> taskSectionContextList = FieldUtil.getAsBeanListFromJsonArray(obj, TaskSectionTemplate.class);
		setSectionTemplates(taskSectionContextList);
		
		tasks = new HashMap<>();
		for(TaskSectionTemplate taskSectionContext :taskSectionContextList) {
			tasks.put(taskSectionContext.getName(), taskSectionContext.getTasks());
		}
		sectionTemplates = taskSectionContextList;
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

	private String preventiveMaintenanceString;
	
	public String getPreventiveMaintenanceString() {
		return preventiveMaintenanceString;
	}
	public void setPreventiveMaintenanceString(String preventiveMaintenanceString) {
		this.preventiveMaintenanceString = preventiveMaintenanceString;
	}
	
	public void setPreventiveMaintenancecontex(String preventiveMaintenanceString) {
		this.preventivemaintenance = convertPM(preventiveMaintenanceString);
	}
	public PreventiveMaintenance convertPM(String preventiveMaintenanceString)
	{
		PreventiveMaintenance pm = null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(preventiveMaintenanceString);
			pm = FieldUtil.getAsBeanFromJson(obj, PreventiveMaintenance.class);

		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return pm;
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
			log.info("Exception occurred ", e);
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
	
	public void setRemindercontex(String reminder_string) {
		this.reminders = convertReminder(reminder_string);
	}
	public List<PMReminder> convertReminder(String reminder_string)
	{
		List<PMReminder> re = null;
		JSONParser parser = new JSONParser();
		try {
			JSONArray obj = (JSONArray) parser.parse(reminder_string);
			re = FieldUtil.getAsBeanListFromJsonArray(obj, PMReminder.class);

		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return re;
	}
	
	private String workOrderString;
	public String getWorkOrdertString() {
		return workOrderString;
	}
	public void setWorkOrderString(String workOrderString) {
		this.workOrderString = workOrderString;
	}
	
	public void setWorkordercontex(String workorder_string) throws Exception {
		this.workorder = convert(workorder_string);
	}
	public WorkOrderContext convert(String workOrderStr) throws Exception
	{
		WorkOrderContext wo = null;
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(workOrderStr);
		wo = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
		return wo;
	}
	

	@SuppressWarnings("unchecked")
	public String workOrderList() throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		if (isApproval()) {
			setViewName("approval_" + getViewName());
		}
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.WO_DUE_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WO_DUE_ENDTIME, getEndTime());
		if (getCount() != null) {	// only count
			context.put(FacilioConstants.ContextNames.WO_LIST_COUNT, getCount());
		}
		if(getShowViewsCount()) {
			context.put(FacilioConstants.ContextNames.WO_VIEW_COUNT, true);
		}
		if( getSubView() != null) {
			context.put(FacilioConstants.ContextNames.SUB_VIEW, getSubView());
		}
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		context.put(FacilioConstants.ContextNames.CRITERIA_IDS, getCriteriaIds());

		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "workorder.subject,workorder.description");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}

		JSONObject sorting = new JSONObject();
		if (getOrderBy() != null) {
			sorting.put("orderBy", getOrderBy());
			sorting.put("orderType", getOrderType());
		} else {
			sorting.put("orderBy", "createdTime");
			sorting.put("orderType", "desc");
		}
		context.put(FacilioConstants.ContextNames.SORTING, sorting);

		JSONObject pagination = new JSONObject();
		pagination.put("page", getPage());
		pagination.put("perPage", getPerPage());
		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		
		Chain workOrderListChain = ReadOnlyChainFactory.getWorkOrderListChain();
		workOrderListChain.execute(context);

		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		if (getCount() != null) {
//			setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
			setWoCount((long) context.get(FacilioConstants.ContextNames.WORK_ORDER_COUNT));
			System.out.println("data" + getWoCount() + getViewName());
		}
		else {
			if(getShowViewsCount()) {
				setSubViewsCount((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.SUB_VIEW_COUNT));
				setSubView((String) context.get(FacilioConstants.ContextNames.SUB_VIEW));
			}
			setWorkOrders((List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
		}
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if (cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}

		return SUCCESS;
	}

	private Map<Long, List<TaskContext>> taskList;

	public Map<Long, List<TaskContext>> getTaskList() {
		return taskList;
	}

	public void setTaskList(Map<Long, List<TaskContext>> taskList) {
		this.taskList = taskList;
	}
	
	private List<TaskContext> listOfTasks;
	public List<TaskContext> getListOfTasks() {
		return listOfTasks;
	}
	public void setListOfTasks(List<TaskContext> taskTemplates) {
		this.listOfTasks = taskTemplates;
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

	public String getActivitiesList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET_ID, workOrderId);

		Chain activitiesChain = FacilioChainFactory.getTicketActivitiesChain();
		activitiesChain.execute(context);

		setActivities((List<TicketActivity>) context.get(FacilioConstants.TicketActivity.TICKET_ACTIVITIES));

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

		Chain getWorkOrderChain = FacilioChainFactory.getWorkOrderDataChain();
		getWorkOrderChain.execute(context);

		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);

		if (workorder != null) {
			long duration = TicketAPI.getEstimatedWorkDuration(workorder, System.currentTimeMillis());
			setEstimatedDuration(duration);
		}

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
		setResult(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		return SUCCESS;
	}
	
	public String v2addWorkOrder() throws Exception {
		addWorkOrder();
		viewWorkOrder();
		setResult(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		return SUCCESS;
	}
	
	public String v2updateWorkOrder() throws Exception {
		updateWorkOrder();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	public String v2assignWorkOrder() throws Exception {
		assignWorkOrder();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	public String v2workOrderList() throws Exception {
		workOrderList();
		setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		if (getSubView() != null) {
			setResult(FacilioConstants.ContextNames.SUB_VIEW, subView);
		}
		if (getShowViewsCount()) {
			setResult(FacilioConstants.ContextNames.SUB_VIEW_COUNT, subViewsCount);
		}
		setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		return SUCCESS;
	}
	
	public String addPortalOrders() throws Exception {
		workorder.setSourceType(SourceType.SERVICE_PORTAL_REQUEST);
		workorder.setSendForApproval(true);
		return v2addWorkOrder();
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
			
			Chain offlineSync = FacilioChainFactory.addOfflineSyncErrorChain();
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

}
