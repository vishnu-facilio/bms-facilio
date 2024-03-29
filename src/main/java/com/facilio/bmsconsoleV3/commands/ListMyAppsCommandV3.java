package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMyAppsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long ouId = (Long) context.get(FacilioConstants.ContextNames.ORG_USER_ID);

        String appDomain = (String)context.get(FacilioConstants.ContextNames.APP_DOMAIN);
        AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);

        List<OrgUserApp> apps = ApplicationApi.getApplicationListForUser(ouId,appDomainObj);

        context.put(FacilioConstants.ContextNames.MY_APPS, apps);


        return false;
    }

}

