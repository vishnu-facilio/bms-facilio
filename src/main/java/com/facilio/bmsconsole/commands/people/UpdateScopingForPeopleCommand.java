package com.facilio.bmsconsole.commands.people;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.FacilioException;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateScopingForPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PeopleContext> pplList = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if(CollectionUtils.isEmpty(pplList)) {
            pplList = new ArrayList<>();
            PeopleContext people = (PeopleContext) context.get(FacilioConstants.ContextNames.RECORD);
            if(people != null) {
                pplList.add(people);
            }
        }
        if(CollectionUtils.isNotEmpty(pplList)) {
            for(PeopleContext ppl : pplList) {
                Map<String, Long> scopingsMap = ppl.getScopingsMap();
                if(scopingsMap != null) {
                    for(String linkname : scopingsMap.keySet()) {
                        long appId = ApplicationApi.getApplicationIdForLinkName(linkname);
                        AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
                        if (appDomain != null) {
                            User user = AccountUtil.getUserBean().getUser(ppl.getEmail(), appDomain.getIdentifier());
                            if (user != null) {
                                Long scopingId = scopingsMap.get(linkname);
                                FacilioUtil.throwIllegalArgumentException(scopingId == null || scopingId <= -1, "Invalid scoping id");
                                ApplicationApi.updateScopingForUser(scopingId,appId, user.getOuid());
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
