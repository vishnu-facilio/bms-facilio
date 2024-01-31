package com.facilio.bmsconsoleV3.commands.ocr;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsoleV3.context.ocr.OcrFieldRuleLogContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddSupplementsForOcrRuleLog extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<OcrFieldRuleLogContext> ruleLogs = (List<OcrFieldRuleLogContext>) recordMap.get(FacilioConstants.Ocr.OCR_RULE_LOG);

        List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.Ocr.PARSED_BILL);
        List<FacilioField> listItemFields = Constants.getModBean().getAllFields(FacilioConstants.Ocr.PRE_UTILITY_LINE_ITEMS);

        for(OcrFieldRuleLogContext ruleLogContext: ruleLogs){
            List<FacilioField> fld = fields.stream().filter(field -> field.getFieldId() == ruleLogContext.getMapFieldId()).collect(Collectors.toList());

            if(CollectionUtils.isEmpty(fld)){
                fld = listItemFields.stream().filter(field -> field.getFieldId() == ruleLogContext.getMapFieldId()).collect(Collectors.toList());
            }

            if(CollectionUtils.isNotEmpty(fld)){
                //ruleLogContext.setMapFieldName(fld.get(0).getDisplayName());
            }
        }

        return false;
    }
}
