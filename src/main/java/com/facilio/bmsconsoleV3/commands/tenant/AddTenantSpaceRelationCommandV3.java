package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddTenantSpaceRelationCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);

        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        Boolean spacesUpdate = false;

        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("spacesUpdate")) {
            spacesUpdate = true;
        }

        if (CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
                if ((spacesUpdate != null && spacesUpdate) || (tenant.getSpaces() != null && tenant.getSpaces().size() > 0)) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
                    List<FacilioField> fields = modBean.getAllFields(module.getName());
                    List<V3TenantSpaceContext> tenantSpaces = new ArrayList<>();
                    for (BaseSpaceContext space : tenant.getSpaces()) {
                        V3TenantSpaceContext tenantSpace = new V3TenantSpaceContext();
                        tenantSpace.setTenant(tenant);
                        tenantSpace.setSpace(space);
                        tenantSpaces.add(tenantSpace);
                    }
                    InsertRecordBuilder<V3TenantSpaceContext> insertBuilder = new InsertRecordBuilder<V3TenantSpaceContext>()
                            .table(module.getTableName()).module(module).fields(fields)
                            .addRecords(tenantSpaces);
                    insertBuilder.save();
                }
            }
        }
        return false;
    }
}
