package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class ValidateSubModuleFormFormCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String lookupFieldName = (String) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField lookupField = modBean.getField(lookupFieldName,moduleName);

        FacilioUtil.throwIllegalArgumentException(lookupField instanceof LookupField && ((LookupField) lookupField).getLookupModule()!= null, "invalid lookup field");

        String subModuleName = ((LookupField) lookupField).getLookupModule().getName();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, subModuleName);

        return false;
    }
}
