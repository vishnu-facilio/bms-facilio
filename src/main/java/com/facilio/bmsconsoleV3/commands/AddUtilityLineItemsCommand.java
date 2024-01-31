package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationLineItemContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddUtilityLineItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<UtilityIntegrationBillContext> utilityBills = recordMap.get(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        UtilityIntegrationBillContext utilityBill = null;

        if(CollectionUtils.isNotEmpty(utilityBills)){
            utilityBill = utilityBills.get(0);
        }
        if(utilityBill != null){
            List<UtilityIntegrationLineItemContext> lineItems= utilityBill.getUtilityIntegrationLineItemContexts();
            if(CollectionUtils.isNotEmpty(lineItems)){
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule lineItemModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS);

                if(CollectionUtils.isNotEmpty(lineItems)){
                    for(UtilityIntegrationLineItemContext lineItem:lineItems){
                        lineItem.setUtilityIntegrationBill(new UtilityIntegrationBillContext(utilityBill.getId()));
                    }
                    V3Util.createRecordList(lineItemModule, FieldUtil.getAsMapList(lineItems,UtilityIntegrationLineItemContext.class),null,null);
                }
            }
        }

        return false;
    }
}

