package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Map;

public abstract class ProcessSubFormLineItemsCommand extends FacilioCommand {

    protected FacilioField getLookupField(Long fieldId, Map<Long, FacilioField> fieldMap, String mainModuleName) throws Exception {
        if (fieldId != null && fieldId > 0) {
            return fieldMap.get(fieldId);
        }

        Collection<FacilioField> fields = fieldMap.values();
        if (CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                if (field instanceof LookupField) {
                    LookupField lookupField = (LookupField) field;
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule parentModule = modBean.getModule(mainModuleName);
                    FacilioModule extendModule = parentModule;
                    while (extendModule != null) {
                        if (lookupField.getLookupModule().getName().equals(extendModule.getName())) {
                            return lookupField;
                        }
                        extendModule = extendModule.getExtendModule();
                    }
                }
            }
        }
        return null;
    }
}
