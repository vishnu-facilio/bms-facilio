package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.WorkOrderFeatureSettingType;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * AddTasksRelatedPermissionCheckCommand
 * - This commands fetches workorderFeaturesSettings for the workorder's moduleState.
 * - Considering the following params in the same order wise, we calculate if specific action (Execute Task,
 * Manage Task) is allowed or not,
 * - isPrerequisiteCompleted
 * - FacilioStatus.recordLocked
 * - WorkOrder Feature Setting
 * - User permission
 * <p>
 * We set calculated actionPermissible status into WorkOrderSettings object's parameter.
 */
public class AddTasksRelatedPermissionCheckCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3WorkOrderContext workOrderContext = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
        FacilioStatus currentModuleState = (FacilioStatus) context.get(FacilioConstants.ContextNames.CURRENT_MODULE_STATE);
        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingsContexts = (List<V3WorkOrderFeatureSettingsContext>)
                context.getOrDefault(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, new ArrayList<>());
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        Boolean isLockedState = currentModuleState.isRecordLocked();
        Boolean hasExecuteTaskPermission = checkIfUserHasPermission(moduleName,"UPDATE_WORKORDER_TASK");
        Boolean hasManageTaskPermission = checkIfUserHasPermission(moduleName,"UPDATE_TASK");


        Boolean isTaskExecutionFeatureSettingEnabledForCurrentState = false;
        Boolean isWorkOrderPlanningFeatureSettingEnabledForCurrentState = false;

        for (V3WorkOrderFeatureSettingsContext featureSettingsContext : workOrderFeatureSettingsContexts) {
            if (featureSettingsContext.getSettingType() == WorkOrderFeatureSettingType.EXECUTION.getVal()) {
                isTaskExecutionFeatureSettingEnabledForCurrentState = true;
            }
            if (featureSettingsContext.getSettingType() == WorkOrderFeatureSettingType.PLANNING.getVal()) {
                isWorkOrderPlanningFeatureSettingEnabledForCurrentState = true;
            }
        }

        boolean canExecuteTask = WorkOrderFeatureHelper.isPrerequisiteCompleted(workOrderContext)
                                && !isLockedState
                                && isTaskExecutionFeatureSettingEnabledForCurrentState
                                && hasExecuteTaskPermission;
        boolean canManageTask = WorkOrderFeatureHelper.isPrerequisiteCompleted(workOrderContext)
                                && !isLockedState
                                && isWorkOrderPlanningFeatureSettingEnabledForCurrentState
                                && hasManageTaskPermission;

        //overriding, for main app
        if (AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)){
            canExecuteTask = true;
            canManageTask = true;
            hasExecuteTaskPermission = true;
            hasManageTaskPermission = true;
        }

        WorkOrderSettings workOrderSettings = (WorkOrderSettings) context.get(FacilioConstants.ContextNames.WORK_ORDER_SETTINGS);
        WorkOrderFeatureSetting executeTaskFeatureSetting = new WorkOrderFeatureSetting();
        executeTaskFeatureSetting.setAllowed(canExecuteTask);

        WorkOrderFeatureSetting manageTaskFeatureSetting = new WorkOrderFeatureSetting();
        manageTaskFeatureSetting.setAllowed(canManageTask);

        workOrderSettings.setExecuteTask(executeTaskFeatureSetting);
        workOrderSettings.setManageTask(manageTaskFeatureSetting);

        // storing in context for accessing in other commands
        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_HAS_EXECUTE_TASK_PERMISSION, hasExecuteTaskPermission);
        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_HAS_MANAGE_TASK_PERMISSION, hasManageTaskPermission);
        context.put(FacilioConstants.ContextNames.IS_LOCKED_STATE, isLockedState);

        return false;
    }

    /**
     * Helper function to check if the user has permission to do @param action in module @param moduleName.
     * @param moduleName
     * @param action
     * @return
     * @throws Exception
     */
    private Boolean checkIfUserHasPermission(String moduleName, String action) throws Exception {
        if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null && AccountUtil.getCurrentUser().getRole().isPrevileged()){
            return true;
        }

        if (AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)){
            if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null){
                Role role = AccountUtil.getCurrentUser().getRole();
                boolean hasActionPermission = PermissionUtil.currentUserHasPermission(moduleName,action,role);
                return hasActionPermission;
            }
        }else {
            WebTabContext tab = AccountUtil.getCurrentTab();
            return V3PermissionUtil.currentUserHasPermission(tab.getId(), action);
        }

        return false;
    }
}
