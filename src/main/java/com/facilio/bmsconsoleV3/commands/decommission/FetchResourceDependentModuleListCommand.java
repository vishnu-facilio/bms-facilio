package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.DecommissionContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.bmsconsoleV3.util.DecommissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class FetchResourceDependentModuleListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DecommissionContext decommissionContext = (DecommissionContext) context.get(FacilioConstants.ContextNames.DECOMMISSION);
        Long currentResourceId = decommissionContext.getResourceId();
        String currentResourceModuleName = decommissionContext.getModuleName();
        FacilioUtil.throwIllegalArgumentException(currentResourceId == null || currentResourceModuleName == null,"Resource Id or ModuleName cannot be null");
        
        List<V3ResourceContext> dependentResourcesData = (List<V3ResourceContext>) context.get(FacilioConstants.ContextNames.DEPENDENT_RESOURCES_DATA);

        List<Long> dependentResourceIds = dependentResourcesData.stream().map(V3ResourceContext::getId).collect(Collectors.toList());
        
        FacilioUtil.throwIllegalArgumentException(dependentResourceIds.isEmpty() || dependentResourcesData.isEmpty(),"No Resource Found");

        JSONObject resourceDependentModuleList = new JSONObject();

        //CUSTOMIZATION DEPENDENCIES
        if(currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {

            FacilioModule supportEmailModule = ModuleFactory.getSupportEmailsModule();
            long orgId = AccountUtil.getCurrentOrg().getId();
            Long emailCount = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> DecommissionUtil.getDependentResourceEmailCount(Collections.singletonList(currentResourceId),orgId));
            if( emailCount > 0){
                    resourceDependentModuleList.put(FacilioConstants.ContextNames.EMAIL_SETTING,DecommissionUtil.constructResultJSON(supportEmailModule,emailCount,true));
            }
        }

        //USERS AS ACCESSIBLE RESOURCE
        FacilioModule userModule = ModuleFactory.getOrgUserModule();
        Map<String, Long> userList = AccessibleSpacesUtil.getAccessibleSpaceUserList(dependentResourceIds);
        int usersCount = userList != null ?  userList.values().stream().mapToInt(i -> Math.toIntExact(i)).sum() : 0;
        JSONObject userObject = new JSONObject();
        if(usersCount > 0 )
        {
            userObject = DecommissionUtil.constructResultJSON(userModule , (long) usersCount, true);
            userObject.put(FacilioConstants.ContextNames.USERS, userList);
        }
        resourceDependentModuleList.put(FacilioConstants.ContextNames.APPLICATION,userObject);

        //MODULES CONCERNED
        resourceDependentModuleList.put(FacilioConstants.ContextNames.MODULE_LIST,DecommissionUtil.fetchDependentModulesDataForResource(currentResourceId,dependentResourceIds,currentResourceModuleName));

        //PORTFOLIO DEPENDENCIES
        resourceDependentModuleList.put(FacilioConstants.ContextNames.RESOURCE,DecommissionUtil.getPortfolioDependency(dependentResourcesData,currentResourceModuleName));

        Map<String ,List<Long>> errorModuleData = new HashMap<>();
        errorModuleData.put(FacilioConstants.ContextNames.TENANT_ID , DecommissionUtil.getTenantsIdsOccupiedUnits(dependentResourceIds));
        if(currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)){
            errorModuleData.put(FacilioConstants.ContextNames.CLIENT_IDS ,DecommissionUtil.getClientIdOfDependentUnits(currentResourceId));
        }
        //TENANT DETAILS
        resourceDependentModuleList.put(FacilioConstants.ContextNames.ERROR_MODULE_DATA,errorModuleData);

        context.put(FacilioConstants.ContextNames.RESOURCE_LIST, resourceDependentModuleList);

        return false;
    }
}
