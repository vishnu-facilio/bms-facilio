package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdateQuotationParentIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        if(CollectionUtils.isNotEmpty(list)) {
            for(QuotationContext quotation : list) {
                if (quotation.getParentId() == null) {
                    quotation.setParentId(quotation.getLocalId());
                    V3RecordAPI.updateRecord(quotation, module, fields);
                }
            }
        }
        return false;
    }
}
