package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckForWatchListRecordCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list)) {
            for(V3VisitorLoggingContext vLog : list) {
                VisitorContext visitor = vLog.getVisitor();
                if(visitor != null) {
                    if(visitor.getId() > 0) {
                        visitor = VisitorManagementAPI.getVisitor(visitor.getId(), null);
                    }
                    WatchListContext watchListRecord = VisitorManagementAPI.getBlockedWatchListRecordForPhoneNumber(visitor.getPhone(), visitor.getEmail());
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
