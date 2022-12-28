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
        Double duration = null;
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
        return duration / 1000;
    }

    public static Long getReturnTimeFromDurationAndIssueTime(Double duration, Long issueTime) {
        Long returnTime = null;
        if (issueTime!=null && issueTime >= 0) {
            returnTime = (long) (issueTime + (duration * 1000));
        }
        return returnTime;
    }

    public static Long getIssueTimeFromDurationAndReturnTime(Double duration, Long returnTime) {
        Long issueTime = null;
        if(returnTime != null && returnTime >= 0) {
            issueTime = (long) (returnTime - (duration * 1000));
        }
        return issueTime;
    }
}
