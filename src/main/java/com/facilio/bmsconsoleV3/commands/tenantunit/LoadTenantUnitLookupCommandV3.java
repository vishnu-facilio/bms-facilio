package com.facilio.bmsconsoleV3.commands.tenantunit;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadTenantUnitLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);


        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionalLookups = new ArrayList<>();
        additionalLookups.add((LookupField) fieldsAsMap.get("tenant"));
        additionalLookups.add((LookupField) fieldsAsMap.get("location"));
        additionalLookups.add((LookupField) fieldsAsMap.get("spaceCategory"));
        additionalLookups.add((LookupField) fieldsAsMap.get("site"));
        additionalLookups.add((LookupField) fieldsAsMap.get("building"));
        additionalLookups.add((LookupField) fieldsAsMap.get("floor"));
        additionalLookups.add((LookupField) fieldsAsMap.get("sysCreatedBy"));
        additionalLookups.add((LookupField) fieldsAsMap.get("sysModifiedBy"));

        for (FacilioField field : fields) {
            if (!field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
                additionalLookups.add((LookupField) field);
            }
        }
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionalLookups);
        return false;
    }
}
