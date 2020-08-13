package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3WatchListContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.v3.context.Constants;

public class CheckForWatchListRecordBaseVisitCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BaseVisitContextV3> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list)) {
            for(BaseVisitContextV3 vLog : list) {
                V3VisitorContext visitor = vLog.getVisitor();
                if(visitor != null) {
                    if(visitor.getId() > 0) {
                        visitor = V3VisitorManagementAPI.getVisitor(visitor.getId(), null);
                    } 
                    
                    V3WatchListContext watchListRecord = V3VisitorManagementAPI.getBlockedWatchListRecordForPhoneNumber(visitor.getPhone(), visitor.getEmail());
                    if(watchListRecord != null) {
                        if(watchListRecord.isVip()) {
                            vLog.setIsVip(true);
                        }
                        else if(watchListRecord.isBlocked()) {
                            vLog.setIsBlocked(true);
                        }
                    }    			
                }
            }
        }
        return false;
    }
}

