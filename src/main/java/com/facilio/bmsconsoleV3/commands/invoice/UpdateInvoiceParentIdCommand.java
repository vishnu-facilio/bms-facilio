package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateInvoiceParentIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InvoiceContextV3> list = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField parentField = modBean.getField("parentId",FacilioConstants.ContextNames.INVOICE);

        if(CollectionUtils.isNotEmpty(list)) {
            for(InvoiceContextV3 invoice : list) {
                if (invoice.getParentId() == null) {
                    invoice.setParentId(invoice.getId());
                    V3RecordAPI.updateRecord(invoice, module, Collections.singletonList(parentField));
                }
            }
        }
        return false;
    }
}
