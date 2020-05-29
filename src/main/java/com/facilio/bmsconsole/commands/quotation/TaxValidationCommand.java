package com.facilio.bmsconsole.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.quotation.TaxContext;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaxValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<TaxContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TaxContext tax : list) {
                if (tax != null) {
                    if (StringUtils.isEmpty(tax.getName())) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tax Name is mandatory");
                    }
                    if (tax.getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
                        if (tax.getRate() == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tax Rate is mandatory");
                        }
                    } else if (tax.getType() == TaxContext.Type.GROUP.getIndex()) {
                        if (CollectionUtils.isNotEmpty(tax.getChildTaxes()) && tax.getChildTaxes().size() > 1) {
                            List<Long> taxIds = tax.getChildTaxes().stream().map(TaxContext::getId).collect(Collectors.toList());
                            List<TaxContext> childTaxes = QuotationAPI.getTaxesForIdList(taxIds);
                            double parentTaxRate = 0;
                            for (TaxContext childTax : childTaxes) {
                                parentTaxRate += childTax.getRate();
                            }
                            tax.setRate(parentTaxRate);
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Choose At least 2 Taxes for Tax Group");
                        }
                    }
                    if (tax.getId() > 0) {
                        QuotationAPI.setTaxAsInactive(tax);
                        tax.setId(-1);
                    }
                }
            }
        }


        return false;
    }
}
