package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.workorder.workorderFeature.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.pmv1ToPmv2Migration.PMv1TasksToJobPlanMigration;
import com.facilio.v3.V3Action;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.facilio.bmsconsoleV3.context.workorder.setup.FetchWorkOrderFeatureSettingsSetUpCommand;
@Getter
@Setter
public class WorkorderAction extends V3Action {
    private static Logger LOGGER = LogManager.getLogger(WorkorderAction.class.getName());

    private List<Long> ids;
    private V3WorkOrderContext workOrder;
    private long startTime = -1;
    public long getStartTime(){
        return startTime;
    }
    public void setStartTime(long startTime){
        this.startTime = startTime;
    }
    private long endTime = -1;
    public long getEndTime(){
        return endTime;
    }
    public void setEndTime(long endTime){
        this.endTime = endTime;
    }
    private String currentView;
    public String getCurrentView(){
        return currentView;
    }
    public void setCurrentView(String currentView){
        this.currentView = currentView;
    }
    private String filters;

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getFilters() {
        return this.filters;
    }


    public String close() throws Exception {

        if (CollectionUtils.isEmpty(ids)) {
            LOGGER.error("ids cannot be null");
            return ERROR;
        }

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        CommonCommandUtil.addEventType(EventType.CLOSE_WORK_ORDER, context);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

        FacilioStatus closedState = TicketAPI.getStatus("Closed");
        Map<String, Object> mapping = new HashMap<>();

        // TODO::VR updating status to be removed when status is removed from view criteria
        mapping.put("status", closedState);


        JSONObject map = new JSONObject();
        map.put(FacilioConstants.ContextNames.CLOSE_ALL_FROM_BULK_ACTION,true);

        V3Util.updateBulkRecords("workorder", mapping, ids, map,null,false);

        return SUCCESS;
    }

    public String assign() throws Exception {

        if (CollectionUtils.isEmpty(ids)) {
            LOGGER.error("ids cannot be null");
            return ERROR;
        }

        if (workOrder.getAssignmentGroup() == null && workOrder.getAssignedTo() == null) {
            return SUCCESS;
        }
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSIGN_TICKET);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
        context.put(FacilioConstants.ContextNames.REQUESTER, workOrder.getRequester());

        Map<String, Object> mapping = new HashMap<>();
        if (workOrder.getAssignmentGroup() != null) {
            mapping.put("assignmentGroup", workOrder.getAssignmentGroup());
        }
        if (workOrder.getAssignedTo() != null) {
            mapping.put("assignedTo", workOrder.getAssignedTo());
        }
        FacilioContext ctx = V3Util.updateBulkRecords("workorder", mapping, ids, false);

        return SUCCESS;
    }

    public String getPpmJobs() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_START_TIME, getStartTime());
        context.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_END_TIME, getEndTime());
        context.put(FacilioConstants.ContextNames.CURRENT_CALENDAR_VIEW, getCurrentView());
        if (getFilters() != null) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }

        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        FacilioChain getPmchain = FacilioChainFactory.getPpmJobListChain();
        getPmchain.execute(context);
        Map<String ,Object> result = new HashMap<>();
        result.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_JOB_LIST,context.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_JOB_LIST));
        result.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_MAP,context.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_MAP));
        result.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_TRIGGER_LIST,context.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_TRIGGER_LIST));
        result.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_RESOURCE_LIST,context.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_RESOURCE_LIST));
        setData("result",result);


        return SUCCESS;
    }

    // WorkOrder Feature Settings
    /**
     * featureSettings() => /api/v3/workorders/features/{record_id}
     * - This API returns
     * {
     *     "data": {
     *             "workOrderSettings": {
     *                  "executeTask":{
     * 		                "allowed": false,
     * 		                "reason":"You don't have permission to manage or execute tasks"
     *                  },
     *                  "inventoryActuals": {
     * 		                "allowed": true,
     * 		                "reason":""
     *                  },
     *                  "inventoryPlaning": {
     * 		                "allowed": false,
     * 		                "reason":"Actions in this tab cannot be performed in the current state!"
     *                  },
     *                  "manageTask": {
     * 		                "allowed": true,
     * 		                "reason":""
     *                  }
     *              }
     *     }
     * }
     *
     *  'executeTask': BOOLEAN value -> based on `canExecuteTask` at com/facilio/bmsconsoleV3/commands/workorder/workorderFeature/AddTasksRelatedPermissionCheckCommand.java:58
     *  'manageTask': BOOLEAN value -> based on `canManageTask` at com/facilio/bmsconsoleV3/commands/workorder/workorderFeature/AddTasksRelatedPermissionCheckCommand.java:62
     *  'inventoryPlaning': BOOLEAN value -> based on `canDoActionsOnPlans` at com/facilio/bmsconsoleV3/commands/workorder/workorderFeature/AddWorkOrderPlansPermissionCheckCommand.java:40
     *  'inventoryActuals': BOOLEAN value -> based on `canDoActionsOnActuals` at com/facilio/bmsconsoleV3/commands/workorder/workorderFeature/AddWorkOrderActualsPermissionCheckCommand.java:40
     */
    @Getter
    @Setter
    private Long recordId;
    public String featureSettings() throws Exception{
        FacilioChain chain = FacilioChain.getTransactionChain();

        chain.getContext().put("recordId", recordId);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.addCommand(new InitializeWorkOrderSettingObjectsCommand());
        chain.addCommand(new ValidateAndFetchRecordCommand());
        // check if atleast one workorder setting is configured.
        // fetch workorder settings for current module state
        chain.addCommand(new V3FetchWorkOrderFeatureSettingsCommand());
        // add node for task execution and management
        chain.addCommand(new AddTasksRelatedPermissionCheckCommand());
        chain.addCommand(new AddWorkOrderPlansPermissionCheckCommand());
        chain.addCommand(new AddWorkOrderActualsPermissionCheckCommand());
        chain.addCommand(new FillWorkOrderFeatureSettingsBasedBannerMessagesCommand());

        chain.execute();

        WorkOrderSettings workOrderSettings = (WorkOrderSettings) chain.getContext().get(FacilioConstants.ContextNames.WORK_ORDER_SETTINGS);
        setData("workOrderSettings", workOrderSettings);

//        HashMap<String, Boolean> featureSettingValueMap = (HashMap<String, Boolean>) chain.getContext().get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_VALUES_MAP);
//        setData("workOrderFeatureSettingValues", featureSettingValueMap);

        return SUCCESS;
    }

    /**
     * workOrderFeatureSettingsList() function fetches the WorkOrder Feature Settings List and sends in following format.
     * This API was requested for mobile offline support.
     * API: /api/v3/workorders/states/featureSettings
     * This API returns:
     *  "data": {
     *         "workOrderStatesFeatureSettings": {
     *             "16": [
     *                 {
     *                     "allowedTicketStatusId": 16,
     *                     "id": 2,
     *                     "isAllowed": true,
     *                     "lastModifiedTime": 1696417326124,
     *                     "orgId": 1,
     *                     "settingType": 1,
     *                     "settingTypeEnum": "PLANNING",
     *                 }
     *             ],
     *             }
     *         }
     * @return
     * @throws Exception
     */
    public String workOrderFeatureSettingsList() throws Exception {

        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.addCommand(new FetchWorkOrderFeatureSettingsSetUpCommand());
        chain.addCommand(new FormatWorkOrderFeatureSettingsBasedOnStates());
        chain.execute();
        Map<Long, ArrayList<V3WorkOrderFeatureSettingsContext>> workOrderStatesFeatureSettings = (Map<Long, ArrayList<V3WorkOrderFeatureSettingsContext>>) chain.getContext().get(FacilioConstants.ContextNames.WORK_ORDER_STATES_FEATURE_SETTINGS);
        if(workOrderStatesFeatureSettings == null){
            workOrderStatesFeatureSettings = new HashMap<>();
        }
        setData(FacilioConstants.ContextNames.WORK_ORDER_STATES_FEATURE_SETTINGS, workOrderStatesFeatureSettings);
        return  SUCCESS;
    }

    @Getter
    @Setter
    List<Long> pmV1Ids;
    public String tasksToJobPlan() throws Exception{
//        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
//        bean.migratePmv1TasksToJobPlan(pmV1Ids);
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.getContext().put("pmV1Ids", this.pmV1Ids);
        chain.addCommand(new PMv1TasksToJobPlanMigration(this.pmV1Ids));
        chain.execute();
        return SUCCESS;
    }
}
