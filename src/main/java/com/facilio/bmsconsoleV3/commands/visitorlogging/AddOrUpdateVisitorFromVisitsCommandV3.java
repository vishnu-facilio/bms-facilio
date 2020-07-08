package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateVisitorFromVisitsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> visitorLogs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLogs)) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            for(V3VisitorLoggingContext vL : visitorLogs) {
                CommonCommandUtil.handleLookupFormData(modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING), vL.getData());
                if(StringUtils.isNotEmpty(vL.getVisitorPhone())) {
                    V3VisitorContext visitor = V3VisitorManagementAPI.getVisitor(-1L, vL.getVisitorPhone());
                    if(visitor == null) {
                        visitor = new V3VisitorContext();
                        visitor.setEmail(vL.getVisitorEmail());
                        visitor.setPhone(vL.getVisitorPhone());
                        visitor.setName(vL.getVisitorName());
                        visitor.setIsReturningVisitor(false);
                        V3RecordAPI.addRecord(true, Collections.singletonList(visitor) , module, fields);
                    }
                    else {
                        visitor.setEmail(vL.getVisitorEmail());
                        visitor.setPhone(vL.getVisitorPhone());
                        visitor.setName(vL.getVisitorName());
                        visitor.setIsReturningVisitor(true);
                        V3RecordAPI.updateRecord(visitor, module, fields);
                    }
                    vL.setVisitor(visitor);
                }
            }
        }
        return false;
    }
}
