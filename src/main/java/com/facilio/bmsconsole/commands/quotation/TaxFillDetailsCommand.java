package com.facilio.bmsconsole.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class TaxFillDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        List list = CommandUtil.getModuleDataList(context, moduleName);
        QuotationAPI.fillTaxDetails(list);
        return false;
    }
}
