package com.facilio.bmsconsoleV3.commands.tenantunit;

import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckForTenantBeforeDeletionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> tenantUnitIds = (List<Long>) context.get(Constants.RECORD_ID_LIST);
        String tenantUnitModuleName = FacilioConstants.ContextNames.TENANT_UNIT_SPACE;
        List<FacilioField> fieldTenantUnits = FieldFactory.getTenantUnitFields();
        if (CollectionUtils.isNotEmpty(tenantUnitIds)) {
            for(long id : tenantUnitIds){
                SelectRecordsBuilder<V3TenantUnitSpaceContext> builder = new SelectRecordsBuilder<V3TenantUnitSpaceContext>()
                        .moduleName(tenantUnitModuleName)
                        .select(fieldTenantUnits)
                        .beanClass(V3TenantUnitSpaceContext.class)
                        .table("Tenant_Unit_Space")
                        .innerJoin("Tenants")
                        .on("Tenants.ID = Tenant_Unit_Space.TENANT_ID")
                        .andCondition(CriteriaAPI.getCondition("Tenant_Unit_Space.ID", "id", String.valueOf(id), NumberOperators.EQUALS));
                List<V3TenantUnitSpaceContext> props = builder.get();
                if(props.size() > 0){
                    throw new IllegalArgumentException("The Parent Module Tenant has data associated");
                }
            }
        }
        return false;
    }
}
