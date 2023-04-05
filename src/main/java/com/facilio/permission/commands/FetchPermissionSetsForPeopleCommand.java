package com.facilio.permission.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionSetContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchPermissionSetsForPeopleCommand extends FacilioCommand {
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
            List<ApplicationContext> apps = ApplicationApi.getAllApplications();
            Map<Long, String> appLinkNames= new HashMap<>();
            if(CollectionUtils.isNotEmpty(apps)) {
                for(ApplicationContext app : apps) {
                    appLinkNames.put(app.getId(), app.getLinkName());
                }
            }
            PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
            for(PeopleContext ppl : pplList) {
                List<Long> permissionSetIds = permissionSetBean.getUserPermissionSetMapping(ppl.getId(),false).stream().map(PermissionSetContext::getId).collect(Collectors.toList());
                ppl.setPermissionSets(permissionSetIds);
            }
        }
        return false;
    }
}
