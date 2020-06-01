package com.facilio.bmsconsole.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.quotation.TaxContext;
import com.facilio.bmsconsole.context.quotation.TaxGroupContext;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertTaxGroupsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<TaxContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TaxContext tax : list) {
                if (tax.getType() == TaxContext.Type.GROUP.getIndex()) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule taxGroupsModule = modBean.getModule(FacilioConstants.ContextNames.TAX_GROUPS);
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TAX_GROUPS);
                    Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
                    DeleteRecordBuilder<TaxGroupContext> deleteBuilder = new DeleteRecordBuilder<TaxGroupContext>()
                            .module(taxGroupsModule)
                            .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("parentTax"), String.valueOf(tax.getId()), NumberOperators.EQUALS));
                    deleteBuilder.delete();
                    List<TaxGroupContext> taxGroups = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(tax.getChildTaxes())) {
                        for (TaxContext childTax : tax.getChildTaxes()) {
                            TaxGroupContext taxGroup = new TaxGroupContext();
                            taxGroup.setParentTax(tax);
                            taxGroup.setChildTax(childTax);
                            taxGroups.add(taxGroup);
                        }
                    }
                    RecordAPI.addRecord(false, taxGroups, taxGroupsModule, fields);
                } else {
                    Long oldTaxId = (Long) context.get(FacilioConstants.ContextNames.OLD_TAX_ID);
                    if (oldTaxId != null && oldTaxId > 0) {
                        QuotationAPI.updateTaxGroupsOnChildUpdate(tax, oldTaxId);
                    }
                }
            }
        }
        return false;
    }
}
