package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.V3WatchListContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckForWatchListRecordCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list)) {
            for(V3VisitorLoggingContext vLog : list) {
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
