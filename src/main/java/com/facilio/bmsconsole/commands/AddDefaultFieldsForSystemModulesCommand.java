package com.facilio.bmsconsole.commands;


import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddDefaultFieldsForSystemModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // For now the behaviour is we'll add system fields for all modules in the list. If few modules doesn't need system fields, put them in a separate list and call the chain
        boolean addSysFields = (boolean) context.getOrDefault(FacilioConstants.Module.SYS_FIELDS_NEEDED, false);
        boolean ignoreModifiedSysFields = (boolean) context.getOrDefault(FacilioConstants.Module.IGNORE_MODIFIED_SYS_FIELDS, false);
        if (addSysFields) {
            List<FacilioModule> modules = CommonCommandUtil.getModules(context);
            if (CollectionUtils.isNotEmpty(modules)) {
                for (FacilioModule module : modules) {
                    List<FacilioField> fields = module.getFields() == null ? new ArrayList<>() : new ArrayList<>(module.getFields());
                    fields.addAll(FieldFactory.getSystemPointFields(module, !ignoreModifiedSysFields));
                    module.setFields(fields);
                }
            }
        }
        return false;
    }
}
