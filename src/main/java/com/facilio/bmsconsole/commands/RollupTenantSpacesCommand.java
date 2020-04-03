package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class RollupTenantSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayListMultimap<String, Long> recordList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        for (String key :recordList.keySet()) {
            if (key.equals(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)) {
                List<Long> unitSpaceIdsList = recordList.get(key);
                if (CollectionUtils.isNotEmpty(unitSpaceIdsList)) {
                    List<TenantUnitSpaceContext> records = SpaceAPI.getTenantUnitSpaceList(unitSpaceIdsList);
                    if (CollectionUtils.isNotEmpty(records)) {
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
                        List<FacilioField> fields = modBean.getAllFields(module.getName());
                        List<TenantSpaceContext> tenantSpaces = new ArrayList<>();
                        for (TenantUnitSpaceContext record: records) {
                            if (record.getTenant() != null) {
                                TenantSpaceContext tenantSpace = new TenantSpaceContext();
                                tenantSpace.setTenant(record.getTenant());
                                tenantSpace.setSpace(record);
                                tenantSpaces.add(tenantSpace);
                            }
                        }
                        InsertRecordBuilder<TenantSpaceContext> insertBuilder = new InsertRecordBuilder<TenantSpaceContext>()
                                .table(module.getTableName()).module(module).fields(fields)
                                .addRecords(tenantSpaces);
                        insertBuilder.save();
                    }
                }
            }
        }
        return false;
    }
}
