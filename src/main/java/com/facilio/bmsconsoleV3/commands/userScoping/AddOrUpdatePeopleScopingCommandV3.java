package com.facilio.bmsconsoleV3.commands.userScoping;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdatePeopleScopingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> pplList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(pplList)) {
            for(V3PeopleContext ppl : pplList) {
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                    Long scopingId = ppl.getScopingId();
                    userScopeBean.updatePeopleScoping(ppl.getId(),scopingId);
                }
            }
        }
        return false;
    }
}
