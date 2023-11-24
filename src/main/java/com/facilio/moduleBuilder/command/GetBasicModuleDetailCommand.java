package com.facilio.moduleBuilder.command;

import com.beust.ah.A;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

public class GetBasicModuleDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Module name can't be null to fetch module details");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid moduleName");

        Map<String, Object> moduleDetails = ResponseFormatUtil.formatModuleBasedOnResponseFields(module,
                Arrays.asList("moduleId","orgId", "name","displayName", "type","extendModule","extendedModuleIds","custom"), true);

        FacilioField mainField = modBean.getPrimaryField(moduleName);

        moduleDetails.put("mainField", ResponseFormatUtil.formatFieldsBasedOnResponseFields(mainField,
                Arrays.asList("fieldId","dataType","displayName","displayType","displayTypeInt", "accessType"),false));


        context.put(FacilioConstants.ContextNames.MODULE, moduleDetails);

        return false;
    }
}
