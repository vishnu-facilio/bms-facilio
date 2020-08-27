package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UserNotificationAction extends V3Action {

    private static Logger LOGGER = LogManager.getLogger(UserNotificationAction.class.getName());

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    Long userId ;


    public String updateUserNotificationSeen () throws Exception {
        try {
            if (userId == null) {
                userId = AccountUtil.getCurrentUser().getId();
            }
            FacilioChain c = TransactionChainFactoryV3.getUserNotificationSeenUpdateChain();
            c.getContext().put(FacilioConstants.ContextNames.USER, userId);
            c.execute();
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error" + ex);
            return "failure";
        }
        return SUCCESS;
    }

    public String updateUserNotificationRead () throws Exception {
        try {
            if (userId == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "User Id cannot be null");
            }
            FacilioChain c = TransactionChainFactoryV3.getMarkAllAsReadUserNotification();
            c.getContext().put(FacilioConstants.ContextNames.USER, userId);
            c.execute();
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error" + ex);
            return "failure";
        }
        return SUCCESS;
    }
}
