package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class TaxFillDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        List list = CommandUtil.getModuleDataList(context, moduleName);
        QuotationAPI.fillTaxDetails(list);
        return false;
    }
}
