package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMyAppsForUserCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long ouId = (Long) context.get(FacilioConstants.ContextNames.ORG_USER_ID);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);

        AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);

        List<OrgUserApp> apps = ApplicationApi.getApplicationListForUser(ouId,appDomain);

        context.put(FacilioConstants.ContextNames.MY_APPS, apps);

        return false;
    }
    }


