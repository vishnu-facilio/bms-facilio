package com.facilio.bmsconsoleV3.commands.ocr;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsoleV3.context.ocr.BillTemplateContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateVariableContext;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;

public class TemplateSupplementsCommand extends FacilioCommand {      		// need to impl

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);
        if(CollectionUtils.isNotEmpty(recordIds)){
            long id = recordIds.get(0);
            BillTemplateContext billTemplate = (BillTemplateContext) CommandUtil.getModuleData(context, FacilioConstants.Ocr.BILL_TEMPLATE, id);

            if(billTemplate != null) {
                List<OCRTemplateVariableContext> templateVariables = OcrUtil.getTemplateVariables(id);
                billTemplate.setVariables(templateVariables);
//                List<LineItemRuleContext> lineItemRules = OcrUtil.getLineItemRules(id);

                //billTemplate.setFieldRules(templateFieldRules);
                //billTemplate.setLineItemRules(lineItemRules);
            }
        }
        return false;
    }
}
