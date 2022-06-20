package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetLabourListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        List<LookupField>fetchLookup = Arrays.asList((LookupField) fieldsAsMap.get("user"), (LookupField) fieldsAsMap.get("location"));
        context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,fetchLookup);
        return false;
    }
}
