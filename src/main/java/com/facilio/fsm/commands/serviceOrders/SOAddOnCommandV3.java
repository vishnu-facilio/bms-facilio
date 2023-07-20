package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.TerritoryContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SOAddOnCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);

        List<ServiceOrderContext> dataList = (List<ServiceOrderContext>) recordMap.get(moduleName);
        List<ServiceOrderContext> serviceOrdersNew = new ArrayList<>();
        for(ServiceOrderContext order : dataList) {
            V3SiteContext site = order.getSite();
            if(site != null){
                SiteContext siteData = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SITE,site.getId());
                if(siteData.getTerritory() != null){
                    order.setTerritory(siteData.getTerritory());
                }
                serviceOrdersNew.add(order);
            } else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Site Cannot be Null for Service Order");
            }
        }
        recordMap.put(moduleName,serviceOrdersNew);
        context.put(Constants.RECORD_MAP,recordMap);
        return false;
    }
}
