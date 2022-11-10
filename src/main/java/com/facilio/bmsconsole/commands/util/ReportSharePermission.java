package com.facilio.bmsconsole.commands.util;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import com.facilio.report.util.ReportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReportSharePermission extends FacilioCommand{


    public boolean executeCommand (Context context) throws Exception{
       Long parentId = (Long) context.get("reportId");
       Boolean isCreate = (Boolean) context.get("isCreate");
        Boolean isGet = (Boolean) context.get("isGet");
        if(parentId != null && isCreate!=null && isCreate) {
           ReportUtil.deleteReportShareRecords(parentId);
           ArrayList<SingleSharingContext> reportDetails = (ArrayList<SingleSharingContext>) context.get("reportShareInfo");
           for (SingleSharingContext sharingContext : reportDetails) {
               sharingContext.setParentId(parentId);
               ReportUtil.shareReport(sharingContext);
           }

       }
        else if(parentId !=null && isGet!=null && isGet){
            List<Map<String, Object>> reportShareDetails= ReportUtil.getReportShareDetails(parentId);
            context.put("reportShareDetails",reportShareDetails);
        }
        return false;
    }
}
