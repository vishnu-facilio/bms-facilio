package com.facilio.bmsconsoleV3.commands.userScoping;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

;import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Log4j
public class AddOrUpdateUserScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
            ScopingContext userScoping = (ScopingContext) context.get(FacilioConstants.ContextNames.RECORD);
            long userScopingId = userScoping.getId();
            V3Util.throwRestException(StringUtils.isEmpty(userScoping.getScopeName()), ErrorCode.VALIDATION_ERROR, "Scope name is mandatory");

            if (userScopingId > 0) {
                userScopeBean.updateUserScoping(userScoping);
            } else {
                List<ScopingContext> userScopingList = userScopeBean.getUserScopingList("", -1, -1);
                int existingRecords = CollectionUtils.size(userScopingList);
                V3Util.throwRestException(existingRecords >= 10, ErrorCode.VALIDATION_ERROR, "Maximum number of user scoping reached.");
                userScopeBean.addUserScoping(userScoping);
                context.put(FacilioConstants.ContextNames.ID,userScoping.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while adding User Scoping " + e.getMessage());
            if (e instanceof RESTException) {
                throw e;
            }
            if (e instanceof InvocationTargetException) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, ((InvocationTargetException) e).getTargetException().getMessage());
            }
        }

        return false;
    }
}
