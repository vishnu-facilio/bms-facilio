package com.facilio.bmsconsoleV3.signup.moduleconfig;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class QuotationModule extends SignUpData {
    @Override
    public void addData() throws Exception {


        addQuoteLineItemFields();
        addQuateFields();
    }
    private Boolean addQuoteLineItemFields() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule quoteLineItemModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE_LINE_ITEMS);


        List<FacilioField> fields = new ArrayList<>();


        NumberField markup = (NumberField) FieldFactory.getDefaultField("markup", "Markup", "MARKUP", FieldType.DECIMAL);
        markup.setModule(quoteLineItemModule);

        modBean.addField(markup);

        return true;
    }
    private Boolean addQuateFields() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule quoteModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE);

        LookupField parentQuotationField = FieldFactory.getDefaultField("parentQuotationId", "Parent quotation Id", "PARENT_QUOTEID", FieldType.LOOKUP);
        parentQuotationField.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.QUOTE));
        parentQuotationField.setModule(quoteModule);


        NumberField markup = (NumberField) FieldFactory.getDefaultField("markup", "Markup", "MARKUP", FieldType.DECIMAL);
        markup.setModule(quoteModule);

        NumberField totalmarkup = (NumberField) FieldFactory.getDefaultField("totalMarkup", "Total Markup", "TOTAL_MARKUP_AMOUNT", FieldType.DECIMAL);
        totalmarkup.setModule(quoteModule);

        LookupField vendor = FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR_ID", FieldType.LOOKUP);
        vendor.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.VENDORS));
        vendor.setModule(quoteModule);


        BooleanField showMarkupValue = (BooleanField) FieldFactory.getDefaultField("showMarkupValue", "Show Markup value", "SHOW_MARKUP_VALUE", FieldType.BOOLEAN);
        showMarkupValue.setModule(quoteModule);

        BooleanField isGlobalMarkup = (BooleanField) FieldFactory.getDefaultField("isGlobalMarkup", "IS Global Markup", "IS_GLOBAL_MARKUP", FieldType.BOOLEAN);
        isGlobalMarkup.setModule(quoteModule);




        modBean.addField(parentQuotationField);
        modBean.addField(markup);
        modBean.addField(totalmarkup);
        modBean.addField(vendor);
        modBean.addField(showMarkupValue);
        modBean.addField(isGlobalMarkup);


        return true;
    }
}


