package com.facilio.v3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
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
            List<SupplementRecord> supplements = fields.stream().filter(f -> f.getDataTypeEnum().isMultiRecord())
                                                                .map(f -> (SupplementRecord) f)
                                                                .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(supplements)) {
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
            }
        }
        return false;
    }
}
