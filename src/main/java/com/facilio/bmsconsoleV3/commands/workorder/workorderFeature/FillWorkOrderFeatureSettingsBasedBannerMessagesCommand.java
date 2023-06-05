package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.WorkOrderFeatureSettingType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * FillWorkOrderFeatureSettingsBasedBannerMessagesCommand
 * - Constructs the banner messages that has to be shown up in each tab [TASKS, PLANS, ACTUALS] of workorder.
 * - Messages is constructed based on the WorkOrder Feature Setting & Permission & other params to checked with planned workorders
 */
public class FillWorkOrderFeatureSettingsBasedBannerMessagesCommand extends FacilioCommand {


    Boolean isTaskExecutionFeatureSettingEnabledForCurrentState = false;
    Boolean isWorkOrderPlanningFeatureSettingEnabledForCurrentState = false;
    Boolean isWorkOrderActualsFeatureSettingEnabledForCurrentState = false;
    Boolean hasExecuteTaskPermission = null;
    Boolean hasManageTaskPermission = null;
    Boolean isLockedState = false;
    V3WorkOrderContext workOrderContext;

    @Override
    public boolean executeCommand(Context context) throws Exception {

        WorkOrderSettings workOrderSettings = (WorkOrderSettings) context.get(FacilioConstants.ContextNames.WORK_ORDER_SETTINGS);
        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingsContexts = (List<V3WorkOrderFeatureSettingsContext>)
                context.getOrDefault(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, new ArrayList<>());
        workOrderContext = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);

        hasExecuteTaskPermission = (Boolean) context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_HAS_EXECUTE_TASK_PERMISSION);
        hasManageTaskPermission = (Boolean) context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_HAS_MANAGE_TASK_PERMISSION);
        isLockedState = (Boolean) context.get(FacilioConstants.ContextNames.IS_LOCKED_STATE);

        for (V3WorkOrderFeatureSettingsContext featureSettingsContext : workOrderFeatureSettingsContexts) {
            if (featureSettingsContext.getSettingType() == WorkOrderFeatureSettingType.EXECUTION.getVal()) {
                isTaskExecutionFeatureSettingEnabledForCurrentState = true;
            }
            if (featureSettingsContext.getSettingType() == WorkOrderFeatureSettingType.PLANNING.getVal()) {
                isWorkOrderPlanningFeatureSettingEnabledForCurrentState = true;
            }
            if (featureSettingsContext.getSettingType() == WorkOrderFeatureSettingType.ACTUALS.getVal()) {
                isWorkOrderActualsFeatureSettingEnabledForCurrentState = true;
            }
        }

        workOrderSettings.getExecuteTask().setReason(getTasksTabBannerMessage());
        workOrderSettings.getManageTask().setReason(getTasksAddTaskMessage());
        workOrderSettings.getInventoryPlaning().setReason(getInventoryPlansTabBannerMessage());
        workOrderSettings.getInventoryActuals().setReason(getInventoryActualsTabBannerMessage());

        //overriding, for main app
        if (AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)){
            isTaskExecutionFeatureSettingEnabledForCurrentState = true;
            isWorkOrderPlanningFeatureSettingEnabledForCurrentState = true;
            isWorkOrderActualsFeatureSettingEnabledForCurrentState = true;

            workOrderSettings.getExecuteTask().setReason("");
            workOrderSettings.getManageTask().setReason("");
            workOrderSettings.getInventoryPlaning().setReason("");
            workOrderSettings.getInventoryActuals().setReason("");
        }
        return false;
    }

    /**
     * Helper function to construct message to be shown in Tasks Tab when Add Task permission/setting is disabled.
     * @return
     */
    private String getTasksAddTaskMessage() {
        StringBuilder message = new StringBuilder();

        if(!WorkOrderFeatureHelper.isPrerequisiteCompleted(this.workOrderContext)) {
            message = new StringBuilder("Complete Prerequisites to manage task");
        } else if(isLockedState){
            message = new StringBuilder("Task cannot be added in locked state");
        } else if(!hasManageTaskPermission && !isWorkOrderPlanningFeatureSettingEnabledForCurrentState){
            message = new StringBuilder("You don't have permission to add new Task!");
        } else if(!isWorkOrderPlanningFeatureSettingEnabledForCurrentState){
            message = new StringBuilder("Task cannot be managed in this state");
        } else if (!hasManageTaskPermission) {
            message = new StringBuilder("You don't have permission to add new Task!");
        }
        return message.toString();
    }

    /**
     * Helper function to construct banner message for Tasks Tab
     * @return
     */
    private String getTasksTabBannerMessage() {
        StringBuilder bannerMessage = new StringBuilder();

        if(!WorkOrderFeatureHelper.isPrerequisiteCompleted(this.workOrderContext)) {
            bannerMessage = new StringBuilder("Complete Prerequisites to initiate task execution");
        } else if(isLockedState){
            bannerMessage = new StringBuilder("Tasks cannot be managed or executed in locked state");
        } else if (!hasExecuteTaskPermission && !hasManageTaskPermission) {
            bannerMessage = new StringBuilder("You don't have permission to manage or execute tasks");
        }else if (!isTaskExecutionFeatureSettingEnabledForCurrentState && !isWorkOrderPlanningFeatureSettingEnabledForCurrentState) {
            bannerMessage = new StringBuilder("Task cannot be managed or executed in this state");
        }else if (!isTaskExecutionFeatureSettingEnabledForCurrentState) {
            bannerMessage = new StringBuilder("Task cannot be executed in this state");
        }else if(!hasExecuteTaskPermission){
            bannerMessage = new StringBuilder("You don't have permission to execute tasks");
        }

        return bannerMessage.toString();
    }

    /**
     * Helper function to construct banner message for Plans Tab
     * @return
     */
    private String getInventoryPlansTabBannerMessage() {
        StringBuilder bannerMessage = new StringBuilder();
        if(isLockedState){
            bannerMessage = new StringBuilder("Actions in this tab cannot be performed in the locked state");
        } else if (!isWorkOrderPlanningFeatureSettingEnabledForCurrentState) {
            bannerMessage = new StringBuilder("Actions in this tab cannot be performed in the current state");
        }
        return bannerMessage.toString();
    }

    /**
     * Helper function to construct banner message for Actuals Tab
     * @return
     */
    private String getInventoryActualsTabBannerMessage() {
        StringBuilder bannerMessage = new StringBuilder();
        if(isLockedState){
            bannerMessage = new StringBuilder("Actions in this tab cannot be performed in the locked state");
        }else if (!isWorkOrderActualsFeatureSettingEnabledForCurrentState) {
            bannerMessage = new StringBuilder("Actions in this tab cannot be performed in the current state");
        }
        return bannerMessage.toString();
    }
}
