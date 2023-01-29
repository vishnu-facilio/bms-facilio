package com.facilio.bmsconsoleV3.commands.userScoping;

import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Log4j
public class GetUserScopingConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
            Long scopingId = (Long) context.get(FacilioConstants.ContextNames.SCOPING_ID);
            List<ScopingConfigContext> userScopingConfigList = userScopeBean.getUserScopingConfig(scopingId);
            context.put(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST, userScopingConfigList);
        } catch (Exception e) {
            LOGGER.error("Error occurred while getting user scoping config for module " + e.getMessage());
            if (e instanceof InvocationTargetException) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, ((InvocationTargetException) e).getTargetException().getMessage());
            }
        }
        return false;
    }
}
