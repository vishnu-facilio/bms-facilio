package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;

/**
 * WorkOrderFeatureHelper is the helper class that has the common functions that are used in WorkOrder Feature Settings implementation.
 */
public class WorkOrderFeatureHelper {

    /**
     * Helper function to check if Prerequisites are completed if the workorder has any Prerequisites.
     * The logic is extracted from client as it was to move the logic to backend.
     * @param workOrderContext
     * @return
     */
    public static boolean isPrerequisiteCompleted(V3WorkOrderContext workOrderContext) {
        return (
                workOrderContext.getPrerequisiteEnabled() == null ||
                !workOrderContext.getPrerequisiteEnabled() ||
                        workOrderContext.getAllowNegativePreRequisite() == null ||
                        workOrderContext.getAllowNegativePreRequisite() == -1 ||
                        workOrderContext.getPreRequestStatus() == null ||
                        workOrderContext.getPreRequestStatus() == -1 ||
                        (workOrderContext.getPreRequestStatusEnum() != null &&
                                workOrderContext.getAllowNegativePreRequisite() == 2 &&
                                (workOrderContext.getPreRequestStatus() == 2 ||
                                        workOrderContext.getPreRequestStatus() == 3)) ||
                        (workOrderContext.getAllowNegativePreRequisite() == 3 &&
                                workOrderContext.getPreRequestStatus() == 3)
        );
    }
}
