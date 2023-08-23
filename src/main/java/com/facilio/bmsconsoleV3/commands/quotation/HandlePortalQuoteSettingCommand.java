package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;


public class HandlePortalQuoteSettingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(QuotationFillDetailsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {


        ApplicationContext app = AccountUtil.getCurrentApp();


       if(app != null && (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP))) {
           QuotationSettingContext quotationSetting = QuotationAPI.fetchQuotationSetting();

           if(quotationSetting != null) {
               context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, quotationSetting);

           }

           String moduleName = Constants.getModuleName(context);
           Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
           List<QuotationContext> list = recordMap.get(moduleName);
           if (CollectionUtils.isNotEmpty(list)) {
               for (QuotationContext quotation : list) {
                   if(quotationSetting != null) {
                       quotation.setShowMarkupValue(QuotationAPI.showMarkupValue(context));
                   }
               }
           }

       }

        return false;
    }
}
