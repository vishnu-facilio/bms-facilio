package com.facilio.bmsconsoleV3.commands.ocr;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.ParsedBillContext;
import com.facilio.bmsconsoleV3.context.ocr.PreUtilityIntegerationLineItems;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class GetPreUtilityLineItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        List<ParsedBillContext> parsedBillContexts = recordMap.get(moduleName);

        if(parsedBillContexts != null && CollectionUtils.isNotEmpty(parsedBillContexts)) {
            for(ParsedBillContext parsedBillContext : parsedBillContexts)
            {
                if (parsedBillContext != null && parsedBillContext.getId() > 0) {

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String lineItemModuleName = FacilioConstants.Ocr.PRE_UTILITY_LINE_ITEMS;
                    List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
                    Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

                    SelectRecordsBuilder<PreUtilityIntegerationLineItems> builder = new SelectRecordsBuilder<PreUtilityIntegerationLineItems>()
                            .moduleName(lineItemModuleName)
                            .select(fields)
                            .beanClass(PreUtilityIntegerationLineItems.class)
                            .andCondition(CriteriaAPI.getCondition("PARSED_BILL", "parsedBill", String.valueOf(parsedBillContext.getId()), NumberOperators.EQUALS));

                    List<PreUtilityIntegerationLineItems> list = builder.get();
                    parsedBillContext.setUtilityIntegrationLineItemContexts(list);
                }
            }
        }
        return false;
    }
}
