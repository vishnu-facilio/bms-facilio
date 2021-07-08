package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.stream.Collectors;

public class AddFieldsAndLookupFieldsInContextCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> fields = modBean.getAllFields(moduleName);
        context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);

        List<FacilioField> supplementRecords = fields.stream().filter(field ->
                field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP)
                .collect(Collectors.toList());
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementRecords);

        return false;
    }
}
