package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WhitelistSystemFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> whitelistedFields = Arrays.asList("sysCreatedTime");

        final String moduleName = (String) context.get("moduleName");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName).
                stream().
                filter(f -> f.isRequired() && !whitelistedFields.contains(f.getName())).
                collect(Collectors.toList());

        context.put(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS, fields);
        return false;
    }
}
