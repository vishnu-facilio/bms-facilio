package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadMovesLookupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField employeeField = (LookupField) fieldsAsMap.get("employee");
        additionaLookups.add(employeeField);
        LookupField departmentField = (LookupField) fieldsAsMap.get("department");
        additionaLookups.add(departmentField);
        LookupField fromField = (LookupField) fieldsAsMap.get("from");
        additionaLookups.add(fromField);
        LookupField toField = (LookupField) fieldsAsMap.get("to");
        additionaLookups.add(toField);
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(moduleName));
        additionaLookups.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(moduleName));
        additionaLookups.add(sysModifiedBy);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}
