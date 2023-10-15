package com.facilio.bmsconsoleV3.commands.employee;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadEmployeeLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> additionalLookups = new ArrayList<>();
        additionalLookups.add((LookupField) fieldsAsMap.get("sysCreatedBy"));
        additionalLookups.add((LookupField) fieldsAsMap.get("sysModifiedBy"));
        if(fieldsAsMap.get("territories")!=null){
            additionalLookups.add((MultiLookupField) fieldsAsMap.get("territories"));
        }
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionalLookups);
        return false;
    }
}
