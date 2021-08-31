package com.facilio.bmsconsole.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveAssociatedTenantUnitCommand extends FacilioCommand {

    public boolean executeCommand(Context context) throws Exception {


        List<Long> tenantId = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);

        List<FacilioField> updatedFields = new ArrayList<FacilioField>();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        FacilioField tenantField = fieldMap.get("tenant");
        FacilioField isVacantField = fieldMap.get("isOccupied");

        updatedFields.add(tenantField);
        updatedFields.add(isVacantField);

        UpdateRecordBuilder<TenantUnitSpaceContext> updateBuilder = new UpdateRecordBuilder<TenantUnitSpaceContext>()
                .module(module)
                .fields(updatedFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("tenant"), tenantId, NumberOperators.EQUALS));

        Map<String, Object> value = new HashMap<>();
        TenantContext tenant = new TenantContext();
        tenant.setId(-99);
        value.put("tenant", FieldUtil.getAsProperties(tenant));
        value.put("isOccupied", false);

        updateBuilder.updateViaMap(value);


        return false;
    }

}



