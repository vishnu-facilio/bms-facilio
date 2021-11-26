package com.facilio.v3.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class AddMultiSelectFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if(CollectionUtils.isNotEmpty(fields)) {
            List<SupplementRecord> supplements = fields.stream().filter(f -> f.getDataTypeEnum().isRelRecordField())
                                                                .map(f -> (SupplementRecord) f)
                                                                .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(supplements)) {
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
            }
        }
        return false;
    }
}
