package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class RollupTenantSpacesWhileUpdatingCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayListMultimap<String, Long> recordList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        for (String key :recordList.keySet()) {
            if (key.equals(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)) {
                List<Long> unitSpaceIdsList = recordList.get(key);
                if (CollectionUtils.isNotEmpty(unitSpaceIdsList)) {
                    List<TenantUnitSpaceContext> records = SpaceAPI.getTenantUnitSpaceList(unitSpaceIdsList);
                    if (CollectionUtils.isNotEmpty(records)) {

                        //removing tenant from unit
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
                        List<FacilioField> fields = modBean.getAllFields(module.getName());

                        FacilioModule unitModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
                        List<FacilioField> unitFields = modBean.getAllFields(module.getName());

                        for (TenantUnitSpaceContext record : records) {
                            if (record.getTenant() != null ) {
                                if (!record.isOccupied()) {
                                    GenericUpdateRecordBuilder unitBuilder = new GenericUpdateRecordBuilder()
                                            .table(unitModule.getTableName())
                                            .fields(unitFields)
                                            .andCondition(CriteriaAPI.getIdCondition(record.getId(), module));

                                    Map<String, Object> valueMap = new HashMap<>();
                                    TenantContext tenant = new TenantContext();
                                    tenant.setId(-99);
                                    valueMap.put("tenant", FieldUtil.getAsProperties(tenant));
                                    unitBuilder.update(valueMap);
                                }
                            }
                            markUnitTenantsExpired(record);
                        }
                    }
                }
            }

        }
        return false;
    }

    private void markUnitTenantsExpired(TenantUnitSpaceContext ts) throws Exception {
        List<TenantContext> tenantList = TenantsAPI.getAllTenantsForSpace(Collections.singletonList(ts.getId()));
        List<Long> tenantIds = new ArrayList<>();
        boolean toAddTenantSpace = true;
        if(CollectionUtils.isNotEmpty(tenantList)) {
            for (TenantContext tenant : tenantList) {
                List<TenantUnitSpaceContext> tsList = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
                if (CollectionUtils.isEmpty(tsList)) {
                    tenantIds.add(tenant.getId());
                }
                if (ts.getTenant() != null && ts.getTenant().getId() == tenant.getId()) {
                    toAddTenantSpace = false;
                }
            }
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if(CollectionUtils.isNotEmpty(tenantIds)) {
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(fields);
            FacilioField updatedFields = tenantFieldMap.get("moduleState");
            UpdateRecordBuilder<TenantContext> updateBuilder = new UpdateRecordBuilder<TenantContext>()
                    .module(module)
                    .fields(Collections.singletonList(updatedFields))
                    .andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(tenantIds, ","), NumberOperators.EQUALS));

            Map<String, Object> value = new HashMap<>();
            value.put("moduleState", FieldUtil.getAsProperties(TicketAPI.getStatus(module, "Expired")));
            updateBuilder.updateViaMap(value);
        }

        //add tenant space if needed for current tenant

        if (toAddTenantSpace && ts.isOccupied() && ts.getTenant() != null) {
            FacilioModule tenantSpaceModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
            List<FacilioField> tenantSpaceFields = modBean.getAllFields(tenantSpaceModule.getName());

            List<TenantSpaceContext> tenantSpaces = new ArrayList<>();
            TenantSpaceContext tenantSpace = new TenantSpaceContext();
            tenantSpace.setTenant(ts.getTenant());
            tenantSpace.setSpace(ts);
            tenantSpaces.add(tenantSpace);
            InsertRecordBuilder<TenantSpaceContext> insertBuilder = new InsertRecordBuilder<TenantSpaceContext>()
                    .table(tenantSpaceModule.getTableName()).module(tenantSpaceModule).fields(tenantSpaceFields)
                    .addRecords(tenantSpaces);
            insertBuilder.save();
        }
    }
}
