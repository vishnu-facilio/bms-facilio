package com.facilio.bmsconsoleV3.commands.people;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateScopingForPeopleCommandV3 extends FacilioCommand {
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
                } else {
                    Map<String, Long> scopingsMap = ppl.getScopingsMap();
                    if (scopingsMap != null) {
                        for (String linkname : scopingsMap.keySet()) {
                            long appId = ApplicationApi.getApplicationIdForLinkName(linkname);
                            AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
                            if (appDomain != null) {
                                User user = AccountUtil.getUserBean().getUser(ppl.getEmail(), appDomain.getIdentifier());
                                if (user != null) {
                                    Long scopingId = scopingsMap.get(linkname);
                                    FacilioUtil.throwIllegalArgumentException(scopingId == null || scopingId <= -1, "Invalid scoping id");
                                    ApplicationApi.updateScopingForUser(scopingId, appId, user.getOuid());
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
