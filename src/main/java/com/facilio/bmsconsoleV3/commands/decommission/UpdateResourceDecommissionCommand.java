package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.DecommissionContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.bmsconsoleV3.util.DecommissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateResourceDecommissionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        DecommissionContext decommissionContext = (DecommissionContext) context.get(FacilioConstants.ContextNames.DECOMMISSION);
        List<V3ResourceContext> dependentResourcesData = (List<V3ResourceContext>) context.get(FacilioConstants.ContextNames.DEPENDENT_RESOURCES_DATA);

        List<Long> dependentResourceIds = dependentResourcesData.stream().map(V3ResourceContext::getId).collect(Collectors.toList());
        String currentResourceModuleName = decommissionContext.getModuleName();
        Boolean decommission = decommissionContext.getDecommission();
        FacilioUtil.throwIllegalArgumentException(decommissionContext.getResourceId() == null || currentResourceModuleName == null,"Resource Id or ModuleName cannot be null");

        //ERROR MODULES CHECK FOR SCRIPT LEVEL DECOMMISSIONING

        if(currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)){
            long orgId = AccountUtil.getCurrentOrg().getId();
            Long emailCount = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> DecommissionUtil.getDependentResourceEmailCount(Collections.singletonList(decommissionContext.getResourceId()),orgId));
            FacilioUtil.throwIllegalArgumentException(emailCount > 0,"Email is associated with the resource");
        }

        Map<String, Long> userList = AccessibleSpacesUtil.getAccessibleSpaceUserList(dependentResourceIds);
        int userCount = userList != null ?  userList.values().stream().mapToInt(i -> Math.toIntExact(i)).sum() : 0;
        FacilioUtil.throwIllegalArgumentException(userCount > 0 ,"User  is associated as Accessible space with the resource");

        Long tenantUnitCount = DecommissionUtil.getTenantUnitSpaceCount(dependentResourceIds);
        FacilioUtil.throwIllegalArgumentException(tenantUnitCount > 0 ,"Tenant Unit  is associated with the resource");

        //UPDATING RESOURCES

        FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
        List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getField(FacilioConstants.ContextNames.DECOMMISSION, "IS_DECOMMISSIONED", ModuleFactory.getResourceModule(), FieldType.BOOLEAN));
            fields.add(FieldFactory.getField(FacilioConstants.ContextNames.COMMISSION_TIME, "DECOMMISSIONED_TIME", ModuleFactory.getResourceModule(), FieldType.NUMBER));

        UpdateRecordBuilder<V3ResourceContext> builder = new UpdateRecordBuilder<V3ResourceContext>()
                .module(resourceModule)
                .fields(fields)
                .table(resourceModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("Resources.ID","id", StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));

        Map<String, Object> props = new HashMap<>();
        props.put(FacilioConstants.ContextNames.DECOMMISSION, decommission);
        long decommissionTime = System.currentTimeMillis();
        props.put(FacilioConstants.ContextNames.COMMISSION_TIME, decommissionTime);
        builder.update(FieldUtil.getAsBeanFromMap(props, V3ResourceContext.class));

        context.put(FacilioConstants.ContextNames.COMMISSION_TIME,decommissionTime);

        return false;
    }
}
