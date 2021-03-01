package com.facilio.bmsconsoleV3.commands.visitorlog;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddWatchListRecordCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<VisitorLogContextV3> visitorLogs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLogs)) {
            List<WatchListContext> watchList = new ArrayList<>();
            for (VisitorLogContextV3 vL : visitorLogs) {
                V3VisitorContext visitor = vL.getVisitor();
                if(visitor != null && (vL.getIsVip() || vL.getIsBlocked())) {
                WatchListContext existing = VisitorManagementAPI.getBlockedWatchListRecordForPhoneNumber(visitor.getPhone(), visitor.getEmail());
                if (existing == null) {
                    WatchListContext wL = new WatchListContext();
                    wL.setName(visitor.getName());
                    wL.setEmail(visitor.getEmail());
                    wL.setPhone(visitor.getPhone());
                    wL.setIsBlocked(vL.getIsBlocked());
                    wL.setIsVip(vL.getIsVip());
                    wL.setAvatar(visitor.getAvatar());
                    watchList.add(wL);
                }
            }
            if (CollectionUtils.isNotEmpty(watchList)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WATCHLIST);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                RecordAPI.addRecord(true, watchList, module, fields);
            }
        }
    }
        return false;
    }
}
