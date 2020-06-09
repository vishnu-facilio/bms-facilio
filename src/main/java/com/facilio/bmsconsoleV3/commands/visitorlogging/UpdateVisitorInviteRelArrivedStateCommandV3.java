package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateVisitorInviteRelArrivedStateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> visitorLoggingRecords = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLoggingRecords)) {
            Map<String, Map<Long, List<UpdateChangeSet>>> moduleChangeSetMap = new HashMap<String, Map<Long,List<UpdateChangeSet>>>();
            Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<Long, List<UpdateChangeSet>>();

            for(V3VisitorLoggingContext vl : visitorLoggingRecords) {
                List<UpdateChangeSet> changes = new ArrayList<UpdateChangeSet>();
                UpdateChangeSet updateChangeState = new UpdateChangeSet();

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
                long fieldId = modBean.getField("moduleState", module.getName()).getFieldId();

                V3VisitorLoggingContext visitorLogging = V3VisitorManagementAPI.getVisitorLoggingTriggers(vl.getId(), null, false);

                if(visitorLogging != null) {
                    updateChangeState.setFieldId(fieldId);
                    updateChangeState.setNewValue(visitorLogging.getModuleState().getId());
                    changes.add(updateChangeState);
                    changeSet.put(visitorLogging.getId(), changes);

                }
            }
            moduleChangeSetMap.put(FacilioConstants.ContextNames.VISITOR_LOGGING, changeSet);
            context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, moduleChangeSetMap);
        }
        return false;
    }
}
