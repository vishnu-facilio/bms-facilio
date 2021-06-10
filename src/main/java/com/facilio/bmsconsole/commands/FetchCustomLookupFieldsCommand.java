package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FetchCustomLookupFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        List<LookupField> supplementRecords = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (CollectionUtils.isEmpty(fields)) {
            fields = modBean.getAllFields(moduleName);
            context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
        }

        if(CollectionUtils.isEmpty(supplementRecords)) {
            supplementRecords = new ArrayList<>();
        }
        if(CollectionUtils.isNotEmpty(fields)) {
            for(FacilioField f : fields) {
                if (!f.isDefault()) {
                  if (f instanceof LookupField) {
                        supplementRecords.add((LookupField) f);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(supplementRecords)) {
                context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, supplementRecords);
            }
        }
        return false;
    }

}
