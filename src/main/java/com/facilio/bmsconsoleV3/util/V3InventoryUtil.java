package com.facilio.bmsconsoleV3.util;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;

public class V3InventoryUtil {
    public static Double getServiceCost(V3ServiceContext service, Double duration, Double quantity) {
        Double costOccured = null;
        if(service.getBuyingPrice()!=null && service.getBuyingPrice() > 0) {
            if (service.getPaymentTypeEnum() == V3ServiceContext.PaymentType.FIXED) {
                costOccured = service.getBuyingPrice() * quantity;
            }
            else {
                costOccured = service.getBuyingPrice() * duration * quantity;
            }
        }
        return costOccured;
    }

    public static Double getWorkorderActualsDuration(Long issueTime, Long returnTime, V3WorkOrderContext workorder) {
        Double duration = 1.0;
        if (issueTime!=null && returnTime!=null && issueTime >= 0 && returnTime >= 0) {
            duration = getEstimatedWorkDuration(issueTime, returnTime);
        } else {
            if(workorder.getActualWorkDuration()!=null && workorder.getActualWorkDuration() > 0) {
                double hours = (((double)workorder.getActualWorkDuration()) / (60 * 60));
                duration = Math.round(hours*100.0)/100.0;
            }
        }
        return duration;
    }

    public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
        double duration = -1;
        if (issueTime != -1 && returnTime != -1) {
            duration = returnTime - issueTime;
        }

        double hours = ((duration / (1000 * 60 * 60)));
        return Math.round(hours*100.0)/100.0;
    }

    public static Long getReturnTimeFromDurationAndIssueTime(Double duration, Long issueTime) {
        Long returnTime = null;
        if (issueTime!=null && issueTime >= 0) {
            returnTime = (long) (issueTime + (duration * (60*60*1000)));
        }
        return returnTime;
    }
}
