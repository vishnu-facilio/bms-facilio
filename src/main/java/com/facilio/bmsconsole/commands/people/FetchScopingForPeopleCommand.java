package com.facilio.bmsconsole.commands.people;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchScopingForPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        List<PeopleContext> pplList = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if(CollectionUtils.isEmpty(pplList)) {
            pplList = new ArrayList<>();
            PeopleContext people = (PeopleContext) context.get(FacilioConstants.ContextNames.RECORD);
            if(people != null) {
                pplList.add(people);
            }
        }
        if(CollectionUtils.isNotEmpty(pplList)) {
            List<ApplicationContext> apps = ApplicationApi.getAllApplications();
            Map<Long, String> appLinkNames= new HashMap<>();
            if(CollectionUtils.isNotEmpty(apps)) {
                for(ApplicationContext app : apps) {
                    appLinkNames.put(app.getId(), app.getLinkName());
                }
            }
            for(PeopleContext ppl : pplList) {
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                    Long scopingId = userScopeBean.getPeopleScoping(ppl.getId());
                    ppl.setScopingId(scopingId);
                } else {
                    Map<String, Long> scopingAppForPeople = new HashMap<>();
                    List<Long> ouIds = PeopleAPI.getUserIdForPeople(ppl.getId());
                    if (CollectionUtils.isNotEmpty(ouIds)) {
                        for (Long ouId : ouIds) {
                            List<OrgUserApp> scopingsForUsers = ApplicationApi.getScopingsForUser(ouId, null);
                            if (CollectionUtils.isNotEmpty(scopingsForUsers)) {
                                for (OrgUserApp userScopingApp : scopingsForUsers) {
                                    scopingAppForPeople.put(appLinkNames.get(userScopingApp.getApplicationId()), userScopingApp.getScopingId());
                                }
                            }
                        }
                        ppl.setScopingsMap(scopingAppForPeople);
                    }
                }
            }
        }
        return false;
    }
}
